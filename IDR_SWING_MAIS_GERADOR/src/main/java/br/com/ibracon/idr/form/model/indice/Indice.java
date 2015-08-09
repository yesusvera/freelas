package br.com.ibracon.idr.form.model.indice;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("indice")
public class Indice {

	public Indice() {
	}
	
	@XStreamAlias("ParteA")
	public ArrayList<Item> parteA = new ArrayList<Item>();

	@XStreamAlias("ParteB")
	public ArrayList<Item> parteB = new ArrayList<Item>();

	public ArrayList<Item> getParteA() {
		return parteA;
	}

	public void setParteA(ArrayList<Item> parteA) {
		this.parteA = parteA;
	}

	public ArrayList<Item> getParteB() {
		return parteB;
	}

	public void setParteB(ArrayList<Item> parteB) {
		this.parteB = parteB;
	}
}
