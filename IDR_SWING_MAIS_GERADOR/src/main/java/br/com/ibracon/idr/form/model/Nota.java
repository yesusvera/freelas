package br.com.ibracon.idr.form.model;

import java.util.Date;

public class Nota {
	LivroIDR livroIBR;
	int pagina;
	String texto;
	String titulo;
	Date dataCriacao;
	Date dataModificacao;

	public Nota(LivroIDR livroIBR, int pagina, String texto, Date dataCriacao,
			String titulo, Date dataModificacao) {
		super();
		this.livroIBR = livroIBR;
		this.pagina = pagina;
		this.texto = texto;
		this.dataCriacao = dataCriacao;
		this.dataModificacao = dataModificacao;
		this.titulo = titulo;
	}
	
	public Nota(){
		
	}

	public LivroIDR getLivroIBR() {
		return livroIBR;
	}

	public void setLivroIBR(LivroIDR livroIBR) {
		this.livroIBR = livroIBR;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataModificacao() {
		return dataModificacao;
	}

	public void setDataModificacao(Date dataModificacao) {
		this.dataModificacao = dataModificacao;
	}

}
