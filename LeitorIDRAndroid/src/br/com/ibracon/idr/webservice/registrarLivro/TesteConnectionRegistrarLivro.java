package br.com.ibracon.idr.webservice.registrarLivro;

import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;

public class TesteConnectionRegistrarLivro {
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
			System.out.println(responseRegistrar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
