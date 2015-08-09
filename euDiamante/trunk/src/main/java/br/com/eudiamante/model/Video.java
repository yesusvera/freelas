package br.com.eudiamante.model;

import java.util.ArrayList;
import java.util.List;

public class Video {
	public String codigo;
	public String nome;
	public int percentualAssistido;
	public String arquivo;
	public String miniatura;
	public String urlYoutube;
	public String tipo;
	public String legenda;
	public List<Video> proximosVideos = new ArrayList<Video>();
	public boolean isDisponivel;
	public boolean isPlaying;
	private boolean visited = false;

	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void adicionaProximoVideo(Video video) {
		this.proximosVideos.add(video);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getPercentualAssistido() {
		return percentualAssistido;
	}

	public void setPercentualAssistido(int percentualAssistido) {
		this.percentualAssistido = percentualAssistido;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public String getMiniatura() {
		return miniatura;
	}

	public void setMiniatura(String miniatura) {
		this.miniatura = miniatura;
	}

	public String getUrlYoutube() {
		return urlYoutube;
	}

	public void setUrlYoutube(String urlYoutube) {
		this.urlYoutube = urlYoutube;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<Video> getProximosVideos() {
		return proximosVideos;
	}

	public void setProximosVideos(List<Video> proximosVideos) {
		this.proximosVideos = proximosVideos;
	}

	public String getLegenda() {
		return legenda;
	}

	public void setLegenda(String legenda) {
		this.legenda = legenda;
	}

	public boolean isDisponivel() {
		return isDisponivel;
	}

	public void setDisponivel(boolean isDisponivel) {
		this.isDisponivel = isDisponivel;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void setVisited(boolean b) {
		this.visited  = b;
	}

	public boolean isVisited() {
		return visited;
	}
}