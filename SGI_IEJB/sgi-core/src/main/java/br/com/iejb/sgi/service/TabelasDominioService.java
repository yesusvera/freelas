package br.com.iejb.sgi.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.iejb.sgi.domain.UF;
import br.com.iejb.sgi.repository.UFRepository;

@Stateless
public class TabelasDominioService implements TabelasDominioServiceRemote {

	@Inject private UFRepository ufRepository;

	@Override
	public List<UF> listarUFs() {
		return ufRepository.findAll();
	}
}
