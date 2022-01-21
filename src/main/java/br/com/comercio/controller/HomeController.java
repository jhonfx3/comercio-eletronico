package br.com.comercio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.Payment.Status;
import com.mercadopago.resources.datastructures.payment.Payer;

import br.com.comercio.model.Produto;
import br.com.comercio.repository.ProdutoRepository;

@Controller
public class HomeController {

	@Autowired
	private ProdutoRepository produtoRepository;

	@GetMapping("/")
	public String home(Model model) throws MPException {
		System.out.println("Exibindo as variaveis de ambiente do heroku");
		System.out.println(System.getenv("mercadoPagoAccessToken"));
		System.out.println(System.getenv("mercadoPagoPublicKey"));
		List<Produto> produtos = produtoRepository.findAll();
		model.addAttribute("produtos", produtos);
		return "home";
	}
}
