package br.com.comercio.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.ClienteRepository;

@Controller
@RequestMapping(value = "/cliente")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				try {
					setValue(LocalDate.parse(text, DateTimeFormatter.ISO_DATE));
				} catch (Exception e) {
					// e.printStackTrace();
					setValue(null);
				}
			}
		});

	}

	@PostMapping("/novo")
	public String novo(@Validated(PersistirUsuario.class) ClienteFisico cliente, BindingResult result,
			@Validated(PersistirUsuario.class) Usuario usuario, BindingResult result2, Model model,
			String confirmarPassword) {
		usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
		if (result2.hasErrors()) {
			if (result2.hasFieldErrors("email")) {
				model.addAttribute("erroEmail", result2.getFieldError("email").getDefaultMessage());
			}
			if (result2.hasFieldErrors("password")) {
				model.addAttribute("erroPassword", result2.getFieldError("password").getDefaultMessage());
			}
			return "cliente/formulario";
		}

		if (result.hasErrors()) {
			return "cliente/formulario";
		}
		cliente.setUsuario(usuario);
		clienteRepository.save(cliente);
		return "home";
	}

	@GetMapping("/formulario")
	public String formulario(ClienteFisico cliente) {
		return "cliente/formulario";
	}

}
