package br.com.iejb.sgi.service.cadastrarMembro;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.repository.MembroRepository;

@Stateless
public class CadastrarMembroService implements CadastrarMembroServiceRemote {

	@Inject private MembroRepository membroRepository;

	@Override
	public void salvarMembro(Membro membro) {
		membroRepository.save(membro);
	}

	@Override
	public List<Membro> listarMembros() {
		return membroRepository.findAll();
	}
}
