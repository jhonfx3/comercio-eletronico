package br.com.comercio.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.comercio.interfaces.ListaVazia;
import br.com.comercio.model.Preco;

public class ListaValidator implements ConstraintValidator<ListaVazia, Object> {

	@Override
	public void initialize(ListaVazia vazia) {

	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		List<Preco> precos = (List<Preco>) value;
		for (Preco preco : precos) {
			if (preco.getValor() == null) {
				return false;
			}
		}
		// false = nunca vai
		// true = sempre vai
		return true;
	}

}