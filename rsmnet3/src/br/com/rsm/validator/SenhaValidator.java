package br.com.rsm.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("senhaValidator")
public class SenhaValidator implements Validator{

	@Override
	public void validate(FacesContext arg0, UIComponent component, Object arg2) throws ValidatorException {
		
		String confirm = (String) arg2;
		String pass = (String) component.getAttributes().get("confirm");
        if (confirm == null || pass == null) {
            return; // Just ignore and let required="true" do its job.
        }

        if (!confirm.equals(pass)) {
        	FacesMessage message = new FacesMessage("A senha e sua confirmação devem ser iguais.");
        	message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
	}

}
