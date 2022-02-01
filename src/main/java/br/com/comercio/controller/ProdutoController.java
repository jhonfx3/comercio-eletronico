package br.com.comercio.controller;

import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.comercio.enums.TipoPreco;
import br.com.comercio.model.Preco;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
@RequestMapping("produto")
public class ProdutoController {
	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping("formulario/{acao}")
	public String formulario(@PathVariable("acao") String acao, Produto produto, Model model,
			HttpServletRequest request) {
		model.addAttribute("tipos", TipoPreco.values());
		if (acao.equals("cadastrar")) {
			return "produto/formulario";
		} else {
			if (request.getParameter("produto") != null) {
				model.addAttribute("idProdutoEditar", request.getParameter("produto"));
				List<Preco> precos = produto.getPrecos();
				/*
				 * Preciso ordenar em ordem alfabética senão ocorre inversão de valores na hora
				 * de renderizar na página de edição.
				 */
				precos.sort(new Comparator<Preco>() {
					@Override
					public int compare(Preco p1, Preco p2) {
						return p1.getTipo().compareTo(p2.getTipo());
					}
				});
			}
			return "produto/formularioEditar";
		}

	}

	@PostMapping("editar/{id}")
	public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes attr) {
		Produto produto = produtoRepository.findById(id).get();
		model.addAttribute("produto", produto);
		model.addAttribute("tipos", TipoPreco.values());
		model.addAttribute("idProdutoEditar", id);
		attr.addAttribute("idProdutoEditar", id);
		return "produto/formularioEditar";
	}

	@PostMapping("excluir/{id}")
	public String excluir(@PathVariable("id") Long id, Model model) {
		Produto produto = produtoRepository.getById(id);
		produtoRepository.delete(produto);
		return "redirect:/";
	}

	@GetMapping("detalhe/{id}")
	public String detalhe(@PathVariable("id") Long id, Model model) {
		Produto produto = produtoRepository.findById(id).get();
		model.addAttribute("produto", produto);
		return "produto/detalhe";
	}

	@PostMapping("novo")
	public String novo(@Valid Produto produto, BindingResult result, Model model, RedirectAttributes attributes) {
		model.addAttribute("tipos", TipoPreco.values());
		if (result.hasErrors()) {
			System.out.println("Deu erro!");
			return "produto/formulario";
		}
		produtoRepository.save(produto);
		attributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso");
		return "redirect:formulario/cadastrar";
	}

	@PostMapping("editarProduto")
	public String editarProduto(@Valid Produto produto, BindingResult result, Model model,
			RedirectAttributes attributes, String idProdutoEditar) {
		model.addAttribute("tipos", TipoPreco.values());
		if (result.hasErrors()) {
			System.out.println("Deu erro!");
			model.addAttribute("produto", produto);
			model.addAttribute("idProdutoEditar", idProdutoEditar);
			return "produto/formularioEditar";
		}
		// Tenho que setar um ID para o repository editar e não salvar
		// um produto novo
		produto.setId(Long.valueOf(idProdutoEditar));
		produtoRepository.save(produto);
		attributes.addFlashAttribute("sucesso", "Produto editado com sucesso");
		// Tem que ser addattribute e não flash attribute
		// senão com o passar das requisições o atributo é deletado
		attributes.addAttribute("produto", produto);
		attributes.addAttribute("idProdutoEditar", idProdutoEditar);
		return "redirect:formulario/editar";
	}

}
