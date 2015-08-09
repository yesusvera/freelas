package br.com.iejb.sgi.converter;

import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * @author Bruno Augusto e Marcos Dourado
 * 
 * Conversor Genérico de Enums
 * */

@FacesConverter("converterEnum")
public class ConverterEnum implements Converter{

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof Enum) {
			component.getAttributes().put("enumClassType", value.getClass());
			return ((Enum<?>) value).name().toString();
		} else {
			throw new ConverterException(new FacesMessage("Value is not an enum: " + value.getClass()));
		}
	}
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		@SuppressWarnings("rawtypes")
		Class<Enum> enumType = (Class<Enum>) component.getAttributes().get("enumClassType");
		try {
			return Enum.valueOf(enumType, value);
		} catch (IllegalArgumentException e) {
			throw new ConverterException(new FacesMessage("Value is not an enum of type: " + enumType));
		}
	}

}
