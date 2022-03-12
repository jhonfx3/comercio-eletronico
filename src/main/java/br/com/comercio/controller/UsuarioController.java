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
import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.impl.UsuarioRepositoryImpl;
import br.com.comercio.model.Authorities;
import br.com.comercio.model.Email;
import br.com.comercio.model.Endereco;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.AuthoritiesRepository;
import br.com.comercio.repository.PedidoRepository;
import br.com.comercio.repository.UsuarioRepository;
import br.com.comercio.service.CriptografiaService;
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
	private UsuarioRepositoryImpl usuarioImpl;

	@Autowired
	private EmailService emailService;

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
		binder.setAllowedFields("email", "nome", "sobrenome", "password", "cpf", "rg", "telefone", "nascimento");

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
			model.addAttribute("erro", "Ocorreu um erro interno, não é possivel mandar o e-mail");
			return "usuario/formEnviarEmailRecuperacao";
		}
		String codigo = RandomString.make(20);
		/*
		 * Se eu já possuo um código de verificação então eu pego o mantenho, não seto
		 * um novo senão isso pode fazer com que caso outros emails já tenham sido
		 * enviados antes, eles não serão mais válidos se bem que isso pode até ser
		 * benéfico pois um código deveria ter um prazo para mudar de senha se expirou,
		 * não vale mais esse código preciso pensar melhor a respeito, depende da regra
		 * de negócio na verdade, segurança
		 */
		if (usuario.getCodigoVerificacao() != null) {
			codigo = usuario.getCodigoVerificacao();
		} else {
			usuario.setCodigoVerificacao(codigo);
		}
		usuarioRepository.save(usuario);
		String content = "Você solicitou a recuperacao de senha,<br>" + "Clique no link abaixo para redefini-la<br>"
				+ "<h3><a href=\"[[URL]]\">Redefinicao de senha</a></h3>";
		String link = "/usuario/trocar-senha-esquecida/";
		emailService.enviarEmail(usuario, codigo, request, content, link);
		return "redirect:/";
	}

	// URL que chama o formulário para inserir uma nova senha em caso de
	// esquecimento
	@GetMapping("/trocar-senha-esquecida/{codigo}")
	public String formularioEsqueciSenha(@PathVariable("codigo") String codigo, Usuario usuario, Model model) {
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
			usuarioRepository.save(usuarioCodigo);
			attributes.addFlashAttribute("sucessoAlteracaoSenha",
					"Sua senha foi recuperada com sucesso, faça login com sua nova senha");
			enviaNotificacaoDeTrocaDeSenha(usuarioCodigo.getEmail());
		} catch (Exception e) {
			throw new RuntimeException("deu erro com o codigo");
		}
		return "redirect:/login";
	}

	@PostMapping("/novo")
	public String novo(@Validated(PersistirUsuario.class) Usuario usuario, BindingResult result,
			HttpServletRequest request, RedirectAttributes attributes, String confirmarSenha) throws Exception {
		if (!usuario.getPassword().equals("")) {
			if (!usuario.getPassword().equals(confirmarSenha)) {
				result.rejectValue("password", "SenhasNaobatem.usuario.senha");
			}
		}
		if (result.hasErrors()) {
			return "usuario/formulario";
		}
		usuario.setCpf(new CriptografiaService().encriptar(usuario.getCpf()));
		Authorities userAuthority = authoritiesRepository.findById("ROLE_USER").get();
		String codigo = RandomString.make(20);
		usuario.setCodigoVerificacao(codigo);
		criarUsuario(usuario, Arrays.asList(userAuthority));
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " cadastrado com sucesso");
		String content = "Bem-vindo " + usuario.getNome() + ",<br>"
				+ "Clique no link abaixo para confirmar seu cadastro no nosso e-commerce<br>"
				+ "<h3><a href=\"[[URL]]\">Confirme seu cadastro</a></h3>";
		String link = "/usuario/confirmar/";
		emailService.enviarEmail(usuario, codigo, request, content, link);
		return "redirect:/usuario/sucessoContaCriada";
	}

	@GetMapping("/sucessoContaCriada")
	public String sucessoContaCriada(Model model) {
		System.out.println("chamando...");
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

	@PostMapping("/editar")
	public String editarUsuario(@Validated(EditarUsuario.class) Usuario usuario, BindingResult result,
			RedirectAttributes attributes, Endereco endereco, Model model) throws Exception {
		if (result.hasErrors()) {
			model.addAttribute("endereco", endereco);
			return "usuario/formularioEditar";
		}
		Usuario usuarioLogado = getUsuarioLogado();
		usuario.setRoles(usuarioLogado.getRoles());

		/*
		 * Se eu identifico que o usuário trocou de e-mail significa que o Spring
		 * JPA(Hibernate) vai identificar uma nova chave. Então ele irá criar um novo
		 * registro, ele não interpreta como um registro existente no bd, porque
		 * obviamente ainda não existe
		 */

		/*
		 * Uma maneira de não precisar disso é simplesmente eu adicionar um ID
		 * incremental que nunca muda, assim ele sempre considera como o mesmo registro
		 */

		if (!usuario.getEmail().equals(usuarioLogado.getEmail())) {
			/*
			 * Então eu preciso deletar o usuário do antigo e-mail do contrário será mantido
			 * no banco de dados impedindo que novos usuários o usem
			 */
			usuarioRepository.deletaUsuario(usuarioLogado.getEmail());
		}
		usuario.setCpf(new CriptografiaService().encriptar(usuario.getCpf()));
		usuario.setEnabled(true);
		usuarioImpl.updateUser(usuario);
		// Caso eu perceba que o usuário trocou de e-mail
		// Necessito atualizar o usuário logado
		if (!usuario.getEmail().equals(usuarioLogado.getEmail())) {
			atualizaUsuarioLogado(usuario);
		}
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " alterado com sucesso");
		return "redirect:/usuario/formulario/editar";
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
		// String cpfDecriptado = criptografia.decriptar(usuarioLogado.getCpf());
		// usuarioLogado.setCpf(cpfDecriptado);
		model.addAttribute("usuario", usuarioLogado);
		return "usuario/formularioEditar";
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
