package Download;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MonitorThread extends Thread {
	
	private ThreadGroup grupo;
	private ArrayList<Thread> hilos;
	private static PrintStream ps = new PrintStream(System.out);
	private  String color = Utils.COLORES[5];
	
	
	@SuppressWarnings("static-access")
	public MonitorThread(ThreadGroup group , ArrayList<Thread> lista , Map<String,String> coloresHilos) {
		super("Monitor-Hilo");
		this.currentThread().setPriority(10);
		this.hilos = lista;
		this.grupo = group;
		this.setDaemon(true);
	}
	
	@Override
	public void run(){
		Map<String,Boolean> completados  = new HashMap<>();
		for()
		
		boolean activos = true;
		while(activos)
		{
			for() {
				
			}
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				ps.println(color + "[MONITOR]  Monitoreo interrumpido" + Utils.RESET);
                break;
			}
		}
		
		this.interrupt();
		ps.println( color + "[MONITOR]  Todas las tareas en el grupo han terminado." + Utils.RESET );
	}
	
	
}
