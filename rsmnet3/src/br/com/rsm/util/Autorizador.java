package br.com.rsm.util;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.com.rsm.bean.LoginBean;

public class Autorizador implements PhaseListener {

	private static final long serialVersionUID = 405560136537836299L;

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		
		if(!context.getViewRoot().getViewId().contains("/templates/backend/")){
			return;
		}
//		if("/templates/frontend/index_rsm.xhtml".equals(context.getViewRoot().getViewId())
//				|| 
//		   "/templates/frontend/form_wizard.xhtml".equals(context.getViewRoot().getViewId())
//		   		||
//		   "/templates/frontend/bemvindo.xhtml".equals(context.getViewRoot().getViewId())){
//			return;
//		}
		
		//Obtendo LoginBean da sessão
		LoginBean loginBean = context.getApplication().evaluateExpressionGet(context,  "#{loginBean}", LoginBean.class);
		NavigationHandler handler = context.getApplication().getNavigationHandler();
		
		if(!loginBean.isLogged()){
			
			handler.handleNavigation(context, null, "pretty:home");

		} else if (loginBean.getUserAuth().getAccount().getStatus().endsWith("CRIADA") && context.getViewRoot().getViewId().contains("/templates/backend/index.xhtml")) {
			
				handler.handleNavigation(context, null, "pretty:home");
			
		} else if(loginBean.isLogged() && context.getViewRoot().getViewId().contains("/templates/backend/bemvindo.xhtml") || context.getViewRoot().getViewId().contains("/templates/backend/index.xhtml")){
			return; //ta logado querendo deslogar ou chamar uma action como alterar senha
		}
		
		//Efetua renderização da tela
		context.renderResponse();

	}

	@Override
	public void beforePhase(PhaseEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
