package br.com.comercio.hibernategroups;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.springframework.stereotype.Component;

import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

@Component
public class UsuarioGroupProvider implements DefaultGroupSequenceProvider<Usuario> {
	private UsuarioRepository usuarioRepository;


	@Override
	public List<Class<?>> getValidationGroups(Usuario usuario) {
		List<Class<?>> groups = new ArrayList<>();

		groups.add(Usuario.class);
		if (usuarioRepository == null) {
			System.out.println("repository null");
		}
		if (usuario != null) {
			System.out.println("usuario diferente de null..");
			if (usuario.getUsername() != null) {
				System.out.println(usuario.getUsername());
				Usuario usuario2 = usuarioRepository.findById(usuario.getUsername()).get();
				if (usuario2 != null) {
					groups.add(EditarUsuario.class);
				} else {
					System.out.println("adicionando o editar usuario");
					groups.add(PersistirUsuario.class);
				}
			}
		}

		return groups;
	}

}
