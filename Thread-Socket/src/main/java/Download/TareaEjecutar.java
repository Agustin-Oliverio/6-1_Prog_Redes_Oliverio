package Download;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

public class TareaEjecutar {

	private static PrintStream ps = new PrintStream(System.out);

	public static void TareaEjecutar(String nombre, String tipo, String color) {
		ArrayList<String> pasos = new ArrayList<String>(
				Arrays.asList("Descargando", "Verificando", "Descomprimiendo", "Procesando", "Finalizando"));

		// Tarea que vamos a realizar
		for (String paso : pasos) {

			if( Thread.currentThread().isInterrupted() ) {
				ps.println(color + "[" + Thread.currentThread().getName() + "] Interrumpido! Abortando Hilo "
						   + nombre	+ Utils.RESET);					
				return;
			}
			
			ps.println(color + "[" + Thread.currentThread().getName() + "]" + tipo + " -> " + nombre + ": " + paso
					+ " ... " + Utils.RESET);

			try {
				Thread.sleep((int) (200 + Math.random() * 800));
			} catch (InterruptedException ex) {
				ps.println(color + "[" + Thread.currentThread().getName() + "] Interrupcion detectada en medio de "
						+ paso + Utils.RESET);
				return;
			}
		}

		// tarea completa
		ps.println(color + "[" + Thread.currentThread().getName() + "]" + tipo + " -> " + nombre + " COMPLETADO "
				+ Utils.RESET);
	}

}
