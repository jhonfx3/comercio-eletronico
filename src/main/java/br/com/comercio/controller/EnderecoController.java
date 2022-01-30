package br.com.comercio.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String novo(@Valid Endereco endereco, BindingResult result, Model model, RedirectAttributes attrs) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRepository.findById(username).get();
		if (result.hasErrors()) {
			System.out.println("deu erro");
			model.addAttribute("usuario", usuario);
			model.addAttribute("endereco", endereco);
			return "usuario/formularioEditar";
		}
		System.out.println("salvando endereco...");
		System.out.println(endereco.getLogradouro());
		endereco.setUsuario(usuario);
		enderecoRepository.save(endereco);
		attrs.addFlashAttribute("sucessoEndereco", "Endereco cadastrado com sucesso");
		return "redirect:/usuario/formulario/editar";
	}
}
