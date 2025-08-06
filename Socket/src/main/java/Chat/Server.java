package Chat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
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
			
			dosClient = new DataOutputStream(  sockC.getOutputStream()  );
			disServer = new DataInputStream(   sockC.getInputStream()   );
			
			ps.println(Utils.Colors.ANSI_GREEN+"Cliente conectado con exito."+Utils.Colors.ANSI_RESET);
			Thread.sleep( 200 );
			
			ps.println(Utils.Colors.ANSI_RED+"Esperando archivo ...."+Utils.Colors.ANSI_RESET);
			recibeFile(sockC);
			
			/*
			ps.println(Utils.Colors.ANSI_RED+"Esperando mensaje del cliente ...."+Utils.Colors.ANSI_RESET);
			
			while( !sockC.isClosed() && (msg = disServer.readUTF() )  != null )
			{
				ps.printf( Utils.Colors.ANSI_YELLOW+"\tMensaje: %s\n" +Utils.Colors.ANSI_RESET, msg );
				
				dosClient.writeUTF("ok");
				dosClient.flush();
				msg = "";
				
				//sockC.close();
			}
			*/
			
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
	
	
	public void recibeFile( Socket sock) {
		
		try {
			DataInputStream dis = new DataInputStream( sock.getInputStream() );
			
			Float size = dis.readFloat();
			Thread.sleep(100);
			String nameWhithExtend = dis.readUTF();
			Thread.sleep(100);
			
			DecimalFormat df = new DecimalFormat("#.00");
			
			
			File file = new File(  nameWhithExtend ); 
			if( file.exists() )
				file.delete();
			
			FileOutputStream fos = new FileOutputStream( file , true );
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			BufferedInputStream bis = new BufferedInputStream(  sock.getInputStream()  );
			
			
			byte[] buff = new byte[  size.intValue() ];
			
			int data = 0;
			while(   (data = bis.read()) != -1 ) {
				bos.write(  buff , 0  , data );	
			}
			
			bos.close();
			fos.close();
			bis.close();
			dis.close();

			ps.printf("Nombre: %s - Peso: %f / %n \n", nameWhithExtend, size , file.length() );
			
			if( file.length() == size )
			{
				ps.println("Recibido con exito");
			}else {
				ps.println("Error al recibir.");
			}

		} catch (IOException | InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);

		}
		
	}
		
	
}
