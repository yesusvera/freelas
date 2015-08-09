package br.com.iejb.sgi.util;

import java.util.UUID;

/**
 * classe responsável por gerar senhas.
 * @author yvera - YESUS CASTILLO VERA
 *
 */
public class GerarSenha {

	public String gerarSenha(int qtdeCaracteres){
		UUID uuid = UUID.randomUUID();    
		String myRandom = uuid.toString();    
		return myRandom.substring(0,qtdeCaracteres);  
	}
}
