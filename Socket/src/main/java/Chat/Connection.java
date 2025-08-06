package Chat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import Utils.enumType;

public class Connection {
		
	private int port = 2006;
	private String ip  = "127.0.0.1";
	protected String msg = "";
	
	protected PrintStream ps;
	protected Socket sockC;
	protected ServerSocket sockS;
	
	private InetAddress direction;
	protected DataOutputStream dosServer, dosClient;
	
	public Connection( Utils.enumType type ) throws UnknownHostException, IOException
	{
		ps = new PrintStream( System.out );
		direction = InetAddress.getByName(ip);

		switch(type)
		{
		case SERVER:
			sockS = new ServerSocket(this.port);
			sockC = new Socket();
			break;
		case CLIENT:
			sockC = new Socket(direction,port);
			break;	
		}
	}
	
	public Connection(enumType type, String iP, int puerto) throws UnknownHostException, IOException {
		ps = new PrintStream( System.out );
		direction = InetAddress.getByName(iP);

		switch(type)
		{
		case CLIENT:
			sockC = new Socket(iP,puerto);
			break;	
		}		
	}

	public int getPort() {
		return port;
	}
	public void setPort(int p) {
		this.port = p;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String i) {
		this.ip = i;
	}
	
	
	
}







