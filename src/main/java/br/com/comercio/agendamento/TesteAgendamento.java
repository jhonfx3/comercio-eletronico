package br.com.comercio.agendamento;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TesteAgendamento {

	@Scheduled(initialDelay = 10000,fixedDelay = 10000)
	public void testeAgendamento() {
		System.out.println("chamando agendamento...");
	}
	
}
