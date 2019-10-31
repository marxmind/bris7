package com.test;

public abstract class Test {
	
	private static String a = "20";
	
	public static void main(String[] args) {
		
		Mother m = new Son();
		m.setName(a);
		Son s = (Son)m;
		System.out.println(s.getName());
		s.getAll();
		
		
	}
	
	
}
