package br.com.ibracon.idr.model;

import java.io.Serializable;


public class Nota implements Serializable{
	
	private static final long serialVersionUID = 6036420201224963737L;
	
	private String codigoLivro = "";
	private String pagina ="";
	private String titulo ="";
	private String nota ="";
	
	public String getCodigoLivro() {
		return codigoLivro;
	}
	public void setCodigoLivro(String codigoLivro) {
		this.codigoLivro = codigoLivro;
	}
	public String getPagina() {
		return pagina;
	}
	public void setPagina(String pagina) {
		this.pagina = pagina;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
}
