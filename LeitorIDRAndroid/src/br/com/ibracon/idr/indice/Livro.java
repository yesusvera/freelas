package br.com.ibracon.idr.indice;
import java.io.Serializable;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("livro")
public class Livro implements Serializable{
	@XStreamAlias("indice")
	public ArrayList<Item> indice = new ArrayList<Item>();

	public Livro() {
	}
	
	public ArrayList<Item> getListaItens() {
		return indice;
	}
}
