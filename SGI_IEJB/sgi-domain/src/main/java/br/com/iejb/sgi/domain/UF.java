package br.com.iejb.sgi.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import br.com.iejb.sgi.customSearch.labelSelectOneMenu;

@Entity(name="TB_UF")
public class UF implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@labelSelectOneMenu
	@Column(length=2)
	private String sigla;
	
	@OneToMany(mappedBy="uf", fetch=FetchType.EAGER)
	List<Cidade> cidades;

	public UF(Long id){
		this.id = id;
	}
	
	public UF(){
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}
}