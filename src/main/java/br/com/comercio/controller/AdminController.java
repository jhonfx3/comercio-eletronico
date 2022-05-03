package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Categoria;
import br.com.comercio.repository.CategoriaRepository;

@RequestMapping("admin")
@Controller
public class AdminController {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping("/painel")
	public String painel(Model model) {
		List<Categoria> categorias = categoriaRepository.findAll();
		model.addAttribute("categorias", categorias);
		return "admin/painel";
	}
}
