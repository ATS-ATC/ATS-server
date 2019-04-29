package test.nokia.singleton;

public class Singleton4 {
	private static Singleton4 singleton4;
	private Singleton4() {}
	public static Singleton4 getInstance() {
		if(singleton4 ==null) {
			synchronized(Singleton4.class) {
				if(singleton4 ==null) {
					singleton4 = new Singleton4();
				}
			}
		}
		return singleton4;
	}
}
