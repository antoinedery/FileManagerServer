import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class Client {

	private static Socket socket;

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
				
					
				if(command[0].equals("upload"))
				{
					/*a changer*/
					File file = new File("C:\\Users\\Antoine\\Desktop\\Coding\\git\\INF3405\\TP1\\monDossier\\test.txt");
					long fileSize = file.length();
					
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(commandInput+ " " +String.valueOf(fileSize));
										
					FileInputStream fis = new FileInputStream(file);
					byte[] buffer = new byte[4096];
					
					while (fis.read(buffer) > 0) {
						out.write(buffer);
					}
					
					fis.close();
				}
				
				else {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(commandInput);
						
					DataInputStream in = new DataInputStream(socket.getInputStream());
					String feedBackFromServer = in.readUTF();
					System.out.println(feedBackFromServer);			
				}
			}

		} while (!command[0].equals("exit"));

		socket.close();
		input.close();
	}

}
