package br.com.rsm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;


public class PhoneConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
//		Formatador.formataTelefone(input);
		return null;
	}


	// CEP - resultado: 81580-200
	
	// CPF - resultado 012.345.699-01
//	format("###.###.###-##", "01234569905");
	// CNPJ - resultado: 01.234.569/9052-34
//	format("##.###.###/####-##", "01234569905234");
	
}
