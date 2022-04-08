package p2vj;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
public class PaintersCanvas{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	int width;
	int height;
	int vwidth,vheight;
	EXItemList<PPLayer> layerList = new EXItemList<PPLayer>();
	boolean layerStateChangedFlag = true;
	BufferedImage backImage;
	BufferedImage frontImage;
	boolean antiAliasing = true;
	boolean memSaveMode = true;
	RenderingHints myRenderingHints = null;
	PPLayer currentLayer;
	PaintersCanvas(){
		this(800,600,800,600);
		
	}
	PaintersCanvas(int ww,int hh,int vw,int vh){
		changeBounds(ww,hh);
		setView(vw,vh);
		BufferedImage si = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = si.createGraphics();
		myRenderingHints = sg.getRenderingHints();
	}
	public java.util.ArrayList<PPLayer> getLayerList(){
		
		return layerList;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void setCurrentLayer(PPLayer pl) throws Exception{
		if(pl != null && layerList.indexOf(pl) == -1){
			throw new Exception("The layer is not a member of the canvas!");
		}
		currentLayer = pl;
	}
	
	public PPLayer getCurrentLayer(){
		return currentLayer;
	}
	
	public BufferedImage getCurrentImage(){
		return currentLayer.getFrontImage();
	}
	
	public Graphics2D getCurrentGraphics(){
		return currentLayer.getFrontGraphics();
	}
	
	public void setRenderingHints(RenderingHints rh){
		myRenderingHints = rh;
		for(int ii = 0;ii < layerList.size();ii++){
			layerList.get(ii).setRenderingHints(rh);
		}
	}
	public void adjustZoom(){
		
		for(int ii = 0;ii < layerList.size();ii++){
			layerList.get(ii).adjustZoom();
		}
	}
	public RenderingHints getRenderingHints(){
		return myRenderingHints;
	}
	public void addLayer(PPLayer pl){
		layerList.add(pl);
		try{
		setCurrentLayer(pl);
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	public void addLayer(){
		addLayer(new PPLayer(this,false));
	}
	public void removeLayer(PPLayer pl){
		layerList.remove(pl);
		if(layerList.size() == 0){
			addLayer();
		}else{
			if(currentLayer == pl){
				try{
				setCurrentLayer(layerList.get(layerList.size()-1));
				}catch(Exception exx){
					exx.printStackTrace();
				}
			}
			
		}
	}
	public void setAntiAliasing(boolean bb){
		antiAliasing = bb;
	}
	
	public void setHintsTo(Graphics2D g2d){
		g2d.setRenderingHints(myRenderingHints);
	}
	
	public void changeBounds(int ww,int hh){
		width = ww;
		height = hh;
		layerStateChangedFlag = true;
	}
	
	public void setView(int ww,int hh){
		vwidth = ww;
		vheight = hh;
		layerStateChangedFlag = true;
	}
	
	public void paint(Graphics2D g2){
		if(memSaveMode){
			for(int ii = 0; ii < layerList.size();ii++){
					layerList.get(ii).paint(g2);
			}
		}else{
			if(layerStateChangedFlag){
				if(currentLayer == null)return;
				int ind = layerList.indexOf(currentLayer);
				boolean needback = true;
				boolean needfront = false;
				if(ind == 0){
					needback = false;
				}
				if(ind == layerList.size() -1){
					needfront = false;
				}
				
				backImage = null;
				frontImage = null;
				
				if(needback){
					backImage = new BufferedImage(vwidth,vheight,BufferedImage.TYPE_INT_ARGB);
					Graphics2D backg = (Graphics2D)backImage.createGraphics();
					setHintsTo(backg);
					for(int ii = 0; ii < ind;ii++){
						layerList.get(ii).paint(backg);
					}
				}
				if(needfront){
					frontImage = new BufferedImage(vwidth,vheight,BufferedImage.TYPE_INT_ARGB);
					Graphics2D frontg = (Graphics2D)backImage.createGraphics();
					setHintsTo(frontg);
					for(int ii = ind; ii < layerList.size();ii++){
						layerList.get(ii).paint(frontg);
					}
				}
			}
			if(backImage != null){
				g2.drawImage(backImage,FlexPoint.getAffineTransform(),null);
			}
			currentLayer.paint(g2);
			
			if(frontImage != null){
				g2.drawImage(frontImage,FlexPoint.getAffineTransform(),null);
			}
		}
	}
	
}