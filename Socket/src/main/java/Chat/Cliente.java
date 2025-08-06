package Chat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import Utils.enumType;

public class Cliente extends Connection  {

	public Cliente(enumType type) throws UnknownHostException, IOException {
		super(type);
	}

	public void clientOn() {
		DataInputStream disClient = null;
		
		try {
			disClient = new DataInputStream(sockC.getInputStream());
			dosClient = new DataOutputStream(sockC.getOutputStream());
			
			dosClient.writeUTF("Hola, soy kevin y llegue reee tarde ");
			dosClient.flush();
			
			dosClient.writeUTF("kevin esta muy ansioso");
			dosClient.flush();
			while( !sockC.isClosed() && (msg = disClient.readUTF()) != null)
			{
				ps.printf( Utils.Colors.ANSI_YELLOW+"\tMensaje: %s\n" +Utils.Colors.ANSI_RESET, msg );		
				msg = "";
				//this.disconect( disClient , dosClient);
			}
	
		} catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}finally {	
			try {
				dosClient.close();
				disClient.close();
				
				//sockS.close();
			} catch (IOException ex) {
	            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			}			
		}

	}
	
	public void disconect(DataInputStream i ,DataOutputStream o)
	{
		try {
			i.close();
			o.close();
			
			sockC.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFile(String ruta , DataOutputStream out , Socket sock )
	{
		File archivo = new File(ruta);
		FileInputStream fis = null;
		BufferedInputStream bi = null;
		
		BufferedOutputStream buffOutput = null;
		
		try {
			if( archivo.exists() )
			{
				fis = new FileInputStream(archivo);
				bi = new BufferedInputStream(fis);
				buffOutput = new BufferedOutputStream(sock.getOutputStream());
				
				DecimalFormat df = new DecimalFormat("#.00");
				float size = archivo.length();
				ps.println("Se prepara el fichero:" 
						+ archivo.getName() 
						+ " / "
						+ df.format(size) + "Kb");
				
				out.writeFloat(size);
				Thread.sleep(100);
				out.writeUTF( archivo.getName() );
				Thread.sleep(100);
				
				byte buff[] = new byte[(int)size];
				bi.read( buff );
				for(int i=0;i<buff.length;i++) {
					buffOutput.write( buff[i] );
				}
				
				Thread.sleep(500);
				ps.println("Se ah enviado el fichero");
			}
		}catch(UnknownHostException ex) {
		
		}catch(IOException ex) {
			
		}catch(InterruptedException ex) {
			
		}finally {
			try {
				buffOutput.close();
				bi.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
