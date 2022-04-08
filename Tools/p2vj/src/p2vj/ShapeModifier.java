package p2vj;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;

public class ShapeModifier{
	
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	
	
	
	
	
	public static PathGroup changeShapeToPath(Shape ss){
		return changeShapeToPath(new PathGroup(),ss);
	}
	public static PathGroup changeShapeToPath(PathGroup pg,Shape ss){
		return changeShapeToPath(pg,ss,false);
	}
	public static PathGroup changeShapeToPath(PathGroup pg,Shape ss,boolean modi){
		if(ss == null)return pg;
		AffineTransform af = new AffineTransform();
		if(modi){
			af.concatenate(new AffineTransform(1,0,0,1,-FlexPoint.getOffsetX(),-FlexPoint.getOffsetY()));
			af.concatenate(new AffineTransform(1/FlexPoint.getExpand(),0,0,1/FlexPoint.getExpand(),0,0));
		}
		PathIterator pite = ss.getPathIterator(af);
		
		float dd[] = new float[6];
		pite.currentSegment(dd);
		LAnchorPoint newla = new LAnchorPoint(dd[0],dd[1]);
		pite.next();
		LAnchorPoint lastla = newla;
		boolean nflag=true;
		while(!pite.isDone()){
			int typ = pite.currentSegment(dd);
			switch(typ){
				case PathIterator.SEG_CLOSE:
				{
					
					LAnchorPoint xla = (LAnchorPoint)pg.currentChain.start;
					if(xla.shapePoint[LAnchorPoint.MAIN].equals(lastla.shapePoint[LAnchorPoint.MAIN])){
						xla.moveTo(LAnchorPoint.PREV,lastla.shapePoint[LAnchorPoint.PREV].x,lastla.shapePoint[LAnchorPoint.PREV].y);
						lastla = null;
					}else {
						pg.addPoint(lastla);
						lastla = null;
					}
					pg.currentChain.closePath();
				}
				break;
				case PathIterator.SEG_CUBICTO:{
					newla = new LAnchorPoint(dd[4],dd[5]);
					
					lastla.moveTo(LAnchorPoint.NEXT,dd[0],dd[1]);
					newla.moveTo(LAnchorPoint.PREV,dd[2],dd[3]);
					
					pg.addPoint(lastla,nflag);
					lastla = newla;
					nflag = false;
				}
				break;
				
				case PathIterator.SEG_LINETO:{
					newla = new LAnchorPoint(dd[0],dd[1]);
					
					pg.addPoint(lastla,nflag);
					
					lastla = newla;
					nflag = false;
				}
				break;
				
				case PathIterator.SEG_MOVETO:
				{	
					pg.addPoint(lastla);
					if(pg.chainList.size()>0){
						//pg.currentChain.closePath();
						pg.setCurrentChain(null);
					}
					newla = new LAnchorPoint(dd[0],dd[1]);
					nflag = true;
					lastla = newla;
				}
				break;
				
				case PathIterator.SEG_QUADTO:{
					newla = new LAnchorPoint(dd[2],dd[3]);
					
					
					lastla.moveTo(LAnchorPoint.NEXT,
					(dd[0]*2+lastla.shapePoint[LAnchorPoint.MAIN].x)/3.0,
					(dd[1]*2+lastla.shapePoint[LAnchorPoint.MAIN].y)/3.0
					);
					newla.moveTo(LAnchorPoint.PREV,
					(dd[0]*2+dd[2])/3.0,
					(dd[1]*2+dd[3])/3.0
					);
					
					
					
					newla.moveTo(LAnchorPoint.NEXT,dd[2],dd[3]);
					
					pg.addPoint(lastla,nflag);
					
					lastla = newla;
					nflag = false;
				}
				break;
				
				default:
				
				System.err.println("exception in Font calculation. Undefined Method. ");
				break;
				
			}
			pite.next();
		}
		if(lastla != null){
			pg.addPoint(lastla,nflag);
		}
		pg.makePathLine();
		return pg;
	}
		
}