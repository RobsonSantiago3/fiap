package br.com.nubank.openBankCliente.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import br.com.nubank.openBankCliente.dto.ClienteCreditoNubank;
import br.com.nubank.openBankCliente.dto.ClienteCreditoOpenBank;
import br.com.nubank.openBankCliente.producer.ClienteProducer;
import br.com.nubank.openBankCliente.service.ClienteOpenBankService;

@Service
public class ClienteOpenBankServiceImpl implements ClienteOpenBankService {

	@Autowired
	private InteractiveQueryService interactiveQueryService;

	@Value("${order.topicAvaliado}")
	private String store;

	@Autowired
	private ClienteProducer clienteProducer;

	@Override
	public void processar(String cpf, ClienteCreditoOpenBank cliente) {

		List<ClienteCreditoOpenBank> porIntituicao = buscaDados(cpf, cliente);

		Optional<ClienteCreditoOpenBank> ultimoMes = porIntituicao.stream().findFirst();

		porIntituicao.add(cliente);

		ClienteCreditoNubank nova = new ClienteCreditoNubank();

		nova.setCpf(cpf);

		nova.setDataUltimaValicao(LocalDateTime.now());
		ClienteCreditoOpenBank clienteCreditoOpenBank = ultimoMes.get();

		nova.setHouveAumento(clienteCreditoOpenBank.getValorCredito().compareTo(cliente.getValorCredito()) < 1);

		nova.setHouveReducao(clienteCreditoOpenBank.getValorCredito().compareTo(cliente.getValorCredito()) > 1);

		nova.setNaoHouveAlteracao(clienteCreditoOpenBank.getValorCredito().compareTo(cliente.getValorCredito()) == 0);

		nova.setBancosCliente(porIntituicao);

		clienteProducer.send(nova);
	}

	private List<ClienteCreditoOpenBank> buscaDados(String cpf, ClienteCreditoOpenBank cliente) {
		List<ClienteCreditoNubank> clienteNubank = buscaAvaliacoesCliente(cpf);

		List<ClienteCreditoOpenBank> porIntituicao = filtraPorInstuicao(cliente, clienteNubank);
		return porIntituicao;
	}

	private List<ClienteCreditoOpenBank> filtraPorInstuicao(ClienteCreditoOpenBank cliente,
			List<ClienteCreditoNubank> clienteNubank) {
		List<ClienteCreditoOpenBank> openBank = clienteNubank.stream().map(ClienteCreditoNubank::getBancosCliente)
				.flatMap(List::stream).collect(Collectors.toList());

		List<ClienteCreditoOpenBank> porIntituicao = openBank.stream()
				.filter(o -> o.getCodigoInstitucao().compareTo(cliente.getCodigoInstitucao()) == 0).sorted(Comparator
						.comparing(ClienteCreditoOpenBank::getData, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());
		return porIntituicao;
	}

	private List<ClienteCreditoNubank> buscaAvaliacoesCliente(String cpf) {
		final ReadOnlyKeyValueStore<String, List<ClienteCreditoNubank>> clienteNubankStore = interactiveQueryService
				.getQueryableStore(store, QueryableStoreTypes.<String, List<ClienteCreditoNubank>>keyValueStore());

		return clienteNubankStore.get(cpf).stream().filter(Objects::nonNull).sorted(Comparator
				.comparing(ClienteCreditoNubank::getDataUltimaValicao, Comparator.nullsLast(Comparator.reverseOrder())))
				.collect(Collectors.toList());
	}

}
