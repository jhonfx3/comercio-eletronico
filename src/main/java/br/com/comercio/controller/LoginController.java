package br.com.comercio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	@GetMapping("/login")
	public String login(String erro, String invalidsession,Model model) {
		System.out.println(erro);
		if (erro != null && erro.equals("true")) {
			model.addAttribute("erro", "E-mail ou senha incorretos. Revise-os!");
		} else {
			model.addAttribute("erro", null);
		}
		if(invalidsession != null) {
			System.out.println(invalidsession);
		}
		return "login";
	}
}
