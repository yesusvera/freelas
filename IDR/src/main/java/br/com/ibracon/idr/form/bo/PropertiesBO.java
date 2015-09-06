package br.com.ibracon.idr.form.bo;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class PropertiesBO {
	static Logger logger = Logger.getLogger(PropertiesBO.class);
	
	InstalacaoBO instalacaoBO = new InstalacaoBO();
	
	public static ResourceBundle findProperties(String arg) {
		ResourceBundle resource = ResourceBundle.getBundle(arg);
		return resource;
	}

	public static ResourceBundle findWebServicesProperties() {
		return findProperties("br.com.ibracon.idr.form.configuracoes.webservices");
	}
}
