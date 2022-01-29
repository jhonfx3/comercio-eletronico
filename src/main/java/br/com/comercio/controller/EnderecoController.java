package br.com.comercio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Endereco;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.EnderecoRepository;
import br.com.comercio.repository.UsuarioRepository;

@Controller
@RequestMapping(value = "endereco")
public class EnderecoController {

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping("/novo")
	public String novo(Endereco endereco) {
		System.out.println("salvando endereco...");
		System.out.println(endereco.getRua());
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepository.findById(username).get();
		endereco.setUsuario(usuario);
		enderecoRepository.save(endereco);
		return "redirect:/usuario/formulario/editar";
	}
}
