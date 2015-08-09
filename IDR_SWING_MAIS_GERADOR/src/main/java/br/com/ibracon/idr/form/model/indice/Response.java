package br.com.ibracon.idr.form.model.indice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

@XStreamAlias("response")
public class Response {
	@XStreamAlias("livro")
	public Livro livro = new Livro();

	public String erro;
	public String msgErro;

	public Response() {
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public String getMsgErro() {
		return msgErro;
	}

	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}

	public Response montaResposta(byte[] byteBuff) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new ByteArrayInputStream(byteBuff), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		XStream xStream = new XStream(new DomDriver());
		xStream.processAnnotations(Response.class);
	
		//*******PARA TRATAR O TIPO DE ÍNDICE ANTIGO E O NOVO*******
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		StringBuffer xmlIndiceBuffer = new StringBuffer();
		String tmp;
		try {
			while((tmp = bufferedReader.readLine())!=null){
				xmlIndiceBuffer.append(tmp);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		String xmlIndice = xmlIndiceBuffer.toString(); 
				
		if(xmlIndice!=null && xmlIndice.length() > 0){
			xmlIndice = xmlIndice.replace("<ParteA>", "<item>"
															+ "<id>0</id>"
															+ "<capitulo>Parte A</capitulo>"
															+ "<paginavirtual></paginavirtual>"
															+ "<paginareal></paginareal>"
															+ "<pai>0</pai>"
															+ "<itens>")
								.replace("</ParteA>", "</itens></item>")
								.replace("<ParteB>", "<item>"
															+ "<id>0</id>"
															+ "<capitulo>Parte B</capitulo>"
															+ "<paginavirtual></paginavirtual>"
															+ "<paginareal></paginareal>"
															+ "<pai>0</pai>"
															+ "<itens>")
								.replace("</ParteB>", "</itens></item>");
			
			//**********************************************************
			
			Response response = (Response) xStream.fromXML(xmlIndice);
	
			return response;
		
		}else{
			return null;
		}
	}

	public static void main(String[] args) {
//		Response response = new Response()
//				.montaResposta(new File(
//						"/Users/yesus/Documents/Trabalho/CODETEC/final/INDICE2013.xml"));

//		String str = new String();
//		for (Item item : response.getLivro().getIndice().getParteA()) {
//			str+=item.getCapitulo() + "\n";
//		}
//		
//		JOptionPane.showMessageDialog(null, str);
	}

}
