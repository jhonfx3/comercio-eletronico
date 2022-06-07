package br.com.comercio.controller.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentAdditionalInfoRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentItemRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

import br.com.comercio.dto.PaymentResponseDTO;
import br.com.comercio.enums.StatusPedido;
import br.com.comercio.model.CardPaymentDTO;
import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Produto;
import br.com.comercio.model.ProdutoPedido;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.ClienteFisicoRepository;
import br.com.comercio.repository.PedidoRepository;
import br.com.comercio.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "pagamento")
public class PagamentoController {

	@Autowired
	private PedidoRepository pedidoRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private ClienteFisicoRepository clienteFisicoRepository;

	@Autowired
	private CarrinhoDeCompras carrinho;

	@PostMapping("/gerar")
	@ResponseBody
	public ResponseEntity<PaymentResponseDTO> formularioPagamento(@RequestBody CardPaymentDTO cardPaymentDTO,
			HttpServletRequest request) throws MPException {
		ArrayList<PaymentItemRequest> itens = new ArrayList<PaymentItemRequest>();
		List<Produto> produtos = new ArrayList<>();
		Map<CarrinhoItem, Integer> itensMap = carrinho.getItensMap();
		List<ProdutoPedido> listaProdutosPedido = new ArrayList<ProdutoPedido>();

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepository.findById(name).get();
		ClienteFisico cliente = clienteFisicoRepository.findClienteByEmail(usuario.getEmail());
		for (Map.Entry<CarrinhoItem, Integer> entry : itensMap.entrySet()) {
			CarrinhoItem carrinhoItem = entry.getKey();
			produtos.add(carrinhoItem.getProduto());
			ProdutoPedido produtoPedido = new ProdutoPedido();
			produtoPedido.setProduto(carrinhoItem.getProduto());
			produtoPedido.setQuantidade(entry.getValue());

			if (cardPaymentDTO.getInstallments() != null && cardPaymentDTO.getInstallments() > 1) {
				produtoPedido
						.setTotal(calculaValorAPrazo(carrinhoItem.getTotalCarrinhoItem(entry.getValue()).floatValue(),
								cardPaymentDTO.getInstallments()));
			} else {
				produtoPedido.setTotal(carrinhoItem.getTotalCarrinhoItem(entry.getValue()));
			}

			listaProdutosPedido.add(produtoPedido);
			PaymentItemRequest item = PaymentItemRequest.builder().title(carrinhoItem.getProduto().getNome())
					.id(String.valueOf(carrinhoItem.getProduto().getId()))
					.pictureUrl(carrinhoItem.getProduto().getUrlImagem())
					.unitPrice(carrinhoItem.getProduto().getPreco()).quantity(entry.getValue()).build();
			itens.add(item);
		}
		// Tabela de cálculo de porcentagem a prazo do mercado livre
		// https://www.mercadopago.com.br/ajuda/Custos-de-parcelamento_322
		PaymentAdditionalInfoRequest additionalInfo = PaymentAdditionalInfoRequest.builder().items(itens).build();
		BigDecimal transactionAmount;
		String token = "";

		if (cardPaymentDTO.getToken() != null) {
			token = cardPaymentDTO.getToken();
		}

		if (cardPaymentDTO.getInstallments() != null && cardPaymentDTO.getInstallments() > 1) {
			transactionAmount = calculaValorAPrazo(cardPaymentDTO.getTransactionAmount(),
					cardPaymentDTO.getInstallments());
		} else {
			transactionAmount = new BigDecimal(cardPaymentDTO.getTransactionAmount());
		}

		String cpfLimpo = cliente.getCpf().replace(".", "").replace("-", "");

		IdentificationRequest identification = IdentificationRequest.builder().type("CPF").number(cpfLimpo).build();

		PaymentPayerRequest payer = PaymentPayerRequest.builder().firstName(cliente.getNome())
				.lastName(cliente.getSobrenome()).email(usuario.getEmail()).identification(identification).build();

		PaymentClient client = new PaymentClient();

		PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder().transactionAmount(transactionAmount)
				.additionalInfo(additionalInfo).token(cardPaymentDTO.getToken())
				.description(cardPaymentDTO.getProductDescription()).installments(cardPaymentDTO.getInstallments())
				.paymentMethodId(cardPaymentDTO.getPaymentMethodId()).payer(payer).build();

		Payment pagamentoGerado = new Payment();
		try {
			pagamentoGerado = client.create(paymentCreateRequest);
		} catch (MPException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (MPApiException e) {
			System.out.println(e.getApiResponse().getContent());
			e.printStackTrace();
		}

		PaymentResponseDTO pagamentoRespostaDTO = new PaymentResponseDTO(String.valueOf(pagamentoGerado.getId()),
				String.valueOf(pagamentoGerado.getStatus()), pagamentoGerado.getStatusDetail());
		Pedido pedido = new Pedido();
		pedido.setId(String.valueOf(pagamentoGerado.getId()));
		StatusPedido status = Enum.valueOf(StatusPedido.class, pagamentoGerado.getStatus().toString());
		pedido.setStatus(status);
		System.out.println(pagamentoGerado.getTransactionAmount());
		pedido.setTotal(pagamentoGerado.getTransactionAmount());
		pedido.setUsuario(usuario);
		pedido.setParcelas(pagamentoGerado.getInstallments());
		pedido.setValorParcela(
				pagamentoGerado.getTransactionAmount().divide(new BigDecimal(pagamentoGerado.getInstallments())));
		pedido.setData(pagamentoGerado.getDateCreated().toLocalDate());
		pedido.setMetodoPagamento(pagamentoGerado.getPaymentMethodId());
		// Se o pagamento gerado foi com pix
		if (pagamentoGerado.getPaymentMethodId().equals("pix")) {
			String qrCodeBase64 = pagamentoGerado.getPointOfInteraction().getTransactionData().getQrCodeBase64();
			// Seto o link do QR code no atributo
			pedido.setQrCodeBase64(qrCodeBase64);
		}
		// Se o pagamento gerado foi com boleto
		if (pagamentoGerado.getPaymentMethodId().equals("bolbradesco")) {
			// Seto o link do boleto no atributo
			pedido.setLinkBoleto(pagamentoGerado.getTransactionDetails().getExternalResourceUrl());
		}
		Pedido pedidoSalvo = pedidoRepository.save(pedido);
		for (ProdutoPedido produtoPedido : listaProdutosPedido) {
			produtoPedido.setPedido(pedidoSalvo);
		}
		pedidoSalvo.setProdutos(listaProdutosPedido);
		pedidoRepository.save(pedidoSalvo);

		return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoRespostaDTO);
	}

	private BigDecimal calculaValorAPrazo(float valorDaCompra, Integer quantidadeParcelas) {
		float porcentagemDeAcrescimo = 0;

		switch (quantidadeParcelas) {
		case 2:
			porcentagemDeAcrescimo = (float) 6.76;
			break;
		case 3:
			porcentagemDeAcrescimo = (float) 8.44;
			break;
		case 4:
			porcentagemDeAcrescimo = (float) 10.23;
			break;
		case 5:
			porcentagemDeAcrescimo = (float) 11.93;
			break;
		case 6:
			porcentagemDeAcrescimo = (float) 13.58;
			break;
		case 7:
			porcentagemDeAcrescimo = (float) 15.01;
			break;
		case 8:
			porcentagemDeAcrescimo = (float) 16.90;
			break;
		case 9:
			porcentagemDeAcrescimo = (float) 18.86;
			break;
		case 10:
			porcentagemDeAcrescimo = (float) 20.07;
			break;
		case 11:
			porcentagemDeAcrescimo = (float) 21.92;
			break;
		case 12:
			porcentagemDeAcrescimo = (float) 23.75;
			break;
		}
		float valor;
		valor = (valorDaCompra * porcentagemDeAcrescimo) / 100;
		valorDaCompra += valor;
		BigDecimal setScale = new BigDecimal(valorDaCompra).setScale(2, RoundingMode.HALF_UP);
		System.out.println(setScale);
		return new BigDecimal(valorDaCompra).setScale(2, RoundingMode.HALF_UP);
	}

	/*
	 * Cartões de teste do mercado pago:
	 * 
	 * Mastercard 5031 4332 1540 6351 123 11/25 Visa 4235 6477 2802 5682 123 11/25
	 * 
	 * American Express 3753 651535 56885 1234 11/25
	 */

}
