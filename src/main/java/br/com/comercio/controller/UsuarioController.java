package br.com.comercio.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.comercio.hibernategroups.EditarUsuario;
import br.com.comercio.hibernategroups.PersistirUsuario;
import br.com.comercio.impl.UsuarioRepositoryImpl;
import br.com.comercio.model.Authorities;
import br.com.comercio.model.Endereco;
import br.com.comercio.model.Pedido;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.AuthoritiesRepository;
import br.com.comercio.repository.PedidoRepository;
import br.com.comercio.repository.UsuarioRepository;

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

	@PostMapping("/novo")
	public String novo(@Validated(PersistirUsuario.class) Usuario usuario, BindingResult result,
			RedirectAttributes attributes, String confirmarSenha) {
		if (!usuario.getPassword().equals("")) {
			if (!usuario.getPassword().equals(confirmarSenha)) {
				result.rejectValue("password", "SenhasNaobatem.usuario.senha");
			}
		}
		if (result.hasErrors()) {
			return "usuario/formulario";
		}
		Authorities userAuthority = authoritiesRepository.findById("ROLE_USER").get();
		criarUsuario(usuario, Arrays.asList(userAuthority));
		attributes.addFlashAttribute("sucesso", "Usuário " + usuario.getUsername() + " cadastrado com sucesso");
		return "redirect:/usuario/formulario";
	}

	@PostMapping("/editar")
	public String editarUsuario(@Validated(EditarUsuario.class) Usuario usuario, BindingResult result,
			RedirectAttributes attributes, Endereco endereco, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("endereco", endereco);
			System.out.println("deu erro");
			List<ObjectError> allErrors = result.getAllErrors();
			for (ObjectError objectError : allErrors) {
				System.out.println(objectError.getDefaultMessage());
			}
			return "usuario/formularioEditar";
		}
		Usuario usuarioLogado = getUsuarioLogado();
		usuario.setRoles(usuarioLogado.getRoles());
		System.out.println("usuario email: " + usuario.getEmail());
		System.out.println("usuario logado email: " + usuarioLogado.getEmail());

		/*
		 * Se eu identifico que o usuário trocou de e-mail significa que o Spring
		 * JPA(Hibernate) vai identificar uma nova chave. Então ele irá criar um novo
		 * registro, ele não interpreta como um registro existente no bd, porque
		 * obviamente ainda não existe
		 */
		if (!usuario.getEmail().equals(usuarioLogado.getEmail())) {
			System.out.println("entrei nesse if");
			/*
			 * Então eu preciso deletar o usuário do antigo e-mail do contrário será mantido
			 * no banco de dados impedindo que novos usuários o usem
			 */
			usuarioRepository.deletaUsuario(usuarioLogado.getEmail());
		}
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
	//	System.out.println(usuario.getPassword() + " nova senha" + novaSenha);
		
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
		validator.afterPropertiesSet();
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
	public String formularioEditar(Usuario usuario, Model model, Endereco endereco) {
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
}
