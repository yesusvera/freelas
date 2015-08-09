package br.com.eudiamante.servlets;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.util.SendMail;
import br.com.eudiamante.util.Sh1Util;

public class ValidarConviteServlet extends HttpServlet {

	private static final long serialVersionUID = 120789158431318451L;

	private FacesContextFactory facesContextFactory;
	private Lifecycle lifecycle;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		lifecycle = lifecycleFactory
				.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		FacesContext context = facesContextFactory.getFacesContext(
				getServletContext(), request, response, lifecycle);

		String hashValidacao = request.getPathInfo();

		System.out.println("usuarioGeraConvite -->" + hashValidacao);
		if (hashValidacao != null) {
			hashValidacao = hashValidacao.replace("/", "");
			ConviteDAO conviteDAO = new ConviteDAO();
			Convite convite = conviteDAO
					.getConvitePorHashValidacao(hashValidacao);
			if (convite != null) {
				/** TODO EFETUAR VALIDACAO COM DATA DE EXPIRACAO */
				System.out.println(convite);
				convite.setAtivo(true);
				convite.setHashAcesso(Sh1Util.criptografaSHA1(convite
						.getHashValidacao() + "EUDIAMANTE_2013"));
				conviteDAO.gravar(convite);

				String link = "";
				if (context.getCurrentInstance().getExternalContext()
						.getRequestServerName().contains("localhost")) {
					link = "http://localhost:8080/euDiamante/convite/"
							+ convite.getHashAcesso();
				} else {
					link = "http://www.eudiamante.com/convite/"
							+ convite.getHashAcesso();
				}

				SendMail sendMail = new SendMail();
				sendMail.sendMail(
						"euDiamante.com",
						convite.getEmailEnvioConvite(),
						"Você já pode acessar a euDiamante.com",
						"<html><body>Olá, entre na nossa plataforma a partir de qualquer dispositivo (computador, celular, tablet...) com este link <a href='"
								+ link + "'> " + link + "</a> </body></html>");

				context.getCurrentInstance()
						.addMessage(
								"mensagemServlet",
								new FacesMessage(
										"O Convite está validado. Acabamos de mandar um link no seu email "
												+ convite
														.getEmailEnvioConvite()
												+ " para que você possa acessar nossa plataforma, aguarde uns instantes..."));
			} else {
				context.addMessage("mensagemServlet", new FacesMessage(
						"Não foi possível encontrar este convite..."));
			}

			response.sendRedirect(request.getContextPath()
					+ "/retornoMensagem.xhtml");
		}
	}
}