package br.com.nubank.openBankCliente.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteCreditoNubank {
	private String cpf;
	private List<ClienteCreditoOpenBank> bancosCliente;
	private LocalDateTime dataUltimaValicao;
	private boolean houveReducao;
	private boolean houveAumento;
	private boolean naoHouveAlteracao;

}
