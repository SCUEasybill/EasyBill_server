package Server;

import static Util.ConstantUtil.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import Util.DBUtil;

public class ServerAgent extends Thread {
	public Socket socket;
	public DataInputStream din;
	public DataOutputStream dout;
	public boolean flag = false;

	public ServerAgent(Socket socket) {
		this.socket = socket;
		try {
			this.din = new DataInputStream(socket.getInputStream());
			this.dout = new DataOutputStream(socket.getOutputStream());
			flag = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * �������߳�ִ�з���
	 */
	public void run() {
		while (flag) {
			try {
				String msg = din.readUTF();
				System.out.println("�յ�����Ϣ�ǣ�" + msg);
				if (msg.startsWith("<#REGISTER#>")) {
					String content = msg.substring(12);
					String[] sa = content.split("\\|");
					String result = DBUtil.registerUser(sa[0], sa[1], sa[2], sa[3]);// ע�����ͣ��ֻ��Ż������䣬���룬�ǳ�
					System.out.println("���ݲ������");
					if (result.equals(CONNECTION_OUT) || result.equals(REGISTERFAIL)
							|| result.equals(REGSTERFAILDUPLICATE)) {
						dout.writeUTF("<#REGISTERFAIL#>" + result);
					} else {
						dout.writeUTF("<#REGISTERSUCCESS#>" + result);// <#REGISTERSUCCESS#>nickName
																		// + "|"
																		// +
																		// userId
						System.out.println("�������ݲ������");
					}
				} else if (msg.startsWith("<#LOGIN#>")) {
					String content = msg.substring(9);
					System.out.println(content);
					String[] sa = content.split("\\|");
					System.out.println(sa[0] + "==" + sa[1]);
					ArrayList<String> result = DBUtil.login(sa[0], sa[1]);
					System.out.println(result);
					if (result.size() > 1) {// ��¼�ɹ�
						StringBuilder sb = new StringBuilder();
						sb.append("<#LOGINSUCCESS#>");
						for (String s : result) {
							sb.append(s);
							sb.append("|");
						}
						String loginInfo = sb.substring(0, sb.length() - 1);
						dout.writeUTF(loginInfo);// �����û�������Ϣ��user_id,
													// user_password,
													// user_email,
													// user_phone,user_name
					} else {// ��¼ʧ��
						String loginInfo = "<#LOGINFAIL#>" + result.get(0);
						dout.writeUTF(loginInfo);
					}
				} else if (msg.startsWith("<#FINDPASSWORD#>")) {// �������룬�������û�ID�������룬������
					String content = msg.substring(16);
					String[] sa = content.split("\\|");
					System.out.println("������==='" + sa[2] + "'");
					String result = DBUtil.findPassword(sa[0], sa[1], sa[2]);// sa[2]��null���ǡ�����������.������ǡ���
					if (result.startsWith(MODIFYPASSWORDSUCCESS)) {
						dout.writeUTF("<#FINDPASSWORDSUCCESS#>");
						System.out.println("�޸ĳɹ���׼�����ص��ͻ���" + result);
					} else {
						dout.writeUTF("<#FINDPASSWORDFAIL#>");
					}
				} else if (msg.startsWith("<#MODIFYUSERINFO#>")) {
					String content = msg.substring(18);
					String[] sa = content.split("\\|");
					// sa[0]Ϊ"nickName","email","phone"
					String result = DBUtil.modifyUserInfo(sa[0], sa[1], sa[2]);// �޸����ͣ��ǳƻ���������ֻ����޸ĺ��ֵ���û�ID
					if (result.equals(MODIFYSUCCESS)) {
						dout.writeUTF("<#MODIFYUSERINFOSUCCESS#>" + MODIFYSUCCESS);
					} else if (result.equals(MODIFYFAIL)) {
						dout.writeUTF("<#MOFIFYUSERINFOFAIL#>" + MODIFYFAIL);
					} else {
						dout.writeUTF("<#MOFIFYUSERINFOFAIL#>");
					}
				}
				// else if(msg.startsWith("")){
				// String content = msg.substring();
				// String[] sa = content.split("\\|");
				//
				// }else if(msg.startsWith("")){
				// String content = msg.substring();
				// String[] sa = content.split("\\|");
				// }else if(msg.startsWith("")){
				// String content = msg.substring();
				// String[] sa = content.split("\\|");
				// }
				else if (msg.startsWith("<#USER_LOGOUT#>")) {// ��ϢΪ�˳���¼
					this.din.close();
					this.dout.close();
					this.flag = false;
					this.socket.close();
					this.socket = null;
				}
			} catch (SocketException se) {
				try {
					dout.close();
					din.close();
					socket.close();
					socket = null;
					flag = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (EOFException eof) {
				try {
					dout.close();
					din.close();
					socket.close();
					socket = null;
					flag = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
