package br.com.comercio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("admin")
@Controller
public class AdminController {

	@GetMapping("/painel")
	public String painel() {
		return "admin/painel";
	}
}
