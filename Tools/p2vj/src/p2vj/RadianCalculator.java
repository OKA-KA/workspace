package p2vj;

import java.util.ArrayList;
public class RadianCalculator{
	
	/**
	 * about 5times faster than acos
	 * resolution rates become very low at points close to 0 or pi.
	 */
	private static final int RESOLUTION_RATE = 10000;
	private static double radian_at_length[] = new double[RESOLUTION_RATE+3];
	private static double rate = RESOLUTION_RATE/4.0;//RESOLUTION_RATE/maxvalue
	static{
		for(int ii = 0;ii < radian_at_length.length;ii++){
			radian_at_length[ii] = -1;
		}
		
		for(int ii = 0;ii < RESOLUTION_RATE;ii++){
			double dd = ii*Math.PI/(double)(RESOLUTION_RATE);
			double c = Math.cos(dd);
			double s = Math.sin(dd);
			double len =(1-c)*(1-c)+s*s;
			if(s < 0){
				System.err.println(s);
				continue;
			}
			radian_at_length[hash(len)] = dd;
		}
		radian_at_length[0] = 0.0;
		radian_at_length[RESOLUTION_RATE] = Math.PI;
		radian_at_length[RESOLUTION_RATE+1] = Math.PI;
		
		double last = 0.0;
		for(int ii = 0;ii < radian_at_length.length;ii++){
			if(radian_at_length[ii] < -0.5){
				radian_at_length[ii] = last;
			}else{
				last = radian_at_length[ii];
			}
		}
	}
	
	public static int hash(double d){
	//	return (int)(rate*((d-2)*(d-2)*((d<2)?(-1):(1))+4));
		return (int)(rate*d+0.5);
		
	}
	
	
	/**
	 *    p1
	 * p2<
	 *    p3
	 * のような角のラジアンを返す
	 */
	public static double radian(double x1,double y1,double x2,double y2,double x3,double y3){
		
		double tx = x1-x2;
		double ty = y1-y2;
		double sx = x3-x2;
		double sy = y3-y2;
		
		if((tx == 0 && ty == 0)|| (sx == 0 && sy == 0)){
			return 0;
		}
		double tlen = Math.sqrt(tx*tx+ty*ty);
		tx /= tlen;
		ty /= tlen;
		
		double slen = Math.sqrt(sx*sx+sy*sy);
		sx /= slen;
		sy /= slen;
		
		sx -= tx;
		sy -= ty;
		return radian(sx*sx+sy*sy);
	}
	
	public static double radian(double length){
		//System.err.println((int)(length*rate));
		return radian_at_length[hash(length)];
	}
	public static double[] bezier(double[] p){
		
		int breaksize = 1000;
		double[] pp = new double[breaksize*2+2];
		
		for(int i = 0;i <= 1000;i++){
			double t = i/1000.0;
			double s = 1-t;
			pp[i] = p[0]*s*s*s+3*s*s*t*p[1]+3*s*t*t*p[2]+t*t*t*p[3];
			pp[i+breaksize+1] = p[4]*s*s*s+3*s*s*t*p[5]+3*s*t*t*p[6]+t*t*t*p[7];
		}
		
		return pp;
	}
	public static double[] bezier_averagedist(double[] p){
		
		int breaksize = 1000;
		int breaksize10 = breaksize*10;
		double[] pp = new double[breaksize*2+2];
		double dsum = 0;
		double lastx = p[0];
		double lasty = p[4];
		for(int i = 0;i <= breaksize10;i++){
			double t = i/(double)breaksize10;
			double s = 1-t;
			double ss = p[0]*s*s*s+3*s*s*t*p[1]+3*s*t*t*p[2]+t*t*t*p[3];
			double kk = p[4]*s*s*s+3*s*s*t*p[5]+3*s*t*t*p[6]+t*t*t*p[7];
			dsum += distance(ss,kk,lastx,lasty);
			lastx = ss;
			lasty = kk;
		}
		
		double step = dsum/breaksize;
		
		int cou = 0;
		double ddsum = 0;
		lastx = p[0];
		lasty = p[4];
		pp[cou] = lastx;
		pp[cou+breaksize+1] = lasty;
		for(int i = 0;i <= breaksize10;i++){
			double t = i/(double)breaksize10;
			double s = 1-t;
			double ss = p[0]*s*s*s+3*s*s*t*p[1]+3*s*t*t*p[2]+t*t*t*p[3];
			double kk = p[4]*s*s*s+3*s*s*t*p[5]+3*s*t*t*p[6]+t*t*t*p[7];
			ddsum += distance(ss,kk,lastx,lasty);
			lastx = ss;
			lasty = kk;
			
			if(ddsum > cou*step){
				//System.err.println(cou+"#"+i);
				cou++;
				if(cou > breaksize){
					System.err.println(cou);
				}else{
					pp[cou] = ss;
					pp[cou+breaksize+1] = kk;
					if(i%100 == 0){
					//	System.err.println(cou+"#"+ss+";"+kk+";"+p[0]+","+p[4]+";"+p[3]+","+p[7]+";"+t);
					}
				}
			}
		}
		
		
		pp[breaksize] = p[3];
		pp[breaksize*2+1] = p[7];
		
		
		return pp;
	}
	
	
	
	public static double[] calcWithRadian(double[] points,double ratio){
		int breaksize = points.length/2;
		double sumratio = 0;
		ArrayList<Double> al = new ArrayList<>();
		for(int i = 1;i < breaksize-1;i++){
			double rad = radian(
			points[i-1],
			points[i+breaksize-1],
			points[i],
			points[i+breaksize],
			points[i+1+breaksize],
			points[i+1+breaksize]
			);
			sumratio += Math.PI-rad;
			al.add(sumratio);
		}
		double[] ret = {points[breaksize-1],points[points.length-1]};
		for(int ii = 0;ii < al.size();ii++){
			if(al.get(ii) > sumratio*ratio){
				ret[0] = points[ii];
				ret[1] = points[ii+breaksize];
				System.err.println(ii);
				return ret;
			}
		}
		return ret;
		
	}
	
	public static double distance(double x,double y,double x2,double y2){
		double len = (x-x2)*(x-x2)+(y-y2)*(y-y2);
		
		if(len == 0){
			return 0;
		}
		return Math.sqrt(len);
	}
	public static double[] calcWithLength(double[] points,double ratio){
		int breaksize = points.length/2;
		double sumratio = 0;
		ArrayList<Double> al = new ArrayList<>();
		for(int ii = 0;ii < breaksize-1;ii++){
			double len = distance(points[ii],points[breaksize+ii],points[ii+1],points[breaksize+1+ii]);
			sumratio += len;
			al.add(sumratio);
		}
		double[] ret = {points[breaksize-1],points[points.length-1]};
		for(int ii = 0;ii < al.size();ii++){
			if(al.get(ii) > sumratio*ratio){
				ret[0] = points[ii];
				ret[1] = points[ii+breaksize];
				return ret;
			}
		}
		return ret;
		
	}
	
	
	
	public static void main_(String[] args){
		double diffsum = 0;
		for(int ii = 0;ii < 100;ii++){
			
			double x0 = Math.random()*200;
			double x1 = Math.random()*200;
			double x2 = 400-Math.random()*200;
			double x3 = Math.random()*200+400;
			x0 = 0;
			x3 = 400;
			
			double y0 = Math.random()*0;
			double y1 = Math.random()*400+200;
			double y2 = Math.random()*400+200;
			double y3 = Math.random()*0;
			
			double tmp[] = {x0,x1,x2,x3,y0,y1,y2,y3};
			double p[] = bezier(tmp);
			
			double assumed[] = bezier_averagedist(tmp);
			
			double ratio = 0.9;
			double[] r = calcWithRadian(assumed,ratio);
			double[] l = calcWithLength(assumed,ratio);
			
			int bs = p.length/2-1;
			double rdist = distance(r[0],r[1],p[(int)(bs*ratio)],p[(int)(bs*ratio)+bs]);
			double ldist = distance(l[0],l[1],p[(int)(bs*ratio)],p[(int)(bs*ratio)+bs]);
			diffsum += rdist-ldist;
			System.err.println(r[0]+","+r[1]+","+p[(int)(bs*ratio)]+","+p[(int)(bs*ratio)+bs]);
			System.err.println(rdist+";"+ldist);
		}
		System.err.println(diffsum);
	
	}
	
	public static void main(String[] args){
		
		double tsum = 0;
		double csum = 0;
		for(int ii = 0;ii < 10;ii++){
			double dm = System.currentTimeMillis();
			for(double dd = 0.0;dd <= Math.PI;dd+=0.01){
				double c = Math.cos(dd);
				double s = Math.sin(dd);
				double d = radian(c,s,0,0,1.0,0);
				
				if(dd < 1.0)System.out.println(d+","+dd+";"+String.valueOf(dd-d));
			}
			double ttime = System.currentTimeMillis()-dm;
			
			dm = System.currentTimeMillis();
			for(double dd = 0.0;dd <= Math.PI;dd+=0.01){
				double c = Math.cos(dd);
				double s = Math.sin(dd);
				double d = Math.acos(c);
			}
			double ctime = System.currentTimeMillis()-dm;
			tsum+=ttime;
			csum+=ctime;
		}
		System.err.println(tsum+";"+csum);
		
		
	
	}
	
	
}