package br.com.ibracon.idr.webservice.estante;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;

public class TesteConnectionEstante {
	static Logger logger = Logger.getLogger(TesteConnectionEstante.class);
	
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
			logger.debug(responseEstante);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
