package br.com.nubank.openBankCliente.service;

import br.com.nubank.openBankCliente.dto.ClienteCreditoOpenBank;

public interface ClienteOpenBankService {

	void processar(String cpf, ClienteCreditoOpenBank cliente);

}
