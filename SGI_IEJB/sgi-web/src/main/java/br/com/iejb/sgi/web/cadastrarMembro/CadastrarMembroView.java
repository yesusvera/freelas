package br.com.iejb.sgi.web.cadastrarMembro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.inject.Named;

import br.com.iejb.sgi.domain.Filho;
import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.domain.UF;

@Named
public class CadastrarMembroView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Membro membro = new Membro();
	
	private List<Membro> listaMembros = new ArrayList<Membro>();

	private List<SelectItem> listaUF;
	
	private List<SelectItem> listaCidade = new ArrayList<SelectItem>();
	
	private UF uf;
	
	private String senhaConfirmacao;
	
	private Filho filho;

	public Membro getMembro() {
		return membro;
	}

	public void setMembro(Membro membro) {
		this.membro = membro;
	}

	public List<Membro> getListaMembros() {
		return listaMembros;
	}

	public void setListaMembros(List<Membro> listaMembros) {
		this.listaMembros = listaMembros;
	}

	public List<SelectItem> getListaUF() {
		return listaUF;
	}

	public void setListaUF(List<SelectItem> listaUF) {
		this.listaUF = listaUF;
	}

	public List<SelectItem> getListaCidade() {
		return listaCidade;
	}

	public void setListaCidade(List<SelectItem> listaCidade) {
		this.listaCidade = listaCidade;
	}

	public String getSenhaConfirmacao() {
		return senhaConfirmacao;
	}

	public void setSenhaConfirmacao(String senhaConfirmacao) {
		this.senhaConfirmacao = senhaConfirmacao;
	}
	
	public UF getUf() {
		return uf;
	}
	
	public void setUf(UF uf) {
		this.uf = uf;
	}

	public Filho getFilho() {
		return filho;
	}

	public void setFilho(Filho filho) {
		this.filho = filho;
	}
}
