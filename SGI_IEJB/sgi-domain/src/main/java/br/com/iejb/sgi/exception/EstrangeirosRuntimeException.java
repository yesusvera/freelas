/**
 * 
 */
package br.com.iejb.sgi.exception;

/**
 * Encapsula erros de {@code Runtime}.
 */
public class EstrangeirosRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8529092755183330462L;

    /**
     * @param exception {@link Throwable}.
     */
    public EstrangeirosRuntimeException(final Throwable exception) {

        super(exception);
    }

}
