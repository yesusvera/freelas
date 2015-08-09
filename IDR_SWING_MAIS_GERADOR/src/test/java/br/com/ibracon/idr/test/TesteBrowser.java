package br.com.ibracon.idr.test;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class TesteBrowser {
	static Logger logger = Logger.getLogger(TesteBrowser.class);
	
	public static void main(String[] args) {
		Desktop desktop = null;
		URI uri = null;
		try {
			desktop = Desktop.getDesktop();
			uri = new URI("http://www.google.com");
			desktop.browse(uri);
		} catch (URISyntaxException erroUri) {
			logger.debug("deu erro ao criar a url, acho que ela está errada");
		} catch (IOException desktopErro) {
			logger.debug("deu erro ao abrir o navegador com o endereço informado");
		}
	}

}
