package br.com.comercio.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.comercio.interfaces.SenhaFraca;

public class SenhaFracaValidator implements ConstraintValidator<SenhaFraca, Object> {

	@Override
	public void initialize(SenhaFraca senhaFraca) {

	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null)
			return true;
		String valor = (String) value;
		if (valor.isEmpty())
			return true;
		ClassLoader classLoader = SenhaFracaValidator.class.getClassLoader();
		InputStream in = classLoader.getResourceAsStream("senhas-comuns.txt");
		InputStreamReader streamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
		BufferedReader reader = new BufferedReader(streamReader);
		try {
			while (reader.readLine() != null) {
				// System.out.println(reader.readLine());
				String readLine = reader.readLine();
				if (readLine.equals(valor)) {
					return false;
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
			return true;
		}
		return true;
	}

}