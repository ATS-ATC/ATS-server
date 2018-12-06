package test.nokia.request;

public class StringTest {
	public static void main(String[] args) {
		String  AA = "[PROMDB311, SYDB312, GTMDB28A, ACMDB104, GPRSSIM08, AIRTDB311, VTXDB28A, AECIDB312, CTRTDB311, PMOUDB311, SIMDB311]";
		String BB = "[AethosTest, DIAMCL312, DROUTER312, ECTRL312, ENWTPPS312, EPAY312, EPPSA312, EPPSM312, GATEWAY312, NWTCOM111, NWTGSM066]";
		String upperCase = BB.trim().replaceAll("\\d+\\w?$", "").replace(".*", "").replace("RTDB ", "").toUpperCase();
		System.out.println(upperCase);
	}
}
