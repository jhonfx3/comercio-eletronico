package br.com.comercio.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.enums.TipoPreco;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
@RequestMapping("produto")
public class ProdutoCocntroller {
	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping("formulario")
	public String formulario(Produto produto, Model model) {
		model.addAttribute("tipos", TipoPreco.values());
		return "produto/formulario";
	}

	@GetMapping("detalhe/{id}")
	public String detalhe(@PathVariable("id") Long id, Model model) {
		Produto produto = produtoRepository.getById(id);
		model.addAttribute("produto", produto);
		return "produto/detalhe";
	}

	@PostMapping("novo")
	public String novo(@Valid Produto produto, BindingResult result, Model model) {
		model.addAttribute("tipos", TipoPreco.values());
		if (result.hasErrors()) {
			System.out.println("Deu erro!");
			return "produto/formulario";
		}
		model.addAttribute("sucesso", "Produto cadastrado com sucesso");
		produtoRepository.save(produto);
		return "redirect:/";
	}
	
}
