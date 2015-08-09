package br.com.eudiamante.dao;

import java.util.List;

import br.com.eudiamante.framework.persistencia.BaseDAO;
import br.com.eudiamante.model.Convite;

public class ConviteDAO extends BaseDAO<Convite> {

	public ConviteDAO() {
		super(Convite.class);
	}

	public Convite getConvitePorHashAcesso(String hashAcesso) {
		List<Convite> listaConv = super.listar("select conv from Convite conv where conv.hashAcesso='"+hashAcesso+"'");
		if(listaConv!=null && listaConv.size()>0){
			return listaConv.get(0);
		}else{
			return null;
		}
	}

	public Convite getConvitePorHashValidacao(String hashValidacao) {
		List<Convite> listaConv = super.listar("select conv from Convite conv where conv.hashValidacao='"+hashValidacao+"'");
		if(listaConv!=null && listaConv.size()>0){
			return listaConv.get(0);
		}else{
			return null;
		}
	}
	

}
