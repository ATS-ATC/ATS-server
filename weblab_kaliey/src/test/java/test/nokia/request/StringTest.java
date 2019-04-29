package test.nokia.request;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.jcraft.jsch.jce.Random;

import mx4j.tools.config.DefaultConfigurationBuilder.New;

public class StringTest {
	public static void main(String[] args) {
		/*String  AA = "[PROMDB311, SYDB312, GTMDB28A, ACMDB104, GPRSSIM08, AIRTDB311, VTXDB28A, AECIDB312, CTRTDB311, PMOUDB311, SIMDB311]";
		String BB = "[AethosTest, DIAMCL312, DROUTER312, ECTRL312, ENWTPPS312, EPAY312, EPPSA312, EPPSM312, GATEWAY312, NWTCOM111, NWTGSM066]";
		String upperCase = BB.trim().replaceAll("\\d+\\w?$", "").replace(".*", "").replace("RTDB ", "").toUpperCase();
		System.out.println(upperCase);*/
		
		/*String status = "Succeed";
		status = status.equals("Succeed")?"Idle":status;
		System.err.println(status);*/
		
		String failType="code bug ";
		boolean contains = failType.contains("code bug");
		System.out.println(contains);
	}
	@Test
	public void test() {
		String failType="1";
		boolean contains = failType.contains("code bug");
		System.out.println(contains);
	}
	@Test
	public void test1() {
		String str1 = "Str";
		String str2 = "String";
		String str3 = "ing";
		String str4 = str1 + str3;
		str1 = str4;
		System.err.println("str1"+(str1==str2?"==":"!=")+"str2");
	}
	@Test
	public void test2() {
		Integer i = 10;
		Integer j = 10;
		System.out.println(i == j);
		Integer a = 128;
		Integer b = 128;
		System.out.println(a == b);
		int k = 10;
		System.out.println(k == i);
		int kk = 128;
		System.out.println(kk == a);
		Integer m = new Integer(10);
		Integer n = new Integer(10);
		System.out.println(m == n);
	}
	@Test
	public void test3() {
		int i = 1;
		i = i++;
		int j = i++;
		int k = i + ++i * i++;
		System.out.println("i="+i);//4
		System.out.println("j="+j);//3
		System.out.println("k="+k);//15
	}
	@Test
	public void test4() {
		int i = 1;
		i = i++;
		System.out.println(i);
	}
	@Test
	public void test5() {
		int i = 0;
        int j = i++ + i++;      //这条语句等价于j = (i++) + (i++)
        System.out.println("输出i的结果为"+i);
        System.out.println("输出j的结果为"+j);
	}
	@Test
	public void test6() {
		double random = Math.random()*100;
		System.out.println(random);
	}
	@Test
	public void test7() {
		System.out.println(new Date().getTime());
	}
	@Test
	public void test8() {
		//1555901134780
		long createtime = Long.parseLong("1556243489973");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(createtime);
		String format = simpleDateFormat.format(date);
		System.out.println(format);
	}
	
}
