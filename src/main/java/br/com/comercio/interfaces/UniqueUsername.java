package br.com.comercio.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.comercio.validator.UsuarioValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsuarioValidator.class)
@Documented
public @interface UniqueUsername {

	String message() default "Usuário já existente";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String value() default "";
}