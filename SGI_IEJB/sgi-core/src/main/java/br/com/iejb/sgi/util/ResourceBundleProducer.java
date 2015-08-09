package br.com.iejb.sgi.util;

import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import br.com.iejb.sgi.annotation.Core;

/**
 * Produz o resource bundle com as mensagens de negócio do sistema.
 * 
 */
@ApplicationScoped
public class ResourceBundleProducer {

	@Produces
	@Core
	private ResourceBundle resourceBundle = ResourceBundle
			.getBundle("br.com.iejb.sgi.service.Messages");

}
