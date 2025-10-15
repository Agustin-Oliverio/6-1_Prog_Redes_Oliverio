package TP4;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Servidor {
    PrintStream ps = new PrintStream(System.out);
    public static final List<cli> clientesConectados = Collections.synchronizedList(new ArrayList<>());

    public Servidor() {
        ps.println("INICIANDO SERVIDOR");
        HiloServidor serv = new HiloServidor();
        serv.setName("SERVIDOR");
        serv.start();
    }
    

    public static void main(String[] args) {
        new Servidor();
    }
}


class cli implements Runnable {
    PrintStream ps = new PrintStream(System.out);
    String nick = "";
    Socket sock;
    Thread hilo;
    DataOutputStream dos;
    DataInputStream dis;
    boolean isConected;

    
    public cli(Socket sock, String nick, DataInputStream in, DataOutputStream out) {
        this.nick = nick;
        this.sock = sock;
        this.dis = in;
        this.dos = out;
        this.isConected = true;
        this.hilo = new Thread(this, nick);
    }

    
    public void run() {
        String msgRecibido = "";
        try {
            while (sock.isConnected() && this.isConected) {
                msgRecibido = dis.readUTF();
                
                if (msgRecibido.equals("FORWARD::PRIVATE")) {
                    String recipient = dis.readUTF();
                    int ivLen = dis.readInt();
                    byte[] iv = new byte[ivLen]; dis.readFully(iv);
                    int cipherLen = dis.readInt();
                    byte[] cipherBytes = new byte[cipherLen]; dis.readFully(cipherBytes);
                    String type = dis.readUTF();
                    String extra = dis.readUTF();
                    boolean sent = false;
                    
                    synchronized (Servidor.clientesConectados) {
                        for (cli c : Servidor.clientesConectados) {
                            if (c.nick.equalsIgnoreCase(recipient) && c.isConected) {
                                synchronized (c.dos) {
                                    c.dos.writeUTF("INCOMING::PRIVATE");
                                    c.dos.writeUTF(this.nick);
                                    c.dos.writeInt(ivLen); c.dos.write(iv);
                                    c.dos.writeInt(cipherLen); c.dos.write(cipherBytes);
                                    c.dos.writeUTF(type);
                                    c.dos.writeUTF(extra == null ? "" : extra);
                                    c.dos.flush();
                                }
                                sent = true;
                                break;
                            }
                        }
                    }
                    
                    if (!sent) {
                        synchronized (dos) {
                            dos.writeUTF("RESP::NOTFOUND");
                            dos.writeUTF(recipient);
                            dos.flush();
                        }
                    }
                    continue;    
                }
                
                if (msgRecibido.equals("FORWARD::BROADCAST")) {
                    int ivLen = dis.readInt();
                    byte[] iv = new byte[ivLen]; dis.readFully(iv);
                    int cipherLen = dis.readInt();
                    byte[] cipherBytes = new byte[cipherLen]; dis.readFully(cipherBytes);
                    String type = dis.readUTF();
                    String extra = dis.readUTF();
                    
                    synchronized (Servidor.clientesConectados) {
                        for (cli c : Servidor.clientesConectados) {
                            if (c.nick.equalsIgnoreCase(this.nick)) continue;
                            synchronized (c.dos) {
                                c.dos.writeUTF("INCOMING::BROADCAST");
                                c.dos.writeUTF(this.nick);
                                c.dos.writeInt(ivLen); c.dos.write(iv);
                                c.dos.writeInt(cipherLen); c.dos.write(cipherBytes);
                                c.dos.writeUTF(type);
                                c.dos.writeUTF(extra == null ? "" : extra);
                                c.dos.flush();
                            }
                        }
                    }
                    continue;
                }
                
                if (msgRecibido.equals("/listar")) {
                    String lista;
                    synchronized (Servidor.clientesConectados) {
                        lista = Servidor.clientesConectados.stream()
                                .map(c -> c.nick)
                                .collect(Collectors.joining(","));
                    }
                    synchronized (dos) {
                        dos.writeUTF("RESP::LIST");
                        dos.writeUTF(lista);
                        dos.flush();
                    }
                    continue;
                }
                
                if (msgRecibido.startsWith("/")) {
                    if (msgRecibido.equals("/salir")) {
                        this.isConected = false;
                        try { this.dis.close(); } catch (Exception e) {}
                        try { this.dos.close(); } catch (Exception e) {}
                        try { this.sock.close(); } catch (Exception e) {}
                        Servidor.clientesConectados.remove(this);
                        ps.println(Utils.COLORES[4] + "\tEl cliente " + this.nick + " se ha desconectado.\n" + Utils.RESET);
                        this.notificarClientes(false);
                        break;
                    }
                }
                
                String cliName = "Todos";
                String msg = msgRecibido;
                if (msgRecibido.contains("&")) {
                    String[] token = msgRecibido.split("&", 2);
                    cliName = token[0].trim().toLowerCase();
                    msg = token.length > 1 ? token[1].trim() : "";
                }
                
                ps.println("\n" + Utils.COLORES[1] + "El cliente " + this.nick + " envia:" + msgRecibido + "\n\t"
                        + " al cliente =>" + Utils.COLORES[2] + (cliName.equals("Todos") ? " Todos" : cliName.toUpperCase())
                        + "\n" + Utils.RESET);
                if (!msg.equals("")) {
                    synchronized (Servidor.clientesConectados) {
                        for (cli c : Servidor.clientesConectados) {
                            if (!c.isConected) continue;
                            if (!cliName.equals("Todos") && cliName.equalsIgnoreCase(c.nick)) {
                                synchronized (c.dos) {
                                    c.dos.writeUTF(this.nick + ":" + msg);
                                    c.dos.flush();
                                }
                                break;
                            } else if (cliName.equals("Todos") && !c.nick.equalsIgnoreCase(this.nick)) {
                                synchronized (c.dos) {
                                    c.dos.writeUTF(this.nick + ":" + msg);
                                    c.dos.flush();
                                }
                            }
                        }
                    }
                }
                
            }
        } catch (IOException e) {
            try {
                this.isConected = false;
                this.dis.close();
                this.dos.close();
                this.sock.close();
            } catch (Exception ex) {}
            
            Servidor.clientesConectados.remove(this);
            ps.println(Utils.COLORES[4] + "Cliente " + this.nick + " desconectado inesperadamente." + Utils.RESET);
            this.notificarClientes(false);
        }
        
    }

    public void notificarClientes(boolean joined) {
        synchronized (Servidor.clientesConectados) {
            for (cli c : Servidor.clientesConectados) {
                if (c.isConected && !c.nick.equals(this.nick)) {
                    try {
                        synchronized (c.dos) {
                            String msg = joined ? ("\t---" + this.nick + " se ha unido al chat---") : ("\t---" + this.nick + " se ha desconectado---");
                            c.dos.writeUTF(Utils.COLORES[6] + msg + Utils.RESET);
                            c.dos.flush();
                        }
                    } catch (IOException e) {}
                }
            }
        }
    }
    
}



class HiloServidor extends Thread {

    ServerSocket server;
    int puerto = 7777;
    Socket sockAux;
    PrintStream ps = new PrintStream(System.out);
    DataInputStream disCliente;
    DataOutputStream dosCliente;

    public HiloServidor() {
        try {
            server = new ServerSocket(puerto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                ps.println("Esperando conexion con un cliente");
                sockAux = server.accept();
                ps.println(Utils.COLORES[3] + "Cliente conectado: " + sockAux.getInetAddress().getHostAddress() + Utils.RESET);
                disCliente = new DataInputStream(sockAux.getInputStream());
                dosCliente = new DataOutputStream(sockAux.getOutputStream());
                ps.println(Utils.COLORES[4] + "Creando un cliente... Esperando NickName" + Utils.RESET);
                String ID = disCliente.readUTF();
                
                if (ID == null || ID.trim().isEmpty()) ID = "anon";
                
                cli newCliente = new cli(sockAux, ID, disCliente, dosCliente);
                ps.println(Utils.COLORES[1] + "El cliente " + newCliente.nick + " accedió al servidor.\n" + Utils.RESET);
                Servidor.clientesConectados.add(newCliente);
                newCliente.hilo.start();
                newCliente.notificarClientes(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
