package br.com.comercio.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.comercio.interfaces.UniqueUsernamePersistir;
import br.com.comercio.repository.UsuarioRepository;

public class UsuarioValidatorPersistir implements ConstraintValidator<UniqueUsernamePersistir, Object> {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public void initialize(UniqueUsernamePersistir usuario) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		String username = (String) value;
		try {
			usuarioRepository.findById(username).get();
			// Se achou alguém com esse username então não posso deixar validar
			return false;
		} catch (Exception e) {
			// Se não achou ninguém, então posso deixar validar
			return true;
		}
	}

}