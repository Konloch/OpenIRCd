package com.konloch;

import com.konloch.irc.server.data.serializer.CollectionSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to test the API
 *
 * @author Konloch
 * @since 2/14/2023
 */
public class SerializerTest
{
	public static void main(String[] args)
	{
		CollectionSerializer serializer = new CollectionSerializer();
		
		List<String> stringList = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			stringList.add("Name-" + i);
		
		//serialize
		String stringListTest = serializer.serialize(stringList);
		
		System.out.println("String List Test:\n" + stringListTest);
		
		//TODO deserialize collections
		
		List<TestData> simpleDataTest = new ArrayList<>();
		for(int i = 0; i < 10; i++)
			simpleDataTest.add(new TestData("Account-" + i));
		
		//serialize
		String simpleTest = serializer.serialize(simpleDataTest);
		
		System.out.println("Account List Test:\n" + simpleTest);
		
		//TODO deserialize collections
		
		if(true)
			return;
		
		//build test map
		HashMap<String, TestData> complexData = new HashMap<>();
		
		//create test entry-#0
		TestData user = new TestData("#0 Depth-1");
		complexData.put("Map Entry #0", user);
		
		//create test entry-#1
		user = new TestData("#1 Depth-1");
		
		user.setEmpty = "";
		user.setNull = null;
		
		TestData recursiveFun = user;
		for(int i = 2; i < 3_002; i++)
		{
			recursiveFun = (recursiveFun.recursiveTest = new TestData("#1 Depth-" + i));
		}
		
		//user.testNewLines = "\n\n\n\nnew line test\r\r\r";
		
		//store test entry
		complexData.put("Map Entry #1", user);
		
		//serialize
		String test = serializer.serialize(complexData);
		
		System.out.println("Account Map Test:\n" + test);
		
		HashMap<String, TestData> dataDeserialized = new HashMap<>();
		
		long ms = System.currentTimeMillis();
		serializer.deserializeHashMap(test, dataDeserialized);
		long diff = System.currentTimeMillis()-ms;
		
		System.out.println("TOOK: " + diff + " ms");
		
		System.out.println();
		recursiveFun = user;
		for(int i = 2; i < 102; i++)
		{
			System.out.println("RECURSIVE TEST: " + recursiveFun.email);
			recursiveFun = recursiveFun.recursiveTest;
		}
	}
	
	public static class TestData
	{
		private final String email;
		private String setNull = "should be null";
		private String setEmpty = "should be empty";
		private String testNewLines = "\n\n\n\nbreaks\n\n\n";
		private boolean boolTest = true;
		private byte byteTest = 1;
		private short shortTest = 1;
		private int integerTest = 1;
		private long longTest = 1;
		private double doubleTest = 1;
		private float floatTest = 1;
		private EnumTest enums = EnumTest.VALUE_1;
		
		//TODO
		//private int[] wazza = new int[]{1,2,3,4,5,6};
		//private ArrayList<String> arrayList = new ArrayList<>();
		//`private HashMap<String, String> hashMap = new HashMap<>();
		
		private TestData recursiveTest;
		
		public TestData()
		{
			email = null;
		}
		
		public TestData(String email)
		{
			this.email = email;
		}
		
		@Override
		public String toString()
		{
			return email;
		}
	}
	
	public enum EnumTest
	{
		VALUE_1,
		VALUE_2,
	}
}
