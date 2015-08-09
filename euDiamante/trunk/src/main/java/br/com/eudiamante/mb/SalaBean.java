package br.com.eudiamante.mb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Navegacao;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.model.Video;
import br.com.eudiamante.util.GeradorNomeUtil;
import br.com.eudiamante.util.UtilSession;

@SessionScoped
@ManagedBean(name = "salaBean")
public class SalaBean {

	Usuario usuario = new Usuario();
	private static LinkedList<Usuario> usuarios = new LinkedList<Usuario>();

	public ArrayList<Video> videos = new ArrayList<Video>();
	
	public Video videoAtual = new Video();
	
	private NavegacaoBean navegacaoBean = new NavegacaoBean();

	private Convite convite = new Convite();
	
	ConviteDAO conviteDao = new ConviteDAO();
	
	public SalaBean() {
		if(UtilSession.getConviteSessao()!=null){
			convite = conviteDao.buscarPorId(UtilSession.getConviteSessao().getId());
		}
		
		
		Video vd_01 = new Video();
		vd_01.setUrlYoutube("videos/vd_01.mp4");
		vd_01.setArquivo("vd_01.png");
		vd_01.setTipo("video/mp4");
		vd_01.setNome("ApresentaÁ„o");
		vd_01.setDisponivel(true);
		vd_01.setVisited(true);
		vd_01.setCodigo("vd_01");
		
		
		Video vd_1_1 = new Video();
		vd_1_1.setUrlYoutube("videos/vd_1_1.mp4");
		vd_1_1.setArquivo("vd_1_1.png");
		vd_1_1.setTipo("video/mp4");
		vd_1_1.setNome("Uma Nova Era");
		vd_1_1.setDisponivel(false);
		vd_1_1.setCodigo("vd_1_1");
		
		
		Video vd_1_2 = new Video();
		vd_1_2.setUrlYoutube("videos/vd_1_2.mp4");
		vd_1_2.setArquivo("vd_1_2.png");
		vd_1_2.setTipo("video/mp4");
		vd_1_2.setNome("A M·quina de ConstruÁ„o");
		vd_1_2.setDisponivel(false);
		vd_1_2.setVisited(false);
		vd_1_2.setCodigo("vd_1_2");

		Video vd_1_3 = new Video();
		vd_1_3.setUrlYoutube("videos/vd_1_3.mp4");
		vd_1_3.setArquivo("vd_1_3.png");
		vd_1_3.setTipo("video/mp4");
		vd_1_3.setNome("VÌdeo Completo");
		vd_1_3.setDisponivel(false);
		vd_1_2.setVisited(false);
		vd_1_3.setCodigo("vd_1_3");
		

		Video vd_02 = new Video();
		vd_02.setUrlYoutube("videos/vd_02.mp4");
		vd_02.setArquivo("vd_02.png");
		vd_02.setTipo("video/mp4");
		vd_02.setNome("IntroduÁ„o");
		vd_02.setDisponivel(true);
		vd_02.setVisited(false);
		vd_02.setCodigo("vd_02");

		Video vd_2_1 = new Video();
		vd_2_1.setUrlYoutube("videos/vd_2_1.mp4");
		vd_2_1.setArquivo("vd_2_1.png");
		vd_2_1.setTipo("video/mp4");
		vd_2_1.setNome("Multiplataforma");
		vd_2_1.setDisponivel(false);
		vd_2_1.setVisited(false);
		vd_2_1.setCodigo("vd_2_1");
		
		
		Video vd_2_2 = new Video();
		vd_2_2.setUrlYoutube("videos/vd_2_2.mp4");
		vd_2_2.setArquivo("vd_2_2.png");
		vd_2_2.setTipo("video/mp4");
		vd_2_2.setNome("ComunicaÁ„o do Futuro");
		vd_2_2.setDisponivel(false);
		vd_2_2.setVisited(false);
		vd_2_2.setCodigo("vd_2_2");

		
		Video vd_2_3 = new Video();
		vd_2_3.setUrlYoutube("videos/vd_2_3.mp4");
		vd_2_3.setArquivo("vd_2_3.png");
		vd_2_3.setTipo("video/mp4");
		vd_2_3.setNome("Ferramenta Intuitiva");
		vd_2_3.setDisponivel(false);
		vd_2_3.setVisited(false);
		vd_2_3.setCodigo("vd_2_3");

		
		Video vd_2_4 = new Video();
		vd_2_4.setUrlYoutube("videos/vd_2_4.mp4");
		vd_2_4.setArquivo("vd_2_4.png");
		vd_2_4.setTipo("video/mp4");
		vd_2_4.setNome("Vis„o de Futuro");
		vd_2_4.setDisponivel(false);
		vd_2_4.setVisited(false);
		vd_2_4.setCodigo("vd_2_4");

		
		Video cadastro = new Video();
		cadastro.setArquivo("cadastro.png");
		cadastro.setNome("Cadastro");
		cadastro.setDisponivel(false);
		cadastro.setVisited(false);
		cadastro.setCodigo("cadastro");
		
		vd_01.adicionaProximoVideo(vd_02);
		vd_02.adicionaProximoVideo(vd_1_1);
		vd_02.adicionaProximoVideo(vd_2_1);
		
		
		vd_1_1.adicionaProximoVideo(vd_1_2);
		vd_1_1.adicionaProximoVideo(vd_1_3);
		
		
		vd_1_2.adicionaProximoVideo(vd_1_3);
		vd_1_2.adicionaProximoVideo(cadastro);

		vd_1_3.adicionaProximoVideo(cadastro);
		
		vd_2_1.adicionaProximoVideo(vd_2_2);
		vd_2_1.adicionaProximoVideo(vd_2_4);
		
		vd_2_2.adicionaProximoVideo(vd_2_3);
		vd_2_2.adicionaProximoVideo(vd_2_4);
		
		vd_2_3.adicionaProximoVideo(vd_2_4);
		vd_2_3.adicionaProximoVideo(cadastro);
		
		vd_2_4.adicionaProximoVideo(cadastro);
		
		vd_02.setPlaying(true); //Ao entrar na sala esse È o vÌdeo no estado playing... alterado dinamicamente posteriormente.
		
		this.videos.add(vd_01);
		this.videos.add(vd_02);
		this.videos.add(vd_1_1);
		this.videos.add(vd_1_2);
		this.videos.add(vd_1_3);
		this.videos.add(vd_2_1);
		this.videos.add(vd_2_2);
		this.videos.add(vd_2_3);
		this.videos.add(vd_2_4);
		this.videos.add(cadastro);

	}

	public Convite getConvite() {
		return convite;
	}



	public void setConvite(Convite convite) {
		this.convite = convite;
	}



	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void acrescentarUsuario() {
		Usuario u = new Usuario();
		u.setLogin(GeradorNomeUtil.getNomeRandomico());
		u.setUf(GeradorNomeUtil.getUFRandomico());
		long x = Math.round(Math.random() * 5);
		if (x == 2) {
			if (usuarios.size() > 10) {
				usuarios.removeFirst();
			}
			usuarios.add(u);
		}

	}

	public String callMeJoao() {
		String retorno = "";
		this.usuario = new Usuario();
		this.usuario.setLogin("Joã„o");
		// this.usuario.setIp(recuperaIP());
		retorno = entrar();
		return retorno;
	}

	public String callMeMaria() {
		String retorno = "";
		this.usuario = new Usuario();
		this.usuario.setLogin("Maria");
		// this.usuario.setIp(recuperaIP());
		retorno = entrar();
		return retorno;
	}

	public String entrar() {
		//this.usuarios.add(this.usuario);
		for (Video v: videos){
			System.out.println(">>Arquivo de Video: "+v.arquivo);
			System.out.println(">>Disponibilidade do video: "+v.isDisponivel);
			System.out.println(">>Url video: "+v.urlYoutube);
			System.out.println(">>Esta sendo executado: "+v.isPlaying);
			if(v.isPlaying()){
				this.videoAtual = v;
				System.out.println("Video atual: "+videoAtual.arquivo);
			}
			
			
		}
		navegacaoBean.gravarPaginaConvite(Navegacao.SALA);
		System.out.println("Video atual: "+videoAtual.urlYoutube);
		return "sala?faces-redirect=true";
	}

	// Recupera o IP da maquina local. Parametro necessáario para a
	// geolocalização.
	public String recuperaIP() {
		try {
			InetAddress end = InetAddress.getLocalHost();
			return end.getHostAddress();
		} catch (UnknownHostException uhex) {
			return "N/A";
		}

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Video> getVideos() {
		return videos;
	}

	public void setVideos(ArrayList<Video> videos) {
		this.videos = videos;
	}

	/*
	 * public String listaUsuario(){
	 * 
	 * // String resultado = null;
	 * 
	 * / if (this.usuario.login == null) { HttpSession session = (HttpSession)
	 * FacesContext .getCurrentInstance().getExternalContext()
	 * .getSession(false);
	 * this.usuario.setLogin((String)session.getAttribute("login"));
	 * this.resultado = dao.buscarPorId(convite.getId());
	 * session.setAttribute("listaUsuarios", this.resultado); } } return
	 * convite; }
	 */
	
	
	public String selectVideo(Video videoClicado){
		
		Video videoAnterior = new Video();
		
		for(Video video : videos){
			if(video.isPlaying()){
				videoAnterior = video;
			}
		}
		
		if(videoClicado.isDisponivel){
			if(videoClicado.nome.equalsIgnoreCase("Cadastro")){
				return "cadastrar?faces-redirect=true";
			}
			videoClicado.setPlaying(true);
			videoClicado.setVisited(true);
			videoAnterior.setPlaying(false);
			System.out.println(">>>Percentual assistido do video anterior: "+videoAnterior.percentualAssistido);
			this.videoAtual = videoClicado;
		}
		
		return "sala";
	}

	public Video getVideoAtual() {
		return videoAtual;
	}

	public void setVideoAtual(Video videoAtual) {
		this.videoAtual = videoAtual;
	}
	
	
	
	public String liberarProximosVideos(){
		String retorno = null;
		
		for(Video v : videoAtual.proximosVideos){
			v.setDisponivel(true);
		} 
		
		if(videoAtual.getCodigo().equalsIgnoreCase("vd_1_3") || videoAtual.getCodigo().equalsIgnoreCase("vd_2_4")){
			retorno = "cadastrar?faces-redirect=true";
		}
		
		return retorno;
	}
	

	public static void main(String[] args) {
		ArrayList< ArrayList<String> > multi = new ArrayList<ArrayList<String>>();

		ArrayList<String> item = new ArrayList<String>();
		item.add("yesus");

		multi.add(item);
	}
	
}
