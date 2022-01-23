package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.comercio.model.Authorities;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.AuthoritiesRepository;
import br.com.comercio.repository.UserRepository;

@Controller
public class UserController {
	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Autowired
	private UserRepository usuarioRepository;

	@GetMapping("/urlmagica")
	public String urlMagica(Model model) {
		System.out.println("chamando url magica");
		if (usuarioRepository.findById("joao").get() == null) {
			Usuario usuario = new Usuario();
			usuario.setUsername("joao");
			usuario.setPassword(new BCryptPasswordEncoder().encode("102938AS"));
			usuario.setEnabled(true);
			List<Authorities> authorities = authoritiesRepository.findAll();
			usuario.setAuthorities(authorities);
			usuarioRepository.save(usuario);
			System.out.println("joao salvo com sucesso");
		}
		return "home";
	}

	@PostMapping
	public String novo(Usuario usuario) {
		System.out.println("chamando");
		return "redirect:/";
	}

	@GetMapping("/usuario/formulario")
	public String formulario(Usuario usuario) {
		System.out.println("chamando " + usuario.getUsername() + " " + usuario.getPassword());
		return "/usuario/formulario";
	}
}
