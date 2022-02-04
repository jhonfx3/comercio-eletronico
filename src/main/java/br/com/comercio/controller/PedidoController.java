package br.com.comercio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.comercio.model.Pedido;
import br.com.comercio.repository.PedidoRepository;

@Controller
@RequestMapping(value = "pedido")
public class PedidoController {

	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping("/detalhe/{id}")
	private String detalhesPedido(@PathVariable("id") String id, Model model) {
		Pedido pedido = pedidoRepository.findById(id).get();
		model.addAttribute("pedido", pedido);
		return "pedido/detalhe";
	}

}
