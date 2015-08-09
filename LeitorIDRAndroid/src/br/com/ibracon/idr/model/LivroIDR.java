package br.com.ibracon.idr.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.util.ZipUtils;

public class LivroIDR implements Serializable {
	
	private static final long serialVersionUID = 3429018740055565031L;
	
	public static final String INDICE_XML = "indice.xml";
	public static final String FOTO_PNG = "foto.png";
	public static final String LIVRO_PDF = "livro.pdf";

	private String codigoLivro;
	private String titulo;
	private String versao;
	private String codigoLoja;
	
	private byte[] fotoByteArray;
	private byte[] indiceByteArray;
	private byte[] pdfByteArray;
	
	
	public LivroIDR(){
		
	}
	
	public LivroIDR(File arquivoLivroIDR) 
			throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException {
		// DESCRIPTOGRAFA O IDR PARA ZIP
		FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);
		byte[] arrayBytesDescriptografado = cripto.getArrayBytesDescriptografado(new FileInputStream(arquivoLivroIDR));

		// DESCOMPACTA O ARQUIVO
		ZipUtils.extractArrayBytes(arrayBytesDescriptografado, this);
	}

	public String getCodigoLivro() {
		return codigoLivro;
	}

	public void setCodigoLivro(String codigoLivro) {
		this.codigoLivro = codigoLivro;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getCodigoLoja() {
		return codigoLoja;
	}

	public void setCodigoLoja(String codigoLoja) {
		this.codigoLoja = codigoLoja;
	}

	public byte[] getFotoByteArray() {
		return fotoByteArray;
	}

	public void setFotoByteArray(byte[] fotoByteArray) {
		this.fotoByteArray = fotoByteArray;
	}

	public byte[] getIndiceByteArray() {
		return indiceByteArray;
	}

	public void setIndiceByteArray(byte[] indiceByteArray) {
		this.indiceByteArray = indiceByteArray;
	}

	public byte[] getPdfByteArray() {
		return pdfByteArray;
	}

	public void setPdfByteArray(byte[] pdfByteArray) {
		this.pdfByteArray = pdfByteArray;
	}
}