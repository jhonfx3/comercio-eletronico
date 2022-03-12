package br.com.comercio.validator;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.comercio.interfaces.Maioridade;

public class MaioridadeValidator implements ConstraintValidator<Maioridade, Object> {

	@Override
	public void initialize(Maioridade maioridade) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		LocalDate nascimento = (LocalDate) value;
		Calendar nascimentoCalendar = Calendar.getInstance();
		/*
		 * Deixo o validator passar pois não é resoonsabilidade dessa classe validar se
		 * é nulo ou não
		 */
		if (nascimento == null)
			return true;
		nascimentoCalendar.setTime(Date.valueOf(nascimento));
		int anoNascimento = nascimentoCalendar.get(Calendar.YEAR);
		int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
		if (anoAtual - anoNascimento < 18)
			return false;
		return true;

	}

}