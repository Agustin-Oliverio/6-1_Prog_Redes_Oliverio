package Chat;

import java.awt.FileDialog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import Utils.enumType;

public class Cliente extends Connection  {

	public Cliente(enumType type , String IP , int port) throws UnknownHostException, IOException {
		super(type , IP , port);
	}

	public void clientOn() {
		DataInputStream disClient = null;
		
		try {
			disClient = new DataInputStream(  sockC.getInputStream()  );
			dosClient = new DataOutputStream( sockC.getOutputStream() );
			
			
			ps.println(Utils.Colors.ANSI_RED+"Enviando archivo ...."+Utils.Colors.ANSI_RESET);
			sendFile( dialogFile() , dosClient, sockC);
			
			/*
			dosClient.writeUTF("Hola, soy kevin y llegue reee tarde.");
			dosClient.flush();
			
			dosClient.writeUTF("kevin esta muy ansioso");
			dosClient.flush();
			
			
			while( !sockC.isClosed() && (msg = disClient.readUTF()) != null)
			{
				ps.printf( Utils.Colors.ANSI_YELLOW+"\tMensaje: %s\n" +Utils.Colors.ANSI_RESET, msg );		
				msg = "";
				//this.disconect( disClient , dosClient);
			}
			*/
			
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
				
				//BufferedOutputStream comunicacion a nivel de BYTE
				buffOutput = new BufferedOutputStream( sock.getOutputStream() );
				
				DecimalFormat df = new DecimalFormat("#.00");
				float size = archivo.length();
				
				ps.println("Se prepara el fichero:" 
						+ archivo.getName() 
						+ " / "
						+ df.format(size) + "Kb");
				
				
				// DataOutputStream comunicacion NORMAL texto 
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
				ps.println("Se ha enviado el fichero");
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

	public void recibeFile( Socket sock) {
		
		try {
			DataInputStream dis = new DataInputStream( sock.getInputStream() );
			
			Float size = dis.readFloat();
			Thread.sleep(100);
			String nameWhithExtend = dis.readUTF();
			Thread.sleep(100);
			
			
			File file = new File( "Recibidos/" + nameWhithExtend ); 
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
	
	public String dialogFile() {
		
		FileDialog window = new FileDialog(
				new JFrame(),
				"Eleccion de un archivo.",
				FileDialog.LOAD
				); 
		window.setVisible(true);
		
		if( window.getFile() != null )
			return window.getDirectory().concat( window.getFile() );
			
		return null;
	}

}
