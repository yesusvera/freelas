package br.com.iejb.sgi.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.joda.time.DateTime;
import org.joda.time.Years;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidaIdadeParticipanteService.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ValidaIdadeParticipanteService {

	/**
	 * Método que validade se participante tem a idade necessário para realizar
	 * o financiamento de crédito imobiliário conforme regra RN001 Menor de 16
	 * anos, não permite financiamento. De 16 anos a 17 anos, apenas se for
	 * emancipado: É considerado emancipado quando: for casado ou por concurso
	 * público ou decisão judicial. Em uma das três situações o participante
	 * deverá apresentar documento de comprovação.
	 *
	 * @param dataNascimento the data nascimento
	 * @throws IdadeInvalidaException the idade invalida exception
	 */
	public void validar(Date dataNascimento) throws IdadeInvalidaException {
		DateTime dtNascimento = new DateTime(dataNascimento);
		DateTime dtAtual = new DateTime(new Date());
		int a = Years.yearsBetween(dtNascimento, dtAtual).getYears();

		
		if (dtNascimento.isAfter(dtAtual)) {
			throw new IdadeInvalidaException(
					"Operação não permitida. A data de nascimento do participante é maior ou igual à data atual.");
		}
		if (a < 16) {
			throw new IdadeInvalidaException(
					"Idade do participante não permite a concessão de crédito imobiliário.");
		}
	}
	
	/**
	 * Verifica obrigatoriedade idade emancipacao.
	 *
	 * @param dataNascimento the data nascimento
	 * @return the boolean
	 * @throws IdadeInvalidaException the idade invalida exception
	 */
	public Boolean verificaObrigatoriedadeIdadeEmancipacao(Date dataNascimento){
		DateTime dtNascimento = new DateTime(dataNascimento);
		DateTime dtAtual = new DateTime(new Date());
		int a = Years.yearsBetween(dtNascimento, dtAtual).getYears();
		
		if(a >= 16 && a <= 17){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
