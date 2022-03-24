package br.com.comercio.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.comercio.interfaces.UniqueColumn;
import br.com.comercio.repository.ClienteFisicoRepository;
import br.com.comercio.service.CriptografiaService;

public class UniqueValidator implements ConstraintValidator<UniqueColumn, Object> {
	private String campo;
	private Class<?> nomeClasseImplRepository;
	@Autowired
	private ClienteFisicoRepository repository;

	@PersistenceContext
	private EntityManager manager;
	private Class<?> classeASerValidada;
	private Class<?> tipoParametro;

	@Override
	public void initialize(UniqueColumn unique) {
		campo = unique.campo();
		nomeClasseImplRepository = unique.nomeClasseImplRepository();
		classeASerValidada = unique.classeASerValidada();
		tipoParametro = unique.tipoParametro();
		System.out.println("Campo: " + campo + " nome classe impl:" + nomeClasseImplRepository.getName()
				+ "Classe a ser validada: " + classeASerValidada.getName() + " tipo parametro: "
				+ tipoParametro.getName());
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		String cpf = (String) value;
		try {
			// Setando a class que implementou simplejparepository vinda da anotação
			Class<?> cls = nomeClasseImplRepository;
			// Pegando os construtores
			Constructor<?>[] construtores = cls.getDeclaredConstructors();

			// Definindo o tipo dos parametros
			// Nesse caso so vai funcionar se o campo for do tipo string
			// Mas da pra mudar isso, agora to cansado,
			// É só passar na anotação o tipo do parametro
			Class[] paramString = new Class[1];
			paramString[0] = tipoParametro;

			// Vetor que vai pegar os tipos dos parametros do construtor
			Parameter[] parameters = null;

			// Pegando os parametros do construtor
			for (Constructor<?> constructor : construtores) {
				parameters = constructor.getParameters();
			}
			// Meio que instanciando o construtor passando seus respectivos tipos
			Constructor<?> construtor = cls.getConstructor(
					new Class[] { parameters[0].getType(), parameters[1].getType(), parameters[2].getType() });

			// Instanciando o objeto
			/*
			 * Sempre vai ter um manager e um repository, mas o Usuario.class so vai
			 * funcionar se a classe que eu quiser validar for um Usuario preciso mudar e
			 * colocar para passar como parametro o model a ser validado do contrário, isso
			 * não funcionará para outras entidades
			 */
			Object obj = construtor.newInstance(new Object[] { classeASerValidada, manager, repository });
			System.out.println(cls.getClass().getName());
			Method metodo = cls.getDeclaredMethod("findBy" + campo, paramString);
			System.out.println("cpf ->" + cpf);
			Object invoke = metodo.invoke(obj, new String(new CriptografiaService().encriptar(cpf)));
			System.out.println("invoke -> " + invoke);
			if (invoke == null) {
				// não encontrei ninguém com esse campo, então posso permitir
				return true;
			} else {
				// já existe alguém com esse valor, então tenho que recusar
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

}