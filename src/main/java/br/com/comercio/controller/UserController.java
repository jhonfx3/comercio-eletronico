package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		try {
			usuarioRepository.findById("joao").get();
		} catch (Exception e) {
			Usuario usuario = new Usuario();
			usuario.setUsername("joao");
			usuario.setPassword("102938AS");
			criarUsuario(usuario);
			System.out.println("joao salvo com sucesso");
		}
		return "home";
	}

	@PostMapping("/usuario/novo")
	public String novo(Usuario usuario, RedirectAttributes attributes) {
		criarUsuario(usuario);
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " cadastrado com sucesso");
		return "redirect:/usuario/formulario";
	}

	@GetMapping("/usuario/formulario")
	public String formulario(Usuario usuario) {
		return "/usuario/formulario";
	}

	private void criarUsuario(Usuario usuario) {
		usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
		List<Authorities> authorities = authoritiesRepository.findAll();
		usuario.setAuthorities(authorities);
		usuarioRepository.save(usuario);
	}
}
