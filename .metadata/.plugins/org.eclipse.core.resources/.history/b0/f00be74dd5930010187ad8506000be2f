package Download;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class Utils {

	public static final String RESET = "\u001B[0m";
    public static final String[] COLORES = {
            "\u001B[31m", // rojo juego
            "\u001B[32m", // verde video
            "\u001B[33m", // amarillo musica
            "\u001B[34m", // azul doc
            "\u001B[35m", // magenta imagen
            "\u001B[36m"  // cyan
    };	
	
    private static PrintStream ps = new PrintStream(System.out);
   
    
    public static Map<String,String> coloresHilos = new HashMap<>();
    
    
    public static Thread crearHilo(ThreadGroup grupo , Runnable tarea, String nombre , int prioridad , String color) {
    	Thread t = new Thread( grupo , tarea );
    	t.setPriority(prioridad);
    	t.setName(nombre);
    	coloresHilos.put( nombre , color );
    	return t;
    }
    
    
}
