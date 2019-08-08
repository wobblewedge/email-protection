package com.email.phisher.email;

import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.*;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.*;

@Component
public class EmailServiceImpl implements EmailService {
	
	@Autowired
	public JavaMailSender emailSender;
	@Override
	public void sendSimpleMessage(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			message.setText(text);
		} catch (MailException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template,
			String... templateArgs) {
		String text = String.format(template.getText(), (Object) templateArgs);
		sendSimpleMessage(to,subject,text);
		
	}

	@Override
	public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
		try {
			  MimeMessage message = emailSender.createMimeMessage();
	            // pass 'true' to the constructor to create a multipart message
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);

	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(text);

	            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
	            helper.addAttachment("Invoice", file);

	            emailSender.send(message);
		}catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
