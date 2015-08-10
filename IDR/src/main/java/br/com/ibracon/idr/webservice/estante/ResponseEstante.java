package br.com.ibracon.idr.webservice.estante;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import br.com.ibracon.idr.form.model.Livro;
import br.com.ibracon.idr.webservice.ResponseWS;

@XStreamAlias("response")
public class ResponseEstante extends ResponseWS {

	@XStreamAlias("parabaixar")
	public ArrayList<Livro> parabaixar = new ArrayList<Livro>();

	@XStreamAlias("dedireito")
	public ArrayList<Livro> dedireito = new ArrayList<Livro>();
	
	@XStreamAlias("baixados")
	public ArrayList<Livro> baixados = new ArrayList<Livro>();

	public ArrayList<Livro> getParabaixar() {
		return parabaixar;
	}
	public void setParabaixar(ArrayList<Livro> parabaixar) {
		this.parabaixar = parabaixar;
	}
	public ArrayList<Livro> getDedireito() {
		return dedireito;
	}
	public void setDedireito(ArrayList<Livro> dedireito) {
		this.dedireito = dedireito;
	}
	public ArrayList<Livro> getBaixados() {
		return baixados;
	}
	public void setBaixados(ArrayList<Livro> baixados) {
		this.baixados = baixados;
	}
}
