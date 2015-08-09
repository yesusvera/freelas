package br.com.iejb.sgi.domain.to;

import java.util.Date;
public class PerfilTO {
	
	private static final long serialVersionUID = 1L;
	
	private Long id; 
	
	private String descricaoAtividadePep;
	
	private String observacao;

	private Date dataValidade;

	public PerfilTO(){
		
	}
	
	public PerfilTO(Long id) {
		this.id=id;
	}
	
	public PerfilTO(Long id,
			String descricaoAtividadePep, String observacao, Date dataValidade) {
		super();
		this.id = id;
		this.descricaoAtividadePep = descricaoAtividadePep;
		this.observacao = observacao;
		this.dataValidade = dataValidade;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricaoAtividadePep() {
		return descricaoAtividadePep;
	}

	public void setDescricaoAtividadePep(String descricaoAtividadePep) {
		this.descricaoAtividadePep = descricaoAtividadePep;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {

		if (!(object instanceof PerfilTO)) {
			return false;
		}
		PerfilTO other = (PerfilTO) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return getDescricaoAtividadePep(); 
	}
}
