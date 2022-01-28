package br.com.comercio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Endereco;
import br.com.comercio.repository.EnderecoRepository;

@Controller
@RequestMapping(value = "endereco")
public class EnderecoController {

	@Autowired
	private EnderecoRepository enderecoRepository;

	@PostMapping("/novo")
	public String novo(Endereco endereco) {
		System.out.println("salvando endereco...");
		System.out.println(endereco.getRua());
		return "redirect:/usuario/formulario/editar";
	}
}
