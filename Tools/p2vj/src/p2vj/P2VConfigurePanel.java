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
import java.util.*;
import java.net.*;
import javax.imageio.*;
import java.io.*;

import java.awt.geom.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

public class P2VConfigurePanel extends JPanel implements ActionListener{
	// P2VJ version 0.9 Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// THX!
	
	
	public static final int EVENT_SELECTFILE = 0;
	public static final int EVENT_CLOSE = 1;
	public static final int EVENT_CANCEL = 2;
	public static final int EVENT_RUN = 3;
	public static final int EVENT_SHOWABOUT = 4;
	public static final int EVENT_SHOWPDFSIZE = 5;
	public static final int EVENT_SIZE_SELECTED = 6;
	
	
	
	public static final int JPANEL_CHECKBOX_BASIC = 0;
	public static final int JPANEL_CHECKBOX_ADVANCE = 1;
	public static final int JPANEL_PNGSIZE = 2;
	public static final int JPANEL_PDFSIZE = 3;
	public static final int JPANEL_INFORMATION = 4;
	public static final int JPANEL_BUTTON_MAIN = 5;
	public static final int JPANEL_PDF_OPTION = 6;
	public static final String[] JPANEL__NAME = {"basic","advance","pngsize","pdfsize","information","button","pdfoption"};
	public static final int[] JPANEL__GROUP = {-1,-2,-1,-1,-1,-1,-2};
	
	public static final int JBUTTON_SELECTFILE = 0;
	public static final int JBUTTON_CANCEL = 1;
	public static final int JBUTTON_CLOSE = 2;
	public static final int JBUTTON_RUN = 3;
	public static final int JBUTTON_ABOUT = 4;
	public static final int JBUTTON_PDFSIZE = 5;
	public static final String[] JBUTTON__NAME = {"selectfile","cancel","close","run","@","<<"};
	public static final int[] JBUTTON__GROUP = {JPANEL_BUTTON_MAIN,JPANEL_BUTTON_MAIN,JPANEL_BUTTON_MAIN,-2,JPANEL_BUTTON_MAIN,-2};
	public static final int[] JBUTTON__EVENT ={EVENT_SELECTFILE,EVENT_CANCEL,EVENT_CLOSE,EVENT_RUN,EVENT_SHOWABOUT,EVENT_SHOWPDFSIZE};
	
	
	public static final int JCHECKBOX_POSTERIZE = 0;
	public static final int JCHECKBOX_REMOVEANTIALIASE = 1;
	public static final int JCHECKBOX_BINARYALPHA = 2;
	public static final int JCHECKBOX_MAKEPNG = 3;
	public static final int JCHECKBOX_MAKEPDF = 4;
	public static final int JCHECKBOX_DRAGANDRUN = 5;
	public static final String[] JCHECKBOX__NAME = {"posterize","remove anti-Aliase","binary alpha","makepng","makepdf (no alpha channel)","drag&run"};
	public static final int[] JCHECKBOX__GROUP = {JPANEL_CHECKBOX_BASIC,JPANEL_CHECKBOX_BASIC,JPANEL_CHECKBOX_BASIC,JPANEL_PNGSIZE,JPANEL_PDFSIZE,-2};
	public static final int[] JCHECKBOX__EVENT ={-1,-1,-1,-1,-1,-1};
	
	public static final int JLABEL_FILENAME = 0;
	public static final int JLABEL_PROGRESS = 1;
	public static final String[] JLABEL__NAME = {"file:","progress:"};
	public static final int[] JLABEL__GROUP = {JPANEL_INFORMATION,JPANEL_INFORMATION};
	
	
	Hashtable<Object,Integer> eventObjectHash = new Hashtable<Object,Integer>();
	JPanel basePanel = new JPanel();
	String inifileName = "p2vjsettings.ini";
	JPanel[] jPanel;
	JButton[] jButton;
	JCheckBox[] jCheckBox;
	JLabel[] jLabel;
	String[] sizeData = {
		"Select page size",
		"A3 297x420",
		"A4 210x297",
		"A5 148x210",
		"B3 364x515",
		"B4 257x364",
		"B5 182x257",
		"B6 128x182",
		"A3h 420x297",
		"A4h 297x210",
		"A5h 210x148",
		"B3h 515x364",
		"B4h 364x257",
		"B5h 257x182",
		"B6h 182x128",
		"Letter	216x279"
		};
	JComboBox<String>  sizeBox = new JComboBox<String>(sizeData);
	NumberField pngSizeField = new NumberField(NumberField.MODE_INT);
	NumberField pdfSizeField_w = new NumberField(NumberField.MODE_INT);
	NumberField pdfSizeField_h = new NumberField(NumberField.MODE_INT);
	JTextField pdfTitleField = new JTextField(10);
	JTextField pdfAuthorField = new JTextField(10);
	
	Vector<File> processingList = new Vector<File>();
	String directory = "./";
	Thread vThread;
	HashMap<String,String> settingHash = new HashMap<String,String>();
	boolean cancelPressed = false;
	public void setAllPanels(){
		jPanel = new JPanel[JPANEL__NAME.length];
		for(int ii = 0;ii < JPANEL__NAME.length;ii++){
			jPanel[ii] = new JPanel();
			if(JPANEL__GROUP[ii] > -1){
				jPanel[JPANEL__GROUP[ii]].add(jPanel[ii]);
			}else if(JPANEL__GROUP[ii] == -1){
				basePanel.add(jPanel[ii]);
			}
		}
		jButton = new JButton[JBUTTON__NAME.length];
		for(int ii = 0;ii < JBUTTON__NAME.length;ii++){
			jButton[ii] = new JButton(JBUTTON__NAME[ii]);
			if(JBUTTON__GROUP[ii] > -1){
				jPanel[JBUTTON__GROUP[ii]].add(jButton[ii]);
			}else if(JBUTTON__GROUP[ii] == -1){
				basePanel.add(jButton[ii]);
			}
			jButton[ii].addActionListener(this);
			if(JBUTTON__EVENT[ii] > -1){
				eventObjectHash.put(jButton[ii],JBUTTON__EVENT[ii]);
			}
			jButton[ii].setMargin(new Insets(2,2,2,2));
		}
		
		//jButton[JBUTTON_ABOUT].setMargin(new Insets(0,0,0,0));
		
		jButton[JBUTTON_PDFSIZE].setMargin(new Insets(0,0,0,0));
		
		
		jLabel = new JLabel[JLABEL__NAME.length];
		for(int ii = 0;ii < JLABEL__NAME.length;ii++){
			jLabel[ii] = new JLabel(JLABEL__NAME[ii]);
			if(JLABEL__GROUP[ii] > -1){
				jPanel[JLABEL__GROUP[ii]].add(jLabel[ii]);
			}else if(JLABEL__GROUP[ii] == -1){
				basePanel.add(jLabel[ii]);
			}
		}
		jCheckBox = new JCheckBox[JCHECKBOX__NAME.length];
		for(int ii = 0;ii < JCHECKBOX__NAME.length;ii++){
			jCheckBox[ii] = new JCheckBox(JCHECKBOX__NAME[ii]);
			if(JCHECKBOX__GROUP[ii] > -1){
				jPanel[JCHECKBOX__GROUP[ii]].add(jCheckBox[ii]);
			}else if(JCHECKBOX__GROUP[ii] == -1){
				basePanel.add(jCheckBox[ii]);
			}
			jCheckBox[ii].addActionListener(this);
			if(JCHECKBOX__EVENT[ii] > -1){
				eventObjectHash.put(jCheckBox[ii],JCHECKBOX__EVENT[ii]);
			}
		}
		
		jCheckBox[JCHECKBOX_POSTERIZE].setSelected(false);
		jCheckBox[JCHECKBOX_MAKEPNG].setSelected(false);
		jCheckBox[JCHECKBOX_REMOVEANTIALIASE].setSelected(true);
		jCheckBox[JCHECKBOX_BINARYALPHA].setSelected(true);
		jCheckBox[JCHECKBOX_DRAGANDRUN].setSelected(true);
		
	}
	
	
	P2VConfigurePanel(){
		BoxLayout bp = new BoxLayout(basePanel,BoxLayout.Y_AXIS );
		basePanel.setLayout(bp);
		
		setAllPanels();
		pngSizeField.setValue(100);
		jPanel[JPANEL_CHECKBOX_BASIC].setLayout(new GridLayout(5,1));
		jPanel[JPANEL_CHECKBOX_BASIC].add(jPanel[JPANEL_PNGSIZE]);
		jPanel[JPANEL_CHECKBOX_BASIC].add(jPanel[JPANEL_PDFSIZE]);
		jPanel[JPANEL_PNGSIZE].add(pngSizeField);
		jPanel[JPANEL_PNGSIZE].add(new Label("%"));
		
		
		FlowLayout tf = new FlowLayout(FlowLayout.LEFT);
		tf.setHgap(0);
		jPanel[JPANEL_PNGSIZE].setLayout(tf);
		
		FlowLayout tf2 = new FlowLayout(FlowLayout.LEFT);
		tf2.setHgap(0);
		jPanel[JPANEL_PDFSIZE].setLayout(tf2);
		
		
		
		
		JPanel psize = new JPanel();
		
		FlowLayout tf3 = new FlowLayout(FlowLayout.LEFT);
		tf3.setHgap(2);
		psize.setLayout(tf);
		
		psize.add(new JLabel("Size: "));
		psize.add(pdfSizeField_w);
		psize.add(new Label(" x"));
		psize.add(pdfSizeField_h);
		//psize.add(jButton[JBUTTON_PDFSIZE]);
		
		pdfSizeField_w.setText("210");
		pdfSizeField_h.setText("297");
		
		
		
		
		
		
		
		JPanel titlepanel = new JPanel();
		titlepanel.setLayout(new GridLayout(2,2));
		titlepanel.add(new JLabel("Title: "));
		titlepanel.add(pdfTitleField);
		titlepanel.add(new JLabel("Author: "));
		titlepanel.add(pdfAuthorField);
		
		jPanel[JPANEL_PDF_OPTION].setLayout(new BoxLayout(jPanel[JPANEL_PDF_OPTION],BoxLayout.Y_AXIS ));
		jPanel[JPANEL_PDF_OPTION].add(psize);
		
		jPanel[JPANEL_PDF_OPTION].add(sizeBox);
		jPanel[JPANEL_PDF_OPTION].add(titlepanel);
		
		sizeBox.addActionListener(this);
		eventObjectHash.put(sizeBox,EVENT_SIZE_SELECTED);
		
		/*
		JPanel jplist = new JPanel();
		jplist.setLayout(new GridLayout(1,1));
		JList<String> jl = new JList<String>(sizeData);
		jplist.setPreferredSize(new Dimension(240,160));
		jplist.add(new JScrollPane(jl));
		*/
		
		
		
		hashSettingVariables();
		loadSetting(inifileName);
		mapSettingVariables();
		
		jPanel[JPANEL_INFORMATION].setLayout(new GridLayout(2,1));
		this.add(basePanel);
		new DropTarget(this, new DropTargetAdapter(){
			public void drop( DropTargetDropEvent e ) {
				try {
					Transferable transfer = e.getTransferable();
					if (transfer.isDataFlavorSupported( DataFlavor.javaFileListFlavor ) ) {
						e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						java.util.List fileList =(java.util.List)( transfer.getTransferData( DataFlavor.javaFileListFlavor ) );
						for(int ii = 0;ii < fileList.size();ii++){
							//Image img = loadImageFile(((File)fileList.get(ii)).getPath());
							//System.out.println(((File)fileList.get(ii)).getPath());
							processingList.add((File)fileList.get(ii));
						}
						jButton[JBUTTON_RUN].doClick();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	public PolycoCanvas vectorize(String infile){
		BufferedImage img = null;
		PolycoCanvas ret = null;
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
			if(jCheckBox[JCHECKBOX_POSTERIZE].isSelected()){
				posterize = 256;
			}
			ret = BitMapVectorizer.getAllPath(img,6,3,posterize
					,jCheckBox[JCHECKBOX_REMOVEANTIALIASE].isSelected(),false,jLabel[JLABEL_PROGRESS]
			,jCheckBox[JCHECKBOX_BINARYALPHA].isSelected());
		}
		return ret;
	}
	
	public void hashSettingVariables(){
		settingHash.put("pdfwidth",pdfSizeField_w.getText());
		settingHash.put("pdfheight",pdfSizeField_h.getText());
		settingHash.put("pdftitle",pdfTitleField.getText());
		settingHash.put("pdfauthor",pdfAuthorField.getText());
		settingHash.put("pngsize",pngSizeField.getText());
		
		settingHash.put("removeae",jCheckBox[JCHECKBOX_REMOVEANTIALIASE].isSelected()?("1"):("0"));
		settingHash.put("posterize",jCheckBox[JCHECKBOX_POSTERIZE].isSelected()?("1"):("0"));
		settingHash.put("makepng",jCheckBox[JCHECKBOX_MAKEPNG].isSelected()?("1"):("0"));
		settingHash.put("makepdf",jCheckBox[JCHECKBOX_MAKEPDF].isSelected()?("1"):("0"));
		settingHash.put("binaryalpha",jCheckBox[JCHECKBOX_BINARYALPHA].isSelected()?("1"):("0"));
	}
	
	public boolean in_s(String s){
		return settingHash.containsKey(s);
	}
	public String of_s(String s){
		return settingHash.get(s);
	}
	public boolean of_t(String s){
		if(in_s(s)){
			return of_s(s).equals("1");
		}
		return false;
		
	}
	public void mapSettingVariables(){
		if(in_s("pdfwidth")){
			pdfSizeField_w.setText(of_s("pdfwidth"));
		}
		if(in_s("pdfheight")){
			pdfSizeField_h.setText(of_s("pdfheight"));
		}
		if(in_s("pdftitle")){
			pdfTitleField.setText(of_s("pdftitle"));
		}
		
		if(in_s("pdfauthor")){
			pdfAuthorField.setText(of_s("pdfauthor"));
		}
		
		if(in_s("pngsize")){
			pngSizeField.setText(of_s("pngsize"));
		}
		
		jCheckBox[JCHECKBOX_REMOVEANTIALIASE].setSelected(of_t("removeae"));
		jCheckBox[JCHECKBOX_POSTERIZE].setSelected(of_t("posterize"));
		jCheckBox[JCHECKBOX_MAKEPNG].setSelected(of_t("makepng"));
		jCheckBox[JCHECKBOX_MAKEPDF].setSelected(of_t("makepdf"));
		jCheckBox[JCHECKBOX_BINARYALPHA].setSelected(of_t("binaryalpha"));
		
		
	}
	
	public String getOutFileName(String infile,String ext){
		String base = infile.replaceAll("\\.[^.]+$","");
		String ret = base+ext;
		int num = 0;
		File f = new File(ret);
		while(f.exists()){
			ret = base+"_"+String.valueOf(num++)+ext;
			f = new File(ret);
		}
		return ret;
	}
	public void actionPerformed(ActionEvent e){
		if(eventObjectHash.get(e.getSource()) != null){
			switch(eventObjectHash.get(e.getSource())){
				case EVENT_CLOSE:
				Container c = getParent();
				while(c.getParent() != null){
					c = c.getParent();
				}
				if(c.getClass() == JFrame.class){
					((JFrame)c).dispose();
				}
				
				break;
				
				
				case EVENT_SIZE_SELECTED:
					int sel = sizeBox.getSelectedIndex();
					if(sel > -1){
						String code = sizeData[sel];
						Pattern pat = Pattern.compile("([0-9]+)x([0-9]+)");
						Matcher mat = pat.matcher(code);
						if(mat.find()){
							pdfSizeField_w.setText(mat.group(1));
							pdfSizeField_h.setText(mat.group(2));
						}
					}
				break;
				case EVENT_CANCEL:
				cancelPressed = true;
				processingList.clear();
				BitMapVectorizer.cancelPressed = true;
				break;
				case EVENT_SELECTFILE:
				 {
					JFileChooser fc = new JFileChooser(directory);
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setMultiSelectionEnabled(true);
					int selected = fc.showOpenDialog(this);
					if(selected == JFileChooser.APPROVE_OPTION){
						File[] fl = fc.getSelectedFiles() ;
						for(int ii = 0;ii < fl.length;ii++){
							processingList.add(fl[ii]);
						}
					}
					jButton[JBUTTON_RUN].doClick();
				}
				break;
				
				case EVENT_SHOWPDFSIZE:
				//showPDFPreset();
				break;
				case EVENT_SHOWABOUT:
					showMessage("\"P2VJ\" Generates SVG from Raster images.\n"
								+"Copyright (C) 2010 sesamecake\n"
								+"http://sesamecake.blog84.fc2.com/\n\n"
								
								+"This program is free software: you can redistribute it and/or modify\n"
								+"it under the terms of the GNU General Public License as published by\n"
								+"the Free Software Foundation, either version 3 of the License, or\n"
								+" any later version.\n"
								+"\n"
								+"This program is distributed in the hope that it will be useful,\n"
								+"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
								+"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
								+"GNU General Public License for more details.\n"
								
								+"\n"
								+"You should have received a copy of the GNU General Public License\n"
								+"along with this program.   If not, see <http://www.gnu.org/licenses/>.\n"
					);
				break;
				case EVENT_RUN:
					if(vThread != null && vThread.isAlive())break;
					vThread = new Thread() {
				        public void start() {
				           super.start();
							cancelPressed = false;
				        }
						public void run(){
							
							disableCheckBox();
							SimplePDFCompiler spdf = null;
							File firstfile = null;
							Collections.sort(processingList);
							if(jCheckBox[JCHECKBOX_MAKEPDF].isSelected()){
								showPDFOption();
								spdf = new SimplePDFCompiler();
								int w = 210;
								int h = 297;
								try{
									w = Integer.parseInt(pdfSizeField_w.getText());
									h = Integer.parseInt(pdfSizeField_h.getText());
								}catch(NumberFormatException nfe){
									nfe.printStackTrace();
									w = 210;
									h = 297;
									pdfSizeField_w.setText(String.valueOf(w));
									pdfSizeField_h.setText(String.valueOf(h));
								}
								
								spdf.cassette.setSize(w,h);
								spdf.setTitle(pdfTitleField.getText());
								spdf.setAuthor(pdfAuthorField.getText());
							}
							
							
							
							
							while(processingList.size() > 0){
								File file = processingList.remove(0);
								jLabel[JLABEL_FILENAME].setText("file: "+file.getName());
								repaint();
								if(file.getParent() != null){
									directory = file.getParent();
								}
								PolycoCanvas pc = vectorize(file.getPath());
								if(cancelPressed){
									jLabel[JLABEL_FILENAME].setText("canceled");
									break;
								}
								if(pc != null){
									String outfile = getOutFileName(file.getPath(),".svg");
									if(firstfile == null){
										firstfile = file;
									}
									SVGParser.saveToFile(outfile,pc);
									
									if(jCheckBox[JCHECKBOX_MAKEPNG].isSelected()){
										try{
											double zoom = pngSizeField.getValue()/100;
											if(zoom == 0.0){
												pngSizeField.setValue(100);
												zoom = 1;
											}
											
											int wid = (int)(pc.getWidth()*zoom);
											int hei = (int)(pc.getHeight()*zoom);
											BufferedImage vbi = new BufferedImage(wid,hei,BufferedImage.TYPE_INT_ARGB);
											Graphics2D g2 = vbi.createGraphics();
											//g2.setColor(Color.white);
											//g2.fillRect(0,0,wid,hei);
											g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
											g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY);
											g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
											FlexPoint.setModiValues(zoom,0,0);
											pc.adjustZoom();
											pc.paint(g2);
											String pngoutfile = getOutFileName(file.getPath()+"_vec",".png");
											ImageIOWrapper.saveToFile(vbi,pngoutfile);
											FlexPoint.setModiValues(1.0,0,0);
											pc.adjustZoom();
										}catch(Exception exx){
											exx.printStackTrace();
										}
									}
									if(spdf != null){
										int ox = (int)pc.boundingOffsetX;
										int oy = (int)pc.boundingOffsetY;
										int wid = (int)pc.boundingWidth;
										int hei = (int)pc.boundingHeight;
										AffineTransform aff = SVGToPDF.transCenter(new Rectangle2D.Double(ox,oy,wid,hei),new Rectangle2D.Double(0,0,spdf.cassette.getWidth(),spdf.cassette.getHeight()));
										PDFPageObject pp = spdf.addBlankPage();
										SVGToPDF.drawSVG(pc.layerList,aff,spdf,pp);//layer will be transformed
										pp.saveMem();
									}
									
								}else{
									System.err.println("Could not vectorize "+file.getPath()+".");
								}
								
							}
							jLabel[JLABEL_FILENAME].setText("file: completed");
							if(spdf != null){
								String outfilepdf = getOutFileName(firstfile.getPath(),".pdf");
								spdf.generatePDF(outfilepdf);
							}
							
							enableCheckBox();
							hashSettingVariables();
							saveSetting(inifileName);
							repaint();
							
						}
					};
				vThread.start();
				break;
				
			}
		}
	}
	public void disableCheckBox(){
		for(JCheckBox jc:jCheckBox){
			jc.setEnabled(false);
		}
		pngSizeField.setEditable(false);
	}
	
	public void enableCheckBox(){
		for(JCheckBox jc:jCheckBox){
			jc.setEnabled(true);
		}
		pngSizeField.setEditable(true);
	
	}
	
	
	public static String loadString(String filename){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			return sb.toString();
		}catch(Exception exx){
			exx.printStackTrace();
			return "";
		}
	}
	public void loadSetting(String fname){
		File f = new File(fname);
		if(!f.exists()){
			return;
		}
		String[] s = loadString(fname).split("[\r\n]+");
		Pattern pat = Pattern.compile("^([^=]+)=(.+)");
		for(String ss:s){
			Matcher mat = pat.matcher(ss);
			if(mat.find()){
				settingHash.put(mat.group(1).replaceAll("[\\s]",""),mat.group(2).replaceAll("[\\s]+$","").replaceAll("^[\\s]+",""));
			}
		}
		
		
	}
	public void saveSetting(String fname){
		try{
			File f = new File(fname);
			if(f.exists()){
				String s = loadString(fname);
				if(s.indexOf("P2VJSetting") != 0){
					System.err.println("Setting file is broken or has been generated by another program.\nPlease remove manually.");
					return;
				}
			}
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname),"UTF-8"))); 
		
			Iterator<String> ite = settingHash.keySet().iterator();
			pw.print("P2VJSetting\n");
			while(ite.hasNext()){
				String k = ite.next();
				pw.print(k+"="+settingHash.get(k).replaceAll("[\n\r]",""));
				pw.print("\n");
			}
			pw.close();
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	public void showMessage(String mess){
		JTextArea messArea = new JTextArea(mess,5,25);
		messArea.setOpaque(false);
		messArea.setAlignmentX(TextArea.CENTER_ALIGNMENT);
		messArea.setEditable(false);
		
		JOptionPane.showMessageDialog(this,messArea, 
				"Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	
	
	public void showPDFOption(){
		JOptionPane.showConfirmDialog(this,jPanel[JPANEL_PDF_OPTION], 
				"PDF Option", JOptionPane.DEFAULT_OPTION );
		
	}
}