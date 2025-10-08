package Download;

import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Utils {

	public static final String RESET = "\u001B[0m";
	public static final String[] COLORES = { "\u001B[31m", // rojo juego
			"\u001B[32m", // verde video
			"\u001B[33m", // amarillo musica
			"\u001B[34m", // azul doc
			"\u001B[35m", // magenta imagen
			"\u001B[36m" // cyan
	};

	private static PrintStream ps = new PrintStream(System.out);

	public static Map<String, String> coloresHilos = new HashMap<>();

	public static Thread crearHilo(ThreadGroup grupo, Runnable tarea, String nombre, int prioridad, String color) {
		Thread t = new Thread(grupo, tarea);
		t.setPriority(prioridad);
		t.setName(nombre);
		coloresHilos.put(nombre, color);
		return t;
	}

	public static SecureRandom sr = new SecureRandom();

	public static String encriptar(String clave, byte[] iv, String value) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKeySpec sks = new SecretKeySpec(clave.getBytes("UTF-8"), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(iv));

			byte[] encriptado = cipher.doFinal(value.getBytes());
			return DatatypeConverter.printBase64Binary(encriptado);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decriptar(String clave, byte[] iv, String encriptado) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKeySpec sks = new SecretKeySpec(clave.getBytes("UTF-8"), "AES");
			cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(iv));

			byte[] dec = cipher.doFinal(DatatypeConverter.parseBase64Binary(encriptado));
			return new String(dec);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
