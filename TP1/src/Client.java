import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.lang.*;
import java.io.ObjectOutputStream;

public class Client {
	
	private static Socket socket;

	public static void main(String[] args) throws Exception
	{
		Scanner input = new Scanner(System.in);  	
		
		String ipAddress;
		String portNumber;
		boolean ipAddressIsValid = false;
		boolean portNumberIsValid = false;
		
		do {
			System.out.print("(Client) Saisir l'adresse IP : ");
			ipAddress = input.nextLine();
			
			ipAddressIsValid = Validator.validateIPAddress(ipAddress);
			
			if(!ipAddressIsValid)
				System.out.print("Adresse IP invalide\n");
			
		} while(!ipAddressIsValid);
				
		do {
			System.out.print("Saisir le port du serveur : ");
			portNumber = input.nextLine();
				
			portNumberIsValid = Validator.validatePortNumber(portNumber);
				
			if(!portNumberIsValid)
				System.out.print("Numero de port invalide\n");
				
		} while(!portNumberIsValid);
		
		
		
		int serverPort = Integer.parseInt(portNumber);
			
		socket = new Socket(ipAddress, serverPort);
				
		System.out.format("Connexion avec le serveur établie à l'adresse %s:%d%n", ipAddress, serverPort);
		
		String commandInput;
		String[] command;
			
		
		do {
			commandInput = input.nextLine();
			command = commandInput.split(" ");
						
			if(!(command[0].equals("mkdir") || 
					command[0].equals("cd") || 
					command[0].equals("upload") || 
					command[0].equals("download") ||
					command[0].equals("ls") ||
					command[0].equals("exit"))) {
				System.out.println("Commande invalide");
			}
			else
			{
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(commandInput);
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				String feedBackFromServer = in.readUTF();
				System.out.println(feedBackFromServer);
			}
			
		} while(!command[0].equals("exit"));
		
		System.out.println("Done");
				
		socket.close();
		input.close();
	}
	
}


