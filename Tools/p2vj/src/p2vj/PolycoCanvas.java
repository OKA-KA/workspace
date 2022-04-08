package p2vj;

public class PolycoCanvas extends PaintersCanvas{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!

	double boundingOffsetX = 0.0;
	double boundingOffsetY = 0.0;
	double boundingWidth = 1280;
	double boundingHeight = 1024;
	double boundingRatio = 1.0;
	PolycoCanvas(){
		this(1280,1024,1280,1024);
	}
	PolycoCanvas(int ww,int hh,int vw,int vh){
		super(ww,hh,vw,vh);
		boundingWidth = ww;
		boundingHeight = hh;
		boundingRatio = 1.0;
	}
	PolycoCanvas(double box,double boy,double bx,double by,double br,int ww,int hh,int vw,int vh){
		super(ww,hh,vw,vh);
		boundingOffsetX = box;
		boundingOffsetY = boy;
		boundingWidth = bx;
		boundingHeight = by;
		boundingRatio = br;
	}
}