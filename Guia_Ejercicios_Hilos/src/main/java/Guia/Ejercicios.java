package Guia;

import java.awt.Font;
import java.io.*;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;


public class Ejercicios {
	
    // EJERCICIO 1 - Hilo Alfanumérico
    public static void ejercicio1() {
    	final PrintStream ps = new PrintStream(System.out);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            ps.print("Ingrese el tipo (1 para números, 2 para letras): ");
            int tipo = Integer.parseInt(br.readLine());
            Thread hilo = new Thread(new HiloAlfanumerico(tipo));
            hilo.start();
            hilo.join();
        } catch (Exception e) {
            ps.println("Error: " + e.getMessage());
        }
        
        esperarTecla();
    }

    static class HiloAlfanumerico implements Runnable {
    	static PrintStream ps = new PrintStream(System.out);
        private final int tipo;
        private final String[] letras = {
                "a","b","c","d","e","f","g","h","i","j",
                "k","l","m","n","ñ","o","p","q","r","s",
                "t","u","v","w","x","y","z"
        };

        public HiloAlfanumerico(int tipo) { this.tipo = tipo; }

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
                    ps.println("Tipo no válido (1 o 2).");
                }
            } catch (InterruptedException e) {
                ps.println("Hilo interrumpido.");
            }
        }
    }

    

 // EJERCICIO 2 - Contadores simultáneos
    public static void ejercicio2() {
    	PrintStream ps = new PrintStream(System.out);
        Random random = new Random();
        int cantidad = 3 + random.nextInt(8);

        ps.println("Iniciando " + cantidad + " contadores...\n");
        List<Thread> hilos = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            int limite = 5 + random.nextInt(16);
            String nombre = "Contador-" + i;
            Thread hilo = new Thread(new Contador(nombre, limite));
            hilo.start();
            hilos.add(hilo);
        }

        for (Thread t : hilos) {
            try { t.join(); } catch (InterruptedException e) {}
        }

        ps.println("\n✅ Todos los contadores finalizaron.");
        esperarTecla();
    }

    static class Contador implements Runnable {
    	static PrintStream ps = new PrintStream(System.out);
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
            int contador = 0;

            ps.println(nombre + " iniciado...");

            while (contador < limite) {
                contador++;
                ps.println(nombre + " contando: " + contador + "/" + limite);
                try {
                    Thread.sleep(200 + random.nextInt(801)); // 200–1000ms
                } catch (InterruptedException e) {
                    return;
                }
            }

            long duracion = System.currentTimeMillis() - inicio;
            JOptionPane.showMessageDialog(null,
                "El contador \"" + nombre + "\" llegó a " + limite +
                "\nDuración: " + duracion + " ms",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    
    // EJERCICIO 3 - Carrera Liebre vs Tortuga
    public static void ejercicio3() {
        new Carrera().iniciarCarrera();
        esperarTecla();
    }

    static class Carrera {
        private JTextArea area;
        private final int META = 70;
        private volatile boolean carreraTerminada = false;

        public void iniciarCarrera() {
            JFrame ventana = new JFrame("Carrera de la Liebre y la Tortuga");
            area = new JTextArea(20, 80);
            area.setFont(new Font("Consolas", Font.PLAIN, 14));
            area.setEditable(false);
            ventana.add(new JScrollPane(area));
            ventana.pack();
            ventana.setVisible(true);

            area.append("🏁 ¡Comienza la carrera!\n\n");
            Thread tortuga = new Thread(new Animal("Tortuga", this));
            Thread liebre = new Thread(new Animal("Liebre", this));
            tortuga.start(); liebre.start();
        }

        public synchronized void actualizarPantalla(int posT, int posL) {
            if (carreraTerminada) return;
            StringBuilder t = new StringBuilder();
            StringBuilder l = new StringBuilder();
            for (int i = 1; i <= META; i++) {
                t.append(i == posT ? "T" : " ");
                l.append(i == posL ? "L" : " ");
            }
            area.append(t + "\n" + l + "\n\n");

            if (posT >= META && posL >= META) { area.append("🤝 ¡Empate!\n"); carreraTerminada = true; }
            else if (posT >= META) { area.append("🐢 ¡Gana la tortuga!\n"); carreraTerminada = true; }
            else if (posL >= META) { area.append("🐇 ¡Gana la liebre!\n"); carreraTerminada = true; }
        }

        class Animal implements Runnable {
            private final String nombre;
            private final Carrera carrera;
            private int pos = 1;
            private final Random rand = new Random();
            public Animal(String nombre, Carrera carrera) { this.nombre = nombre; this.carrera = carrera; }

            @Override
            public void run() {
                while (!carrera.carreraTerminada) {
                    try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
                    int prob = rand.nextInt(100);
                    if (nombre.equals("Tortuga")) pos += (prob <= 50 ? 3 : prob <= 70 ? -6 : 1);
                    else if (prob <= 20) {}
                    else if (prob <= 40) pos += 9;
                    else if (prob <= 50) pos -= 12;
                    else if (prob <= 80) pos += 1;
                    else pos -= 2;
                    if (pos < 1) pos = 1; if (pos > META) pos = META;
                    carrera.actualizarPantalla(pos, pos);
                }
            }
        }
    }

    
    
 // EJERCICIO 4 - Producto de matrices
    public static void ejercicio4() {
        ProductoMatrices.run();
        esperarTecla();
    }

    static class ProductoMatrices {
    	static PrintStream ps = new PrintStream(System.out);
    	
        public static void run() {
            int n = 4;
            int[][] A = new int[n][n];
            int[][] B = new int[n][n];
            int[][] resultadoSecuencial = new int[n][n];
            int[][] resultadoHilos = new int[n][n];
            Random r = new Random();

            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    A[i][j] = r.nextInt(10);
                    B[i][j] = r.nextInt(10);
                }

            
            long inicioSec = System.currentTimeMillis();
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    for (int k = 0; k < n; k++)
                        resultadoSecuencial[i][j] += A[i][k] * B[k][j];
            long finSec = System.currentTimeMillis();

            
            Thread[] hilos = new Thread[n];
            long inicioHilos = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                final int fila = i;
                hilos[i] = new Thread(() -> {
                    for (int j = 0; j < n; j++)
                        for (int k = 0; k < n; k++)
                            resultadoHilos[fila][j] += A[fila][k] * B[k][j];
                });
                hilos[i].start();
            }

            try { for (Thread t : hilos) t.join(); } catch (InterruptedException e) {}
            long finHilos = System.currentTimeMillis();

            String resultado = String.format(
                "Matriz A:\n%s\nMatriz B:\n%s\n\n--- RESULTADOS ---\n" +
                "Secuencial (%.2f ms):\n%s\nPor Hilos (%.2f ms):\n%s",
                matriz(A), matriz(B),
                (double)(finSec - inicioSec), matriz(resultadoSecuencial),
                (double)(finHilos - inicioHilos), matriz(resultadoHilos)
            );
            
            ps.println(resultado);
            ps.flush();
            JOptionPane.showMessageDialog(null, resultado, "Producto de Matrices", JOptionPane.INFORMATION_MESSAGE);
        }

        private static String matriz(int[][] m) {
            StringBuilder sb = new StringBuilder();
            for (int[] fila : m) {
                for (int v : fila) sb.append(String.format("%4d", v));
                sb.append("\n");
            }
            return sb.toString();
        }
    }
    

    
    // EJERCICIO 5 - Contador de renglones
    public static void ejercicio5() {
        ContadorRenglones.run();
    }

    static class ContadorRenglones {
    	static PrintStream ps = new PrintStream(System.out);
        private static int total = 0;
        
        public static void run() {
            File carpeta = new File("C:\\Agustin\\Escuela\\2025\\Programación sobre redes\\6-1_Prog_Redes");
            File[] archivos = carpeta.listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
            if (archivos == null) {
                ps.println("No se encontraron archivos.");
                return;
            }

            Thread[] hilos = new Thread[archivos.length];
            for (int i = 0; i < archivos.length; i++) {
                File archivo = archivos[i];
                hilos[i] = new Thread(() -> contar(archivo));
                hilos[i].start();
            }

            for (Thread h : hilos) try { h.join(); } catch (Exception e) {}

            JOptionPane.showMessageDialog(null, "Total de líneas: " + total);
            
            esperarTecla();
        }

        private static void contar(File f) {
            int lineas = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                while (br.readLine() != null) lineas++;
            } catch (IOException e) {}
            synchronized (ContadorRenglones.class) { total += lineas; }
            ps.println(f.getName() + " → " + lineas + " líneas.");
        }
    }

    
    
    // EJERCICIO 6 - Oficina de alumnos
    public static void ejercicio6() {
        Escuela.run();
        esperarTecla();
    }

    static class Alumno {
        String nombre, apellido;
        double asistencia;
        double[] notas = new double[3];
        boolean esRegular;
        Alumno(String n, String a) { nombre = n; apellido = a; }
        double promedio() { return Arrays.stream(notas).average().orElse(0); }
        public String toString() { return nombre + " " + apellido; }
    }

    static class OficinaDeAlumnos {
        static List<Alumno> cargar() {
            return new ArrayList<>(List.of(
                new Alumno("Thiago","Arellano"),
                new Alumno("Facundo","Aleksiunas"),
                new Alumno("Jazmín","Gonzalez"),
                new Alumno("Luciano","Vela")
            ));
        }
    }

    static class Docente extends Thread {
    	static PrintStream ps = new PrintStream(System.out);
        List<Alumno> alumnos; Random r = new Random();
        Docente(List<Alumno> a){ alumnos=a; }
        
        public void run(){
            ps.println("🧑‍🏫 Cargando notas...");
            for(Alumno al: alumnos){
                for(int i=0;i<3;i++) al.notas[i]=4+r.nextDouble()*6;
                ps.println(al+" → "+Arrays.toString(al.notas));
            }
        }
    }

    static class Preceptor extends Thread {
    	static PrintStream ps = new PrintStream(System.out);
        List<Alumno> alumnos; Random r=new Random();
        Preceptor(List<Alumno>a){alumnos=a;}
        
        public void run(){
            ps.println("🧑‍💼 Registrando asistencias...");
            for(Alumno al: alumnos){
                al.asistencia=r.nextDouble()*100;
                al.esRegular=al.asistencia>=75;
                ps.printf("%-20s → %.1f%%\n",al,al.asistencia);
            }
        }
    }

    static class Escuela {
    	static PrintStream ps = new PrintStream(System.out);
    	
        public static void run() {
            List<Alumno> alumnos = OficinaDeAlumnos.cargar();
            Preceptor p=new Preceptor(alumnos);
            Docente d=new Docente(alumnos);
            p.start(); d.start();
            try{p.join(); d.join();}catch(Exception e){}
            ps.println("\n🏅 Eximidos:");
            for(Alumno al: alumnos)
                if(al.esRegular && al.promedio()>=7)
                    ps.printf("%-20s → Prom: %.2f | Asis: %.1f%%\n",al,al.promedio(),al.asistencia);
        }
    }

    
    
    // EJERCICIO 7 - Control de empleados
    public static void ejercicio7() throws IOException {
        ControlEmpleados.run();
        esperarTecla();
    }

    static class Empleado {
        String nombre,dia; LocalTime hora; boolean temprano;
        Empleado(String n,String d,LocalTime h){
            nombre=n;dia=d;hora=h;temprano=h.isBefore(LocalTime.of(8,0));
        }
        public String toString(){
            return nombre+" ingresó el "+dia+" a las "+hora+" → "+
                   (temprano?"✅ Temprano":"⏰ Tarde");
        }
    }

    static class ControlEmpleados {
    	
    	static PrintStream ps = new PrintStream(System.out);
        static List<Empleado> lista=new ArrayList<>();
        static boolean fin=false;
        
        public static void run() throws IOException {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            ps.print("Cantidad de empleados: ");
            int cant=Integer.parseInt(br.readLine());

            Thread registro=new Thread(()->{
                try{
                    for(int i=0;i<cant;i++){
                        ps.println("\nEmpleado #"+(i+1));
                        ps.print("Nombre: ");String n=br.readLine();
                        ps.print("Día: ");String d=br.readLine();
                        ps.print("Hora (HH:MM): ");LocalTime h=LocalTime.parse(br.readLine());
                        synchronized(lista){lista.add(new Empleado(n,d,h));lista.notify();}
                    }
                }catch(Exception e){}
                synchronized(lista){fin=true;lista.notifyAll();}
            });

            Thread verificador=new Thread(()->{
                while(true){
                    Empleado e=null;
                    synchronized(lista){
                        while(lista.isEmpty()&&!fin){try{lista.wait();}catch(Exception ex){}}
                        if(!lista.isEmpty()) e=lista.remove(0);
                        else if(fin) break;
                    }
                    if(e!=null) ps.println("📋 "+e);
                }
            });

            ps.println("\n=== Iniciando control de empleados ===\n");
            registro.start(); verificador.start();
            try{registro.join(); verificador.join();}catch(Exception e){}
            ps.println("\n✅ Todos registrados. Fin del programa.");
        }
    }
    
    
    
    
    private static void esperarTecla() {
        System.out.println("\nPresione ENTER para volver al menú...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





