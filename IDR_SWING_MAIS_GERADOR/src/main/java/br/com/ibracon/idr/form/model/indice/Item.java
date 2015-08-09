package br.com.ibracon.idr.form.model.indice;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("item")
public class Item {
	private String id;
	private String capitulo;
	private String paginavirtual;
	private String paginareal;
	private String parte;
	private String pai;

	@XStreamAlias(value = "itens")
	public ArrayList<Item> itens = new ArrayList<Item>();

	public Item() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCapitulo() {
		return capitulo;
	}

	public void setCapitulo(String capitulo) {
		this.capitulo = capitulo;
	}

	public String getPaginavirtual() {
		return paginavirtual;
	}

	public void setPaginavirtual(String paginavirtual) {
		this.paginavirtual = paginavirtual;
	}

	public String getPaginareal() {
		return paginareal;
	}

	public void setPaginareal(String paginareal) {
		this.paginareal = paginareal;
	}

	public String getParte() {
		return parte;
	}

	public void setParte(String parte) {
		this.parte = parte;
	}

	public String getPai() {
		return pai;
	}

	public void setPai(String pai) {
		this.pai = pai;
	}

	public ArrayList<Item> getItens() {
		return itens;
	}

	public void setItens(ArrayList<Item> itens) {
		this.itens = itens;
	}

	@Override
	public String toString() {
		String texto = capitulo;
		if(paginavirtual!=null && paginavirtual.length()>0){
			texto += " (" + paginavirtual + ")";
		}
		return texto;
	}

}
