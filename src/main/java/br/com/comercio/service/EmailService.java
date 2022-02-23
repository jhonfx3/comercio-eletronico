package br.com.comercio.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.comercio.enums.StatusEmail;
import br.com.comercio.model.Email;
import br.com.comercio.model.Usuario;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender emailSender;

	public void enviarEmail(Usuario usuario, String codigo) throws MessagingException, UnsupportedEncodingException {
		Email email = new Email();
		try {
			System.out.println("Tentando enviar email...");
			email.setAssunto("Confirme seu cadastro");
			email.setOrigem("jcaferreira9@gmail.com");
			email.setDestinatario(usuario.getEmail());
			String content = "Bem-vindo " + usuario.getNome() + ",<br>"
					+ "Clique no link abaixo para confirmar seu cadastro no nosso e-commerce<br>"
					+ "<h3><a href=\"[[URL]]\" target=\"_self\">Confirme seu cadastro</a></h3>";

			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			
			helper.setFrom(email.getOrigem(), "E-commerce");
			helper.setTo(email.getDestinatario());
			helper.setSubject(email.getAssunto());
			
			
			content = content.replace("[[nome]]", "Novo usuário");
			String urlDeConfirmacao = "http://localhost:8080/usuario/confirmar/" + codigo;
			content = content.replace("[[URL]]", urlDeConfirmacao);

			helper.setText(content, true);
			emailSender.send(message);
			email.setStatus(StatusEmail.ENVIADO);
		} catch (MailException e) {
			e.printStackTrace();
			email.setStatus(StatusEmail.FALHA);
		}
		System.out.println("Status email:" + email.getStatus());
	}
}
