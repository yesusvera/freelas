package br.com.iejb.sgi.util;

import java.math.MathContext;
import java.math.RoundingMode;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Cria o MathContext que deve ser usado para os calculos da simulação. 
 * A precisão e a forma de arrendodamento foram definidas pela gestora 
 * Geovania em 25/07/2013. 
 */
@ApplicationScoped
public class MathContextProducer {

	private MathContext mc = new MathContext(9, RoundingMode.HALF_UP);

	@Produces
	public MathContext getMathContext() {
		return mc;

	}
}