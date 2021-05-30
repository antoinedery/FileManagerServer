import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;

public class Commands {

	private Path currentDirectory;

	/**
	 * Default constuctor of Commands class which allows each client to have their own commands
	 */
	public Commands() {
		currentDirectory = Paths.get("").toAbsolutePath();
	}

	/**
	 * Change directory to directoryName (cd command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'cd' command)
	 */
	public void changeDirectory(String directoryName, Socket socket) {
		File tempDirectoryName = new File(currentDirectory.toString() + "\\" + directoryName);
		
		if (directoryName.equals("..")) {
			currentDirectory = currentDirectory.getParent();
			transmitStringToClient("Vous êtes dans le dossier '" + currentDirectory.toFile().getName() + "'.", socket);
		}

		else if (tempDirectoryName.isDirectory()) {
			currentDirectory = Paths.get(directoryName).toAbsolutePath();
			transmitStringToClient("Vous êtes dans le dossier '" + directoryName + "'.", socket);
		}

		else
			transmitStringToClient("Le dossier '" + directoryName + "' n'existe pas.", socket);
	}

	/**
	 * Send the list of files in current directory (ls command)
	 * 
	 * @param socket : current client (who called the 'ls' command)
	 */
	public void listFiles(Socket socket) {
		File currentDirectoryFile = currentDirectory.toFile(); // Recuperer le dossier actuel
		String directoryFilesList = "";

		if(currentDirectoryFile.listFiles().length != 0) {
			for (File file : currentDirectoryFile.listFiles()) {
	
				if (file.isDirectory())
					directoryFilesList += "[Folder] " + file.getName() + '\n';
	
				else if (file.isFile())
					directoryFilesList += "[File] " + file.getName() + '\n';
	
				else
					directoryFilesList += file.getName() + '\n';
			}
			transmitStringToClient(directoryFilesList, socket);
		}
		else
			transmitStringToClient("Ce répertoire ne contient aucun dossier/fichier.", socket);	
	}

	/**
	 * Create a new folder in current directory (mkdir command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'mkdir' command)
	 */
	public void makeDirectory(String directoryName, Socket socket) {
		File folder = new File(currentDirectory.toString() + "\\" + directoryName);
		
		if (folder.mkdir()) // mkdir retourne vrai si le dossier est crée
			transmitStringToClient("Le dossier '" + directoryName + "' a été créé avec succès.", socket);

		else
			transmitStringToClient("Le dossier '" + directoryName + "' existe déjà.", socket);

	}
	
	// TODO : public static void uploadFile()
	public void uploadFile(String fileName, String fileSize, Socket socket) throws IOException
	{
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buffer = new byte[4096];
				
		int filesize = Integer.parseInt(fileSize); // Send file size in separate msg
		System.out.println("file size : " + filesize);
		int read = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			remaining -= read;
			fos.write(buffer, 0, read);
		}
		System.out.println("Done");
		fos.close();
	}
	
	
	// TODO : public static void downloadFile()

	/**
	 * Transmit a string to the current client
	 * 
	 * @param message : string to transmit
	 * @param socket  : current client
	 */
	public void transmitStringToClient(String message, Socket socket) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Erreur : " + e);
		}
	}

}
