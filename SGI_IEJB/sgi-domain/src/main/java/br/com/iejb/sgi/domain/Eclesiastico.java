package br.com.iejb.sgi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

//@Entity(name="TB_ECLESIASTISCO")
public class Eclesiastico  implements Serializable{

	private static final long serialVersionUID = 1L;

//	@Id
	private Long id;
	
	private int anoConversao;

//	@OneToOne
//	@PrimaryKeyJoinColumn
	private Membro membro;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAnoConversao() {
		return anoConversao;
	}

	public void setAnoConversao(int anoConversao) {
		this.anoConversao = anoConversao;
	}
}
