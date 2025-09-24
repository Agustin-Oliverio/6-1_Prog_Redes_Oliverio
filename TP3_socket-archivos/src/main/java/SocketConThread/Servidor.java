package SocketConThread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Download.Utils;

public class Servidor {

	PrintStream ps = new PrintStream(System.out);
	public static ArrayList<cli> clientesConectados = new ArrayList<>();

	public Servidor() {
		ps.println("INICIANDO SERVIDOR");
		HiloServidor serv = new HiloServidor();
		serv.setName("SERVIDOR");

		serv.start();
	}
}

class cli implements Runnable {

	PrintStream ps = new PrintStream(System.out);

	String nick = "";
	Socket sock;
	Thread hilo;

	DataOutputStream dos;
	DataInputStream dis;
	boolean isConected;

	public cli(Socket sock, String nick, DataInputStream in, DataOutputStream out) {
		this.nick = nick;
		this.sock = sock;
		this.dis = in;
		this.dos = out;

		this.isConected = true;
		this.hilo = new Thread(this, nick);
	}

	public void run() {
		String msg = "";
		String msgRecibido = "";
		String cli = "";

		while (sock.isConnected() && this.isConected) {
			try {
				msgRecibido = dis.readUTF();

				if (msgRecibido.contains("&")) // cli : mens Seba:hacemos algo como ejeplo: jugar?
				{
					StringTokenizer token = new StringTokenizer(msgRecibido, "&");
					cli = token.nextToken().trim().toLowerCase();
					msg = token.nextToken().trim();
				} else {
					msg = msgRecibido.trim();
					cli = "Todos";
				}

				ps.println("\n" + Utils.COLORES[1] + "El cliente " + this.nick + " envia:" + msgRecibido + "\n\t"
						+ " al cliente =>" + Utils.COLORES[2] + (cli.equals("Todos") ? " Todos" : cli.toUpperCase())
						+ "\n" + Utils.RESET);

				if (msgRecibido.startsWith("/")) {
					switch (msgRecibido.substring(1, msgRecibido.length())) {
					case "salir":
						this.dis.close();
						this.dos.close();
						this.isConected = false;
						this.sock.close();
						Servidor.clientesConectados.remove(this);
						ps.println(
								Utils.COLORES[4] + "\tCliente " + this.nick + " se ah desconectado.\n" + Utils.RESET);
						this.notificarClientes(false);
						break;
					}
				}

				for (cli c : Servidor.clientesConectados) {
					if (msg.equals("") || cli.equals(""))
						break;

					if (cli.toLowerCase().equals(c.nick) && this.isConected) {
						c.dos.writeUTF(this.nick + ":" + msg);
						break;
					} else if (cli.equals("Todos") && this.isConected
							&& !c.nick.toLowerCase().equals(this.nick.toLowerCase())) {
						c.dos.writeUTF(this.nick + ":" + msg);
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void notificarClientes(boolean b) {
		for (cli c : Servidor.clientesConectados) {
			if (c.isConected && !c.nick.equals(this.nick)) {
				try {
					if (b) {
						c.dos.writeUTF(
								Utils.COLORES[6] 
								+ "\t---" 
								+ this.nick 
								+ " se ah unido al chat---" 
								+ Utils.RESET);
					} else {
						c.dos.writeUTF(
								Utils.COLORES[0] 
								+ "\t---" 
								+ this.nick 
								+ " se ah desconectado---" 
								+ Utils.RESET);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

class HiloServidor extends Thread {

	ServerSocket server;
	int puerto = 7777;
	Socket sockAux;
	PrintStream ps = new PrintStream(System.out);

	DataInputStream disCliente;
	DataOutputStream dosCliente;

	public HiloServidor() {

		try {
			server = new ServerSocket(puerto);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {
			try {
				ps.println("Esperando conexion con un cliente");
				sockAux = server.accept();

				ps.println(Utils.COLORES[3] + "Cliente conectado: " + sockAux.getInetAddress().getHostAddress()
						+ Utils.RESET);

				disCliente = new DataInputStream(sockAux.getInputStream());
				dosCliente = new DataOutputStream(sockAux.getOutputStream());

				ps.println(Utils.COLORES[4] + "Creando un cliente... Esperano NickName" + Utils.RESET);

				String ID = disCliente.readUTF();

				cli newCliente = new cli(sockAux, ID, disCliente, dosCliente);

				ps.println(
						Utils.COLORES[1] + "El cliente " + newCliente.nick + " accedio al servidor.\n" + Utils.RESET);

				Servidor.clientesConectados.add(newCliente);
				newCliente.hilo.start();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
