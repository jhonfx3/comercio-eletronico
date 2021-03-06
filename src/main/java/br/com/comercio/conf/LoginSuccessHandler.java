package br.com.comercio.conf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String urlAnterior;
		try {
			HttpSession session = request.getSession();
			urlAnterior = (String) session.getAttribute("urlAnterior");
			// Removo da sessão esse atributo
			session.removeAttribute("urlAnterior");
			/*
			 * Se eu não tenho uma url anterior, então eu seto a url anterio como a página
			 * principal Isso acontece quando o usuário acessa uma url diretamente e não
			 * consegue logo não existe nenhuma url anterior por ex:
			 * produto/formulario/cadastrar
			 */
			if (urlAnterior == null || (urlAnterior != null && urlAnterior.isEmpty())) {
				urlAnterior = "/";
			}
			if (urlAnterior.contains("/login")) {
				urlAnterior = "/";
			}
		} catch (Exception e) {
			e.printStackTrace();
			urlAnterior = "/";
		}
		int status = response.getStatus();
		response.sendRedirect(urlAnterior);
	}

}
