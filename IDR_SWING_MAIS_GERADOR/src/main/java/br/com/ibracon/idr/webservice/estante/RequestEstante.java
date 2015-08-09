package br.com.ibracon.idr.webservice.estante;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import br.com.ibracon.idr.webservice.RequestWS;

public class RequestEstante extends RequestWS {

	private String cliente;
	private String documento;
	private String dispositivo;
	private String keyword;
	private String senha;

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getDispositivo() {
		return dispositivo;
	}

	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public Properties getParameters() {
		Properties parameters = new Properties();
		try {
			parameters.setProperty("cliente",
					URLEncoder.encode(cliente, "ISO-8859-1"));
			parameters.setProperty("documento",
					URLEncoder.encode(documento, "ISO-8859-1"));
			parameters.setProperty("dispositivo",
					URLEncoder.encode(dispositivo, "ISO-8859-1"));
			parameters.setProperty("keyword",
					URLEncoder.encode(keyword, "ISO-8859-1"));
			parameters.setProperty("senha",
					URLEncoder.encode(senha, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return parameters;
	}

}
