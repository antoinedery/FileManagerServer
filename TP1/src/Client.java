import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

	private static Socket socket;
	private static Path currentDirectory = Paths.get("").toAbsolutePath();

	/**
	 * main function that runs the whole client program
	 */
	public static void main(String[] args) throws Exception {
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
				System.out.print("Numero de port invalide\n");

		} while (!portNumberIsValid);

		int serverPort = Integer.parseInt(portNumber);

		socket = new Socket(ipAddress, serverPort);

		System.out.format("Connexion avec le serveur établie à l'adresse %s:%d%n", ipAddress, serverPort);
		
		String commandInput;
		String[] command;

		do {
			commandInput = input.nextLine();
			command = commandInput.split(" ");

			if (!(command[0].equals("mkdir") || command[0].equals("cd") || command[0].equals("upload")
					|| command[0].equals("download") || command[0].equals("ls") || command[0].equals("exit")))
				System.out.println("Commande invalide.");

			else {
				
				/*Faire une classe similaire a ServerCommands pour le client?? (pour alleger le code)*/
				if (command[0].equals("upload")) {
					File file = new File(currentDirectory.toString() + "\\" + command[1]);
					int fileSize = (int) file.length();

					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(commandInput + " " + String.valueOf(fileSize));

					FileInputStream fis = new FileInputStream(file);
					byte[] buffer = new byte[fileSize];
					
					buffer = fis.readAllBytes();
					out.write(buffer);

					fis.close();
				}

				else {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(commandInput);

				}
				//Recevoir le stream du serveur
				DataInputStream in = new DataInputStream(socket.getInputStream());
				String feedBackFromServer = in.readUTF();
				System.out.println(feedBackFromServer);
			}

		} while (!command[0].equals("exit"));

		socket.close();
		input.close();
	}

}
