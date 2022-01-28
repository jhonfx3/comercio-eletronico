package br.com.comercio.controller;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
@RequestMapping(value = "cep")
public class CepController {
	@PostMapping("/buscar")
	public ResponseEntity<String> retornaCEP(@RequestBody String cep) {
		JsonObject obj = new Gson().fromJson(cep, JsonObject.class);
		cep = obj.get("cep").getAsString();
		String conteudo = "";
		try {
			Client client = ClientBuilder.newClient();
			conteudo = client.target("https://viacep.com.br/ws/" + cep + "/json/").request().get(String.class);
		} catch (Exception e) {
		}
		if (conteudo.contains("erro"))
			conteudo = "";
		return ResponseEntity.ok(new Gson().toJson(conteudo));
	}

}
