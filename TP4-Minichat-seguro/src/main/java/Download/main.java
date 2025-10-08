package Download;

import java.io.PrintStream;
import java.util.ArrayList;

import javax.management.monitor.Monitor;

public class main {

	private static ThreadGroup grupo = new ThreadGroup("GrupoDescargas");
	private static ArrayList<Thread> listaHilos = new ArrayList<>();
	private static PrintStream ps = new PrintStream(System.out);
	
	public static void main(String[] args) {

		ps.println( "["  + Thread.currentThread().getName() + "] MAIN... "  );
		
		//1) Instancia directa de obje Thread  //  Thread( tarea ,  nombre)
		//									   //  TareaRunable( ejecucion , color )
		Thread hilo1 = new Thread( new TareaRunnable("consorti.java", Utils.COLORES[0]) , "Hilo-Java" );
		hilo1.setPriority(10);
		listaHilos.add(hilo1);
		//hilo1.start();
		
		//2) Intancia de una Class que herede de Thread
		TareaThread hilo2 = new TareaThread(grupo, "consorti.mp3", "Hilo-musica", 10, Utils.COLORES[1]);
		listaHilos.add(hilo2);
		//hilo2.start();
		
		//3) Intancia de una class Que se a una Tarea osea Runnable
		Thread hilo3 = Utils.crearHilo(grupo, new TareaRunnable("consorti.png", Utils.COLORES[2]),
							"Hilo-imagen", 10, Utils.COLORES[2]);
		listaHilos.add(hilo3);
		//hilo3.start();
		
		//4) Con class anonima
		Thread hilo4 = Utils.crearHilo(grupo, 
				new Runnable() {					
					@Override
					public void run() {
						ps.println( Utils.COLORES[3]  + "["  + Thread.currentThread().getName() + "] Preparando archivo... " 
					                                  + Utils.RESET );
						TareaEjecutar.TareaEjecutar("consorti.rar", "Clss Anonima", Utils.COLORES[3]);
					}
				},
				"Hilo-RAR", 10, Utils.COLORES[3]);
		listaHilos.add(hilo4);
		//hilo4.start();
		
		//5) Lambda
		Thread hilo5 = Utils.crearHilo(grupo, 
						() -> {
						ps.println( Utils.COLORES[4]  + "["  + Thread.currentThread().getName() + "] Preparando archivo... " 
					                                  + Utils.RESET );
						TareaEjecutar.TareaEjecutar("consorti.pdf", "Class Lambda", Utils.COLORES[4]);
				},
				"Hilo-PDF", 10, Utils.COLORES[4]);
		listaHilos.add(hilo5);
		//hilo5.start();		
		
		
		for(Thread t : listaHilos )
		{
			ps.println("[MAIN] Iniciando " + t.getName() + " con prioridad " + t.getPriority());
			t.start();
		}
		
		
		//otras tareas que puede ejecutar el MAIN
		
		
		//Administrador de Hilos
		MonitorThread monitor = new MonitorThread(grupo , listaHilos , Utils.coloresHilos);
		monitor.start();
		
		//Esperar a que todas las tareas terminene para concluir el programa MAIN
		for(Thread hilo : listaHilos)
		{
			try {
				hilo.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//aca cerramos el programa/MAIN
		ps.println( "["  + Thread.currentThread().getName() + "] FIN MAIN. "  );
	}

}
