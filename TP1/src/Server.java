import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;

public class Server {
	
	private static ServerSocket listener;
	
	public static void main(String[] args) throws Exception
	{
		Scanner input = new Scanner(System.in);
		
		String ipAddress;
		String portNumber;
		boolean ipAddressIsValid = false;
		boolean portNumberIsValid = false;
		int clientNumber = 0;
		
		do {
			System.out.print("(Serveur) Saisir l'adresse IP : ");
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
		
		input.close();
		
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(ipAddress);
		
		int serverPort = Integer.parseInt(portNumber);
		
		listener.bind(new InetSocketAddress(serverIP, serverPort));
											
		System.out.format("Le serveur fonctionne sur l'adresse %s:%d%n", ipAddress, serverPort);
		
		try {
			
			while(true)
			{
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		}
		finally {
			listener.close();
		}
	}
	
	private static class ClientHandler extends Thread
	{
		private Socket socket;
		private int clientNumber;
		
		public ClientHandler(Socket socket, int clientNumber)
		{
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("Nouvelle connexion du client #" + clientNumber + " à l'adresse " + socket);
		}
		
		public void run()
		{
			try
			{
				String commandFromClient;
				do {
					DataInputStream in = new DataInputStream(socket.getInputStream());
					commandFromClient = in.readUTF();
									
					runCommand(commandFromClient, socket);
								
				} while(!commandFromClient.equals("exit"));
			} catch (IOException e)
			{
				System.out.println("Erreur avec le client #" + clientNumber + ": " + e);	
			}
			finally
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					System.out.println("Le socket ne peut être fermé");
				}
				System.out.println("La connexion avec le client # " + clientNumber + " a été fermée.");
			}
		}
	}
	
	/*Fonction runCommand qui fait un switch case selon la commande reçu du client*/
	private static void runCommand(String commandInput, Socket socket)
	{
		String[] command = commandInput.split(" ");
		
		switch(command[0])
		{
		case "cd":
			System.out.println("Change directory to " + command[1] + " (Not yet implemented)");
			break;
			
		case "ls":
			System.out.println("List of files " + " (Not yet implemented)");
			break;
			
		case "mkdir":
			createDirectory(command[1], socket);
			break;
			
		case "upload":
			System.out.println("Upload new file named " + command[1] + " (Not yet implemented)");
			break;
			
		case "download":
			System.out.println("Download file named " + command[1] + " (Not yet implemented)");
			break;
		}
	}
	
	/*Fonction createDirectory utilisée avec le case mkdir*/
	private static void createDirectory(String directoryName, Socket socket)
	{
		File folder = new File(directoryName);
		
		if(folder.exists())
			sendToClient("Le dossier " + directoryName + " existe déjà.", socket);
		
		else if(folder.mkdir()) 
			sendToClient("Le dossier " + directoryName + " a été créé avec succès.", socket);
		
		else 
			sendToClient("Le dossier " + directoryName + " existe déjà.", socket);
		
	}
	
	/*Voir si on la met dans une autre classe pour utiliser avec Client aussi*/
	/*Fonction sendToClient pour envoyé un message au client)*/
	private static void sendToClient(String message, Socket socket)
	{
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e)
		{
			System.out.println("Erreur : " + e);	
		}
	}
}


