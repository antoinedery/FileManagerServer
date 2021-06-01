public class Validator {
	
	/**
	 * Validate the IP address (IPv4 format)
	 * @param ipAddress : the IP address to validate
	 * @return boolean if the address meets IPv4 requirements
	 * Source : https://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java
	 */
	public static boolean validateIPAddress(String ipAddress) {
		try {
			if (ipAddress == null || ipAddress.isEmpty())
	            return false;
	        
	        String[] bytes = ipAddress.split("\\.", -1);	//Séparer chaque octet dans un tableau
	        if (bytes.length != 4)	//Si adresse IP sous format IPv4
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
	
	/**
	 * Validate the port number (has to be between 5000 and 5050)
	 * @param portNumber : the port number to validate
	 * @return boolean if the port number is between 5000 and 5050
	 */
	public static boolean validatePortNumber(String portNumber) {
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
	
	/**
	 * Validate the command input (has to be mkdir, cd, upload, download, ls or exit)
	 * @param command: array of command 
	 * @return boolean if the command input is one of the five available options
	 */
	public static boolean validateCommand(String[] command) {
		if (!(command[0].equals("mkdir") || command[0].equals("cd") || command[0].equals("upload")
				|| command[0].equals("download") || command[0].equals("ls") || command[0].equals("exit")))
			return false;
		return true;
		
	}
	
}
