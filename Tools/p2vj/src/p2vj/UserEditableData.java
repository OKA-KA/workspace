package p2vj;


import java.util.*;
public class UserEditableData {
	public static final int UI_TEXTFIELD = 0;
	public static final int UI_FILE = 1;
	public static final int UI_OTHERS = 2;
	
	
	Hashtable<String,String> data = new Hashtable<String,String>();	
	ArrayList<String> order = new ArrayList<String>();
	Hashtable<String,Integer> uiChange = new Hashtable<String,Integer>();
	Hashtable<String,String> choice = new Hashtable<String,String>();
}



