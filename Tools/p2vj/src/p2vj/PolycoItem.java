package p2vj;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
public abstract class PolycoItem{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	static final int CODE_NONE = 0;
	static final int CODE_TEXT = 1;
	static final int CODE_PATH = 2;
	static final int CODE_IMAGE = 3;
	static int allnum = 0;
	GeneralPath outline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	GeneralPath outline_Selected = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	String name = "PolycoItem";
	String listHtml = "";
	Color fillColor = Color.black;
	Color drawColor = Color.black;
	BasicStroke bStroke =  new BasicStroke(1.0f);
	BasicStroke trueStroke =  new BasicStroke(1.0f);
	int classCode = CODE_NONE;
	int windingRule = GeneralPath.WIND_NON_ZERO;
	int number = 0;
	boolean fillFlag = true;
	boolean drawFlag = true;
	boolean visibleFlag = true;
	boolean deadFlag = false;
	GradationCasette gradation = null;
	AffineTransform extraTrans = new AffineTransform();
	ArrayList<PolycoGroup> parentGroup = new ArrayList<PolycoGroup>();
	PolycoItem(PolycoItem pi,boolean historyflag){
		copyFrom(pi,historyflag);
	}
	public void copyFrom(PolycoItem pi,boolean historyflag){
		if(historyflag){
			number = pi.number;
		}else{
			number = allnum++;
		}
		name = pi.name;
		copyColorFrom(pi);
		visibleFlag = pi.visibleFlag;
		classCode = pi.classCode;
		setStroke(pi.trueStroke);
		extraTrans = new AffineTransform(pi.extraTrans);
	}
	public void joinIn(PolycoGroup pg){
		if(!parentGroup.contains(pg)){
			parentGroup.add(pg);
		}
	}
	public PolycoItem getCopy(){
		return null;
	}
	public void excludeFrom(PolycoGroup pg){
		parentGroup.remove(pg);
	}
	PolycoItem(PolycoItem pi){
		this(pi,false);
	}
	PolycoItem(){
		number = allnum++;
	}
	public void makePathLine(){
	}
	public boolean isDead(){
		return deadFlag;
	}
	public ArrayList<FlexPoint> getSelectedPoints(){
		
		return new ArrayList<FlexPoint>();
	}
	public ArrayList<FlexPoint> getAllPoints(){
		return new ArrayList<FlexPoint>();
	}
	public void rotate(double rota,FlexPoint fp){
		rotate(rota,fp.x,fp.y);
	}
	public void rotate(double rota,double xx,double yy){
		ArrayList<FlexPoint> flist = new ArrayList<FlexPoint>();
		for(int ii = 0;ii < flist.size();ii++){
			flist.get(ii).rotate(rota,xx,yy);
			
		}
		
	}
	public void paint(Graphics2D g){
	}
	public boolean deleteSelected(){
		return false;
	}
	public void paintBone(Graphics2D g){
	}
	public void paintSelectedBone(Graphics2D g){
	}
	public void changeColorElement(int changecode,double value){
		
		if(fillFlag){
			if(gradation != null){
				gradation.changeColorElement(changecode,value);
			}else{
				fillColor = ColorModifier.changeColorElement(changecode,fillColor,value);
			}
		}
		
		if(drawFlag){
			drawColor = ColorModifier.changeColorElement(changecode,drawColor,value);
		}
	}
	public void changeAlpha(double al){
		changeColorElement(ColorModifier.CHANGECODE_ALPHA,al);
	}
	public void changeBrightness(double dk){
		changeColorElement(ColorModifier.CHANGECODE_BRIGHTNESS,dk);
	}
	public void changeContrast(double ct){
		changeColorElement(ColorModifier.CHANGECODE_CONTRAST,ct);
	}
	public boolean deselect(Shape ss){
		return true;
	}
	public boolean deselectAll(Shape ss){
		return true;
	}
	public PathGroup copiedPath(){
		return null;
	}
	public void addTransform(AffineTransform af){
		
	}
	public void setGradient(MultipleGradientPaint mp){
		if(mp != null){
			gradation = new GradationCasette(mp);
		}else{
			gradation = null;
		}
	}
	public void adjustZoom(){
		setStroke(trueStroke);
		if(gradation != null){
			gradation.adjustZoom();
		}
	}
	public void setStroke(BasicStroke bs){
		trueStroke = bs;
		if(trueStroke == null){
			bStroke = null;
			drawFlag = false;
			return;
		}
		bStroke = new BasicStroke(trueStroke.getLineWidth()*(float)FlexPoint.s_expand,trueStroke.getEndCap()
			,trueStroke.getLineJoin(),trueStroke.getMiterLimit());
	}
	public void changeStrokeSize(double size){
		setStroke(new BasicStroke((float)size,trueStroke.getEndCap()
			,trueStroke.getLineJoin(),trueStroke.getMiterLimit()));
	}
	public boolean deselectAll(){
		return true;//全てなくなった時trueを返す
	}
	public boolean select(Shape ss){
		return false;
	}
	public boolean selectAll(Shape ss){
		return false;
	}
	public boolean selectAll(){
		return true;
	}
	public void setColors(Color d,Color f){
		if(d != null){
			drawColor = d;
		}
		if(f != null && f.getRGB() != fillColor.getRGB()){
			fillColor = f;
			gradation = null;
		}
	}
	public void transPoints(AffineTransform af){
		
	}
	public void makeSelectedPathLine(){
	}
	public ArrayList<PolycoItem> getAllMembers(){
		ArrayList<PolycoItem> al = new ArrayList<PolycoItem>();
		al.add(this);
		return al;
	}
	public void copyStylesFrom(PolycoItem src){
		copyColorFrom(src);
		copyStrokeFrom(src);
	}
	public void copyStrokeFrom(PolycoItem src){
		bStroke = src.bStroke;
		trueStroke = src.trueStroke;
		if(trueStroke == null){
			drawFlag = false;
		}
	}
	public void setFlags(boolean d,boolean f){
		drawFlag = d;
		fillFlag = f;
	}
	public void copyColorFrom(PolycoItem src){
		drawColor = src.drawColor;
		fillColor = src.fillColor;
		drawFlag = src.drawFlag;
		fillFlag = src.fillFlag;
		if(src.gradation != null){
			gradation = new GradationCasette(src.gradation);
		}
	}
	public void setVisible(boolean vi){
		visibleFlag = vi;
		setListHtml();
	}
	public void moveAll(double xx,double yy){
		if(gradation != null){
			gradation.moveAll(xx,yy);
		}
	}
	public void refreshAll(){
		
	}
	public GeneralPath getOutline(){
		return outline;
	}
	public void setName(String na){
		name = na;
		setListHtml();
	}
	public void setListHtml(){
		String vis = (visibleFlag)?("+"):("-");
		listHtml = "<html><body><TT>&nbsp;"+vis+"&nbsp;&nbsp;&nbsp;"+name+"</TT></body></html>";
	}
	public String toString(){
		return listHtml;
	}
}