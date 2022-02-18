package br.com.comercio.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.comercio.interfaces.UniqueColumnEditar;
import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;
import br.com.comercio.service.CriptografiaService;

public class UniqueValidatorEditar implements ConstraintValidator<UniqueColumnEditar, Object> {
	private String campo;
	private Class<?> nomeClasseImplRepository;
	@Autowired
	private UsuarioRepository repository;

	@PersistenceContext
	private EntityManager manager;
	private Class<?> classeASerValidada;
	private Class<?> tipoParametro;

	@Override
	public void initialize(UniqueColumnEditar unique) {
		campo = unique.campo();
		nomeClasseImplRepository = unique.nomeClasseImplRepository();
		classeASerValidada = unique.classeASerValidada();
		tipoParametro = unique.tipoParametro();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		String cpf = (String) value;
		if (value == null) {
			System.out.println("E NULL CARALHO");
			// Deixo a responsabilidade para outro validador
			return true;
		}
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
			Method metodo = cls.getDeclaredMethod("findBy" + campo, paramString);
			Object entidadeNoBanco = metodo.invoke(obj, new String(new CriptografiaService().encriptar(cpf)));

			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			Usuario usuario = repository.findById(name).get();
			Method declaredMethod = usuario.getClass().getDeclaredMethod("get" + campo);
			Object invoke = declaredMethod.invoke(usuario);
			System.out.println("infok.." + invoke);
			System.out.println("cpf informado: " + cpf);
			if (cpf.equals(invoke)) {
				/*
				 * Encontrei alguém com esse valor Porém quem eu encontrei é ele mesmo então ele
				 * tem permissão de ter esse valor então a validação é permitida
				 */
				return true;
			} else if (entidadeNoBanco != null) {
				// Encontrei alguém no banco com esse valor, então não pode validar
				System.out.println("estou retornando false...");
				return false;
			} else {
				// Entidade é null, como não encontrei ninguém no banco
				// com esse valor então pode validar
				System.out.println("estou retornando true...");
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

}