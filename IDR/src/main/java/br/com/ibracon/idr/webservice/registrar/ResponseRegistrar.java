package br.com.ibracon.idr.webservice.registrar;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import br.com.ibracon.idr.webservice.ResponseWS;

@XStreamAlias("response")
public class ResponseRegistrar extends ResponseWS {
	
	private String codCliente;
	private String codDispositivo;
	private String status;
	private String urlApp;
	
	public String getCodCliente() {
		return codCliente;
	}
	public void setCodCliente(String codCliente) {
		this.codCliente = codCliente;
	}
	
	public String getCodDispositivo() {
		return codDispositivo;
	}
	public void setCodDispositivo(String codDispositivo) {
		this.codDispositivo = codDispositivo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUrlApp() {
		return urlApp;
	}
	public void setUrlApp(String urlApp) {
		this.urlApp = urlApp;

	}
	@Override
	public String toString() {
		return "ResponseRegistrar [codCliente=" + codCliente
				+ ", codDispositivo=" + codDispositivo + ", status=" + status
				+ ", urlApp=" + urlApp + "]";
	}
}
