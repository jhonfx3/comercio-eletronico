package br.com.comercio.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "notificacao")
public class NotificacaoController {

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> gerenciarNotificacoes(@RequestParam("topic") String topic, @RequestParam("id") String id,
			HttpServletResponse response) {
		System.out.println("chamando notificacoes...!@");
		System.out.println("id -> " + id + " topic -> " + topic);
		if (response != null) {
			System.out.println("response != null");
		}
		response.setStatus(200);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	public ResponseEntity<?> gerenciarNotificacoesWebhook(@RequestBody Map<String, Object> payload) {
		System.out.println("chamando notificacoes webhook...!@");
		System.out.println(payload);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
