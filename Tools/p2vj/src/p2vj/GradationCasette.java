package p2vj;

import java.awt.Graphics.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
class GradationCasette{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int pointsize = 3;
	RadialGradientPaint radial;
	LinearGradientPaint linear;
	MultipleGradientPaint modi;
	FlexPoint startPoint = new FlexPoint(0,0);
	FlexPoint endPoint = new FlexPoint(100,100);
	FlexPoint centerPoint = new FlexPoint(0,0);
	FlexPoint focusPoint = new FlexPoint(0,0);
	float radius = 0;
	AffineTransform trans;
	Shape xShape;
	GradationCasette(MultipleGradientPaint mg){
		setGradation(mg);
	}
	GradationCasette(GradationCasette grac){
		setGradation(grac);
	}
	public void setGradation(GradationCasette grac){
		if(grac.radial != null){
			setGradation(grac.radial);
		}else{
			setGradation(grac.linear);
		}
	}
	public void setGradation(MultipleGradientPaint mg){
		trans = mg.getTransform();
		if(mg.getClass() == RadialGradientPaint.class){
			setGradation((RadialGradientPaint)mg);
		}else if(mg.getClass() == LinearGradientPaint.class){
			setGradation((LinearGradientPaint)mg);
		}
		adjustZoom();
	}
	
	public void transPoints(AffineTransform aff){
		aff.transform(focusPoint,focusPoint);
		aff.transform(centerPoint,centerPoint);
		double motolen = FlexPoint.getLength(startPoint,endPoint);
		aff.transform(startPoint,startPoint);
		aff.transform(endPoint,endPoint);
		double ex = 0.1;
		if(motolen != 0){
			ex = FlexPoint.getLength(startPoint,endPoint)/motolen;
		}
		radius = (float)(radius*ex);
		adjustZoom();
	}
	public void setGradation(RadialGradientPaint rg){
		radial = rg;
		linear = null;
		centerPoint.set(rg.getCenterPoint().getX(),rg.getCenterPoint().getY());
		focusPoint.set(rg.getFocusPoint().getX(),rg.getFocusPoint().getY());
		radius = rg.getRadius();
		startPoint.set(centerPoint.x-radius,centerPoint.y-radius);
		endPoint.set(centerPoint.x+radius,centerPoint.y+radius);
	}
	public void moveAll(double xx,double yy){
		focusPoint.rMoveTo(xx,yy);
		centerPoint.rMoveTo(xx,yy);
		startPoint.rMoveTo(xx,yy);
		endPoint.rMoveTo(xx,yy);
		refresh();
	}
	
	public void setGradation(LinearGradientPaint lg){
		radial = null;
		linear = lg;
		startPoint.set(lg.getStartPoint().getX(),lg.getStartPoint().getY());
		endPoint.set(lg.getEndPoint().getX(),lg.getEndPoint().getY());
		
	}
	public void changeColorElement(int changecode,double val){
		if(radial != null){
			Color[] cc = radial.getColors();
			for(int ii = 0;ii < cc.length;ii++){
				cc[ii] = ColorModifier.changeColorElement(changecode,cc[ii],val);
			}
			radial = new RadialGradientPaint(centerPoint,radius,focusPoint,
			radial.getFractions(),cc,radial.getCycleMethod(),
			radial.getColorSpace(),radial.getTransform());
		}else{
			Color[] cc = linear.getColors();
			for(int ii = 0;ii < cc.length;ii++){
				cc[ii] = ColorModifier.changeColorElement(changecode,cc[ii],val);
			}
			linear = new LinearGradientPaint(startPoint,endPoint,linear.getFractions(),cc,linear.getCycleMethod(),
			linear.getColorSpace(),linear.getTransform());
		}
		adjustZoom();
	}
	
	public void changeContrast(double val){
		changeColorElement(ColorModifier.CHANGECODE_CONTRAST,val);
	}
	public void changeBrightness(double val){
		changeColorElement(ColorModifier.CHANGECODE_BRIGHTNESS,val);
	}
	public void adjustZoom(){
			startPoint.adjustZoom();
			endPoint.adjustZoom();
		if(radial != null){
			focusPoint.adjustZoom();
			centerPoint.adjustZoom();
			Point2D.Double foc = new Point2D.Double(focusPoint.modix,focusPoint.modiy);
			Point2D.Double cen = new Point2D.Double(centerPoint.modix,centerPoint.modiy);
			
			modi = new RadialGradientPaint(cen,(float)(Math.max(radius*FlexPoint.getExpand(),0.01)),foc,
			radial.getFractions(),radial.getColors(),radial.getCycleMethod(),
			radial.getColorSpace(),radial.getTransform());
		}else{
			Point2D.Double sta = new Point2D.Double(startPoint.modix,startPoint.modiy);
			Point2D.Double end = new Point2D.Double(endPoint.modix,endPoint.modiy);
			modi = new LinearGradientPaint(sta,end,linear.getFractions(),linear.getColors(),linear.getCycleMethod(),
			linear.getColorSpace(),linear.getTransform());
			
		}
	}
	public void refresh(){
		if(radial != null){
			
			radial = new RadialGradientPaint(centerPoint,radius,focusPoint,
			radial.getFractions(),radial.getColors(),radial.getCycleMethod(),
			radial.getColorSpace(),radial.getTransform());
		}else{
			linear = new LinearGradientPaint(startPoint,endPoint,linear.getFractions(),linear.getColors(),linear.getCycleMethod(),
			linear.getColorSpace(),linear.getTransform());
		}
		adjustZoom();
	}
	public MultipleGradientPaint getGradation(){
		return modi;
	}
	public void paintBone(Graphics g){
	}
	
	
	
}



