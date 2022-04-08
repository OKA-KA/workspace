package p2vj;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.geom.GeneralPath;
import java.awt.Color;
import java.awt.BasicStroke;
import java.io.*;

public class LAnchorPath{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	LAnchorPoint next = null;
	LAnchorPoint prev = null;
	GeneralPath path;
	LAnchorPath(LAnchorPoint pre,LAnchorPoint nex){
		next = nex;
		next.prev = pre;
		next.p_prev = this;
		prev = pre;
		prev.next = nex;
		prev.p_next = this;
		path = new GeneralPath();
		makeLine();
	}
	public void makeLine(){
		path.reset();
		path.moveTo(prev.shapePoint[LAnchorPoint.MAIN].modix,prev.shapePoint[LAnchorPoint.MAIN].modiy);
		path.curveTo(prev.shapePoint[LAnchorPoint.NEXT].modix,prev.shapePoint[LAnchorPoint.NEXT].modiy
		,next.shapePoint[LAnchorPoint.PREV].modix,next.shapePoint[LAnchorPoint.PREV].modiy
		,next.shapePoint[LAnchorPoint.MAIN].modix,next.shapePoint[LAnchorPoint.MAIN].modiy
		);
	}
	public static GeneralPath getPath(LAnchorPoint l1,LAnchorPoint l2){
		
		GeneralPath ret = new GeneralPath();
		ret.moveTo(l1.shapePoint[LAnchorPoint.MAIN].modix,l1.shapePoint[LAnchorPoint.MAIN].modiy);
		ret.curveTo(l1.shapePoint[LAnchorPoint.NEXT].modix,l1.shapePoint[LAnchorPoint.NEXT].modiy
		,l2.shapePoint[LAnchorPoint.PREV].modix,l2.shapePoint[LAnchorPoint.PREV].modiy
		,l2.shapePoint[LAnchorPoint.MAIN].modix,l2.shapePoint[LAnchorPoint.MAIN].modiy
		);
		return ret;
	}
	public static GeneralPath getPath_d(LAnchorPoint l1,LAnchorPoint l2){//Flexをかけない
		
		GeneralPath ret = new GeneralPath();
		ret.moveTo(l1.shapePoint[LAnchorPoint.MAIN].x,l1.shapePoint[LAnchorPoint.MAIN].y);
		ret.curveTo(l1.shapePoint[LAnchorPoint.NEXT].x,l1.shapePoint[LAnchorPoint.NEXT].y
		,l2.shapePoint[LAnchorPoint.PREV].x,l2.shapePoint[LAnchorPoint.PREV].y
		,l2.shapePoint[LAnchorPoint.MAIN].x,l2.shapePoint[LAnchorPoint.MAIN].y
		);
		return ret;
	}
	public static FlexPoint getBezierPoint(LAnchorPoint prev,LAnchorPoint next,double tt){
		
		double[] x = {prev.shapePoint[LAnchorPoint.MAIN].x,prev.shapePoint[LAnchorPoint.NEXT].x
		,next.shapePoint[LAnchorPoint.PREV].x,next.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {prev.shapePoint[LAnchorPoint.MAIN].y,prev.shapePoint[LAnchorPoint.NEXT].y
		,next.shapePoint[LAnchorPoint.PREV].y,next.shapePoint[LAnchorPoint.MAIN].y};
		double nowx = getBezierPoint(x,tt);
		double nowy = getBezierPoint(y,tt);
		return new FlexPoint(nowx,nowy);
	}
	
	public static double getBezierPoint(double[] d,double t){
		return Math.pow(1-t,3)*d[0]+3*(1-t)*(1-t)*t*d[1]+3*(1-t)*t*t*d[2]+Math.pow(t,3)*d[3];
	}
	public ArrayList<FlexPoint> getOutlinePoints(int num,double size){
		double[] x = {prev.shapePoint[LAnchorPoint.MAIN].x,prev.shapePoint[LAnchorPoint.NEXT].x
		,next.shapePoint[LAnchorPoint.PREV].x,next.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {prev.shapePoint[LAnchorPoint.MAIN].y,prev.shapePoint[LAnchorPoint.NEXT].y
		,next.shapePoint[LAnchorPoint.PREV].y,next.shapePoint[LAnchorPoint.MAIN].y};
		int currentnum = 0;
		
		double lastx = getBezierPoint(x,0);
		double lasty = getBezierPoint(y,0);
		double step = getLength()/num;
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		double tmplen = 0;
		for(double dd = 0;dd < 1.0;dd += 0.0001){
			double nowx =getBezierPoint(x,dd);
			double nowy =getBezierPoint(y,dd);
			
			tmplen += Math.sqrt((lastx-nowx)*(lastx-nowx)+(lasty-nowy)*(lasty-nowy));
			if(tmplen >= step*currentnum){
				ret.add(FlexPoint.getRotatedPoint(new FlexPoint(getBezierPoint(x,dd+0.000000001)
				,getBezierPoint(y,dd+0.000000001))
				,new FlexPoint(nowx,nowy),Math.PI/2.0,size));
				currentnum++;
				
			}
			lastx = nowx;
			lasty = nowy;
		}
		
		ret.add(FlexPoint.getRotatedPoint(new FlexPoint(getBezierPoint(x,1.0-0.000000001)
					,getBezierPoint(y,1.0-0.000000001))
					,new FlexPoint(next.shapePoint[LAnchorPoint.MAIN].x,next.shapePoint[LAnchorPoint.MAIN].y),-Math.PI/2.0,size));
		
		return ret;
	}
	public GeneralPath getPath(){
		return path;	
	}
	public GeneralPath getPath_d(){
		return getPath_d(prev,next);	
	}
	public void paintBone(Graphics2D g2){
		g2.draw(path);
	}
	public void paintControlPoints(Graphics2D g2){
		if(next != null){
			next.paintBone(g2);
		}
		if(prev != null){
			prev.paintBone(g2);
		}
		
	}
	public double getLength(){
		double[] x = {prev.shapePoint[LAnchorPoint.MAIN].x,prev.shapePoint[LAnchorPoint.NEXT].x
		,next.shapePoint[LAnchorPoint.PREV].x,next.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {prev.shapePoint[LAnchorPoint.MAIN].y,prev.shapePoint[LAnchorPoint.NEXT].y
		,next.shapePoint[LAnchorPoint.PREV].y,next.shapePoint[LAnchorPoint.MAIN].y};
		double lastx = getBezierPoint(x,0);
		double lasty = getBezierPoint(y,0);
		double ret = 0;
		for(double dd = 0;dd <= 1.0;dd += 0.0001){
			double nowx = getBezierPoint(x,dd);
			double nowy = getBezierPoint(y,dd);
			
			ret += Math.sqrt((lastx-nowx)*(lastx-nowx)+(lasty-nowy)*(lasty-nowy));
			
			lastx = nowx;
			lasty = nowy;
		}
		
		return ret;
		
	}

	
	public static double getLength(double p1x,double p1y,double p2x,double p2y,double p3x,double p3y,double p4x,double p4y){
		return getLength(p1x,p1y,p2x,p2y,p3x,p3y,p4x,p4y,0.0001);
	}
	public static double getLength(double p1x,double p1y,double p2x,double p2y,double p3x,double p3y,double p4x,double p4y,double accuracy){
		double[] x = {p1x,p2x,p3x,p4x};
		double[] y = {p1y,p2y,p3y,p4y};
		double lastx = getBezierPoint(x,0);
		double lasty = getBezierPoint(y,0);
		double ret = 0;
		for(double dd = 0;dd <= 1.0;dd += accuracy){
			double nowx = getBezierPoint(x,dd);
			double nowy = getBezierPoint(y,dd);
			
			ret += Math.sqrt((lastx-nowx)*(lastx-nowx)+(lasty-nowy)*(lasty-nowy));
			
			lastx = nowx;
			lasty = nowy;
		}
		
		return ret;
		
	}
	public static void main(String[] args){
		for(int jj = 0;jj < 10;jj++){
			FlexPoint fp[] = new FlexPoint[4];
			double plen = 0;
			for(int ii = 0;ii < 4;ii++){
				fp[ii] = new FlexPoint(Math.random()*500,Math.random()*500);
				
			}
			
			double ppx[] = {fp[0].x,(fp[0].x+fp[1].x)/2,((fp[0].x+fp[1].x)/2+(fp[1].x+fp[2].x)/2)/2
			,((fp[2].x+fp[3].x)/2+(fp[1].x+fp[2].x)/2)/2,(fp[2].x+fp[3].x)/2,fp[3].x};
			
			double ppy[] = {fp[0].y,(fp[0].y+fp[1].y)/2,((fp[0].y+fp[1].y)/2+(fp[1].y+fp[2].y)/2)/2
			,((fp[2].y+fp[3].y)/2+(fp[1].y+fp[2].y)/2)/2,(fp[2].y+fp[3].y)/2,fp[3].y};
			for(int ii = 1;ii < ppy.length;ii++){
				plen += Math.sqrt((ppx[ii]-ppx[ii-1])*(ppx[ii]-ppx[ii-1])+(ppy[ii]-ppy[ii-1])*(ppy[ii]-ppy[ii-1]));
			}
			
			
			System.err.println(plen);
			System.err.println((plen+getLength(fp[0].x,fp[0].y,fp[1].x,fp[1].y,fp[2].x,fp[2].y,fp[3].x,fp[3].y,0.5))/2);
			System.err.println(getLength(fp[0].x,fp[0].y,fp[1].x,fp[1].y,fp[2].x,fp[2].y,fp[3].x,fp[3].y,0.25));
			System.err.println(getLength(fp[0].x,fp[0].y,fp[1].x,fp[1].y,fp[2].x,fp[2].y,fp[3].x,fp[3].y,0.0001));
			System.err.println("----------------");
			
		}
	}
	
	
}