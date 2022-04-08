package p2vj;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;
public class PolycoGroup extends PolycoItem{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	ArrayList<PolycoItem> itemList = new ArrayList<PolycoItem>();
	
	PolycoGroup(){
		super();
	}
	public PolycoItem getCopy(){
		PolycoGroup ret = new PolycoGroup();
		
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			ret.addItem(pi.getCopy());
		}
		return ret;
	}
	public boolean deselectAll(){
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).deselectAll();
		}
		return true;
	}
	public ArrayList<PolycoItem> getAllMembers(){
		ArrayList<PolycoItem> al = new ArrayList<PolycoItem>();
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			al.addAll(pi.getAllMembers());
		}
		return al;
	}
	public void changeColorElement(int changecode,double value){
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.changeColorElement(changecode,value);
		}
	}
	public ArrayList<FlexPoint> getSelectedPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		for(int ii = 0;ii < itemList.size();ii++){
			ret.addAll(itemList.get(ii).getSelectedPoints());
		}
		return ret;
	}
	
	public void transPoints(AffineTransform af){
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).transPoints(af);
		}
	}
	public void removeAllItem(){
		ArrayList<PolycoItem>  tlist = new ArrayList<PolycoItem>();
		tlist.addAll(itemList);
		for(Iterator<PolycoItem> i = tlist.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			removeItem(pi);
		}
	}
	public void moveAll(double xx,double yy){
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.moveAll(xx,yy);
		}
	}
	public void addItem(PolycoItem pi){
		itemList.add(pi);
		pi.joinIn(this);
	}
	public void addAllItem(ArrayList<PolycoItem> al){
		for(Iterator<PolycoItem> i = al.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			addItem(pi);
		}
	}
	public void removeItem(PolycoItem pi){
		itemList.remove(pi);
		pi.excludeFrom(this);
	}
	public boolean selectAll(){
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).selectAll();
		}
		return true;
	}
	public boolean selectAll(Shape ss){
		boolean ret = false;
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			if(pi.selectAll(ss)){
				ret = true;
				break;
			}
		}
		if(!ret){
			return ret;
		}
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.selectAll();
			
		}
		return ret;
	}
	public boolean deleteSelected(){
		for(int ii = 0;ii < itemList.size();ii++){
			itemList.get(ii).deleteSelected();
		}
		return isDead();
	}
	public boolean isDead(){
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			if(pi.isDead()){
				i.remove();
			}
		}
		deadFlag = itemList.isEmpty();
		return deadFlag;
	}
	public boolean select(Shape ss){
		return selectAll(ss);
	}
	public boolean deselectAll(Shape ss){
		boolean ret = false;
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			if(pi.deselectAll(ss)){
				ret = true;
				break;
			}
		}
		if(!ret){
			return false;
		}
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.deselectAll();
			
		}
		return ret;
	}
	public boolean deselect(Shape ss){
		return deselectAll(ss);
	}
	
	
	
	public void paint(Graphics2D g2){
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.paint(g2);
		}
	}
	
	public void paintBone(Graphics2D g2){

		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.paintBone(g2);
		}
	}
	public void paintSelectedBone(Graphics2D g2){

		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.paintSelectedBone(g2);
		}
	}
	public void makeSelectedPathLine(){
		
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.makeSelectedPathLine();
		}
	}
	
	public void makePathLine(){
		outline.reset();
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.makePathLine();
			outline.append(pi.getOutline(),false);
		}
	}
	
	public void adjustZoom(){
		for(Iterator<PolycoItem> i = itemList.iterator();i.hasNext();){
			PolycoItem pi = i.next();
			pi.adjustZoom();
		}
		makePathLine();
	}
	

}