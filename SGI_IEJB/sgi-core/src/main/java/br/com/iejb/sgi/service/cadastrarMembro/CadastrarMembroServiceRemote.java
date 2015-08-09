package br.com.iejb.sgi.service.cadastrarMembro;

import java.util.List;

import javax.ejb.Remote;

import br.com.iejb.sgi.domain.Membro;

@Remote
public interface CadastrarMembroServiceRemote {
	
	public void salvarMembro(Membro membro);
	
	public List<Membro> listarMembros();
	
	public void enviarEmailConfirmacao(Membro membro);
	
	public Membro buscarMembroPorHashConfirmacao(String hashConfirmacaoCadastro);
}
