package br.com.comercio.hibernategroups;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import br.com.comercio.model.Usuario;

public class UsuarioGroupProvider implements DefaultGroupSequenceProvider<Usuario> {

	@Override
	public List<Class<?>> getValidationGroups(Usuario usuario) {
		List<Class<?>> groups = new ArrayList<>();

		groups.add(Usuario.class);

		if (usuario.getUsername() != null) {
			groups.add(EditarUsuario.class);
			System.out.println("adicionando o editar usuario");
		} else {
			groups.add(PersistirUsuario.class);
			System.out.println("adicionando o persistir usuario");
		}

		return groups;
	}

}
