package br.com.comercio.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class RecaptchaService {

	public boolean verificaRecaptcha(String recaptcha) {
		Client client = ClientBuilder.newClient();
		String secret = "6LeXC9AfAAAAADlyiiUhoroXo4oa5AxVIG0PDozJ";
		String conteudo = client
				.target("https://www.google.com/recaptcha/api/siteverify?secret=" + secret + "&response=" + recaptcha)
				.request().get(String.class);
		JsonObject obj = new Gson().fromJson(conteudo, JsonObject.class);
		boolean sucesso = obj.get("success").getAsBoolean();
		return sucesso;
	}
}
