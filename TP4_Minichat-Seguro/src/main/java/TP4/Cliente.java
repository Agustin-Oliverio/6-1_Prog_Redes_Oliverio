package TP4;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;

public class Cliente {
	PrintStream ps = new PrintStream(System.out);
	DataInputStream disServidor = null;
	DataOutputStream dosServidor = null;
	BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
	InetAddress IP = null;
	int puerto = 3500;
	Socket sock = null;
	boolean isConected = false;
	private String key = "Consortini Gonzalini :3";
	private SecureRandom sr = Utils.sr;

	public Cliente() {
		try {
			IP = InetAddress.getByName("127.0.0.1");
			sock = new Socket(IP, puerto);
			isConected = true;
			disServidor = new DataInputStream(sock.getInputStream());
			dosServidor = new DataOutputStream(sock.getOutputStream());
			
			if (sock.isConnected()) {
				ps.println("Ingrese su ID:");
				String ID = buff.readLine();
				if (ID == null)
					ID = "anon";
				dosServidor.writeUTF(ID);
				dosServidor.flush();
				ps.println("Bienvenido al chat " + ID);
			}
			ps.print("\t->");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Thread enviarMensaje = new Thread(() -> {
			String msg = "";
			try {
				while (true) {
					msg = buff.readLine();
					if (msg == null)
						break;
					msg = msg.trim();
					if (msg.equals("")) {
						ps.print("\t->");
						continue;
					}
					if (msg.equalsIgnoreCase("/salir")) {
						dosServidor.writeUTF("/salir");
						dosServidor.flush();
						break;
					} else if (msg.equalsIgnoreCase("/listar")) {
						dosServidor.writeUTF("/listar");
						dosServidor.flush();
					} else if (msg.equalsIgnoreCase("/verComandos") || msg.equalsIgnoreCase("/ayuda")) {
						mostrarComandos();
					} else if (msg.startsWith("/msg ")) {
						String[] parts = msg.split(" ", 3);
						if (parts.length < 3) {
							ps.println(Utils.COLORES[4] + "Uso: /msg usuario mensaje" + Utils.RESET);
							ps.print("\t->");
							continue;
						}
						String to = parts[1];
						String texto = parts[2];
						enviarMsgPrivado(to, texto);
					} else if (msg.startsWith("/enviarArchivo ")) {
						String[] parts = msg.split(" ", 3);
						if (parts.length < 2) {
							ps.println(Utils.COLORES[4] + "Uso: /enviarArchivo usuario [ruta]" + Utils.RESET);
							ps.print("\t->");
							continue;
						}
						String to = parts[1];
						String ruta = (parts.length == 3) ? parts[2] : null;
						if (ruta == null || ruta.trim().isEmpty()) {
							ps.println(Utils.COLORES[4] + "Falta ruta. Uso: /enviarArchivo usuario ruta" + Utils.RESET);
							ps.print("\t->");
							continue;
						}
						enviarArchivoPrivado(to, ruta);
					} else {
						broadcastTexto(msg);
					}
					ps.print("\t->");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					isConected = false;
					if (dosServidor != null)
						dosServidor.close();
					if (sock != null)
						sock.close();
				} catch (Exception ex) {
				}
			}
		}, "ENVIO");
		Thread recibirMensaje = new Thread(() -> {
			try {
				while (isConected) {
					String token = disServidor.readUTF();
					if ("RESP::LIST".equals(token)) {
						String lista = disServidor.readUTF();
						ps.println(Utils.COLORES[3] + "Usuarios: " + lista + Utils.RESET);
					} else if ("RESP::NOTFOUND".equals(token)) {
						String t = disServidor.readUTF();
						ps.println(Utils.COLORES[4] + "Usuario no encontrado: " + t + Utils.RESET);
					} else if ("INCOMING::PRIVATE".equals(token) || "INCOMING::BROADCAST".equals(token)) {
						String from = disServidor.readUTF();
						int ivLen = disServidor.readInt();
						byte[] iv = new byte[ivLen];
						disServidor.readFully(iv);
						int cipherLen = disServidor.readInt();
						byte[] cipherBytes = new byte[cipherLen];
						disServidor.readFully(cipherBytes);
						String type = disServidor.readUTF();
						String extra = disServidor.readUTF();
						byte[] plainBytes = Utils.decriptarBytes(key, iv, cipherBytes);
						if (plainBytes == null) {
							ps.println(Utils.COLORES[4] + "Error al descifrar mensaje de " + from + Utils.RESET);
						} else {
							if ("TEXT".equals(type)) {
								String plain = new String(plainBytes, java.nio.charset.StandardCharsets.UTF_8);
								if ("INCOMING::PRIVATE".equals(token)) {
									ps.println(Utils.COLORES[2] + "[PRIVADO de " + from + "] " + plain + Utils.RESET);
								} else {
									ps.println(Utils.COLORES[2] + "[" + from + "] " + plain + Utils.RESET);
								}
							} else if ("FILE".equals(type)) {
								byte[] fileBytes = plainBytes;
								File dir = new File("recibidos");
								if (!dir.exists())
									dir.mkdirs();
								File out = new File(dir, extra == null ? "file" : extra);
								int i = 1;
								String filename = out.getName();
								while (out.exists()) {
									int dot = filename.lastIndexOf('.');
									String nameOnly = dot > 0 ? filename.substring(0, dot) : filename;
									String ext = dot > 0 ? filename.substring(dot) : "";
									out = new File(dir, nameOnly + "_" + i + ext);
									i++;
								}
								try (FileOutputStream fos = new FileOutputStream(out)) {
									fos.write(fileBytes);
									fos.flush();
								}
								ps.println(Utils.COLORES[0] + "Archivo recibido de " + from + " -> "
										+ out.getAbsolutePath() + Utils.RESET);
							}
						}
					} else {
						ps.println(Utils.COLORES[0] + token + Utils.RESET);
					}
					ps.print("\t->");
				}
			} catch (Exception e) {
				try {
					if (sock != null)
						sock.close();
				} catch (Exception ex) {
				}
			}
		}, "RECIBIR");
		recibirMensaje.start();
		enviarMensaje.start();
	}

	private void mostrarComandos() {
		ps.println(Utils.COLORES[3] + "Comandos disponibles:" + Utils.RESET);
		ps.println("/salir - desconectarse");
		ps.println("/listar - ver usuarios conectados");
		ps.println("/verComandos - mostrar comandos");
		ps.println("/msg [usuario] [mensaje] - mensaje privado");
		ps.println("/enviarArchivo [usuario] [ruta] - enviar archivo");
		ps.println("/ayuda - ayuda");
	}

	private void enviarMsgPrivado(String to, String texto) {
		try {
			byte[] iv = new byte[16];
			sr.nextBytes(iv);
			byte[] plain = texto.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			byte[] cipher = Utils.encriptarBytes(key, iv, plain);
			synchronized (dosServidor) {
				dosServidor.writeUTF("FORWARD::PRIVATE");
				dosServidor.writeUTF(to);
				dosServidor.writeInt(iv.length);
				dosServidor.write(iv);
				dosServidor.writeInt(cipher.length);
				dosServidor.write(cipher);
				dosServidor.writeUTF("TEXT");
				dosServidor.writeUTF("");
				dosServidor.flush();
			}
			ps.println(Utils.COLORES[0] + "Mensaje privado enviado a " + to + Utils.RESET);
		} catch (Exception e) {
			ps.println(Utils.COLORES[4] + "Error al enviar mensaje: " + e.getMessage() + Utils.RESET);
		}
	}

	private void enviarArchivoPrivado(String to, String ruta) {
		try {
			File f = new File(ruta);
			if (!f.exists() || !f.isFile()) {
				ps.println(Utils.COLORES[4] + "Archivo no encontrado." + Utils.RESET);
				return;
			}
			if (f.length() > 10 * 1024 * 1024) {
				ps.println(Utils.COLORES[4] + "Archivo demasiado grande (límite 10 MB)." + Utils.RESET);
				return;
			}
			byte[] fileBytes;
			try (FileInputStream fis = new FileInputStream(f)) {
				fileBytes = fis.readAllBytes();
			}
			byte[] iv = new byte[16];
			sr.nextBytes(iv);
			byte[] cipher = Utils.encriptarBytes(key, iv, fileBytes);
			synchronized (dosServidor) {
				dosServidor.writeUTF("FORWARD::PRIVATE");
				dosServidor.writeUTF(to);
				dosServidor.writeInt(iv.length);
				dosServidor.write(iv);
				dosServidor.writeInt(cipher.length);
				dosServidor.write(cipher);
				dosServidor.writeUTF("FILE");
				dosServidor.writeUTF(f.getName());
				dosServidor.flush();
			}
			ps.println(Utils.COLORES[0] + "Archivo enviado a " + to + Utils.RESET);
		} catch (Exception e) {
			ps.println(Utils.COLORES[4] + "Error al enviar archivo: " + e.getMessage() + Utils.RESET);
		}
	}

	private void broadcastTexto(String texto) {
		try {
			byte[] iv = new byte[16];
			sr.nextBytes(iv);
			byte[] plain = texto.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			byte[] cipher = Utils.encriptarBytes(key, iv, plain);
			synchronized (dosServidor) {
				dosServidor.writeUTF("FORWARD::BROADCAST");
				dosServidor.writeInt(iv.length);
				dosServidor.write(iv);
				dosServidor.writeInt(cipher.length);
				dosServidor.write(cipher);
				dosServidor.writeUTF("TEXT");
				dosServidor.writeUTF("");
				dosServidor.flush();
			}
			ps.println(Utils.COLORES[0] + "Mensaje difundido a todos." + Utils.RESET);
		} catch (Exception e) {
			ps.println(Utils.COLORES[4] + "Error broadcast: " + e.getMessage() + Utils.RESET);
		}
	}

	public static void main(String[] args) {
		new Cliente();
	}
}