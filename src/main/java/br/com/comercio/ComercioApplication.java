package br.com.comercio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mercadopago.MercadoPago;

import br.com.comercio.model.Categoria;
import br.com.comercio.repository.CategoriaRepository;

@SpringBootApplication
public class ComercioApplication implements CommandLineRunner {

	@Value("${mercadopago_access_token}")
	private String mercadoPagoAccessToken;
	@Autowired
	private CategoriaRepository categoriaRepository;

	public static void main(String[] args) {
		SpringApplication.run(ComercioApplication.class, args);
		// System.out.println(new BCryptPasswordEncoder().encode("joao"));
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
		verificaSePrecisaPersistirCategorias();
	}

	private void verificaSePrecisaPersistirCategorias() {
		List<String> nomeCategorias = new ArrayList<String>();
		nomeCategorias.add("eletrônicos");
		nomeCategorias.add("eletrodomésticos");
		nomeCategorias.add("celulares");
		nomeCategorias.add("diversos");
		for (String categoria : nomeCategorias) {
			try {
				Categoria categoriaBuscada = categoriaRepository.findByNome(categoria);
				// Se eu não encontrei essa categoria no bd
				// significa que eu preciso persisti-la
				if (categoriaBuscada == null) {
					Categoria categoriaCad = new Categoria();
					categoriaCad.setNome(categoria);
					categoriaRepository.save(categoriaCad);
				}
			} catch (Exception e) {

			}
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
