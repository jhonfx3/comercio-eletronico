package br.com.comercio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPago;

@SpringBootApplication
public class ComercioApplication implements CommandLineRunner {

	@Value("${mercadopago_access_token}")
	private String mercadoPagoAccessToken;

	public static void main(String[] args) {
		SpringApplication.run(ComercioApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String mercadoPagoAccessTokenHeroku = System.getenv("mercadoPagoAccessToken");
		// Estou usando Heroku
		if (mercadoPagoAccessTokenHeroku != null) {
			MercadoPago.SDK.setAccessToken(mercadoPagoAccessTokenHeroku);
		} else {
			MercadoPago.SDK.setAccessToken(mercadoPagoAccessToken);
		}
	}

	/*
	 * // implements WebMvcConfigurer
	 * 
	 * @Override public void
	 * configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers)
	 * { resolvers.add(new ControllerExceptionHandlerr()); }
	 */

}
