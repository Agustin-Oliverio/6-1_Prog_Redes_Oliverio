package Chat;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mainServer {

	public static void main(String[] args) {
		
		try ( Server servidor = new Server(Utils.enumType.SERVER) ){			
			servidor.setIp("127.0.0.1");
			servidor.setPort(2006);
			
			servidor.serverOn();
		} catch (UnknownHostException ex) {
            Logger.getLogger(mainServer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
            Logger.getLogger(mainServer.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

}
