package p2vj;

import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

public class BitMapVectorizer{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int GAUS_SIZE_DEFAULT = 3;
	public static final int GAUS_EFFECT_DEFAULT = 10;
	public static boolean cancelPressed = false;
	public static int THREADWAIT = 0;
	public static PolycoCanvas getAllPath(BufferedImage bi,double gaus_size,double gaus_effect,int posterization,boolean removeantialiase,boolean multithreading){
		return getAllPath(bi,gaus_size,gaus_effect,posterization,removeantialiase,multithreading,null,false);
	}
	public static PolycoCanvas getAllPath(BufferedImage bi,double gaus_size,double gaus_effect,int posterization,boolean removeantialiase,boolean multithreading,JLabel progresslabel,boolean binaryalpha){
		cancelPressed = false;
		PolycoCanvas ret = new PolycoCanvas(bi.getWidth(),bi.getHeight(),bi.getWidth(),bi.getHeight());
		
		if(binaryalpha){
			ImagePosterizer.binaryAlpha(bi,64);
		}
		
		if(posterization > 0){
			if(progresslabel != null){
				progresslabel.setText("posterizing...");
				progresslabel.repaint();
			}else{
				System.err.println("posterizing...");
			}
			
			ImagePosterizer.selectiveGaussianBlur(bi,2,48);
			//ImageIOWrapper.saveToFile(bi,"posterized1b.png");
			//ImagePosterizer.contrastPosterization(bi,0.8,true,9);
			//ImageIOWrapper.saveToFile(bi,"posterized1x.png");
			//ImagePosterizer.advancedPosterization(bi,posterization);
			ImagePosterizer.hardPosterization(bi,posterization,5);
			//ImageIOWrapper.saveToFile(bi,"posterized1c.png");
			ImagePosterizer.removeAntiAliasing(bi,3,32);
		
		}
		if(removeantialiase){
			if(progresslabel != null){
				progresslabel.setText("removing antialiases...");
				progresslabel.repaint();
			}else{
				System.err.println("removing antialiases...");
			}
			ImagePosterizer.removeAntiAliasing(bi,3,32);
			//ImageIOWrapper.saveToFile(bi,"posterized1z1.png");
			ImagePosterizer.removeSmallDots(bi,48,3);
			//ImageIOWrapper.saveToFile(bi,"posterized1z2.png");
			//ImagePosterizer.hardPosterization(bi,4096,16);
			ImagePosterizer.removeSmallDots(bi,128,1);
			ImagePosterizer.removeSmallDots(bi,12,-1);
			//ImageIOWrapper.saveToFile(bi,"posterized1z3.png");
		}
		final Hashtable<Integer,Integer> colortable = getColors(bi);
		Enumeration<Integer> en = colortable.keys();
		int maxnum = 0;
		ArrayList<Integer> lowalpha = new ArrayList<Integer>();
		while(en.hasMoreElements()){
			int cc = en.nextElement();
				Color pcc = new Color(cc);
			if(pcc.getAlpha() < 128){
				lowalpha.add(cc);
			}
			if(colortable.get(cc) > maxnum){
				maxnum = colortable.get(cc);
			}
		}
		
		for(int ii = 0;ii < lowalpha.size();ii++){
			colortable.put(lowalpha.get(ii),maxnum+20+ii);
		}
		if(colortable.get(Color.black.getRGB()) != null){
			colortable.put(Color.black.getRGB(),1);
		}
		
		class ColorPixComparator implements java.util.Comparator<Integer>{
			public int compare(Integer o1, Integer o2){
			return colortable.get((Integer)o2) - colortable.get((Integer)o1);
			}
		}
		
		
		Integer[] coli = colortable.keySet().toArray(new Integer[colortable.keySet().size()]);
		Arrays.sort(coli,new ColorPixComparator());
		for(int ii = 0;ii <coli.length;ii++){
			Color c = new Color(coli[ii],true);
			//System.out.println(String.valueOf(colortable.get(coli[ii]))+";"+String.valueOf(c.getRed())+";"+String.valueOf(c.getGreen())+";"
			//+String.valueOf(c.getBlue())+";"+String.valueOf(c.getAlpha()));
		}
		
		PPLayer layer = new PPLayer(ret,false);
		
		if(progresslabel != null){
			progresslabel.setText("Tracing...");
			progresslabel.repaint();
		}else{
			System.err.println("tracing...");
		}
		BufferedImage tracingimg = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int ii = 0;ii <coli.length ;ii++){
			if(cancelPressed){
				if(progresslabel != null){
					progresslabel.setText("canceled");
					progresslabel.repaint();
				}
				return null;
			}
			if(THREADWAIT > 0){
				try{
					Thread.sleep(THREADWAIT);
				}catch(Exception exx){
					exx.printStackTrace();
				}
			}
			colortable.put(coli[ii],colortable.get(coli[ii])+coli.length-ii);
			BitMapTracer.treatBoundaryOverlapping(bi,tracingimg,(Integer)coli[ii],colortable,1);
			

			//BufferedImage img = bi;
			//System.out.println("==================================================");
			if(progresslabel != null){
				progresslabel.setText("Vectorizing "+String.valueOf(ii+1)+"/"+String.valueOf(coli.length));
				progresslabel.repaint();
			}else{
				if(coli.length > 20){
					if((ii%(coli.length/10)) == 0){
						System.err.println("Vectorizing "+String.valueOf(ii+1)+"/"+String.valueOf(coli.length));
					}
				}else{
					System.err.println("Vectorizing "+String.valueOf(ii+1)+"/"+String.valueOf(coli.length));
				}
			}
			Color dcc = new Color(coli[ii],true);
			if(dcc.getAlpha() > 5){//PDF は不透明度ないみたいなので、ほぼ透明なものは追加しない。
				PathGroup pg = getOutlineOf(tracingimg,dcc,gaus_size,gaus_effect);
				pg.setColors(dcc,dcc);
				pg.setFlags(false,true);
				layer.addItem(pg);
			}
		}
		ret.addLayer(layer);
		if(progresslabel != null){
			progresslabel.setText("completed");
			progresslabel.repaint();
		}else{
			System.err.println("completed");
			
		}
		return ret;
	}
	public static Hashtable<Integer,Integer> getColors(BufferedImage src){
		Hashtable<Integer,Integer> ret = new Hashtable<Integer,Integer>();
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				if(ret.get(colors[xx]) != null){
					ret.put(colors[xx],ret.get(colors[xx])+1);
				}else{
					ret.put(colors[xx],+1);
				}
			}
		}
		return ret;
	}
	
	public static PathGroup getOutlineOf(BufferedImage bi,Color c,double gaus_size,double gaus_effect){
		ArrayList<PointList> al = BitMapTracer.traceColorBoundary(bi,c);
		ArrayList<PointList> allcurves = new ArrayList<PointList>();
		PathGroup pg = new PathGroup();
			
		//System.out.println(String.valueOf(c.getRed()));
		for(int ii = 0;ii < al.size();ii++){
			double plen = al.get(ii).getLength();//----------these parts need consideration...
			double roundrange = Math.max(Math.min(plen/10,gaus_size),0.8);
			double cornerround = Math.max(Math.min(plen/10,4.0),1.5);
			double cornerangle = Math.max(Math.min(plen/30,2.0),1.8);
			///double cornerround = 0.8;
			//double cornerangle = 1.6;
			double minlen = Math.max(Math.min(plen/10,1.5),1.0);
			//ArrayList<PointList> pal = CurveReviser.breakAtCorner(BitMapTracer.correctOneDot(al.get(ii).list,true),minlen,1.5,cornerround,true);
			ArrayList<PointList> pal = CurveReviser.breakAtCorner(BitMapTracer.correctOneDot(al.get(ii).list,true),cornerangle,1.5,cornerround,true);
			LAnchorPoint last = null;
			LAnchorPoint first = null;
			
			
			for(int jj = 0;jj < pal.size();jj++){
				ArrayList<FlexPoint> sal = CurveReviser.breakLine(pal.get(jj).list,0.5);
				
				
				
				
				
				//smooth curve 
				ArrayList<FlexPoint> tal = CurveReviser.reviseCurve_Gaussian_Advance(sal//CurveReviser.reviseCurve(sal)
				,Math.min(gaus_size,roundrange),gaus_effect,(pal.size() == 1 && pal.get(jj).isClosed()));
				
				
				for(int kk = 0;kk < tal.size();kk++){
					double dist = sal.get(kk).getLength(tal.get(kk));
					if(dist > 0.5){
						FlexPoint p1 = sal.get(kk);
						FlexPoint p2 = tal.get(kk);
						
						p2.set((p2.x-p1.x)/dist*0.5+p1.x,(p2.y-p1.y)/dist*0.5+p1.y);
						
					}
				}
				
				
				
				ArrayList<LAnchorPath> lal = BezierTracer.traceAllPoints(tal);
				
				//last = null;
				for(int kk = 0;kk < lal.size();kk++){
					LAnchorPath lap = lal.get(kk);
					LAnchorPoint pre = lap.prev;
					LAnchorPoint nex = lap.next;
					pre.setNext(null);
					if(last != null){
						last.meltWith(pre);
					}else{
						pg.addPoint(pre,true);
						first = pre;
						
					}
					pg.addPoint(nex);
					last = nex;
				//System.out.println(String.valueOf(kk)+":"+String.valueOf(lap.prev.shapePoint[LAnchorPoint.MAIN].x)+";"+String.valueOf(lap.prev.shapePoint[LAnchorPoint.MAIN].y));
				//System.out.println(String.valueOf(kk)+":"+String.valueOf(lap.next.shapePoint[LAnchorPoint.MAIN].x)+";"+String.valueOf(lap.next.shapePoint[LAnchorPoint.MAIN].y));
				}
			}
			//first = null;
			if(first != null){
				first.meltWith(last);
			}
		}
		
		pg.refreshAll();
		return pg;
	}
	public static void main(String args[]){
		try{
			BufferedImage img = ImageIO.read(new File("homete2.png"));
			//BufferedImage img = ImageIO.read(new File("testcolor2.png"));
			SVGParser.saveToFile("test.svg",getAllPath(img,3,10,6,true,false));
		}catch(Exception exx){
			exx.printStackTrace();
			
		}
		
	}
}

