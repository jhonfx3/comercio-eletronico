package br.com.comercio.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(value = "erro", required = false) Integer erro,
			@RequestParam(value = "recaptcha", required = false) String recaptcha, Model model,
			HttpServletRequest request, RedirectAttributes attributes) {
		HttpSession session = request.getSession();
		if (erro != null && erro == 1) {
			model.addAttribute("erro",
					"Falha no login: E-mail ou senha incorretos. Ou talvez você ainda não tenha ativado sua conta.");
		} else {
			model.addAttribute("erro", null);
		}
		if(session.getAttribute("recaptchaErro")!=null) {
			String erroRecaptcha = (String) session.getAttribute("recaptchaErro");
			System.out.println("atributo ->"+erroRecaptcha);
			model.addAttribute("erroRecaptcha",erroRecaptcha);
			session.removeAttribute("recaptchaErro");
		}
		System.out.println(request.getHeader("Referer"));
		session.setAttribute("urlAnterior", request.getHeader("Referer"));
		return "login";
	}
}
