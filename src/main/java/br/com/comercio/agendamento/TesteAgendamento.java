package br.com.comercio.agendamento;

//@Component
//@EnableScheduling
public class TesteAgendamento {

	//@Autowired
	//private UsuarioRepository usuarioRepository;

	//@Scheduled(initialDelay = 10000, fixedDelay = 60000)
	public void testeAgendamento() {
		/*
		 * Esse código funciona porém não deve ser o mais adequado de invalidar o código
		 * de verificação
		 * 
		 * System.out.println("zerando códigos de verificação..."); List<Usuario>
		 * usuarios = usuarioRepository.findAll(); for (Usuario usuario : usuarios) { if
		 * (usuario.isEnabled()) { if (usuario.getCodigoVerificacao() != null) {
		 * usuario.setCodigoVerificacao(null); usuarioRepository.save(usuario); } } }
		 * 
		 */
	}

}
