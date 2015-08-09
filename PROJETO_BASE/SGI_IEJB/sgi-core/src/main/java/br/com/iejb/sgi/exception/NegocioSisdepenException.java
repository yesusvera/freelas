package br.com.iejb.sgi.exception;

public class NegocioSisdepenException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String mensagem;

    public NegocioSisdepenException(final String mensagem) {
        super(mensagem);
        this.mensagem = mensagem;
    }

    @Override
    public String getMessage() {
        return this.mensagem;
    }
}
