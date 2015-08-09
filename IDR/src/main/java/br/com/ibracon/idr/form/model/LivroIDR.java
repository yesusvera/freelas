package br.com.ibracon.idr.form.model;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class LivroIDR {

	static Logger logger = Logger.getLogger(LivroIDR.class);
	
	
	private String codigoLivro;
	private String titulo;
	private String versao;
	private String codigoLoja;

	private File fotoFile;
	private File indiceXmlFile;
	private File pdfFile;

	private InputStream fotoFileInputStream;
	private InputStream indiceXmlFileInputStream;
	private InputStream pdfFileInputStream;

	private byte[] fotoByteArray;
	private byte[] indiceByteArray;
	private byte[] pdfByteArray;
	private byte[] indexacaoZipByteArray;


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

	public File getFotoFile() {
		return fotoFile;
	}

	public void setFotoFile(File fotoFile) {
		this.fotoFile = fotoFile;
	}

	public File getIndiceXmlFile() {
		return indiceXmlFile;
	}

	public void setIndiceXmlFile(File indiceXmlFile) {
		this.indiceXmlFile = indiceXmlFile;
	}

	public File getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(File pdfFile) {
		this.pdfFile = pdfFile;
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

	public InputStream getFotoFileInputStream() {
		return fotoFileInputStream;
	}

	public void setFotoFileInputStream(InputStream fotoFileInputStream) {
		this.fotoFileInputStream = fotoFileInputStream;
	}

	public InputStream getIndiceXmlFileInputStream() {
		return indiceXmlFileInputStream;
	}

	public void setIndiceXmlFileInputStream(InputStream indiceXmlFileInputStream) {
		this.indiceXmlFileInputStream = indiceXmlFileInputStream;
	}

	public InputStream getPdfFileInputStream() {
		return pdfFileInputStream;
	}

	public void setPdfFileInputStream(InputStream pdfFileInputStream) {
		this.pdfFileInputStream = pdfFileInputStream;
	}

	public byte[] getIndexacaoZipByteArray() {
		return indexacaoZipByteArray;
	}

	public void setIndexacaoZipByteArray(byte[] indexacaoZipByteArray) {
		this.indexacaoZipByteArray = indexacaoZipByteArray;
	}
}