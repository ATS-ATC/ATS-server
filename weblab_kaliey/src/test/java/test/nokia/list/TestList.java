package test.nokia.list;

public class TestList {
	public static void main(String[] args) {
		int[] arr = {11,2,44,12,42,663,43,21};
		for(int i=0;i<arr.length-1;i++) {
			for(int j=0;j<arr.length-1-i;j++) {
				if(arr[j] > arr[j+1]) {
					int temp = arr[j+1];
					arr[j+1] = arr[j];
					arr[j] = temp;
				}
			}
		}
		for (int i : arr) {
			System.out.println(i);
		}
	}
}
