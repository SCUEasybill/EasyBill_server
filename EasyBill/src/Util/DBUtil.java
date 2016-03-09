package Util;

import static Util.ConstantUtil.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import Server.MD5;

/**
 * 
 * @author guyu
 *
 */
public class DBUtil {

	/**
	 * ������ʹ������Դ���ӳء�JDBC�������ݿ�
	 */
	public static Connection getConnection() {
		Connection con = null;
		// ʹ�����ݳ�����
		// try{
		// Context initial = new InitialContext();
		// //����mysqlΪ����Դjndi����
		// DataSource ds =
		// (DataSource)initial.lookup("java:comp/env/jdbc/mysql");
		// con=ds.getConnection();
		// }
		// catch(Exception e){
		// e.printStackTrace();
		// }
		// ʹ��JDBCֱ�ӷ������ݿ�
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://rdsw9wr7bb0138tguf1wo.mysql.rds.aliyuncs.com/easybill",
					"guxuesong", "123456");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public static Connection testConnection() {
		String result = null;
		Connection con = null;
		con = getConnection();
		if (con == null) {
			result = CONNECTION_OUT;
		}
		return con;
	}

	/**
	 * ��ȡ6λID
	 */
	final public static int getUserId() {
		int userId = 100000;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select MAX(user_id) from user ";
		try {
			con = testConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			if (rs.next()) {
				userId = rs.getInt(1);
			} else {
				userId = 100000;
			}
			System.out.println("��ǰ���ֵ��" + rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return userId + 1;
	}

	/**
	 * ������ע���û�
	 */
	public static String registerUser(String type, String account, String pwd, String nickName) {
		String result = null;
		Connection con = null;
		PreparedStatement ps = null;
		String sql = null;
		int userId = 000000;
		try {
			// account = new String(account.getBytes(), "ISO-8859-1");
			// pwd = new String(pwd.getBytes(), "ISO-8859-1");
			// int num = Integer.valueOf(u_no); //�Ը���ʽת��
			con = getConnection();
			pwd = MD5.MD5(pwd);
			userId = getUserId();
			if (con == null) {
				result = CONNECTION_OUT;
			}
			// sql = "insert into user(user_name, user_password) values ('" +
			// account + "', '" + pwd + "');";
			if (type.equals(PHONE)) {
				sql = "insert into user(user_phone, user_password, user_name,user_id)" + "values(?,?,?,?)";
			} else if (type.equals(EMAIL)) {
				sql = "insert into user(user_email, user_password, user_name,user_id)" + "values(?,?,?,?)";
			}
			ps = con.prepareStatement(sql);
			ps.setString(1, account);
			ps.setString(2, pwd);
			ps.setString(3, nickName);
			ps.setInt(4, userId);
			int count = ps.executeUpdate();// ִ�����ݿ����
			System.out.println("count======" + count);
			if (count == 1) {// data insert success
				result = REGISTERSUCCESS;// �����û��ǳƺ�6λ��ID
			} else {
				result = REGISTERFAIL; // data insert fail
			}
		} catch (MySQLIntegrityConstraintViolationException e) {
			result = REGSTERFAILDUPLICATE;// ���ظ��û���
		} catch (Exception e) {
			e.printStackTrace();
			result = REGISTERFAIL; // data insert fail
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return result;
	}

	/**
	 * ��������¼ �������û��˺ţ�����
	 */
	public static ArrayList<String> login(String account, String pwd) {
		ArrayList<String> result = new ArrayList<String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		pwd = MD5.MD5(pwd);
		sql = "select user_id, user_name, user_phone, user_email from user where user_phone = '" + account
				+ "' or user_email ='" + account + "' or user_id = '" + account + "' and user_password = '" + pwd + "'";
		System.out.println(sql);
		try {
			con = testConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {// �жϽ�����Ƿ�Ϊ��
				for (int i = 1; i <= 4; i++) {
					result.add(rs.getString(i));
				}
			} else {// ���޴���
				result.add(LOGINFAIL);
			}
			System.out.println("result.size=====" + result.size() + result.get(0));
		} catch (SQLException e) {
			e.printStackTrace();
			result.add(LOGINFAIL);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				rs = null;
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return result;
	}

	/**
	 * �޸��û���Ϣ �������޸�����ǳƻ�����������ֻ���
	 */
	public static String modifyUserInfo(String type, String userId, String modifyInfo) {
		String result = null;
		Connection con = null;
		PreparedStatement ps = null;
		String sql = null;
		if (type.equals(NICKNAME)) {
			sql = "update user set user_name = '" + modifyInfo + "' where user_id = '" + userId + "'";
		} else if (type.equals(PHONE)) {
			sql = "update user set user_phone = '" + modifyInfo + "' where user_id  = '" + userId + "'";
		} else if (type.equals(EMAIL)) {
			sql = "update user set user_email = '" + modifyInfo + "' where user_id = '" + userId + "'";
		}
		System.out.println("sql===" + sql);
		try {
			con = testConnection();
			ps = con.prepareStatement(sql);
			int count = ps.executeUpdate();
			if (count == 1) {// �����ɹ�
				result = MODIFYSUCCESS;
			} else {
				result = MODIFYFAIL;
			}
			System.out.println("result" + result);
		} catch (SQLException e) {
			result = MODIFYFAIL;
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
		}
		return result;
	}

	/**
	 * ������md5 ����
	 */
	private static String md5(String string) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("md5");
			md.update(string.getBytes());
			byte[] md5Bytes = md.digest();
			return bytes2Hex(md5Bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String bytes2Hex(byte[] byteArray) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] >= 0 && byteArray[i] < 16) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toHexString(byteArray[i] & 0xFF));
		}
		return strBuf.toString();
	}

	/**
	 * �������ֻ��һ����� �������ֻ��ţ������룬������
	 */
	public static String findPassword(String account, String newPsw, String oldPsw) {
		String result = null;
		Connection con = null;
		PreparedStatement ps = null;
		String sql = null;
		try {
			newPsw = MD5.MD5(newPsw);
			oldPsw = MD5.MD5(oldPsw);
			con = testConnection();
			if (!oldPsw.equals(newPsw)) {
				sql = "update user set user_password = '" + newPsw + "' where user_id = '" + account
						+ "' or user_phone = '" + account + "' or user_email = '" + account + "' and user_password = '"
						+ oldPsw + "'";
			} else if (oldPsw.equals(newPsw)) {
				sql = "update user set user_password = '" + newPsw + "' where user_id = '" + account
						+ "' or user_phone = '" + account + "' or user_email = '" + account + "'";
			}
			System.out.println("sql====" + sql);
			ps = con.prepareStatement(sql);
			int count = ps.executeUpdate();
			System.out.println("count===" + count);
			if (count == 1) {// data update success
				result = MODIFYPASSWORDSUCCESS;// get the user_ID
				System.out.println("��������ɹ�");
			} else {
				result = MODIFYPASSWORDFAIL; // data insert fail
				System.out.println("��������ʧ��");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = MODIFYPASSWORDFAIL;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ps = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				con = null;
			}
		}
		return result;
	}

	/**
	 * ���������Է���
	 */
	public static void main(String args[]) {
		// ����ע��
		// String res = registerUser("email", "972042723@qq.com", "123",
		// "guyu3");
		// ���Ե�¼
		ArrayList<String> res = login("97204723@qq.com", "456");
		// �����޸�����
		// String res = findPassword("972042723@qq.com", "456", "456");
		// �����޸ĸ�����Ϣ
		// String res = modifyUserInfo("nickName", "nihao", "100009");
		System.out.println(res);
		// String md5 = "guyu";
		// String re = md5(md5);
		// System.out.println(re);
	}

}
