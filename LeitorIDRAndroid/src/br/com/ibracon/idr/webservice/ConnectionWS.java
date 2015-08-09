package br.com.ibracon.idr.webservice;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import android.util.Log;
import br.com.ibracon.idr.model.Livro;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;
import br.com.ibracon.idr.webservice.registrarLivro.LivroRegistrado;
import br.com.ibracon.idr.webservice.registrarLivro.ResponseRegistrarLivro;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class ConnectionWS {

	String urlString = "http://www.ibracon.com.br/idr/ws/";
	//String urlString = "http://192.168.0.11:82/ws/";

	public static final String WS_REGISTRAR = "ws_registrar.php";
	public static final String WS_ESTANTES = "ws_estantes_mobile.php";
	public static final String WS_REGISTRAR_LIVRO = "ws_registrar_livro.php";
	
	public ResponseWS serviceConnect(RequestWS requestWS, String ws_servico)
			throws Exception {
		if (!ws_servico.equals(WS_REGISTRAR) && !ws_servico.equals(WS_ESTANTES)
				&& !ws_servico.equals(WS_REGISTRAR_LIVRO)) {
			throw new Exception("Não foi possível conectar no serviço: "
					+ ws_servico);
		}
		
		urlString += ws_servico;
		
		ResponseWS response = null;
		// os parametros a serem enviados
		Properties parameters = requestWS.getParameters();

		Iterator i = parameters.keySet().iterator();
		int counter = 0;
		while (i.hasNext()) {
			String name = (String) i.next();
			String value = parameters.getProperty(name);
			urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
		}
		
		// REALIZA CONEXAO
		Log.i("URL", urlString);

		//imprimeRespostaXml(urlString);  
		
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Request-Method", "GET");
		connection.setConnectTimeout(15000);
		connection.setDoInput(true);
		connection.setDoOutput(false);
		
		
		// IMPRIME RESPOSTA NO CONSOLE
		try{
		InputStream arquivoXML = connection.getInputStream();
		//IMPRIME O CODIGO DE RESPOSTA HTTP E MENSAGEM
		Log.i("WebService",  
			    "CODIGO DE RESPOSTA HTTP E MENSAGEM: "  
			        + connection.getResponseCode()  
			        + "/"  
			        + connection.getResponseMessage());  
		
		
		try {
			InputStreamReader reader = new InputStreamReader(arquivoXML,
					"ISO-8859-1");
			
			// Cria o objeto xstream
			XStream xStream = new XStream(new DomDriver());
			if (ws_servico.equals(WS_REGISTRAR)) {
				xStream.alias("response", ResponseRegistrar.class);
				response = (ResponseRegistrar) xStream.fromXML(reader);
			}
			if (ws_servico.equals(WS_ESTANTES)) {
				xStream.alias("response", ResponseEstante.class);
				xStream.alias("livro", Livro.class);
				response = (ResponseEstante) xStream.fromXML(reader);
			}
			if (ws_servico.equals(WS_REGISTRAR_LIVRO)) {
				xStream.alias("response", ResponseRegistrarLivro.class);
				xStream.alias("livro", LivroRegistrado.class);
				response = (ResponseRegistrarLivro) xStream.fromXML(reader);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		}catch(ConnectException conn){
			Log.i("Erro de conexão","Houve um problema de conexão com o servidor do Ibracon. Pode ser que você esteja sem conexão com a internet ou que o servidor esteja indisponível.");
		}

		return response;
	}

}
