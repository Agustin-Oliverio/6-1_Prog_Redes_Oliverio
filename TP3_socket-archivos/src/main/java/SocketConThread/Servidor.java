package SocketConThread;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private static final int PUERTO = 5000; // puerto según requisito
    private ServerSocket server;

    public Servidor() {
        try {
            server = new ServerSocket(PUERTO);
            System.out.println(Utils.BLUE + "Servidor iniciado. Esperando conexiones en el puerto " + PUERTO + "..." + Utils.RESET);
            while (true) {
                Socket cliente = server.accept();
                System.out.println(Utils.GREEN + "Cliente conectado: " + cliente.getInetAddress().getHostAddress() + Utils.RESET);
                // Manejar cliente en hilo separado para permitir conexiones múltiples
                new Thread(new Handler(cliente)).start();
            }
        } catch (IOException e) {
            System.err.println(Utils.RED + "Error al iniciar el servidor: " + e.getMessage() + Utils.RESET);
            e.printStackTrace();
        } finally {
            if (server != null && !server.isClosed()) {
                try { server.close(); } catch (IOException ignored) {}
            }
        }
    }

    // Handler para cada cliente conectado
    private static class Handler implements Runnable {
        private final Socket sock;

        Handler(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            // Carpeta de recepción
            File dir = new File("recibido");
            if (!dir.exists()) dir.mkdirs();

            try (DataInputStream dis = new DataInputStream(sock.getInputStream())) {
                while (true) {
                    // Protocolo: primero nombre de archivo (UTF)
                    String fileName = dis.readUTF();
                    if (fileName == null) break;
                    if (fileName.equals(Utils.FIN_SIGNAL)) {
                        System.out.println(Utils.BLUE + "Cliente " + sock.getInetAddress().getHostAddress() + " finalizó la transmisión." + Utils.RESET);
                        break;
                    }

                    long fileLength = dis.readLong(); // tamaño en bytes
                    System.out.println(Utils.BLUE + "Recibiendo archivo: " + fileName + " (" + fileLength + " bytes)..." + Utils.RESET);

                    File outFile = new File(dir, new File(fileName).getName()); // evitar rutas de cliente
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        long remaining = fileLength;
                        int read;
                        while (remaining > 0 && (read = dis.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                            fos.write(buffer, 0, read);
                            remaining -= read;
                        }
                        fos.flush();
                    }

                    System.out.println(Utils.GREEN + "Archivo recibido y guardado en: " + outFile.getAbsolutePath() + Utils.RESET);
                }
            } catch (IOException e) {
                System.err.println(Utils.RED + "Error en la comunicación con cliente " + sock.getInetAddress().getHostAddress() + ": " + e.getMessage() + Utils.RESET);
                // e.printStackTrace();
            } finally {
                try { sock.close(); } catch (IOException ignored) {}
                System.out.println(Utils.BLUE + "Conexión con cliente cerrada: " + sock.getInetAddress().getHostAddress() + Utils.RESET);
            }
        }
    }

    public static void main(String[] args) {
        new Servidor();
    }
}
