package br.com.comercio.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.comercio.interfaces.AntiXss;

public class XssValidator implements ConstraintValidator<AntiXss, Object> {

	@Override
	public void initialize(AntiXss xss) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		String valor = (String) value;
		if (valor.isEmpty())
			return true;
		if (valor.contains(">") || valor.contains("<")) {
			return false;
		}
		return true;

	}

}