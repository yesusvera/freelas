package br.com.ibracon.idr.form.model.indice;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("livro")
public class Livro {
	@XStreamAlias("indice")
	public ArrayList<Item> listaItens = new ArrayList<Item>();

	public Livro() {
	}
	public ArrayList<Item> getListaItens() {
		return listaItens;
	}

	public void setListaItens(ArrayList<Item> listaItens) {
		this.listaItens = listaItens;
	}
	
}
