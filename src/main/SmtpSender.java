package main;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class SmtpSender {
	
	private final String SMTP_SERVICE = "smtp.gmail.com";
	private final int PORT = 465;

	private SSLSocket socket;
	
	public void sendMail(String encodedUser, String encodedPassword, String receiverMail) {
		
		SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        try{
        	System.out.println("Conectar...");
        	socket = (SSLSocket)factory.createSocket(SMTP_SERVICE, PORT);
        	System.out.println("Socket opened successfully.");
        	
        	// Create a Buffered Reader
            InputStream inStream = socket.getInputStream();
            InputStreamReader inReader = new InputStreamReader(inStream);
            BufferedReader bReader = new BufferedReader(inReader);
            
            // Read Server's Greeting -> must receive code 220
            String serverResponse = bReader.readLine();
            System.out.println("Server Greeting: " + serverResponse);           
            if(!serverResponse.startsWith("220")){
                System.out.println("220 code error.");
            }
            
            // Reference socket's output stream
            OutputStream oStream = socket.getOutputStream();
            
            // Send HELO command -> must receive code 250
            String heloCommand = "HELO " + SMTP_SERVICE + "\r\n";
            oStream.write(heloCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("HELO Response: " + serverResponse);
            if(!serverResponse.startsWith("250")) {
                System.out.println("250 code error.");
            }
            
            // Request login -> must receive code 334
            String authCommand = "AUTH LOGIN\r\n";
            oStream.write(authCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("Authentication request response: " + serverResponse);
            if(!serverResponse.startsWith("334")) {
                System.out.println("334 code error when authenticating.");
            }
            
            // Send user and password -> must receive code 
            oStream.write(encodedUser.getBytes());
            oStream.write("\r\n".getBytes());
            serverResponse = bReader.readLine();
            System.out.println("User response: " + serverResponse);
            oStream.write(encodedPassword.getBytes());
            oStream.write("\r\n".getBytes());
            serverResponse = bReader.readLine();
            System.out.println("Password response: " + serverResponse);
            if(!serverResponse.startsWith("235")) {
            	System.out.println("235 user and password code error.");
            	throw new IllegalArgumentException();
            }
            
            // Mail from command -> must receive code 250
            String mailFromCommand = "MAIL FROM:< " + Base64.decode(encodedUser) + ">\r\n";
            oStream.write(mailFromCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("MAIL FROM response: " + serverResponse);
            if(!serverResponse.startsWith("250")) {
            	System.out.println("250 code error.");
            }
            
            // RCPT command -> must receive code 250
            String rcptCommand = "RCPT TO:<" + receiverMail + ">\r\n";
            oStream.write(rcptCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("RCPT TO response: " + serverResponse);
            if(!serverResponse.startsWith("250")) {
            	System.out.println("250 code error.");
            }
            
            // Verify receiver
            String verifyCommand = "VRFY < " + receiverMail + ">\r\n";
            oStream.write(verifyCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("Verify response: " + serverResponse);
            if(!serverResponse.startsWith("252")) {
            	System.out.println("252 code error.");
            }
            
            // DATA command -> must receive code 354
            String dataCommand = "DATA\r\n";
            oStream.write(dataCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("DATA response: " + serverResponse);
            if(!serverResponse.startsWith("354")) {
            	System.out.println("354 code error.");
            }
            
            // Send all the data
            String subject = "Subject: Actividad Aula Mentor\r\n";
            String date = "Date: 08/07/2020\r\n";
            String message = "bla bla bla bla bla\r\n";
            oStream.write(subject.getBytes());
            oStream.write(date.getBytes());
            oStream.write(message.getBytes());
            oStream.write(".\r\n".getBytes());
            serverResponse = bReader.readLine();
            System.out.println("END DATA response: " + serverResponse);
            if(!serverResponse.startsWith("250")) {
            	System.out.println("250 code error.");
            }
            
            // Quit command -> must receive code 221
            String closeCommand = "QUIT\r\n";
            oStream.write(closeCommand.getBytes());
            serverResponse = bReader.readLine();
            System.out.println("QUIT response: " + serverResponse);
            if(!serverResponse.startsWith("221")) {
            	System.out.println("221 code error.");
            }
        	
        }catch(UnknownHostException ex) {
        	System.out.println(ex);
        }catch(IOException ex) {
        	System.out.println(ex);
        }
	}

}
