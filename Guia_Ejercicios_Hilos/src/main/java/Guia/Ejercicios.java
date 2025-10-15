package Guia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Ejercicios {

	public static void main(String[] args) {

	}	
}



//Ejercicio 1
class HiloAlfanumerico implements Runnable{
	PrintStream ps = new PrintStream(System.out);
	PrintStream psErr = new PrintStream(System.err);
	
	int tipo = 1;
	int i = 0;
	String[] letras = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "ñ",
	                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	
	@Override
	public void run() {
		if(tipo == 1) {
			while (i != 30){
				i = i + 1;
				ps.println(i);
			}
		}
		if(tipo == 2){
			i = i + 1;
			ps.println(letras[i - 1]);
		}
		
	}
}



// Ejercicio 2
class Contador implements Runnable{
	PrintStream ps = new PrintStream(System.out);
	PrintStream psErr = new PrintStream(System.err);
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	int contador = 0;
	String nombre;
	int limite;
	int tiempo = 0;
	
	@Override
	public void run() {
		ps.print("Pongale nombre al contador: ");
		try {
			nombre = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(contador != limite) {
			contador = contador + 1;
		}
		ps.println("El contador " + nombre + " ha contado hasta " + limite + " en " + tiempo + " milisegundos.");
	}
}



// Ejercicio 3
