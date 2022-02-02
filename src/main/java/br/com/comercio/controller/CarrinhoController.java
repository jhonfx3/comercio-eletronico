package br.com.comercio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import br.com.comercio.enums.TipoPreco;
import br.com.comercio.model.CarrinhoDeCompras;
import br.com.comercio.model.CarrinhoItem;
import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
@RequestMapping(value = "carrinho")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class CarrinhoController {

	@Autowired
	private ProdutoRepository ProdutoRepository;

	@Value("${spring.profiles.active}")
	private String profileAtivo;
	@Value("${mercadopago_public_key}")
	private String mercadopagoPublicKey;

	@Autowired
	private CarrinhoDeCompras carrinho;

	@GetMapping("/limpar")
	public String limpar() {
		carrinho.limpaCarrinho();
		return "redirect:/carrinho";
	}

	@GetMapping("/remover/{id}")
	public String remover(@PathVariable("id") Long produtoId) {
		Produto produto = ProdutoRepository.findByIdProduto(produtoId);
		carrinho.remove(produto);
		return "redirect:/carrinho";
	}

	@PostMapping("/adiciona")
	public String adiciona(Model model, Long produtoId) {
		Produto produto = ProdutoRepository.findByIdProduto(produtoId);
		carrinho.adiciona(new CarrinhoItem(produto));
		model.addAttribute("itens", carrinho.getItensMap());
		model.addAttribute("totalVista", carrinho.getTotalCarrinho(TipoPreco.VISTA));
		model.addAttribute("totalPrazo", carrinho.getTotalCarrinho(TipoPreco.PRAZO));
		model.addAttribute("tipos", TipoPreco.values());
		model.addAttribute("profileAtivo", profileAtivo);
		return "redirect:/carrinho";
	}

	@GetMapping
	public String carrinho(Model model, Long produtoId) {
		model.addAttribute("itens", carrinho.getItensMap());
		model.addAttribute("totalVista", carrinho.getTotalCarrinho(TipoPreco.VISTA));
		model.addAttribute("totalPrazo", carrinho.getTotalCarrinho(TipoPreco.PRAZO));
		model.addAttribute("tipos", TipoPreco.values());
		model.addAttribute("profileAtivo", profileAtivo);
		return "carrinho/itens";
	}

	@PostMapping("/formulario")
	public String retornaFormPagamento(Model model, String formaPagamento) {
		System.out.println("Forma pagamento ->" + formaPagamento);
		String mercadoPagoPublicKeyHeroku = System.getenv("mercadopagoPublicKey");
		// Quer dizer que estou no heroku
		if (mercadoPagoPublicKeyHeroku != null) {
			model.addAttribute("mercadopagoPublicKey", mercadoPagoPublicKeyHeroku);
		} else {
			System.out.println(mercadopagoPublicKey);
			model.addAttribute("mercadopagoPublicKey", mercadopagoPublicKey);
		}
		model.addAttribute("totalDaCompra", String.valueOf(carrinho.getTotalCarrinho(TipoPreco.VISTA)));
		return "pagamento/formulario";
	}
}
