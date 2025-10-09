package SocketConThread;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import Download.Utils;

public class Cliente {

	PrintStream ps = new PrintStream(System.out);

	DataInputStream disServidor = null;
	DataOutputStream dosServidor = null;

	InputStreamReader is = new InputStreamReader(System.in);
	BufferedReader buff = new BufferedReader(is);

	InetAddress IP = null;
	int puerto = 7779;
	Socket sock = null;
	boolean isConected = false;

	boolean sendNickname = false;
	
	
	private static String CLAVE = "1234567890123456"; // 16 chars = 128 bits
	

	public static String cifrar(String texto) throws Exception {
	        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        byte[] cifrado = cipher.doFinal(texto.getBytes());
	        return Base64.getEncoder().encodeToString(cifrado);
	}

	public static String descifrar(String textoCifrado) throws Exception {
	        SecretKeySpec key = new SecretKeySpec(CLAVE.getBytes(), "AES");
	        Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.DECRYPT_MODE, key);
	        byte[] decodificado = Base64.getDecoder().decode(textoCifrado);
	        return new String(cipher.doFinal(decodificado));
	}
	
	

	public Cliente() {
		try {

			IP = InetAddress.getByName("127.0.0.1");
			sock = new Socket(IP, puerto);
			
			isConected = true;
			sendNickname = true;
			
			disServidor = new DataInputStream(sock.getInputStream());
			dosServidor = new DataOutputStream(sock.getOutputStream());

			if (sock.isConnected() && sendNickname) {
				ps.println("Ingrese su ID:");
				String ID = buff.readLine();
				dosServidor.writeUTF(ID);
				sendNickname = false;

				ps.println("Bienvenido al chat " + ID);
			}

			ps.print("\t->");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Thread enviarMensaje = new Thread(new Runnable() {
			@Override
			public void run() {
				String msg = "";
				String msgCifrado;
				try {
					msgCifrado = cifrar(msg);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				while (true && !msg.equalsIgnoreCase("/salir")) {
					try {
						msg = buff.readLine();
						
						if (msg == "/salir") {
							isConected = false;
							disServidor.close();
							dosServidor.close();
							sock.close();
							break;
						}	

						dosServidor.writeUTF(msgCifrado);
						ps.print("\t->");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, "ENVIO");

		Thread recibirMensaje = new Thread(new Runnable() {
			@Override
			public void run() {
				String msg = "";
				while (true && isConected) {
					 try {
						msg = disServidor.readUTF();
						String msgDescifrado = descifrar(msg);
						ps.println(Utils.COLORES[0] + msgDescifrado + Utils.RESET);
						ps.println("\t ->");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					isConected = false;
					disServidor.close();
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}, "RECIBIR");

		
		recibirMensaje.start();
		enviarMensaje.start();
		}

}
