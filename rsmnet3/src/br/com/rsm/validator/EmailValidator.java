package br.com.rsm.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class EmailValidator implements Validator {

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {

		String enteredEmail = (String) object;
		// Set the email pattern string
		if (enteredEmail != null && !enteredEmail.isEmpty()) {

			Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
//			Pattern p = Pattern.compile("[\w\.-]*[a-zA-Z0-9_]@[\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]");
//			Pattern p = Pattern.compile(".+@.+\\\\.[a-z]+");

			// Match the given string with the pattern
			Matcher m = p.matcher(enteredEmail);

			// Check whether match is found
			boolean matchFound = m.matches();

			if (!matchFound) {
				FacesMessage message = new FacesMessage();
				message.setDetail("Email not valid");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}
		}
	}

}
