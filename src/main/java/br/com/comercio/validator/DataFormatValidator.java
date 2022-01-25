package br.com.comercio.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.comercio.interfaces.DataFormatValidacao;

public class DataFormatValidator implements ConstraintValidator<DataFormatValidacao, Object> {

	@Override
	public void initialize(DataFormatValidacao data) {

	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		System.out.println("data format validator chamado");
		try {

			if(value == null) {
				System.out.println("??");
			}
			LocalDate dataa = (LocalDate) value;
			System.out.println(dataa.toString());
			System.out.println("?");
		} catch (Exception e) {
			return false;
		}
		if (value != null) {
			System.out.println("?????????????????????");
			LocalDate date = (LocalDate) value;
			String data = date.toString();
			System.out.println("to aqui mano");
			// Se a data é vazia então eu deixo passar, e deixo outro validator ficar
			// encarregado(IsEmpty)
			if (data.isEmpty())
				return false;

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			data = data.replaceAll("-", "/");
			System.out.println("data->" + data);

			try {
				// O localDate já verifica o dia e mes
				data = date.format(formatter).toString();
			} catch (Exception e) {
				return false;
			}

			System.out.println("data->" + data);

			// Se nÃ£o tiver quantidade de caracteres suficientes
			// EntÃ£o Ã© data invÃ¡lida

			if (data.length() < 5)
				return false;
			String dia, mes;
			dia = data.substring(0, data.indexOf("/"));
			mes = data.substring(data.indexOf("/") + 1, data.indexOf("/") + 3);
			System.out.println("Dia: " + dia + " MÃªs: " + mes);
			boolean invalido = true;
			// Vou comecar a verificar os dias
			if (dia.equals("00") || mes.equals("00")) {
				System.out.println("00 ou 00 por isso recebeu true");
				invalido = false;
			}

			if (Integer.valueOf(dia) > 31 || Integer.valueOf(mes) > 12) {
				System.out.println("> 31 ou > 12 por isso recebeu true");
				invalido = false;
			}
			System.out.println("fim do metodo de validacao");
			return invalido;
		}
		return true;
	}

}