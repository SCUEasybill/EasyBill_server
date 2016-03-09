package MailFindKey;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailConfig {
	public boolean SendEmailTest(String ToEmailStr, String Title, String Content, String FromEmaila)
			throws AddressException {
		// ToEmailStr收件人地址
		// Title邮件标题
		// Content邮件内容
		// FromEmail发件人地址
		String FromEmail = "jerry8059@163.com";
		// System.out.println(FromEmail);
		boolean fnt = false;
		Properties props = new Properties();
		Session sendMailSession;
		Transport transport;
		sendMailSession = Session.getInstance(props, null);
		props.put("mail.smtp.host", "smtp.163.com"); // // 是“smtp.sohu.com”的IP！
		props.put("mail.smtp.auth", "true"); // 允许smtp校验
		try {
			transport = sendMailSession.getTransport("smtp");
			transport.connect("smtp.163.com", "jerry8059@163.com", "你的邮箱密码"); // 你在的用户名,密码...........
			// 改为你的密码
			// transport.connect("smtp.qq.com","717766957","密码");
			Message newMessage = new MimeMessage(sendMailSession);
			// 设置mail主题
			String mail_subject = Title;
			newMessage.setSubject(mail_subject);
			// 设置发信人地址
			// String strFrom = "service@jrsoft.com.cn"; // <--------------
			// strFrom = new String(strFrom.getBytes(), "8859_1");
			// newMessage.setFrom(new InternetAddress(strFrom));
			Address address[] = { new InternetAddress(FromEmail) };// 改变发件人地址
			newMessage.addFrom(address);
			// 设置收件人地址
			newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(ToEmailStr));
			// 设置mail正文
			newMessage.setSentDate(new java.util.Date());
			String mail_text = Content;
			newMessage.setText(mail_text);
			newMessage.saveChanges(); // 保存发送信息
			transport.sendMessage(newMessage, newMessage.getRecipients(Message.RecipientType.TO)); // 发送邮件
			fnt = true;
			transport.close();
		} catch (Exception e) {
			fnt = false;
		}
		return fnt;
	}

	public static void main(String[] a) {
		MailConfig send = new MailConfig();
		boolean s = true;
		try {
			if (s) {
				boolean aa = send.SendEmailTest("251637313@qq.com", "hello", "nihao", "jerry8059@163.com");// 参数为：收件人
																											// 标题
																											// 内容
																											// 发件人
				System.out.println(aa);
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}