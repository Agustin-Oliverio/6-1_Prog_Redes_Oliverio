package Guia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws Exception {
    	
    	final PrintStream ps = new PrintStream(System.out);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int opcion;

        do {
        	ps.println("\n=== MENÚ DE EJERCICIOS ===");
            ps.println("1. Hilo alfanumérico");
            ps.println("2. Contadores simultáneos");
            ps.println("3. Carrera de animales");
            ps.println("4. Producto de matrices");
            ps.println("5. Contador de renglones");
            ps.println("6. Oficina de alumnos");
            ps.println("7. Control de empleados");
            ps.println("0. Salir");
            ps.print("Seleccione una opción: ");

            opcion = Integer.parseInt(br.readLine());
            ps.println();

            switch (opcion) {
                case 1 -> Ejercicios.ejercicio1();
                case 2 -> Ejercicios.ejercicio2();
                case 3 -> Ejercicios.ejercicio3();
                case 4 -> Ejercicios.ejercicio4();
                case 5 -> Ejercicios.ejercicio5();
                case 6 -> Ejercicios.ejercicio6();
                case 7 -> Ejercicios.ejercicio7();
                case 0 -> ps.println("Saliendo...");
                default -> ps.println("Opción inválida.");
            }

        } while (opcion != 0);
    }
}
