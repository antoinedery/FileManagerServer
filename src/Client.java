import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

	private static Socket socket;
	private static Path currentDirectory = Paths.get("").toAbsolutePath();

	/**
	 * main function that runs the whole client program
	 */
	public static void main(String[] args) throws IOException {

		/*---------------SAISIE DES ADRESSES IP ET PORTS-------------- */
		Scanner input = new Scanner(System.in);

		String ipAddress;
		String portNumber;
		boolean ipAddressIsValid = false;
		boolean portNumberIsValid = false;

		do {
			System.out.print("(Client) Saisir l'adresse IP : ");
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
				System.out.println("Numero de port invalide.");

		} while (!portNumberIsValid);

		int serverPort = Integer.parseInt(portNumber);

		socket = new Socket(ipAddress, serverPort);

		System.out.format("Connexion avec le serveur établie à l'adresse %s:%d%n", ipAddress, serverPort);

		/*-----------------SAISIE DES COMMANDES-------------- */
		String commandInput;
		String[] command;

		do {
			commandInput = input.nextLine();
			command = commandInput.split(" ");

			if (!Validator.validateCommand(command))
				System.out.println("Commande invalide.");

			else {
				if (command[0].equals("upload")) {
					uploadFile(command, socket);

				}

				else if (command[0].equals("download"))
					downloadFile(command, socket);

				else {
					transmitStringToServer(commandInput, socket);
					receiveStringFromServer(socket);
				}
			}

		} while (!command[0].equals("exit"));

		socket.close();
		input.close();
	}

	/**
	 * send a file from the current client to the server
	 * 
	 * @param command : array of string containing the 'upload' command and the file name
	 * @param socket  : current socket
	 */
	private static void uploadFile(String[] command, Socket socket) throws IOException {
		File file = new File(currentDirectory.toString() + "\\" + command[1]);
		if (Validator.validateFile(file)) {
			int fileSize = (int) file.length();

			transmitStringToServer(command[0] + " " + command[1] + " " + String.valueOf(fileSize), socket);

			FileInputStream fis = new FileInputStream(file);

			byte[] buffer = new byte[fileSize];
			buffer = fis.readAllBytes();

			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out = new DataOutputStream(socket.getOutputStream());
			out.write(buffer);

			fis.close();

			receiveStringFromServer(socket);
		} else
			System.out.println("Erreur : Le fichier " + command[1] + " est introuvable.");
	}

	/**
	 * Download a file form the server to the local directory of client
	 * 
	 * @param command : array of string containing the 'download' command and the file name
	 * @param socket  : current socket
	 */
	private static void downloadFile(String[] command, Socket socket) throws IOException {
		transmitStringToServer(command[0] + " " + command[1], socket);

		DataInputStream feedbackFile = new DataInputStream(socket.getInputStream());
		String validFile = feedbackFile.readUTF();

		if (!validFile.equals("false")) {
			DataInputStream inSize = new DataInputStream(socket.getInputStream());
			String fileSize = inSize.readUTF();

			DataInputStream in = new DataInputStream(socket.getInputStream());
			FileOutputStream fos = new FileOutputStream(command[1]);
			int size = Integer.parseInt(fileSize);
			byte[] buffer = new byte[size];

			in.readFully(buffer);
			fos.write(buffer);

			fos.close();

			System.out.println("Le fichier " + command[1] + " a bien été téléchargé.");
		} else
			receiveStringFromServer(socket);
	}

	/**
	 * Transmit a string to the server
	 * 
	 * @param string : string to transmit
	 * @param socket : current socket
	 */
	private static void transmitStringToServer(String string, Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out = new DataOutputStream(socket.getOutputStream());
		out.writeUTF(string);
	}

	/**
	 * Read and display a string sent by the server
	 * 
	 * @param socket : current socket
	 */
	private static void receiveStringFromServer(Socket socket) throws IOException {
		DataInputStream in = new DataInputStream(socket.getInputStream());
		String feedBackFromServer = in.readUTF();
		System.out.println(feedBackFromServer);
	}

}