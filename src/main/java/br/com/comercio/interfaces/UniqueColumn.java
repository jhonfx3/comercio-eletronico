package br.com.comercio.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.comercio.validator.UniqueValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValidator.class)
@Documented
public @interface UniqueColumn {

	String message() default "O campo já existe";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value() default "";

	String campo();
	
	Class<?> tipoParametro();
	
	Class<?> classeASerValidada();
	
	Class<?> nomeClasseImplRepository();
}