package p2vj;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

public class FlexPoint extends Point2D.Double{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	static double s_expand = 1.0;
	static double s_offsetX = 0.0;
	static double s_offsetY = 0.0;
	
	
	static double save_expand = 1.0;
	static double save_offsetX = 0.0;
	static double save_offsetY = 0.0;
	double modix,modiy;
	static AffineTransform exaff = new AffineTransform(); 
	
	FlexPoint(){
		x = 0;
		y = 0;
		modix = 0;
		modiy = 0;
	}
	FlexPoint(double xx,double yy){
		this(xx,yy,false);
	}
	FlexPoint(double xx,double yy,boolean modiflag){
		if(modiflag){
			set_M(xx,yy);
		}else{
			set(xx,yy);
		}
	}
	FlexPoint(FlexPoint fp){
		this(fp,false);
	}
	FlexPoint(FlexPoint fp,boolean modiflag){
		if(modiflag){
			set_M(fp);
		}else{
			set(fp);
		}
	}
	public static AffineTransform getAffineTransform(){
		return exaff;
	
	}
	private static void setAffineTransform(){
		exaff = new AffineTransform(getExpand(),0,0,getExpand(),getOffsetX()*getExpand(),getOffsetY()*getExpand());
	
	}
	public double getModiX(){
		return x*s_expand+s_offsetX*s_expand;
	}
	public double getModiY(){
		return y*s_expand+s_offsetY*s_expand;
	}
	public static double getModiX(double xx){
		
		return xx*s_expand+s_offsetX*s_expand;
	}
	
	public static double getModiY(double yy){
		
		return yy*s_expand+s_offsetY*s_expand;
	}
	
	public void rMoveTo(double xx,double yy){
		x += xx;
		y += yy;
		adjustZoom();
	}
	public void rMoveTo_M(double xx,double yy){
		x += xx/s_expand;
		y += yy/s_expand;
		modix += xx;
		modiy += yy;
		
		
	}
	public void set(double xx,double yy){
		x = xx;
		y = yy;
		adjustZoom();
	}
	public void set(FlexPoint fp){
		x = fp.x;
		y = fp.y;
		adjustZoom();
	}
	public void set_M(FlexPoint fp){
		modix = fp.x;
		modiy = fp.y;
		x = fp.x/s_expand-s_offsetX;
		y = fp.y/s_expand-s_offsetY;
	}
	public void set_M(double xx,double yy){
		x = xx/s_expand-s_offsetX;
		y = yy/s_expand-s_offsetY;
		modix = xx;
		modiy = yy;
	}
	public static void setModiValues(double ex,double offx,double offy){
		s_expand = ex;
		s_offsetX = offx;
		s_offsetY = offy;
		setAffineTransform();
	}
	public static void formatModiValuesTMP(){
		save_expand = s_expand;
		save_offsetX = s_offsetX;
		save_offsetY = s_offsetY;
		setModiValues(1.0,0.0,0.0);
	}
	public static void recoverModiValues(){
		setModiValues(save_expand,save_offsetX,save_offsetY);
	}
	public static double getExpand(){
		return s_expand;
	}
	public static double getOffsetX(){
		return s_offsetX;
	}
	public static double getOffsetY(){
		return s_offsetY;
	}
	public void adjustZoom(){
		modix =  x*s_expand+s_offsetX*s_expand;
		modiy =  y*s_expand+s_offsetY*s_expand;
	}
	
	public void rotate(double radi,FlexPoint fp){
		rotate(radi,fp.x,fp.y);
	}
	public void rotate(double radi,double tx,double ty){
		double rx = (x-tx)*Math.cos(radi)-(y-ty)*Math.sin(radi);
		double ry = (x-tx)*Math.sin(radi)+(y-ty)*Math.cos(radi);
		this.set(rx+tx,ry+ty);
	}
	public static double getLength(FlexPoint f1,FlexPoint f2){
		return Math.sqrt((f1.getX()-f2.getX())*(f1.getX()-f2.getX())+(f1.getY()-f2.getY())*(f1.getY()-f2.getY()));
	
	}
	public static double getLength(double fx1,double fy1,double fx2,double fy2){
		return Math.sqrt((fx1-fx2)*(fx1-fx2)+(fy1-fy2)*(fy1-fy2));
	
	}
	public double getLength(FlexPoint f1){
		return FlexPoint.getLength(this,f1);
	}
	public static FlexPoint getRotatedPoint(FlexPoint p1,double radi){
		return getRotatedPoint(p1,radi,false);
	}
	public static FlexPoint getRotatedPoint(FlexPoint p1,FlexPoint p2,double radi,double size){
		FlexPoint tmp = getRotatedPoint(new FlexPoint(p1.x-p2.x,p1.y-p2.y),radi,true);
		tmp.set(tmp.x*size+p2.x,tmp.y*size+p2.y);
		return tmp;
	}
	
	public static double getAngle(Point2D.Double p1,Point2D.Double p2,Point2D.Double p3){
		return getAngle(p2.x-p1.x,p2.y-p1.y,p3.x-p1.x,p3.y-p1.y);
		
		
	}
	
	public static double getAngle(double x1,double y1,double x2,double y2){//get radian value (0.0-3.14)
	
		double rota1 = getAngle(x1,y1);
		double rota2 = getAngle(x2,y2);
		double tmpret = Math.abs(rota1-rota2);
		while(tmpret > Math.PI){
			tmpret = Math.abs(tmpret-Math.PI*2);
		}
		return Math.abs(tmpret);
	}
	public static double getAngle(double xx,double yy){//get radian value (0.0-6.28)
		double rr = Math.sqrt(xx*xx+yy*yy);
		double rx = Math.acos(xx/rr);
		double ry = Math.asin(yy/rr);
		double rot = 0.0;
		if(xx < 0.0){
			rot = Math.PI-ry;
		}else{
			if(yy < 0.0){
				rot = Math.PI*2+ry;
			}else{
				rot = ry;
			}
		}
		
		return rot;
	}
	public static FlexPoint getRotatedPoint(FlexPoint p1,double radi,boolean stdflag){
		FlexPoint ret = new FlexPoint(p1);
		double rx = p1.x*Math.cos(radi)-p1.y*Math.sin(radi);
		double ry = p1.x*Math.sin(radi)+p1.y*Math.cos(radi);
		if(stdflag){
			double val = Math.sqrt(rx*rx+ry*ry);
			if(val != 0){
				rx = rx/val;
				ry = ry/val;
			}
		}
		ret.set(rx,ry);
		return ret;
	}
	public static void main_(String args[]){
		FlexPoint fp = new FlexPoint(1,0);
		FlexPoint fp2 = getRotatedPoint(fp,3.14/3);
		//System.out.println(String.format("x = %f;y = %f;",fp2.x,fp2.y));
		
		//System.out.println(getAngle(1,0));
		//System.out.println(getAngle(1,1));
		//System.out.println(getAngle(0,1));
		//System.out.println(getAngle(-1,1));
		//System.out.println(getAngle(-1,0));
		//System.out.println(getAngle(-1,-1));
		//System.out.println(getAngle(0,-1));
		//System.out.println(getAngle(1,-1));
		
		
		
		
	}
}