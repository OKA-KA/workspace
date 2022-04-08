package p2vj;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
public class PolycoImage extends PolycoItem{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	FlexPoint basePoint = new FlexPoint(0,0);
	BufferedImage image = null;
	String filePath = "dummy";
	AffineTransform exaff = new  AffineTransform();
	Graphics2D graphics;
	boolean selectedFlag = false;
	PolycoImage(String filename){
		loadFromFile(filename);
		name = filename;
		setListHtml();
		classCode = CODE_IMAGE;
	}
	PolycoImage(String filename,boolean appletflag){
		loadFromFile(filename,appletflag);
	}
	PolycoImage(){
		
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
	PolycoImage(PolycoImage pi){
		this(pi,false);
	}
	PolycoImage(PolycoImage pi,boolean historyflag){
		super(pi,historyflag);
	}
	public void copyFrom(PolycoImage pi,boolean historyflag){
		image = new BufferedImage(pi.image.getWidth(),pi.image.getHeight(),BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D)image.getGraphics();
		graphics.drawImage(pi.image,0,0,null);
		
	}
	
	public PolycoItem getCopy(){
		PolycoImage ret = new PolycoImage();
		ret.copyFrom(this,false);
		return ret;
	}
	public void dot(int x,int y,int rgb){
		image.setRGB(x,y,rgb);
		//graphics.setColor(new Color(rgb,false));
		//graphics.fillRect(x,y,1,1);
	}
	
	public void createImage(int wid,int hei){
		image = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D)image.getGraphics();
	}
	
	public void loadFromFile(String filepath,boolean appletflag){
		try{
			if(appletflag){
				image = ImageIO.read(new URL(filepath));
			}else{
				image = ImageIO.read(new File(filepath));
			}
		graphics = (Graphics2D)image.getGraphics();
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	public void loadFromFile(String filepath){
		loadFromFile(filepath,false);
	}
	public void paint(Graphics2D g2){
		g2.drawImage(image,exaff,null);
	}
	public void adjustZoom(){
		exaff = new AffineTransform(FlexPoint.getExpand(),0,0,FlexPoint.getExpand(),0,0);
		
		AffineTransform taff = new AffineTransform(1,0,0,1,FlexPoint.getOffsetX()-basePoint.x,FlexPoint.getOffsetY()-basePoint.y);
		
		exaff.concatenate(taff);
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
	public void setListHtml(){
		String vis = (visibleFlag)?("+"):("-");
		listHtml = "<html><body><TT>&nbsp;"+vis+"&nbsp;&nbsp;&nbsp;"+name+"</TT></body></html>";
	}
	public String toString(){
		return listHtml;
	}
}
