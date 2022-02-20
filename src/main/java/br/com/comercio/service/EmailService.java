package br.com.comercio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import br.com.comercio.enums.StatusEmail;
import br.com.comercio.model.Email;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender emailSender;

	public void enviarEmail(Email email) {
		try {
			System.out.println("Tentando enviar email...");
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(email.getOrigem());
			message.setTo(email.getDestinatario());
			message.setSubject(email.getAssunto());
			message.setText(email.getMensagem());
			emailSender.send(message);
			email.setStatus(StatusEmail.ENVIADO);
		} catch (MailException e) {
			e.printStackTrace();
			email.setStatus(StatusEmail.FALHA);
		}
		System.out.println("Status email:" + email.getStatus());
	}
}
