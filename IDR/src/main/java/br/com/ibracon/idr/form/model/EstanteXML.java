package br.com.ibracon.idr.form.model;

import br.com.ibracon.idr.webservice.estante.RequestEstante;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;

public class EstanteXML {
	RequestEstante requestEstante;
	ResponseEstante responseEstante;
	public RequestEstante getRequestEstante() {
		return requestEstante;
	}
	public void setRequestEstante(RequestEstante requestEstante) {
		this.requestEstante = requestEstante;
	}
	public ResponseEstante getResponseEstante() {
		return responseEstante;
	}
	public void setResponseEstante(ResponseEstante responseEstante) {
		this.responseEstante = responseEstante;
	}
}
