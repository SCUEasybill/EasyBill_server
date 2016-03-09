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
	 * 方法：线程执行方法
	 */
	public void run() {
		while (flag) {
			try {
				String msg = din.readUTF();
				System.out.println("收到的消息是：" + msg);
				if (msg.startsWith("<#REGISTER#>")) {
					String content = msg.substring(12);
					String[] sa = content.split("\\|");
					String result = DBUtil.registerUser(sa[0], sa[1], sa[2], sa[3]);// 注册类型，手机号或者邮箱，密码，昵称
					System.out.println("数据操作完成");
					if (result.equals(CONNECTION_OUT) || result.equals(REGISTERFAIL)
							|| result.equals(REGSTERFAILDUPLICATE)) {
						dout.writeUTF("<#REGISTERFAIL#>" + result);
					} else {
						dout.writeUTF("<#REGISTERSUCCESS#>" + result);// <#REGISTERSUCCESS#>nickName
																		// + "|"
																		// +
																		// userId
						System.out.println("发送数据操作完成");
					}
				} else if (msg.startsWith("<#LOGIN#>")) {
					String content = msg.substring(9);
					System.out.println(content);
					String[] sa = content.split("\\|");
					System.out.println(sa[0] + "==" + sa[1]);
					ArrayList<String> result = DBUtil.login(sa[0], sa[1]);
					System.out.println(result);
					if (result.size() > 1) {// 登录成功
						StringBuilder sb = new StringBuilder();
						sb.append("<#LOGINSUCCESS#>");
						for (String s : result) {
							sb.append(s);
							sb.append("|");
						}
						String loginInfo = sb.substring(0, sb.length() - 1);
						dout.writeUTF(loginInfo);// 返回用户基本信息，user_id,
													// user_password,
													// user_email,
													// user_phone,user_name
					} else {// 登录失败
						String loginInfo = "<#LOGINFAIL#>" + result.get(0);
						dout.writeUTF(loginInfo);
					}
				} else if (msg.startsWith("<#FINDPASSWORD#>")) {// 忘记密码，参数：用户ID，新密码，旧密码
					String content = msg.substring(16);
					String[] sa = content.split("\\|");
					System.out.println("旧密码==='" + sa[2] + "'");
					String result = DBUtil.findPassword(sa[0], sa[1], sa[2]);// sa[2]是null还是“”，待测试.传入的是“”
					if (result.startsWith(MODIFYPASSWORDSUCCESS)) {
						dout.writeUTF("<#FINDPASSWORDSUCCESS#>");
						System.out.println("修改成功，准备返回到客户端" + result);
					} else {
						dout.writeUTF("<#FINDPASSWORDFAIL#>");
					}
				} else if (msg.startsWith("<#MODIFYUSERINFO#>")) {
					String content = msg.substring(18);
					String[] sa = content.split("\\|");
					// sa[0]为"nickName","email","phone"
					String result = DBUtil.modifyUserInfo(sa[0], sa[1], sa[2]);// 修改类型：昵称或者邮箱或手机，修改后的值，用户ID
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
				else if (msg.startsWith("<#USER_LOGOUT#>")) {// 消息为退出登录
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
