package br.com.comercio.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.comercio.model.Authorities;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.AuthoritiesRepository;
import br.com.comercio.repository.UsuarioRepository;

@Controller
@RequestMapping("usuario")
public class UsuarioController {
	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				System.out.println("oi");
				try {
					setValue(LocalDate.parse(text, DateTimeFormatter.ISO_DATE));
				} catch (Exception e) {
					// e.printStackTrace();
					setValue(null);
				}
			}
		});

	}

	@GetMapping("/urlmagica")
	public String urlMagica(Model model) {
		System.out.println("chamando url magica");
		try {
			usuarioRepository.findById("joao").get();
			authoritiesRepository.findById("ROLE_ADM").get();
			authoritiesRepository.findById("ROLE_USER").get();
		} catch (Exception e) {
			Authorities adm = new Authorities();
			Authorities user = new Authorities();
			adm.setAuthority("ROLE_ADM");
			user.setAuthority("ROLE_USER");
			authoritiesRepository.save(adm);
			authoritiesRepository.save(user);
			Usuario usuario = new Usuario();
			usuario.setUsername("joao");
			usuario.setPassword("102938AS");
			criarUsuario(usuario, Arrays.asList(adm, user));
			System.out.println("joao + role salvo com sucesso");
		}
		return "home";
	}

	@PostMapping("/novo")
	public String novo(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			System.out.println("deu erro no usuario");
			return "usuario/formulario";
		}
		Authorities userAuthority = authoritiesRepository.findById("ROLE_USER").get();
		criarUsuario(usuario, Arrays.asList(userAuthority));
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " cadastrado com sucesso");
		return "redirect:/usuario/formulario";
	}

	@PostMapping("/editar")
	public String editarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				System.out.println(fieldError.getDefaultMessage());
			}
			System.out.println("deu erro no usuario");
			return "usuario/formularioEditar";
		}
		System.out.println("usuario id?" + usuario.getUsername());
		usuarioRepository.save(usuario);
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " alterado com sucesso");
		return "redirect:/usuario/formulario/editar";
	}

	@GetMapping("/formulario")
	public String formulario(Usuario usuario) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Verifica se o authentication é uma instancia de anonymous...
		// Caso seja não existe ninguem logado então posso acessar a URL
		if (authentication instanceof AnonymousAuthenticationToken) {
			return "usuario/formulario";
		} else {
			return "redirect:/";
		}

	}

	@GetMapping("/formulario/editar")
	public String formularioEditar(Usuario usuario, Model model) {
		Usuario usuarioLogado = getUsuarioLogado();
		System.out.println("id  " + usuarioLogado.getUsername());
		model.addAttribute("usuario", getUsuarioLogado());
		return "usuario/formularioEditar";
	}

	private Usuario getUsuarioLogado() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return usuarioRepository.findById(username).get();
	}

	private void criarUsuario(Usuario usuario, List<Authorities> authorities) {
		usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
		usuario.setAuthorities(authorities);
		usuarioRepository.save(usuario);
	}
}
