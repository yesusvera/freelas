package br.com.iejb.sgi.service;

import java.util.Date;

import br.com.iejb.sgi.exception.NegocioSisdepenException;

public class IdadeInvalidaException extends NegocioSisdepenException {

	private static final long serialVersionUID = 1L;

	public IdadeInvalidaException(String mensagem) {
		super(mensagem);
	}
	
	public IdadeInvalidaException(String mensagem, Date dataNascimento) {
		super(mensagem);
	}
	 

}
