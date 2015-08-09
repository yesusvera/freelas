package br.com.ibracon.idr.model;

import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="registroXML")
public class RegistroXml {
	RequestRegistrar requestRegistrar;
	ResponseRegistrar responseRegistrar;
	
	public RequestRegistrar getRequestRegistrar() {
		return requestRegistrar;
	}
	public void setRequestRegistrar(RequestRegistrar requestRegistrar) {
		this.requestRegistrar = requestRegistrar;
	}
	public ResponseRegistrar getResponseRegistrar() {
		return responseRegistrar;
	}
	public void setResponseRegistrar(ResponseRegistrar responseRegistrar) {
		this.responseRegistrar = responseRegistrar;
	}
	
}
