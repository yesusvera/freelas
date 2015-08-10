package br.com.ibracon.idr.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.bo.PropertiesBO;
import br.com.ibracon.idr.form.modal.JanelaProgresso;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;
import br.com.ibracon.idr.webservice.registrarLivro.ResponseRegistrarLivro;

public abstract class ConnectionWS {

	static Logger logger = Logger.getLogger(ConnectionWS.class);

	String urlString = "http://localhost/ws/";

	public static final String WS_REGISTRAR = "ws_registrar.php";
	public static final String WS_ESTANTES = "ws_estantes.php";
	public static final String WS_REGISTRAR_LIVRO = "ws_registrar_livro.php";

	public ConnectionWS() {
		ResourceBundle rb = PropertiesBO.findWebServicesProperties();
		if (rb.getString("urlServicos") != null
				&& !rb.getString("urlServicos").trim().equals("")) {
			urlString = rb.getString("urlServicos");
			logger.info("O Leitor irá se conectar com a url: " + urlString);
		}
	}

	public ResponseWS serviceConnect(RequestWS requestWS, String ws_servico)
			throws Exception {
		JanelaProgresso jp = new JanelaProgresso(FormPrincipal.formPrincipal);
		ResponseWS response = null;
		try {
			jp.aparecer();
			jp.aumentaPercentual(5);
			
			jp.setTexto("   Conectando-se ao serviço do Ibracon.   " );
			
			if (!ws_servico.equals(WS_REGISTRAR)
					&& !ws_servico.equals(WS_ESTANTES)
					&& !ws_servico.equals(WS_REGISTRAR_LIVRO)) {

				logger.debug("Não é possível conectar no serviço: "
						+ ws_servico);
				throw new Exception("Não é possível conectar no serviço: "
						+ ws_servico);

			}

			urlString += ws_servico;

			
			// os parametros a serem enviados
			Properties parameters = requestWS.getParameters();

			Iterator i = parameters.keySet().iterator();
			int counter = 0;

			jp.aumentaPercentual(5);
			while (i.hasNext()) {
				String name = (String) i.next();
				String value = parameters.getProperty(name);
				urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
			}

			jp.aumentaPercentual(5);
			jp.setTexto("   Parametros de requisição montados. Enviando ao Ibracon.   ");
			
			logger.info("   Parametros de requisição montados   ");
			// REALIZA CONEXAO
			logger.debug(urlString);

			// imprimeRespostaXml(urlString);

			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			jp.aumentaPercentual(5);
			logger.info("Request-Method" + "GET");

			connection.setRequestProperty("Request-Method", "GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);

			// IMPRIME RESPOSTA NO CONSOLE
			try {
				jp.aumentaPercentual(5);
				jp.setTexto("   Recebendo resposta do Ibracon. Aguarde um momento...   ");
				InputStream arquivoXML = connection.getInputStream();
				// IMPRIME O CODIGO DE RESPOSTA HTTP E MENSAGEM
				logger.debug("CODIGO DE RESPOSTA HTTP E MENSAGEM: "
						+ connection.getResponseCode() + "/"
						+ connection.getResponseMessage());

				jp.setTexto("   Processando resposta do Ibracon.   ");
				// MONTA OBJETO DE RESPOSTA
				try {
					jp.aumentaPercentual(5);
					InputStreamReader reader = new InputStreamReader(
							arquivoXML, "ISO-8859-1");

					// Cria o objeto xstream
					XStream xStream = new XStream(new DomDriver());
					if (ws_servico.equals(WS_REGISTRAR)) {
						xStream.processAnnotations(ResponseRegistrar.class);
						response = (ResponseRegistrar) xStream.fromXML(reader);
					}
					if (ws_servico.equals(WS_ESTANTES)) {
						xStream.processAnnotations(ResponseEstante.class);
						response = (ResponseEstante) xStream.fromXML(reader);
					}
					if (ws_servico.equals(WS_REGISTRAR_LIVRO)) {
						xStream.processAnnotations(ResponseRegistrarLivro.class);
						response = (ResponseRegistrarLivro) xStream
								.fromXML(reader);
					}

				} catch (UnsupportedEncodingException e) {
					logger.error(e);
					e.printStackTrace();
					jp.encerrar();
				}
			} catch (ConnectException conn) {
				logger.error(conn);
				jp.encerrar();
				JOptionPane
						.showMessageDialog(
								null,
								"Houve um problema de conexão com o servidor do Ibracon. Pode ser que você esteja sem conexão com a internet ou que o servidor esteja indisponível.");
			}

		} catch (Exception e) {
			jp.encerrar();
			throw e;
		}
		
		jp.encerrar();
		return response;
	}

	private HttpURLConnection imprimeRespostaXml(String urlString)
			throws IOException {

		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream arquivoXML = connection.getInputStream();
		connection.setRequestProperty("Request-Method", "GET");
		connection.setDoInput(true);
		connection.setDoOutput(false);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(arquivoXML));

		String s = "";
		while (null != ((s = br.readLine()))) {
			logger.debug(s);
		}
		br.close();
		return connection;
	}

}
