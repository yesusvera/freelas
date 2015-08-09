package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class PropertiesBO {
	static Logger logger = Logger.getLogger(PropertiesBO.class);
	
	InstalacaoBO instalacaoBO = new InstalacaoBO();
	
	public static ResourceBundle findProperties(String arg) {
		ResourceBundle resource = ResourceBundle.getBundle(arg);
		return resource;
	}

	public Properties findProxyProperties() {
	
		FileInputStream input;
		try {
			input = new FileInputStream(instalacaoBO.getDiretorioInstalacao()
					+ File.separator + "proxy.properties");
			Properties prop = new Properties();
			prop.load(input);
			return prop;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Properties();
	}

	public static ResourceBundle findWebServicesProperties() {
		return findProperties("br.com.ibracon.idr.form.configuracoes.webservices");
	}

	public void saveProxyProperties(Properties properties) {
		try {
			String dirProxyProperties = instalacaoBO.getDiretorioInstalacao()
					+ File.separator + "proxy.properties";
			logger.debug("Salvando configuração de proxy em : " + dirProxyProperties);
			properties.store(new FileOutputStream(dirProxyProperties),
					"Alterando configuracoes");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
