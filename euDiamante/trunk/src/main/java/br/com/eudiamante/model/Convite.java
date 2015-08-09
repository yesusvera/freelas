package br.com.eudiamante.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@SequenceGenerator(name = "seq_convite", sequenceName = "seq_convite", allocationSize = 1)
public class Convite {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_convite")
	private Long id;

	/** SOBRE O ENVIO DE CONVITES **/
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "usuario_id")
	private Usuario usuarioConvidador;

	private String emailEnvioConvite;
	private Boolean ativo = false;

	@Column(unique = true)
	private String hashValidacao;

	private Date dataEnvioConvite;
	/*****************************/
	
	private Date dataNascimento;

	@Column(unique = true)
	private String hashAcesso;

	private Integer videoAtual;
	private Integer percentualAssistido;

	//TRUE - FISICA | FALSE - JURIDICA
	private boolean tipoPessoa;
	
	private String paginaAtual;
	private String nomeRazao;
	private String cpfCnpj;
	private String email;
	private String telefone;
	private String celular;
	private String login;
	private String senha;
	private String cep;
	private String endereco;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	
	private String operadora;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuarioConvidador() {
		return usuarioConvidador;
	}

	public void setUsuarioConvidador(Usuario usuarioConvidador) {
		this.usuarioConvidador = usuarioConvidador;
	}

	public String getEmailEnvioConvite() {
		return emailEnvioConvite;
	}

	public void setEmailEnvioConvite(String emailEnvioConvite) {
		this.emailEnvioConvite = emailEnvioConvite;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getHashValidacao() {
		return hashValidacao;
	}

	public void setHashValidacao(String hashValidacao) {
		this.hashValidacao = hashValidacao;
	}

	public Date getDataEnvioConvite() {
		return dataEnvioConvite;
	}

	public void setDataEnvioConvite(Date dataEnvioConvite) {
		this.dataEnvioConvite = dataEnvioConvite;
	}

	public String getHashAcesso() {
		return hashAcesso;
	}

	public void setHashAcesso(String hashAcesso) {
		this.hashAcesso = hashAcesso;
	}

	public Integer getVideoAtual() {
		return videoAtual;
	}

	public void setVideoAtual(Integer videoAtual) {
		this.videoAtual = videoAtual;
	}

	public Integer getPercentualAssistido() {
		return percentualAssistido;
	}

	public void setPercentualAssistido(Integer percentualAssistido) {
		this.percentualAssistido = percentualAssistido;
	}

	public String getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(String paginaAtual) {
		this.paginaAtual = paginaAtual;
	}

	public String getNomeRazao() {
		return nomeRazao;
	}

	public void setNomeRazao(String nomeRazao) {
		this.nomeRazao = nomeRazao;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean isTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(boolean tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getOperadora() {
		return operadora;
	}

	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
}