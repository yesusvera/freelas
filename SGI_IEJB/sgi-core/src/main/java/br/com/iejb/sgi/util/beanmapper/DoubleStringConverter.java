package br.com.iejb.sgi.util.beanmapper;

import org.dozer.DozerConverter;

/**
 * Conversor de {@link String} para {@link Double}.
 * 
 * @see BeanMapperProducer
 */
public class DoubleStringConverter extends DozerConverter<String, Double> {

    /**
     * Construtor padrao.
     */
    public DoubleStringConverter() {

        super(String.class, Double.class);
    }

    /**
     * @see org.dozer.DozerConverter#convertFrom(java.lang.Object, java.lang.Object)
     */
    @Override
    public String convertFrom(final Double arg0, final String arg1) {

        return Long.toString(Math.round(arg0));
    }

    /**
     * @see org.dozer.DozerConverter#convertTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public Double convertTo(final String arg0, final Double arg1) {

        return Double.parseDouble(arg0);
    }

}
