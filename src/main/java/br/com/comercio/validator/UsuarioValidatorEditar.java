package br.com.comercio.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.comercio.interfaces.UniqueUsernameEditar;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

public class UsuarioValidatorEditar implements ConstraintValidator<UniqueUsernameEditar, Object> {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public void initialize(UniqueUsernameEditar usuario) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		System.out.println("CHAMANDO USUARIO VALIDATOR EDITAR");
		String usernameDoInput = (String) value;
		String usernameNoBanco = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("username do input:" + usernameDoInput);
		System.out.println("username no banco:" + usernameNoBanco);

		if (usernameNoBanco.equals(usernameDoInput)) {
			// Deixo passar porque ele é o detentor daquele username
			return true;
		} else {
			Usuario usuario = usuarioRepository.findById(usernameDoInput).get();
			if (usuario == null) {
				// Não encontrei ninguém com esse username, pode validar
				return true;
			} else {
				// Já existe alguém com esse username, não pode validar
				return false;
			}
		}

	}

}