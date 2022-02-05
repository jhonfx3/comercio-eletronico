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
		if (pedido.getParcelas() > 1) {
			model.addAttribute("acrescimo", getValorAcrescimo(pedido.getParcelas()));
		}
		return "pedido/detalhe";
	}

	private float getValorAcrescimo(Integer quantidadeParcelas) {
		float porcentagemDeAcrescimo = 0;
		switch (quantidadeParcelas) {
		case 2:
			porcentagemDeAcrescimo = (float) 6.76;
			break;
		case 3:
			porcentagemDeAcrescimo = (float) 8.44;
			break;
		case 4:
			porcentagemDeAcrescimo = (float) 10.23;
			break;
		case 5:
			porcentagemDeAcrescimo = (float) 11.93;
			break;
		case 6:
			porcentagemDeAcrescimo = (float) 13.58;
			break;
		case 7:
			porcentagemDeAcrescimo = (float) 15.01;
			break;
		case 8:
			porcentagemDeAcrescimo = (float) 16.90;
			break;
		case 9:
			porcentagemDeAcrescimo = (float) 18.86;
			break;
		case 10:
			porcentagemDeAcrescimo = (float) 20.07;
			break;
		case 11:
			porcentagemDeAcrescimo = (float) 21.92;
			break;
		case 12:
			porcentagemDeAcrescimo = (float) 23.75;
			break;
		}
		return porcentagemDeAcrescimo;

	}

}
