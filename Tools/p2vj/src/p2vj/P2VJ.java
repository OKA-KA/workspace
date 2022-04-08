/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2vj;
import java.awt.image.*;
import javax.imageio.*;
import java.util.HashMap;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;


public class P2VJ{
	
	
	public static void commandLineAction(HashMap<String,String> args){
		BufferedImage img = null;
		PolycoCanvas pc = null;
		String infile = args.get("-i");
		try{
			img = ImageIO.read(new File(infile));
		}catch(Exception exx){
			exx.printStackTrace();
			img = null;
		}
		if(img == null){
			System.err.println("ImageIO ERROR");
		}else if (img.getWidth() < 20 || img.getHeight() < 20){
			System.err.println("Too small image!");
		
		}else{
			int posterize = -1;
			if(args.containsKey("-posterize")){
				posterize = Integer.parseInt(args.get("-posterize"));
			}
			pc = BitMapVectorizer.getAllPath(img,6,3,posterize
					,!args.get("-remove_aa").equals("FALSE")
					,false
					,null
			,args.get("-binary_alpha").equals("TRUE"));
		}
		
		if(args.containsKey("-o")){
			SVGParser.saveToFile(args.get("-o"),pc);
		}else{
			SVGParser.printInStdout(pc);
		}
		
		if(args.containsKey("-png")){
			try{
				double zoom = Double.parseDouble(args.get("-png_scale"));
				if(zoom == 0.0){
					zoom = 1;
				}
				
				int wid = (int)(pc.getWidth()*zoom);
				int hei = (int)(pc.getHeight()*zoom);
				BufferedImage vbi = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = vbi.createGraphics();
				g2.setColor(Color.white);
				g2.fillRect(0,0,wid,hei);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
				FlexPoint.setModiValues(zoom,0,0);
				pc.adjustZoom();
				pc.paint(g2);
				String pngoutfile = args.get("-png");
				ImageIOWrapper.saveToFile(vbi,pngoutfile);
				FlexPoint.setModiValues(1.0,0,0);
				pc.adjustZoom();
			}catch(Exception exx){
				exx.printStackTrace();
			}
		}
		
		if(args.containsKey("-pdf")){
			int ox = (int)pc.boundingOffsetX;
			int oy = (int)pc.boundingOffsetY;
			int wid = (int)pc.boundingWidth;
			int hei = (int)pc.boundingHeight;
			
			int w = wid;
			int h = hei;
			
			SimplePDFCompiler spdf = new SimplePDFCompiler();
			spdf.cassette.setSize(w,h);
			spdf.setTitle("Vectrized Image");
			spdf.setAuthor("");
			AffineTransform aff = SVGToPDF.transCenter(new Rectangle2D.Double(ox,oy,wid,hei),new Rectangle2D.Double(0,0,spdf.cassette.getWidth(),spdf.cassette.getHeight()));
			PDFPageObject pp = spdf.addBlankPage();
			SVGToPDF.drawSVG(pc.layerList,aff,spdf,pp);//layer will be transformed
			pp.saveMem();
			String outfilepdf = args.get("-pdf");
			spdf.generatePDF(outfilepdf);
		}
	}
	public static HashMap<String,String> parseArgs(String args[]){
		HashMap<String,String> ret = new HashMap<>();
		
		for(int ii = 0;ii < args.length;ii++){
			if(args[ii].indexOf("-") == 0){
				if(args.length <= ii+1){
					ret.put(args[ii],"TRUE");
				}else{
					if(args[ii+1].indexOf("-") == 0){
						ret.put(args[ii],"TRUE");
					}else{
						ret.put(args[ii],args[ii+1]);
					}
				}
			}
		}
		return ret;
	}
	
	
	public static void main(String[] args){
		HashMap<String,String> parsed = parseArgs(args);
		
		
		for(String kk:parsed.keySet()){
			if(kk.equals("-i")
			||kk.equals("-o")
			||kk.equals("-h")
			||kk.equals("-png")
			||kk.equals("-png_scale")
			||kk.equals("-pdf")
			||kk.equals("-remove_aa")
			||kk.equals("-posterize")
			||kk.equals("-binary_alpha")
			
			){
			}else{
				System.err.println("Unknown option "+kk);
				System.exit(-1);
			}
		}
		if(!parsed.containsKey("-remove_aa")){
			parsed.put("-remove_aa","TRUE");
		}
		if(!parsed.containsKey("-binary_alpha")){
			parsed.put("-binary_alpha","FALSE");
		}
		if(!parsed.containsKey("-png_scale")){
			parsed.put("-png_scale","1.0");
		}
		if(parsed.containsKey("-h")){
			System.err.println("-i <Required: input file name. If this option is not exist, GUI mode will start.>");
			System.err.println("-o <Optional: output file name. default is STDOUT>");
			
			System.err.println("-png <Optional: png file name>");
			System.err.println("-png_scale <Optional: scale of png image. The default is 1.0>");
			System.err.println("-pdf <Optional: pdf file name> ");
			System.err.println("-posterize <Optional: (int) number of colors> ");
			System.err.println("-remove_aa <Optional: TRUE or FALSE. Remove antialiase or not. The default is TRUE> ");
			System.err.println("-binary_alpha <Optional: TRUE or FALSE. Pixels whose alpha channel value less than 64 become completely transparent and more than or equal to 64 become completely opaque. The default is FALSE> ");
			
			
			//System.out.println("Multi page pdf:");
			//System.out.println("-list <file contains names of files which you want to process> -pdf <pdf file name> ");
			
		}else{
			if(!parsed.containsKey("-i")){
				P2VConfigurePanel p2v = new P2VConfigurePanel();
				JFrame jf = new JFrame("P2VJ");
				jf.add(p2v);
				jf.pack();
				jf.setLocationRelativeTo(null);
				jf.setVisible(true);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}else{
				commandLineAction(parsed);
				
			}
		}
	}
}