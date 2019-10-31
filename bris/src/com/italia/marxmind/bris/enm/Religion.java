package com.italia.marxmind.bris.enm;

/**
 * 
 * @author mark italia
 * @since 10/11/2017
 * @version 1.0
 *
 */
public enum Religion {
	
	NONE(0, "NONE"),
	ROMAN_CATHOLIC(1, "ROMAN CATHOLIC"),
	PROTESTANT(2, "PROTESTANT"),
	INC(3,"IGLESIA NI KRISTO"),
	AGLIPAY(4,"AGLIPAY"),
	ISLAM(5,"ISLAM"),
	BAPTIST(6,"BAPTIST"),
	OTHERS(7, "OTHERS");
	
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private Religion(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(Religion type : Religion.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return Religion.ROMAN_CATHOLIC.getName();
	}
	public static int typeId(String name){
		for(Religion type : Religion.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return Religion.ROMAN_CATHOLIC.getId();
	}
	
}
