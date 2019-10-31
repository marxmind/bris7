package com.italia.marxmind.bris.application;

import com.italia.marxmind.bris.controller.DocumentFormatter;
import com.italia.marxmind.bris.controller.DocumentPrintingV6;
import com.italia.marxmind.bris.enm.DocStyle;

/**
 * 
 * @author Mark Italia
 * @since 08/03/2018
 * @version 1.0
 *
 */

public class GlobalReportHandler {
	
	public enum GlobalReport{
		
		TRANSMITAL_LETTER(1),
		BANK_LETTER(2);
		
		private int id;
		
		public int getId() {
			return id;
		}
		
		private GlobalReport(int id) {
			this.id = id;
		}
	}
	
	public static String report(GlobalReport rpt) {
		String docStyle = DocumentFormatter.getTagName("documentLayout");
		String report = "";
		if(DocStyle.V5.getName().equalsIgnoreCase(docStyle)) {
			
		}else if(DocStyle.V6.getName().equalsIgnoreCase(docStyle)) {
				switch(rpt.getId()) {
				case 1:
					report = DocumentFormatter.getTagName("v6_transmittal_letter"); break;
				case 2:
					report = DocumentFormatter.getTagName("v6_bank_letter");	break;
				}
		}
		
		return report;
	}
	
}
