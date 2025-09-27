package SocketConThread;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {

    private static final String SERVER_HOST = "127.0.0.1"; // cambiar si el servidor está en otra máquina
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        new Cliente().runClient();
    }

    public void runClient() {
        System.out.println(Utils.BLUE + "Intentando conectar con el servidor " + SERVER_HOST + ":" + SERVER_PORT + "..." + Utils.RESET);
        try (Socket socket = new Socket(InetAddress.getByName(SERVER_HOST), SERVER_PORT);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            System.out.println(Utils.GREEN + "Conexión establecida con el servidor." + Utils.RESET);

            boolean seguir = true;
            while (seguir) {
                // Abrir un JFileChooser para seleccionar archivo
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Seleccionar archivo a enviar");
                int seleccion = chooser.showOpenDialog(null);

                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (!file.exists() || !file.isFile()) {
                        System.err.println(Utils.RED + "Archivo inválido seleccionado." + Utils.RESET);
                        continue;
                    }

                    System.out.println(Utils.BLUE + "Preparando envío de: " + file.getAbsolutePath() + Utils.RESET);

                    // Enviar: nombre (UTF), tamaño (long), luego bytes en bloques
                    try (FileInputStream fis = new FileInputStream(file)) {
                        dos.writeUTF(file.getName());
                        dos.flush();
                        long fileLength = file.length();
                        dos.writeLong(fileLength);
                        dos.flush();

                        byte[] buffer = new byte[4096];
                        int read;
                        long totalSent = 0;
                        while ((read = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, read);
                            totalSent += read;
                        }
                        dos.flush();

                        if (totalSent == fileLength) {
                            System.out.println(Utils.GREEN + "Envío completado: " + file.getName() + " (" + totalSent + " bytes)." + Utils.RESET);
                        } else {
                            System.err.println(Utils.RED + "Se detectó discrepancia en bytes enviados: esperado=" + fileLength + " enviado=" + totalSent + Utils.RESET);
                        }
                    } catch (IOException e) {
                        System.err.println(Utils.RED + "Error durante la lectura/envío del archivo: " + e.getMessage() + Utils.RESET);
                        // e.printStackTrace();
                    }

                    // Preguntar si quiere enviar otro archivo
                    int resp = JOptionPane.showConfirmDialog(null, "¿Desea enviar otro archivo?", "Enviar otro", JOptionPane.YES_NO_OPTION);
                    if (resp != JOptionPane.YES_OPTION) {
                        // enviar señal de FIN al servidor
                        try {
                            dos.writeUTF(Utils.FIN_SIGNAL);
                            dos.flush();
                        } catch (IOException e) {
                            System.err.println(Utils.RED + "Error enviando señal de finalización al servidor: " + e.getMessage() + Utils.RESET);
                        }
                        seguir = false;
                        System.out.println(Utils.BLUE + "Finalizando cliente..." + Utils.RESET);
                    }
                } else {
                    // canceló el diálogo de selección
                    System.out.println(Utils.YELLOW + "Selección de archivo cancelada por el usuario." + Utils.RESET);
                    int resp = JOptionPane.showConfirmDialog(null, "No se seleccionó archivo. ¿Desea intentar seleccionar otro archivo?", "Continuar", JOptionPane.YES_NO_OPTION);
                    if (resp != JOptionPane.YES_OPTION) {
                        // enviar señal de FIN y salir
                        try {
                            dos.writeUTF(Utils.FIN_SIGNAL);
                            dos.flush();
                        } catch (IOException e) {
                            // ignore
                        }
                        seguir = false;
                        System.out.println(Utils.BLUE + "Finalizando cliente (sin enviar archivos)..." + Utils.RESET);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(Utils.RED + "Error de conexión con el servidor: " + e.getMessage() + Utils.RESET);
            // e.printStackTrace();
        }
    }
}
