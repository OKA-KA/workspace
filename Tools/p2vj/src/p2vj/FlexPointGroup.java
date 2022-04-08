package p2vj;

import java.util.*;
import java.awt.geom.*;

public class FlexPointGroup{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	ArrayList<FlexPoint> list = new ArrayList<FlexPoint>();
	boolean closedFlag = false;
	FlexPointGroup(){
		
	}
	FlexPointGroup(ArrayList<FlexPoint> al){
		addAll(al);
	}
	public void add(double x,double y){
		list.add(new FlexPoint(x,y));
	}
	public void add(Point2D p2){
		add(p2.getX(),p2.getY());
	}
	public void add(FlexPoint p2){
		add(p2.x,p2.y);
	}
	public void add(FlexPoint p2,boolean memsaveflag){
		if(memsaveflag){
			list.add(p2);
		}else{
			add(p2.x,p2.y);
		}
	}
	public FlexPoint get(int i){
		if(closedFlag){
			i = i%list.size();
		}
		return list.get(i);
	}
	public void remove(int i){
		if(closedFlag){
			i = i%list.size();
		}
		list.remove(i);
	}
	public boolean isClosed(){
		return closedFlag;
		
	}
	public void setClosed(boolean f){
		closedFlag = f;
	}
	public FlexPoint getLast(){
		return list.get(list.size()-1);
	}
	public void removeLast(){
		list.remove(list.size()-1);
	}
	public int size(){
		return list.size();
	}
	public void addAll(FlexPointGroup pl){
		for(int ii = 0;ii < pl.size();ii++){
			//System.out.println(ii);
			list.add(pl.get(ii));
		}
	}
	public void addAll(ArrayList<FlexPoint> al){
		for(int ii = 0;ii < al.size();ii++){
			list.add(al.get(ii));
		}
	}
	public void clear(){
		list.clear();
	}
}