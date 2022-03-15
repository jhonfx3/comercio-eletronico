package br.com.comercio.agendamento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

@Component
@EnableScheduling
public class TesteAgendamento {

	@Autowired
	private UsuarioRepository usuarioRepository;

	// Começa com 10 segundos e a cada 10 minutos repete
	@Scheduled(initialDelay = 10000, fixedDelay = 600000)
	public void testeAgendamento() {
		// Devem existir melhores formas de fazer essa rotina
		// Preciso aprimorar...
		System.out.println("zerando códigos de verificação...");
		List<Usuario> usuarios = usuarioRepository.findAll();
		for (Usuario usuario : usuarios) {
			if (usuario.isEnabled()) {
				if (usuario.getCodigoVerificacao() != null) {
					usuario.setCodigoVerificacao(null);
					usuarioRepository.save(usuario);
				}
			}
		}

	}

}
