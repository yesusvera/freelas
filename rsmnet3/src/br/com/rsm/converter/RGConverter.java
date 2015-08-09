package br.com.rsm.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

public class RGConverter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		/*
		 * Irá converter CPF formatado para um sem pontos e traço. Ex.:
		 * 355.245.198-87 torna-se 35524519887.
		 */
		String rg = value;
		if (value != null && !value.equals(""))
			rg = value.replaceAll("\\.", "").replaceAll("\\-", "");

		return rg;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		/*
		 * Irá converter RG não formatado para um com pontos e traço. Ex.:
		 * 35524519887 torna-se 355.245.198-87.
		 * 1137720689
		 */
		String rg = (String) value;
		if (rg != null && rg.length() == 11)
			rg = rg.substring(0, 2) + "." + rg.substring(2, 5) + "." + rg.substring(5, 8) + "-"
					+ rg.substring(9, 11);

		return rg;
	}
}
