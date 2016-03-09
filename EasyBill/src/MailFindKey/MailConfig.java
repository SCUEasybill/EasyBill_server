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
		// ToEmailStr�ռ��˵�ַ
		// Title�ʼ�����
		// Content�ʼ�����
		// FromEmail�����˵�ַ
		String FromEmail = "jerry8059@163.com";
		// System.out.println(FromEmail);
		boolean fnt = false;
		Properties props = new Properties();
		Session sendMailSession;
		Transport transport;
		sendMailSession = Session.getInstance(props, null);
		props.put("mail.smtp.host", "smtp.163.com"); // // �ǡ�smtp.sohu.com����IP��
		props.put("mail.smtp.auth", "true"); // ����smtpУ��
		try {
			transport = sendMailSession.getTransport("smtp");
			transport.connect("smtp.163.com", "jerry8059@163.com", "�����������"); // ���ڵ��û���,����...........
			// ��Ϊ�������
			// transport.connect("smtp.qq.com","717766957","����");
			Message newMessage = new MimeMessage(sendMailSession);
			// ����mail����
			String mail_subject = Title;
			newMessage.setSubject(mail_subject);
			// ���÷����˵�ַ
			// String strFrom = "service@jrsoft.com.cn"; // <--------------
			// strFrom = new String(strFrom.getBytes(), "8859_1");
			// newMessage.setFrom(new InternetAddress(strFrom));
			Address address[] = { new InternetAddress(FromEmail) };// �ı䷢���˵�ַ
			newMessage.addFrom(address);
			// �����ռ��˵�ַ
			newMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(ToEmailStr));
			// ����mail����
			newMessage.setSentDate(new java.util.Date());
			String mail_text = Content;
			newMessage.setText(mail_text);
			newMessage.saveChanges(); // ���淢����Ϣ
			transport.sendMessage(newMessage, newMessage.getRecipients(Message.RecipientType.TO)); // �����ʼ�
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
				boolean aa = send.SendEmailTest("251637313@qq.com", "hello", "nihao", "jerry8059@163.com");// ����Ϊ���ռ���
																											// ����
																											// ����
																											// ������
				System.out.println(aa);
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}