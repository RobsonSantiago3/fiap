package br.com.nubank.openBankCliente.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.nubank.openBankCliente.dto.ClienteCreditoNubank;

@Component
public class ClienteProducer {

	@Value("${order.topic}")
	private String orderTopic;
	@Autowired
	private KafkaTemplate kafkaTemplate;

	public void send(final @RequestBody ClienteCreditoNubank clienteCreditoNubank) {
		kafkaTemplate.send(orderTopic, clienteCreditoNubank.getCpf(), clienteCreditoNubank);
	}

}
