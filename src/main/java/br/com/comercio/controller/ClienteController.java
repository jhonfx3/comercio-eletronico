package br.com.comercio.controller;

import java.beans.PropertyEditorSupport;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.model.Cliente;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.model.ClienteJuridico;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.ClienteRepository;
import br.com.comercio.repository.UsuarioRepository;
import br.com.comercio.service.CriptografiaService;
import br.com.comercio.service.EmailService;
import net.bytebuddy.utility.RandomString;

@Controller
@RequestMapping(value = "/cliente")
public class ClienteController {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UsuarioRepository usuarioRepository;

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
			String confirmarPassword, HttpServletRequest request) throws Exception {
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
		String codigo = RandomString.make(20);
		usuario.setCodigoVerificacao(codigo);
		cliente.setUsuario(usuario);
		cliente.setCpf(new CriptografiaService().encriptar(cliente.getCpf()));
		clienteRepository.save(cliente);
		enviaEmailDeConfirmacao(cliente, request, codigo);
		return "home";
	}

	private void enviaEmailDeConfirmacao(Cliente cliente, HttpServletRequest request, String codigo)
			throws MessagingException, UnsupportedEncodingException {
		String content = "Bem-vindo " + cliente.getNome() + ",<br>"
				+ "Clique no link abaixo para confirmar seu cadastro no nosso e-commerce<br>"
				+ "<h3><a href=\"[[URL]]\">Confirme seu cadastro</a></h3>";
		String link = "/cliente/confirmar/";
		emailService.enviarEmail(cliente, codigo, request, content, link);
	}

	@PostMapping("/novoJuridico")
	public String novoJuridico(@Validated(PersistirUsuario.class) ClienteJuridico cliente, BindingResult result,
			@Validated(PersistirUsuario.class) Usuario usuario, BindingResult result2, Model model,
			String confirmarPassword, HttpServletRequest request) throws Exception {
		usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
		if (result2.hasErrors()) {
			if (result2.hasFieldErrors("email")) {
				model.addAttribute("erroEmail", result2.getFieldError("email").getDefaultMessage());
			}
			if (result2.hasFieldErrors("password")) {
				model.addAttribute("erroPassword", result2.getFieldError("password").getDefaultMessage());
			}
			return "cliente/formularioJuridico";
		}

		if (result.hasErrors()) {
			return "cliente/formularioJuridico";
		}
		String codigo = RandomString.make(20);
		usuario.setCodigoVerificacao(codigo);
		cliente.setUsuario(usuario);
		cliente.setCnpj(new CriptografiaService().encriptar(cliente.getCnpj()));
		clienteRepository.save(cliente);
		enviaEmailDeConfirmacao(cliente, request, codigo);
		return "home";
	}

	@GetMapping("/confirmar/{codigo}")
	public String confirmarCadastro(@PathVariable("codigo") String codigo, Model model) {
		try {
			Usuario usuarioCadastrado = usuarioRepository.findByCodigoVerificacao(codigo);
			usuarioCadastrado.setCodigoVerificacao(null);
			usuarioCadastrado.setEnabled(true);
			usuarioRepository.save(usuarioCadastrado);
		} catch (Exception e) {
			return "redirect:/";
		}
		model.addAttribute("mensagem", "Parabéns, você confirmou o cadastro de sua conta com sucesso");
		return "usuario/infoSobreConta";
	}

	@GetMapping("/formulario/cadastrar")
	public String formulario(ClienteFisico cliente) {
		return "cliente/formulario";
	}

	@GetMapping("/formulario/juridico/cadastrar")
	public String formulario(ClienteJuridico cliente) {
		return "cliente/formularioJuridico";
	}

}
