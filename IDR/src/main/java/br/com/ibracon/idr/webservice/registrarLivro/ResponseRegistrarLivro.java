package br.com.ibracon.idr.webservice.registrarLivro;

import br.com.ibracon.idr.webservice.ResponseWS;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class ResponseRegistrarLivro extends ResponseWS {
	
	
	private LivroRegistrado livro;
	private String erro;
	private String msgErro;
	public LivroRegistrado getLivro() {
		return livro;
	}
	public void setLivro(LivroRegistrado livro) {
		this.livro = livro;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
	public String getMsgErro() {
		return msgErro;
	}
	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}
	@Override
	public String toString() {
		return "ResponseRegistrarLivro [livro=" + livro + ", erro=" + erro
				+ ", msgErro=" + msgErro + "]";
	}
}
