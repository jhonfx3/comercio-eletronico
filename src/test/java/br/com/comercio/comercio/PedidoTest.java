package br.com.comercio.comercio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mercadopago.exceptions.MPException;

import br.com.comercio.repository.PedidoRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PedidoTest {

	@Autowired
	private PedidoRepository pedidoRepository;

	@BeforeEach
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	@AfterEach
	public void after() {
	}

	@Test
	public void verificaSeOsDadosDosPedidosEstaoSincronizadosComMP() throws MPException {
		/*
		 * Pedido pedido = pedidoRepository.findFirst(); Payment pedidoMercadoPago =
		 * Payment.findById(pedido.getId());
		 * Assertions.assertEquals(pedidoMercadoPago.getTransactionAmount(),
		 * pedido.getTotal().floatValue());
		 * Assertions.assertEquals(pedidoMercadoPago.getPaymentMethodId().toString(),
		 * pedido.getMetodoPagamento());
		 * Assertions.assertEquals(pedidoMercadoPago.getInstallments(),
		 * pedido.getParcelas()); Assertions.assertEquals(pedidoMercadoPago.getId(),
		 * pedido.getId());
		 * Assertions.assertEquals(pedidoMercadoPago.getStatus().toString(),
		 * pedido.getStatus().toString());
		 * if(pedidoMercadoPago.getTransactionDetails().getExternalResourceUrl()!=null)
		 * { Assertions.assertNotNull(pedido.getLinkBoleto());
		 * Assertions.assertEquals(pedidoMercadoPago.getTransactionDetails().
		 * getExternalResourceUrl(), pedido.getLinkBoleto()); }
		 */
	}

}
