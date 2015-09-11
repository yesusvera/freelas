package br.com.ibracon.idr.form.util;

import java.awt.Desktop;
import java.awt.Image;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.bo.ProxyBO;
import br.com.ibracon.idr.form.model.EnumSO;

public class IdrUtil {
	
	static Logger logger = Logger.getLogger(IdrUtil.class);
	
	
	public static EnumSO verificarSO() {
		String so = System.getProperty("os.name");
		// logger.debug(so);
		if (so != null) {
			if (so.toLowerCase().contains("windows")) {
				return EnumSO.WINDOWS;
			}
			if (so.toLowerCase().contains("mac")) {
				return EnumSO.MACOS;
			}

			if (so.toLowerCase().contains("linux")) {
				return EnumSO.LINUX;
			}
		}
		return EnumSO.NAOIDENTIFICADO;
	}

	public static void abrirBrowser(String url) {
		Desktop desktop = null;
		URI uri = null;
		try {
			desktop = Desktop.getDesktop();
			uri = new URI(url);
			desktop.browse(uri);
		} catch (URISyntaxException erroUri) {
			logger.debug("deu erro ao criar a url");
		} catch (IOException desktopErro) {
			System.out
					.println("deu erro ao abrir o navegador com o endereço informado");
		}
	}

	public static boolean internetEstaAtiva() {
		try {
			URL url = new URL("http://www.ibracon.com.br");
			HttpURLConnection urlConnect = (HttpURLConnection) url
					.openConnection();
			
			if(FormPrincipal.usarProxy){
				Properties prop = new ProxyBO().findProxyProperties();
				final String usuario = prop.getProperty("usuario");
				final String senha = prop.getProperty("senha");
				sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
				String encodedUserPwd = encoder.encode((usuario+":"+senha).getBytes());
				urlConnect.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
			}
				
			Object objData = urlConnect.getContent();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

		/*
		 * try { InetAddress inet = InetAddress.getByName("www.ibracon.com.br");
		 * int timeout = 3000;
		 * 
		 * return inet.isReachable(timeout); } catch (UnknownHostException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * return false;
		 */
	}

	/**
	 * @author yesus
	 * @param resource
	 * @return
	 */
	public static ImageIcon getImageIcon(String resource){
		logger.info("getImageIcon("+resource+")");
		Image img = null;
		try {
			img = ImageIO.read(FormPrincipal.class.getResourceAsStream(resource));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new ImageIcon(img);
	}
	
	public static void freeMemorySugested(){
		System.gc();
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().gc();
	}
	
	public static void main(String[] args) {
		logger.debug(verificarSO());
		logger.debug(System.getProperties());
	}
}