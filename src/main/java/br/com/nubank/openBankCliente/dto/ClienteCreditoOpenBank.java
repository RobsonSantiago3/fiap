package br.com.nubank.openBankCliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteCreditoOpenBank {

	private String cpf;
	private Long codigoInstitucao;
	private BigDecimal valorCredito;
	private LocalDateTime data;

}
