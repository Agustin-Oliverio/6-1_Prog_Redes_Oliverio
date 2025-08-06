package Chat;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utils.enumType;

public class mainCliente {

	public static void main(String[] args) {
		
		try {
			Cliente cli = new Cliente(enumType.CLIENT);
			
			cli.clientOn();
		} catch (IOException ex) {
            Logger.getLogger(mainCliente.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

}
