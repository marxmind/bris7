package com.italia.marxmind.bris.enm;

public enum EducationLevel {

	NO_GRADE(1, "NON GRADE"),
	ELEMENTARY(2, "ELEMENTARY"),
	SECONDARY(3, "SECONDARY"),
	POST_SECONDARY(4, "POST SECONDARY"),
	COLLEGE(5, "COLLEGE"),
	POST_GRADUATE(6, "POST GRADUATE");
	
	private int id;
	private String name;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	private EducationLevel(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public static String typeName(int id){
		for(EducationLevel type : EducationLevel.values()){
			if(id==type.getId()){
				return type.getName();
			}
		}
		return EducationLevel.NO_GRADE.getName();
	}
	public static int typeId(String name){
		for(EducationLevel type : EducationLevel.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getId();
			}
		}
		return EducationLevel.NO_GRADE.getId();
	}
	
}
