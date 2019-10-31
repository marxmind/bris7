package com.italia.marxmind.bris.controller;

public class Test {

	public static void main(String[] args) {
		
		StringBuilder sb = new StringBuilder("<marcos> is this for you");
        //sb.append("01-02-2013");

        sb.replace(sb.indexOf("<marcos>"), sb.indexOf("<marcos>")+8, "mark") ;
        
        System.out.println(sb.toString());
	}
	
}
