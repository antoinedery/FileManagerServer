import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;

public class Commands {

	private static Path currentDirectory = Paths.get("").toAbsolutePath();

	/**
	 * Change directory to directoryName (cd command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'cd' command)
	 */
	public static void changeDirectory(String directoryName, Socket socket) {
		if (directoryName.equals("..")) {
			currentDirectory = currentDirectory.getParent();
			transmitStringToClient("Vous êtes dans le dossier '" + currentDirectory.toFile().getName() + "'.", socket);
		}

		else if (Files.exists(Paths.get(directoryName).toAbsolutePath())) {
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
	public static void listFiles(Socket socket) {
		File currentDirectoryFile = currentDirectory.toFile(); // Recuperer le dossier actuel
		String directoryFilesList = "";

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

	/**
	 * Create a new folder in current directory (mkdir command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'mkdir' command)
	 */
	public static void makeDirectory(String directoryName, Socket socket){
		File folder = new File(currentDirectory.toString() + "\\" + directoryName);

		if (folder.mkdir()) // mkdir retourne vrai si le dossier est crée
			transmitStringToClient("Le dossier '" + directoryName + "' a été créé avec succès.", socket);

		else
			transmitStringToClient("Le dossier '" + directoryName + "' existe déjà.", socket);
		
 	}

	// TODO : public static void uploadFile() 
	// TODO : public static void downloadFile()

	//À enlever - seulement pour debugger
	public static void printPath(Socket socket)
	{
		transmitStringToClient(currentDirectory.toString(), socket);
	}
	
	/**
	 * Transmit a string to the current client
	 * 
	 * @param message : string to transmit
	 * @param socket  : current client
	 */
	public static void transmitStringToClient(String message, Socket socket) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Erreur : " + e);
		}
	}
	
	
}
