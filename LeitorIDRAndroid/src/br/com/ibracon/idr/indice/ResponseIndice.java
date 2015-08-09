package br.com.ibracon.idr.indice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

@XStreamAlias("response")
public class ResponseIndice implements Serializable{
	@XStreamAlias("livro")
	public Livro livro = new Livro();

	public String erro;
	public String msgErro;

	public ResponseIndice() {
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

	public static ResponseIndice montaResposta(String pathIndice) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File(pathIndice)), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Cria o objeto xstream
		XStream xStream = new XStream(new DomDriver());
		
		xStream.alias("response", ResponseIndice.class);
		xStream.alias("livro", Livro.class);
		xStream.alias("indice", ArrayList.class);
		xStream.alias("item", Item.class);
		
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		//*******PARA TRATAR O TIPO DE ÍNDICE ANTIGO E O NOVO*******
		
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
			
			ResponseIndice response = (ResponseIndice) xStream.fromXML(xmlIndice);
	
			return response;
		
		}else{
			return null;
		}
	}
}
