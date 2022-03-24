package br.com.comercio.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.comercio.enums.StatusEmail;
import br.com.comercio.model.Cliente;
import br.com.comercio.model.Email;
import br.com.comercio.repository.EmailRepository;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private EmailRepository emailRepository;

	public void enviarEmail(Cliente cliente, String codigo, HttpServletRequest request, String content, String link)
			throws MessagingException, UnsupportedEncodingException {
		Email email = new Email();
		try {
			System.out.println("Tentando enviar email...");
			email.setAssunto("Confirme seu cadastro");
			email.setOrigem("jcaferreira9@gmail.com");
			email.setDestinatario(cliente.getUsuario().getEmail());

			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(email.getOrigem(), "E-commerce");
			helper.setTo(email.getDestinatario());
			helper.setSubject(email.getAssunto());
			String urlBase = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build()
					.toUriString();

			content = content.replace("[[nome]]", "Novo usuário");
			String urlDeConfirmacao = urlBase + link + codigo;
			content = content.replace("[[URL]]", urlDeConfirmacao);
			helper.setText(content, true);
			emailSender.send(message);
			email.setStatus(StatusEmail.ENVIADO);
			email.setMensagem(content);
			emailRepository.save(email);
		} catch (MailException e) {
			e.printStackTrace();
			email.setStatus(StatusEmail.FALHA);
			emailRepository.save(email);
		}
		System.out.println("Status email:" + email.getStatus());
	}

	public void enviarEmail(Email email) {
		System.out.println("Tentando enviar email...");
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(email.getOrigem());
			message.setTo(email.getDestinatario());
			message.setSubject(email.getAssunto());
			message.setText(email.getMensagem());
			emailSender.send(message);
			email.setStatus(StatusEmail.ENVIADO);
			emailRepository.save(email);
		} catch (MailException e) {
			email.setStatus(StatusEmail.FALHA);
			emailRepository.save(email);
		}

	}
}
