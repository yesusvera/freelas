package br.com.iejb.sgi.web.cadastrarMembro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import br.com.iejb.sgi.domain.Membro;

@Named
public class CadastrarMembroView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Membro membro = new Membro();
	
	private List<Membro> listaMembros = new ArrayList<Membro>();

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
}
