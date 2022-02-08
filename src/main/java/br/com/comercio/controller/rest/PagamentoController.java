package br.com.comercio.controller.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.datastructures.payment.AdditionalInfo;
import com.mercadopago.resources.datastructures.payment.Address;
import com.mercadopago.resources.datastructures.payment.Identification;
import com.mercadopago.resources.datastructures.payment.Item;
import com.mercadopago.resources.datastructures.payment.Payer;

import br.com.comercio.dto.PaymentResponseDTO;
import br.com.comercio.enums.StatusPedido;
import br.com.comercio.enums.TipoPreco;
import br.com.comercio.model.CardPaymentDTO;
import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Produto;
import br.com.comercio.model.ProdutoPedido;
import br.com.comercio.model.Usuario;
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
	private CarrinhoDeCompras carrinho;

	@PostMapping("/gerar")
	@ResponseBody
	public ResponseEntity<PaymentResponseDTO> formularioPagamento(@RequestBody CardPaymentDTO cardPaymentDTO,
			HttpServletRequest request) throws MPException {
		ArrayList<Item> itens = new ArrayList<Item>();
		List<Produto> produtos = new ArrayList<>();
		Map<CarrinhoItem, Integer> itensMap = carrinho.getItensMap();
		List<ProdutoPedido> listaProdutosPedido = new ArrayList<ProdutoPedido>();

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepository.findById(name).get();
		for (Map.Entry<CarrinhoItem, Integer> entry : itensMap.entrySet()) {
			System.out.println(entry.getKey().getProduto().getNome());
			CarrinhoItem carrinhoItem = entry.getKey();
			produtos.add(carrinhoItem.getProduto());
			ProdutoPedido produtoPedido = new ProdutoPedido();
			produtoPedido.setProduto(carrinhoItem.getProduto());
			produtoPedido.setQuantidade(entry.getValue());

			if (cardPaymentDTO.getInstallments() != null && cardPaymentDTO.getInstallments() > 1) {
				produtoPedido.setTotal(new BigDecimal(calculaValorAPrazo(
						carrinhoItem.getTotalCarrinhoItem(entry.getValue(), TipoPreco.VISTA).floatValue(),
						cardPaymentDTO.getInstallments())));
			} else {
				produtoPedido.setTotal(carrinhoItem.getTotalCarrinhoItem(entry.getValue(), TipoPreco.VISTA));
			}

			listaProdutosPedido.add(produtoPedido);
			Item item = new Item();
			item.setId(String.valueOf(carrinhoItem.getProduto().getId()));
			item.setPictureUrl(carrinhoItem.getProduto().getUrlImagem());
			item.setTitle(carrinhoItem.getProduto().getNome());
			item.setUnitPrice(carrinhoItem.getProduto().getPrecos().get(0).getValor().floatValue());
			item.setQuantity(entry.getValue());
			itens.add(item);
		}
		// Tabela de cálculo de porcentagem a prazo do mercado livre
		// https://www.mercadopago.com.br/ajuda/Custos-de-parcelamento_322

		Payment payment = new Payment();

		if (cardPaymentDTO.getInstallments() != null && cardPaymentDTO.getInstallments() > 1) {
			payment.setTransactionAmount(
					calculaValorAPrazo(cardPaymentDTO.getTransactionAmount(), cardPaymentDTO.getInstallments()));
		} else {
			payment.setTransactionAmount(cardPaymentDTO.getTransactionAmount());
		}

		if (cardPaymentDTO.getToken() != null) {
			payment.setToken(cardPaymentDTO.getToken());
		}
		payment.setDescription(cardPaymentDTO.getProductDescription());
		if (cardPaymentDTO.getInstallments() != null) {
			payment.setInstallments(cardPaymentDTO.getInstallments());
		}
		payment.setPaymentMethodId(cardPaymentDTO.getPaymentMethodId());
		Address endereco = new Address();

		endereco.setZipCode("06233200");
		endereco.setStreetName("Av. das Nações Unidas");
		endereco.setStreetNumber(3003);
		endereco.setNeighborhood("Bonfim");
		endereco.setCity("Osasco");
		endereco.setFederalUnit("SP");
		Identification identification = new Identification();
		String cpfLimpo = usuario.getCpf().replace(".", "").replace("-", "");
		identification.setType("CPF").setNumber(cpfLimpo);
		Payer payer = new Payer();
		payer.setEmail(usuario.getEmail());
		payer.setIdentification(identification);
		payer.setAddress(endereco);
		payer.setFirstName(usuario.getNome());
		payer.setLastName(usuario.getSobrenome());
		payment.setPayer(payer);

		AdditionalInfo infoAdicionais = new AdditionalInfo();
		infoAdicionais.setItems(itens);
		payment.setAdditionalInfo(infoAdicionais);
		Payment pagamentoGerado = payment.save();
		PaymentResponseDTO pagamentoRespostaDTO = new PaymentResponseDTO(pagamentoGerado.getId(),
				String.valueOf(pagamentoGerado.getStatus()), pagamentoGerado.getStatusDetail());
		System.out.println("id ->" + pagamentoGerado.getId() + " status ->" + pagamentoGerado.getStatus()
				+ "status detail -> " + pagamentoGerado.getStatusDetail());
		Pedido pedido = new Pedido();
		pedido.setId(pagamentoGerado.getId());
		StatusPedido status = Enum.valueOf(StatusPedido.class, pagamentoGerado.getStatus().toString());
		pedido.setStatus(status);
		System.out.println(pagamentoGerado.getTransactionAmount());
		pedido.setTotal(new BigDecimal(pagamentoGerado.getTransactionAmount()));
		pedido.setUsuario(usuario);
		pedido.setParcelas(pagamentoGerado.getInstallments());
		pedido.setValorParcela(
				new BigDecimal(pagamentoGerado.getTransactionAmount() / pagamentoGerado.getInstallments()));
		pedido.setData(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(pagamentoGerado.getDateCreated())));
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

		// System.out.println(createdPayment.getExternalReference());
		// MPApiResponse payment_methods = MercadoPago.SDK.Get("/v1/payment_methods");
		return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoRespostaDTO);
	}

	private float calculaValorAPrazo(float valorDaCompra, Integer quantidadeParcelas) {
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
		return new BigDecimal(valorDaCompra).setScale(2, RoundingMode.HALF_UP).floatValue();
	}
	/*
	 * Função de teste que usei para processar um pagamento com pix separadamente
	 * 
	 * @PostMapping("/gerar/pix")
	 * 
	 * @ResponseBody public ResponseEntity<PixDTO> processaPix(@RequestBody PixDTO
	 * pixDTO) throws MPException { Payment pagamento = new Payment(); String name =
	 * SecurityContextHolder.getContext().getAuthentication().getName(); Usuario
	 * usuario = usuarioRepository.findById(name).get();
	 * pagamento.setTransactionAmount(carrinho.getTotalCarrinho(TipoPreco.VISTA).
	 * floatValue()) .setPaymentMethodId("pix"); Payer pagador = new
	 * Payer().setEmail(usuario.getEmail()).setFirstName(usuario.getNome())
	 * .setLastName(usuario.getSobrenome()); Identification identification = new
	 * Identification(); identification.setNumber(pixDTO.getNumeroIdentificacao());
	 * identification.setType(pixDTO.getTipoIdentificacao());
	 * pagador.setIdentification(identification);
	 * 
	 * Address endereco = new Address();
	 * 
	 * endereco.setZipCode("06233200");
	 * endereco.setStreetName("Av. das Nações Unidas");
	 * endereco.setStreetNumber(3003); endereco.setNeighborhood("Bonfim");
	 * endereco.setCity("Osasco"); endereco.setFederalUnit("SP");
	 * 
	 * pagador.setAddress(endereco); pagamento.setPayer(pagador); Payment
	 * pagamentoGerado = pagamento.save();
	 * System.out.println(pagamentoGerado.getId()); return
	 * ResponseEntity.status(HttpStatus.CREATED).body(pixDTO); }
	 */

	/*
	 * Cartões de teste do mercado pago:
	 * 
	 * Mastercard 5031 4332 1540 6351 123 11/25 Visa 4235 6477 2802 5682 123 11/25
	 * 
	 * American Express 3753 651535 56885 1234 11/25
	 */

}
