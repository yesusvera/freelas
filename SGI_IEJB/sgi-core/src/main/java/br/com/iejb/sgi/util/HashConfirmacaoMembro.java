package br.com.iejb.sgi.util;

import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.util.cripto.Sha1Cripto;

/**
 * 
 * @author Yesus Castillo Vera
 * Classe para gerar e validar hash de confirmacao enviada ao membro
 *
 */
public class HashConfirmacaoMembro {

	private Membro membro;
	
	public HashConfirmacaoMembro(Membro membro){
		this.membro = membro;
	}
	
	public boolean hashConfirmacaoEValido(String hash){
		return geraHashConfirmacao().equals(hash);
	}
	
	public String geraHashConfirmacao(){
		return Sha1Cripto.sha1(getPadraoHashMembro());
	}
	
	private String getPadraoHashMembro(){
		String str = membro.getNome();
		if(membro.getDataNascimento()!=null){
			str+= membro.getDataNascimento().toGMTString(); 
		}
		str+=membro.getTelefoneCelular();
		return str;
	}
	
	@Override
	public String toString() {
		return geraHashConfirmacao();
	}
}
