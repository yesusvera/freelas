package br.com.iejb.sgi.util.beanmapper;

import org.dozer.DozerConverter;

import com.google.common.base.Strings;

/**
 * Conversor de CPF.
 * 
 * @see BeanMapperProducer
 */
public class LongCpfStringCpfConverter extends DozerConverter<String, Long> {

    private static final int TAMANHO = 11;

    private static final char PAD = '0';

    /**
     * Construtor padrao.
     */
    public LongCpfStringCpfConverter() {

        super(String.class, Long.class);
    }

    /**
     * @see org.dozer.DozerConverter#convertFrom(java.lang.Object, java.lang.Object)
     */
    @Override
    public String convertFrom(final Long arg0, final String arg1) {

        final String cpf = Long.toString(arg0);

        return Strings.padStart(cpf, TAMANHO, PAD);
    }

    /**
     * @see org.dozer.DozerConverter#convertTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public Long convertTo(final String arg0, final Long arg1) {

        return Long.valueOf(arg0);
    }

}
