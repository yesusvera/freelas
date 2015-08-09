package br.com.ibracon.idr.webservice.registrarLivro;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;

public class TesteConnectionRegistrarLivro {
	static Logger logger = Logger.getLogger(TesteConnectionRegistrarLivro.class);
	
	
	public static void main(String[] args) {
		
		RequestRegistrarLivro requestRegistrarLivro = new RequestRegistrarLivro();
		requestRegistrarLivro.setCliente("1");
		requestRegistrarLivro.setDocumento("01.010.010/0001-01");
		requestRegistrarLivro.setDispositivo("6");
		requestRegistrarLivro.setKeyworkd("123");
		requestRegistrarLivro.setProduto("1");
		requestRegistrarLivro.setSenha("123");
		ConnectionRegistrarLivro connectionRegistrar = new ConnectionRegistrarLivro();
		try {
			ResponseWS resp = connectionRegistrar.serviceConnect(requestRegistrarLivro, ConnectionWS.WS_REGISTRAR_LIVRO);
			ResponseRegistrarLivro responseRegistrar = (ResponseRegistrarLivro)resp;
			logger.debug(responseRegistrar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
