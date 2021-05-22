import java.io.DataInputStream;
import java.net.Socket;

import java.io.DataOutputStream;

public class ClientExemple {
	private static Socket socket;
	
	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.2";
		int port = 5000;
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("(Client) The server is running on %s:%d%n", serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());

		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		socket.close();
	}
}
