package br.com.iejb.sgi.web.servicos;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.iejb.sgi.domain.Cidade;
import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.domain.Usuario;
import br.com.iejb.sgi.service.cadastrarMembro.CadastrarMembroServiceRemote;
import br.com.iejb.sgi.util.HashConfirmacaoMembro;
import br.com.iejb.sgi.util.SendMail;

/**
 * @author YESUS CASTILLO VERA
 * Servlet implementation class RecebeCadastroMembro
 */
@WebServlet("/RCadMem")
@ManagedBean
@Named
public class RecebeCadastroMembroServlet extends HttpServlet implements Serializable {
	private static final long serialVersionUID = 1L;
       
	private HttpServletRequest request;
	
	@EJB private CadastrarMembroServiceRemote cadastrarMembroService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecebeCadastroMembroServlet() {
        super();
    }
    

    public  Long getLong(String keyParameter){
    	String str = getString(keyParameter);
    	try{
    		return Long.parseLong(str);
    	}catch(NumberFormatException nfe){
    		return null;
    	}
    }

    public Integer getInt(String keyParameter){
    	String str = getString(keyParameter);
    	try{
    		return Integer.parseInt(str);
    	}catch(NumberFormatException nfe){
    		return null;
    	}
    }
    
    public Byte getByte(String keyParameter){
    	String str = getString(keyParameter);
    	try{
    		return Byte.parseByte(str);
    	}catch(NumberFormatException nfe){
    		return null;
    	}
    }
    
    public  String getString(String keyParameter){
    	if(request!=null && request.getParameter(keyParameter)!=null)
    		return request.getParameter(keyParameter);
    	else
    		return "";
    }
    
    public Date getDate(String keyParameter){
    	String dataTmp = getString(keyParameter);
    	SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
    	try {
			return sdf.parse(dataTmp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.request = request;
		
		response.setHeader("Content-Type", "application/xml; charset=UTF-8");
		
		String user = getString("usrCadServ");
		String pass = getString("passCadServ");
		
		StringBuffer strResp = new StringBuffer();
		
		strResp.append("<?xml version=\"1.0\"?>\n<iejbCadMemb>");
		
		
		if(user.equals("iejb2014") && pass.equals("iejb%membro%2014")){
			
			Membro membro = new Membro();
			membro.setAtivo(false);
			
			membro.setNome(getString("nome"));
			membro.setNomeConjuge(getString("nomeConjuge"));
			membro.setCpf(getString("cpf"));
			membro.setSexo(getString("sexo"));
			membro.setDataNascimento(getDate("dataNascimento"));
			membro.setTipoSanguineo(getString("tipoSanguineo"));
			membro.setEmail(getString("email"));
			
			//Endereço
			membro.setEndereco(getString("endereco"));
			membro.setBairro(getString("bairro"));
			
			if(getLong("idCidade")!=null){
				Cidade cidade = new Cidade(getLong("idCidade"));
				membro.setCidade(cidade);
			}
			
			membro.setCep(getString("cep"));
			membro.setTelefoneFixo(getString("telefoneFixo"));
			membro.setTelefoneCelular(getString("telefoneCelular"));
			membro.setTelefoneComercial(getString("telefoneComercial"));
			
			//Login
			Usuario usuario = new Usuario(false);
			usuario.setLogin(getString("login"));
			usuario.setSenha(getString("senha"));
			
			usuario.setMembro(membro);
			membro.setUsuario(usuario);

			cadastrarMembroService.salvarMembro(membro);
			
			enviarEmailConfirmacao(membro, request);
			
			System.out.println("Gravando na base de dados...");
			System.out.println(membro);
			
			strResp.append("\n<erro>false</erro>");
			strResp.append("\n<mensagem>Cadastrado com sucesso!</mensagem>");
		}else{
			strResp.append("\n<erro>true</erro>");
			strResp.append("\n<mensagem>Erro de autenticação</mensagem>");
		}
		
		strResp.append("\n</iejbCadMemb>");
		response.getWriter().print(strResp);
	}

	
	public void enviarEmailConfirmacao(Membro membro, HttpServletRequest request) {
		
		StringBuffer corpoEmail = new StringBuffer();
		
		HashConfirmacaoMembro hashConfirmacaoMembro = new HashConfirmacaoMembro(membro);
		
		String link = "http://";
		link += request.getRemoteHost();
		if(request.getLocalPort()!=80){
			link+=":"+request.getLocalPort();
		}
		
		corpoEmail
			.append("Prezado(a) <b>").append(membro.getNome()).append("</b>,<br>")
			.append("Recebemos um cadastro indicando este email e precisamos da validação do mesmo, para isto basta clicar no link abaixo: <br>")
			.append(link  + request.getContextPath() + "/validacaoCadastro/" + hashConfirmacaoMembro.geraHashConfirmacao())
			.append("<br> Obrigado.");
		
		new SendMail().sendMail("iejb.desenvolvimento@gmail.com", membro.getEmail(), "Cadastro de membro - IEJB", corpoEmail.toString());
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("yesusvera@gmail.com"));
	}
	
}
