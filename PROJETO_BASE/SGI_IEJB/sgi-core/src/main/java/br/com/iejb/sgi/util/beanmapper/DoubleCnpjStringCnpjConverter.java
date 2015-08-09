package br.com.iejb.sgi.util.beanmapper;

import org.dozer.DozerConverter;

import com.google.common.base.Strings;

/**
 * Conversor de CPF.
 * 
 * @see br.com.poupex.sci.util.BeanMapperProducer
 */
public class DoubleCnpjStringCnpjConverter extends DozerConverter<String, Double> {

    private static final int TAMANHO = 14;
    private static final char PAD = '0';

    /**
     * Construtor padrao.
     */
    public DoubleCnpjStringCnpjConverter() {

        super(String.class, Double.class);
    }

    /**
     * @see org.dozer.DozerConverter#convertFrom(java.lang.Object, java.lang.Object)
     */
    @Override
    public String convertFrom(final Double arg0, final String arg1) {

        final long cpLong = Math.round(arg0);

        final String cnpj = Long.toString(cpLong);

        return Strings.padStart(cnpj, TAMANHO, PAD);
    }

    /**
     * @see org.dozer.DozerConverter#convertTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public Double convertTo(final String arg0, final Double arg1) {

        return Double.valueOf(arg0);
    }
}
