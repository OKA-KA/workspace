package p2vj;

import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.RenderingHints;
public class PPLayer{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static int allnum = 0;
	int number;
	EXItemList<PolycoItem> itemList = new EXItemList<PolycoItem>();
	BufferedImage frontImage;
	Graphics2D frontGraphics;
	PaintersCanvas parentCanvas;
	String name = "layer";
	String listHtml = "layer";
	boolean visibleFlag = true;
	double alpha = 100;
	PPLayer(PaintersCanvas pc,boolean makeimageflag){
		parentCanvas = pc;
		if(makeimageflag){
			makeFrontImage();
		}else{
			frontGraphics = null;
			frontImage = null;
		}
		
		number = allnum++;
		setListHtml();
		
	}
	PPLayer(PaintersCanvas pc){
		this(pc,true);
	}
	PPLayer(){
		this(new PaintersCanvas(),false);
	}
	
	PPLayer(PPLayer pl,boolean historyflag){
		this(null,pl,historyflag);
	}
	PPLayer(PaintersCanvas pc,PPLayer pl,boolean historyflag){
		if(pc == null){
			parentCanvas = pl.parentCanvas;
		}else{
			parentCanvas = pc;
		}
		if(pl.frontImage != null){
			makeFrontImage();
			drawToFrontImage(pl.frontImage);
		}
		setListHtml();
		name = pl.name;
		listHtml = pl.listHtml;
		visibleFlag = pl.visibleFlag;
		alpha = pl.alpha;
		for(int ii = 0;ii < pl.itemList.size();ii++){
			PolycoItem pi = pl.itemList.get(ii);
			switch(pi.classCode){
				case PolycoItem.CODE_IMAGE:
				itemList.add(new PolycoImage((PolycoImage)pi,historyflag));
				break;
				case PolycoItem.CODE_TEXT:
				itemList.add(new PolycoText((PolycoText)pi,historyflag));
				break;
				case PolycoItem.CODE_PATH:
				itemList.add(new PathGroup((PathGroup)pi,historyflag));
				break;
				default:
				break;
			}
		}
		if(historyflag){
			number = pl.number;
		}else{
			number = allnum++;
			setListHtml();
		}
		
	}
	PPLayer(PPLayer pl){
		this(null,pl,false);
	}
	public EXItemList<PolycoItem> getList(){
		return itemList;
	}
	public void upItem(PolycoItem pi){
		itemList.upItem(pi);
	}
	public void downItem(PolycoItem pi){
		itemList.downItem(pi);
	}
	public void topTo(PolycoItem pi){
		itemList.topTo(pi);
	}
	public void bottomTo(PolycoItem pi){
		itemList.bottomTo(pi);
	}
	public void setVisible(boolean b){
		visibleFlag = b;
		
	}
	public PPLayer margeWith(PPLayer pp){
		itemList.addAll(pp.itemList);
		return pp;
	}
	public PathGroup margeAll(){
		PolycoItem base = itemList.get(0);
		while(itemList.size() > 1){
			margeItem(base,itemList.get(1));
		}
		return (PathGroup)base;
	}
	public PathGroup margeItem(PolycoItem base,PolycoItem ov){
		PathGroup baseg;
		PathGroup ovg;
		if(base.classCode != PolycoItem.CODE_PATH){
			baseg = ShapeModifier.changeShapeToPath(base.outline);
			swapItem(base,baseg);
		}else{
			baseg = (PathGroup)base;
		}
		if(ov.classCode != PolycoItem.CODE_PATH){
			ovg = ShapeModifier.changeShapeToPath(ov.outline);
			swapItem(ov,ovg);
		}else{
			ovg = (PathGroup)ov;
		}
		baseg.addChains(ovg.chainList);
		removeItem(ovg);
		removeItem(ov);
		return baseg;
		
	}
	public void addAllItems(ArrayList<PolycoItem> al){
		itemList.addAll(al);
	}
	public void addAllItems(EXItemList<PolycoItem> al){
		itemList.addAll(al);
	}
	public void removeItem(PolycoItem pi){
		itemList.remove(pi);
	}
	public void moveItemTo(int pos,PolycoItem pi){
		itemList.moveItemTo(pos,pi);
	}
	public void addItem(PolycoItem pi){
		itemList.addItem(pi);
	}
	public void addItem(int ii,PolycoItem pi){
		itemList.addItem(ii,pi);
	}
	public void swapItem(PolycoItem moto,PolycoItem tugi){
		itemList.swapItem(moto,tugi);
	}
	public boolean removeItem(int ii){
		return itemList.removeItem(ii);
	}
	
	public void removeAll(){
		itemList.clear();
	}
	public boolean removeItem(Object pi){
		return itemList.removeItem(pi);
	}
	public void adjustZoom(){
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).adjustZoom();
		}
	}
	public void makeFrontImage(){
		frontImage = new BufferedImage(parentCanvas.width,parentCanvas.height,BufferedImage.TYPE_INT_ARGB);
		frontGraphics = frontImage.createGraphics();
	}
	public void drawToFrontImage(BufferedImage bi){
		frontGraphics.drawImage(bi,0,0,null);
	}
	public void paint(Graphics2D g2){
		if(!visibleFlag){
			return;
		}
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).paint(g2);
		}
		if(frontImage != null){
			g2.drawImage(frontImage,(int)(-FlexPoint.getOffsetX()),(int)(-FlexPoint.getOffsetY()),null);
		}
	}
	public BufferedImage getFrontImage(){
		return frontImage;
	}
	public Graphics2D getFrontGraphics(){
		return frontGraphics;
	}
	
	public void setRenderingHints(RenderingHints rh){
		if(frontGraphics != null){
			frontGraphics.setRenderingHints(rh);
		}
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