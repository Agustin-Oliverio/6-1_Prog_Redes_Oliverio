package Prueba;

import java.io.*;
import java.net.*;

// Flag: /ID Passo 051
// TU-CODIGO-UNICO: 6E3F0E0A Code: BACC



public class Cliente {

    public static void main(String[] args) {

        try {
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Ingrese IP: ");
            String ip = teclado.readLine();

            System.out.print("Ingrese puerto: ");
            int puerto = Integer.parseInt(teclado.readLine());

            Socket socket = new Socket(ip, puerto);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Thread listener = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = in.readLine()) != null) {
                        System.out.println(linea);
                    }
                } catch (Exception e) {
                    System.out.println("Conexión cerrada.");
                }
            });
            listener.start();

            while (true) {
                String msg = teclado.readLine();
                if (msg == null) break;

                out.println(msg);

                if (msg.equals("/logout")) break;
            }

            socket.close();

        } catch (Exception e) {
            System.out.println("Error conectando al servidor.");
        }
    }
}
