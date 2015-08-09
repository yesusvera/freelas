package br.com.iejb.sgi.util.beanmapper;

import org.dozer.DozerConverter;

import com.google.common.base.Strings;

/**
 * Conversor de CPF.
 * 
 * @see BeanMapperProducer
 */
public class DoubleCpfStringCpfConverter extends DozerConverter<String, Double> {

    private static final int TAMANHO = 11;
    private static final char PAD = '0';

    /**
     * Construtor padrao.
     */
    public DoubleCpfStringCpfConverter() {

        super(String.class, Double.class);
    }

    /**
     * @see org.dozer.DozerConverter#convertFrom(java.lang.Object, java.lang.Object)
     */
    @Override
    public String convertFrom(final Double arg0, final String arg1) {

        final long cpLong = Math.round(arg0);

        final String cpf = Long.toString(cpLong);

        return Strings.padStart(cpf, TAMANHO, PAD);
    }

    /**
     * @see org.dozer.DozerConverter#convertTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public Double convertTo(final String arg0, final Double arg1) {

        return Double.valueOf(arg0);
    }
}
