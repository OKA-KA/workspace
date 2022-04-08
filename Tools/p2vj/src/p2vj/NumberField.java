package p2vj;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
public class NumberField extends JTextField{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int MODE_INT = 0;
	public static final int MODE_FLOAT = 1;
	public static final int MODE_HEX = 2;
	public static final String limitString[] ={"-+0123456789 ","-+0123456789.eE ","-+0123456789abcdefABCDEF "}; 
	private int fieldMode = MODE_INT;
	NumberField(int mode){
		super();
		this.setPreferredSize(new Dimension(42,18));
		setFieldMode(mode);
	}
	public void setFieldMode(int mode){
		fieldMode = mode;
	}
	protected Document createDefaultModel() {
		return new NumberDocument();
	}
	public void setValue(double d){
		
		switch(fieldMode){
			case MODE_INT:
			setText(String.valueOf((int)d));
			break;
			case MODE_HEX:
			setText(String.valueOf((int)d));
			break;
			case MODE_FLOAT:
			setText(String.valueOf(d));
			break;
		}
		
	}
	public double getValue(){
		//String valstr = getText();
		//valstr = valstr.replaceAll(" ","");
		//if(valstr.toUpperCase().indexOf("E+") > -1){
		//	return 999999;
		//}else if(valstr.toUpperCase().indexOf("E-") > -1){
		//	return 0;
		//}
		try{
			switch(fieldMode){
				case MODE_INT:
				return Integer.parseInt(this.getText());
				case MODE_HEX:
				return Integer.parseInt(this.getText(),16);
				case MODE_FLOAT:
				return Double.parseDouble(this.getText());
			}
		}catch(Exception exx){
			exx.printStackTrace();
		}
		return 0.0;
	}
	class NumberDocument extends PlainDocument {
 		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str == null) {
				return;
			}
			String[] strarray = str.split("");
			StringBuffer sb = new StringBuffer();
			for (int ii = 0; ii < strarray.length; ii++) {
				if(limitString[fieldMode].indexOf(strarray[ii]) > -1){
					sb.append(strarray[ii]);
				}
             }
             super.insertString(offs,sb.toString(), a);
         }
     }
	
}