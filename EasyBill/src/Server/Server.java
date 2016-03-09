package Server;

import static Util.ConstantUtil.PORT;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ServerSocket ss = new ServerSocket(PORT);
			ServerThread st = new ServerThread(ss);
			st.start();
			System.out.println("Listening Port ===="+PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
