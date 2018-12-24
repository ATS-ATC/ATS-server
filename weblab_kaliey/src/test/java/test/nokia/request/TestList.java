package test.nokia.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestList {
	public static void main(String[] args) {
	    List<String> list1 = new ArrayList<String>();
	    list1.add("A");
	    list1.add("B");
	    list1.add("C");

	    List<String> list2 = new ArrayList<String>();
	    list2.add("C");
	    list2.add("B");
	    list2.add("D");
	    // 并集
	    //list1.addAll(list2);
	    // 去重复并集
	    //list2.removeAll(list1);
	    //list1.addAll(list2);
	    // 交集
	    list1.retainAll(list2);
	    // 差集
	    //list1.removeAll(list2);
	    System.out.println(list1);
	}
	
	@Test
	public void testMapList() {
		List<Map> list1 = new ArrayList<Map>();
		Map map1 = new HashMap<>();
		map1.put("A", "a");
		map1.put("B", "b");
		map1.put("C", "c");
		list1.add(map1);
		List<Map> list2 = new ArrayList<Map>();
		Map map2 = new HashMap<>();
		map2.put("B", "b");
		map2.put("C", "c");
		map2.put("D", "d");
		list2.add(map2);
		
		// 并集
	    list1.addAll(list2);
	    // 去重复并集
	    //list2.removeAll(list1);
	    //list1.addAll(list2);
	    // 交集
	    //list1.retainAll(list2);
	    // 差集
	    //list1.removeAll(list2);
	    System.out.println(list1);
	}
}
