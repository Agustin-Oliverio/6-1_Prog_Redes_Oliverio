package Guia;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Ejercicios {

	public static void main(String[] args) {
		PrintStream ps = new PrintStream(System.out);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//Ejercicio 1
		try {
			ps.print("Ingrese el tipo (1 para números, 2 para letras): ");
	        int tipo = Integer.parseInt(br.readLine());

	        Thread hilo = new Thread(new HiloAlfanumerico(tipo));
	        hilo.start();
	        
		}catch (IOException e) {
            ps.println("Error al leer la entrada.");
		}
		
		
		
		//Ejercicio 2
		Random random = new Random();
        int cantidad = 3 + random.nextInt(8);
        
        for (int i = 1; i <= cantidad; i++) {
            int limite = 5 + random.nextInt(16);
            String nombre = "Contador-" + i;

            Thread hilo = new Thread(new Contador(nombre, limite));
            hilo.start();
        }
        
        
        
        // Ejercicio 3
        new Carrera().iniciarCarrera();
	}
}



//Ejercicio 1
class HiloAlfanumerico implements Runnable {
    PrintStream ps = new PrintStream(System.out);
	PrintStream psErr = new PrintStream(System.err);
    
    private final int tipo;
    private final String[] letras = {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
        "k", "l", "m", "n", "ñ", "o", "p", "q", "r", "s",
        "t", "u", "v", "w", "x", "y", "z"
    };

    public HiloAlfanumerico(int tipo) {
        this.tipo = tipo;
    }

    @Override
    public void run() {
        try {
            if (tipo == 1) {
                for (int i = 1; i <= 30; i++) {
                    ps.println(i);
                    Thread.sleep(100);
                }
            } else if (tipo == 2) {
                for (String letra : letras) {
                    ps.println(letra);
                    Thread.sleep(100);
                }
            } else {
                ps.println("Tipo no válido. Use 1 (números) o 2 (letras).");
            }
        } catch (InterruptedException e) {
            ps.println("El hilo fue interrumpido.");
        }
    }
}



// Ejercicio 2
class Contador implements Runnable {
    private int contador = 0;
    private final String nombre;
    private final int limite;

    public Contador(String nombre, int limite) {
        this.nombre = nombre;
        this.limite = limite;
    }

    @Override
    public void run() {
        Random random = new Random();
        long inicio = System.currentTimeMillis();

        while (contador < limite) {
            contador++;
            try {
                Thread.sleep(200 + random.nextInt(801));
            } catch (InterruptedException e) {
                System.out.println("El hilo " + nombre + " fue interrumpido.");
                return;
            }
        }

        long fin = System.currentTimeMillis();
        long duracion = fin - inicio;

        JOptionPane.showMessageDialog(null,
                "El contador \"" + nombre + "\" ha contado hasta " + limite +
                "\nTiempo total: " + duracion + " ms",
                "Resultado del contador", JOptionPane.INFORMATION_MESSAGE);
    }
    

    public static void main(String[] args) {
        
    }
}



// Ejercicio 3
class Carrera {
    private JTextArea area;
    private final int META = 70;
    private volatile boolean carreraTerminada = false;

    public void iniciarCarrera() {
        JFrame ventana = new JFrame("Carrera de la Liebre y la Tortuga");
        area = new JTextArea(20, 80);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        ventana.add(scroll);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);

        area.append("🏁 ¡Comienza la carrera entre la liebre y la tortuga!\n\n");

        Thread tortuga = new Thread(new Animal("Tortuga", this));
        Thread liebre = new Thread(new Animal("Liebre", this));

        tortuga.start();
        liebre.start();
    }

    
    public synchronized void actualizarPantalla(int posTortuga, int posLiebre) {
        if (carreraTerminada) return;

        StringBuilder lineaT = new StringBuilder();
        StringBuilder lineaL = new StringBuilder();

        for (int i = 1; i <= META; i++) {
            lineaT.append(i == posTortuga ? "T" : " ");
            lineaL.append(i == posLiebre ? "L" : " ");
        }

        area.append(lineaT + "\n" + lineaL + "\n\n");

        
        if (posTortuga >= META && posLiebre >= META) {
            area.append("🤝 ¡Empate entre la tortuga y la liebre!\n");
            carreraTerminada = true;
        } else if (posTortuga >= META) {
            area.append("🐢 ¡La tortuga gana la carrera!\n");
            carreraTerminada = true;
        } else if (posLiebre >= META) {
            area.append("🐇 ¡La liebre gana la carrera!\n");
            carreraTerminada = true;
        }
    }

    class Animal implements Runnable {
        private final String nombre;
        private final Carrera carrera;
        private int posicion = 1;
        private final Random random = new Random();

        public Animal(String nombre, Carrera carrera) {
            this.nombre = nombre;
            this.carrera = carrera;
        }

        @Override
        public void run() {
            while (!carrera.carreraTerminada) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                int prob = random.nextInt(100) + 1;

                if (nombre.equals("Tortuga")) {
                    if (prob <= 50) posicion += 3;
                    else if (prob <= 70) posicion -= 6;
                    else posicion += 1;
                } else {
                    if (prob <= 20) { /* duerme */ }
                    else if (prob <= 40) posicion += 9;
                    else if (prob <= 50) posicion -= 12;
                    else if (prob <= 80) posicion += 1;
                    else posicion -= 2;
                }

                
                if (posicion < 1) posicion = 1;
                if (posicion > META) posicion = META;

                
                carrera.actualizarPantalla(
                        nombre.equals("Tortuga") ? posicion : obtenerPosicionTortuga(),
                        nombre.equals("Liebre") ? posicion : obtenerPosicionLiebre()
                );
            }
        }

        private synchronized int obtenerPosicionTortuga() {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            for (Thread t : threads) {
                if (t != null && t.getName().contains("Tortuga")) {
                    return posicion;
                }
            }
            return posicion;
        }

        private synchronized int obtenerPosicionLiebre() {
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            for (Thread t : threads) {
                if (t != null && t.getName().contains("Liebre")) {
                    return posicion;
                }
            }
            return posicion;
        }
    }
}