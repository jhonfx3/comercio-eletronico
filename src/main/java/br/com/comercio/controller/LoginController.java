package br.com.comercio.controller;

import java.util.Map;
import java.util.Map.Entry;

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
			model.addAttribute("erro", "E-mail ou senha incorretos. Revise-os!");
		} else {
			model.addAttribute("erro", null);
		}
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Entry<String, String[]> entry : parameterMap.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
		HttpSession session = request.getSession();
		session.setAttribute("urlAnterior", request.getHeader("Referer"));
		return "login";
	}
}
