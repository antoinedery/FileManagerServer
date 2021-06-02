import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;

public class ServerCommands {

	private Path currentDirectory;
	private Socket socket;

	/**
	 * Default constuctor of Commands class which allows each client to have their
	 * own commands
	 */
	public ServerCommands(Socket socket) {
		currentDirectory = Paths.get("").toAbsolutePath();
		this.socket = socket;
	}

	/**
	 * Change directory to directoryName (cd command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'cd' command)
	 */
	public void changeDirectory(String directoryName) {
		File tempDirectoryName = new File(currentDirectory.toString() + "\\" + directoryName);

		if (directoryName.equals("..")) {
			currentDirectory = currentDirectory.getParent();
			transmitStringToClient("Vous êtes dans le dossier '" + currentDirectory.toFile().getName() + "'.");
		}

		else if (tempDirectoryName.isDirectory()) {
			currentDirectory = tempDirectoryName.toPath();
			transmitStringToClient("Vous êtes dans le dossier '" + directoryName + "'.");
		}

		else
			transmitStringToClient("Le dossier '" + directoryName + "' n'existe pas.");
	}

	/**
	 * Send the list of files in current directory (ls command)
	 * 
	 * @param socket : current client (who called the 'ls' command)
	 */
	public void listFiles() {
		File currentDirectoryFile = currentDirectory.toFile(); // Recuperer le dossier actuel
		String directoryFilesList = "";

		if (currentDirectoryFile.listFiles().length != 0) {
			for (File file : currentDirectoryFile.listFiles()) {

				if (file.isDirectory())
					directoryFilesList += "[Folder] " + file.getName() + '\n';

				else if (file.isFile())
					directoryFilesList += "[File] " + file.getName() + '\n';

				else
					directoryFilesList += file.getName() + '\n';
			}
			transmitStringToClient(directoryFilesList);
		} else
			transmitStringToClient("Ce répertoire ne contient aucun dossier/fichier.");
	}

	/**
	 * Create a new folder in current directory (mkdir command)
	 * 
	 * @param directoryName : name of the folder to create
	 * @param socket        : current client (who called the 'mkdir' command)
	 */
	public void makeDirectory(String directoryName) {
		File folder = new File(currentDirectory.toString() + "\\" + directoryName);

		if (folder.mkdir()) // mkdir retourne vrai si le dossier est crée
			transmitStringToClient("Le dossier '" + directoryName + "' a été créé avec succès.");

		else
			transmitStringToClient("Le dossier '" + directoryName + "' existe déjà.");

	}

	/**
	 * Read a bytes array (file) from client and create a file in the server
	 * directory
	 * 
	 * @param fileName : name of the file to be created
	 * @param fileSize : size of the file (useful for buffer site)
	 *
	 * Source :
	 * https://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-
	 * byte-over-sockets
	 * Source :
	 * https://stackoverflow.com/questions/28900085/datainputstream-hangs-at-the-end
	 * -of-the-stream
	 */
	public void uploadFile(String fileName, String fileSize) throws IOException {
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(fileName);
		int size = Integer.parseInt(fileSize);
		byte[] buffer = new byte[size];
					
		in.readFully(buffer);
		fos.write(buffer);
		
		transmitStringToClient("Le fichier " + fileName + " a bien été téléversé.");
		fos.close();
	}

	public void DownloadFile(String fileName) throws IOException {
		
		File file = new File(currentDirectory.toString() + "\\" + fileName);
		int fileSize = (int) file.length();
		
		if(Validator.validateFile(file)) {
			transmitStringToClient("true");
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(String.valueOf(fileSize));	//Envoyer la taille du fichier
	
			FileInputStream fis = new FileInputStream(file);
	
			byte[] buffer = new byte[fileSize];
			buffer = fis.readAllBytes();
			out.write(buffer);
			
			fis.close();
		}
		
		else {
			transmitStringToClient("false");
			transmitStringToClient("Erreur: Fichier " + fileName + " introuvable");
		}
	}

	/**
	 * Transmit a string to the current client
	 * 
	 * @param message : string to transmit
	 * @param socket  : current client
	 */
	public void transmitStringToClient(String message) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Erreur : " + e);
		}
	}

}