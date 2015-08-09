package br.com.ibracon.idr.form.bo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.form.model.EstanteXML;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.estante.ConnectionEstante;
import br.com.ibracon.idr.webservice.estante.RequestEstante;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class EstantesBO {
	
	static Logger logger = Logger.getLogger(EstantesBO.class);
	
	private static final String RESPONSE_ESTANTE_CRIPT = "responseEstanteXML.cript";
	private FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);

	private InstalacaoBO instalacaoBO = new InstalacaoBO();

	public void conectarEstante(FormPrincipal formPrincipal,
			String palavraChave, String senha) {
		logger.info("Conectando com a estante online");
		RequestEstante requestEstante = new RequestEstante();
		requestEstante.setCliente(formPrincipal.registroXML
				.getResponseRegistrar().getCodCliente());
		requestEstante.setDocumento(formPrincipal.registroXML
				.getRequestRegistrar().getDocumento());
		requestEstante.setDispositivo(formPrincipal.registroXML
				.getResponseRegistrar().getCodDispositivo());
		requestEstante.setKeyword(palavraChave);
		requestEstante.setSenha(senha);

		ConnectionEstante connectionEstante = new ConnectionEstante();
		try {
			ResponseWS resp = connectionEstante.serviceConnect(requestEstante,
					ConnectionWS.WS_ESTANTES);
			ResponseEstante responseEstante = (ResponseEstante) resp;

			if (!responseEstante.getErro().equals("0")) {
				JOptionPane.showMessageDialog(formPrincipal,
						responseEstante.getMsgErro());
			} else {
				EstanteXML estanteXML = new EstanteXML();
				estanteXML.setRequestEstante(requestEstante);
				estanteXML.setResponseEstante(responseEstante);
				formPrincipal.estanteXML = estanteXML;
				formPrincipal.responseEstanteXML = responseEstante;
				
				//gravaEstanteEmDisco(responseEstante);
			}

		} catch (Exception exception) {
			logger.error(exception);
			exception.printStackTrace();
		}

	}

	public ResponseEstante pegaEstanteEmDisco() {
		logger.info("Pegando a estante em disco");
		File fileXMLCripto = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + RESPONSE_ESTANTE_CRIPT);

		if (!fileXMLCripto.exists()) {
			return null;
		}

		File fileXmlDescript = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + RESPONSE_ESTANTE_CRIPT + ".xml");

		try {
			logger.info("Descriptografando o xml do disco");
			cripto.descriptografa(new FileInputStream(fileXMLCripto),
					new FileOutputStream(fileXmlDescript));

			if (fileXmlDescript.exists()) {
				// MONTA OBJETO
				XStream xStream = new XStream(new DomDriver());
				xStream.processAnnotations(ResponseEstante.class);
				ResponseEstante responseEstanteXML = (ResponseEstante) xStream
						.fromXML(new FileInputStream(fileXmlDescript));

				// DELETA O DESCRIPTOGRAFADO
				fileXmlDescript.delete();

				return responseEstanteXML;
			}
		} catch (InvalidKeyException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		}

		return null;
	}

	public void gravaEstanteEmDisco(ResponseEstante responseEstanteXML)
			throws IOException {

		logger.info("Gravando estante em disco para uso offline");
		
		
		// PEGA O XML
		XStream xstream = new XStream();
		xstream.alias("response", ResponseEstante.class);
		String strRegistroXML = xstream.toXML(responseEstanteXML);

		// GRAVA O XML REGISTRO EM DISCO
		File fileXML = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + "reponseEstante.xml");
		if (fileXML.exists()) {
			fileXML.delete();
		}
		fileXML.createNewFile();

		FileWriter fw = new FileWriter(fileXML);
		BufferedWriter bf = new BufferedWriter(fw);

		bf.write(strRegistroXML);
		bf.close();
		fw.close();

		// CRIPTOGRAFA O REGISTRO
		File fileXMLCripto = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + RESPONSE_ESTANTE_CRIPT);

		try {
			logger.info("Criptografando o xml de retorno.");
			cripto.criptografa(new FileInputStream(fileXML),
					new FileOutputStream(fileXMLCripto));
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		// DELETA O ARQUIVO XML
		logger.info("exclulindo o arquivo xml");
		fileXML.delete();

	}
}
