import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
											
		System.out.format("The server is running on %s:%d%n", ipAddress, serverPort);
		
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
			System.out.println("New connection with client#" + clientNumber + " at " + socket);
		}
		
		public void run()
		{
			try
			{
				String commandFromClient;
				do {
					DataInputStream in = new DataInputStream(socket.getInputStream());
					commandFromClient = in.readUTF();
					
					//System.out.println(commandFromClient);
					
					runCommand(commandFromClient);
					
									
				} while(!commandFromClient.equals("exit"));
			} catch (IOException e)
			{
				System.out.println("Error handling client#" + clientNumber + ": " + e);	
			}
			finally
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					System.out.println("Could not close a socket");
				}
				System.out.println("Connection with client# " + clientNumber + " closed");
			}
		}
	}
	
	private static void runCommand(String commandInput)
	{
		String[] command = commandInput.split(" ");
		
		switch(command[0])
		{
		case "cd":
			System.out.println("Change directory to " + command[1]);
			break;
			
		case "ls":
			System.out.println("List of files ");
			break;
			
		case "mkdir":
			System.out.println("Create new directory named " + command[1]);
			break;
			
		case "upload":
			System.out.println("Upload new file named " + command[1]);
			break;
			
		case "download":
			System.out.println("Download file named " + command[1]);
			break;
		}
		
	}

}


