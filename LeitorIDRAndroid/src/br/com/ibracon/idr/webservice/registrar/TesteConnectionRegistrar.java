package br.com.ibracon.idr.webservice.registrar;

import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;

public class TesteConnectionRegistrar {
	public static void main(String[] args) {
		
		RequestRegistrar requestRegistrar = new RequestRegistrar();
		requestRegistrar.setBairro("Guara II");
		requestRegistrar.setCep("72345098104");
		requestRegistrar.setCidade("Brasília");
		requestRegistrar.setCliente("Yesus Alfonsino Castillo vera");
		requestRegistrar.setComplemento("Próximo ao Tokio");
		requestRegistrar.setEmail("yesusvera@gmail.com");
		requestRegistrar.setEndereco("QE 42 conjunto e casa 17");
		requestRegistrar.setIp("192.168.1.1");
		requestRegistrar.setMacadress("23:234:235:45");
		requestRegistrar.setNumero("234");
		requestRegistrar.setRegistro("RN0001");
		requestRegistrar.setSenha("");
		requestRegistrar.setSerial("234234");
		requestRegistrar.setUf("DF");
		requestRegistrar.setDocumento("01.010.010/0001-01");
		
		ConnectionRegistrar connectionRegistrar = new ConnectionRegistrar();
		try {
			ResponseWS resp = connectionRegistrar.serviceConnect(requestRegistrar, ConnectionWS.WS_REGISTRAR);
			ResponseRegistrar responseRegistrar = (ResponseRegistrar)resp;
			System.out.println(responseRegistrar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
