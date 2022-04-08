package p2vj;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;
import java.io.*;
public class ImagePosterizer{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	
	public static Hashtable<Color,Integer> getPixelNum(BufferedImage bi){
		Hashtable<Color,Integer> ret = new Hashtable<Color,Integer>();
		for(int xx = 0;xx < bi.getWidth();xx++){
			for(int yy = 0;yy < bi.getHeight();yy++){
				Color c = new Color(bi.getRGB(xx,yy));
				if(ret.get(c) != null){
					ret.put(c,ret.get(c)+1);
				}else{
					ret.put(c,1);
				}
			}
		}
		return ret;
	}
	
	public static HashMap<Color,Integer> getPixelNum_(BufferedImage bi){
		HashMap<Color,Integer> ret = new HashMap<Color,Integer>();
		for(int xx = 0;xx < bi.getWidth();xx++){
			for(int yy = 0;yy < bi.getHeight();yy++){
				Color c = new Color(bi.getRGB(xx,yy),true);
				if(ret.get(c) != null){
					ret.put(c,ret.get(c)+1);
				}else{
					ret.put(c,1);
				}
			}
		}
		return ret;
	}
	public static RegionData[][] mapRegionData(BufferedImage src){
		RegionData[][] rd = new RegionData[src.getWidth()][src.getHeight()];
		ArrayList<RegionData> mainarray = new ArrayList<RegionData>();
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				RegionData[] around = getAdjacentRegionData(rd,xx,yy,1,true);
				for(int ii = 0;ii < around.length;ii++){
					if(around[ii] != null && around[ii].isSameColor(colors[xx])){
						if(rd[xx][yy] != null){
							rd[xx][yy].mergeWith(around[ii]);
						//around[ii].mergeWith(rd[xx][yy]);
						
						}else{
							rd[xx][yy] = around[ii];
							rd[xx][yy].addPoint(xx,yy);
							rd[xx][yy].increment();
						}
					}
				}
				if(rd[xx][yy] == null){
							rd[xx][yy] = new RegionData(new Color(colors[xx],true),xx,yy,mainarray);
				}
			}
		}
		return rd;
	}
	public static BufferedImage removeAntiAliasing(BufferedImage src,int dotnum,int redundancy){
		RegionData[][] rd = mapRegionData(src);
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				if(!rd[xx][yy].isAntiAliasing() && rd[xx][yy].getPixNum() > dotnum){
					continue;
				}
				RegionData[] around = getAdjacentRegionData(rd,xx,yy,1);
				int mindiff = 9999999;
				Color mincolor = null;
				for(int jj = 0;jj < around.length;jj++){
					for(int ii = 0;ii < around.length;ii++){
						if(!around[jj].isAntiAliasing() && !around[ii].isAntiAliasing()
								&&  around[ii].getPixNum() > dotnum &&  around[jj].getPixNum() > dotnum){
							
								
							int[] res = isBetween(around[ii].getColor().getRGB()
									,rd[xx][yy].getColor().getRGB(),around[jj].getColor().getRGB()
									,redundancy
							,true);
							if(res[0] == 1){
								int diff = getColorDifference(around[ii].getColor(),around[jj].getColor(),false);
								//	System.out.println(String.valueOf(xx)+";"+String.valueOf(yy)+";"+String.valueOf(diff));
								if(diff > 0 && diff < mindiff){
								//	System.out.println("TT");
									mindiff = diff;
									mincolor = new Color(res[1],true);
									
								}
							}
							
							
						}
					}
				}
				
				if(mincolor != null){
					rd[xx][yy].setColor(mincolor);
				}
				
			}
			
			
		}
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				colors[xx] = rd[xx][yy].getColor().getRGB();
				
			}
			
			src.setRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			
		}
		
		return src;
	}
	
	
	
	public static BufferedImage removeSmallDots(BufferedImage src,int redundancy,int dotnum){
		RegionData[][] rd = mapRegionData(src);
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				if(rd[xx][yy].getPixNum() > dotnum && dotnum > 0){
					continue;
				}
				RegionData[] around = getAdjacentRegionData(rd,xx,yy,1);
				int mindiff = redundancy+1;
				Color mincolor = null;
				for(int jj = 0;jj < around.length;jj++){
					if(around[jj].getPixNum() > dotnum && around[jj].getPixNum() > rd[xx][yy].getPixNum() 
					&& (rd[xx][yy].getPixNum() <= dotnum || dotnum < 0)){
						int diff = getColorDifference(around[jj].getColor(),rd[xx][yy].getColor(),true);
						if((diff > 0 || dotnum > 0) && diff < mindiff){
							mindiff = diff;
							mincolor = around[jj].getColor();
						}
					}
				}
				//中間色
				if(mincolor == null){
					for(int jj = 0;jj < around.length;jj++){
						for(int ii = 0;ii < around.length;ii++){
							if(around[ii].getPixNum() >  rd[xx][yy].getPixNum()
									&&  around[jj].getPixNum() >  rd[xx][yy].getPixNum()){
								Color[] ccc = {around[ii].getColor(),around[jj].getColor()};
								Color pc = getAveragedColor(ccc);
								int diff = getColorDifference(rd[xx][yy].getColor(),pc,true);
								if(diff < redundancy){
									int diff1 = getColorDifference(rd[xx][yy].getColor(),around[ii].getColor(),true);
									int diff2 = getColorDifference(rd[xx][yy].getColor(),around[jj].getColor(),true);
									if(diff1 > 0 && diff1 < mindiff){
										mindiff = diff1;
										mincolor = around[ii].getColor();
									}
									if(diff2 > 0 && diff2 < mindiff){
										mindiff = diff2;
										mincolor = around[jj].getColor();
									}
								}
							}
						}
					}
				}
				
				if(mincolor != null){
					rd[xx][yy].setColor(mincolor);
				}
				
			}
			
			
		}
		for(int yy = 0;yy < src.getHeight();yy++){
			int[] colors = new int[src.getWidth()];
			src.getRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			for(int xx = 0;xx < src.getWidth();xx++){
				colors[xx] = rd[xx][yy].getColor().getRGB();
				
			}
			
			src.setRGB(0,yy,src.getWidth(),1,colors,0,src.getWidth());
			
		}
		
		return src;
	}
	
	public static RegionData[] getAdjacentRegionData(RegionData[][] src,int x, int y,int bsize){
		return getAdjacentRegionData(src,x,y,bsize,false);
	}
	public static RegionData[] getAdjacentRegionData(RegionData[][] src,int x, int y,int bsize,boolean smaller){
		int startx = Math.max(x-bsize,0);
		int starty = Math.max(y-bsize,0);
		int endx = Math.min(x+bsize,src.length-1);
		int endy = Math.min(y+bsize,src[0].length-1);
		RegionData ret[];
		
		if(smaller){
			ret = new RegionData[(bsize*2+1)*(bsize*2+1)/2];
			int num = 0;
			
			for(int yy = starty;yy <= endy;yy++){
				for(int xx = startx;xx <= endx;xx++){
					if(yy < y || (yy == y && xx < x)){
						ret[num++] = src[xx][yy];
					}
				}	
			}
		}else{
			
			 ret = new RegionData[(endx-startx+1)*(endy-starty+1)-1];
			int num = 0;
			
			for(int yy = starty;yy <= endy;yy++){
				for(int xx = startx;xx <= endx;xx++){
					if(xx != x || yy != y){
						ret[num++] = src[xx][yy];
					}
				}	
			}
		}
		return ret;
	}
	public static Color getAveragedColor(ArrayList<Color> al){
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		for(int ii = 0;ii < al.size();ii++){
			r += al.get(ii).getRed();
			g += al.get(ii).getGreen();
			b += al.get(ii).getBlue();
			a += al.get(ii).getAlpha();
			
		}
		return new Color(r/al.size(),g/al.size(),b/al.size(),a/al.size());
	}
	public static Color getAveragedColor(Color[] al){
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		for(int ii = 0;ii < al.length;ii++){
			r += al[ii].getRed();
			g += al[ii].getGreen();
			b += al[ii].getBlue();
			a += al[ii].getAlpha();
			
		}
		return new Color(r/al.length,g/al.length,b/al.length,a/al.length);
	}
	public static int[] getColorsAround(BufferedImage bi,int x, int y){
		int startx = Math.max(x-1,0);
		int starty = Math.max(y-1,0);
		int endx = Math.min(x+1,bi.getWidth()-1);
		int endy = Math.min(y+1,bi.getHeight()-1);
		int colors[] = new int[(endx-startx+1)*(endy-starty+1)];
		int ret[] = new int[(endx-startx+1)*(endy-starty+1)-1];
		bi.getRGB(startx,starty,endx-startx+1,endy-starty+1,colors,0,endx-startx+1);
		int width = (endx-startx+1);
		int num = 0;
		for(int yy = starty;yy <= endy;yy++){
			for(int xx = startx;xx <= endx;xx++){
				if(xx != x || yy!= y){
					ret[num++] = colors[xx-startx+(yy-starty)*width];
				}
			}	
		}
		return ret;
	}
	public static BufferedImage contrastPosterization(BufferedImage bi,double value,boolean brightnessChange,int bigregion_mask){
		
		//removeSmallDots(bi,12,2);
		BufferedImage dest = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_ARGB);
		int brightnesscode = 0;
		RegionData[][] rdb = mapRegionData(bi);
		ArrayList<RegionData> alb = rdb[0][0].mainArray;
		if(brightnessChange){
			int wid = bi.getWidth();
			int hei = bi.getHeight();
			ArrayList<RegionData> bigregion = new ArrayList<RegionData>();
			int biggest = 0;
			Color biggestc = null;
			for(int ii = 0;ii < alb.size();ii++){
				RegionData rd = alb.get(ii);
				if(rd.getRect().width/(double)wid > 0.2 && rd.getRect().height/(double)hei > 0.2 && rd.isActive()){
					bigregion.add(rd);
				}
				if(rd.getPixNum() > biggest){
					biggest = rd.getPixNum();
					biggestc = rd.getColor();
				}
			}
			
			float[] hsv = Color.RGBtoHSB(biggestc.getRed(),biggestc.getGreen(),biggestc.getBlue(),null);
			if(hsv[2] < 0.5){
				brightnesscode = 1;
			}else{
				brightnesscode = -1;	
			}
			
		}
		Hashtable<String,Color> testhash = new Hashtable<String,Color>();
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			bi.getRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			for(int xx = 0;xx < bi.getWidth();xx++){
				Color cc = new Color(colors[xx],true);
				if(brightnesscode == 1){
					cc = ColorModifier.changeBrightness(cc,0.3,false);
				}
				if(brightnesscode == -1){
					cc = ColorModifier.changeBrightness(cc,-0.3,false);
				}
				colors[xx] = ColorModifier.changeContrast(cc,value).getRGB();
				if(bigregion_mask > 0){
					if(!rdb[xx][yy].isAntiAliasing() && rdb[xx][yy].getPixNum() > bigregion_mask){
						colors[xx] = Color.red.getRGB();
					}
				}
			}
			dest.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
		}
		//if(true)return dest;
		RegionData[][] rd = mapRegionData(dest);
		ArrayList<RegionData> al = rd[0][0].mainArray;
		HashSet<RegionData> finished = new HashSet<RegionData>();
		for(int ii = 0;ii < al.size();ii++){
			RegionData rdd = al.get(ii);
			if(rdd.isActive() && !finished.contains(rdd)){
				finished.add(rdd);
				Rectangle rec = rdd.getRect();
				Hashtable<Integer,Integer> colhash = new Hashtable<Integer,Integer>();
				for(int yy = rec.y;yy < rec.y+rec.height;yy++){
					for(int xx = rec.x;xx < rec.x+rec.width;xx++){
						if(rd[xx][yy].getRefObj() == rdd){
							int cc =bi.getRGB(xx,yy);
							if(colhash.get(cc) != null){
								colhash.put(cc,colhash.get(cc)+1);
							}else{
								colhash.put(cc,1);
							}
						}
					}	
				}
				
				Enumeration<Integer> e = colhash.keys();
				ArrayList<Integer> maxcol = new ArrayList<Integer>();
				int maxnum = 0;
				while(e.hasMoreElements()){
					int c = e.nextElement();
					if(colhash.get(c) > maxnum){
						maxcol.clear();
						maxcol.add(c);
						maxnum = colhash.get(c);
					}else if(colhash.get(c) == maxnum){
						maxcol.add(c);
					}
				}
				if(maxcol.size() == 1){
					rdd.setColor(new Color(maxcol.get(0),true));
				}else{
					if(maxcol.size() > 0){ //Here is a bug. sometimes maxcol.size() == 0.
						int r = 0;
						int g = 0;
						int b = 0;
						int a = 0;
						for(int kk = 0;kk < maxcol.size();kk++){
							Color c = new Color(maxcol.get(kk),true);
							r += c.getRed();
							g += c.getGreen();
							b += c.getBlue();
							a += c.getAlpha();
						}
						int s = maxcol.size();
						rdd.setColor(new Color(r/s,g/s,b/s,a/s));
					}
				}
			}
		}
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			for(int xx = 0;xx < bi.getWidth();xx++){
				colors[xx] = rd[xx][yy].getColor().getRGB();
				if(bigregion_mask > 0){
					if(!rdb[xx][yy].isAntiAliasing() && rdb[xx][yy].getPixNum() > bigregion_mask){
						colors[xx] = rdb[xx][yy].getColor().getRGB();
						//System.out.println(rdb[xx][yy].getColor());
					}
				}
			}
			
			bi.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			
		}
		return bi;
	}
	public static BufferedImage advancedPosterization(BufferedImage bi,int factnum){
		int colorstep = 256/factnum;
		
		//removeSmallDots(bi,64,2);
		BufferedImage dest = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_ARGB);
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			bi.getRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			for(int xx = 0;xx < bi.getWidth();xx++){
				Color cc = new Color(colors[xx],true);
				int cp[] = {cc.getRed(),cc.getGreen(),cc.getBlue(),cc.getAlpha()};
				for(int ii = 0;ii < 4;ii++){
					cp[ii] = (cp[ii]+colorstep/2)/colorstep*colorstep;
					if(cp[ii] > 255){
						cp[ii] = 255;
					}
				}
				colors[xx] = cp[3]*0x01000000+cp[0]*0x00010000+cp[1]*0x00000100+cp[2];
			}
			dest.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
		}
		RegionData[][] rd = mapRegionData(dest);
		ArrayList<RegionData> al = rd[0][0].mainArray;
		HashSet<RegionData> finished = new HashSet<RegionData>();
		for(int ii = 0;ii < al.size();ii++){
			RegionData rdd = al.get(ii);
			if(rdd.isActive() && !finished.contains(rdd)){
				finished.add(rdd);
				Rectangle rec = rdd.getRect();
				Hashtable<Integer,Integer> colhash = new Hashtable<Integer,Integer>();
				for(int yy = rec.y;yy < rec.y+rec.height;yy++){
					for(int xx = rec.x;xx < rec.x+rec.width;xx++){
						if(rd[xx][yy].getRefObj() == rdd){
							int cc =bi.getRGB(xx,yy);
							if(colhash.get(cc) != null){
								colhash.put(cc,colhash.get(cc)+1);
							}else{
								colhash.put(cc,1);
							}
				}else{
						}
					}	
				}
				
				Enumeration<Integer> e = colhash.keys();
				ArrayList<Integer> maxcol = new ArrayList<Integer>();
				int maxnum = 0;
				while(e.hasMoreElements()){
					int c = e.nextElement();
					if(colhash.get(c) > maxnum){
						maxcol.clear();
						maxcol.add(c);
						maxnum = colhash.get(c);
					}else if(colhash.get(c) == maxnum){
						maxcol.add(c);
					}
				}
				if(maxcol.size() == 1){
					rdd.setColor(new Color(maxcol.get(0),true));
					
				}else if(maxcol.size() > 0){ //Here is a bug. sometimes maxcol.size() == 0.
					int r = 0;
					int g = 0;
					int b = 0;
					int a = 0;
					for(int kk = 0;kk < maxcol.size();kk++){
						Color c = new Color(maxcol.get(kk),true);
						r += c.getRed();
						g += c.getGreen();
						b += c.getBlue();
						a += c.getAlpha();
					}
					int s = maxcol.size();
					rdd.setColor(new Color(r/s,g/s,b/s,a/s));
				}
			}
		}
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			for(int xx = 0;xx < bi.getWidth();xx++){
				colors[xx] = rd[xx][yy].getColor().getRGB();
				
			}
			
			bi.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			
		}
		return bi;
	}
	
	/**
	 * threshold 未満のアルファ値の場合アルファをゼロに、以上の場合アルファは 255 にする
	 * @param bi
	 * @param threshold
	 * @return 
	 */
	public static BufferedImage binaryAlpha(BufferedImage bi,int threshold){
		
		int[] colors = new int[bi.getWidth()*bi.getHeight()];
		bi.getRGB(0,0,bi.getWidth(),bi.getHeight(),colors,0,bi.getWidth());
		
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			for(int xx = 0;xx < bi.getWidth();xx++){
				int cc = colors[xx+yy*bi.getWidth()];
				int r = cc >> 16 & 0xFF;
				int g = cc >> 8 & 0xFF;
				int b = cc & 0xFF;
				int a = cc >> 24 & 0xFF;;

				if(a < threshold){
					Color co = new Color(r,g,b,0);
					colors[xx+yy*bi.getWidth()] = co.getRGB();
				}else{
					Color co = new Color(r,g,b,255);
					colors[xx+yy*bi.getWidth()] = co.getRGB();
				}
			}
		}
		
		bi.setRGB(0,0,bi.getWidth(),bi.getHeight(),colors,0,bi.getWidth());
		return bi;
	}
	
	public static BufferedImage selectiveGaussianBlur(BufferedImage bi,int size,int range){
		
		int[] colors = new int[bi.getWidth()*bi.getHeight()];
		bi.getRGB(0,0,bi.getWidth(),bi.getHeight(),colors,0,bi.getWidth());
		
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			for(int xx = 0;xx < bi.getWidth();xx++){
				int cc = colors[xx+yy*bi.getWidth()];
				double effect = 0;
				double r = 0;
				double g = 0;
				double b = 0;
				double a = 0;
				boolean flag = true;
				for(int px = xx -size;px < xx+size+1;px++){
					if(px*(bi.getWidth()-1-px) < 0){
						continue;
					}
					for(int py = yy -size;py < yy+size+1;py++){
						if(py*(bi.getHeight()-1-py) < 0){
							continue;
						}
						int pc = colors[px+py*bi.getWidth()];
						if(pc != cc){
							flag = false;
							break;
						}
					}
				}
				if(flag){
					continue;
				}
				for(int px = xx -size;px < xx+size+1;px++){
					if(px*(bi.getWidth()-1-px) < 0){
						continue;
					}
					for(int py = yy -size;py < yy+size+1;py++){
						if(py*(bi.getHeight()-1-py) < 0){
							continue;
						}
						int pc = colors[px+py*bi.getWidth()];
						int cdist = getColorDifference(cc,pc,true);
						double ef = 0;
						if(pc == cc){
							 ef = 1/(Math.PI*32);
						}else{
							ef = gaussianEffect_Fast(Math.sqrt((xx-px)*(xx-px)+(yy-py)*(yy-py)));
						}
						if(cdist < range){
							Color ppc = new Color(pc,true);
							r += ppc.getRed()*ef;
							g += ppc.getGreen()*ef;
							b += ppc.getBlue()*ef;
							a += ppc.getAlpha()*ef;
							effect += ef;
						}
					}
				}
				
				int pr = Math.min(Math.max((int)(r/effect+0.5),0),255);
				int pg = Math.min(Math.max((int)(g/effect+0.5),0),255);
				int pb = Math.min(Math.max((int)(b/effect+0.5),0),255);
				int pa = Math.min(Math.max((int)(a/effect+0.5),0),255);
				Color co = new Color(pr,pg,pb,pa);
				
				colors[xx+yy*bi.getWidth()] = co.getRGB();
			}
		}
		
		bi.setRGB(0,0,bi.getWidth(),bi.getHeight(),colors,0,bi.getWidth());
		return bi;
	}
	
	public static double gaussianEffect(double dist,double sig){
		return 1/(Math.PI*sig*sig*2)*Math.exp(-dist*dist/(2*sig*sig));
	}
	public static double gaussianEffect_Fast(double dist){
		return 1/(Math.PI*32)*Math.exp(-dist*dist/(32));
	}
	
	
	public static BufferedImage normalPosterization(BufferedImage bi,int factnum){
		int colorstep = 256/factnum;
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			bi.getRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			for(int xx = 0;xx < bi.getWidth();xx++){
				Color cc = new Color(colors[xx],true);
				int cp[] = {cc.getRed(),cc.getGreen(),cc.getBlue(),cc.getAlpha()};
				for(int ii = 0;ii < 4;ii++){
					cp[ii] = (cp[ii]+colorstep/2)/colorstep*colorstep;
					if(cp[ii] > 255){
						cp[ii] = 255;
					}
				}
				colors[xx] = cp[3]*0x01000000+cp[0]*0x00010000+cp[1]*0x00000100+cp[2];
			}
			bi.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
		}
		
		return bi;
	}
	
	/**
	 * ColorNum の数に色をそろえる
	 */
	public static BufferedImage hardPosterization(BufferedImage bi,int colornum,int mindiff){
		HashMap<Color,Integer> hm = getPixelNum_(bi);
		ArrayList<ColorNum> al = new ArrayList<>();
		for(Color c:hm.keySet()){
			al.add(new ColorNum(c,hm.get(c)));
		}
		ArrayList<Color> cal = new ArrayList<>();
		if(colornum > 200){
			for(int r = 0;r <= 256;r+=128){
				for(int g = 0;g <= 256;g+=128){
					for(int b = 0;b <= 256;b+=128){
						cal.add(new Color(
						Math.min(r,255),
						Math.min(g,255),
						Math.min(b,255)
						)
						);
					}
				}
			}
		}
		
		Collections.sort(al,new ColorNumComparator());
		Collections.reverse(al);
		for(ColorNum c:al){
			boolean flag = true;
			for(Color cc:cal){
				int dff = getColorDifference(c.color,cc,true);
				if(dff < Math.max(mindiff,10*(256/colornum))){
					flag = false;
					break;
				}
			}
			if(flag){
				cal.add(c.color);
				if(cal.size() >= colornum){
					break;
				}
			}
		}
		
		for(ColorNum c:al){
			int nearest = 0;
			int diff = getColorDifference(c.color,cal.get(0),true);
			for(int ii = 1;ii < cal.size();ii++ ){
				Color cc = cal.get(ii);
				int pdiff = getColorDifference(c.color,cc,true);
				if(pdiff < diff){
					nearest = ii;
					diff = pdiff;
				}
			}
			hm.put(c.color,nearest);
		}
		
		
		for(int yy = 0;yy < bi.getHeight();yy++){
			int[] colors = new int[bi.getWidth()];
			bi.getRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
			for(int xx = 0;xx < bi.getWidth();xx++){
				Color cc = new Color(colors[xx],true);
				if(hm.get(cc) == null){
					throw new RuntimeException("Error in application");
				}
				colors[xx] = cal.get(hm.get(cc)).getRGB();
			}
			bi.setRGB(0,yy,bi.getWidth(),1,colors,0,bi.getWidth());
		}
		
		return bi;
	}
	
	public static int getColorDifference(Color c1,Color c2,boolean alphaflag){
		int ret = 0;
		int r1 = c1.getRed();
		int r2 = c2.getRed();
		int g1 = c1.getGreen();
		int g2 = c2.getGreen();
		int b1 = c1.getBlue();
		int b2 = c2.getBlue();
		
		int a1 = c1.getAlpha();
		int a2 = c2.getAlpha();
		
		ret = Math.max(Math.abs(r1-r2),ret);
		ret = Math.max(Math.abs(g1-g2),ret);
		ret = Math.max(Math.abs(b1-b2),ret);
		
		if(alphaflag){
			ret += Math.abs(a1-a2);
			if(Math.max(a1, a2) < 128){
				ret = (int)(ret*(Math.max(a1, a2)/128.0));
			}
		}
		//System.out.println(ret+";"+getColorDifference(c1.getRGB(),c2.getRGB(),alphaflag));
		return ret;
	}
	public static int getColorDifference(int cc1,int cc2,boolean alphaflag){
		int ret = 0;
		
		ret = Math.max(Math.abs((cc1 >> 16 & 0xFF)-(cc2 >> 16 & 0xFF)),ret);
		ret = Math.max(Math.abs((cc1 >> 8 & 0xFF)-(cc2 >> 8 & 0xFF)),ret);
		ret = Math.max(Math.abs((cc1 & 0xFF)-(cc2 & 0x0FF)),ret);
		if(alphaflag){
			int a1 = (cc1 >> 24 & 0xFF);
			int a2 = (cc2 >> 24 & 0xFF);
			
			ret += Math.abs((cc1 >> 24 & 0xFF)-(cc2 >> 24 & 0xFF));
		}
		return ret;
	}
	public static int getColorDifference_HSV(int cc1,int cc2,boolean alphaflag){
		double ret = 0;
		float[] f1 = new float[3];
		float[] f2 = new float[3];
		if(alphaflag){
			ret += Math.abs((cc1 & 0xFF000000)-(cc2 & 0xFF000000))/16777216;
		}
		getHSB(cc1,f1);
		getHSB(cc2,f2);
		
		ret += Math.abs(f1[1]-f2[1])*256;
		ret += Math.abs(f1[2]-f2[2])*256;
		double ps = Math.abs(f1[0]-f2[0]);
		if(ps > 0.5){
			ps -= 0.5;
		}
		ret += 255*ps*(f1[1]+f2[1])/2/(Math.abs(f1[2]-0.5)+Math.abs(f2[2]-0.5)+1);
		ret += Math.abs(f1[1]-f2[1])*255;
		ret += Math.abs(f1[2]-f2[2])*255;
		return (int)ret;
	}
	public static float[] getHSB(int c,float f[]){
		int r =  (c & 0x00FF0000)/65536;
		int g = (c & 0x0000FF00)/256;
		int b = (c & 0x000000FF);
		return Color.RGBtoHSB(r,g,b,f);
	}
	
	public static int[] isBetween(int c1,int c2,int c3,int redundancy,boolean alphaflag){//0:true 1 or false 0; 1:closer color c1.getRGB() or c3.getRGB();
		int ret[] = new int[2];
		int a1 = (c1 >> 24 & 0xFF);
		int a2 = (c2 >> 24 & 0xFF);
		int a3 = (c3 >> 24 & 0xFF);
		int cp1[] = {(c1 & 0x00FF0000)/0x00010000,(c1 & 0x0000FF00)/0x00000100,(c1 & 0x000000FF)};
		int cp2[] = {(c2 & 0x00FF0000)/0x00010000,(c2 & 0x0000FF00)/0x00000100,(c2 & 0x000000FF)};
		int cp3[] = {(c3 & 0x00FF0000)/0x00010000,(c3 & 0x0000FF00)/0x00000100,(c3 & 0x000000FF)};
		
		if(a1*a3 == 0 && a1 != a3 && alphaflag){
			if((a1-a2)*(a3-a2) <= 0){
				ret[0] = 1;
				if(Math.abs(a1-a2) > Math.abs(a3-a2)){
					ret[1] = c3;
				}else{
					ret[1] = c1;
				}
			}else{
				ret[0] = 0;
				ret[1] = 0;
			}
		}else{
			boolean isin = true;
			
			
			for(int ii = 0;ii < 3;ii++){
				int maxa = Math.max(cp1[ii],cp3[ii])+redundancy;
				int mina = Math.min(cp1[ii],cp3[ii])-redundancy;
				if((maxa-cp2[ii])*(mina-cp2[ii]) > 0){
					isin = false;
				}
			}
			if(isin){
					ret[0] = 1;
				if(getColorDifference(c1,c2,alphaflag) <= getColorDifference(c3,c2,alphaflag)){
					ret[1] = c1;
				}else{
					ret[1] = c3;
					
				}
			}else{
				ret[0] = 0;
				ret[1] = 0;
			}
		}
		
		
		return ret;
	}
	
	
	public static void main(String[] args){
		try{
		BufferedImage img = ImageIO.read(new File("C:\\portable_programs\\azpainter2\\plus_pictures\\00000452.jpg"));
		//getPixelNum(img);
		//ImageIOWrapper.saveToFile(hardPosterization(img,8),"C:\\portable_programs\\azpainter2\\plus_pictures\\00000452_8.png");
		//ImageIOWrapper.saveToFile(hardPosterization(img,16),"C:\\portable_programs\\azpainter2\\plus_pictures\\00000452_16.png");
		//ImageIOWrapper.saveToFile(hardPosterization(img,64),"C:\\portable_programs\\azpainter2\\plus_pictures\\00000452_64.png");
		ImageIOWrapper.saveToFile(hardPosterization(img,64,5),"C:\\portable_programs\\azpainter2\\plus_pictures\\00000452_32.png");
		//ImageIOWrapper.saveToFile(advancedPosterization(img,6),"posterized1b.png");
		//ImageIOWrapper.saveToFile(removeAntiAliasing(img,3),"posterized4.png");
		//ImageIOWrapper.saveToFile(removeSmallDots(img,64,3),"posterized5.png");
		//ImageIOWrapper.saveToFile(removeSmallDots(img,512,1),"posterized6.png");
		//ImageIOWrapper.saveToFile(oneDotCorrection(normalPosterization(img,8),1),"posterized2.png");
		//removeAntiAliasing(img,1,3);
		
		//for(int ii = 0;ii < 100;ii++){
		//	Color cc = new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
		//	float ff[] = new float[3];
		//	getHSB(cc.getRGB(),ff);
		//	System.out.println(ff[0]);
		//}
		
		
		//System.out.println(gaussianEffect(1,1));
		}catch(Exception exx){
			exx.printStackTrace();
		}
		
	}
	
	
	
}

class ColorNumComparator implements Comparator<ColorNum>{
	@SuppressWarnings("unchecked")
	public int compare(ColorNum arg1, ColorNum arg2){
		
		if(arg1.count < arg2.count ){
			return -1;
		}
		if(arg1.count == arg2.count ){
			return 0;
		}
			return 1;
	}
	
}


class ColorNum{
	Color color;
	int count;
	ColorNum(Color cc,int p){
		color = cc;
		count = p;
	}
	
}


class RegionData{
	ArrayList<RegionData> mainArray = null;
	RegionData ref = this;
	int id = -1;
	Color color;
	int pixNum = -1;
	Rectangle rect = null;
	RegionData(Color c,int x,int y,ArrayList<RegionData> al){
		mainArray = al;
		color = c;
		ref = this;
		id = mainArray.size();
		mainArray.add(this);
		pixNum = 1;
		rect = new Rectangle(x,y,1,1);
		
	}
	public void addPoint(int x,int y){
		if(ref == this){
			rect.add(x,y);
		}else{
			ref.addPoint(x,y);
		}
	}
	public void increment(){
		getRef().pixNum++;
	}
	public void decrement(){
		getRef().pixNum--;
	}
	public int getLongerSideLength(){
		Rectangle r = getRect();
		if(r.height > r.width){
			return r.height;
		}else{
			return r.width;
		}
	}
	public void mergeWith(RegionData target){
		if(getRef() == target.getRef()){
			return;
		}
		
		ref.pixNum += target.getPixNum();
		ref.getRect().add(target.getRect());
		target.getRef().setRef(ref);
		target.setRef(ref);
		if(!target.color.equals(color)){
			System.err.println("Warning: Color of target region is different from that of this region.");
		}
	}
	public void setRef(RegionData r){
		ref = r;
		if(this != ref){
			rect = null;
		}
	}
	public RegionData getRefObj(){
		return ref;
	}
	public RegionData getRef(){
		if(ref == this){
			return this;
		}else{
			if(ref.ref == ref){
				return ref;
			}
			ref = ref.getRef();
			return ref;
		}
	}
	public boolean isActive(){
		return ref == this;
	}
	public Rectangle getRect(){
		if(this != ref){
			return ref.getRect();
		}else{
			return rect;
		}
	}
	public boolean isAntiAliasing(){
		return getLongerSideLength()+3 > getPixNum();
	}
	public int getID(){
		return id;
	}
	public boolean isSameColor(Color c){
		return getColor().equals(c);
	}
	public boolean isSameColor(int i){
		return getColor().getRGB() == i;
	}
	public void setColor(Color c){
		getRef().color = c;
	}
	public int getPixNum(){
		return getRef().pixNum;
	}
	public Color getColor(){
		if(this != ref){
			return ref.getColor();
		}else{
			return color;
		}
	}
}