package MailFindKey;


public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Mail mail = new Mail("972042723@qq.com", "875203073@qq.com", "smtp.qq.com", "guyu", "yanshuzhibei", "nihao",
				"nihao");
		boolean bl = mail.sendMail();
		System.out.println(bl);
	}

}
