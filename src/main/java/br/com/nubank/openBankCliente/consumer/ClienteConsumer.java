package br.com.nubank.openBankCliente.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.nubank.openBankCliente.dto.ClienteCreditoOpenBank;
import br.com.nubank.openBankCliente.service.ClienteOpenBankService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClienteConsumer {

	@Autowired
	private ClienteOpenBankService clienteOpenBankService;

	@KafkaListener(topics = "${order.topicAvalicao}", groupId = "${spring.kafka.consumer.group-id}")
	public void consumer(final ConsumerRecord<String, ClienteCreditoOpenBank> consumerRecord) {
		log.debug("Iniciou o processamento do cliente: {}", consumerRecord.key());
		this.clienteOpenBankService.processar(consumerRecord.key(), consumerRecord.value());
	}

}
