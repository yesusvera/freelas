package br.com.iejb.sgi.domain;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity(name="TB_MEMBRO")
public class Membro implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=100)
	private String nome;
	
	@Column(length=100)
	private String nomeConjuge;
	
	private String cpf;
	
	@Column(length=1)
	private String sexo;
	
	private Date dataNascimento;
	
	@Column(length=4)
	private String tipoSanguineo;
	
	@Column(length=50)
	private String email;
	
	@OneToMany(mappedBy="membro", fetch=FetchType.EAGER, orphanRemoval=true)
	@Column(insertable=true, updatable=true)
	private List<Filho> filhos;

	private String endereco;
	
	private String bairro;
	
	@OneToOne
	@JoinColumn(name="idCidade")
	private Cidade cidade;
	
	private String cep;
	
	private String telefoneFixo;
	
	private String telefoneCelular;
	
	private String telefoneComercial;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="idUsuario", insertable=true, updatable=true)
	private Usuario usuario;
	
	private boolean ativo;
	
	public String hashConfirmacaoCadastro;

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNomeConjuge() {
		return nomeConjuge;
	}
	public void setNomeConjuge(String nomeConjuge) {
		this.nomeConjuge = nomeConjuge;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getTipoSanguineo() {
		return tipoSanguineo;
	}
	public void setTipoSanguineo(String tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getTelefoneFixo() {
		return telefoneFixo;
	}
	public void setTelefoneFixo(String telefoneFixo) {
		this.telefoneFixo = telefoneFixo;
	}
	public String getTelefoneCelular() {
		return telefoneCelular;
	}
	public void setTelefoneCelular(String telefoneCelular) {
		this.telefoneCelular = telefoneCelular;
	}
	public String getTelefoneComercial() {
		return telefoneComercial;
	}
	public void setTelefoneComercial(String telefoneComercial) {
		this.telefoneComercial = telefoneComercial;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public List<Filho> getFilhos() {
		return filhos;
	}
	public void setFilhos(List<Filho> filhos) {
		this.filhos = filhos;
	}
	public String getHashConfirmacaoCadastro() {
		return hashConfirmacaoCadastro;
	}
	public void setHashConfirmacaoCadastro(String hashConfirmacaoCadastro) {
		this.hashConfirmacaoCadastro = hashConfirmacaoCadastro;
	}
}
