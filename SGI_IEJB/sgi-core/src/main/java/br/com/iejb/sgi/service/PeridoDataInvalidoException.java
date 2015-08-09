package br.com.iejb.sgi.service;

import java.util.Date;

import br.com.iejb.sgi.exception.NegocioSisdepenException;

public class PeridoDataInvalidoException extends NegocioSisdepenException {

	private static final long serialVersionUID = 1L;

	public PeridoDataInvalidoException(String mensagem) {
		super(mensagem);
	}
	
	public PeridoDataInvalidoException(String mensagem, Date dataInicial, Date  dataFinal) {
		super(mensagem);
	}
	 

}
