package br.com.rsm.validator;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.rsm.model.Account;
import br.com.rsm.service.AccountService;

public class IndicacaoValidator implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object idIndicacao) throws ValidatorException {
		
		String userId = null;
		
		if(!(idIndicacao instanceof String)){
			userId = idIndicacao.toString();
		}else{
			userId = (String) idIndicacao;
		}
		
		if (userId!=null) {
			AccountService accountService = new AccountService();
			Account accountParam = new Account();
			accountParam.setUserId(userId);
			boolean idValidParentId = false;
			try {
				idValidParentId = accountService.verificarIdIndicacao(userId);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (!idValidParentId) {
				FacesMessage msg = new FacesMessage();
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				msg.setDetail("ID de indicação inválidoxxx");
				throw new ValidatorException(msg);
			} 
		}
	}
}
