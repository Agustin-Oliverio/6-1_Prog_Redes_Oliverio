package Clase_hilos;

import java.io.PrintStream;

public class TareaThread extends Thread {
	private String nombreArchivo = "";
	private String color = "";
	PrintStream ps = new PrintStream (System.out);
	
	public TareaThread(ThreadGroup grupo, String nombreArchivo, String nombreHilo, int prioridad, String color) {
		super(grupo, nombreArchivo);
		this.nombreArchivo = nombreArchivo;
		this.color = color;
		setPriority(prioridad);
		Utils.coloresHilos.put(nombreHilo, color);
	}
	
	@Override
	public void run() {
		ps.println(color + "[" + this.getName() + "] preparando recursos para " + nombreArchivo + Utils.RESET);
		TareaEjecutar.TareaEjecutar(nombreArchivo, "Thread", color);
		ps.println(color + "[" + this.getName() + "] Libreando recursos de " + nombreArchivo + Utils.RESET);
	}
	
	}
