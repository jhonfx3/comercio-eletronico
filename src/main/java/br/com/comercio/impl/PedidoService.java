package br.com.comercio.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.comercio.model.Pedido;
import br.com.comercio.repository.PedidoRepository;

@Service
public class PedidoService {
	@Autowired
	private PedidoRepository pedidoRepository;

	public Optional<Pedido> findById(String id) {
		return pedidoRepository.findById(id);
	}
	
	public String teste(String qualquer) {
		return "abacaxi";
	}
	
	

}
