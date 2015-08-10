package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.criptografia.CriptografiaUtil;

public class ProxyBO {
	static Logger logger = Logger.getLogger(ProxyBO.class);

	InstalacaoBO instalacaoBO = new InstalacaoBO();

	public static ResourceBundle findProperties(String arg) {
		ResourceBundle resource = ResourceBundle.getBundle(arg);
		return resource;
	}

	public Properties findProxyProperties() {

		logger.info("Capturando properties de configuração de proxy");
		
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

	public static ResourceBundle findSOProperties() {
		return findProperties("br.com.ibracon.idr.form.configuracoes.so");
	}

	public void salvarProxyProperties(Properties properties) {
		logger.info("Salvando configurações de proxy");
		try {
			String dirProxyProperties = instalacaoBO.getDiretorioInstalacao()
					+ File.separator + "proxy.properties";
			logger.debug("Salvando configuração de proxy em : "
					+ dirProxyProperties);
			properties.store(new FileOutputStream(dirProxyProperties),
					"Alterando configuracoes");
		} catch (FileNotFoundException e1) {
			logger.error(e1);
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
	}

	public boolean internetAtiva() {
		try {
			java.net.URL mandarMail = new java.net.URL("http://www.google.com.br");
			java.net.URLConnection conn = mandarMail.openConnection();

			java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) conn;
			httpConn.connect();
			int x = httpConn.getResponseCode();
			if (x == 200) {
				return true;
			}
		} catch (java.net.MalformedURLException urlmal) {
		} catch (java.io.IOException ioexcp) {
		} 
		
		return false;
	}

	public void configurarProxy() {
		logger.info("Configurando proxy no sistema operacional");
		
		Properties prop = findProxyProperties();
		final String usuario = prop.getProperty("usuario");
		final String senha = prop.getProperty("senha");
		final String porta = prop.getProperty("porta");
		final String host = prop.getProperty("proxy");
		// final String nonProxyHosts = prop.getProperty("ignorar");
		// System.setProperty("http.nonProxyHosts", nonProxyHosts);

		System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort",porta);

		// Usuario e senha
		System.setProperty("http.proxyUser", "teste");
		System.setProperty("http.proxyPassword", "teste");
	}
	
	 

	public void dialogConfigurarProxy(FormPrincipal formPrincipal) throws InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnsupportedEncodingException, InvalidKeySpecException {
		logger.info("Perguntando se deseja configurar proxy.");
		if (!internetAtiva()) {
			if (JOptionPane
					.showConfirmDialog(
							formPrincipal,
							"Você está sem conexão com a internet. Sua rede possui proxy?",
							"Leitor IDR - Falha de autenticação",
							JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
				
				JTextField proxyField = new JTextField();
				JTextField portaField = new JTextField();
				JTextField usuarioField = new JTextField();
				JPasswordField senhaField = new JPasswordField();
				
				if (JOptionPane.showConfirmDialog(formPrincipal,
						new Object[] { 
						new JLabel("Proxy:"), proxyField,
						new JLabel("Porta"), portaField, 
						new JLabel("Usuário:"), usuarioField,
						new JLabel("Senha"), senhaField 
								
									},
						"Leitor IDR - Autenticação de Proxy", JOptionPane.OK_CANCEL_OPTION) == 0) {
					
					Properties properties = new Properties();
					
					
					
					properties.setProperty("proxy", proxyField.getText());
					properties.setProperty("porta",  portaField.getText());
					properties.setProperty("usuario", CriptografiaUtil.encrypt(usuarioField.getText(), "IBRACON", CriptografiaUtil.ALGORITMO_DES));
					properties.setProperty("senha",  CriptografiaUtil.encrypt(senhaField.getText(), "IBRACON", CriptografiaUtil.ALGORITMO_DES));

					salvarProxyProperties(properties);
					
					configurarProxy();

				} else {
					logger.info("Autenticação inválida. Tente novamente");
					JOptionPane.showMessageDialog(null,
							"Autenticação inválida. Tente novamente");
				}

				

			}
		}
	}
}
