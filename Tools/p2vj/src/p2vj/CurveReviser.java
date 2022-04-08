package p2vj;

import java.util.*;
import java.awt.geom.*;

public class CurveReviser{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	
	public static ArrayList<FlexPoint> breakLine(ArrayList<FlexPoint> al,double breaklength){
		if(al.size() < 2)return al;
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		ret.add(al.get(0));
		double length = 0;
		double fragment = 0;
		FlexPoint lastpoint = al.get(0);
		for(int ii = 1;ii < al.size();ii++){
			FlexPoint p1 = al.get(ii-1);
			FlexPoint p2 = al.get(ii);
			length = p1.distance(p2)+fragment;
			if(length > breaklength){
				double plength = 0;
				for(int jj = 1;jj < length*5;jj++){
					FlexPoint tp = new FlexPoint(p1.x+(p2.x-p1.x)*jj/length/5,p1.y+(p2.y-p1.y)*jj/length/5);
					if(lastpoint.distance(tp)+fragment > breaklength){
						ret.add(tp);
						lastpoint = tp;
						fragment = 0;
					}
				}
				fragment = p2.distance(lastpoint);
			}else{
				fragment = length;
			}
		}
		if(lastpoint.distance(al.get(al.size()-1)) > 0){
			ret.add(al.get(al.size()-1));
		}
		return ret;
	}
	public static ArrayList<FlexPoint> reviseCurve_Multi(ArrayList<FlexPoint> al,int num){
		for(int ii = 0; ii < num;ii++){
			al = reviseCurve(al);
		}
		return al;
	}
	public static ArrayList<FlexPoint> reviseCurve(ArrayList<FlexPoint> al){
		
		if(al.size() < 3){
			return al;
		}
		ArrayList<FlexPoint> ret1 = getRevisedPoints(al);
		//ArrayList<FlexPoint> ret1 = getRevisedPoints_Gaussian(al,5,5);
		ArrayList<FlexPoint> alrev = new ArrayList<FlexPoint>();
		for(int ii = 0;ii < al.size();ii++){
			alrev.add(al.get(al.size()-ii-1));
		}
		ArrayList<FlexPoint> ret2 = getRevisedPoints(alrev);
		
				
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();		
		for(int ii = 0;ii < al.size();ii++){
			double px = ret2.get(al.size()-ii-1).x*(ii/(al.size()-1.0))+ret1.get(ii).x*((al.size()-ii-1)/(al.size()-1.0));
			double py = ret2.get(al.size()-ii-1).y*(ii/(al.size()-1.0))+ret1.get(ii).y*((al.size()-ii-1)/(al.size()-1.0));
			ret.add(new FlexPoint(px,py));
		}
		
		//ArrayList<FlexPoint> ret = getRevisedPoints(alrev);		
		
		
		return ret1;
	}
	public static ArrayList<FlexPoint> reviseCurve_Gaussian(ArrayList<FlexPoint> al,double effect){
		ArrayList<FlexPoint> ret = reviseCurve_Gaussian(al,5,effect);
		
		return ret;
	}
	
	
	public static ArrayList<FlexPoint> reviseCurve_Gaussian_Advance(ArrayList<FlexPoint> al,double psize,double effect){
		return reviseCurve_Gaussian_Advance(al,psize,effect,false);
	}
	public static ArrayList<FlexPoint> reviseCurve_Gaussian_Advance(ArrayList<FlexPoint> al,double psize,double effect,boolean pathclosed){
		ArrayList<FlexPoint> ret = reviseCurve_Gaussian(al,psize,effect,pathclosed);
		if(!pathclosed){
			ret.get(0).set(al.get(0).x,al.get(0).y);
			ret.get(ret.size()-1).set(al.get(al.size()-1).x,al.get(al.size()-1).y);
		}
		return ret;
		
	}
	public static ArrayList<FlexPoint> reviseCurve_Gaussian(ArrayList<FlexPoint> al,double psize,double effect){
		return reviseCurve_Gaussian(al,psize,effect,false);
	}
	/**
	 * smoothing curve
	 */
	public static ArrayList<FlexPoint> reviseCurve_Gaussian(ArrayList<FlexPoint> al,double psize,double effect,boolean pathclosed){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		if(pathclosed){
			FlexPoint f = al.get(0);
			FlexPoint l = al.get(al.size()-1);
			if(f.getLength(l) < 0.2){
				al.remove(al.size()-1);
			}
			
		}
		
		
		int lsize = al.size();
		for(int ii = 0;ii < al.size();ii++){
			
			double newx = 0.0;
			double newy = 0.0;
			double tmpx = 0.0;
			double tmpy = 0.0;
			double pratio = 0;
			double tmpdist = 0;
			FlexPoint last = al.get(ii);
			
			double prelen = 0;
			double postlen = 0;
			double rangesize = psize;
			
			rangesize = psize;
			for(int  jj = ii;;jj++){
				if(!pathclosed && jj >= lsize){
					break;
				}
				FlexPoint pe = al.get(jj%lsize);
				tmpdist += last.distance(pe);
				double gp = gaussianEffect(tmpdist,effect);//*(Math.abs((jj-al.size()/2.0)*(ii-al.size()/2.0))+1);
				tmpx += pe.x*gp;
				tmpy += pe.y*gp;
				pratio += gp;
				last = pe;
				if(tmpdist >= rangesize){
					break;
				}
			}
			newx += tmpx/pratio/2;
			newy += tmpy/pratio/2;
			
			tmpx = 0.0;
			tmpy = 0.0;
			pratio = 0;
			tmpdist = 0;
			last = al.get(ii);
			for(int  jj = ii;;jj--){
				
				if(!pathclosed && jj < 0){
					break;
				}
				FlexPoint pe = al.get((jj+lsize)%lsize);
				tmpdist += last.distance(pe);
				double gp = gaussianEffect(tmpdist,effect);//*(Math.abs((jj-al.size()/2.0)*(ii-al.size()/2.0))+1);
				tmpx += pe.x*gp;
				tmpy += pe.y*gp;
				pratio += gp;
				last = pe;
				if(tmpdist >= rangesize){
					break;
				}
			}
			newx += tmpx/pratio/2;
			newy += tmpy/pratio/2;
			
			//System.out.println(String.valueOf(newx)+" "+String.valueOf(newy)+" "+String.valueOf(al.get(ii).x)+" "+String.valueOf(al.get(ii).y));
			ret.add(new FlexPoint(newx,newy));
		}
		return ret;
		
	}
	public static double gaussianEffect(double dist,double sig){
		return 1/(Math.PI*sig*sig*2)*Math.exp(-dist*dist/(2*sig*sig));
	}
	
	
	
	public static ArrayList<FlexPoint> getRevisedPoints(ArrayList<FlexPoint> al){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		ret.add(al.get(0));
		for(int ii = 1;ii < al.size()-1;ii++){
			//FlexPoint p1 = ret.get(ii-1);
			FlexPoint p1 = al.get(ii-1);
			FlexPoint p2 = al.get(ii);
			FlexPoint p3 = al.get(ii+1);
			double length1 = p1.distance(p2);
			double length2 = p3.distance(p2);
			double dlen = length1+length2;
			double x4 = (length1/dlen)*p3.x+(length2/dlen)*p1.x;
			double y4 = (length1/dlen)*p3.y+(length2/dlen)*p1.y;
			ret.add(new FlexPoint(x4/2+p2.x/2,y4/2+p2.y/2));
		}
		ret.add(al.get(al.size()-1));
		
		
		return ret;
		
	}

	
	
	public static double getRadi(FlexPoint p1,FlexPoint p2,FlexPoint p3){
		return getRadi(p2.x-p1.x,p2.y-p1.y,p3.x-p1.x,p3.y-p1.y);
		
		
	}
	
	public static double getRadi(double x1,double y1,double x2,double y2){
	
		double rota1 = getRota(x1,y1);
		double rota2 = getRota(x2,y2);
		double tmpret = Math.abs(rota1-rota2);
		while(tmpret > Math.PI){
			tmpret = Math.abs(tmpret-Math.PI*2);
		}
		return Math.abs(tmpret);
	}
	
	public static double getRota(double xx,double yy){
		double rr = Math.sqrt(xx*xx+yy*yy);
		double rx = Math.acos(xx/rr);
		double ry = Math.asin(yy/rr);
		double rot = 0.0;
		if(xx < 0.0){
			rot = Math.PI-ry;
		}else{
			if(yy < 0.0){
				rot = Math.PI*2+ry;
			}else{
				rot = ry;
			}
		}
		
		return rot;
	}
	public static ArrayList<PointList> breakAtCorner(ArrayList<FlexPoint> al,double maxradi,double minlen,double roundrange){
		return breakAtCorner(al,maxradi,minlen,roundrange,false);
	}
	
	public static double[] getAngleAround(ArrayList<FlexPoint> al,int pos,double roundrange,boolean closedflag){
		FlexPoint p1 = al.get(pos);
		double len = 0;
		double radi = 0;
		int num = 0;
		FlexPoint lastpoint = p1;
		double p1x = 0;
		double p1y = 0;
		double p2x = 0;
		double p2y = 0;
		
		int lsize = al.size();
		for(int jj = pos+1;;jj++){
			if(!closedflag && jj >= al.size()){
				break;
			}
			if(jj > lsize*2){
				break;
			}
			FlexPoint pp1 = al.get(jj%lsize);
			double plen = lastpoint.distance(pp1);
			double ratio = 1;
			if(plen+len > roundrange){
				ratio = (roundrange-len)/plen;
			}
			len += plen;
			lastpoint = pp1;
			p1x += (pp1.x-p1.x)*ratio;
			p1y += (pp1.y-p1.y)*ratio;
			num++;

			if(jj > al.size() -1){
				FlexPoint pp2 = al.get((jj+1)%lsize);
				FlexPoint pp3 = al.get((jj-1+lsize)%lsize);
			}
			if(len > roundrange){
				break;
			}
			
		}
		
		p1x /= num;
		p1y /= num;
		len = 0;
		radi = 0;
		num = 0;
		lastpoint = p1;
		for(int jj = pos-1;;jj--){
			if(!closedflag && jj < 0){
				break;
			}
			if(jj+lsize < 0){
				break;
			}
			FlexPoint pp1 = al.get((jj+lsize)%lsize);
			double plen = lastpoint.distance(pp1);
			double ratio = 1;
			if(plen+len > roundrange){
				ratio = (roundrange-len)/plen;
			}
			len += plen;
			lastpoint = pp1;
			p2x += (pp1.x-p1.x)*ratio;
			p2y += (pp1.y-p1.y)*ratio;
			num++;

			if(jj > 0){
				FlexPoint pp2 = al.get((jj+1+lsize*2)%lsize);
				FlexPoint pp3 = al.get((jj-1+lsize*2)%lsize);
			}
			if(len > roundrange){
				break;
			}
		}
		
		p2x /= num;
		p2y /= num;
		double[] res = {getRadi(p1x,p1y,p2x,p2y),(p1x+p2x)/2,(p1x+p2x)/2};
		return res;
	}
	
	
	
	public static ArrayList<PointList> breakAtCorner(ArrayList<FlexPoint> al,double maxradi,double minlen,double roundrange,boolean closedflag){
		ArrayList<PointList> ret = new ArrayList<PointList>();
		FlexPoint clast = al.get(0);
		double lastradi = 999;
		double distfromcorner = 0;
		PointList currentlist = new PointList();
		currentlist.add(al.get(0));
		ret.add(currentlist);
		double[] startradi = getAngleAround(al,0,roundrange,closedflag);
		double lastdirectionx = startradi[1];
		double lastdirectiony = startradi[2];
		boolean zeroiscorner = true;
			//System.out.println(String.valueOf(maxradi)+";;;;"+String.valueOf(startradi[0]));
		if(closedflag &&  maxradi < startradi[0]){
			//System.out.println("Is not corner");
			zeroiscorner = false;
		}
		
		
		for(int ii = 1;ii < al.size()-1;ii++){
			FlexPoint p1 = al.get(ii);
			FlexPoint p2 = al.get(ii+1);
			FlexPoint p3 = al.get(ii-1);
			distfromcorner += clast.distance(p1);
			clast = p1;
			if(true){//(getRadi(p1,p2,p3) < maxradi*2){
				
				double[] resradi = getAngleAround(al,ii,roundrange,closedflag);
				currentlist.add(p1);
				if(resradi[0] < maxradi){
					if(distfromcorner < minlen && ret.size() > 1){
						if(resradi[0] < lastradi){
							currentlist.remove(0);
							ret.get(ret.size()-2).addAll(currentlist);
							currentlist.clear();
							currentlist.add(p1);
							distfromcorner = 0;
							lastradi = resradi[0];
							lastdirectionx = resradi[1];
							lastdirectiony = resradi[2];
						}
					}else{
						if(distfromcorner >= minlen || getRadi(lastdirectionx,lastdirectiony,resradi[1],resradi[2]) > 1.2){
							
							lastradi = resradi[0];
							currentlist = new PointList();
							currentlist.add(p1);
							ret.add(currentlist);
							distfromcorner = 0;
							lastdirectionx = resradi[1];
							lastdirectiony = resradi[2];
						}
					}
				}
				
				
			}
			
		}
		if(currentlist.getLast().getLength(al.get(al.size()-1)) > 0.0001){
			currentlist.add(al.get(al.size()-1));
		}
		if(!zeroiscorner){
			if(ret.size() > 1){
				currentlist.addAll(ret.get(0));
				ret.remove(0);
			}else{
				currentlist.setClosed(true);
			}
		}
		//System.out.println("Corner broke "+String.valueOf(ret.size()));
		
		
		return ret;
	}
	
	
}