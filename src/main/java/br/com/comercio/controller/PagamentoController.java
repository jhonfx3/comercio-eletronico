package br.com.comercio.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.datastructures.payment.Address;
import com.mercadopago.resources.datastructures.payment.Identification;
import com.mercadopago.resources.datastructures.payment.Payer;

import br.com.comercio.model.CardPaymentDTO;
import br.com.comercio.model.PaymentResponseDTO;

@RestController
@RequestMapping(value = "pagamento")
public class PagamentoController {
	@PostMapping("/formulario")
	@ResponseBody
	public ResponseEntity<PaymentResponseDTO> formularioPagamento(@RequestBody CardPaymentDTO cardPaymentDTO,
			HttpServletRequest request) throws MPException {
		System.out.println("chamando...");
		System.out.println(cardPaymentDTO.getToken());
		System.out.println(cardPaymentDTO.getPaymentMethodId());
		System.out.println(cardPaymentDTO.getTransactionAmount());
		System.out.println(cardPaymentDTO.getPayer().getEmail());
		System.out.println(cardPaymentDTO.getInstallments());
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
		System.out.println("e-mail -> " + payer.getEmail());
		Payment createdPayment = payment.save();
		PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(createdPayment.getId(),
				String.valueOf(createdPayment.getStatus()), createdPayment.getStatusDetail());
		System.out.println("id ->" + createdPayment.getId() + " status ->" + createdPayment.getStatus()
				+ "status detail -> " + createdPayment.getStatusDetail());
		// System.out.println(createdPayment.getExternalReference());
		// MPApiResponse payment_methods = MercadoPago.SDK.Get("/v1/payment_methods");
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDTO);
	}

	/*
	 * Cartões de teste do mercado pago:
	 * 
	 * Mastercard 5031 4332 1540 6351 123 11/25 Visa 4235 6477 2802 5682 123 11/25
	 * 
	 * American Express 3753 651535 56885 1234 11/25
	 */

}
