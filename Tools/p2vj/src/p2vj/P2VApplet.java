package p2vj;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Graphics;
import java.awt.event.*;
import java.applet.*;
import java.util.Hashtable;
import java.awt.image.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.*;
import javax.imageio.*;
import java.io.*;


/**
 * OBSOLETE CLASSSS
 * @author sesamecake
 */
public class P2VApplet extends JApplet implements ActionListener{
	
	public final int DEFAULT_WIDTH = 500;
	public final int DEFAULT_HEIGHT = 500;
	public final int EVENT_DOWNLOAD = 0;
	
	Hashtable <Object,Integer> eventObjectHash = new Hashtable<Object,Integer>();
	JInternalFrame panelFrame = null;
	JButton dlButton = null;
	P2VConfigurePanel config = null;
	PolycoCanvas currentPC = null; 
	JPanel basePanel = null;
	String jumpAddress = "";
	BufferedImage backImage;
	Thread vThread = null;
	String fileName = null;
	Image fImage;
	public P2VApplet(){
	}
	public void init(){
		BitMapVectorizer.THREADWAIT = 10;
		try{
			jumpAddress = getParameter("jump_cgi");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		backImage = new BufferedImage(DEFAULT_WIDTH,DEFAULT_HEIGHT,BufferedImage.TYPE_INT_ARGB);
		config = new P2VConfigurePanel();
		config.jButton[P2VConfigurePanel.JBUTTON_CLOSE].setEnabled(false);
		config.jButton[P2VConfigurePanel.JBUTTON_SELECTFILE].setEnabled(false);
		config.jButton[P2VConfigurePanel.JBUTTON_ABOUT].setEnabled(false);
		config.jCheckBox[P2VConfigurePanel.JCHECKBOX_MAKEPNG].setEnabled(false);
		config.jCheckBox[P2VConfigurePanel.JCHECKBOX_POSTERIZE].setSelected(true);
		dlButton = new JButton("DOWNLOAD");
		panelFrame = new JInternalFrame("config");
		basePanel = new JPanel(){
		public void paint(Graphics g){
			g.drawImage(backImage,0,0,this);
			//paintComponent(g);
			panelFrame.repaint();
		}
		
		};
		this.setSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
		basePanel.setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
		this.add(basePanel);
		
		eventObjectHash.put(dlButton,EVENT_DOWNLOAD);
		//Graphics2D gg2 = backImage.createGraphics();
		//gg2.setColor(Color.red);
		//gg2.fillRect(0,0,100,100);
		panelFrame.add(config);
		basePanel.add(panelFrame);
		panelFrame.setVisible(true);
	}
	public void start(){
		
	}
	public String getSVGData(){
		
		if(currentPC != null){
			StringBuffer sb = SVGParser.makeSVGData(currentPC);
			return sb.toString();
		}
		return "";
	}
	public void stopApplet(){
		
		config.cancelPressed = true;
		config.processingList.clear();
		BitMapVectorizer.cancelPressed = true;
	}
	public void loadImage(String fname){
		if(fname == null)return;
		if(fname.toUpperCase().indexOf(".PNG") == -1 && fname.toUpperCase().indexOf(".JPG") == -1 && fname.toUpperCase().indexOf(".GIF") == -1){
			return;
		}
		if(vThread != null && vThread.isAlive())return;
		fileName = fname;
		try{
		if(fname.length() < 1)return;
		System.err.println(fname);
		fImage = getImage(getDocumentBase(),fname);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(fImage, 1);
		mt.waitForAll();
		}catch(Exception exx){
			exx.printStackTrace();
			return;
		}
		vThread = new Thread() {
	        public void start() {
				           super.start();
	        }
			public void run(){
				loadImage_T(fImage);
			}
		};
		vThread.start();
	}
	public void loadImage_T(Image fimage){
		try{
			boolean flag = false;
			int pwidth = -1;
			int pheight = -1;
			int breakpoint = 0;
			while(!flag){
				pwidth = fimage.getWidth(this);
				pheight = fimage.getHeight(this);
				if(pwidth > 0 && pheight > 0){
					flag = true;
					break;
					
				}
				Thread.sleep(200);
				breakpoint++;
				if(breakpoint > 200){
					System.err.println("Unable to load Image!\n");
					return;
				}
			}
			
			BufferedImage bi = new BufferedImage(pwidth,pheight,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bi.createGraphics();
			g2.drawImage(fimage,0,0,null);
			//サポート切れです
			PolycoCanvas pc = BitMapVectorizer.getAllPath(bi,6,10,(config.jCheckBox[config.JCHECKBOX_POSTERIZE].isSelected())?(6):(0)
			,config.jCheckBox[config.JCHECKBOX_REMOVEANTIALIASE].isSelected(),false,config.jLabel[config.JLABEL_PROGRESS],false);
			double range = 1;
			if(pc.getWidth()/(double)DEFAULT_WIDTH > pc.getHeight()/(double)DEFAULT_HEIGHT){
				range = pc.getHeight()/(double)DEFAULT_HEIGHT;
			}else{
				range = pc.getWidth()/(double)DEFAULT_WIDTH;
			}
			FlexPoint.setModiValues(1/range,0,0);
			pc.adjustZoom();
			
			currentPC = pc;
			
			if(currentPC != null){
				Graphics2D gg2 = backImage.createGraphics();
				gg2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				gg2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				gg2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
				currentPC.paint(gg2);
			}
			
			
			
			repaint();
		}catch(Exception e){
			
			System.err.println("Could not get  uploaded image!");
			e.printStackTrace();
			
		}
	}
	public void actionPerformed(ActionEvent e){
		
		
	}
	  public void stop(){
	  
	  stopApplet();
	  }
	  

	public static void main(String args[]){
		P2VApplet ap = new P2VApplet();
	}
	
}


