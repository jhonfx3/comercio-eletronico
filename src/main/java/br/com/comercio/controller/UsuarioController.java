package br.com.comercio.controller;

import java.beans.PropertyEditorSupport;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.comercio.hibernategroups.EditarUsuario;
import br.com.comercio.model.Authorities;
import br.com.comercio.model.ClienteFisico;
import br.com.comercio.model.ClienteJuridico;
import br.com.comercio.model.Email;
import br.com.comercio.model.Endereco;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.AuthoritiesRepository;
import br.com.comercio.repository.ClienteFisicoRepository;
import br.com.comercio.repository.ClienteJuridicoRepository;
import br.com.comercio.repository.PedidoRepository;
import br.com.comercio.repository.UsuarioRepository;
import br.com.comercio.service.EmailService;
import net.bytebuddy.utility.RandomString;

@Controller
@RequestMapping("usuario")
public class UsuarioController {
	@Autowired
	private AuthoritiesRepository authoritiesRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ClienteFisicoRepository clienteFisicoRepository;
	@Autowired
	private ClienteJuridicoRepository clienteJuridicoRepository;

	// @Autowired
	// private UsuarioRepositoryImpl usuarioImpl;

	@Autowired
	private EmailService emailService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		defineConversorDeStringParaLocalDate(binder);
		binder.setAllowedFields("usuario.email", "nome", "sobrenome", "password", "cpf", "rg", "telefone", "nascimento",
				"cnpj", "fundacao", "ie", "site");

	}

	private void defineConversorDeStringParaLocalDate(WebDataBinder binder) {
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

	@GetMapping("/meus-pedidos")
	public String pedidosUsuario(Model model) {
		Usuario usuarioLogado = getUsuarioLogado();
		List<Pedido> pedidos = pedidoRepository.findPedidosByUsuario(usuarioLogado.getEmail());
		model.addAttribute("pedidos", pedidos);
		return "usuario/pedidosUsuario";
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

	// URL que chama o formulário para inserir o e-mail que se deseja recuperar a
	// senha
	@PostMapping("/recuperar-senha")
	public String recuperarSenha() {
		return "usuario/formEnviarEmailRecuperacao";
	}

	// URL que manda um email de recuperação de senha
	@PostMapping("/recuperar-senha/enviarEmailRecuperacao")
	public String mandarEmailRecuperacao(String email, HttpServletRequest request, Model model)
			throws UnsupportedEncodingException, MessagingException {
		Usuario usuario = new Usuario();
		try {
			usuario = usuarioRepository.findById(email).get();
		} catch (Exception e) {
			model.addAttribute("erro",
					"Caso este e-mail corresponda a uma conta do nosso sistema, enviaremos um e-mail com o processo de recuperação");
			return "usuario/formEnviarEmailRecuperacao";
		}
		String codigo = RandomString.make(20);
		usuario.setCodigoVerificacao(codigo);
		usuarioRepository.save(usuario);
		enviaEmailDeRecuperacaoDeSenha(usuario, codigo);
		/*
		 * String content = "Você solicitou a recuperacao de senha,<br>" +
		 * "Clique no link abaixo para redefini-la<br>" +
		 * "<h3><a href=\"[[URL]]\">Redefinicao de senha</a></h3>"; String link =
		 * "/usuario/trocar-senha-esquecida/";
		 */
		// emailService.enviarEmail(usuario, codigo, request, content, link);
		return "usuario/formConfirmarCodigoRecuperacao";
	}

	private void enviaEmailDeRecuperacaoDeSenha(Usuario usuario, String codigo) {
		Email emailEnviar = new Email();
		emailEnviar.setAssunto("Código de mudança de senha");
		emailEnviar.setOrigem("jcaferreira9@gmail.com");
		emailEnviar.setDestinatario(usuario.getEmail());
		emailEnviar.setMensagem("Insira esse código no formulário de recuperação de senha: " + codigo);
		emailService.enviarEmail(emailEnviar);
	}

	// URL que chama o formulário para inserir uma nova senha em caso de
	// esquecimento
	@PostMapping("/trocar-senha-esquecida")
	public String formularioEsqueciSenha(String codigo, Usuario usuario, Model model) {
		try {
			Usuario usuarioCodigo = usuarioRepository.findByCodigoVerificacao(codigo);
			model.addAttribute("codigo", usuarioCodigo.getCodigoVerificacao());
		} catch (Exception e) {
			throw new RuntimeException("deu erro com o codigo");
		}
		return "usuario/formTrocarSenhaEsquecida";
	}

	@PostMapping("/trocarSenhaEsquecida")
	public String novaSenha(@Valid Usuario usuario, BindingResult result, String confirmarSenha, Model model,
			String codigo, RedirectAttributes attributes) {
		if (!usuario.getPassword().equals("")) {
			if (!usuario.getPassword().equals(confirmarSenha)) {
				result.rejectValue("password", "SenhasNaobatem.usuario.senha");
			}
		}
		if (result.hasFieldErrors("password")) {
			model.addAttribute("codigo", codigo);
			return "usuario/formTrocarSenhaEsquecida";
		}
		try {
			Usuario usuarioCodigo = usuarioRepository.findByCodigoVerificacao(codigo);
			usuarioCodigo.setCodigoVerificacao(null);
			usuarioCodigo.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
			usuarioCodigo.setEnabled(true);
			usuarioRepository.save(usuarioCodigo);
			attributes.addFlashAttribute("sucessoAlteracaoSenha",
					"Sua senha foi recuperada com sucesso, faça login com sua nova senha");
			enviaNotificacaoDeTrocaDeSenha(usuarioCodigo.getEmail());
		} catch (Exception e) {
			throw new RuntimeException("deu erro com o codigo");
		}
		return "redirect:/login";
	}

	@GetMapping("/sucessoContaCriada")
	public String sucessoContaCriada(Model model) {
		model.addAttribute("mensagem",
				"Parabéns, você criou sua conta com sucesso, verifique seu e-mail para confirmar seu cadastro e ter acesso ao nosso comércio");
		return "usuario/infoSobreConta";
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

	@GetMapping("/mudar-senha")
	public String formularioMudarSenha(Usuario usuario, String novaSenha) {
		System.out.println(usuario.getPassword());
		return "usuario/formularioMudarSenha";
	}

	@PostMapping("/nova-senha")
	public String novaSenha(@Valid Usuario usuario, BindingResult result, Model model, String novaSenha) {
		// System.out.println(usuario.getPassword() + " nova senha" + novaSenha);

		boolean matches = new BCryptPasswordEncoder().matches(usuario.getPassword(), getUsuarioLogado().getPassword());

		if (novaSenha.isEmpty()) {
			result.rejectValue("password", "NovaSenhaVazia.usuario.senha");
		}
		if (!matches) {
			result.rejectValue("password", "SenhasNaobatemBanco.usuario.senha");
			model.addAttribute("usuario", usuario);
			return "usuario/formularioMudarSenha";
		}
		if (result.hasFieldErrors("password")) {
			model.addAttribute("usuario", usuario);
			return "usuario/formularioMudarSenha";
		}
		usuario.setPassword(novaSenha);
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		// validator.afterPropertiesSet();
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(usuario, "Usuario");
		validator.validate(usuario, bindingResult);
		result = bindingResult;
		if (bindingResult.hasFieldErrors("password")) {
			model.addAttribute("errors", result.getFieldErrors("password"));
			return "usuario/formularioMudarSenha";
		}
		usuarioRepository.atualizaSenha(getUsuarioLogado().getEmail(), new BCryptPasswordEncoder().encode(novaSenha));
		Usuario usuario2 = usuarioRepository.findById(getUsuarioLogado().getEmail()).get();
		atualizaUsuarioLogado(usuario2);
		enviaNotificacaoDeTrocaDeSenha(usuario2.getEmail());
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
	public String formularioEditar(Usuario usuario, Model model, Endereco endereco) throws Exception {
		Usuario usuarioLogado = getUsuarioLogado();

		ClienteFisico clienteFisico = null;
		ClienteJuridico clienteJuridico = null;
		clienteFisico = clienteFisicoRepository.findClienteByEmail(getUsuarioLogado().getEmail());
		if (clienteFisico == null) {
			clienteJuridico = clienteJuridicoRepository.findClienteByEmail(getUsuarioLogado().getEmail());
		}
		if (clienteFisico != null) {
			model.addAttribute("clienteFisico", clienteFisico);
		} else {
			System.out.println(clienteJuridico.getNome());
			model.addAttribute("clienteJuridico", clienteJuridico);
		}
		return "usuario/formularioEditar";
	}

	@PostMapping("/editar")
	public String editarUsuario(@Validated(EditarUsuario.class) ClienteFisico cliente, BindingResult result,
			RedirectAttributes attributes, Endereco endereco, Model model, String email) throws Exception {
		model.addAttribute("endereco", endereco);
		Usuario usuario = cliente.getUsuario();
		// a senha não é alterada nessa view logo eu posso setar o password
		usuario.setPassword(getUsuarioLogado().getPassword());
		usuario.setRoles(getUsuarioLogado().getRoles());

		/*
		 * 1 erro sempre vai dar por causa do password por causa disso o ErrorCount > 1
		 * significa que deu erros o bindingresult ja esta com todos os erros carregados
		 * portanto mesmo setando o password, eu teria que atualizar o bindingresult
		 */
		if (result.hasErrors() && result.getErrorCount() > 1) {
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "usuario/formularioEditar";
		}
		attributes.addFlashAttribute("sucesso", "Usuário alterado com sucesso");
		if (!usuario.getEmail().equals(getUsuarioLogado().getEmail())) {
			usuarioRepository.deletaUsuario(getUsuarioLogado().getEmail());
			atualizaUsuarioLogado(usuario);
		}
		cliente.getUsuario().setEnabled(true);
		Long idCliente = clienteFisicoRepository.findById(getUsuarioLogado().getCliente().getId()).get().getId();
		cliente.setId(idCliente);
		clienteFisicoRepository.save(cliente);
		return "redirect:/usuario/formulario/editar";
	}

	@PostMapping("/editarJuridico")
	public String editarUsuarioJuridico(@Validated(EditarUsuario.class) ClienteJuridico cliente, BindingResult result,
			RedirectAttributes attributes, Endereco endereco, Model model, String email) throws Exception {
		Usuario usuario = cliente.getUsuario();
		// a senha não é alterada nessa view logo eu posso setar o password
		usuario.setPassword(getUsuarioLogado().getPassword());
		usuario.setRoles(getUsuarioLogado().getRoles());
		/*
		 * 1 erro sempre vai dar por causa do password por causa disso o ErrorCount > 1
		 * significa que deu erros o bindingresult ja esta com todos os erros carregados
		 * portanto mesmo setando o password, eu teria que atualizar o bindingresult
		 */
		if (result.hasErrors() && result.getErrorCount() > 1) {
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "usuario/formularioEditar";
		}
		attributes.addFlashAttribute("sucesso", "Usuário alterado com sucesso");
		if (!usuario.getEmail().equals(getUsuarioLogado().getEmail())) {
			usuarioRepository.deletaUsuario(getUsuarioLogado().getEmail());
			atualizaUsuarioLogado(usuario);
		}
		cliente.getUsuario().setEnabled(true);
		Long idCliente = clienteJuridicoRepository.findById(getUsuarioLogado().getCliente().getId()).get().getId();
		cliente.setId(idCliente);
		clienteJuridicoRepository.save(cliente);
		return "redirect:/usuario/formulario/editar";
	}

	private Usuario getUsuarioLogado() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return usuarioRepository.findById(username).get();
	}

	private void criarUsuario(Usuario usuario, List<Authorities> authorities) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>(); // use list if you wish
		for (Authorities authority : authorities) {
			Authorities autho = authoritiesRepository.findById(authority.getAuthority()).get();
			grantedAuthorities.add(autho);
		}
		usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
		usuario.setAuthorities(grantedAuthorities);
		usuarioRepository.save(usuario);
	}

	// O nome do método é auto explicativo
	// Mas explicando: Ele atualiza o usuário que está logado no sistema
	private void atualizaUsuarioLogado(Usuario usuario) {
		User user = new User(usuario.getUsername(), usuario.getPassword(), true, true, true, true,
				usuario.getAuthorities());
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	private void enviaNotificacaoDeTrocaDeSenha(String destinatario) {
		Email email = new Email();
		email.setOrigem("jcaferreira9@gmail.com");
		email.setAssunto("Troca de senha");
		email.setMensagem("Você efeutou a troca de senha com sucesso");
		email.setDestinatario(destinatario);
		emailService.enviarEmail(email);
	}

}
