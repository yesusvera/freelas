package br.com.iejb.sgi.service;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailService{
	
	private static final String USER_NAME_EMAIL = "apgomes@stefanini.com";
	private static final String PASSWORD_EMAIL = "Alvarex1!";
	

	public void enviaEmail(String destinatarios, String titulo, String corpo){
		Session session = geraConfiguracoesEmail();

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USER_NAME_EMAIL)); // Remetente.

			Address[] toUser = InternetAddress.parse(destinatarios);  // Destinatários

			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject(titulo); //Assunto
			message.setText(corpo);
			
			/**Método para enviar a mensagem criada*/
			Transport.send(message);

			System.out.println("Email Enviado!!!");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	protected Session geraConfiguracoesEmail() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(USER_NAME_EMAIL, PASSWORD_EMAIL);
			}
		});

		/** Ativa Debug para sessão */
		session.setDebug(true);
		return session;
	}
	
	protected Session geraConfiguracoesEmailGmail() {
		Properties props = new Properties();
		/** Parâmetros de conexão com servidor Gmail */
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(USER_NAME_EMAIL, PASSWORD_EMAIL);
			}
		});
		
		/** Ativa Debug para sessão */
		session.setDebug(true);
		return session;
	}
}

