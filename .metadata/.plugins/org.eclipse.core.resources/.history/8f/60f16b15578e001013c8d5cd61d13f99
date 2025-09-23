package SocketConThread;

import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

	
	PrintStream ps = new PrintStream(System.out);
	ArrayList<cli> clientesConectados = new ArrayList<>();
	
	public Servidor() {
	
		ps.println("INICIANDO SERVIDOR");
		HiloServidor serv = new HiloServidor();
		serv.setname("SERVIDOR");
	
		serv.start();
	}
}

class cli implements Runnable {
	
	String nick = "";
	Socket sock;
	
	
	
	
	public void run() {
		
	}	
}




class HiloServidor extends Thread{
	
}
