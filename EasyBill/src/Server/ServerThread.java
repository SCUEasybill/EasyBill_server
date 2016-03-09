package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerThread extends Thread{

	public ServerSocket ss; //声明ServerSocket对象
	public boolean flag = false;
	
	public ServerThread(ServerSocket ss){
		this.ss =ss;
		flag = true;
	}
	
	public void run(){
		while(flag){
			try {
				Socket socket = ss.accept();
				System.out.println("接入成功");
				ServerAgent sa = new ServerAgent(socket);
				sa.start();
			} catch (SocketException se) {
				try {
					ss.close();
					ss = null;
					System.out.println("ServerSocket Closed");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
