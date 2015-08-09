package br.com.iejb.sgi.service;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ValidaPeridoDataService {

	public void validar(Date dataInicial, Date dataFinal) throws PeridoDataInvalidoException {
		if (dataInicial != null && dataFinal != null) {
			if (!dataInicial.before(dataFinal)) {
				throw new PeridoDataInvalidoException("Data final não pode ser menor ou igual a data inicial.");

			}
		}
	}
}
