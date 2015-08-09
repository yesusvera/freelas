package br.com.rsm.enumeradores;

import java.io.Serializable;


public enum EnumSupportSubject implements Serializable {

	GERAIS (1, "Perguntas Gerais"),
	FINANCEIRO (2, "Financeiro"),
	CONFORMIDADE (3, "Conformidade"),
	OUTROS (4, "Outros");
	
	
	private EnumSupportSubject(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	private Integer code;
	private String name;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	
	
}
