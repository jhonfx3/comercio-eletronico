package br.com.comercio.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import br.com.comercio.validator.ListaValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListaValidator.class)
@Documented
public @interface ListaVazia {
   
  String message() default "Preço obrigatório";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
  String value() default "";
}