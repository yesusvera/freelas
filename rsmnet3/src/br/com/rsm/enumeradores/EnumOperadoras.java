package br.com.rsm.enumeradores;

import java.io.Serializable;


public enum EnumOperadoras  implements Serializable {

	VIVO (1, "Vivo"),
	TIM (2, "Tim"),
	CLARO (3, "Claro"),
	OI (4, "Oi"),
	ALGAR (5, "Algar"),
	NEXTEL (6, "Nextel");

	private EnumOperadoras(Integer codigo, String nome) {
		setCodigo(codigo);
		setNome(nome);
	}

	private Integer codigo;
	private String nome;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
