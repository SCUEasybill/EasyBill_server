package Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zhengzebiaoda {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		boolean re = isEmailNO("972042723@qq.com");
		System.out.println(re);
	}
	//手机号的正则表达
	public static boolean isMobilNO(String mobiles){
		String regex = "^[1]+[3,5]+\\d{9}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	//邮箱正则表达
	public static boolean isEmailNO(String emails){
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(emails);
		return m.matches();
	}
}
