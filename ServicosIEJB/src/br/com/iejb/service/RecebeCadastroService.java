package br.com.iejb.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.com.iejb.model.Membro;
import br.com.iejb.repository.MembroRepository;


@Named
public class RecebeCadastroService{

	@Inject
	private MembroRepository membroRepository;
	
	public void salvarMembro(Membro membro){
		membroRepository.save(membro);
	}
	
}
