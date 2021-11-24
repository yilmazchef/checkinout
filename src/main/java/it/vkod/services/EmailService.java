package it.vkod.services;


import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Component
public class EmailService {

	private final JavaMailSender emailSender;


	public EmailService( final JavaMailSender emailSender ) {

		this.emailSender = emailSender;
	}


	public void sendSimpleMessage( final String to, final String subject, final String text ) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom( "chefsandbox@gmail.com" );
		message.setTo( to );
		message.setSubject( subject );
		message.setText( text );
		emailSender.send( message );
	}


	public void sendMessageWithAttachment( final String to, final String subject, final String text,
	                                       final String pathToAttachment ) throws MessagingException {

		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper( message, true );

		helper.setFrom( "chefsandbox@gmail.com" );
		helper.setTo( to );
		helper.setSubject( subject );
		helper.setText( text );

		FileSystemResource file = new FileSystemResource( new File( pathToAttachment ) );
		helper.addAttachment( "Aanwezigheidslijst", file );

		emailSender.send( message );
	}

}
