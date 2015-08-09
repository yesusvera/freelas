package br.com.ibracon.idr.form.model;

import java.util.ArrayList;

public class ItemResultado {
	private int pagina;
	private String texto;
	
	private ArrayList<Byte> ocorrenciasPerc = new ArrayList<>();

	public ItemResultado(int pagina, String texto) {
		this.pagina = pagina;
		this.texto = texto;
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
	public ArrayList<Byte> getOcorrenciasPerc() {
		return ocorrenciasPerc;
	}
	public void setOcorrenciasPerc(ArrayList<Byte> ocorrenciasPerc) {
		this.ocorrenciasPerc = ocorrenciasPerc;
	}
	@Override
	public String toString() {
		return "Página("+ocorrenciasPerc+" (" + pagina + ") - " + texto;
	}

}
