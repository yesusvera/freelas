package br.com.ibracon.idr.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("livro")
public class Livro implements Serializable{
	private static final long serialVersionUID = 1329512016472840593L;
	private String codigolivro;
	private String titulo;
	private String versao;
	private String codigoloja;
	private String foto;
	private String arquivo;
	
	private String arquivomobile;
    private String indiceXML;
	
	public String getCodigolivro() {
		return codigolivro;
	}
	public void setCodigolivro(String codigolivro) {
		this.codigolivro = codigolivro;
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
	public String getCodigoloja() {
		return codigoloja;
	}
	public void setCodigoloja(String codigoloja) {
		this.codigoloja = codigoloja;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public String getArquivo() {
		return arquivo;
	}
	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
	
	public String getArquivomobile() {
		return arquivomobile;
	}
	public void setArquivomobile(String arquivomobile) {
		this.arquivomobile = arquivomobile;
	}
	
	public String getIndiceXML() {
		return indiceXML;
	}
	public void setIndiceXML(String indiceXML) {
		this.indiceXML = indiceXML;
	}
	@Override
	public String toString() {
		return "Livro [codigolivro=" + codigolivro + ", titulo=" + titulo
				+ ", versao=" + versao + ", codigoloja=" + codigoloja
				+ ", foto=" + foto + ", arquivo=" + arquivo +"]";
	}
}
