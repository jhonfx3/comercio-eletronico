package br.com.comercio.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(String erro, Model model, HttpServletRequest request, RedirectAttributes attributes) {
		if (erro != null && erro.equals("true")) {
			model.addAttribute("erro",
					"Falha no login: E-mail ou senha incorretos. Ou talvez você ainda não tenha ativado sua conta.");
		} else {
			model.addAttribute("erro", null);
		}
		System.out.println(request.getHeader("Referer"));
		HttpSession session = request.getSession();
		session.setAttribute("urlAnterior", request.getHeader("Referer"));
		return "login";
	}
}
