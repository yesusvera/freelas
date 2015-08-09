package br.com.iejb.sgi.exception;

/**
 * Encapsula toda e qualquer {@link RuntimeException} que seja lancada ou pelo {@code container} ou pelo {@code JPA}.
 */
public class SistemaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String mensagem;

    /**
     * @param mensagem {@link String} Mensagem de erro.
     */
    public SistemaException(final String mensagem) {

        super(mensagem);

        this.mensagem = mensagem;
    }

    /**
     * Construtor padrao.
     */
    public SistemaException() {

        super();

    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {

        return this.mensagem;
    }

}
