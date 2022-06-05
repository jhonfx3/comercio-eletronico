package br.com.comercio.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.comercio.service.RecaptchaService;

public class RecaptchaFilter extends UsernamePasswordAuthenticationFilter {

	public RecaptchaFilter(String loginUrl, String method) {
		super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl, method));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String recaptchaFormResponse = request.getParameter("g-recaptcha-response");
		System.out.println(recaptchaFormResponse);
		boolean verificaRecaptcha = new RecaptchaService().verificaRecaptcha(recaptchaFormResponse);
		if (!verificaRecaptcha) {
			try {
				request.getSession().setAttribute("recaptchaErro", "Por favor, confirme que você não é um robô");
				request.setAttribute(recaptchaFormResponse, recaptchaFormResponse);
				response.sendRedirect("/login?erro=1");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		} else {
			return super.attemptAuthentication(request, response);
		}
	}

}
