package p2vj;

import java.awt.*;
import java.util.*;
import java.awt.font.*;
import java.awt.geom.*;
public class PolycoText extends PolycoItem{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int VERTICAL = 0;
	public static final int HOLIZONTAL = 1;
	String text = " ";
	FlexPoint basePoint = new FlexPoint(0,0);
	Font rFont = new Font("Times",Font.PLAIN,49);
	Font trueFont = new Font("Times",Font.PLAIN,49);
	int size = 12;
	int style = Font.PLAIN;
	String fontName = "Times";
	int direction = HOLIZONTAL;
	//int direction = VERTICAL;
	boolean rotate90 = true;
	boolean selectedFlag = false;
	PolycoText(){
		super();
		classCode = CODE_TEXT;
		bStroke = new BasicStroke(1.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
	}
	PolycoText(PolycoText pt,boolean historyflag){
		copyFrom(pt,historyflag);
	}
	public void copyFrom(PolycoText pt,boolean historyflag){
		super.copyFrom(pt,historyflag);
		setFont(pt.trueFont);
		direction = pt.direction;
		rotate90 = pt.rotate90;
		text = pt.text;
	}
	PolycoText(PolycoText pt){
		this(pt,false);
	}
	public PolycoItem getCopy(){
		PolycoText ret = new PolycoText();
		ret.copyFrom(this,false);
		return ret;
	}
	public ArrayList<FlexPoint> getSelectedPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		if(selectedFlag){
			ret.add(basePoint);
		}
		return ret;
	}
	public void moveAll(double xx,double yy){
		super.moveAll(xx,yy);
		basePoint.rMoveTo(xx,yy);
	}
	public boolean deselectAll(){
		selectedFlag = false;
		return !selectedFlag;
	}
	public boolean select(Shape ss){
		if(ss.intersects(outline.getBounds2D())){
			selectedFlag = true;
		}
		return selectedFlag;
	}
	public boolean selectAll(Shape ss){
		if(ss.intersects(outline.getBounds2D())){
			selectedFlag = true;
		}
		return selectedFlag;
	}
	public boolean deselect(Shape ss){
		
		if(ss.intersects(outline.getBounds2D())){
			selectedFlag = false;
		}
		return !selectedFlag;
	}
	public boolean deselectAll(Shape ss){
		if(ss.intersects(outline.getBounds2D())){
			selectedFlag = false;
		}
		return !selectedFlag;
	}
	public void paintSelectedBone(Graphics2D g){
		g.draw(outline);
	}
	public boolean deleteSelected(){
		if(selectedFlag){
			deadFlag = true;
		}
		return !selectedFlag;
	}
	public void setFont(Font ft){
		trueFont = ft;
		style = ft.getStyle();
		size = ft.getSize();
		fontName = ft.getFontName();
		rFont = new Font(ft.getFontName(),ft.getStyle(),(int)(ft.getSize()*FlexPoint.s_expand));
	}
	
	public void transPoints(AffineTransform af){
		FlexPoint fx = basePoint;
		Point2D.Double src = new Point2D.Double(fx.x,fx.y);
		Point2D.Double dest = new Point2D.Double(fx.x,fx.y);
		Point2D dd = af.transform(src,dest);
		fx.set(dd.getX(),dd.getY());
		makePathLine();
		if(gradation != null){
			gradation.transPoints(af);
		}
	}
	public void setText(String str){
		text = str;
		String ttex;
		if(text.length() > 6){
			ttex = text.substring(0,5)+"...";
		}else{
			ttex = text;
		}
		
		name = ttex;
		makePathLine();
		setListHtml();
	}
	public PathGroup copiedPath(){
		PathGroup pg = ShapeModifier.changeShapeToPath(outline);
		pg.copyStylesFrom(this);
		return pg;
	}
	public void setXY_M(double x,double y){
		basePoint.set_M(x,y);
		makePathLine();
	}
	public void setXY(double x,double y){
		basePoint.set(x,y);
		makePathLine();
	}
	public void paint(Graphics2D g2){
		if(gradation != null){
			g2.setPaint(gradation.getGradation());
		}else{
			g2.setColor(fillColor);
		}
		g2.fill(outline);
	}
	public void paintBone(Graphics2D g2){
		g2.draw(outline);
	}
	public void setExpand(){
		double expand = basePoint.s_expand;
		rFont = new Font(fontName,style,(int)(size*expand));
		makePathLine();
	}
	public void makePathLine(){
		FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
		String strarray[] = text.split("[\r\n]");
		int jump = rFont.getSize();
		
		outline.reset();
		for(int ii = 0;ii < strarray.length;ii++){
			if(direction == VERTICAL){
				char[] kss = strarray[ii].toCharArray();
				for(int jj = 0;jj < kss.length;jj++){
					GlyphVector gv = rFont.createGlyphVector(frc,String.valueOf(kss[jj]));

					Shape s = gv.getOutline(0,0);
					double wid = s.getBounds().width;
					double x = s.getBounds().x;
					AffineTransform af = new AffineTransform(1,0,0,1,basePoint.getModiX()-ii*jump*1.5f-wid/2-x,basePoint.getModiY()+jj*jump);
					
					Shape ss = af.createTransformedShape(s);
					//Shape ss = gv.getOutline((float)basePoint.getModiX()-ii*jump,(float)basePoint.getModiY()+jj*jump);
					
					outline.append(ss,false);
				}
				
			}else{
				
				GlyphVector gv = rFont.createGlyphVector(frc,strarray[ii]);
				Shape s = gv.getOutline(0,0);
				if(rotate90){
					AffineTransform af = new AffineTransform(0,1,-1,0,(float)basePoint.getModiX()-ii*jump,(float)basePoint.getModiY());
					s =  af.createTransformedShape(s);
				}else{
					AffineTransform af = new AffineTransform(1,0,0,1,(float)basePoint.getModiX(),(float)basePoint.getModiY()+ii*jump);
					s =  af.createTransformedShape(s);
				}
				outline.append(s,false);
			}
		}
		
	}
	public void setListHtml(){
		String vis = (visibleFlag)?("+"):("-");
		listHtml = "<html><body><TT>&nbsp;"+vis+"&nbsp;&nbsp;&nbsp;"+name+"</TT></body></html>";
	}
	public String toString(){
		return listHtml;
	}
}
