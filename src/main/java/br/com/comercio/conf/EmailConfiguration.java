package br.com.comercio.conf;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

	@Bean
	public JavaMailSender mailSenderConfig() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setUsername("jcaferreira9@gmail.com");
		mailSender.setPassword(System.getenv("passwdMail"));
		mailSender.setPort(587);
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", true);
		mailProperties.put("mail.smtp.starttls.enable", true);
		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}
}
