import java.io.IOException;

public class Validator {
	
	/*https://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java*/
	public static boolean validateIPAddress(String ipAddress) {
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
}
