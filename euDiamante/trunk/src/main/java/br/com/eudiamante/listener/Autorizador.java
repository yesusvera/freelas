package br.com.eudiamante.listener;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import br.com.eudiamante.mb.LoginBean;

public class Autorizador implements PhaseListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();

		if ("/index.xhtml".equals(context.getViewRoot().getViewId())) {
			return;
		}
		LoginBean loginBean = context.getApplication().evaluateExpressionGet(
				context, "#{loginBean}", LoginBean.class);

		// Caso nao quisesse usar o #{loginBean}
		// //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loginBean");

		if (!loginBean.isLogado()) {

			NavigationHandler handler = context.getApplication()
					.getNavigationHandler();
			handler.handleNavigation(context, null, "index?faces-redirect=true");

			context.renderResponse();
		}

	}

	@Override
	public void beforePhase(PhaseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
