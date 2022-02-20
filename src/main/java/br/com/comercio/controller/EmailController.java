package br.com.comercio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Email;
import br.com.comercio.service.EmailService;

@Controller
@RequestMapping(value = "email")
public class EmailController {

	@Autowired
	private EmailService emailService;

	@RequestMapping("/enviar")
	public String enviarEmail() {
		Email email = new Email();
		email.setOrigem("jcaferreira9@gmail.com");
		email.setDestinatario("jcaferreira9x@hotmail.com");
		email.setAssunto("Conta criada com sucesso");
		email.setMensagem(
				"Parabéns, sua conta foi criada com sucesso no nosso e-commerce, agora você pode realizar suas compras");
		emailService.enviarEmail(email);
		return "home";
	}

}
