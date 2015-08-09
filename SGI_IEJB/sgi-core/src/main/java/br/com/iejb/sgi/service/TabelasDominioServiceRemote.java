package br.com.iejb.sgi.service;

import java.util.List;

import javax.ejb.Remote;

import br.com.iejb.sgi.domain.UF;

@Remote
public interface TabelasDominioServiceRemote {
	
	public List<UF> listarUFs();
}
