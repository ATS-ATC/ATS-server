package test.nokia.singleton;

public class Singleton3 {
	static Singleton3 instanceSingleton3;
	static {
		instanceSingleton3 = new Singleton3();
	}
	private Singleton3() {}
}
