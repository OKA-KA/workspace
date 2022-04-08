package p2vj;

import java.util.ArrayList;

class EXItemList<T> extends ArrayList<T>{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public void upItem(T pi){
		moveItemTo(indexOf(pi)+1,pi);
	}
	public void downItem(T pi){
		moveItemTo(indexOf(pi)-1,pi);
	}
	public void topTo(T pi){
		moveItemTo(0,pi);
	}
	public void bottomTo(T pi){
		moveItemTo(size()-1,pi);
	}
	public void moveItemTo(int pos,T pi){
		if(indexOf(pi) == pos || pos < 0 || pos >= size()){
			return;
		}
		remove(pi);
		add(pos,pi);
		
	}
	public void addItem(T pi){
		if(indexOf(pi) == -1)
		add(pi);
	}
	public void addItem(int ii,T pi){
		if(indexOf(pi) == -1)
		add(ii,pi);
	}
	public void swapItem(T moto,T tugi){
		add(tugi);
		remove(moto);
	}
	public boolean removeItem(int ii){
		remove(ii);
		return (size() == 0);
	}
	public boolean removeItem(Object pi){
		remove(pi);
		return (size() == 0);
	}
}