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


public class SVGToPDF{
	public static Rectangle2D.Double getSVGBounds(String s){
		double height = 100;
		double width = 100;
		try{
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			StringReader strread = new StringReader(s);
			Document doc = builder.parse(new InputSource(strread));
			Element root = doc.getDocumentElement();
		
			NodeList nl = root.getChildNodes();
			NamedNodeMap nm = root.getAttributes();
			if(nm.getNamedItem("height") != null){
				height = Double.parseDouble(nm.getNamedItem("height").getNodeValue().replaceAll("[a-z]",""));
			}
			if(nm.getNamedItem("width") != null){
				width = Double.parseDouble(nm.getNamedItem("width").getNodeValue().replaceAll("[a-z]",""));
			}
			for(int ii = 0;ii < nl.getLength();ii++){
				Node childel = nl.item(ii);
				if(childel.getNodeName().toUpperCase().equals("SVG") ){
					NamedNodeMap nnc = childel.getAttributes();
					if(nnc.getNamedItem("height") != null){
						height = Double.parseDouble(nnc.getNamedItem("height").getNodeValue());
					}
					if(nnc.getNamedItem("width") != null){
						width = Double.parseDouble(nnc.getNamedItem("width").getNodeValue());
					}
				}
			}
		}catch(Exception exx){
			exx.printStackTrace();
		
		}
		return new Rectangle2D.Double(0,0,width,height);
		
	}
	public static StringBuffer loadContents(String filename){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			return sb;
		}catch(Exception exx){
			exx.printStackTrace();
			return null;
		}
	}
	
	public static void drawSVG(ArrayList<PPLayer> layerlist,AffineTransform af,SimplePDFCompiler sp,PDFPageObject pp){
		for(int ii = 0;ii < layerlist.size();ii++){
			PPLayer plg = (PPLayer)layerlist.get(ii);
			for(int jj = 0;jj < plg.itemList.size();jj++){
				PolycoItem pi = (PolycoItem)plg.itemList.get(jj);
				pi.transPoints(af);
				
				Color f = pi.fillColor;
				Color d = pi.drawColor;
				if(!pi.fillFlag){
					f = null;
				}
				if(!pi.drawFlag){
					d = null;
				}
				
				PostScriptContents pcc = new PostScriptContents(pi.outline,f,d,sp.cassette.getHeight());
				pcc.setStroke(pi.trueStroke);
				sp.addPostScript(pcc,pp);
			}
		}
	}
	public static AffineTransform calcTransform(String filename,Rectangle2D.Double p){
		ArrayList<PPLayer> layerlist = new ArrayList<PPLayer>();
		String contents = loadContents(filename).toString();
		Rectangle2D.Double bounds = getSVGBounds(contents);
		return calcTransform(bounds,p);
	}
	public static AffineTransform transCenter(Rectangle2D.Double b,Rectangle2D.Double p){
		double xratio = p.getWidth()/b.getWidth();
		double yratio = p.getHeight()/b.getHeight();
		double pratio = Math.min(xratio,yratio);
		Rectangle2D.Double pm = new Rectangle2D.Double(b.getX()*pratio,b.getY()*pratio,b.getWidth()*pratio,b.getHeight()*pratio);
		AffineTransform af = new AffineTransform(1,0,0,1,p.getX()+p.getWidth()/2-pm.getWidth()/2+pm.getX(),p.getY()+p.getHeight()/2-pm.getHeight()/2+pm.getY());
		AffineTransform af2 =new AffineTransform(pratio,0,0,pratio,0,0);
		af.concatenate(af2);
		return af;
	}
	public static AffineTransform calcTransform(Rectangle2D.Double bounds,Rectangle2D.Double p){
		double xratio = p.getWidth()/bounds.getWidth();
		double yratio = p.getHeight()/bounds.getHeight();
		AffineTransform af = new AffineTransform(1,0,0,1,p.getX(),p.getY());
		AffineTransform af2 =new AffineTransform(xratio,0,0,yratio,0,0);
		af.concatenate(af2);
		return af;
	}
	public static void drawSVG(String filename,Rectangle2D.Double p,SimplePDFCompiler sp,PDFPageObject pp){
		ArrayList<PPLayer> layerlist = new ArrayList<PPLayer>();
		String contents = loadContents(filename).toString();
		AffineTransform af = calcTransform(filename,p);
		if(SVGParser.loadFromSVG(contents,layerlist)){
			drawSVG(layerlist,af,sp,pp);
		}
		
	}
	
	
	public static void main(String[] args){
		
		SimplePDFCompiler sp = new SimplePDFCompiler();
		sp.cassette.setSize(1028,728);
		
		PDFPageObject pg = sp.addBlankPage();
		drawSVG("akumako.svg",new Rectangle2D.Double(0,0,728,728),sp,pg);
		sp.generatePDF("svgtest.pdf");
	}
	
}