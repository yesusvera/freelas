package br.com.iejb.sgi.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Persistence;

@FacesConverter("chaveConverteObjeto")
public class ConverterEntity<T extends Persistence> implements Converter {
	private static final Logger logger = Logger.getLogger(ConverterEntity.class.getCanonicalName());

	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
        T entity = null;
        logger.log(Level.INFO, "Tentando converter em uma instância de {0} do Id: {1}", new Object[] {getClass().getSimpleName(), value});

        if (value != null) {
            // carrega a entidade dada pelo ID.
            try {
                Long id = Long.valueOf(value);
                entity = (T) component.getAttributes().get(value);
            }
            catch (NumberFormatException e) {
                logger.log(Level.WARNING, "valor não é um número (Long): {0}", value);
                return null;
            }
        }

        logger.log(Level.INFO, "Retornando: {0}", entity);
        return entity;
    }
	
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        // Verifica se o valor fornecido é uma entidade de classe a que se refere o conversor.
        if ((value != null) && (value.getClass().equals(getClass()))) {
            @SuppressWarnings("unchecked")
			T entity = (T)value;

            // Verifica se há id nulo e retorna o id convertido para string.
            if (entity != null) 
            	return entity.toString();
        }

        // Se ele não passar um dos controlos anteriores, retornar uma string vazia.
        return "";
    }

}
