package br.com.rsm.enumeradores;

import java.io.Serializable;


public enum EnumEstado implements Serializable {

	ACRE (1, "AC", "Acre"),
	ALAGOAS (2, "AL", "Alagoas"),
	AMAPA (3, "AP", "Amapa"),
	AMAZONAS (4, "AM", "Amazonas"),
	BAHIA (5, "BA", "Bahia"),
	CEARA (6, "CE", "Ceara"),
	DISTRITO_FEDERAL (7, "DF", "Distrito Federal"),
	ESPIRITO_SANTO (8, "ES", "Espirito Santo"),
	GOIAS (9, "GO", "Goias"),
	MARANHAO (10, "MA", "Maranhao"),
	MATOGROSSO (11, "MT", "Mato Grosso"),
	MATOGROSSO_SUL (12, "MS", "Mato Grosso do Sul"),
	MINAS_GERAIS (13, "MG", "Minas Gerais"),
	PARA (14, "PA", "Para"),
	PARAIBA (15, "PB", "Paraiba"),
	PARANA (16, "PR", "Parana"),
	PERNAMBUCO (17, "PE", "Pernambuco"),
	PIAUI (18, "PI", "Piaui"),
	RIO_JANEIRO (19, "RJ", "Rio de Janeiro"),
	RIO_GRANDE_NORTE (20, "RN", "Rio Grande do Norte"),
	RIO_GRANDE_SUL (21, "RS", "Rio Grande do Sul"),
	RONDONIA (22, "RO", "Rondonia"),
	RORAIMA (23, "RR", "Roraima"),
	SANTA_CATARINA (24, "SC", "Santa Catarina"),
	SAO_PAULO (25, "SP", "Sao Paulo"),
	SERGIPE (26, "SE", "Sergipe"),
	TOCANTINS (27, "TO", "Tocantins");
	
	
	private EnumEstado(Integer codigo, String sigla, String nome){
		setCodigo(codigo);
		setSigla(sigla);
		setNome(nome);
	}
	
	private Integer codigo;
	private String sigla;
	private String nome;
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public String getSigla() {
		return sigla;
	}
	
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
