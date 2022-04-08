package p2vj;

import java.util.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;


public class BezierTracer{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final double TRACING_SCORE_THRESHOLD_DEFAULT = 1;
	
	public static final double BREAK_ANGLE_MAX = 1.2;
	public static final double BREAK_ANGLE_SUM_MAX = Math.PI/4;
	public static final double LINE_DETECTREDUNDANCY = 0.08;
	public static final double LINE_DETECTLENGTH = 5.0;
	public static final double TRACABLE_WINDOW_SIZE = 10;
	public static double getAngleDifference(double r1,double r2){//return -3.14..3.14
		double ret = r2-r1;
		int sign = (ret > 0)?(1):(-1);
		if(Math.abs(ret) > Math.PI){
			return (sign*-1)*(Math.PI*2-Math.abs(ret));
		}else{
			return sign*Math.abs(ret);
		}
	}
	
	public static ArrayList<LAnchorPath> traceAllPoints(ArrayList<FlexPoint> al){
		ArrayList<LAnchorPath> ret = new ArrayList<LAnchorPath>();
		if(al.size() < 2){
			return ret;
		}
		FlexPoint lasta = al.get(0);
		double firstangle = 0;
		double lastangle = 0;
		
		ArrayList<FlexPointGroup> tmparray = new ArrayList<FlexPointGroup>();
		FlexPointGroup buff = new FlexPointGroup();
		buff.add(al.get(0));
		for(int ii = 1;ii < al.size();ii++){//search sharpe or long curve
			FlexPoint f1 = al.get(ii);
			buff.add(al.get(ii),true);
			double currentangle = FlexPoint.getAngle(f1.x-lasta.x,f1.y-lasta.y);
			if(ii == 1){
				firstangle = currentangle;
				lastangle = currentangle;
			}
			boolean flag = false;
			double angledifference = getAngleDifference(currentangle,firstangle);
			//System.out.println(String.valueOf(currentangle)+";"+String.valueOf(firstangle)+";");
			if(angledifference > BREAK_ANGLE_SUM_MAX){
				flag = true;
			//System.out.println("differes!");
			}
			if(angledifference > BREAK_ANGLE_MAX){
				flag = true;
			}
			//flag = false;
			if(flag){
				tmparray.add(buff);
				buff = new FlexPointGroup();
				buff.add(al.get(ii),true);
				firstangle = currentangle;
			}
			lastangle = currentangle;
		}
		if(buff.size()>1){
			tmparray.add(buff);
		}
		
		for(int ii = 0;ii < tmparray.size();ii++){
			FlexPointGroup parray = tmparray.get(ii);
			//System.out.println(String.valueOf(ii)+":"+String.valueOf(parray.size()));
			if(parray.size() < 2){
				continue;
			}
			PointTracingResult pr = tracePoints(parray.list);
			if(pr.score < TRACING_SCORE_THRESHOLD_DEFAULT){
				ret.add(pr.path);
			}else{
				int fromindex = 0;
				int toindex = (parray.size()-1)/2;
				int gindex = fromindex+1;
				int ngindex = parray.size();
				while(gindex < parray.size()-1){
					PointTracingResult gppr = null;
					while(gindex < ngindex-1){
						ArrayList<FlexPoint> pal = new ArrayList<FlexPoint>();
						for(int jj = fromindex;jj <= toindex;jj++){
							pal.add(parray.get(jj));
						}
						PointTracingResult ppr = tracePoints(pal);
						
						//PointTracingResult ppr = tracePoints(parray.list,fromindex,toindex);
						
						if(ppr.score < TRACING_SCORE_THRESHOLD_DEFAULT){
							gindex = toindex;
							gppr = ppr;
							break;
						}else{
							ngindex = toindex;
						}
						int ttoindex = (gindex+ngindex)/2;
						//System.out.println(ttoindex);
						if(ttoindex == toindex || gindex == fromindex){
							if(gindex == fromindex){
								gindex += 1;
							}
							ArrayList<FlexPoint> ppal = new ArrayList<FlexPoint>();
							for(int jj = fromindex;jj <= gindex;jj++){
								ppal.add(parray.get(jj));
							}
							gppr = tracePoints(ppal);
							//gppr = tracePoints(parray.list,fromindex,gindex);
							break;
						}
						toindex = ttoindex;
					}
					if(gppr != null && gppr.path != null){
						ret.add(gppr.path);
					}
					//System.out.println("-----------------");
					fromindex = gindex;
					toindex = parray.size()-1;
					gindex++;
					ngindex = parray.size();
				}
			}
		}
		return ret;
	}
	
	public static ArrayList<LAnchorPath> traceAll(ArrayList<FlexPoint> al){
		ArrayList<LAnchorPath> ret = new ArrayList<LAnchorPath>();
		FlexPoint lasta = al.get(0);
		double firstangle = 0;
		double lastangle = 0;
		
		ArrayList<FlexPointGroup> tmparray = new ArrayList<FlexPointGroup>();
		FlexPointGroup buff = new FlexPointGroup();
		buff.add(al.get(0));
		for(int ii = 1;ii < al.size();ii++){//search sharpe or long curve
			FlexPoint f1 = al.get(ii);
			buff.add(al.get(ii));
			double currentangle = FlexPoint.getAngle(f1.x-lasta.x,f1.y-lasta.y);
			if(ii == 1){
				firstangle = currentangle;
				lastangle = currentangle;
			}
			boolean flag = false;
			//System.out.println(String.valueOf(currentangle)+";"+String.valueOf(firstangle)+";");
			if(Math.abs(getAngleDifference(currentangle,firstangle)) > BREAK_ANGLE_SUM_MAX){
				flag = true;
				//System.out.println("differes!");
			}
			if(Math.abs(getAngleDifference(currentangle,lastangle)) > BREAK_ANGLE_MAX){
				flag = true;
			}
			
			if(flag){
				//ret.add(calcTracedPath(buff));
				tmparray.add(buff);
				buff = new FlexPointGroup();
				buff.add(al.get(ii));
				firstangle = currentangle;
			}
			lastangle = currentangle;
		}
		if(buff.size()>1){
			tmparray.add(buff);
		}
		ArrayList<FlexPointGroup> tmparray2 = new ArrayList<FlexPointGroup>();
		for(int ii = 0;ii < tmparray.size();ii++){// search "S" shaped curve
			FlexPointGroup parray = tmparray.get(ii);
			
			FlexPoint lastpoint = parray.get(0);
			double tmplinelength = 0.0;
			int linestartindex = 0;
			double lastanglediff = 0;
			boolean lineflag = false;
			ArrayList<Integer> breakat = new ArrayList<Integer>();
			for(int jj = 1;jj < parray.size()-1;jj++){
				FlexPoint f1 = parray.get(jj);
				FlexPoint f2 = parray.get(jj+1);
				double currentangle = FlexPoint.getAngle(f1.x-lastpoint.x,f1.y-lastpoint.y);
				double nextangle = FlexPoint.getAngle(f2.x-f1.x,f2.y-f1.y);
				double anglediff = getAngleDifference(currentangle,nextangle);
				if(Math.abs(anglediff) < LINE_DETECTREDUNDANCY){
					if(!lineflag){
						linestartindex = jj-1;
						tmplinelength = lastpoint.getLength(f1);
					}
					tmplinelength += f1.getLength(f2);
					lineflag = true;
					anglediff = 0;
				}else{
					//if(lineflag && tmplinelength > LINE_DETECTLENGTH){
						//breakat.add(linestartindex);
						//System.out.println("line!;");
						//breakat.add(jj);
					//}
					lineflag = false;
					tmplinelength = 0;
					
					if(lastanglediff*anglediff < -1){
						double[] nex = getAngleAround(parray.list,jj,1.0,1);
						double[] pre = getAngleAround(parray.list,jj,1.0,-1);
						
						if(nex[0]*pre[0] < 0){
							breakat.add(jj);
						}
					}
				}
				lastanglediff = anglediff;
			}
			if(breakat.size() < 1){
				tmparray2.add(parray);
				
			}else{
				int lastbroken = 0;
				for(int jj = 0;jj < breakat.size();jj++){
					int ba = breakat.get(jj);
					if(ba > lastbroken){
						FlexPointGroup fp = new FlexPointGroup();
						for(int kk = lastbroken;kk <= ba;kk++){
							fp.add(parray.get(kk));
						}
						lastbroken = ba;
						tmparray2.add(fp);
					}
				}
				if(lastbroken < parray.size()-1){
					FlexPointGroup fp = new FlexPointGroup();
					for(int kk = lastbroken;kk < parray.size();kk++){
						fp.add(parray.get(kk));
					}
					tmparray2.add(fp);
				}
			}
		}
		
		for(int ii = 0;ii < tmparray2.size();ii++){
			ret.add(calcTracedPath(tmparray2.get(ii).list));
		}
		
		
		
		return ret;
	}
	// average_angle_change,angle_sum_abs(<= very close to zero, the points should be treated as line), 
	public static double[] getAngleAround(ArrayList<FlexPoint> al,int firstpos,double length_limit,int direction){
		FlexPoint last = al.get(firstpos);
		double ret[] = new double[2];
		int posnum = 0;
		double plength = 0;
		double angle_sum = 0;
		double angle_sum_abs = 0;
		double firstangle = 0;
		double lastangle = 0;
		for(int ii = 1;ii < al.size();ii++){
			int index = firstpos+ii*direction;
			
			if(index > al.size()-1 || index < 0){
				break;
			}
			FlexPoint f1 = al.get(index);
			double currentangle = FlexPoint.getAngle(f1.x-last.x,f1.y-last.y);
			if(ii == 1){
				firstangle = currentangle;
				lastangle = currentangle;
			}else{
				double ad = getAngleDifference(currentangle,lastangle);
				if(Math.abs(ad) < LINE_DETECTREDUNDANCY){
					ad = 0;
				}
				angle_sum += ad;
				angle_sum_abs += Math.abs(ad);
			}
			
			posnum++;
			plength += f1.getLength(last);
			if(plength > length_limit){
				break;
			}
			
			last = f1;
		}
		ret[0] = angle_sum/posnum;
		ret[1] = angle_sum_abs;
		
		
		return ret;
	}
	
	
	public static PointTracingResult tracePoints(ArrayList<FlexPoint> al){
		return tracePoints(al,0,al.size()-1);
	}
	
	
	public static double[] bezierAt(double x1,double y1,double x2,double y2,double x3,double y3,double x4,double y4,double ratio){
		
		double[] ret = new double[2];
		double t = ratio;
		double s = 1-t;
		ret[0] = x1*s*s*s+3*s*s*t*x2+3*s*t*t*x3+t*t*t*x4;
		ret[1] = y1*s*s*s+3*s*s*t*y2+3*s*t*t*y3+t*t*t*y4;
		
		
		return ret;
	}
	
	//the most important part of this program
	public static PointTracingResult tracePoints(ArrayList<FlexPoint> al,int start,int end){
		
		//return tracePoints_Simple(al);
		double bsum = 0;
		for(int ii = 1;ii < al.size()-1;ii++){
			
			FlexPoint f1 = al.get(ii-1);
			FlexPoint f2 = al.get(ii);
			FlexPoint f3 = al.get(ii+1);
			bsum += Math.PI-RadianCalculator.radian(f1.x,f1.y,f2.x,f2.y,f3.x,f3.y);
			if(bsum > Math.PI*1.5){
				return new PointTracingResult(null,100000);
			}
		}
		
		ArrayList<FlexPoint> cal = make100DevidedArray(al,start,end);
		
		
		
		LAnchorPath retpath = null;
		double minscore = TRACING_SCORE_THRESHOLD_DEFAULT*2;
		FlexPoint startrota = cal.get(0);
		FlexPoint endrota = cal.get(cal.size()-1);
		int basepoint[] = {10,50,90};//, 5,50,95, 13,50,87, 50,75,90, 10,25,50};
		
		for(int base = 0;base < 1;base++){
			for(int sign = -1; sign < 2;sign += 2){
				int startpoint = (sign==-1)?(0):(1);
				for(int i1 = startpoint;i1 < 3;i1++){
					FlexPoint point01 = cal.get(basepoint[base*3]-i1*sign);
					double currentmin1 = minscore;
					double tmplast1 = minscore;
					for(int i5 = startpoint;i5 < 3;i5++){
						double currentmin5 = minscore;
						double tmplast5 = minscore;
						FlexPoint point05 = cal.get(basepoint[base*3+1]-i5*sign);
						FlexPoint point03 = cal.get((basepoint[base*3]-i1*sign+basepoint[base*3+1]-i5*sign)/2);
						for(int i9 = start;i9 < 3;i9++){
							FlexPoint point09 = cal.get(basepoint[base*3+2]-i9*sign);
							FlexPoint point07 = cal.get((basepoint[base*3+2]-i9*sign+basepoint[base*3+1]-i5*sign)/2);
							
							double sc1x = (-9*point05.x+125*point01.x-90*startrota.x+endrota.x)/27;
							double sc1y = (-9*point05.y+125*point01.y-90*startrota.y+endrota.y)/27;
							double ec1x = (81*point05.x+81*startrota.x-10*endrota.x-125*point01.x)/27;
							double ec1y = (81*point05.y+81*startrota.y-10*endrota.y-125*point01.y)/27;
							double sc2x = (-1000*point09.x-80*startrota.x+648*endrota.x+648*point05.x)/216;
							double sc2y = (-1000*point09.y-80*startrota.y+648*endrota.y+648*point05.y)/216;
							double ec2x = (1000*point09.x+8*startrota.x-720*endrota.x-72*point05.x)/216;
							double ec2y = (1000*point09.y+8*startrota.y-720*endrota.y-72*point05.y)/216;
							
							
							
							
							
							//double s = (FlexPoint.getLength(sc1x,sc1y,sc2x,sc2y)+FlexPoint.getLength(ec1x,ec1y,ec2x,ec2y));
							///Math.max(
							//LAnchorPath.getLength(startrota.x,startrota.y,(sc1x+sc2x)/2,(sc1y+sc2y)/2,(ec1x+ec2x)/2,(ec1y+ec2y)/2,endrota.x,endrota.y,0.01)
							//,1.0);
							
							double[] pre1 = bezierAt(startrota.x,startrota.y,sc1x,sc1y,ec1x,ec1y,endrota.x,endrota.y,0.9);
							double[] pre3 = bezierAt(startrota.x,startrota.y,sc1x,sc1y,ec1x,ec1y,endrota.x,endrota.y,0.7);
							
							double[] pre2 = bezierAt(startrota.x,startrota.y,sc2x,sc2y,ec2x,ec2y,endrota.x,endrota.y,0.1);
							double[] pre4 = bezierAt(startrota.x,startrota.y,sc2x,sc2y,ec2x,ec2y,endrota.x,endrota.y,0.3);
							
							
							double s1 = Math.max(FlexPoint.getLength(point09.x,point09.y,pre1[0],pre1[1]),FlexPoint.getLength(point07.x,point07.y,pre3[0],pre3[1]))*2;
							double s2 = Math.max(FlexPoint.getLength(point01.x,point01.y,pre2[0],pre2[1]),FlexPoint.getLength(point03.x,point03.y,pre4[0],pre4[1]))*2;
							/*
							if(s1 <= s2 && s1 < minscore){
								LAnchorPoint startp = new LAnchorPoint(startrota);
								startp.shapePoint[LAnchorPoint.NEXT].set(sc1x,sc1y);
								LAnchorPoint endp = new LAnchorPoint(endrota);
								endp.shapePoint[LAnchorPoint.PREV].set(ec1x,ec1y);
								LAnchorPath p = new LAnchorPath(startp,endp);
								retpath = p;
								minscore = s1;
								currentmin5 = s1;
								currentmin1 = s1;
								
							}else if(s2 < minscore){
								LAnchorPoint startp = new LAnchorPoint(startrota);
								startp.shapePoint[LAnchorPoint.NEXT].set(sc2x,sc2y);
								LAnchorPoint endp = new LAnchorPoint(endrota);
								endp.shapePoint[LAnchorPoint.PREV].set(ec2x,ec2y);
								LAnchorPath p = new LAnchorPath(startp,endp);
								retpath = p;
								minscore = s2;
								currentmin5 = s2;
								currentmin1 = s2;	
							}
							*/
							
							double s = s1+s2;
							s /= 2;
							
							if(s < minscore){
								LAnchorPoint startp = new LAnchorPoint(startrota);
								startp.shapePoint[LAnchorPoint.NEXT].set((sc2x+sc1x)/2,(sc2y+sc1y)/2);
								LAnchorPoint endp = new LAnchorPoint(endrota);
								endp.shapePoint[LAnchorPoint.PREV].set((ec2x+ec1x)/2,(ec2y+ec1y)/2);
								LAnchorPath p = new LAnchorPath(startp,endp);
								retpath = p;
								minscore = s;
								//System.out.println(String.valueOf(i1)+";"+String.valueOf(i5)+";"+String.valueOf(i9)+";"+String.valueOf(s));
								currentmin5 = s;
								currentmin1 = s;
								
							}
							
							
							//System.out.println("score: "+String.valueOf(s));
						}
						
						if(tmplast5 <= currentmin5){
							break;
						}
					}
					
					if(tmplast1 <= currentmin1){
						break;
					}
				}
			}
		}
		return new PointTracingResult(retpath,minscore);
		

	}
public static PointTracingResult tracePoints_Simple(ArrayList<FlexPoint> al){
		ArrayList<FlexPoint> cal = make100DevidedArray(al);
		FlexPoint startrota = cal.get(0);
		FlexPoint endrota = cal.get(cal.size()-1);
		FlexPoint point01 = cal.get(10);
		FlexPoint point05 = cal.get(50);
		FlexPoint point09 = cal.get(90);
		
		FlexPoint startcont = new FlexPoint(
		(-9*point05.x+125*point01.x-90*startrota.x+endrota.x)/27,
		(-9*point05.y+125*point01.y-90*startrota.y+endrota.y)/27);
		
		FlexPoint endcont = new FlexPoint(
		(81*point05.x+81*startrota.x-10*endrota.x-125*point01.x)/27,
		(81*point05.y+81*startrota.y-10*endrota.y-125*point01.y)/27
		);
		
		
		FlexPoint startcont2 = new FlexPoint(
		(-1000*point09.x-80*startrota.x+648*endrota.x+648*point05.x)/216,
		(-1000*point09.y-80*startrota.y+648*endrota.y+648*point05.y)/216
		);
		
		FlexPoint endcont2 = new FlexPoint(
		(1000*point09.x+8*startrota.x-720*endrota.x-72*point05.x)/216,
		(1000*point09.y+8*startrota.y-720*endrota.y-72*point05.y)/216
		);
		
		
		LAnchorPoint startp = new LAnchorPoint(startrota);
		startp.shapePoint[LAnchorPoint.NEXT].set((startcont2.x+startcont.x)/2,(startcont2.y+startcont.y)/2);
		LAnchorPoint endp = new LAnchorPoint(endrota);
		endp.shapePoint[LAnchorPoint.PREV].set((endcont2.x+endcont.x)/2,(endcont2.y+endcont.y)/2);
		
		
		LAnchorPath p = new LAnchorPath(startp,endp);
		double s = (startcont.getLength(startcont2)+endcont.getLength(endcont2))/Math.max(p.getLength(),3);
		

		return new PointTracingResult(p,s);
		
	}
	public static LAnchorPath calcTracedPath(ArrayList<FlexPoint> al){
	
		return tracePoints(al).path;
		
	}
	public static ArrayList<FlexPoint> make100DevidedArray(ArrayList<FlexPoint> al){
		return make100DevidedArray(al,0,al.size()-1);
	}
	public static ArrayList<FlexPoint> make100DevidedArray(ArrayList<FlexPoint> al,int start,int end){
		double len= 0;
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>(101);
		for(int ii = start+1;ii <= end;ii++){
			len += al.get(ii-1).getLength(al.get(ii));
		}
		double plen = 0;
		int currentnum = 0;
		ret.add(new FlexPoint(al.get(start)));
		for(int ii = start+1;ii <= end;ii++){
			double klen = plen;
			plen += al.get(ii-1).getLength(al.get(ii));
			
			if(plen >= len*0.01*(currentnum+1)){
				for(int jj=1;jj <= 100;jj++){
					klen += al.get(ii-1).getLength(al.get(ii))/100;
					if(klen >= len*0.01*(currentnum+1)){
						ret.add(new FlexPoint(al.get(ii-1).x/100*(100-jj)+al.get(ii).x/100*jj
						,al.get(ii-1).y/100*(100-jj)+al.get(ii).y/100*jj));
						currentnum++;
					}
				}
			}
		}
		while(ret.size() < 101){
			ret.add(new FlexPoint(al.get(end)));
		}
		return ret;
	}
	
}



class PointTracingResult{
	LAnchorPath path;
	double score = 0.0;
	PointTracingResult(LAnchorPath lap,double s){
		path = lap;
		score = s;
	}
}


		/* Point Trace Plan 1 (very slow)
		ArrayList<FlexPoint> cal = make100DevidedArray(al);
		LAnchorPath retpath = null;
		double minscore = 999;
		FlexPoint startrota = cal.get(0);
		FlexPoint endrota = cal.get(cal.size()-1);
		for(int i1 = 5;i1 < 50;i1+=5){
			FlexPoint point01 = cal.get(i1);
			for(int i5 = i1+5;i5 <= 75;i5+=5){
				FlexPoint point05 = cal.get(i5);
				for(int i9 = i5+5;i9 <= 95;i9+=5){
					FlexPoint point09 = cal.get(i9);
					
					FlexPoint startcont = new FlexPoint(
					(-9*point05.x+125*point01.x-90*startrota.x+endrota.x)/27,
					(-9*point05.y+125*point01.y-90*startrota.y+endrota.y)/27);
					
					FlexPoint endcont = new FlexPoint(
					(81*point05.x+81*startrota.x-10*endrota.x-125*point01.x)/27,
					(81*point05.y+81*startrota.y-10*endrota.y-125*point01.y)/27
					);
					
					
					FlexPoint startcont2 = new FlexPoint(
					(-1000*point09.x-80*startrota.x+648*endrota.x+648*point05.x)/216,
					(-1000*point09.y-80*startrota.y+648*endrota.y+648*point05.y)/216
					);
					
					FlexPoint endcont2 = new FlexPoint(
					(1000*point09.x+8*startrota.x-720*endrota.x-72*point05.x)/216,
					(1000*point09.y+8*startrota.y-720*endrota.y-72*point05.y)/216
					);
					
					
					LAnchorPoint startp = new LAnchorPoint(startrota);
					startp.shapePoint[LAnchorPoint.NEXT].set((startcont2.x+startcont.x)/2,(startcont2.y+startcont.y)/2);
					LAnchorPoint endp = new LAnchorPoint(endrota);
					endp.shapePoint[LAnchorPoint.PREV].set((endcont2.x+endcont.x)/2,(endcont2.y+endcont.y)/2);
					
					
					LAnchorPath p = new LAnchorPath(startp,endp);
					double s = (startcont.getLength(startcont2)+endcont.getLength(endcont2))/p.getLength();
					if(s < minscore){
						retpath = p;
						minscore = s;
						if(minscore < TRACING_SCORE_THRESHOLD_DEFAULT/2){
							 return new PointTracingResult(retpath,minscore);
						}
					}
					//System.out.println("score: "+String.valueOf(s));
				}
			}
		}
		return new PointTracingResult(retpath,minscore);
		*/