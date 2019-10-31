package com.italia.marxmind.bris.reports;

public enum ReportTag {

	BUSINESS_CLEARANCE("bizclearance"),
	FISHCAGE_CLEARANCE("cageclearance"),
	ASSISTANCE_CLEARANCE("assclearance"),
	OTHERS_CLEARANCE("othclearance"),
	BID("bid"),
	CHECK("check"),
	BLOTTER("blotters"),
	LARGE_CATTLE("largecattle"),
	LATE_BIRTH("latebirthreg"),
	BARANGAY_BUSINESS_PERMIT("businesspermit"),
	AUTHORIZATION("authorization"),
	BANK_REPORT("bankreport"),
	LAND_REPORT("land"),
	CASES_REPORT("cases"),
	CASE_NOTICE("casenotice"),
	CASE_ENDORSEMENT("endorsementcase"),
	DOCUMENT_OPEN_TITLE("docopentitle");
	
	private String name;
	
	public String getName(){
		return name;
	}
	
	private ReportTag(String name){
		this.name = name;
	}
	
}