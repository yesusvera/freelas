package br.com.ibracon.idr.webservice.estante;

import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;

public class TesteConnectionEstante {
	public static void main(String[] args) {

		RequestEstante requestEstante = new RequestEstante();
		requestEstante.setCliente("1");
		requestEstante.setDocumento("01.010.010/0001-01");
		requestEstante.setDispositivo("1");
		requestEstante.setKeyword("123");
		requestEstante.setSenha("123");

		ConnectionEstante connectionEstante = new ConnectionEstante();
		try {
			ResponseWS resp = connectionEstante.serviceConnect(requestEstante,
					ConnectionWS.WS_ESTANTES);
			ResponseEstante responseEstante = (ResponseEstante) resp;
			System.out.println(responseEstante);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
