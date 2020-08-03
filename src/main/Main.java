package main;

import java.util.Base64;

public class Main {
	
	// Write here a valid email and password
	// This program will send a test email to that same address.
	// The gmail account may need to activate access to less secure apps
	static final String senderMail = "";
	static final String password = "";
	
	public static void main(String[] args) {
		
		if(senderMail.isEmpty() || password.isEmpty())
			throw new IllegalArgumentException("Debes escribir un email y contraseña válidos.");
		
		String encodedMail = Base64.getEncoder().encodeToString(senderMail.getBytes());
	    String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
		
		SmtpSender smtpSender = new SmtpSender();
		
		// change last parameter to change where the email will be sent
		smtpSender.sendMail(encodedMail, encodedPassword, senderMail);
	}
}
