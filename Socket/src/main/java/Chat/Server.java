package Chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server  extends Connection implements AutoCloseable{

	private DataInputStream disServer = null;
	private BufferedReader br = null;    
    
	public Server(Utils.enumType type) throws UnknownHostException, IOException {
		super(type);
	}

	public void serverOn() {
		try {
			ps.printf("");
			ps.printf(Utils.Colors.ANSI_YELLOW+"Esperando conexion de cliente\n\tPort:%s\n"+Utils.Colors.ANSI_RESET, getPort() );

			sockC = sockS.accept();
			
			ps.printf("%s - %s \n",
					sockC.getInetAddress().getHostAddress(),
					sockC.getInetAddress().getHostName()
					);
			
			dosClient = new DataOutputStream(sockC.getOutputStream());
			disServer = new DataInputStream(sockC.getInputStream());
			
			ps.println(Utils.Colors.ANSI_GREEN+"Cliente conectado con exito."+Utils.Colors.ANSI_RESET);
			Thread.sleep( 200 );
			ps.println(Utils.Colors.ANSI_RED+"Esperando mensaje del cliente ...."+Utils.Colors.ANSI_RESET);
				
			while( !sockC.isClosed() && (msg = disServer.readUTF() )  != null )
			{
				ps.printf( Utils.Colors.ANSI_YELLOW+"\tMensaje: %s\n" +Utils.Colors.ANSI_RESET, msg );
				
				dosClient.writeUTF("ok");
				dosClient.flush();
				msg = "";
				
				//sockC.close();
			}
		}catch (IOException | InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}finally {
			try {
				sockC.close();
				if( br!=null)
					br.close();
				
				if(disServer != null)
					disServer.close();
				
				dosClient.close();
				//sockS.close();
			} catch (IOException ex) {
	            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}


	
	@Override
	public void close() throws IOException {
		sockC.close();
		if( br!=null)
			br.close();
		
		if(disServer != null)
			disServer.close();
		
		dosClient.close();
		sockS.close();		
	}
	
}
