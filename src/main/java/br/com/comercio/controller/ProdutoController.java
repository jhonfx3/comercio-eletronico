package br.com.comercio.controller;

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

import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.CategoriaRepository;
import br.com.comercio.repository.ProdutoRepository;

@Controller
@RequestMapping("produto")
public class ProdutoController {
	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private CarrinhoDeCompras carrinho;

	@GetMapping("formulario/{acao}")
	public String formulario(@PathVariable("acao") String acao, Produto produto, Model model,
			HttpServletRequest request) {
		System.out.println("chamando formulario...");
		model.addAttribute("categorias", categoriaRepository.findAll());
		if (acao.equals("cadastrar")) {
			System.out.println("...");
			return "produto/formulario";
		} else {
			if (request.getParameter("produto") != null) {
				model.addAttribute("idProdutoEditar", request.getParameter("produto"));
			}
			return "produto/formularioEditar";
		}

	}

	@PostMapping("editar/{id}")
	public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes attr) {
		Produto produto = produtoRepository.findById(id).get();
		model.addAttribute("produto", produto);
		model.addAttribute("idProdutoEditar", id);
		model.addAttribute("categorias", categoriaRepository.findAll());
		attr.addAttribute("idProdutoEditar", id);
		CarrinhoItem item = new CarrinhoItem(produto);
		if (carrinho.getItensMap().containsKey(item)) {
			System.out.println("o produto a ser editado esta no carrinho");
			/*
			 * Aqui eu posso criar uma lógica para impedir que um produto que está no
			 * carrinho seja editado porém se eu faço isso ou não depende da regra de
			 * negócio do sistema. Depende de como o cliente irá querer o software. Porque
			 * do jeito que está se eu editar um produto que está no carrinho o preço do
			 * produto no carrinho não será atualizado e isso causará divergências Se eu
			 * impeço o produto de ser editado o admin não consegue edita-lo até que ele
			 * seja liberado. Isso pode ser ruim pois por exemplo vai que o preço do produto
			 * está errado. Mas caso eu permita essa edição eu tenho que atualizar o
			 * carrinho e isso pode ser fustrante para o usuário, já que ele colocou com um
			 * preço e do nada esse preço muda
			 */
		}
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
		model.addAttribute("categorias", categoriaRepository.findAll());
		if (result.hasErrors()) {
			System.out.println("erro!!!");
			return "produto/formulario";
		}
		if (produto.getPreco() == null) {
			System.out.println("preco null");
		} else {
			System.out.println("preco nao null");
		}
		produtoRepository.save(produto);
		attributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso");
		return "redirect:formulario/cadastrar";
	}

	@PostMapping("editarProduto")
	public String editarProduto(@Valid Produto produto, BindingResult result, Model model,
			RedirectAttributes attributes, String idProdutoEditar) {
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
