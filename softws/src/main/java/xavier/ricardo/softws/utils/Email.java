package xavier.ricardo.softws.utils;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

	public static void envia(String from, String to, String username, String password, String subject, String content,
			String filename) {

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtpi.kinghost.net");
		props.put("mail.smtp.auth", "true");
		/*
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        */		
		/**/
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		//props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		/**/
		

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			String[] destinatarios = to.split(";");
			for (String d : destinatarios) {
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(d));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(content);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		String from = "remoto@softp.com.br";
		String to = "ricardo.costa.xavier@gmail.com";
		String username = "remoto@softp.com.br";
		String password = "soft101010";
		String subject = "teste";
		String content = "pom.xml";
		String filename = "pom.xml";
		Email.envia(from, to, username, password, subject, content, filename);
	}

}
