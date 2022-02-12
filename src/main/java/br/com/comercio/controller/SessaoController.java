package br.com.comercio.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessaoController {
	@GetMapping("/sessaotimeout")
	public String login(String mensagem,Model model, HttpServletRequest request) {
		if(mensagem != null && mensagem.equals("maximoSessoes")) {
			model.addAttribute("mensagem", "Você só pode efetuar um login por vez. Você foi deslogado");
		}
		if(mensagem != null && mensagem.equals("expirado")) {
			model.addAttribute("mensagem", "Sua sessão foi finalizada devido a inatividade. Logue novamente caso queira");
		}
		System.out.println(mensagem);
		return "sessao/sessaotimeout";
	}
}
