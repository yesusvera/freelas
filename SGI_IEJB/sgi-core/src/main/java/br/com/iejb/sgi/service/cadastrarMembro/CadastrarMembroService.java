package br.com.iejb.sgi.service.cadastrarMembro;

import java.util.List;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.repository.MembroRepository;
import br.com.iejb.sgi.util.HashConfirmacaoMembro;
import br.com.iejb.sgi.util.SendMail;

@Stateless
public class CadastrarMembroService implements CadastrarMembroServiceRemote {

	@Inject private MembroRepository membroRepository;

	@Override
	public void salvarMembro(Membro membro) {
		HashConfirmacaoMembro hashConfirmacaoMembro = new HashConfirmacaoMembro(membro);
		membro.setHashConfirmacaoCadastro(hashConfirmacaoMembro.geraHashConfirmacao());
		membroRepository.save(membro);
		//enviarEmailConfirmacao(membro);
	}

	@Override
	public List<Membro> listarMembros() {
		return membroRepository.findAll();
	}

	@Override
	public void enviarEmailConfirmacao(Membro membro) {
		
		StringBuffer corpoEmail = new StringBuffer();
		
		HashConfirmacaoMembro hashConfirmacaoMembro = new HashConfirmacaoMembro(membro);
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		String link = "http://";
		link += FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()!=80){
			link+=":"+FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
		}
		
		corpoEmail
			.append("Prezado(a) <b>").append(membro.getNome()).append("</b>,<br>")
			.append("Recebemos um cadastro indicando este email e precisamos da validação do mesmo, para isto basta clicar no link abaixo: <br>")
			.append(link  + request.getContextPath() + "/validacaoCadastro/" + hashConfirmacaoMembro.geraHashConfirmacao())
			.append("<br> Obrigado.");
		
		new SendMail().sendMail("iejb.desenvolvimento@gmail.com", membro.getEmail(), "Cadastro de membro - IEJB", corpoEmail.toString());
	}

	@Override
	public Membro buscarMembroPorHashConfirmacao(String hashConfirmacaoCadastro) {
		List<Membro> listaMemb = membroRepository.buscarMembroPorHashConfirmacao(hashConfirmacaoCadastro);
		if(listaMemb!=null && listaMemb.size()>0){
			return listaMemb.get(0);
		}else{
			return null;
		}
	}
}
