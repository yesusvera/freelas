package br.com.ibracon.idr.indice;

import java.io.Serializable;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("indice")
public class Indice implements Serializable{

	public Indice() {
	}
	
	@XStreamAlias("ParteA")
	public ArrayList<Item> ParteA = new ArrayList<Item>();

	@XStreamAlias("ParteB")
	public ArrayList<Item> ParteB = new ArrayList<Item>();

	
}
