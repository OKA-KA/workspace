package p2vj;

import java.awt.*;

public class ColorModifier{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int HSV_H = 0;
	public static final int HSV_S = 1;
	public static final int HSV_V = 2;
	public static final int HSV_A = 3;
	
	
	public static final int CHANGECODE_BRIGHTNESS = 0;
	public static final int CHANGECODE_CONTRAST = 1;
	public static final int CHANGECODE_ALPHA = 2;
	
	public static Color getRandomColor(){
		return getRandomColor(false);
	}
	public static Color getRandomColor(boolean monoflag){
		if(monoflag){
			int dark = (int)(Math.random()*256);
			return new Color(dark,dark,dark);
		}else{
			return new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
			
		}
	}
	public static String getHexColor(Color cc){
		
		String cs = "";
		cs = Integer.toHexString(cc.getRed()*65536+cc.getGreen()*256+cc.getBlue());
		while(cs.length() < 6){
			cs = "0"+cs;
		}
		return cs;
	}
	
	public static String getHexColor_Alpha(Color cc){
		
		String cs = "";
		cs = Integer.toHexString(cc.getAlpha()*16777216+cc.getRed()*65536+cc.getGreen()*256+cc.getBlue());
		while(cs.length() < 8){
			cs = "0"+cs;
		}
		return cs;
	}
	
	public static double[] colorToHSV(Color cc){
		double[] ret = new double[4];
		double r = cc.getRed()/255.0;
		double g = cc.getGreen()/255.0;
		double b = cc.getBlue()/255.0;
		double max = Math.max(r,b);
		max = Math.max(max,g);	
		double min = Math.min(r,b);
		min = Math.min(min,g);
		
		if(max > min){
			if(r > g && r > b){
				ret[HSV_H] = 60*(g-b)/(max-min);
			}else if(g > b){
				ret[HSV_H] = 60*(b-r)/(max-min)+120;
			}else{
				ret[HSV_H] = 60*(r-g)/(max-min)+240;
			}
		}else{
			ret[HSV_H] = 0;
		}
		
		if(max > 0){
			ret[HSV_S] = (max-min)/max;
		}else{
			ret[HSV_S] = 0;
		}
		ret[HSV_V] = max;
		ret[HSV_A] = cc.getAlpha()/255.0;
		return ret;
	}
	public static Color changeColorElement(int changecode,Color c,double value){
		switch(changecode){
			case CHANGECODE_BRIGHTNESS:
			return changeBrightness(c,value);
			case CHANGECODE_CONTRAST:
			return changeContrast(c,value);
			case CHANGECODE_ALPHA:
			return changeAlpha(c,value);
		}
		System.err.println("Cannot find code:"+String.valueOf(changecode));
		return c;
	}
	
	public static Color changeAlpha(Color c,double value){
		int al = (int)(c.getAlpha()*value);
		
		
		if(value > 9999){
			al = 255;
		}
		return new Color(c.getRed(),c.getGreen(),c.getBlue(),Math.max(0,Math.min(255,al))); 
	}
	
	
	
	public static Color changeBrightness(Color cc,double dr){
		return changeBrightness(cc,dr,true);
	}
	public static Color changeBrightness(Color cc,double dr,boolean changesaturation){
		double[] cl = colorToHSV(cc);
		cl[HSV_V] += dr;
		if(cl[HSV_V] > 1.0 && changesaturation){
			cl[HSV_S] -= cl[HSV_V]-1.0;
			cl[HSV_S] = Math.max(cl[HSV_S],0.0);
		}
		cl[HSV_V] = Math.max(Math.min(cl[HSV_V],1.0),0);
		return HSVToColor(cl);
	}
	public static Color changeHue(Color cc,double dr){
		double[] cl = colorToHSV(cc);
		cl[HSV_H] += dr;
		while(cl[HSV_H] > 360){
			cl[HSV_H] -= 360;
		}
		while(cl[HSV_H] < 0){
			cl[HSV_H] += 360;
		}
		return HSVToColor(cl);
	}
	public static Color changeSaturation(Color cc,double val){
		double[] cl = colorToHSV(cc);
		cl[HSV_S] += val;
		cl[HSV_S] = Math.max(Math.min(cl[HSV_S],1.0),0);
		return HSVToColor(cl);
	}
	public static Color changeContrast(Color cc,double value){
		double[] cl = colorToHSV(cc);
		cl[HSV_V] = (value+1)*(cl[HSV_V]-0.5)+0.5;
		cl[HSV_V] = Math.max(Math.min(cl[HSV_V],1.0),0);
		cl[HSV_S] = (value+1)*(cl[HSV_S]);
		cl[HSV_S] = Math.max(Math.min(cl[HSV_S],1.0),0);
		
		return HSVToColor(cl);
	}
	
	public static Color HSVToColor(double h,double s,double v,double alpha){
		Color ret;
		double r,g,b;
		if(s == 0){
			return new Color((float)v,(float)v,(float)v,(float)alpha);
		}
		if(h > 180){
			b = v;
			if(h > 240){
				g = b*(1.0-s);
				r = (b-g)*(h-240)/60+g;
			}else{
				r = b*(1.0-s);
				g = (r-b)*(h-240)/60+r;
			}
			
		}else if(h > 60){
			g = v;
			if(h > 120){
				r = g*(1.0-s);
				b = (g-r)*(h-120)/60+r;
			}else{
				b = g*(1.0-s);
				r = (b-g)*(h-120)/60+b;
			}
		}else{
			r = v;
			if(h > 0){
				b = r*(1.0-s);
				g = (r-b)*(h)/60+b;
			}else{
				g = r*(1.0-s);
				b = (g-r)*(h)/60+g;
			}
		}
		
		r = Math.max(0.0,r);
		g = Math.max(0.0,g);
		b = Math.max(0.0,b);
		
		r = Math.min(1.0,r);
		g = Math.min(1.0,g);
		b = Math.min(1.0,b);
		return new Color((float)r,(float)g,(float)b,(float)alpha);
	

	}
	
	public static Color HSVToColor(double[] hsv){
		Color ret;
		double r,g,b;
		if(hsv[HSV_S] == 0){
			return new Color((float)hsv[HSV_V],(float)hsv[HSV_V],(float)hsv[HSV_V]);
		}
		if(hsv[HSV_H] > 180){
			b = hsv[HSV_V];
			if(hsv[HSV_H] > 240){
				g = b*(1.0-hsv[HSV_S]);
				r = (b-g)*(hsv[HSV_H]-240)/60+g;
			}else{
				r = b*(1.0-hsv[HSV_S]);
				g = (r-b)*(hsv[HSV_H]-240)/60+r;
			}
			
		}else if(hsv[HSV_H] > 60){
			g = hsv[HSV_V];
			if(hsv[HSV_H] > 120){
				r = g*(1.0-hsv[HSV_S]);
				b = (g-r)*(hsv[HSV_H]-120)/60+r;
			}else{
				b = g*(1.0-hsv[HSV_S]);
				r = (b-g)*(hsv[HSV_H]-120)/60+b;
			}
		}else{
			r = hsv[HSV_V];
			if(hsv[HSV_H] > 0){
				b = r*(1.0-hsv[HSV_S]);
				g = (r-b)*(hsv[HSV_H])/60+b;
			}else{
				g = r*(1.0-hsv[HSV_S]);
				b = (g-r)*(hsv[HSV_H])/60+g;
			}
		}
		
		r = Math.max(0.0,r);
		g = Math.max(0.0,g);
		b = Math.max(0.0,b);
		
		r = Math.min(1.0,r);
		g = Math.min(1.0,g);
		b = Math.min(1.0,b);
		if(hsv.length > 3){
			return new Color((float)r,(float)g,(float)b,(float)hsv[HSV_A]);
		}else{
			return new Color((float)r,(float)g,(float)b);
		}
	}
	
	public static void main(String[] args){
	}
	
	
}