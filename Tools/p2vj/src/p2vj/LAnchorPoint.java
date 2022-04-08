package p2vj;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.Color;
public class LAnchorPoint{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int PREV = 1;
	public static final int MAIN = 0;
	public static final int NEXT = 2;
	public static final int pointSize = 4;
	FlexPoint shapePoint[] = new FlexPoint[3];
	boolean pointFlag[] = new boolean[3];
	LAnchorPoint next = null;
	LAnchorPoint prev = null;
	LAnchorPath p_next = null;
	LAnchorPath p_prev = null;
	int lastPress = MAIN;
	int lastFloat = MAIN;
	boolean lineVisible = false; 
	boolean selectedFlag = false;
	LAnchorPoint(FlexPoint fp){
		this(fp.x,fp.y);
	}
	LAnchorPoint(double xx,double yy){
		for(int ii = 0;ii < 3;ii++){
			shapePoint[ii] = new FlexPoint(xx,yy);
			pointFlag[ii] = false;
		}
		
		pointFlag[MAIN] = true;
		
	}
	LAnchorPoint(LAnchorPoint src){
		for(int ii = 0;ii < 3;ii++){
			shapePoint[ii] = new FlexPoint(src.shapePoint[ii]);
			pointFlag[ii] = src.pointFlag[ii];
		}
	}
	public int deletePoint(int num){
		if(num > MAIN){
			pointFlag[num] = false;
			shapePoint[num].set(shapePoint[MAIN]);
			return num;
		}else{
			return num;
		}
	}
	public int deletePoint(FlexPoint fp){
		for(int ii = 0;ii < 3;ii++){
			if(fp == shapePoint[ii]){
				return deletePoint(ii);
			}
		}
		return -1;
	}
	public void adjustZoom(){
		for(int ii = 0;ii < 3;ii++){
			shapePoint[ii].adjustZoom();
		}
	}
	public void paintBone(Graphics2D g2){
		lineVisible = false;
		g2.fill(new Rectangle2D.Double(shapePoint[LAnchorPoint.MAIN].modix-pointSize/2,
		shapePoint[LAnchorPoint.MAIN].modiy-pointSize/2,pointSize,pointSize));
	}
	public void paintBoneLine(Graphics2D g2){
		for(int ii = 1;ii < 3;ii++){
			paintBoneLine(g2,ii);
		}
		lineVisible = true;
	}
	public void paintBoneLine(Graphics2D g2,int num){
		if(num < 0){
			return;
		}
		if(num == MAIN){
			g2.draw(new Rectangle2D.Double(shapePoint[num].modix-pointSize/2,
			shapePoint[num].modiy-pointSize/2,pointSize,pointSize));
			
		}else if(pointFlag[num]){
			g2.draw(new Rectangle2D.Double(shapePoint[num].modix-pointSize/2,
			shapePoint[num].modiy-pointSize/2,pointSize,pointSize));
			g2.draw(new Line2D.Double(shapePoint[num].modix,shapePoint[num].modiy,shapePoint[MAIN].modix,shapePoint[MAIN].modiy));
		}
	}
	public void meltAndAppend(LAnchorPoint lp){
		shapePoint[NEXT].set(lp.shapePoint[NEXT]);
		setNext(lp.next);
	}
	public double getControlRadi(){
		FlexPoint radi1 = new FlexPoint(0,0);
		FlexPoint radi2 = new FlexPoint(0,0);
		
		if(FlexPoint.getLength(shapePoint[NEXT],shapePoint[MAIN]) < 0.000001){
			if(next != null){
				FlexPoint fp = getPointAt(this,next,0.01);
				radi1.set(fp.x-shapePoint[MAIN].x,fp.y-shapePoint[MAIN].y);
			}else{
				return -10;
			}
		}else{
			radi1.set(shapePoint[NEXT].x-shapePoint[MAIN].x,shapePoint[NEXT].y-shapePoint[MAIN].y);
		
		}
		if(FlexPoint.getLength(shapePoint[PREV],shapePoint[MAIN]) < 0.000001){
			if(prev != null){
				FlexPoint fp = getPointAt(this,prev,0.01);
				radi2.set(fp.x-shapePoint[MAIN].x,fp.y-shapePoint[MAIN].y);
			}else{
				return -10;
			}
		}else{
			radi2.set(shapePoint[PREV].x-shapePoint[MAIN].x,shapePoint[PREV].y-shapePoint[MAIN].y);
		}
		
		double rota1 = FlexPoint.getAngle(radi1.x,radi1.y);
		double rota2 = FlexPoint.getAngle(radi2.x,radi2.y);
		double tmpret = Math.abs(rota1-rota2);
		while(tmpret > Math.PI){
			tmpret = Math.abs(tmpret-Math.PI*2);
		}
		return Math.abs(tmpret);
	}
	public static double getBezierPoint(double[] d,double t){
		return Math.pow(1-t,3)*d[0]+3*(1-t)*(1-t)*t*d[1]+3*(1-t)*t*t*d[2]+Math.pow(t,3)*d[3];
	}
	public static LAnchorPoint getIntraPoint(double xx,double yy,LAnchorPoint p,LAnchorPoint n){
		//ポイントxx,yyに最も近いtを探し、それをgetNewInsideに渡してLAnchorPoint を得る
		p.adjustZoom();
		n.adjustZoom();
		double[] x = {p.shapePoint[LAnchorPoint.MAIN].x,p.shapePoint[LAnchorPoint.NEXT].x
		,n.shapePoint[LAnchorPoint.PREV].x,n.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {p.shapePoint[LAnchorPoint.MAIN].y,p.shapePoint[LAnchorPoint.NEXT].y
		,n.shapePoint[LAnchorPoint.PREV].y,n.shapePoint[LAnchorPoint.MAIN].y};
		
		double len = getPathLength(p,n);
		double slen = (len/FlexPoint.s_expand);
		
		double prog = Math.min(Math.max(len/80000.0,0.00001),0.01);
		double plenmin = (PathGroup.pointSize+10)/FlexPoint.s_expand+600;
		
		double lastx = p.shapePoint[LAnchorPoint.MAIN].x;
		double lasty = p.shapePoint[LAnchorPoint.MAIN].y;
		
		LAnchorPoint newlanc = null;
		
		for(double tt = prog;tt < 1.0;tt += prog){
			double nowx = getBezierPoint(x,tt);
			double nowy = getBezierPoint(y,tt);
			
			double plen = Math.sqrt((nowx-xx)*(nowx-xx)+(nowy-yy)*(nowy-yy));
			
			if(plen < (PathGroup.pointSize+2)/FlexPoint.s_expand && plenmin < plen){
				newlanc = getNewInside(p,n,tt);
				return newlanc;
			}
			
			plenmin = Math.min(plen,plenmin);
			lastx = nowx;
			lasty = nowy;
			
		}
		return null;
	}
	public void setNext(LAnchorPoint la){
		if(next != null){
			next.p_prev = null;
			next.prev = null;
		}
		if(la != null){
			next = la;
			if(la.prev != null)la.prev.setNext(null);
			la.prev = this;
		}else{
			p_next = null;
			next = null;
		}
	}
	public void paintBone_E(Graphics2D g2){
		paintBoneLine(g2);
		
		
		if(next != null){
			next.paintBoneLine(g2);
		}
		if(prev != null){
			prev.paintBoneLine(g2);
		}
	}
	
	public static LAnchorPoint getNewInside(LAnchorPoint prev,LAnchorPoint next,double step){
		//prev-next間のt=step位置にあるポイントでの新しいLAnchorPointを与え、
		//prev、nextもそれにあわせて腕を調節、かつflagtrueで新しいLAnchorPointを間に挿入する。
		
		double[] x = {prev.shapePoint[LAnchorPoint.MAIN].x,prev.shapePoint[LAnchorPoint.NEXT].x
		,next.shapePoint[LAnchorPoint.PREV].x,next.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {prev.shapePoint[LAnchorPoint.MAIN].y,prev.shapePoint[LAnchorPoint.NEXT].y
		,next.shapePoint[LAnchorPoint.PREV].y,next.shapePoint[LAnchorPoint.MAIN].y};
		
		double k = step;
		
		
		LAnchorPoint newlanc = new LAnchorPoint(0,0);
		prev.shapePoint[LAnchorPoint.NEXT].set(x[0]*(1-k)+k*x[1],y[0]*(1-k)+k*y[1]);
		
		newlanc.shapePoint[LAnchorPoint.PREV].set((x[0]-2*x[1]+x[2])*k*k-x[0]+2*prev.shapePoint[LAnchorPoint.NEXT].x
		,(y[0]-2*y[1]+y[2])*k*k-y[0]+2*prev.shapePoint[LAnchorPoint.NEXT].y);
		
		newlanc.shapePoint[LAnchorPoint.MAIN].set(Math.pow(1-k,3)*x[0]+3*(1-k)*(1-k)*k*x[1]+3*(1-k)*k*k*x[2]+Math.pow(k,3)*x[3],
		Math.pow(1-k,3)*y[0]+3*(1-k)*(1-k)*k*y[1]+3*(1-k)*k*k*y[2]+Math.pow(k,3)*y[3]);
		
		k = 1-step;
		next.shapePoint[LAnchorPoint.PREV].set(x[3]*(1-k)+k*x[2],y[3]*(1-k)+k*y[2]);
		
		newlanc.shapePoint[LAnchorPoint.NEXT].set((x[3]-2*x[2]+x[1])*k*k-x[3]+2*next.shapePoint[LAnchorPoint.PREV].x
		,(y[3]-2*y[2]+y[1])*k*k-y[3]+2*next.shapePoint[LAnchorPoint.PREV].y);
		newlanc.pointFlag[LAnchorPoint.PREV] = true;
		newlanc.pointFlag[LAnchorPoint.NEXT] = true;
		
		prev.setNext(newlanc);
		newlanc.setNext(next);
		
		return newlanc;
		
	}
	public int checkPoint(double xx,double yy){
		return checkPoint(xx,yy,false);
	}
	public void select(){
		setSelected(true);
	}
	public void deselect(){
		setSelected(false);
	}
	public boolean isSelected(){
		return selectedFlag;
	}
	public void setSelected(boolean f){
		selectedFlag = f;
	}
	public int checkPoint(double xx,double yy,boolean floatingflag){
		lastFloat = -1;
		if(lineVisible){
			for(int ii = 1;ii < 3;ii++){
				if(pointFlag[ii]){
					if((Math.abs(shapePoint[ii].x-xx)) < pointSize/2/FlexPoint.getExpand() && (Math.abs(shapePoint[ii].y-yy)) < pointSize/2/FlexPoint.getExpand()){
						lastFloat = ii;
						if(!floatingflag){
							lastPress = lastFloat;
						}
						return ii;
					}
				}
			}
		}
		if((Math.abs(shapePoint[MAIN].x-xx)) < pointSize/2/FlexPoint.getExpand() && (Math.abs(shapePoint[MAIN].y-yy)) < pointSize/2/FlexPoint.getExpand()){
			lastFloat = MAIN;
			if(!floatingflag){
				lastPress = lastFloat;
			}
			return MAIN;
		}
		return -1;
	}
	public void movePoint(double xx,double yy){
		if(lastPress == MAIN){
			moveTo(xx,yy);
		}else{
			shapePoint[lastPress].set(xx,yy);
			makeLine();
		}
	}
	public void changeHandLength(double ratio){
		shapePoint[NEXT].set((shapePoint[NEXT].x-shapePoint[MAIN].x)*ratio+shapePoint[MAIN].x,(shapePoint[NEXT].y-shapePoint[MAIN].y)*ratio+shapePoint[MAIN].y);
		shapePoint[PREV].set((shapePoint[PREV].x-shapePoint[MAIN].x)*ratio+shapePoint[MAIN].x,(shapePoint[PREV].y-shapePoint[MAIN].y)*ratio+shapePoint[MAIN].y);
		
	}
	
	public void moveTo(double xx,double yy){
		double rmovex = xx-shapePoint[MAIN].x;
		double rmovey = yy-shapePoint[MAIN].y;
		rMoveTo(rmovex,rmovey); 
	}
	public void moveTo(FlexPoint fp){
		moveTo(fp.x,fp.y);
	}
	public void rMoveTo(double xx,double yy){
		for(int ii = 0;ii < 3;ii++){
			shapePoint[ii].rMoveTo(xx,yy);
		}
		makeLine();
	}
	
	public static FlexPoint getPointAt(LAnchorPoint la1,LAnchorPoint la2,double step){
		
		double[] x = {la1.shapePoint[LAnchorPoint.MAIN].x,la1.shapePoint[LAnchorPoint.NEXT].x
		,la2.shapePoint[LAnchorPoint.PREV].x,la2.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {la1.shapePoint[LAnchorPoint.MAIN].y,la1.shapePoint[LAnchorPoint.NEXT].y
		,la2.shapePoint[LAnchorPoint.PREV].y,la2.shapePoint[LAnchorPoint.MAIN].y};
		double k = step;
		
		FlexPoint ret = new FlexPoint(Math.pow(1-k,3)*x[0]+3*(1-k)*(1-k)*k*x[1]+3*(1-k)*k*k*x[2]+Math.pow(k,3)*x[3],
		Math.pow(1-k,3)*y[0]+3*(1-k)*(1-k)*k*y[1]+3*(1-k)*k*k*y[2]+Math.pow(k,3)*y[3]);
		
		return ret;
		
	}
	public void screwTo(double xx,double yy){
		for(int ii = 1;ii < 3;ii++){
			pointFlag[ii] = true;
		}
		if(lastPress == PREV){
			shapePoint[PREV].set(xx,yy);
			shapePoint[NEXT].set(shapePoint[MAIN].x*2-xx,shapePoint[MAIN].y*2-yy);
		}else{
			shapePoint[NEXT].set(xx,yy);
			shapePoint[PREV].set(shapePoint[MAIN].x*2-xx,shapePoint[MAIN].y*2-yy);
		}
		makeLine();
	}
	public void moveTo(int num,double xx,double yy){
		if(num == MAIN){
			moveTo(xx,yy);
		}else{
		for(int ii = 1;ii < 3;ii++){
			pointFlag[ii] = true;
		}
			shapePoint[num].set(xx,yy);
			pointFlag[num] = true;
			makeLine();
		}
	}
	public boolean curveCheck(double xx,double yy){
		if(pointFlag[PREV] && pointFlag[NEXT]){
			return false;
		}else{
			if(!pointFlag[NEXT]){
				moveTo(NEXT,xx,yy);
				lastPress = NEXT;
			}else{
				moveTo(PREV,xx,yy);
				lastPress = PREV;
			}
			return true;
		}
	}
	public void reverse(){
		FlexPoint tmp = shapePoint[PREV];
		shapePoint[PREV] = shapePoint[NEXT];
		shapePoint[NEXT] = tmp;
		LAnchorPoint lap = next;
		next = prev;
		prev = lap;
		p_next = null;
		p_prev = null;
	}
	public void makeLine(){
		if(p_next != null)p_next.makeLine();
		if(p_prev != null)p_prev.makeLine();
	}
	public static double getPathLength(LAnchorPoint p,LAnchorPoint n){
				//1000分割でパスの長さを近似する。
		
		double[] x = {p.shapePoint[LAnchorPoint.MAIN].x,p.shapePoint[LAnchorPoint.NEXT].x
		,n.shapePoint[LAnchorPoint.PREV].x,n.shapePoint[LAnchorPoint.MAIN].x};
		double[] y = {p.shapePoint[LAnchorPoint.MAIN].y,p.shapePoint[LAnchorPoint.NEXT].y
		,n.shapePoint[LAnchorPoint.PREV].y,n.shapePoint[LAnchorPoint.MAIN].y};
		
		
		double lastx = p.shapePoint[LAnchorPoint.MAIN].x;
		double lasty = p.shapePoint[LAnchorPoint.MAIN].y;
		double length = 0.0;
		for(double tt = 0.001;tt < 1.0;tt += 0.001){
			double nowx = getBezierPoint(x,tt);
			double nowy = getBezierPoint(y,tt);
			length += Math.sqrt((nowx-lastx)*(nowx-lastx)+(nowy-lasty)*(nowy-lasty));
			lastx = nowx;
			lasty = nowy;
		}
		
		return length;
		
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
	public LAnchorPoint meltWith(LAnchorPoint lap){
		if(next == null && lap.prev == null){
			shapePoint[MAIN].x = (shapePoint[MAIN].x+lap.shapePoint[MAIN].x)/2;
			shapePoint[MAIN].y = (shapePoint[MAIN].y+lap.shapePoint[MAIN].y)/2;
			shapePoint[NEXT].set(lap.shapePoint[NEXT]);
			setNext(lap.next);
			return lap;
		}
		if(prev == null && lap.next == null){
			shapePoint[MAIN].x = (shapePoint[MAIN].x+lap.shapePoint[MAIN].x)/2;
			shapePoint[MAIN].y = (shapePoint[MAIN].y+lap.shapePoint[MAIN].y)/2;
			shapePoint[PREV].set(lap.shapePoint[PREV]);
			lap.prev.setNext(this);
			return lap;
		}
		
		
		if(next != null){
			System.err.println("$$$$");
			System.err.println("---"+String.valueOf(next.shapePoint[LAnchorPoint.MAIN].x)+";"+String.valueOf(next.shapePoint[LAnchorPoint.MAIN].y));
		}else{
			
			if(prev != null){
				System.err.println("****");
				System.err.println("---"+String.valueOf(prev.shapePoint[LAnchorPoint.MAIN].x)+";"+String.valueOf(prev.shapePoint[LAnchorPoint.MAIN].y));
			}
		}
		System.err.println("Cannot melt Point.");
		return null;
	}
	
	public void rotate(double radi,FlexPoint fp){
		rotate(radi,fp.x,fp.y);
	}
	public void rotate(double radi,double tx,double ty){
		for(int ii = 0;ii < 3;ii++){
			shapePoint[ii].rotate(radi,tx,ty);
		}
	}
	
}