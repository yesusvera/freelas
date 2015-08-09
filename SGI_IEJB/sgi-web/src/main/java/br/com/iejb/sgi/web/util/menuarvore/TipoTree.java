package br.com.iejb.sgi.web.util.menuarvore;

public enum TipoTree {

    OUTROS(0, "OUTROS", "OUTROS"),
    CONFIGURACAO_LINHA(1, "CONFIGURAÇÃO LINHA", "UC001");

    private Integer codigo;
    private String descricao;
    private String casoDeUso;

    private TipoTree(final Integer codigo, final String descricao, final String casoDeUso) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.casoDeUso = casoDeUso;
    }

    public Integer getCodigo() {
        return this.codigo;
    }

    public void setCodigo(final Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public String getCasoDeUso() {
        return this.casoDeUso;
    }

    public void setCasoDeUso(final String casoDeUso) {
        this.casoDeUso = casoDeUso;
    }

}
