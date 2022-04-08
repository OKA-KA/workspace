
package p2vj;
import java.util.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;
public class BitMapTracer{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int LINE_V = 0;
	public static final int LINE_H = 1;
	public static final int DIREC_UP = 0;
	public static final int DIREC_RIGHT = 1;
	public static final int DIREC_DOWN = 2;
	public static final int DIREC_LEFT = 3;
	
	public static BufferedImage treatBoundaryOverlapping(BufferedImage src,BufferedImage dest,Integer c,Hashtable<Integer,Integer> hash,int smallerisupper){
		BufferedImage ret = dest;
		int sheight = src.getHeight();
		int swidth = src.getWidth();
		Graphics2D g2 = ret.createGraphics();
		int background = Color.white.getRGB();
		g2.setColor(Color.white);
		if(c == background){
			background = Color.black.getRGB();
			g2.setColor(Color.black);
		}
		
		g2.fillRect(0,0,swidth,sheight);
		
		int pixnum = hash.get(c);
		for(int yy = 0;yy < sheight;yy++){
			int[] colors = new int[swidth];
			src.getRGB(0,yy,swidth,1,colors,0,swidth);
			for(int xx = 0;xx < swidth;xx++){
				if(colors[xx] == c){
					ret.setRGB(xx,yy,c);
					boolean flag[] = {true,true,true,true};
					//boolean flag[] = {false,false,false,false};
					if(xx != 0 && hash.get(src.getRGB(xx-1,yy))*smallerisupper < pixnum*smallerisupper){//enlarge region if it will be under the adjasent region
						ret.setRGB(xx-1,yy,c);
						if(yy != 0 && hash.get(src.getRGB(xx-1,yy-1))*smallerisupper < pixnum*smallerisupper){
							flag[0] = true;
							ret.setRGB(xx-1,yy-1,c);
						}
						if(yy < sheight-2 && hash.get(src.getRGB(xx-1,yy+1))*smallerisupper < pixnum*smallerisupper){
							flag[1] = true;
							ret.setRGB(xx-1,yy+1,c);
						}
					}
					if(yy != 0 && hash.get(src.getRGB(xx,yy-1))*smallerisupper < pixnum*smallerisupper){
						ret.setRGB(xx,yy-1,c);
						if(xx != 0 && hash.get(src.getRGB(xx-1,yy-1))*smallerisupper < pixnum*smallerisupper){
							if(flag[0])ret.setRGB(xx-1,yy-1,c);
						}
						if(xx < swidth-2 && hash.get(src.getRGB(xx+1,yy-1))*smallerisupper < pixnum*smallerisupper){
							flag[2] = true;
							ret.setRGB(xx+1,yy-1,c);
						}
					}
					
					if(xx < swidth-2 && hash.get(src.getRGB(xx+1,yy))*smallerisupper < pixnum*smallerisupper){
						ret.setRGB(xx+1,yy,c);
						
						if(yy != 0 && hash.get(src.getRGB(xx+1,yy-1))*smallerisupper < pixnum*smallerisupper){
							if(flag[2])ret.setRGB(xx+1,yy-1,c);
						}
						if(yy < sheight-2 && hash.get(src.getRGB(xx+1,yy+1))*smallerisupper < pixnum*smallerisupper){
							flag[3] = true;
							ret.setRGB(xx+1,yy+1,c);
						}
					}
					
					if(yy < sheight-2 && hash.get(src.getRGB(xx,yy+1))*smallerisupper < pixnum*smallerisupper){
						ret.setRGB(xx,yy+1,c);
						if(xx != 0 && hash.get(src.getRGB(xx-1,yy+1))*smallerisupper < pixnum*smallerisupper){
							if(flag[1])ret.setRGB(xx-1,yy+1,c);
						}
						if(xx < swidth-2 && hash.get(src.getRGB(xx+1,yy+1))*smallerisupper < pixnum*smallerisupper){
							if(flag[3])ret.setRGB(xx+1,yy+1,c);
						}
					}
					
				}
			}
		}
		
		//boundary treatment
		//boundary_overlap はもと sheight もしくは swidth だったが、透明度が高い場合枠線のようになってしまったので中止
		int boundary_overlap = 3;
		
		for(int xx = 0;xx < swidth;xx+=swidth-1){
			int[] colors = new int[sheight];
			src.getRGB(xx,0,1,sheight,colors,0,1);
			for(int yy = 0;yy < sheight;yy++){
		
				if(colors[yy] == c){
					for(int y2 = 1;y2 < boundary_overlap;y2+=1){
						if(yy+y2 < sheight && hash.get(colors[yy+y2])*smallerisupper < pixnum*smallerisupper){
							ret.setRGB(xx,yy+y2,c);
						}else{
							break;
						}
					}
					for(int y2 = 1;y2 < boundary_overlap;y2+=1){
						if(yy-y2 >= 0 && hash.get(colors[yy-y2])*smallerisupper < pixnum*smallerisupper){
							ret.setRGB(xx,yy-y2,c);
						}else{
							break;
						}
					}
					
				}
			}
		}
		for(int yy = 0;yy < sheight;yy+=sheight-1){
			int[] colors = new int[swidth];
			src.getRGB(0,yy,swidth,1,colors,0,swidth);
			for(int xx = 0;xx < swidth;xx++){
				if(colors[xx] == c){
					for(int x2 = 1;x2 < boundary_overlap;x2+=1){
						if(xx+x2 < swidth && hash.get(colors[xx+x2])*smallerisupper < pixnum*smallerisupper){
							ret.setRGB(xx+x2,yy,c);
						}else{
							break;
						}
					}
					
					for(int x2 = 1;x2 < boundary_overlap;x2+=1){
						if(xx-x2 >=0 && hash.get(colors[xx-x2])*smallerisupper < pixnum*smallerisupper){
							ret.setRGB(xx-x2,yy,c);
						}else{
							break;
						}
					}
				}
			}
		}
		
		return ret;
	}
	public static ArrayList<PointList> traceColorBoundary(BufferedImage bi,Color c){
		ArrayList<PointList> parray = new ArrayList<PointList>();
		int crgb = c.getRGB();
		int width = bi.getWidth();
		int height = bi.getHeight();
		int histx[] = {-1,-2};
		int histy[] = {-1,-2};
		boolean[][][] ret = new boolean[width+1][height+1][2];
		for(int xx = 0;xx <= width;xx++){
			for(int yy = 0;yy <= height;yy++){
				ret[xx][yy][LINE_V] = false;
				ret[xx][yy][LINE_H] = false;
			}
		}
		
		for(int xx = 0;xx < width;xx++){
			for(int yy = 0;yy < height;yy++){
				int bc = bi.getRGB(xx,yy);
				if(bc == crgb){
					if(xx == 0){
						ret[xx][yy][LINE_V] = true;;
					}else{
						int bcx = bi.getRGB(xx-1,yy);
						if(bcx != crgb){
							ret[xx][yy][LINE_V] = true;;
						}
					}
					
					if(yy == 0){
						ret[xx][yy][LINE_H] = true;
					}else{
						int bcy = bi.getRGB(xx,yy-1);
						if(bcy != crgb){
							ret[xx][yy][LINE_H] = true;;
						}
					}
					
					if(xx == width-1){
						ret[xx+1][yy][LINE_V] = true;;
					}else{
						int bcx = bi.getRGB(xx+1,yy);
						if(bcx != crgb){
							ret[xx+1][yy][LINE_V] = true;;
						}
					}
					if(yy == height-1){
						ret[xx][yy+1][LINE_H] = true;
					}else{
						int bcy = bi.getRGB(xx,yy+1);
						if(bcy != crgb){
							ret[xx][yy+1][LINE_H] = true;;
						}
					}
				}
			}
		}
		for(int xx = 0;xx < width;xx++){
			for(int yy = 0;yy < height;yy++){
				if(ret[xx][yy][LINE_V]){
					PointList plist = new PointList();
					plist.add(xx,yy);
					plist.add(xx,yy+1);
					ret[xx][yy][LINE_V] = false;
					int currentx = xx;
					int currenty = yy+1;
					int lastdirec = DIREC_DOWN;
					int nextdirec = searchNextPath(ret,currentx,currenty,DIREC_DOWN,lastdirec);
					
					histx[1] = xx;
					histy[1] = yy;
					histx[0] = xx;
					histy[0] = yy+1;
					while(nextdirec != -1){
						
						switch(nextdirec){
							case DIREC_UP:
							currenty--;
							ret[currentx][currenty][LINE_V] = false;
							break;
							case DIREC_DOWN:
							ret[currentx][currenty][LINE_V] = false;
							currenty++;
							break;
							case DIREC_RIGHT:
							ret[currentx][currenty][LINE_H] = false;
							currentx++;
							break;
							case DIREC_LEFT:
							currentx--;
							ret[currentx][currenty][LINE_H] = false;
							break;
						}
						if(histx[0] == currentx && histx[1] == currentx){
							plist.getLast().setLocation(currentx,currenty);
						}else if(histy[0] == currenty && histy[1] == currenty){
							plist.getLast().setLocation(currentx,currenty);
						}else{
							plist.add(currentx,currenty);
						}
						/*
						if(lastdirec > -1 || nextdirec > -1){
							plist.add(currentx,currenty);
						}*/
						histx[1] = histx[0];
						histy[1] = histy[0];
						histx[0] = currentx;
						histy[0] = currenty;
						int plastdirec = lastdirec;
						lastdirec = nextdirec;
						nextdirec = searchNextPath(ret,currentx,currenty,lastdirec,plastdirec);
						//System.out.println(String.valueOf(plastdirec)+String.valueOf(lastdirec)+String.valueOf(nextdirec));
						if(lastdirec == nextdirec && plastdirec != -1){
							lastdirec = plastdirec;
						}
					}
					/*
					for(int pp = 0;pp < plist.size();pp++){
						
						//System.out.println(String.valueOf(plist.get(pp).x)+";"+String.valueOf(plist.get(pp).y));
					}*/
					
					
					
					FlexPoint p0 = plist.get(0);
					FlexPoint cp0 = new FlexPoint(plist.get(0));
					FlexPoint p1 = plist.get(1);
					p0.set(cp0.x/2+p1.x/2,cp0.y/2+p1.y/2);
					plist.add(cp0);
					parray.add(plist);
					
				}
			}
		}
		
		return parray;
	}
	
	
	/**
	 * 1 ドットしかない場合カドとみなして削っている部分だったと思う
	 * @param al
	 * @param closedflag
	 * @return 
	 */
	public static ArrayList<FlexPoint> correctOneDot(ArrayList<FlexPoint> al,boolean closedflag){
		Hashtable<Integer,Boolean> isonedot = new Hashtable<Integer,Boolean>();
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		double prev_broken = 1;
		
		if(closedflag){
			while(true){
				FlexPoint p1 = al.get(al.size()-1);
				FlexPoint p2 = al.get(0);
				if(p1.distance(p2) < 0.5){
					al.remove(al.size()-1);
				}else{
					break;
				}
			}
		}
		
		for(int ii = 0;ii < al.size()-1;ii++){
			FlexPoint p1 = al.get(ii);
			FlexPoint p2 = al.get(ii+1);
			if((int)(p2.distance(p1)+0.5) < 2){
				isonedot.put(ii,true);
			}
		}
		if(closedflag){
			FlexPoint p1 = al.get(al.size()-1);
			FlexPoint p2 = al.get(0);
			if((int)(p1.distance(p2)+0.5) == 1){
				isonedot.put(al.size(),true);
			}
		}
		
		Enumeration<Integer> e =isonedot.keys();
		Hashtable<Integer,Double> break_len = new Hashtable<Integer,Double>();//隣に1ドットがあるので分割される点
		while (e.hasMoreElements()){
			int onedot = e.nextElement();
			int asize = al.size();
			int index1 = onedot-1;
			int index2 = onedot;
			int index3 = onedot+1;
			int index4 = onedot+2;
			
			if(closedflag){
				index1 = (index1+asize)%asize;
				index2 = (index2+asize)%asize;
				index3 = (index3+asize)%asize;
				index4 = (index4+asize)%asize;
			}else{
				if(index1 < 0){
					continue;
				}
				if(index4 > asize-1){
					continue;
				}
			}
			
			FlexPoint p1 = al.get(index1);
			FlexPoint p2 = al.get(index2);
			FlexPoint p3 = al.get(index3);
			FlexPoint p4 = al.get(index4);
			
			double len1 = p1.distance(p2);
			double len2 = p3.distance(p4);
			if(Math.abs(len1-len2) < 1){
				break_len.put(onedot,len1);
			}else{
				if(len1 > len2){
					break_len.put(onedot,len2);
				}else{
					break_len.put(onedot,len1);
				}
			}
		}
		
		int roll = (closedflag)?(al.size()+1):(al.size());
		for(int ii = 0;ii < roll-1;ii++){
			int i2 = ii+1;
			int i1 = ii-1;
			if(ii == roll-2){
				if(closedflag){
					i2 = (i2+al.size())%al.size();
					i1 = (i1+al.size())%al.size();
				}else{
					break;
				}
			}
			
			
			FlexPoint p1 = al.get(ii%al.size());
			FlexPoint p2 = al.get(i2%al.size());
			double idist = p1.distance(p2);
			if(break_len.get(i2) != null && break_len.get(i1) != null){
				if(break_len.get(i2)+break_len.get(i1) > idist){
					double overlap = break_len.get(i2)+break_len.get(i1)-idist;
					break_len.put(i1,break_len.get(i1)-overlap/2);
					break_len.put(i2,break_len.get(i2)-overlap/2);
				}
			}
		}
		
		
		for(int ii = 0;ii < roll-1;ii++){
			int nex = (ii+1)%al.size();
			int nex2 = (ii+2)%al.size();
			int pre = (ii-1+al.size())%al.size();

			
			FlexPoint p0 = al.get(pre);
			FlexPoint p1 = al.get(ii%al.size());
			FlexPoint p2 = al.get(nex);
			FlexPoint p3 = al.get(nex2);
			if(isonedot.get(ii) != null){
				if(!closedflag && (ii==0 || ii == al.size() - 1)){
					ret.add(new FlexPoint((p1.x+p2.x)/2,(p1.y+p2.y)/2));
					continue;
				}
				if(break_len.get(ii) != null && isonedot.get(pre) == null){
					double xlen = break_len.get(ii);
					double px = p0.x-p1.x;
					double py = p0.y-p1.y;
					double pl = Math.sqrt(px*px+py*py);
					FlexPoint pp2 = new FlexPoint(px/pl*xlen+p1.x,py/pl*xlen+p1.y);
					ret.add(pp2);
					
				}
				//if(isonedot.get(pre) != null || isonedot.get(nex) != null){
					ret.add(new FlexPoint((p1.x+p2.x)/2,(p1.y+p2.y)/2));
				//}
				if(break_len.get(ii) != null && isonedot.get(nex) == null){
					double xlen = break_len.get(ii);
					double px = p3.x-p2.x;
					double py = p3.y-p2.y;
					double pl = Math.sqrt(px*px+py*py);
					ret.add(new FlexPoint(px/pl*xlen+p2.x,py/pl*xlen+p2.y));
				}
				if(closedflag && ii == roll - 2){
					if(break_len.get(nex) != null){
						double xlen = break_len.get(nex);
						double px = p2.x-p1.x;
						double py = p2.y-p1.y;
						double pl = Math.sqrt(px*px+py*py);
						ret.add(new FlexPoint(px/pl*(pl-xlen)+p1.x,py/pl*(pl-xlen)+p1.y));
					}
					ret.add(new FlexPoint(ret.get(0).x,ret.get(0).y));
					continue;
				}
			}else{
				if(break_len.get(pre) == null){
					if(break_len.get(nex) == null){
						ret.add(new FlexPoint(p1.x,p1.y));// no corrections around
					}else{
						if(p1.distance(p2)-break_len.get(nex) > 0.3){
							ret.add(new FlexPoint(p1.x,p1.y));//is not added
						}
					}
				}else{
					if(break_len.get(nex) == null){
						ret.add(new FlexPoint(p2.x,p2.y));
					}
				}
				if(closedflag && ii == roll - 2){
					if(break_len.get(nex) != null){
						double xlen = break_len.get(nex);
						double px = p2.x-p1.x;
						double py = p2.y-p1.y;
						double pl = Math.sqrt(px*px+py*py);
						ret.add(new FlexPoint(px/pl*(pl-xlen)+p1.x,py/pl*(pl-xlen)+p1.y));
					}
					ret.add(new FlexPoint(ret.get(0).x,ret.get(0).y));
					continue;
				}
				
			}
			
		}
		
		ArrayList<FlexPoint> ret2 = new ArrayList<FlexPoint>();
		FlexPoint last = ret.get(0);
		ret2.add(last);
			//System.out.println(String.valueOf(last.x)+":::"+String.valueOf(last.y));
		for(int ii = 1;ii < ret.size();ii++){
			FlexPoint pp = ret.get(ii);
			if(last.getLength(pp) > 0.2){
				ret2.add(pp);
				//System.out.println(String.valueOf(pp.x)+":::"+String.valueOf(pp.y));
				last = pp;
			}
		}
		return ret;
	}
	public static int[] getNextXY(int x,int y,int direc){
		int ret[] = {0,0};
		ret[0] = x;
		ret[1] = y;
		switch(direc){
			case DIREC_UP:
			ret[1] = y-1;
			break;
			case DIREC_DOWN:
			ret[1] = y+1;
			break;
			case DIREC_RIGHT:
			ret[0] = x+1;
			break;
			case DIREC_LEFT:
			ret[0] = x-1;
			break;
		}
		return ret;
	}
	public static boolean isAvailable(boolean[][][] b,int cx,int cy,int direc){
		
		int xmax = b.length;
		int ymax = b[0].length;
		switch(direc){
			case DIREC_UP:
				if(cy <= 0){
					return false;
				}else{
					return b[cx][cy-1][LINE_V];
				}
			//break;
			
			case DIREC_DOWN:
				return b[cx][cy][LINE_V];
				
			//break;
			
			
			
			case DIREC_LEFT:
			if(cx <= 0){
				return false;
			}else{
				return b[cx-1][cy][LINE_H];
			}
			//break;
			case DIREC_RIGHT:
				return b[cx][cy][LINE_H];
			//break;
		}
		return false;
	}
	public static int searchNextPath(boolean[][][] b,int cx,int cy,int lastdirec,int lastdirec2){
		boolean[] ret = new boolean[4];
		int xmax = b.length;
		int ymax = b[0].length;
		int tmpdirec = -1;
		if(lastdirec2 > -1 && isAvailable(b,cx,cy,lastdirec2)){
			return lastdirec2;
		}
		if(isAvailable(b,cx,cy,(lastdirec+4-1)%4)){
			tmpdirec = (lastdirec+4-1)%4;
			return tmpdirec;
		}
		if(isAvailable(b,cx,cy,(lastdirec+4+1)%4)){
			tmpdirec = (lastdirec+4+1)%4;
			return tmpdirec;
		}
		
		if(isAvailable(b,cx,cy,lastdirec)){
			tmpdirec = lastdirec;
			return tmpdirec;
		}
		
		return tmpdirec;
	}
	
}
