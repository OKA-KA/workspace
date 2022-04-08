package p2vj;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Hashtable;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.*;


public class SVGParser{
	
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static final int ST_M = 0;
	public static final int ST_Z = 1;
	public static final int ST_L = 2;
	public static final int ST_H = 3;
	public static final int ST_V = 4;
	public static final int ST_C = 5;
	public static final int ST_S = 6;
	public static final int ST_Q = 7;
	public static final int ST_T = 8;
	public static final int ST_A = 9;
	public static final String ST_INDEX = "MZLHVCSQTA";
	
	public static boolean loadFromSVG(String svgstr,ArrayList<PPLayer> al){
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			StringReader strread = new StringReader(svgstr);
			Document doc = builder.parse(new InputSource(strread));
			Element root = doc.getDocumentElement();
			al.clear();
			PPLayer pl = new PPLayer();
			al.add(pl);
			Hashtable<String,Node> gradationHash = new Hashtable<String,Node>();
			Hashtable<String,Node> textHash = new Hashtable<String,Node>();
			checkChildNodes(root,0,al,gradationHash,textHash);
			
			
			
		}catch(Exception exx){
			exx.printStackTrace();
			System.err.println("\n\nXML format Error?");
			return false;
		}
		return true;
	}
	
	
	public static void saveToFile(String filename,PolycoCanvas pc){
		
		StringBuffer sb = makeSVGData(pc);
		try{
		FileWriter out = new FileWriter(filename);
		//new SimpleTextFrame("test.svg",sb.toString());
		out.write(sb.toString());
		out.close();
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	public static void printInStdout(PolycoCanvas pc){
		
		StringBuffer sb = makeSVGData(pc);
		try{
			System.out.print(sb.toString());
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	
	public static MultipleGradientPaint getGradationFromDef(Node mynode,PolycoItem pi){
		NamedNodeMap nnm = mynode.getAttributes();//defの中のGradientNodeを渡してください。MultipleGradientPaintを返します。
		NodeList tnl = mynode.getChildNodes();
		Color cc[] = new Color[tnl.getLength()];
		float ff[] = new float[tnl.getLength()];
		int stoplength = getColorFraction(mynode,cc,ff);
		Rectangle2D rect = pi.outline.getBounds2D();
		double x = rect.getX();
		double y = rect.getY();
		double w = rect.getWidth();
		double h = rect.getHeight();
		
		Color color[] = new Color[stoplength];
		float fraction[] = new float[stoplength];
		for(int ii = 0;ii < stoplength;ii++){
			color[ii] = cc[ii];
			fraction[ii] = ff[ii];
		}
		MultipleGradientPaint.CycleMethod cycle = MultipleGradientPaint.CycleMethod.REFLECT;
		if(nnm.getNamedItem("spreadmethod") != null){
			if(nnm.getNamedItem("spreadmethod").getNodeValue().toUpperCase().compareTo("PAD") == 0){
				cycle = MultipleGradientPaint.CycleMethod.NO_CYCLE;
			}else if(nnm.getNamedItem("spreadmethod").getNodeValue().toUpperCase().compareTo("REPEAT") == 0){
				cycle = MultipleGradientPaint.CycleMethod.REPEAT;
			}
		}
		
		
		if(mynode.getNodeName().toUpperCase().indexOf("LINEAR") > -1){
			double x1 = 0.0;
			double x2 =1.0;
			double y1 = 0.0;
			double y2 = 0.0;
			if(nnm.getNamedItem("x1") != null){
				x1 =  Double.parseDouble(nnm.getNamedItem("x1").getNodeValue());
			}
			if(nnm.getNamedItem("x2") != null){
				x2 =  Double.parseDouble(nnm.getNamedItem("x2").getNodeValue());
			}
			if(nnm.getNamedItem("y1") != null){
				y1 =  Double.parseDouble(nnm.getNamedItem("y1").getNodeValue());
			}
			if(nnm.getNamedItem("y2") != null){
				y2 =  Double.parseDouble(nnm.getNamedItem("y2").getNodeValue());
			}
			
			//return new LinearGradientPaint((float)(x+x1*w),(float)(y+y1*h),(float)(x+x2*w),(float)(y+y2*h),fraction,color,cycle);
			return new LinearGradientPaint((float)(x+x1),(float)(y+y1),(float)(x+x2),(float)(y+y2),fraction,color,cycle);
			
		}else if(mynode.getNodeName().toUpperCase().indexOf("RADIAL") > -1){
			double cx = 0.0;
			double cy = 0.0;
			double fx = 0.0;
			double fy = 0.0;
			double r = 1.0;
			
			if(nnm.getNamedItem("cx") != null){
				cx =  Double.parseDouble(nnm.getNamedItem("cx").getNodeValue());
			}
			if(nnm.getNamedItem("fx") != null){
				fx =  Double.parseDouble(nnm.getNamedItem("fx").getNodeValue());
			}
			if(nnm.getNamedItem("cy") != null){
				cy =  Double.parseDouble(nnm.getNamedItem("cy").getNodeValue());
			}
			if(nnm.getNamedItem("fy") != null){
				fy =  Double.parseDouble(nnm.getNamedItem("fy").getNodeValue());
			}
			if(nnm.getNamedItem("r") != null){
				r =  Double.parseDouble(nnm.getNamedItem("r").getNodeValue());
			}
			
			//return new RadialGradientPaint((float)(x+cx*w),(float)(y+cy*h),(float)(x+fx*w),(float)(y+fy*h),(float)(r*Math.max(w,h)),fraction,color,cycle);
			return new RadialGradientPaint((float)(x+cx),(float)(y+cy),(float)(x+fx),(float)(y+fy),(float)(r),fraction,color,cycle);
			
		}
		return null;
		
	}
	public static PolycoText getTextInfoFromDef(Node defnode){
		//defNodeの中のPolycoTextを渡してください。PolycoTextを返します。
		PolycoText pt = new PolycoText();
		NamedNodeMap nnm = defnode.getAttributes();
		NodeList alllist = defnode.getChildNodes();
		StringBuffer sb = new StringBuffer();
		for(int ii = 0;ii < alllist.getLength();ii++){
			Node nn = alllist.item(ii);
			if(nn.getNodeType() == Node.TEXT_NODE){
				sb.append(nn.getNodeValue());
			}
		}
		String data = sb.toString();
		data = data.replaceAll("#nbsp;"," ");
		data = data.replaceAll("#tab;","\t");
		data = data.replaceAll("#br;","\n");
		data = data.replaceAll("#lt;","<");
		data = data.replaceAll("#gt;",">");
		data = data.replaceAll("#sp;","#");
		
		double fontsize = 12;
		if(nnm.getNamedItem("size").getNodeValue() != null){
			fontsize = Double.parseDouble(nnm.getNamedItem("size").getNodeValue());
		}
		String fontname ="Dialog";
		if(nnm.getNamedItem("font") != null){
			fontname = nnm.getNamedItem("font").getNodeValue();
		}
		int style = Font.PLAIN;
		if(nnm.getNamedItem("style") != null){
			String st = nnm.getNamedItem("style").getNodeValue().toUpperCase();
			if(st.compareTo("PLAIN") == 0){
				
			}else if(st.compareTo("BOLD") == 0){
				style = Font.BOLD;
			}else if(st.compareTo("ITALIC") == 0){
				style = Font.ITALIC;
			}else if(st.compareTo("BOLDITALIC") == 0){
				style = Font.BOLD+Font.ITALIC;
			}
		}
		pt.direction = PolycoText.HOLIZONTAL;
		if(nnm.getNamedItem("vertical") != null){
			if(nnm.getNamedItem("vertical").getNodeValue().toUpperCase().compareTo("TRUE") ==0){
				pt.direction = PolycoText.VERTICAL;
			}
		}
		double bx = 0;
		double by = 0;
		try{
			if(nnm.getNamedItem("bx") != null){
				bx = Double.parseDouble(nnm.getNamedItem("bx").getNodeValue());
			}
			if(nnm.getNamedItem("by") != null){
				by = Double.parseDouble(nnm.getNamedItem("by").getNodeValue());
			}
		}catch(Exception exx){
			exx.printStackTrace();
		}
		pt.basePoint.set(bx,by);
		if(nnm.getNamedItem("rotate90") != null){
			if(nnm.getNamedItem("rotate90").getNodeValue().toUpperCase().compareTo("TRUE") == 0){
				pt.rotate90 = true;
			}
		}
		pt.setFont(new Font(fontname,style,(int)fontsize));
		pt.setText(data);
		return pt;
	}
	public static PolycoImage getImageInfoFromDef(Node defnode){
		//defNodeの中のPolycoImageを渡してください。PolycoImageを返します。
		PolycoImage pi = new PolycoImage();
		int ww = 0;
		int hh = 0;
		NamedNodeMap nnm = defnode.getAttributes();
		NodeList alllist = defnode.getChildNodes();
		try{
			if(nnm.getNamedItem("width") != null){
				ww = Integer.parseInt(nnm.getNamedItem("width").getNodeValue());
			}
			if(nnm.getNamedItem("height") != null){
				hh = Integer.parseInt(nnm.getNamedItem("height").getNodeValue());
			}
		}catch(Exception exx){
			exx.printStackTrace();
			return null;
		}
		pi.createImage(ww,hh);
		
		String method = "zip";
		
		if(nnm.getNamedItem("method") != null){
			method = nnm.getNamedItem("method").getNodeValue();
		}
		StringBuffer sb = new StringBuffer();
		for(int ii = 0;ii < alllist.getLength();ii++){
			Node nn = alllist.item(ii);
			if(nn.getNodeType() == Node.TEXT_NODE){
				sb.append(nn.getNodeValue());
			}
		}
		String data = sb.toString();
		sb.delete(0,sb.length());
		data = data.replaceAll("[^0-9^a-z^A-Z]","");
		byte[] bt = new byte[data.length()/2];
		try{
			for(int ii = 0;ii < data.length();ii+=2){
				String dstr =data.substring(ii,ii+2);
				int chk = Integer.parseInt(data.substring(ii,ii+1),16)*16+Integer.parseInt(data.substring(ii+1,ii+2),16);
				
				if(chk > 128){
					bt[ii/2] = (byte)(chk-256);
				}else{
					bt[ii/2] = (byte)(chk);
				}
			}
		}catch(Exception exx){
			exx.printStackTrace();
		}
		byte[] rgbdat = null;
		if(method.compareTo("gzip") == 0){
			rgbdat = ByteZipper.getInflatedByteArray_G(bt);
		}else{
			rgbdat = ByteZipper.getInflatedByteArray(bt);
		
		}
		for(int ii = 0;ii < hh;ii++){
			for(int jj = 0;jj < ww;jj++){
				int a = (int)rgbdat[ii*ww*4+jj*4];
				int r = (int)rgbdat[ii*ww*4+jj*4+1];
				int g = (int)rgbdat[ii*ww*4+jj*4+2];
				int b = (int)rgbdat[ii*ww*4+jj*4+3];
				if(a < 0)a+=256;
				if(r < 0)r+=256;
				if(g < 0)g+=256;
				if(b < 0)b+=256;
				pi.dot(jj,ii,a*16777216+r*65536
				+g*256+b);
			}
		}
		
		
		double bx = 0;
		double by = 0;
		try{
			if(nnm.getNamedItem("bx") != null){
				bx = Double.parseDouble(nnm.getNamedItem("bx").getNodeValue());
			}
			if(nnm.getNamedItem("by") != null){
				by = Double.parseDouble(nnm.getNamedItem("by").getNodeValue());
			}
		}catch(Exception exx){
			bx = 0;
			by = 0;
			exx.printStackTrace();
		}
		pi.basePoint.set(bx,by);
		pi.adjustZoom();
		return pi;
	}
	public static void setStyles(String str,PolycoItem mypg){//style情報からPolycoItemに適用します。
		Hashtable<String,String> stylehash = new Hashtable<String,String>();
		String sarray[] = str.split(";");
		
		
		for(int ii = 0;ii < sarray.length;ii++){
			if(sarray[ii].length() > 3 && sarray[ii].indexOf(":") > -1){
				Pattern spp = Pattern.compile("[ 　\r\n]+");
				Matcher mm = spp.matcher(sarray[ii]);
				String dat = mm.replaceAll(" ");
				String data[] = dat.split(":");
				if(data.length > 1){
					mm = spp.matcher(data[0]);
					data[0] = mm.replaceAll("");
					stylehash.put(data[0].toUpperCase(),data[1].toUpperCase());
				}
			}
		}
		String ss;
		//----------------------------------------------------------------------------Color
		int fillcolor[] = new int[3];
		fillcolor[0] = mypg.fillColor.getRed();
		fillcolor[1] = mypg.fillColor.getGreen();
		fillcolor[2] = mypg.fillColor.getBlue();
		int fillalpha = 255;
		
		int drawcolor[] = new int[3];
		drawcolor[0] = mypg.drawColor.getRed();
		drawcolor[1] = mypg.drawColor.getGreen();
		drawcolor[2] = mypg.drawColor.getBlue();
		int drawalpha = 255;
		
		if((ss = stylehash.get("FILL")) != null){
			if(ss.indexOf("NONE") > -1){
				mypg.fillFlag = false;
			}else{
				Pattern fc = Pattern.compile("[0-9.%]+");
				Matcher mm = fc.matcher(ss);
				double cc[] = new double[3];
				for(int ii = 0;ii < 3;ii++){
					mm.find();
					if(mm.group() != null){
						fillcolor[ii] = parseTo255(mm.group());
					}
					
				}
				mypg.fillFlag = true;
			}
		}
		if((ss = stylehash.get("STROKE")) != null){
			if(ss.indexOf("NONE") > -1){
				mypg.drawFlag = false;
			}else{
				Pattern fc = Pattern.compile("[0-9.%]+");
				Matcher mm = fc.matcher(ss);
				double cc[] = new double[3];
				for(int ii = 0;ii < 3;ii++){
					mm.find();
					if(mm.group() != null){
						drawcolor[ii] = parseTo255(mm.group());
					}
				}
				mypg.drawFlag = true;
			}
		}
		if((ss = stylehash.get("FILL-OPACITY")) != null){
			Pattern fc = Pattern.compile("[0-9.%]+");
			Matcher mm = fc.matcher(ss);
			try{
				if(mm.find()){
					fillalpha = parseTo255(mm.group());
				}
			}catch(Exception exx){
				exx.printStackTrace();
				fillalpha = 255;
			}
		}
		if((ss = stylehash.get("STROKE-OPACITY")) != null){
			Pattern fc = Pattern.compile("[0-9.%]+");
			Matcher mm = fc.matcher(ss);
			try{
				if(mm.find()){
					drawalpha = parseTo255(mm.group());
				}
			}catch(Exception exx){
				exx.printStackTrace();
				drawalpha = 255;
			}
		}
		
		if((ss = stylehash.get("FILL-RULE")) != null && mypg.getClass() == PathGroup.class){
			Pattern fc = Pattern.compile("[a-zA-Z]+");
			Matcher mm = fc.matcher(ss);
			
			try{
				if(mm.find()){
					String gk = mm.group().toUpperCase();
					if(gk.compareTo("EVENODD") == 0){
						mypg.windingRule = GeneralPath.WIND_EVEN_ODD;
					}else if(gk.compareTo("NONZERO") == 0){
						mypg.windingRule = GeneralPath.WIND_NON_ZERO;
					}
				}
			}catch(Exception exx){
				exx.printStackTrace();
			}
		}
		
		mypg.drawColor = new Color(drawcolor[0],drawcolor[1],drawcolor[2],drawalpha);
		mypg.fillColor = new Color(fillcolor[0],fillcolor[1],fillcolor[2],fillalpha);
		//-------------------------------------------------------------------------------------stroke style
		
		float s_w = mypg.trueStroke.getLineWidth();
		int s_j = mypg.trueStroke.getLineJoin();
		int s_c = mypg.trueStroke.getEndCap();
		float s_m = mypg.trueStroke.getMiterLimit();
		
		if((ss = stylehash.get("STROKE-WIDTH")) != null){
			Pattern fc = Pattern.compile("[0-9.]+");
			Matcher mm = fc.matcher(ss);
			try{
				if(mm.find()){
					s_w = (float)Double.parseDouble(mm.group());
				}
			}catch(Exception exx){
				exx.printStackTrace();
				s_w = 1.0f;
			}
		}
		
		if((ss = stylehash.get("STROKE-LINECAP")) != null){
			Pattern fc = Pattern.compile("[a-zA-Z]+");
			Matcher mm = fc.matcher(ss);
			
			try{
				if(mm.find()){
					String gk = mm.group().toUpperCase();
					if(gk.compareTo("BUTT") == 0){
						s_c = BasicStroke.CAP_BUTT;
					}else if(gk.compareTo("ROUND") == 0){
						s_c = BasicStroke.CAP_ROUND;
						
					}else if(gk.compareTo("SQUARE") == 0){
						s_c = BasicStroke.CAP_SQUARE;
					}
				}
			}catch(Exception exx){
				exx.printStackTrace();
				s_c = BasicStroke.CAP_BUTT;
			}
		}
		if((ss = stylehash.get("STROKE-LINEJOIN")) != null){
			Pattern fc = Pattern.compile("[a-zA-Z]+");
			Matcher mm = fc.matcher(ss);
			
			try{
				if(mm.find()){
					String gk = mm.group().toUpperCase();
					if(gk.compareTo("MITER") == 0){
						s_j = BasicStroke.JOIN_MITER;
					}else if(gk.compareTo("ROUND") == 0){
						s_j = BasicStroke.JOIN_ROUND;
						
					}else if(gk.compareTo("BEVEL") == 0){
						s_j = BasicStroke.JOIN_BEVEL;
					}
				}
			}catch(Exception exx){
				exx.printStackTrace();
				s_j = BasicStroke.CAP_BUTT;
			}
		}
		if((ss = stylehash.get("STROKE-MITERLIMIT")) != null){
			Pattern fc = Pattern.compile("[0-9.]+");
			Matcher mm = fc.matcher(ss);
			try{
				if(mm.find()){
					s_m = (float)Double.parseDouble(mm.group());
				}
			}catch(Exception exx){
				exx.printStackTrace();
				s_m = 1.0f;
			}
		}
		mypg.setStroke(new BasicStroke(s_w,s_c,s_j,s_m));
	}
	
	
	public static boolean checkChildNodes(Node mynode,int depth,ArrayList<PPLayer> al,Hashtable<String,Node> ghash,Hashtable<String,Node> thash){
		
		if(mynode.getNodeType() == Node.ELEMENT_NODE){
		}else if(mynode.getNodeType() == Node.TEXT_NODE){
		}else {
		}
		
		PPLayer nowlayer = null;
		if(al.size() > 0){
			nowlayer = al.get(al.size()-1);
		}else{
			nowlayer = new PPLayer();
			al.add(nowlayer);
		}
		PolycoItem adding = null;
		if(mynode.getNodeName().toUpperCase().compareTo("RECT") == 0){
			
			adding = loadRect(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("CIRCLE") == 0){
			adding = loadCircle(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("ELLIPSE") == 0){
			adding = loadEllipse(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("LINE") == 0){
			adding = loadLine(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("POLYLINE") == 0){
			adding = loadPolyLine(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("POLYGON") == 0){
			adding = loadPolygon(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("PATH") == 0){
			adding = loadPath(mynode);
		}else if(mynode.getNodeName().toUpperCase().compareTo("G") == 0){
			NodeList tnl = mynode.getChildNodes();
			if(nowlayer.getList().size() > 0){
				al.add(new PPLayer());
			}
		}else if(mynode.getNodeName().toUpperCase().compareTo("DEFS") == 0){
			NodeList tnl = mynode.getChildNodes();
			for(int ii = 0;ii < tnl.getLength();ii++){
				Node cc = tnl.item(ii);
				NamedNodeMap nnc = cc.getAttributes();
			
				if(cc.getNodeName().toUpperCase().indexOf("GRADIENT") > -1){
					if(nnc.getNamedItem("id") != null){
						ghash.put(nnc.getNamedItem("id").getNodeValue(),cc);
					}
				}else if(cc.getNodeName().toUpperCase().indexOf("POLYCOTEXT") > -1){
					if(nnc.getNamedItem("id") != null){
						thash.put(nnc.getNamedItem("id").getNodeValue(),cc);
					}
				}else if(cc.getNodeName().toUpperCase().indexOf("POLYCOIMAGE") > -1){
					if(nnc.getNamedItem("id") != null){
						thash.put(nnc.getNamedItem("id").getNodeValue(),cc);
					}
				}
			}
		}
		
			
		NamedNodeMap nnm = mynode.getAttributes();
		if(nnm != null){
			if(nnm.getNamedItem("src") != null){
				Pattern te = Pattern.compile("text[0-9]+");
				Pattern im = Pattern.compile("image[0-9]+");
				Matcher ma = te.matcher(nnm.getNamedItem("src").getNodeValue());
				if(ma.find()){
					String gid = ma.group(0);
					if(thash.get(gid) != null){
						adding = getTextInfoFromDef(thash.get(gid));
					}
				}else{
					ma = im.matcher(nnm.getNamedItem("src").getNodeValue());
					if(ma.find()){
						String gid = ma.group(0);
						if(thash.get(gid) != null){
							adding = getImageInfoFromDef(thash.get(gid));
						}
					}
				}
			}
			if(adding != null){
				adding.makePathLine();
				if(nnm.getNamedItem("fill") != null){
					Pattern url = Pattern.compile("url\\(#([^\\)]+)\\)");
					Matcher ma = url.matcher(nnm.getNamedItem("fill").getNodeValue());
					if(ma.find()){
						String gid = ma.group(1);
						if(ghash.get(gid) != null){
							adding.setGradient(getGradationFromDef(ghash.get(gid),adding));
						}
					}
				}
				if(nnm.getNamedItem("name") != null){
					adding.setName(nnm.getNamedItem("name").getNodeValue());
				}
				
				
				nowlayer.addItem(adding);
				setStyles(nnm.getNamedItem("style").getTextContent(),adding);
			}
		}
		depth++;
		NodeList nl = mynode.getChildNodes();
		if(nl.getLength() == 0){
			return false;
		}
		for(int ii = 0;ii < nl.getLength();ii++){
			Node childel = nl.item(ii);
			checkChildNodes(childel,depth,al,ghash,thash);
		}
		return true;
	}
	public static int checkGNode(Node mynode){
		NodeList tnl = mynode.getChildNodes();
		int gnum = 0;
		
		if(tnl.getLength() > 0){
			for(int ii = 0;ii < tnl.getLength();ii++){
				Node childel = tnl.item(ii);
				if(childel.getNodeName().toUpperCase().compareTo("G") == 0){
					gnum++;
				}
				gnum += checkGNode(childel);
			}
		}
		return gnum;
		
	}
	public static PathGroup loadPath(Node mynode){
		NamedNodeMap nnm = mynode.getAttributes();
		try{
			Pattern pat = Pattern.compile("[A-Za-z.0-9\\-]+");
			Pattern numpat = Pattern.compile("[\\-.0-9]+");
			Matcher mat = pat.matcher(nnm.getNamedItem("d").getTextContent()+" END");
			float xx = 0,yy=0;
			float controlxx=0,controlyy=0;
			String strategy = " ";
			int stnum = -10000;
			int stnumlast = -1;
			boolean relateflag = false;
			ArrayList<Point2D.Float> pointal = new ArrayList<Point2D.Float>();
			GeneralPath mgp = new GeneralPath();
			
			while(mat.find()){
				Matcher nummat = numpat.matcher(mat.group());
				
				if(!nummat.find()){
					relateflag = (strategy.compareTo(strategy.toUpperCase()) != 0);
					float tx = 0;
					float ty = 0;
					switch(stnum){
						case ST_M:
						for(int ii = 0;ii < pointal.size();ii++){
							Point2D.Float p2d = pointal.get(ii);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							if(ii == 0){
								mgp.moveTo(tx+p2d.x,ty+p2d.y);
							}else{
								mgp.lineTo(tx+p2d.x,ty+p2d.y);
							}
							
						}
						break;
						case ST_Z:
						mgp.closePath();
						break;
						case ST_L:
						for(int ii = 0;ii < pointal.size();ii++){
							Point2D.Float p2d = pointal.get(ii);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							mgp.lineTo(tx+p2d.x,ty+p2d.y);
						}
						break;
						
						case ST_H:
						for(int ii = 0;ii < pointal.size();ii++){
							Point2D.Float p2d = pointal.get(ii);
							mgp.lineTo(tx+p2d.x,ty+p2d.y);
						}
						break;
						case ST_V:
						for(int ii = 0;ii < pointal.size();ii++){
							Point2D.Float p2d = pointal.get(ii);
							mgp.lineTo(tx+p2d.x,ty+p2d.y);
						}
						break;
						
						case ST_C:
						for(int ii = 0;ii < pointal.size()/3;ii++){
							Point2D.Float p2d = pointal.get(ii*3);
							Point2D.Float p2d2 = pointal.get(ii*3+1);
							Point2D.Float p2d3 = pointal.get(ii*3+2);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							mgp.curveTo(tx+p2d.x,ty+p2d.y,tx+p2d2.x,ty+p2d2.y,tx+p2d3.x,ty+p2d3.y);
							controlxx = p2d2.x+tx;
							controlyy = p2d2.y+ty;
						}
						break;
						case ST_S:
						if(stnumlast != ST_S && stnumlast != ST_C){
							controlxx = xx;
							controlxx = yy;
						}
						for(int ii = 0;ii < pointal.size()/2;ii++){
							Point2D.Float p2d = pointal.get(ii*2);
							Point2D.Float p2d2 = pointal.get(ii*2+1);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							
							
							mgp.curveTo(controlxx,controlyy,tx+p2d.x,ty+p2d.y,tx+p2d2.x,ty+p2d2.y);
							controlxx = p2d2.x+tx;
							controlyy = p2d2.y+ty;
						}
						break;
						
						case ST_Q:
						for(int ii = 0;ii < pointal.size()/2;ii++){
							Point2D.Float p2d = pointal.get(ii*2);
							Point2D.Float p2d2 = pointal.get(ii*2+1);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							controlxx = p2d.x+tx;
							controlyy = p2d.y+ty;
							mgp.quadTo(tx+p2d.x,ty+p2d.y,tx+p2d2.x,ty+p2d2.y);
						}
						break;
						case ST_T:
						if(stnumlast != ST_T && stnumlast != ST_Q){
							controlxx = xx;
							controlxx = yy;
						}
						for(int ii = 0;ii < pointal.size();ii++){
							Point2D.Float p2d = pointal.get(ii);
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							mgp.quadTo(controlxx,controlyy,tx+p2d.x,ty+p2d.y);
						}
						break;
						
						case ST_A:/*
						if(stnumlast != ST_T && stnumlast != ST_Q){
							controlxx = xx;
							controlxx = yy;
						}
						for(int ii = 0;ii < pointal.size()/4;ii++){
							Point2D.Float p2d = pointal.get(ii*4);
							Point2D.Float rotation = pointal.get(ii*4+1);
							Point2D.Float flags = pointal.get(ii*4+2);
							Point2D.Float p2d2 = pointal.get(ii*4+3);
							
							
							if(relateflag){
								tx = xx;
								ty = yy;
							}else{
								tx = 0;
								ty = 0;
							}
							float tmpxx,tmpyy;
							float ledxy = p2d.x/p2d.y;
							
							tmpxx = tx+p2d2.x-xx;
							tmpyy = ty+p2d2.y-yy;
							tmpyy = tmpyy*ledxy;
							float x = p2d.x;
							float t = (float)Math.sqrt((tmpxx/2)*(tmpxx/2)+(tmpyy/2)*(tmpyy/2));
							float k = (float)Math.sqrt(x*x+t*t);
							float tmpcx = x/t*(t/x*tmpxx/2-k/x*tmpyy/2);
							float tmpcy = k/t*(t/x*tmpyy/2+k/x*tmpxx/2);
							
							
							
							double startrx = Math.acos(-tmpcx/x);
							double startry = Math.asin(-tmpcy/x);
							double rot = 0.0;
							if(-tmpcx < 0.0){
								rot = 3.14-startry;
							}else{
								rot = startry;
							}
							double endrx = Math.acos((tmpxx-tmpcx)/x);
							double endry = Math.asin((tmpyy-tmpcy)/x);
							double endrot = 0.0;
							if(tmpxx-tmpcx < 0.0){
								endrot = 3.14-endry;
							}else{
								endrot = endry;
							}
							Arc2D.Float af2 = new Arc2D.Float(-x/2,-x/2,x,x,(float)rot,(float)endrot,Arc2D.Float.OPEN);
							PathGroup nepg = ShapeModifier.changeShapeToPath(new PathGroup(),af2);
							
							for(int jj = 0;jj < nepg.anchorVec.size();jj++){
								LAnchorPoint lap = (LAnchorPoint)nepg.anchorVec.get(jj);
								
								for(int kk = 0;kk < 3;kk++){
									lap.shapePoint[kk].x = lap.shapePoint[kk].x+tmpcx;
									lap.shapePoint[kk].y = (lap.shapePoint[kk].y+tmpcy)/ledxy;
									
									lap.shapePoint[kk].x = lap.shapePoint[kk].x+xx;
									lap.shapePoint[kk].y = lap.shapePoint[kk].y+yy;
									
								}
							}
							for(int jj = 0;jj < nepg.anchorVec.size()-1;jj++){
								LAnchorPoint lap = (LAnchorPoint)nepg.anchorVec.get(jj);
								LAnchorPoint lap2 = (LAnchorPoint)nepg.anchorVec.get(jj+1);
								
								mgp.curveTo(lap.shapePoint[LAnchorPoint.NEXT].x,lap.shapePoint[LAnchorPoint.NEXT].y,
								lap2.shapePoint[LAnchorPoint.PREV].x,lap2.shapePoint[LAnchorPoint.PREV].y,
								lap2.shapePoint[LAnchorPoint.MAIN].x,lap2.shapePoint[LAnchorPoint.MAIN].y);
								
							}
							
							
							
							
						}*/
						break;
						case -10000://dummy
						break;
						default:
						System.err.println("UNKNOWN CODE!");
						break;
						
					}
					
					
					strategy = mat.group();
					pointal.clear();
					stnum = ST_INDEX.indexOf(strategy.toUpperCase());
				}else{
					
					switch(stnum){
						case ST_M:
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp = new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						case ST_Z:
						break;
						case ST_L:
						
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp = new  Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						
						case ST_H:
						{	
							String ssm = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),0.0f);
							pointal.add(newp);
						}
						break;
						case ST_V:
						{	
							String ssm = mat.group();
							Point2D.Float newp =  new Point2D.Float(0.0f,Float.parseFloat(ssm));
							pointal.add(newp);
						}
						break;
						
						case ST_C:
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						case ST_S:
						
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						
						case ST_Q:
						
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						case ST_T:
						
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						
						case ST_A:
						{	
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						{	
							mat.find();
							String ssm = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),0.0f);
							pointal.add(newp);
						}
						{	
							mat.find();
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						{	
							mat.find();
							String ssm = mat.group();
							mat.find();
							String ssm2 = mat.group();
							Point2D.Float newp =  new Point2D.Float(Float.parseFloat(ssm),Float.parseFloat(ssm2));
							pointal.add(newp);
						}
						break;
						default:
						//System.out.println("UNKNOWN CODE!");
						break;
						
					}
					
					
					
				}
				
			}
			/*
			BufferedImage dum = new BufferedImage(800,800,BufferedImage.TYPE_INT_ARGB );
			Graphics2D g2 = (Graphics2D)dum.getGraphics(); 
			g2.draw(mgp);
			ImageIOCapToc.saveToFile(dum,"test.png");
			*/
			return ShapeModifier.changeShapeToPath(new PathGroup(),mgp);
		
		}catch(Exception exx){
			System.err.println("Error in Path Format.");
			return null;
		}
	}
	public static PathGroup loadRect(Node mynode){
		NamedNodeMap nnm = mynode.getAttributes();
		try{
			Pattern pat = Pattern.compile("[\\-0-9.]");
			Matcher mat = pat.matcher(nnm.getNamedItem("x").getTextContent());
			float x,y,w,h;
			mat.find();
			x = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("y").getTextContent());
			mat.find();
			y = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("w").getTextContent());
			mat.find();
			w = Float.parseFloat(mat.group());
			mat = pat.matcher(nnm.getNamedItem("h").getTextContent());
			mat.find();
			h = Float.parseFloat(mat.group());
			
			
			Rectangle2D.Float r2 = new Rectangle2D.Float(x,y,w,h);
			return ShapeModifier.changeShapeToPath(new PathGroup(),r2);
			
		
		}catch(Exception exx){
			System.err.println("Error in Rect Format.");
			return null;
		}
	}
	public static PathGroup loadCircle(Node mynode){
		NamedNodeMap nnm = mynode.getAttributes();
		
				
		try{
			
			Pattern pat = Pattern.compile("[\\-0-9.]");
			Matcher mat = pat.matcher(nnm.getNamedItem("cx").getTextContent());
			float cx,cy,r;
			mat.find();
			cx = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("cy").getTextContent());
			mat.find();
			cy = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("r").getTextContent());
			mat.find();
			r = Float.parseFloat(mat.group());
			
			Ellipse2D.Float e2 = new Ellipse2D.Float(cx,cy,r*2,r*2);
			return ShapeModifier.changeShapeToPath(new PathGroup(),e2);
			
		
		}catch(Exception exx){
			System.err.println("Error in Rect Format.");
			return null;
		}
	}
	public static PathGroup loadEllipse(Node mynode){
		NamedNodeMap nnm = mynode.getAttributes();
		
		try{
			Pattern pat = Pattern.compile("[\\-0-9.]");
			Matcher mat = pat.matcher(nnm.getNamedItem("cx").getTextContent());
			float cx,cy,rx,ry;
			mat.find();
			cx = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("cy").getTextContent());
			mat.find();
			cy = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("rx").getTextContent());
			mat.find();
			rx = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("ry").getTextContent());
			mat.find();
			ry = Float.parseFloat(mat.group());
			
			Ellipse2D.Float e2 = new Ellipse2D.Float(cx,cy,rx,ry);
			return ShapeModifier.changeShapeToPath(new PathGroup(),e2);
		
		}catch(Exception exx){
			System.err.println("Error in Ellipse Format.");
			return null;
		}
		
	}
	public static PathGroup loadLine(Node mynode){
		NamedNodeMap nnm = mynode.getAttributes();
		
		try{
			Pattern pat = Pattern.compile("[\\-0-9.]");
			Matcher mat = pat.matcher(nnm.getNamedItem("x1").getTextContent());
			float x1,y1,x2,y2;
			mat.find();
			x1 = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("y1").getTextContent());
			mat.find();
			y1 = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("x2").getTextContent());
			mat.find();
			x2 = Float.parseFloat(mat.group());
			
			mat = pat.matcher(nnm.getNamedItem("y2").getTextContent());
			mat.find();
			y2 = Float.parseFloat(mat.group());
			
			Line2D.Float l2 = new Line2D.Float(x1,y1,x2,y2);
			return ShapeModifier.changeShapeToPath(new PathGroup(),l2);
		
		}catch(Exception exx){
			System.err.println("Error in Ellipse Format.");
			return null;
		}
		
	}
	public static PathGroup loadPolyLine(Node mynode){
		return loadPolyLine(mynode,false);
	}
	public static PathGroup loadPolygon(Node mynode){
		return loadPolyLine(mynode,true);
	}
	
	public static PathGroup loadPolyLine(Node mynode,boolean closeflag){
		NamedNodeMap nnm = mynode.getAttributes();
		
		try{
			String pointstr = nnm.getNamedItem("points").getTextContent();
			String code[] = pointstr.split("[ \n\r]");
			Path2D.Float pf = new Path2D.Float();
			Pattern pat = Pattern.compile("[^\\-^0-9^.^,]");
			
			for(int ii = 0;ii < code.length;ii++){
				Matcher mat = pat.matcher(code[ii]);
				code[ii] = mat.replaceAll("");
				
				if(code[ii].length() > 1){
					String cxy[] = code[ii].split(",");
					if(ii == 0){
						pf.moveTo(Float.parseFloat(cxy[0]),Float.parseFloat(cxy[1]));
					}else{
						pf.lineTo(Float.parseFloat(cxy[0]),Float.parseFloat(cxy[1]));
					
					}
					
				}
				
			}
			
			if(closeflag){
				pf.closePath();	
			}
			return ShapeModifier.changeShapeToPath(new PathGroup(),pf);
		
		}catch(Exception exx){
			System.err.println("Error in PolyLine Format.");
			return null;
		}
		
	}
	
	public static StringBuffer changeShapeToSVG(Shape ss){
		PathIterator pite = ss.getPathIterator(new AffineTransform());
		float dd[] = new float[6];
		pite.currentSegment(dd);
		StringBuffer rsb = new StringBuffer();
		rsb.append("M "+String.valueOf(dd[0])+" "+String.valueOf(dd[1])+" ");
		pite.next();
		while(!pite.isDone()){
			int typ = pite.currentSegment(dd);
			
			switch(typ){
				case PathIterator.SEG_CLOSE:
				{
					rsb.append("Z ");
				}
				break;
				case PathIterator.SEG_CUBICTO:{
					
					rsb.append("C "+String.valueOf(dd[0])+" "+String.valueOf(dd[1])+" "
					+String.valueOf(dd[2])+" "+String.valueOf(dd[3])+" "
					+String.valueOf(dd[4])+" "+String.valueOf(dd[5])+" ");
					
				}
				break;
				
				case PathIterator.SEG_LINETO:{
					rsb.append("L "+String.valueOf(dd[0])+" "+String.valueOf(dd[1])+" ");
				}
				break;
				
				case PathIterator.SEG_MOVETO:
				{	
					rsb.append("M "+String.valueOf(dd[0])+" "+String.valueOf(dd[1])+" ");
				}
				break;
				
				case PathIterator.SEG_QUADTO:{
					rsb.append("Q "+String.valueOf(dd[0])+" "+String.valueOf(dd[1])+" "+String.valueOf(dd[2])+" "+String.valueOf(dd[3])+" ");
				}
				break;
				
				default:
				
				System.err.println("exception in Font calculation. Undefined Method. ");
				break;
				
			}
			
			pite.next();
		}
		return rsb;
	}
	public static String getGradationDefID(StringBuffer sb,GradationCasette grac,int id,Rectangle2D rect){
		String ret = "";//Gradientからdef要素を作り、IDを返します。IDはGradation+引数のID。
		double boundx = rect.getX();
		double boundy = rect.getY();
		double boundw = rect.getWidth();
		double boundh = rect.getHeight();
		MultipleGradientPaint mp = grac.getGradation();
		if(mp.getClass() == LinearGradientPaint.class){
			LinearGradientPaint lp = (LinearGradientPaint)mp;
			sb.append("\n<linearGradient id=\"Gradation"+String.valueOf(id)+"\" gradientUnits=\"userSpaceOnUse\"");
			ret = "Gradation"+String.valueOf(id);
			sb.append(String.format(" x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" spreadMethod=\""
			,(lp.getStartPoint().getX()-boundx),(lp.getStartPoint().getY()-boundy),
			(lp.getEndPoint().getX()-boundx),(lp.getEndPoint().getY()-boundy)));

		}else if(mp.getClass() == RadialGradientPaint.class){
			RadialGradientPaint rp = (RadialGradientPaint)mp;
			sb.append("<radialGradient id=\"Gradation"+String.valueOf(id)+"\" gradientUnits=\"userSpaceOnUse\"");
			ret = "Gradation"+String.valueOf(id);
			sb.append(String.format(" cx=\"%f\" cy=\"%f\" fx=\"%f\" fy=\"%f\" r=\"%f\" spreadMethod=\""
			,(rp.getCenterPoint().getX()-boundx),(rp.getCenterPoint().getY()-boundy)
			,(rp.getFocusPoint().getX()-boundx),(rp.getFocusPoint().getY()-boundy),rp.getRadius()));
			
			
		}else{
			return "";
		}
		
		if(mp.getCycleMethod() == MultipleGradientPaint.CycleMethod.NO_CYCLE){
				sb.append("pad");
		}else if(mp.getCycleMethod() == MultipleGradientPaint.CycleMethod.REPEAT){
				sb.append("repeat");
		}else{
			sb.append("reflect");
			
		}
		sb.append("\">");
		Color cc[] = mp.getColors();
		float ff[] = mp.getFractions();
		for(int ii = 0;ii < cc.length;ii++){
			sb.append("\n<stop stop-color=\""+getColorString(cc[ii])+"\" offset=\""+String.valueOf(ff[ii])+"\" ");
			if(cc[ii].getAlpha() < 254){
				sb.append("stop-opacity=\""+String.valueOf(cc[ii].getAlpha()/255.0f)+"\"");
			}
			sb.append("/>");
		}
		if(mp.getClass() == LinearGradientPaint.class){
			sb.append("\n</linearGradient>");
		}else if(mp.getClass() == RadialGradientPaint.class){
			sb.append("\n</radialGradient>");
		}
		return ret;
	}
	public static int getColorFraction(Node mynode,Color cc[],float ff[]){
		NodeList tnl = mynode.getChildNodes();
		int gnum = 0;
		if(tnl.getLength() > 0){
			for(int ii = 0;ii < tnl.getLength();ii++){
				Node childel = tnl.item(ii);
				NamedNodeMap nnm = childel.getAttributes();
				if(childel.getNodeName().toUpperCase().compareTo("STOP") == 0){
					cc[gnum] = getColorFromString(nnm.getNamedItem("stop-color").getNodeValue());
					if(nnm.getNamedItem("stop-opacity") != null){
						cc[gnum] = new Color(cc[gnum].getRed(),cc[gnum].getGreen(),cc[gnum].getBlue(),parseTo255(nnm.getNamedItem("stop-opacity").getNodeValue()));
					}
					String off = nnm.getNamedItem("offset").getNodeValue();
					if(off.indexOf("%") == -1){
						ff[gnum] = (float)Double.parseDouble(off);
					}else{
						ff[gnum] = (float)Double.parseDouble(off.substring(0,off.indexOf("%")))/100;
					}
				}
				gnum++;
			}
		}
		return gnum;
	}
	
	public static String setPolycoImageDef(StringBuffer sba,PolycoImage pi,int id){
		int image_width = pi.image.getWidth();
		int image_height = pi.image.getHeight();
		int[] pixelbuffer = new int[image_width];
		StringBuffer sb = new StringBuffer();
		try{
			byte[] pixeldat = new byte[image_width*image_height*4];
				
			for(int ii = 0;ii < image_height;ii++){
				PixelGrabber pg = new PixelGrabber(pi.image,0,ii,image_width,1,pixelbuffer,0,image_width);
				pg.grabPixels();
				for(int jj = 0;jj < pixelbuffer.length;jj++){
					byte[] dd = intToByte4B(pixelbuffer[jj]);
					for(int kk = 0;kk < 4;kk++){
						pixeldat[(ii*pixelbuffer.length+jj)*4+kk] = dd[kk];
					}
				}
			}
			byte[] deflated = ByteZipper.getDeflatedByteArray(pixeldat);
			String idname = "image"+String.valueOf(id);
			sb.append(String.format("<PolycoImage  width=\"%d\" height=\"%d\" bx=\"%f\" by=\"%f\"",image_width,image_height,pi.basePoint.getModiX(),pi.basePoint.getModiY()));
			sb.append(" id=\""+idname+"\" >\n");
			for(int ii = 0;ii < deflated.length;ii++){
				int chk = (int)deflated[ii];
				if( chk < 0){
					chk += 256;
				}
				String stchk = Integer.toHexString(chk);
				if(stchk.length() < 2){
					sb.append("0");
				}
				sb.append(stchk);
				if(ii%400 == 399){
					sb.append("\n");
				}
			}
			sb.append("\n</PolycoImage>");
			sba.append(sb);
			return idname;
		}catch(Exception exx){
			exx.printStackTrace();
			return "";
		}
	}
	public static byte[] intToByte4B(int ii){
        byte[] bb = new byte[4] ;
        
        bb[0] = (byte)((ii >> 24 ) & 0xFF);
        bb[1] = (byte)((ii >> 16 ) & 0xFF);
        bb[2] = (byte)((ii >>  8 ) & 0xFF);
        bb[3] = (byte)((ii >>  0 ) & 0xFF);
        
        /*
        bb[0] = (byte)(0);
        bb[1] = (byte)((ii/65536)%256);
        bb[2] = (byte)((ii/256)%256);
        bb[3] = (byte)((ii)%256);
        */
        return bb;
    }
	public static String setPolycoTextDef(StringBuffer sb,PolycoText pi,int id){
		String data = pi.text;//def要素を作ってsbに当てます。idを返します。
		String idname = "text"+String.valueOf(id);
		data = data.replaceAll("#","&sp;");
		data = data.replaceAll(" ","#nbsp;");
		data = data.replaceAll("\t","#tab;");
		data = data.replaceAll("[\r\n]","#br;");
		data = data.replaceAll("<","#lt;");
		data = data.replaceAll(">","#gt;");
		sb.append("<PolycoText id=\""+idname+"\" ");
		sb.append("size=\""+String.valueOf(pi.rFont.getSize2D())+"\" style=\"");
		switch(pi.rFont.getStyle()){
			case Font.PLAIN:
			sb.append("plain");
			break;
			case Font.ITALIC:
			sb.append("italic");
			break;
			case Font.BOLD:
			sb.append("bold");
			break;
			default:
			sb.append("bolditalic");
			break;
		}
		sb.append("\" ");
		sb.append("font=\""+pi.rFont.getFontName()+"\" ");
		if(pi.direction == PolycoText.VERTICAL){
			sb.append("vertical=\"true\" ");
		}
		sb.append(String.format(" bx=\"%f\" by=\"%f\" ",pi.basePoint.x,pi.basePoint.y));
		
		
		if(pi.rotate90){
			sb.append("rotate90=\"true\" ");
		}
		sb.append(">\n");
		sb.append(data);
		sb.append("\n</PolycoText>");
		return idname;
	}
	public static StringBuffer makeSVGData(PolycoCanvas pc){
		return makeSVGData(pc,pc.getLayerList());
	}
	public static StringBuffer makeSVGData(PolycoCanvas pc,ArrayList<PPLayer> layerlist){
		
		StringBuffer headsb = new StringBuffer();
		StringBuffer datasb = new StringBuffer();
		StringBuffer gradationdef = new StringBuffer();
		double offsetx = 0.0;
		double offsety = 0.0;
		double width = 480;
		double height = 480;
		double ratio = 1.0;
		if(pc != null){
			offsetx = pc.boundingOffsetX;
			offsety = pc.boundingOffsetY;
			width = pc.boundingWidth;
			height = pc.boundingHeight;
			ratio = pc.boundingRatio;
			FlexPoint.setModiValues(1.0,0,0);
			pc.adjustZoom();
		}
		
		headsb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
		headsb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"");
		headsb.append(String.valueOf(width)+"px\" height=\""+String.valueOf(height)+"px\" viewBox=\"0 0 "
		+ String.valueOf(width)+' '+String.valueOf(height)+"\" version=\"1.1\">"+"\n");
		
		headsb.append("\n<defs>");
		//独自タグを使うとインクスケープが落ちるんだが。。。
		headsb.append(String.format("\n<!-- polycosvg ratio=\"%f\" offsetx=\"%f\" offsety=\"%f\" / -->",FlexPoint.getExpand(),FlexPoint.getOffsetX(),FlexPoint.getOffsetY()));
			
		int gradationnum = 0;
		int textnum = 0;
		int imagenum = 0;
		for(int ii = 0;ii < layerlist.size();ii++){
			PPLayer plg = (PPLayer)layerlist.get(ii);
			datasb.append("<g>");
			for(int jj = 0;jj < plg.itemList.size();jj++){
				PolycoItem pi = (PolycoItem)plg.itemList.get(jj);
				String gradationcode = "";
				if(pi.gradation != null){
					//gradationcode = getGradationDefID(gradationdef,pi.gradation,gradationnum,pi.outline.getBounds2D());
					gradationcode = getGradationDefID(gradationdef,pi.gradation,gradationnum,new Rectangle2D.Double(offsetx,offsety,width,height));
					gradationnum++;
				}
				
				
				datasb.append("\n<path style=\""+makeStyles(pi)+"\"");
				String textid = "";
				datasb.append(" name=\""+pi.name+"\"");
				if(pi.getClass() == PolycoText.class){
					textid = setPolycoTextDef(gradationdef,(PolycoText)pi,textnum);
					if(textid.length() > 2){
						datasb.append(" src=\""+textid+"\" "); 
					}
					textnum++;
				}else if(pi.getClass() == PolycoImage.class){
					String imageid = setPolycoImageDef(gradationdef,(PolycoImage)pi,imagenum);
					if(imageid.length() > 2){
						datasb.append(" src=\""+imageid+"\" "); 
					}
					imagenum++;
				}
					if(gradationcode.length() > 2){
						datasb.append(" fill=\"url(#"+gradationcode+")\" ");
					}
					datasb.append(" d=\"");
					datasb.append(changeShapeToSVG(pi.outline));
					datasb.append("\" />\n");
				
			}
			datasb.append("</g>\n");
		}
		
		
		datasb.append("</svg>\n");
		
	
		if(gradationdef.length() > 2){
			headsb.append(gradationdef);
		}
	
		headsb.append("\n</defs>");
		headsb.append(datasb);
		
		return headsb;
	}
	public static String getColorString(Color cc){//rgb(%%%)の形で渡します。
			StringBuffer sb = new StringBuffer();
			sb.append("rgb(");
			sb.append(cc.getRed()/255.0f*100);
			sb.append("%,");
			sb.append(cc.getGreen()/255.0f*100);
			sb.append("%,");
			sb.append(cc.getBlue()/255.0f*100);
			sb.append("%)"); 
			return sb.toString();
	}
	
	public static Color getColorFromString(String str){//不透明度は考えていません
		String cstr = str.toLowerCase();
		
		if(cstr.indexOf("red") > -1){
			return Color.red;
		}else if(cstr.indexOf("green") > -1){
			return Color.green;
		}else if(cstr.indexOf("blue") > -1){
			return Color.blue;
		}
		Pattern fc = Pattern.compile("[0-9.%]+");
		Matcher mm = fc.matcher(cstr);
		int cc[] = new int[3];
		for(int ii = 0;ii < 3;ii++){
			mm.find();
			if(mm.group() != null){
				cc[ii] = parseTo255(mm.group());
			}
		}
		return new Color(cc[0],cc[1],cc[2]);
		
	}
	
	public static StringBuffer changeShapeToSVG_D(Shape ss){
		return changeShapeToSVG_D(ss,new AffineTransform());
	}
	public static StringBuffer changeShapeToSVG_D(Shape ss,AffineTransform aff){
		PathIterator pite = ss.getPathIterator(aff);
		StringBuffer sb = new StringBuffer();
		float dd[] = new float[6];
		boolean nflag=true;
		while(!pite.isDone()){
			int typ = pite.currentSegment(dd);
			
			switch(typ){
				case PathIterator.SEG_CLOSE:
				{
					sb.append(" Z ");
				}
				break;
				case PathIterator.SEG_CUBICTO:{
					sb.append(String.format(" C %f %f %f %f %f %f ",dd[0],dd[1],dd[2],dd[3],dd[4],dd[5]));
				}
				break;
				
				case PathIterator.SEG_LINETO:{
					sb.append(String.format(" L %f %f ",dd[0],dd[1]));
				}
				break;
				
				case PathIterator.SEG_MOVETO:
				{	
					sb.append(String.format(" M %f %f ",dd[0],dd[1]));
				
				}
				break;
				
				case PathIterator.SEG_QUADTO:{
					sb.append(String.format(" Q %f %f  %f %f ",dd[0],dd[1],dd[2],dd[3]));
				
				}
				break;
				
				default:
				
				System.err.println("exception in Shape calculation. Undefined Method. ");
				break;
				
			}
			
			pite.next();
		}
		return sb;
	}
	public static StringBuffer makeFontFormat(Font ft){
		StringBuffer sb =  new StringBuffer();
		sb.append(" font-family=\""+ft.getFamily()+"\"");
		sb.append(" font-size=\""+ft.getSize()+"\"");
		if(ft.isItalic()){
			sb.append(" font-style=\"italic\"");
		}
		if(ft.isBold()){
			sb.append(" font-weight=\"bold\"");
		}
		
		
		
		return sb;
	}
	public static StringBuffer makeStyles(PolycoItem mypg){
		StringBuffer rsb = new StringBuffer();
		
		if(mypg.fillFlag){
			if(mypg.gradation == null){
				rsb.append("fill:"+getColorString(mypg.fillColor)+";");
				if(mypg.fillColor.getAlpha() < 254){
					rsb.append("fill-opacity:"+String.valueOf(mypg.fillColor.getAlpha()/255.0f)+";");
				}
			}
			
			if(mypg.getClass() == PathGroup.class){
				String wstr = "";
				
				switch(mypg.outline.getWindingRule()){
					case GeneralPath.WIND_EVEN_ODD:
						wstr = "evenodd";
					break;
					case GeneralPath.WIND_NON_ZERO:
						wstr = "nonzero";
					break;
					default:
					break;
					
				}
				
				rsb.append("fill-rule:"+wstr+";");
			}else{
				rsb.append("fill-rule:evenodd;");
			
			}
		}else{
			rsb.append("fill:none;");
		}
		if(mypg.drawFlag){
			rsb.append("stroke:"+getColorString(mypg.drawColor)+";");  
			if(mypg.drawColor.getAlpha() < 254){
				rsb.append("stroke-opacity:"+String.valueOf(mypg.drawColor.getAlpha()/255.0f)+";");
			}
			rsb.append("stroke-width:"+mypg.bStroke.getLineWidth()+";");
			
			String wstr = "";
			switch(mypg.trueStroke.getEndCap()){
				case BasicStroke.CAP_BUTT:
					wstr = "butt";
				break;
				case BasicStroke.CAP_ROUND:
					wstr = "round";
				break;
				case BasicStroke.CAP_SQUARE:
					wstr = "square";
				break;
				default:
					wstr = "round";
				break;
				
			}
			
			rsb.append("stroke-linecap:"+wstr+";");
			
			switch(mypg.trueStroke.getLineJoin()){
				case BasicStroke.JOIN_MITER:
					wstr = "miter";
				break;
				case BasicStroke.JOIN_ROUND:
					wstr = "round";
				break;
				case BasicStroke.JOIN_BEVEL:
					wstr = "bevel";
				break;
				default:
					wstr = "round";
				break;
				
			}
			
			rsb.append("stroke-linejoin:"+wstr+";");
			rsb.append("stroke-miterlimit:"+mypg.trueStroke.getMiterLimit()+";");
			
			
		
		}else{
			rsb.append("stroke:none;");
		}
		
		return rsb;
	}
	
	public static int parseTo255(String ss){//カラー用。すべて255に直します。
		int ret = 0;
		try{
		if(ss.indexOf("%") > -1){
			ret = (int)(Double.parseDouble(ss.replaceAll("%",""))/100*255);
		}else if(ss.indexOf(".") > -1){
			ret = (int)(Double.parseDouble(ss)*255);
		}else{
			ret = (int)(Integer.parseInt(ss));
		}
		}catch(Exception exx){
			return 255;
		}
		ret = Math.min(255,ret);
		ret = Math.max(0,ret);
		return ret;
	
	}
}
	