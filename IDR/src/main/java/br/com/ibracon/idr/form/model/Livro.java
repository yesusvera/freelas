package br.com.ibracon.idr.form.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("livro")
public class Livro {
	private String codigolivro;
	private String titulo;
	private String versao;
	private String codigoloja;
	private String foto;
	private String arquivo;

	private boolean baixado;
	
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
	
	public boolean isBaixado() {
		return baixado;
	}
	public void setBaixado(boolean baixado) {
		this.baixado = baixado;
	}
	public String getNomeArquivoBaixado() {
		String nomeArquivo = this.getArquivo().substring(this.getArquivo().lastIndexOf("/"));
		return nomeArquivo;
	}
	
	
	
	@Override
	public String toString() {
		return "Livro [codigolivro=" + codigolivro + ", titulo=" + titulo
				+ ", versao=" + versao + ", codigoloja=" + codigoloja
				+ ", foto=" + foto + ", arquivo=" + arquivo + "]";
	}
}
