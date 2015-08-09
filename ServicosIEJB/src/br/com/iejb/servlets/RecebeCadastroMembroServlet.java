package br.com.iejb.servlets;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.iejb.model.Membro;
import br.com.iejb.service.RecebeCadastroService;

/**
 * Servlet implementation class RecebeCadastroMembro
 */
@WebServlet("/RCadMem")
@Named
public class RecebeCadastroMembroServlet extends HttpServlet implements Serializable {
	private static final long serialVersionUID = 1L;
       
	private static HttpServletRequest request;
	
	@Inject
	private RecebeCadastroService recebeCadastroService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecebeCadastroMembroServlet() {
        super();
    }
    
    public static String getString(String keyParameter){
    	if(request!=null && request.getParameter(keyParameter)!=null)
    		return request.getParameter(keyParameter);
    	else
    		return "";
    }
    
    public static Date getDate(String keyParameter){
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
		
		String user = getString("usuario");
		String pass = getString("senha");
		
		StringBuffer strResp = new StringBuffer();
		
		strResp.append("<?xml version=\"1.0\"?>\n<iejbCadMemb>");
		
		
		if(user.equals("iejb2014") && pass.equals("iejb%membro%2014")){
			
			Membro membro = new Membro();
			membro.setCpf(getString("cpf"));
			membro.setNome(getString("nome"));
			membro.setDataNascimento(getDate("datanascimento"));
			
			recebeCadastroService.salvarMembro(membro);
			
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("YESUS ALFONSINO CASTILLO VERA"));
	}
	
}
