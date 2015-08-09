package br.com.iejb.sgi.service.autenticacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.iejb.sgi.domain.Usuario;
import br.com.iejb.sgi.repository.UsuarioRepository;

@Stateless
public class AutenticacaoService implements AutenticacaoServiceRemote {

	@Inject private UsuarioRepository usuarioRepository;
	
	public Usuario verificarLogin(Usuario usuario){
		
		List<Usuario> lstTmp = usuarioRepository.getUsuarios(usuario.getLogin(), usuario.getSenha());
		
		if(lstTmp==null || lstTmp.size() == 0){
			return null;
		}else{
			return lstTmp.get(0);
		}
	}

}
