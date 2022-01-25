package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mercadopago.exceptions.MPException;

import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
public class HomeController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping("/")
	public String home(Model model) throws MPException {
		List<Produto> produtos = produtoRepository.findAll();
		model.addAttribute("produtos", produtos);
		return "home";
	}

	@GetMapping("/desenvolvedor")
	public String sobreODesenvolvedor(Model model) throws MPException {
		return "desenvolvedor";
	}
}
