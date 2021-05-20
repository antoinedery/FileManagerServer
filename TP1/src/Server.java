import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.lang.*;

public class Server {
	
	public static void main(String[] args) throws Exception
	{
		Scanner input = new Scanner(System.in);  
		
		String ipAddress;
		boolean ipAddressIsValid = false;
		boolean portNumberIsValid = false;
		
		do {
			System.out.print("Saisir l'adresse IP : ");
			ipAddress = input.nextLine();
			
			ipAddressIsValid = validateIPAddress(ipAddress);
			
			if(!ipAddressIsValid)
				System.out.print("Adresse IP invalide\n");
			
		} while(!ipAddressIsValid);
				
		do {
			System.out.print("Saisir le port du serveur : ");
			String portNumber = input.nextLine();
				
			portNumberIsValid = validatePortNumber(portNumber);
				
			if(!portNumberIsValid)
				System.out.print("Numero de port invalide\n");
				
		} while(!portNumberIsValid);
	}

	/*https://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java*/
	private static boolean validateIPAddress(String ipAddress) {
		try {
			if (ipAddress == null || ipAddress.isEmpty())
	            return false;
	        
	        String[] bytes = ipAddress.split("\\.", -1);
	        if (bytes.length != 4)	//Si adresse IP sous format IPV4
	            return false;
	        
	        for (String singleByte : bytes) {
	            int byteValue = Integer.parseInt(singleByte);
	            if ((byteValue < 0) || (byteValue > 255)) 
	                return false;
	        }
	        return true;
		}
		catch(NumberFormatException e) {
				return false;
		}
	}
	
	private static boolean validatePortNumber(String portNumber) {
		try {
			int port = Integer.parseInt(portNumber);
			if (port < 5000 || port > 5050)
				return false;
			
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	
}


