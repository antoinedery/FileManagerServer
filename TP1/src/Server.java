import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

public class Server {

	private static ServerSocket listener;

	/**
	 * main function that runs the whole server program
	 */
	public static void main(String[] args) throws Exception {
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

			if (!ipAddressIsValid)
				System.out.print("Adresse IP invalide\n");

		} while (!ipAddressIsValid);

		do {
			System.out.print("Saisir le port du serveur : ");
			portNumber = input.nextLine();

			portNumberIsValid = Validator.validatePortNumber(portNumber);

			if (!portNumberIsValid)
				System.out.print("Numero de port invalide\n");

		} while (!portNumberIsValid);

		input.close();

		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(ipAddress);

		int serverPort = Integer.parseInt(portNumber);

		listener.bind(new InetSocketAddress(serverIP, serverPort));

		System.out.format("Le serveur est connect� � l'adresse %s:%d%n", ipAddress, serverPort);

		try {

			while (true) {
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;
		private Commands clientCommand;

		public ClientHandler(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			clientCommand = new Commands();
			System.out.println("Nouvelle connexion du client #" + clientNumber + " � l'adresse " + socket);
		}

		public void run() {
			try {
				String commandFromClient;
				
				String[] command;
				
				do {
					DataInputStream in = new DataInputStream(socket.getInputStream());
					commandFromClient = in.readUTF();

					command = commandFromClient.split(" ");

					if ((command[0].equals("mkdir") || command[0].equals("cd") || command[0].equals("upload")
							|| command[0].equals("download") || command[0].equals("ls") || command[0].equals("exit")))
						runCommand(commandFromClient, clientCommand, socket);

					
					
					
				} while (!commandFromClient.equals("exit"));
			} catch (IOException e) {
				System.out.println("Erreur avec le client #" + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
					System.out.println("La connexion avec le client #" + clientNumber + " a �t� ferm�e.");
				} catch (IOException e) {
					System.out.println("Le socket ne peut �tre ferm�.");
				}
			}
		}
	}

	/**
	 * Execute a block of code depending of the command called by the current client
	 * 
	 * @param commandInput : the command to be executed
	 * @param socket       : current client
	 */
	private static void runCommand(String commandInput, Commands commands, Socket socket) throws IOException {
		String[] command = commandInput.split(" ");

		switch (command[0]) {
		case "cd":
			commands.changeDirectory(command[1], socket);
			break;

		case "ls":
			commands.listFiles(socket);
			break;

		case "mkdir":
			commands.makeDirectory(command[1], socket);
			break;

		case "upload":
			commands.uploadFile(command[1], command[2], socket);
			break;

		case "download":
			// TODO
			break;

		case "exit":
			commands.transmitStringToClient("Vous avez �t� d�connect� avec succ�s.", socket);
			break;
		}
		displayCommand(command, socket);
	}

	/**
	 * Display the client information and the command called
	 * 
	 * @param command : command called by the current client
	 * @param socket  : current client
	 */
	private static void displayCommand(String[] command, Socket socket) {
		String address = socket.getLocalAddress().toString().substring(1); // Enlever le '/' au debut de l'adresse IP
		int port = socket.getPort();
		LocalTime timeNow = LocalTime.now().truncatedTo(ChronoUnit.SECONDS); // Sinon affiche nanosecondes
		
		if(command.length == 1)
			System.out.println("[" + address + ":" + port + " - " + LocalDate.now() + "@" + timeNow + "] : " + command[0]);
		else
			System.out.println("[" + address + ":" + port + " - " + LocalDate.now() + "@" + timeNow + "] : " + command[0] + " " + command[1]);
	}
}

