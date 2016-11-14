package com.users.service;

import static javax.mail.Message.RecipientType.TO;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.users.beans.Email;
import com.users.security.PermissionService;

@Service
public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	private final String username = "";
	private final String password = "";

	private Properties props;
	private Authenticator auth;

	@Autowired
	private PermissionService permissionService;
	public EmailService() {
		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};
	}

	public boolean sendMessage(Email email) {
		Session session = Session.getInstance(props, auth);
		Message message = new MimeMessage(session);
		try {
			
			message.setRecipient(TO, new InternetAddress(email.getTo()));
			message.setReplyTo(
					new Address[] { new InternetAddress(permissionService.getCurrentEmail()) });
			message.setSubject(email.getSubject());
			message.setText(email.getCustom() + "\n\n" + email.getMessage());

			Transport.send(message);
		} catch (Exception e) {
			log.error("Unable to send message", e);
			return false;
		}
		return true;
	}
}