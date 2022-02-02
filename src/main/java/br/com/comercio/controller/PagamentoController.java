package br.com.comercio.controller;

import java.math.BigDecimal;
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

import br.com.comercio.enums.StatusPedido;
import br.com.comercio.model.CardPaymentDTO;
import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.PaymentResponseDTO;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Produto;
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
		/*
		 * System.out.println("chamando...");
		 * System.out.println(cardPaymentDTO.getToken());
		 * System.out.println(cardPaymentDTO.getPaymentMethodId());
		 * System.out.println(cardPaymentDTO.getTransactionAmount());
		 * System.out.println(cardPaymentDTO.getPayer().getEmail());
		 * System.out.println(cardPaymentDTO.getInstallments());
		 */
		ArrayList<Item> itens = new ArrayList<Item>();
		List<Produto> produtos = new ArrayList<>();
		Map<CarrinhoItem, Integer> itensMap = carrinho.getItensMap();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepository.findById(name).get();
		for (Map.Entry<CarrinhoItem, Integer> entry : itensMap.entrySet()) {
			System.out.println(entry.getKey().getProduto().getNome());
			CarrinhoItem carrinhoItem = entry.getKey();
			produtos.add(carrinhoItem.getProduto());
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
		payment.setTransactionAmount(cardPaymentDTO.getTransactionAmount()).setToken(cardPaymentDTO.getToken())
				.setDescription(cardPaymentDTO.getProductDescription())
				.setInstallments(cardPaymentDTO.getInstallments())
				.setPaymentMethodId(cardPaymentDTO.getPaymentMethodId());
		Address endereco = new Address();

		endereco.setZipCode("06233200");
		endereco.setStreetName("Av. das Nações Unidas");
		endereco.setStreetNumber(3003);
		endereco.setNeighborhood("Bonfim");
		endereco.setCity("Osasco");
		endereco.setFederalUnit("SP");
		Identification identification = new Identification();
		identification.setType(cardPaymentDTO.getPayer().getIdentification().getType())
				.setNumber(cardPaymentDTO.getPayer().getIdentification().getNumber());

		Payer payer = new Payer();
		payer.setEmail(cardPaymentDTO.getPayer().getEmail());
		payer.setIdentification(identification);
		payer.setAddress(endereco);
		payer.setFirstName("joaquim");
		payer.setLastName("silva");
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
		pedido.setProdutos(produtos);
		pedidoRepository.save(pedido);
		// System.out.println(createdPayment.getExternalReference());
		// MPApiResponse payment_methods = MercadoPago.SDK.Get("/v1/payment_methods");
		return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoRespostaDTO);
	}

	/*
	 * Cartões de teste do mercado pago:
	 * 
	 * Mastercard 5031 4332 1540 6351 123 11/25 Visa 4235 6477 2802 5682 123 11/25
	 * 
	 * American Express 3753 651535 56885 1234 11/25
	 */

}
