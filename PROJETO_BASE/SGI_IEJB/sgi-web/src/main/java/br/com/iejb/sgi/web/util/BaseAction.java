/*
 * 
 */
package br.com.iejb.sgi.web.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Configuração das mensagens
 *
 */
public class BaseAction implements Serializable {

	private static final long serialVersionUID = 1L;
		
	private FacesContext context = FacesContext.getCurrentInstance();
						
	/**
	 * Ex: addMensagemInformativa(Constantes.MSG_XX);
	 * @param summary
	 * @param detail
	 */
	public void addMensagemInformativa(String keyOrMessage, Object... parametros) {
		String mensagem = getMensagem(keyOrMessage, parametros);
        addFacesMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, ""));
    }
	/**
	 * Ex: addMensagemInformativa(idComponente,Constantes.MSG_XX);
	 * @param clientId
	 * @param summary
	 * @param detail
	 */
	public void addMensagemInformativa(final String clientId, String keyOrMessage, Object... parametros) {
		String mensagem = getMensagem(keyOrMessage, parametros);
		addFacesMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, ""));
	}
  
    /**
     * Ex: addMensagemAlerta(Constantes.MSG_XX);
     * @param summary
     * @param detail
     */
    public void addMensagemAlerta(String keyOrMessage, Object... parametros) {
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, ""));  
    }
    /**
     * Ex: addMensagemAlerta(idComponente,Constantes.MSG_XX);
     * @param clientId
     * @param summary
     * @param detail
     */
    public void addMensagemAlerta(final String clientId, String keyOrMessage, Object... parametros) {
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, mensagem, ""));  
    }
  
    /**
     * Ex: addMensagemErro(Constantes.MSG_XX);
     * @param summary
     * @param detail
     */
    public void addMensagemErro(String keyOrMessage, Object... parametros) {
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, ""));  
    }  
    /**
     * Ex: addMensagemErro(idComponente,Constantes.MSG_XX);
     * @param clientId
     * @param summary
     * @param detail
     */
    public void addMensagemErro(final String clientId, String keyOrMessage, Object... parametros) { 
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR,  mensagem, ""));  
    }  
  
    /**
     * Ex: addMensagemErroFatal(Constantes.MSG_XX);
     * @param summary
     * @param detail
     */
    public void addMensagemErroFatal(String keyOrMessage, Object... parametros) {
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(new FacesMessage(FacesMessage.SEVERITY_FATAL,  mensagem, ""));  
    }    
    /**
     * Ex: addMensagemErroFatal(idComponente,Constantes.MSG_XX);
     * @param clientId
     * @param summary
     * @param detail
     */
    public void addMensagemErroFatal(final String clientId, String keyOrMessage, Object... parametros) {
    	String mensagem = getMensagem(keyOrMessage, parametros);
    	addFacesMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_FATAL,  mensagem, ""));  
    }

	
	/**
	 * @param clientId
	 * @param facesMessage
	 */
	private void addFacesMessage(final String clientId, FacesMessage facesMessage) {
		getContext().addMessage(clientId,  facesMessage);
	}	
	/**
	 * @param facesMessage
	 */
	private void addFacesMessage(FacesMessage facesMessage) {
		getContext().addMessage(null,  facesMessage);
	}
				
	/**
	 * 
	 * @return
	 */
	protected Application getApplication(){
		return getContext().getApplication();
	}
	
	/**
	 * Recupera a Mensagem com a chave do arquivo de properties informada e preenche com os parametros informados
	 * @param chaveMensagem
	 * @param parametros
	 * @return
	 */
	protected String getMensagem(String chaveMensagem, Object... parametros) {
		
		ResourceBundle resourceBundle = getApplication().getResourceBundle(getContext(), "msg");

		if (resourceBundle != null && resourceBundle.containsKey(chaveMensagem)) {
			chaveMensagem = resourceBundle.getString(chaveMensagem);
		}
		if (parametros != null) {
			chaveMensagem = MessageFormat.format(chaveMensagem,parametros);
		}
		return chaveMensagem;
	}
	
	public FacesContext getContext() {
		return context;
	}
}
