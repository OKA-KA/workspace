package p2vj;

import java.util.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import java.util.zip.CRC32;
import java.awt.image.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
public class SimplePDFCompiler{
	PDFCassette cassette = new PDFCassette();
	PDFFontCassette fontCassette = PDFFontCassette.generateTestPattern();
	//AuthorData authorData = new AuthorData();
	SimplePDFCompiler(){
	}
	public void compile(){
		
	}  
	
	public void setR2LFlag(boolean b){
		cassette.setR2LFlag(b);
	}
	public void addPage(){
		
	}
	public PDFPageObject addFittedImage(String fpath)throws Exception{
		PDFPageObject pp = new PDFPageObject();
		cassette.addPage(pp);
		BufferedImage img = ImageIO.read(new File(fpath));
		int w = img.getWidth();
		int h = img.getHeight();
		double tw = w/(double)cassette.getWidth();
		double th = h/(double)cassette.getHeight();
		double rate = 1.0;
		double offsetx = 0;
		double offsety = 0;
		if(tw > th){
			rate = tw;
			offsety = cassette.getHeight()/2.0-h/rate/2.0;
		}else{
			rate = th;
			offsetx = cassette.getWidth()/2.0-w/rate/2.0;
		}
		pp.drawImage(fpath,(int)(offsetx),(int)(offsety),(int)(w/rate),(int)(h/rate),PDFContentsObject.TYPE_AUTO);
		return pp;
	}
	public PDFPageObject addPostScript(PostScriptContents ps){
		
		PDFPageObject pp = new PDFPageObject();
		cassette.addPage(pp);
		addPostScript(ps,pp);
		return pp;
	}
	public PDFPageObject addPostScript(PostScriptContents ps,PDFPageObject pp){
		pp.drawPostScript(ps);
		return pp;
	}
	public void setTwoPageFlag(boolean b){
		cassette.setTwoPageFlag(b);
	}
	public PDFBookMarkNode addBookMark(String t,PDFPageObject pp){
		return cassette.addBookMark(t,pp);
	}
	public void setProgressLabel(javax.swing.JLabel jl){
		cassette.setProgressLabel(jl);
	}
	public void cancel(){
		cassette.cancel();
	}
	public void setAuthor(String a){
		cassette.setAuthor(a);
	}
	public void setTitle(String t){
		cassette.setTitle(t);
	}
	public String getProgressString(){
		
		return cassette.getProgressString();
	}
	public PDFPageObject addBlankPage(){
		PDFPageObject pp = new PDFPageObject();
		cassette.addPage(pp);
		return pp;
	}
	public PDFPageObject drawAuthorData(AuthorData ad){
		String nam =  ad.getAuthorName();
		String web = ad.getAuthorWebsiteName();
		String ur = ad.getAuthorWebsite();
		PDFPageObject pp = new PDFPageObject();
		cassette.addPage(pp);
		boolean notitle = false;
		if(web.length() < 1){
			notitle = true;
			web = ad.getAuthorWebsite();
		}
		
		pp.setFontSize(32);
		double namewidth = pp.currentFontObject.getStringWidth(nam,(int)pp.currentFontSize);
		double sitewidth = pp.currentFontObject.getStringWidth(web,(int)pp.currentFontSize);
		double urlwidth = pp.currentFontObject.getStringWidth(ur,(int)(pp.currentFontSize*0.7));
		double lhei = pp.currentFontObject.getStringHeight(pp.currentFontSize);
		double bwid = Math.max(Math.max(namewidth,sitewidth),urlwidth);
		double bhei = lhei*2+(int)(lhei*0.2)+(int)(lhei*0.8);
		if(notitle){
			bhei = lhei*2+(int)(lhei*0.8);
			pp.drawText(nam,(int)(pp.getWidth()/2-namewidth/2),(int)(pp.getHeight()/2+bhei/2));
			pp.setFontSize((int)(pp.currentFontSize*0.7));
			pp.drawLinkText(ur,ur,(int)(pp.getWidth()/2-urlwidth/2),(int)(pp.getHeight()/2+bhei/2-(lhei*1.0)));
		}else{
			pp.drawText(nam,(int)(pp.getWidth()/2-namewidth/2),(int)(pp.getHeight()/2+bhei/2));
			pp.drawLinkText(web,ur,(int)(pp.getWidth()/2-sitewidth/2),(int)(pp.getHeight()/2+bhei/2-(lhei*1.2)));
			pp.setFontSize((int)(pp.currentFontSize*0.7));
			pp.drawLinkText(ur,ur,(int)(pp.getWidth()/2-urlwidth/2),(int)(pp.getHeight()/2+bhei/2-(lhei*2.1)));
		}
		ur = "http://sesamecake.sakura.ne.jp/picopounder/";
		pp.setFontSize(10);
		double pwidth = pp.currentFontObject.getStringWidth("Compiled by PiCoPounder.",10);
		pp.drawLinkText("Compiled by PicoPounder." ,ur,(int)(pp.getWidth()/2-pwidth/2),(int)(lhei*0.5));
		
		return pp;
		
	}
	public void generatePDF(String filename){
		cassette.generatePDF(filename,"Shift-jis");
		if(cassette.cancelPressed){
			File f = new File(filename);
			try{
				f.delete();
			}catch(Exception exx){
				exx.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		SimplePDFCompiler sp = new SimplePDFCompiler();
		try{
			File dir =new File("./test");
			File[] lis = dir.listFiles();
			PDFBookMarkNode parentnode = null;
		for(int ii = 0;ii < lis.length;ii++){
			String gp = lis[ii].getPath();
			
			if(gp.indexOf(".jpg") > -1 || gp.indexOf(".png") > -1){
				//BufferedImage img = ImageIO.read(lis[ii]);
				PDFPageObject pp = sp.addFittedImage(lis[ii].getPath());
				if(parentnode == null){
					parentnode = sp.addBookMark("parent",pp);
					
				}	
				PDFBookMarkNode cc = sp.addBookMark("testbookmark",pp);
				if(parentnode != null){
					parentnode.addChildNode(cc);
				}
				System.err.println(gp);
			}else{
			}
		}
		
		if(true){
				PDFPageObject pp = sp.addPostScript(new PostScriptContents(
				PostScriptContents.makeTextPath("##TEST##",
				new Font("MSGothic",0,48),
				new Point2D.Double(400,400),
				false,
				false),Color.red,Color.blue
				,sp.cassette.getHeight()));
				
				sp.addPostScript(new PostScriptContents(
				PostScriptContents.makeTextPath("##TEST##",
				new Font("MSGothic",0,24),
				new Point2D.Double(400,400),
				false,
				false),Color.orange,Color.blue
				,sp.cassette.getHeight()),pp);
		}
		
		
		}catch(Exception exx){
			exx.printStackTrace();
		}
		AuthorData ad = new AuthorData();
		ad.setAuthorName("てすと");
		ad.setAuthorWebsite("http://www.yahoo.co.jp/");
		ad.setAuthorWebsiteName("TESTPDF");
		PDFPageObject pqq = sp.drawAuthorData(ad);
		//sp.addBookMark("testbookmark2",pqq);
			
		sp.generatePDF("test.pdf");
	}
	public static void main_k(String[] args){
		try{
			PDFCassette pd = new PDFCassette();
			PDFPageObject pp = new PDFPageObject();
			PDFFontCassette f = PDFFontCassette.generateTestPattern();
			BufferedImage img = ImageIO.read(new File("test.jpg"));
			//PDFImageXObject pi = new PDFImageXObject(img);
			//pp.getResource().addFont(f);
			pp.setFont(f,32);
			pp.drawText("!\"#$%&'()*+abcdefghijklmnﾘﾙﾚﾛﾜﾝﾞﾟ",0,400);
			pp.drawText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",0,480);
			//pp.setFont(f,16);
			pp.drawText("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890",0,580);
			pp.drawImage(img,200,200,PDFContentsObject.TYPE_GRAY);
			//pp.addLink(0,0,200,200,"http://www.yahoo.co.jp/");
			pp.drawLinkText("http://www.yahoo.co.jp/","http://www.yahoo.co.jp/",200,200);
			pd.addPage(pp);
			pd.setAuthor("てすと");
			pd.setTitle("!\"てすとえ");
			pd.generatePDF("simpletest.pdf","Shift-jis");
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
}

class PDFCassette{
	private PDFCatalogObject catalog;
	private PDFPagesObject pages;
	private PDFOutlineObject outline;
	private PDFInfoObject info;
	private ArrayList<PDFPageObject> page = new ArrayList<PDFPageObject>();
	private ArrayList<PDFResourceObject> resource = new ArrayList<PDFResourceObject>();
	private ArrayList<PDFContentsObject> contents = new ArrayList<PDFContentsObject>();
	private ArrayList<PDFObject> others = new ArrayList<PDFObject>();
	private int width = 595;
	private int height = 842;
	String progressString = "";
	JLabel progressLabel = null;
	boolean cancelPressed = false;
	public static SimpleDateFormat dateFormat_Day = new SimpleDateFormat("yyyyMMdd");
	public static String getCurrentDate(){
		Calendar cl = Calendar.getInstance();
		return dateFormat_Day.format(cl.getTime());
	}
	
	PDFCassette(){
		catalog = new PDFCatalogObject();
		pages = new PDFPagesObject();
		info = new PDFInfoObject();
		catalog.setPagesObject(pages);
		setCreationDate(getCurrentDate());
	}
	public void setR2LFlag(boolean b){
		catalog.setR2LFlag(b);
	}
	public void setAuthor(String a){
		info.setAuthor(a);
		
	}
	public void setTitle(String a){
		info.setTitle(a);
	}
	public void setTwoPageFlag(boolean b){
		catalog.setTwoPageFlag(b);
	}
	public void setCreationDate(String a){
		info.setCreationDate(a);
		
	}
	public void setCreationDate(){
		setCreationDate(getCurrentDate());
	}
	public void setSize(int w,int h){
		width = w;
		height = h;
		pages.setSize(w,h);
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void addPage(PDFPageObject pg){
		if(resource.indexOf(pg.getResource()) < 0){
			resource.add(pg.getResource());
		}
		if(contents.indexOf(pg.getContents()) < 0){
			contents.add(pg.getContents());
		}
		page.add(pg);
		pages.addPage(pg);
	}
	
	public PDFBookMarkNode addBookMark(String t,PDFPageObject p){
		return addBookMark(t,p,0,height,0);
	}
	
	public PDFBookMarkNode addBookMark(String t,PDFPageObject p,int x,int y,int z){
		int ind = page.indexOf(p);
		/*if(ind < 0){
			return null;
		}*/
		if(outline == null){
			outline = new PDFOutlineObject();
			catalog.setOutline(outline);
		}
		PDFBookMarkNode ret = outline.addBookMark(t,p,x,y,z,null);
		return ret;
	}
	
	public void addObject(PDFObject pd){
		others.add(pd);
	}
	public String getProgressString(){
		return progressString;
	}
	public void setProgressLabel(javax.swing.JLabel jl){
		progressLabel = jl;
	}
	public void setProgressString(String px){
		if(progressLabel != null){
			progressLabel.setText(px);
			progressLabel.repaint();
		}
		progressString = px;
	}
	public void cancel(){
		cancelPressed = true;
	}
	public void addBookMarkTree(PDFBookMarkNode pn){
		
	}
	
	
	public void generatePDF(String filename,String encoding){
		int idseq = 0;
		cancelPressed = false;
		setProgressString("Mapping File...");
		for(int ii = 0;ii < page.size();ii++){
			idseq = page.get(ii).setIds(idseq+1);
		}
		for(int ii = 0;ii < resource.size();ii++){
			idseq = resource.get(ii).setIds(idseq+1);
			ArrayList<PDFFontCassette> al = resource.get(ii).fonts;
			for(int jj = 0;jj < al.size();jj++){
				if(others.indexOf(al.get(jj)) < 0){
					others.add(al.get(jj));
				}
			}
		
		}
		for(int ii = 0;ii < contents.size();ii++){
			idseq = contents.get(ii).setIds(idseq+1);
		}
		for(int ii = 0;ii < others.size();ii++){
			idseq = others.get(ii).setIds(idseq+1);
		}
		
		if(outline != null){
			idseq = outline.setIds(idseq+1);
		}
		idseq = catalog.setIds(idseq+1);
		idseq = pages.setIds(idseq+1);
		idseq = info.setIds(idseq+1);
		int objnum = idseq;
		try{
			FileOutputStream fout = new FileOutputStream(filename);
			FileChannel channel = fout.getChannel();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),encoding));
			bw.write("%PDF-1.5\r\n\r\n");
			Hashtable<Integer,String> pos = new Hashtable<Integer,String>();
			
			
			for(int ii = 0;ii < others.size();ii++){
				 others.get(ii).print(bw,pos,channel);
				setProgressString("others "+ii+" / "+others.size());
				if(cancelPressed){
					bw.close();
					return;
				}
			}
			
			
			
			for(int ii = 0;ii < page.size();ii++){
				page.get(ii).print(bw,pos,channel);
				setProgressString("page "+ii+" / "+page.size());
				if(cancelPressed){
					bw.close();
					return;
				}
			}
			for(int ii = 0;ii < resource.size();ii++){
				 resource.get(ii).print(bw,pos,channel);
				setProgressString("resource "+ii+" / "+resource.size());
				if(cancelPressed){
					bw.close();
					return;
				}
			}
			
			for(int ii = 0;ii < contents.size();ii++){
				 contents.get(ii).print(bw,pos,channel);
				setProgressString("content "+ii+" / "+contents.size());
				if(cancelPressed){
					bw.close();
					return;
				}
			}
			if(cancelPressed){
				bw.close();
				return;
			}
			if(outline != null){
				outline.print(bw,pos,channel);
			}
			catalog.print(bw,pos,channel);
			pages.print(bw,pos,channel);
			info.print(bw,pos,channel);
			bw.write("\r\n");
			bw.flush();
			long startxref = channel.size();
			
			bw.write("xref\r\n0 "+String.valueOf(objnum+1)+"\r\n");
			bw.write("0000000000 65535 f\r\n");
			
			
			for(int ii = 0;ii <= objnum;ii++){
				if(pos.get(ii) != null){
					bw.write(pos.get(ii)+" 00000 n\r\n");
				}
			}
			bw.write("trailer\r\n<<\r\n");
			bw.write("/Root "+String.valueOf(catalog.getId())+" 0 R\r\n");
			bw.write("/Info "+String.valueOf(info.getId())+" 0 R\r\n");
			bw.write("/Size "+String.valueOf(objnum+1)+"\r\n");
			bw.write(">>\r\nstartxref\r\n");
			bw.write(String.valueOf(startxref));
			bw.write("\r\n%%EOF\r\n");
			bw.close();
			channel.close();
			
		}catch(Exception exx){
			exx.printStackTrace();
		}
		
	}
	
}
class PDFObject{
	int id = 0;
	int startPos = 0;
	Hashtable<String,String> attributes = new Hashtable<String,String>();
	PDFObject(){
	}
	public void setId(int i){
		id = i;
	}
	
	public static String getUTF16Sequence(String str){
		
		StringBuffer sb = new StringBuffer();
		sb.append("<FEFF");
		for(int ii = 0;ii < str.length();ii++){
			String ps = Integer.toHexString(Character.codePointAt(str,ii));
			while(ps.length() < 4){
				ps = "0"+ps;
			}
			sb.append(ps);
		}
		sb.append(">");
		return sb.toString();
	}
	public int getId(){
		return id;
	}
	public void setStartPos(int s){
		startPos = s;
	}
	public void setAttributes(String k,String v){
		attributes.put(k,v);
	}
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		
		fw.flush();
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		fw.write(getAttributesString());
		fw.write("endobj\r\n\r\n\r\n");
		
	}
	public int setIds(int i){
		id = i;
		return id;
	}
	public String getAttributesString(){
		StringBuffer ret = new StringBuffer();
		ret.append("<<\r\n");
		if(attributes.get("/Type") != null){
			ret.append("/Type");
			ret.append(" ");
			ret.append(attributes.get("/Type"));
			ret.append("\r\n");
			//attributes.remove("/Type");
		}
		Enumeration<String> en = attributes.keys();
		while(en.hasMoreElements()){
			String kk = en.nextElement();
			if(!kk.equals("/Type")){
				ret.append(kk);
				ret.append(" ");
				ret.append(attributes.get(kk));
				ret.append("\r\n");
			}
		}
		ret.append(">>\r\n");
		return ret.toString();
	}
	public String getPDFString(){
		StringBuffer ret = new StringBuffer();
		ret.append(id);
		ret.append(" 0 obj\r\n");
		ret.append(getAttributesString());
		ret.append("endobj\r\n\r\n\r\n");
		return ret.toString();
	}
	
}
class PDFBookMarkNode extends PDFObject{
	ArrayList<PDFBookMarkNode> child = new ArrayList<PDFBookMarkNode>();
	String title = "bookmark";
	PDFPageObject page;
	PDFOutlineObject outline = null;
	int yPos = 842;
	int xPos=0;
	int zPos=0;
	PDFBookMarkNode parent;
	PDFObject next;
	PDFObject prev;
	
	PDFBookMarkNode(String t,PDFPageObject p,int x,int y,int z,PDFBookMarkNode pa,PDFOutlineObject out){
		super();
		title = t;
		page = p;
		xPos = x;
		yPos = y;
		zPos = z;
		outline = out;
		if(pa != null){
			parent = pa;
		}
	}
	
	public void setPage(PDFPageObject pa){
		page = pa;
	}
	public void setParentNode(PDFBookMarkNode pa){
		parent = pa;
	}
	public void addChildNode(PDFBookMarkNode pa){
		child.add(pa);
		pa.setParentNode(this);
	}
	public ArrayList<PDFBookMarkNode> getAllChildren(){
		ArrayList<PDFBookMarkNode> ret = new ArrayList<PDFBookMarkNode>();
		for(int ii = 0;ii < child.size();ii++){
			ret.add(child.get(ii));
			ret.addAll(child.get(ii).getAllChildren());
		}
		return ret;
	}
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		fw.flush();
		int allnodenum = getAllChildren().size();
		
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		
		fw.write("<</Title "+getUTF16Sequence(title)+"\r\n");
		if(parent == null){
			fw.write("/Parent "+outline.getId()+" 0 R\r\n");
			int ind = outline.nodes.indexOf(this);
			if(ind < outline.nodes.size()-1 && outline.nodes.size() > 1){
				fw.write("/Next "+outline.nodes.get(ind+1).getId()+" 0 R\r\n");	
			}
			if(ind > 0 && outline.nodes.size() > 1){
				fw.write("/Prev "+outline.nodes.get(ind-1).getId()+" 0 R\r\n");	
			}
		}else{
			fw.write("/Parent "+parent.getId()+" 0 R\r\n");
			int ind = parent.child.indexOf(this);
			if(ind < parent.child.size()-1 && parent.child.size() > 1){
				fw.write("/Next "+parent.child.get(ind+1).getId()+" 0 R\r\n");	
			}
			if(ind > 0 && parent.child.size() > 1){
				fw.write("/Prev "+parent.child.get(ind-1).getId()+" 0 R\r\n");	
			}
			
		}
		if(child.size() > 0){
			fw.write("/First "+child.get(0).getId()+" 0 R\r\n");
			fw.write("/Last "+child.get(child.size()-1).getId()+" 0 R\r\n");
		}
		fw.write("/Count "+allnodenum+"\r\n");
		fw.write("/Dest ["+page.getId()+" 0 R /XYZ "+xPos+" "+yPos+" "+zPos+"]\r\n");
		
		fw.write(">>endobj\r\n\r\n\r\n");
	}///Title (TEstBookmark) /Parent 14 0 R/Dest [ 1 0 R /XYZ 0 792 0 ]
	
	/*
	<< /Title ( Section 2 )
	/Parent 22 0 R
	/Prev 25 0 R
	/Next 28 0 R
	/First 27 0 R
	/Last 27 0 R
	/Count 1
	/Dest [ 3 0 R /XYZ null 680 null ]
	>>
	endobj
	*/
}

class PDFOutlineObject extends PDFObject{
	ArrayList<PDFBookMarkNode> nodes = new ArrayList<PDFBookMarkNode>();
	
	
	public int setIds(int i){
		int idseq = i;
		this.id = idseq;
		for(int ii = 0;ii < nodes.size();ii++){
			idseq = nodes.get(ii).setIds(idseq+1);
		}
		return idseq;
	}
	
	public PDFBookMarkNode addBookMark(String t,PDFPageObject p,int x,int y,int z,PDFBookMarkNode pa){
		PDFBookMarkNode ret = new PDFBookMarkNode(t,p,x,y,z,pa,this);
		nodes.add(ret);
		return ret;
	}
	public void checkParentNodes(){
		Iterator<PDFBookMarkNode> ite = nodes.iterator();
		while(ite.hasNext()){
			PDFBookMarkNode pn = ite.next();
			if(pn.parent != null){
				ite.remove();
			}
		}
	}
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		fw.flush();
		checkParentNodes();
		ArrayList<PDFBookMarkNode> allnode = new ArrayList<PDFBookMarkNode>();
		for(int ii = 0;ii < nodes.size();ii++){
			allnode.addAll(nodes.get(ii).getAllChildren());
			allnode.add(nodes.get(ii));
		}
		//allnode.addAll(nodes);
		
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		
		fw.write("<</Count "+String.valueOf(allnode.size())+"\r\n");
		fw.write("/First "+nodes.get(0).getId()+" 0 R\r\n");
		fw.write("/Last "+nodes.get(nodes.size()-1).getId()+" 0 R\r\n");
		fw.write(">>endobj\r\n\r\n\r\n");
		for(int ii = 0;ii < allnode.size();ii++){
			allnode.get(ii).print(fw,pos,channel);
		}
		
		
	}
}
class PDFPagesObject extends PDFObject{
	ArrayList<PDFPageObject> page = new ArrayList<PDFPageObject>();
	int width = 595;
	int height = 842;
	PDFPagesObject(){
		super();
		setDefaultAttributes();
		attributes.put("/Type","/Pages");
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	
	public void setDefaultAttributes(){
		attributes.put("/MediaBox","[ 0 0 595 842 ]");//A4
	}
	public void setSize(int w,int h){
		width = w;
		height = h;
		attributes.put("/MediaBox","[ 0 0 "+String.valueOf(w)+" "+String.valueOf(h)+" ]");
	}
	public void addPage(PDFPageObject pg){
		page.add(pg);
		pg.setParent(this);
	}
	public int setIds(int i){
		int idseq = i;
		this.id = idseq;
		return idseq;
	}
	public String getAttributesString(){
		StringBuffer pg = new StringBuffer();
		pg.append("[");
		for(int ii = 0;ii < page.size();ii++){
			pg.append(page.get(ii).getId());
			pg.append(" 0 R ");
		}
		pg.append("]");
		attributes.put("/Kids",pg.toString());
		attributes.put("/Count",String.valueOf(page.size()));
		return super.getAttributesString();
	}
}
class PDFCatalogObject extends PDFObject{
	PDFPagesObject pages;
	PDFOutlineObject outline = null;
	boolean r2LFlag = false;
	boolean twoPageFlag = false;
	PDFCatalogObject(){
		super();
		attributes.put("/Type","/Catalog");
		attributes.put("/PageMode","/true");
		//attributes.put("/PageLayout","/TwoColumnRight");
		//attributes.put("/OpenAction","<</S /GoTo /D [1 0 R /Fit]>>");
		attributes.put("/PageLayout","/SinglePage");
		attributes.put("/OpenAction","[1 0 R /Fit]");
	
	}
	public void setTwoPageFlag(boolean b){
		twoPageFlag = b;
		if(b){
			if(r2LFlag){
			//	attributes.put("/PageLayout","/TwoPageRight");
			}else{
			//	attributes.put("/PageLayout","/TwoPageLeft");
			}
		}else{
			attributes.remove("/PageLayout");
			
		}
	}
	public void setR2LFlag(boolean b){
		r2LFlag = b;
		if(r2LFlag){
			attributes.put("/ViewerPreferences","<</PageDirection/R2L>>");
			if(twoPageFlag){
			//	attributes.put("/PageLayout","/TwoPageRight");
			}
		}else{
			attributes.remove("/ViewerPreferences");
			if(twoPageFlag){
			//	attributes.put("/PageLayout","/TwoPageLeft");
			}
		}
		
	}
	public void setPagesObject(PDFPagesObject pg){
		pages = pg;
	}
	public void setOutline(PDFOutlineObject out){
		if(out != null){
			attributes.put("/PageMode","/UseOutlines");
		}
		outline = out;
		attributes.remove("/PageMode");
	}
	public PDFOutlineObject getOutline(){
		if(outline != null){
			return outline;
		}
		return null;
	}
	
	
	public String getAttributesString(){
		attributes.put("/Pages",String.valueOf(pages.getId())+" 0 R");
		if(outline != null){
			attributes.put("/Outlines",String.valueOf(outline.getId())+" 0 R");
		}
		return super.getAttributesString();
	}
}
class PDFObjectGroup extends PDFObject{
	
}
class PDFFontObject extends PDFObject{
	String refCode;
	public void setRefCode(String f){
		refCode = f;
	}
	public String getRefCode(){
		return refCode;
	}
	
}
class FontWidthSet{
	int start = 0;
	int end = 0;
	int width = 0;
	FontWidthSet(int s,int e,int w){
		start = s;
		end = e;
		width = w;
		
	}
	public int getFontWidth(){
		return width;
	}
	public boolean isIn(int c){
		return (c-start)*(c-end) < 1;
	}
	public int getRegionSize(){
		return end-start+1;
	}
	
}

class FontWidthMapper{
	ArrayList<FontWidthSet> al = new ArrayList<FontWidthSet>();
	int defaultWidth = 1000;
	FontWidthMapper(int d){
		defaultWidth = d;
	}
	public int getWidth(int code){
		int rsize = -1;
		int cwid = 0;
		for(int ii = 0;ii < al.size();ii++){
			FontWidthSet fs = al.get(ii);
			if(fs.isIn(code)){
				if(rsize < 0 || fs.getRegionSize() < rsize){
					rsize = fs.getRegionSize();
					cwid = fs.getFontWidth();
				}
			}
		}
		if(rsize < 0){
			cwid = defaultWidth;
		}
		return cwid;
	}
	public int getWidth(String str){
		int ret = 0;
		for(int ii = 0;ii < str.length();ii++){
			ret += getWidth(Character.codePointAt(str,ii)+210);
		}
		return ret;
	}
	public void setDefaultWidth(int d){
		defaultWidth = d;
	}
	public void addFontWidth(int s,int e,int w){
		al.add(new FontWidthSet(s,e,w));
	}
}
class PDFFontCassette extends PDFObjectGroup{
	PDFFontObject baseFont = new PDFFontObject();
	PDFObject cidFont = new PDFObject();
	PDFObject fontDescriptor = new PDFObject();
	FontWidthMapper fontWidth = new FontWidthMapper(1000);
	int fontDescent = 0;
	PDFFontCassette(){
		
	}
	public static PDFFontCassette generateTestPattern(){
		PDFFontCassette ret = new PDFFontCassette();
		
		ret.baseFont.setAttributes("/Type","/Font");
		ret.baseFont.setAttributes("/Subtype","/Type0");
		ret.baseFont.setAttributes("/BaseFont","/MS-Gothic");
		ret.baseFont.setAttributes("/Encoding","/90ms-RKSJ-H");//shift_jis_encoding
		//ret.baseFont.setAttributes("/Encoding","/utf-16");//shift_jis_encoding
		
		ret.cidFont.setAttributes("/Type","/Font");
		ret.cidFont.setAttributes("/Subtype","/CIDFontType2");
		ret.cidFont.setAttributes("/BaseFont","/MS-Gothic");
		ret.cidFont.setAttributes("/WinCharSet","/128");
		ret.cidFont.setAttributes("/CIDSystemInfo"," <<\r\n  /Registry(Adobe)\r\n/Ordering(Japan1)\r\n/Supplement 2\r\n>>");
		ret.cidFont.setAttributes("/DW","1000");
		ret.fontWidth.setDefaultWidth(1000);
		ret.cidFont.setAttributes("/W","[ 231 389 500 631 631 500 ]");
		
		ret.fontWidth.addFontWidth(231,389,500);
		ret.fontWidth.addFontWidth(631,631,500);
		
		ret.fontDescriptor.setAttributes("/Type","/FontDescriptor");
		ret.fontDescriptor.setAttributes("/FontName","/MS-Gothic");
		ret.fontDescriptor.setAttributes("/Flags","39");
		ret.fontDescriptor.setAttributes("/FontBBox","[ -150 -147 1100 853 ]");
		ret.fontDescriptor.setAttributes("/MissingWidth","507");
		ret.fontDescriptor.setAttributes("/StemV","92");
		ret.fontDescriptor.setAttributes("/ItalicAngle","0");
		ret.fontDescriptor.setAttributes("/CapHeight","853");
		ret.fontDescriptor.setAttributes("/Ascent","853");
		ret.fontDescriptor.setAttributes("/Descent","-147");
		ret.fontDescent = -147;
		return ret;
	}
	public double getStringWidth(String str,int fsize){
		return fontWidth.getWidth(str)*fsize/1000.0;
	}
	public double getStringHeight(int fsize){
		return fontWidth.defaultWidth*fsize/1000.0;
	}
	
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		baseFont.setAttributes("/DescendantFonts","["+String.valueOf(cidFont.getId())+" 0 R]");
		cidFont.setAttributes("/FontDescriptor",""+String.valueOf(fontDescriptor.getId())+" 0 R");
		
		baseFont.print(fw,pos,channel);
		cidFont.print(fw,pos,channel);
		fontDescriptor.print(fw,pos,channel);
	}
	public int setIds(int i){
		int idseq = i;
		idseq = baseFont.setIds(idseq);
		idseq = cidFont.setIds(idseq+1);
		idseq = fontDescriptor.setIds(idseq+1);
		return idseq;
	}
}



class PDFPageObject extends PDFObject{
	PDFContentsObject contents;
	PDFResourceObject resource;
	ArrayList<PDFObject> annots = new ArrayList<PDFObject>();
	PDFPagesObject parent = null;
	int currentFontSize = 32;
	PDFFontCassette currentFontObject=null;
	PDFPageObject(PDFContentsObject c,PDFResourceObject r){
		super();
		attributes.put("/Type","/Page");
		setContents(c);
		setResource(r);
	}
	PDFPageObject(){
		super();
		attributes.put("/Type","/Page");
		setContents(new PDFContentsObject());
		setResource(new PDFResourceObject());
	}
	public void setFontSize(int size){
		setFont(currentFontObject,size);
	}
	public int getWidth(){
		return parent.getWidth();
	}
	public int getHeight(){
		return parent.getHeight();
	}
	public void saveMem(){
		
		contents.saveMem();
	}
	public void setFont(PDFFontCassette font,int size){
		if(font == null){
			
			font = PDFFontCassette.generateTestPattern();
		}
		int ref = resource.getFontIndexOf(font);
		if(ref < 0){
			resource.addFont(font);
			ref = resource.getFontIndexOf(font);
			if(ref < 0){
				System.err.println("Font appending error.");
				System.exit(1);
			}
		}
		currentFontObject = font;
		currentFontSize = size;
		contents.setFont(resource,ref,size);
	}
	public void drawText(String str,int x,int y){
		contents.drawText(str,x,y);
	}
	public void drawLinkText(String str,String url,int x,int y){
		drawText(str,x,y);
		//System.err.println(pf.getStringWidth(str,currentFontSize));
		addLink(x,y+currentFontObject.getStringHeight(currentFontSize)+currentFontObject.fontDescent*currentFontSize/1000.0
		,x+currentFontObject.getStringWidth(str,currentFontSize),y+currentFontObject.fontDescent*currentFontSize/1000.0,url);
	}
	public void addAnnots(PDFObject an){
		annots.add(an);
		
	}
	public void drawXML(String str){
		contents.drawXML(str);
		
	}
	public int setIds(int i){
		int idseq = i;
		this.id = idseq;
		if(annots.size() > 0){
			for(int ii = 0;ii < annots.size();ii++){
				idseq++;
				idseq = annots.get(ii).setIds(idseq);
			}
		}
		
		return idseq;
	}
	public void drawPostScript(PostScriptContents ps){
		contents.drawPostScript(ps);
	}
	public void drawImage(BufferedImage bi,int x,int y){
		contents.drawImage(bi,x,y);
	}
	public void addLink(double sx,double sy, double ex,double ey,String target){
		addAnnots(new PDFLinkObject(sx,sy,ex,ey,target));
	}
	public void drawImage(BufferedImage bi,int x,int y,int ctype){
		contents.drawImage(bi,x,y,ctype);
	}
	public void drawImage(BufferedImage bi,int x,int y,int w,int h,int ctype){
		contents.drawImage(bi,x,y,w,h,ctype);
	}
	
	public void drawImage(String bi,int x,int y){
		contents.drawImage(bi,x,y);
	}
	public void drawImage(String bi,int x,int y,int ctype){
		contents.drawImage(bi,x,y,ctype);
	}
	public void drawImage(String bi,int x,int y,double w,double h,int ctype){
		contents.drawImage(bi,x,y,w,h,ctype);
	}
	public void setParent(PDFPagesObject p){
		parent = p;
	}
	public void setContents(PDFContentsObject ct){
		contents = ct;
	}
	public void setResource(PDFResourceObject rc){
		resource = rc;
	}
	public PDFResourceObject getResource(){
		return resource;
	}
	public PDFContentsObject getContents(){
		return contents;
	}
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		
		fw.flush();
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		fw.write(getAttributesString());
		fw.write("endobj\r\n\r\n\r\n");
		if(annots.size() > 0){
			for(int ii = 0;ii < annots.size();ii++){
				annots.get(ii).print(fw,pos,channel);
			}
		}
	}
	public String getAttributesString(){
		attributes.put("/Parent",String.valueOf(parent.getId())+" 0 R");
		
		
		StringBuffer ct = new StringBuffer();
		ct.append(contents.getId());
		ct.append(" 0 R \r\n");
		attributes.put("/Contents",ct.toString());
		
		StringBuffer res = new StringBuffer();
		res.append(resource.getId());
		res.append(" 0 R ");
		attributes.put("/Resources",res.toString());
		
		if(annots.size() > 0){
			StringBuffer an = new StringBuffer();
			for(int ii = 0;ii < annots.size();ii++){
				an.append(annots.get(ii).getId());
				an.append(" 0 R ");
			}
			attributes. put("/Annots","["+an.toString()+"]");
		}
		return super.getAttributesString();
	}
}
class PDFResourceObject extends PDFObject{
	ArrayList<PDFFontCassette> fonts = new ArrayList<PDFFontCassette>();
	public void addFont(PDFFontCassette f){
		fonts.add(f);
		f.baseFont.setRefCode("F"+String.valueOf(fonts.size()));
	}
	public PDFFontObject getFontAt(int ii){
		return fonts.get(ii).baseFont;
	}
	public int getFontIndexOf(PDFFontCassette f){
		return fonts.indexOf(f);
	}
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		fw.flush();
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n<<\r\n");
		fw.write("/ProcSet [/PDF/Text]\r\n");
		if(fonts.size() > 0){
		fw.write("/Font <<\r\n");
		for(int ii = 0;ii < fonts.size();ii++){
			fw.write("/"+String.valueOf(fonts.get(ii).baseFont.getRefCode())+" "+String.valueOf(fonts.get(ii).baseFont.getId())+" 0 R \r\n");
		}
		fw.write(">>\r\n");
		}
		fw.write(">>\r\n");
		
		fw.write("endobj\r\n\r\n\r\n");
		
	}
	
}
class PDFInfoObject extends PDFObject{
	PDFInfoObject(){
		super();
		setCreationDate("20110101");
		setAuthor("anonymous");
		setTitle("untitled");
	}
	public void setCreationDate(String str){
		attributes.put("/CreationDate","(D:"+str+")");
	}
	public void setAuthor(String str){
		attributes.put("/Author",getUTF16Sequence(str));
	}
	public void setTitle(String str){
		attributes.put("/Title",getUTF16Sequence(str));
	}
	
}
class PDFLinkObject extends PDFObject{
	/*	4 0 obj
	<</Type/Annot/Subtype/Link/Border[0 0 0]/Rect[56 709.1 150.4 722.9]/A<</Type/Action/S/URI/URI(http://www.yahoo.co.jp/)>>
	>>
	endobj*/
	double startx = 0;
	double starty = 0;
	double endx = 0;
	double endy = 0;
	String targetURL = "";
	PDFLinkObject(double x,double y,double ex,double ey,String u){
		startx = x;
		starty = y;
		endx = ex;
		endy = ey;
		targetURL = u;
	}

	public String getAttributesString(){
		
		attributes.put("/Type","/Annot");
		attributes.put("/Subtype","/Link");
		attributes.put("/Border","[0 0 0]");
		attributes.put("/Rect","["+startx+" "+starty+" "+endx+" "+endy+"]");
		attributes.put("/A","<</Type/Action/S/URI/URI("+targetURL+")>>");
		return super.getAttributesString();
	}

}
class PDFImageXObject extends PDFObject{
	StringBuffer streamString = new StringBuffer();
	BufferedImage image;
	PDFImageXObject(BufferedImage img){
		super();
		image = img;
		attributes.put("/Type","/XObject");
		attributes.put("/Subtype","/Image");
		attributes.put("/Length","100");
		attributes.put("/Name","/img1");
		attributes.put("/BitsPerComponent","8");
		attributes.put("/ColorSpace","DeviceRGB");
		attributes.put("/Width",String.valueOf(img.getWidth()));
		attributes.put("/Height",String.valueOf(img.getHeight()));
		//attributes.put("/Filter","/Filter [/ASCII85Decode /FlateDecode]");
		attributes.put("/Filter","[/ASCII85Decode]");
		//generateFilteredString();
		generateCompressedString();
	}
	public void setRefName(String str){
		attributes.put("/Name","/"+str);
	}
	public void generateCompressedString(){
		byte[] compressed = PDFByteZipper.zip_color(image,3);
		byte b[] = new byte[4];
		int pos = 0;
		for(int ii = 0;ii < compressed.length;ii++){
			b[pos] = compressed[ii];
			pos++;
			if(pos == 4){
				streamString.append(Base85Encoder.encode4Bytes(b,4));
				pos = 0;
			}
		}
		
		if(pos != 0){
			streamString.append(Base85Encoder.encode4Bytes(b,pos));
			pos = 0;
		}
	}
	public void generateFilteredString(){
		int cc[] = new int[image.getWidth()];
		int hei = image.getHeight();
		int wid = image.getWidth();
		byte b[] = new byte[4];
		int pos = 0;
		for(int yy = 0;yy < hei;yy++){
			cc = image.getRGB(0,yy,wid,1,cc,0,wid);
			for(int xx = 0;xx < wid;xx++){
				b[pos] = (byte)((cc[xx] & 0xFF0000)/65536);
				pos++;
				if(pos == 4){
					streamString.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
				
				b[pos] = (byte)((cc[xx] & 0x00FF00)/256);
				pos++;
				if(pos == 4){
					streamString.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
				
				b[pos] = (byte)(cc[xx] & 0x0000FF);
				pos++;
				if(pos == 4){
					streamString.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
			}
		}
		if(pos > 0){
			streamString.append(Base85Encoder.encode4Bytes(b,pos));	
		}
		
	}
	
	
}
class DelayedImage{
	int x;
	int y;
	double w;
	double h;
	int ctype;
	String imagePath;
	DelayedImage(String ip,int xx,int yy,double ww,double hh,int ct){
		imagePath = ip;
		x = xx;
		y = yy;
		w = ww;
		h = hh;
		ctype = ct;
	}
}
class PDFContentsObject extends PDFObject{
	public static final int TYPE_BILINEAR = 0;
	public static final int TYPE_GRAY = 1;
	public static final int TYPE_RGB = 2;
	public static final int TYPE_AUTO = 3;
	StringBuffer buff = new StringBuffer();
	ArrayList<DelayedImage> delayedList  = new ArrayList<DelayedImage>();
	ArrayList<File> bufferedFile = new ArrayList<File>();
	public void setFont(PDFResourceObject res,int ref,int size){
		buff.append("/"+String.valueOf(res.getFontAt(ref).getRefCode())+" "+String.valueOf(size)+" Tf\r\n");
	}
	
	public static String getUTF16Sequence(String str){
		
		StringBuffer sb = new StringBuffer();
		sb.append("<FEFF");
		for(int ii = 0;ii < str.length();ii++){
			String ps = Integer.toHexString(Character.codePointAt(str,ii));
			while(ps.length() < 4){
				ps = "0"+ps;
			}
			sb.append(ps);
		}
		sb.append(">");
		return sb.toString();
	}
	public void drawText(String str,int x,int y){
		buff.append("BT\r\n");
		buff.append("1 0 0 1 "+String.valueOf(x)+" "+String.valueOf(y)+" Tm\r\n");
		//buff.append(getUTF16Sequence(str)+" Tj\r\n");
		buff.append("("+str.replaceAll("\\(","(").replaceAll("\\)",")")+") Tj\r\n");
		buff.append("ET\r\n");
		
	}
	
	public int checkColorType(BufferedImage img){
		byte[] imagedata;
		byte[] ret = null;
		int image_width = img.getWidth();
		int image_height = img.getHeight();
		
		if(image_width > 100 || image_height > 100){
			BufferedImage bi = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
			java.awt.Graphics g2 = bi.getGraphics();
			g2.drawImage(img,0,0,100,100,null);
			img = bi;
			image_width = 100;
			image_height = 100;
		}
		
		
		int[] pixelbuffer = new int[image_width];
		byte[] currentpix = new byte[4];
		
		try{
			int startpos = 0;
			for(int ii = 0;ii < image_height;ii++){
				pixelbuffer = img.getRGB(0,ii,image_width,1,pixelbuffer,0,image_width);
				int currentpos = 0;
						
				for(int jj = 0;jj < image_width;jj++){
					currentpix = PDFByteZipper.intToByte4B(pixelbuffer[jj]);
					byte cp = currentpix[3];
					if(Math.abs(cp-currentpix[2]) > 32 || Math.abs(cp-currentpix[1]) > 32){
						return TYPE_RGB;
					}
				}
			}
				
			return TYPE_GRAY;
		}catch(Exception e){
			
			e.printStackTrace();
			return TYPE_RGB;
		}
		
	}
	public String generateCompressedImageString(BufferedImage image,int ctype){
		StringBuffer streamString = new StringBuffer();
		byte[] compressed;
		
		
		if(ctype == TYPE_RGB){
			compressed  = PDFByteZipper.zip_color(image,3);
		}else{
			compressed  = PDFByteZipper.zip_gray(image,3);
		}
		byte b[] = new byte[4];
		int pos = 0;
		for(int ii = 0;ii < compressed.length;ii++){
			b[pos] = compressed[ii];
			pos++;
			if(pos == 4){
				streamString.append(Base85Encoder.encode4Bytes(b,4));
				pos = 0;
			}
		}
		
		if(pos != 0){
			streamString.append(Base85Encoder.encode4Bytes(b,pos));
			pos = 0;
		}
		return streamString.toString();
	}
	public void writeCompressedImage(BufferedImage image,int ctype,BufferedWriter bw) throws IOException{//I coudl not understand ><
		StringBuffer streamString = new StringBuffer();
		byte[] compressed;
		
		
		if(ctype == TYPE_RGB){
			compressed  = PDFByteZipper.zip_color(image,3);
		}else{
			compressed  = PDFByteZipper.zip_gray(image,3);
		}
		byte b[] = new byte[4];
		int pos = 0;
		for(int ii = 0;ii < compressed.length;ii++){
			/*b[pos] = compressed[ii];
			pos++;
			if(pos == 4){
				bw.write(Base85Encoder.encode4Bytes(b,4));
				pos = 0;
			}*/
			bw.write(compressed[ii]);
			
		}
		/*
		if(pos != 0){
			streamString.append(Base85Encoder.encode4Bytes(b,pos));
			pos = 0;
		}*/
		return;
	}
	public void drawImage(String path,int x, int y){
		drawImage(path,x,y,-1,-1,TYPE_AUTO);
	}
	public void drawImage(String path,int x, int y,int ctype){
		drawImage(path,x,y,-1,-1,ctype);
	}
	public void drawImage(String path,int x, int y,double w,double h,int ctype){
		delayedList.add(new DelayedImage(path,x,y,w,h,ctype));
	}
	public void drawImage(BufferedImage bi,int x, int y){
		drawImage(bi,x,y,bi.getWidth(),bi.getHeight(),TYPE_RGB);
	}
	public void drawImage(BufferedImage bi,int x, int y,int ctype){
		drawImage(bi,x,y,bi.getWidth(),bi.getHeight(),ctype);
	}
	public void drawImage(BufferedImage bi,int x, int y,double w,double h,int ctype){
		drawImage(bi,x,y,w,h,ctype,buff);
	}
	public void drawImage(BufferedImage bi,int x, int y,double w,double h,int ctype,StringBuffer b){
		ColorModel cm = bi.getColorModel();
		int pixsize = cm.getPixelSize();
		int colortype = ctype;
		///---------------------------------------------------------------------------
		b.append("q\r\n");
		if(w <= 0){
			w = bi.getWidth();
		}
		if(h <= 0){
			h = bi.getHeight();
		}
		
		if(colortype ==TYPE_AUTO){
			colortype = checkColorType(bi);
		}
		b.append(w+" 0 0 "+h+" "+String.valueOf(x)+" "+String.valueOf(y)+" cm\r\n");
		b.append("BI\r\n");
		b.append("/W "+bi.getWidth()+"\r\n");
		b.append("/H "+bi.getHeight()+"\r\n");
		if(colortype == TYPE_GRAY){
			b.append("/BPC 8\r\n");
			b.append("/CS /DeviceGray\r\n");
		}else{
			b.append("/BPC 8\r\n");
			b.append("/CS /RGB\r\n");
		}
		b.append("/F [/A85 /FlateDecode]\r\n");
		b.append("ID\r\n");
		b.append(generateCompressedImageString(bi,colortype));
		b.append("~>\r\n");
		b.append("EI\r\n");
		b.append("Q\r\n");
	}
	
	public void drawPostScript(PostScriptContents ps){
		buff.append(ps.getContentsString());
		buff.append("\n");
	}
	
	public void drawImage(BufferedImage bi,int x, int y,double w,double h,int ctype,BufferedWriter bw) throws IOException{
		ColorModel cm = bi.getColorModel();
		int pixsize = cm.getPixelSize();
		int colortype = ctype;
		///---------------------------------------------------------------------------
		bw.write("q\r\n");
		if(w <= 0){
			w = bi.getWidth();
		}
		if(h <= 0){
			h = bi.getHeight();
		}
		
		if(colortype ==TYPE_AUTO){
			colortype = checkColorType(bi);
		}
		bw.write(w+" 0 0 "+h+" "+String.valueOf(x)+" "+String.valueOf(y)+" cm\r\n");
		bw.write("BI\r\n");
		bw.write("/W "+bi.getWidth()+"\r\n");
		bw.write("/H "+bi.getHeight()+"\r\n");
		if(colortype == TYPE_GRAY){
			bw.write("/BPC 8\r\n");
			bw.write("/CS /DeviceGray\r\n");
		}else{
			bw.write("/BPC 8\r\n");
			bw.write("/CS /RGB\r\n");
		}
		bw.write("/F [/FlateDecode]\r\n");
		bw.write("ID\r\n");
		writeCompressedImage(bi,colortype,bw);
		bw.write("\r\n");
		bw.write("EI\r\n");
		bw.write("Q\r\n");
	}
	public void drawXML(String str){
		buff.append("/RV ");
		buff.append("("+str+")\r\n");
	}
	public void print_String(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		fw.flush();
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		fw.write("<<");
		fw.write("/Length ");
		fw.flush();
		long ppos = channel.size();
		
		//fw.write("                      \r\n /LockedContents /Locked \r\n /F [/FlateDecode]\r\n");
		fw.write("                      \r\n /LockedContents /Locked \r\n");
		fw.write(">>\r\n");
		fw.write("stream\r\n");
		fw.flush();
		long streamstart = channel.size();
		
		fw.write(buff.toString());
		
		
		if(delayedList != null){
			for(int ii = 0; ii < delayedList.size();ii++){
				DelayedImage di = delayedList.get(ii);
				StringBuffer sb = new StringBuffer();
				try{
					//drawImage(ImageIO.read(new File(di.imagePath)),di.x,di.y,di.w,di.h,di.ctype,fw);
					drawImage(ImageIO.read(new File(di.imagePath)),di.x,di.y,di.w,di.h,di.ctype,sb);
					fw.write(sb.toString());
					fw.flush();
					/*
					channel.position(channel.size());
					long spos = channel.size();
					byte[] imb = PDFByteZipper.zipme(sb.toString().getBytes("UTF-8"));
					for(long ll = 0;ll < imb.length;ll++){
						fw.write(" ");
					}
					fw.write(" ");
					fw.flush();
					channel.write(ByteBuffer.wrap(imb),spos);
					channel.force(true);*/
				}catch(Exception exx){
					exx.printStackTrace();
					
				}
			}
			
		}
		fw.flush();
		long streamend = channel.size();
		channel.position(ppos);
		channel.write(ByteBuffer.wrap(String.valueOf(streamend-streamstart).getBytes("UTF-8")),ppos);
		channel.force(false);
		channel.position(streamend);
		fw.write("endstream\r\n");
		fw.write("endobj\r\n\r\n\r\n");
	}
	private void printBytes(byte[] b,BufferedWriter fw,FileChannel channel) throws IOException{
			if(b.length == 0){
				return;
			}
			fw.flush();
			channel.position(channel.size());
			long spos = channel.size();
			for(long ll = 0;ll < b.length;ll++){
				fw.write(" ");
			}
			fw.flush();
			channel.write(ByteBuffer.wrap(b),spos);
			channel.force(true);
	}
	public void saveMem(){
		try{
			File tmpf = File.createTempFile("pdfcompiler"+id, null);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpf)))); 
			pw.print(buff);
			pw.close();
			bufferedFile.add(tmpf);
			buff = new StringBuffer();
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	
	public void print(BufferedWriter fw,Hashtable<Integer,String> pos,FileChannel channel) throws IOException{
		fw.flush();
		pos.put(this.getId(),String.format("%010d",channel.size()));
		fw.write(String.valueOf(id)+" 0 obj\r\n");
		fw.write("<<");
		fw.write("/Length ");
		fw.flush();
		long ppos = channel.size();
		
		fw.write("                      \r\n /LockedContents /Locked \r\n /Filter /FlateDecode\r\n");
		//fw.write("                      \r\n /LockedContents /Locked \r\n");
		fw.write(">>\r\n");
		fw.write("stream\r\n");
		fw.flush();
		long streamstart = channel.size();
		
		StringBuffer gsb = new StringBuffer();
		if(bufferedFile.size() > 0){
			for(File f:bufferedFile){
				if(f.exists()){
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
					String line = null;
					while((line = br.readLine()) != null){
						gsb.append(line);
						gsb.append("\n");
					}
					br.close();
					
				}else{
					System.err.println("Temp file is not found! "+f.getPath());
				}
			}
			
		}
		gsb.append(buff);
		if(gsb.length() > 0){
			printBytes(PDFByteZipper.zipme(gsb.toString().getBytes("SHIFT-JIS")),fw,channel);
			gsb = new StringBuffer();
			buff = new StringBuffer();
		}
		if(delayedList != null){
			for(int ii = 0; ii < delayedList.size();ii++){
				DelayedImage di = delayedList.get(ii);
				StringBuffer sb = new StringBuffer();
				try{
					//drawImage(ImageIO.read(new File(di.imagePath)),di.x,di.y,di.w,di.h,di.ctype,fw);
					drawImage(ImageIO.read(new File(di.imagePath)),di.x,di.y,di.w,di.h,di.ctype,sb);
					//fw.write(sb.toString());
					byte[] imb = PDFByteZipper.zipme(sb.toString().getBytes("UTF-8"));
					printBytes(imb,fw,channel);
				}catch(Exception exx){
					exx.printStackTrace();
					
				}
			}
			
		}
		fw.write("\r\n");
		fw.flush();
		long streamend = channel.size();
		channel.position(ppos);
		channel.write(ByteBuffer.wrap(String.valueOf(streamend-streamstart-1).getBytes("UTF-8")),ppos);
		channel.force(false);
		channel.position(streamend);
		fw.write("endstream\r\n");
		fw.write("endobj\r\n\r\n\r\n");
	}
	public String getAttributesString(){
		return "";
	}
	
	
	
	
}

class PDFByteZipper{
	static byte[] intToByte4B(int ii){
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
	public static byte[] zip_color(BufferedImage img,int bytesperpixel){
		byte[] imagedata;
		byte[] ret = null;
		int image_width = img.getWidth();
		int image_height = img.getHeight();
		try{
			imagedata = new byte[image_width*bytesperpixel+30];
		}catch(Exception e){
			System.err.println("can not make imagedata!");
			return null;
		}
		
		int[] pixelbuffer = new int[image_width];
		Deflater deflater = new Deflater(4);
		ByteArrayOutputStream outbytes = new ByteArrayOutputStream(1024);
		DeflaterOutputStream defoutbytes = new DeflaterOutputStream(outbytes,deflater);
		byte[] currentpix = new byte[bytesperpixel];
		
		try{
			int startpos = 0;
			for(int ii = 0;ii < image_height;ii++){
				pixelbuffer = img.getRGB(0,ii,image_width,1,pixelbuffer,0,image_width);
				int currentpos = 0;			
				for(int jj = 0;jj < image_width;jj++){
					currentpix = intToByte4B(pixelbuffer[jj]);
					for(int kk = 4-bytesperpixel;kk < 4;kk++){
						imagedata[currentpos++] = (byte)(currentpix[kk]);
					}
				}
				defoutbytes.write(imagedata, 0, currentpos);
			}
				
			defoutbytes.close();
			deflater.finish();
			ret = outbytes.toByteArray();
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	public static byte[] zipme(byte[] b){
		byte[] ret = null;
		try{
			Deflater deflater = new Deflater(4);
			ByteArrayOutputStream outbytes = new ByteArrayOutputStream(1024);
			DeflaterOutputStream defoutbytes = new DeflaterOutputStream(outbytes,deflater);
			defoutbytes.write(b, 0,b.length);
			
			defoutbytes.close();
			deflater.finish();
			ret = outbytes.toByteArray();
		}catch(Exception exx){
			exx.printStackTrace();
		}
		return ret;
	}
	public static byte[] zip_gray(BufferedImage img,int bytesperpixel){
		byte[] imagedata;
		byte[] ret = null;
		int image_width = img.getWidth();
		int image_height = img.getHeight();
		try{
			imagedata = new byte[image_width+30];
		}catch(Exception e){
			System.err.println("can not make imagedata!");
			return null;
		}
		
		int[] pixelbuffer = new int[image_width];
		Deflater deflater = new Deflater(4);
		ByteArrayOutputStream outbytes = new ByteArrayOutputStream(1024);
		DeflaterOutputStream defoutbytes = new DeflaterOutputStream(outbytes,deflater);
		byte[] currentpix = new byte[bytesperpixel];
		
		try{
			int startpos = 0;
			for(int ii = 0;ii < image_height;ii++){
				pixelbuffer = img.getRGB(0,ii,image_width,1,pixelbuffer,0,image_width);
				int currentpos = 0;			
				for(int jj = 0;jj < image_width;jj++){
					currentpix = intToByte4B(pixelbuffer[jj]);
					imagedata[currentpos++] = (byte)(currentpix[1]);
				}
				defoutbytes.write(imagedata, 0, currentpos);
			}
				
			defoutbytes.close();
			deflater.finish();
			ret = outbytes.toByteArray();
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	public static byte[] zip_back(BufferedImage img,int bytesperpixel){
		byte[] imagedata;
		byte[] ret = null;
		int image_width = img.getWidth();
		int image_height = img.getHeight();
		try{
			imagedata = new byte[image_width*image_height*bytesperpixel+image_height+30];
		}catch(Exception e){
			System.err.println("can not make imagedata!");
			return null;
		}
		
		int[] pixelbuffer = new int[image_width];
		int currentpos = 0;			
		byte[] prevpix = new byte[bytesperpixel];
		byte[] currentpix = new byte[bytesperpixel];
		Deflater deflater = new Deflater(4);
		ByteArrayOutputStream outbytes = new ByteArrayOutputStream(1024);
		DeflaterOutputStream defoutbytes = new DeflaterOutputStream(outbytes,deflater);
		
		
		try{
			prevpix =  intToByte4B(0);
			for(int ii = 0;ii < image_height;ii++){
				pixelbuffer = img.getRGB(0,ii,image_width,1,pixelbuffer,0,image_width);
				for(int jj = 0;jj < image_width;jj++){
					currentpix = intToByte4B(pixelbuffer[jj]);
					for(int kk = 4-bytesperpixel;kk < 4;kk++){
						imagedata[currentpos++] = (byte)(currentpix[kk]);
					}
					prevpix =  intToByte4B(pixelbuffer[jj]);
				}
					
			}
				
			defoutbytes.write(imagedata, 0, currentpos);
			defoutbytes.close();
			deflater.finish();
			ret = outbytes.toByteArray();
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}
	

}
class PostScriptContents{
	Color strokeColor = null;
	Color fillColor = null;
	Path2D path = null;
	int baseWidth = 1;
	int baseHeight = 1;
	double pageHeight = 0;
	Point basePoint = new Point(0,0);
	BasicStroke stroke = new BasicStroke(2.0f);
	PostScriptContents(Path2D s,Color fc,Color sc,double ph){
		fillColor = fc;
		path = s;
		strokeColor = sc;
		pageHeight = ph;
	}
	public void setStroke(BasicStroke b){
		stroke = b;
	}
	public StringBuffer getContentsString(){
		StringBuffer ret = new StringBuffer();
		ret.append("q\n");
		ret.append(baseWidth+" 0 0 "+baseHeight+" "+basePoint.x+" "+String.valueOf(basePoint.y)+" cm\n");
		if(fillColor != null){
			ret.append((fillColor.getRed()/255.0)+" "+(fillColor.getGreen()/255.0)+" "+(fillColor.getBlue()/255.0)+" rg\n");
		}
		if(strokeColor != null){
			ret.append((strokeColor.getRed()/255.0)+" "+(strokeColor.getGreen()/255.0)+" "+(strokeColor.getBlue()/255.0)+" RG\n");
			ret.append(getStrokeCode(stroke));
		}
		ret.append(getShapeCode(path));
		ret.append(getDrawingCode(path,fillColor != null,strokeColor != null));
		return ret;
		
	}
	
	
	public static StringBuffer getStrokeCode(BasicStroke b){
		StringBuffer ret = new StringBuffer();
		ret.append(b.getLineWidth()+" w\n");
		switch(b.getEndCap()){
		 case BasicStroke.CAP_BUTT:
		 ret.append("0 J\n");
		 break;
		 case BasicStroke.CAP_ROUND:
		 ret.append("1 J\n");
		 break;
		 case BasicStroke.CAP_SQUARE:
		 ret.append("2 J\n");
		 break;
		}
		switch(b.getLineJoin()){
		 case BasicStroke.JOIN_BEVEL :
		 ret.append("2 j\n");
		 break;
		 case BasicStroke.JOIN_MITER:
		 ret.append("0 j\n");
		 ret.append(b.getMiterLimit()+" M\n");
		 break;
		 case BasicStroke.JOIN_ROUND:
		 ret.append("1 j\n");
		 break;
		}
		return ret;
	
	}
	public static StringBuffer getDrawingCode(Path2D p,boolean fillflag,boolean strokeflag){
		StringBuffer ret = new StringBuffer();
		if(p.getWindingRule() == Path2D.WIND_NON_ZERO){
			if(fillflag){
				if(strokeflag){
					ret.append("b\n");
					
				}else{
					ret.append("f\n");
				}
			}
		}else{
			if(fillflag){
				if(strokeflag){
					ret.append("b*\n");
				}else{
					ret.append("f*\n");
				}
			}
		}
		if(strokeflag && !fillflag){
			ret.append("S\n");
		}
		return ret;
	}
	public StringBuffer getShapeCode(Shape ss){
		return getShapeCode(ss,pageHeight);
	}
	
	public static String ftos(float f){
		String ret = String.valueOf(f);
		if(ret.indexOf("E") > -1 || ret.indexOf("e") > -1){
			ret = String.valueOf((long)(f));
		}
		return ret;
	}
	public static StringBuffer getShapeCode(Shape ss,double pd){
		float ph = (float)pd;
		StringBuffer ret = new StringBuffer();
		
		if(ss == null)return ret;
		
		AffineTransform af = new AffineTransform();
		PathIterator pite = ss.getPathIterator(af);
		float dd[] = new float[6];
		pite.currentSegment(dd);
		ret.append(ftos(dd[0])+" "+ftos(ph-dd[1])+" m\n");
		Point2D.Float lastpoint = new Point2D.Float(dd[0],dd[1]);
		pite.next();
		boolean nflag=true;
		while(!pite.isDone()){
			int typ = pite.currentSegment(dd);
			switch(typ){
				case PathIterator.SEG_CLOSE:
				{
					
					ret.append("h\n");
				}
				break;
				case PathIterator.SEG_CUBICTO:{
					ret.append(ftos(dd[0])+" "+ftos(ph-dd[1])+" "+ftos(dd[2])+" "+ftos(ph-dd[3])+" "+ftos(dd[4])+" "+ftos(ph-dd[5])+" c\n");
					lastpoint.x = dd[4];
					lastpoint.y = dd[5];
					
					nflag = false;
				}
				break;
				
				case PathIterator.SEG_LINETO:{
					ret.append(ftos(dd[0])+" "+ftos(ph-dd[1])+" l\n");
					lastpoint.x = dd[0];
					lastpoint.y = dd[1];
					nflag = false;
				}
				break;
				
				case PathIterator.SEG_MOVETO:
				{	
					ret.append(ftos(dd[0])+" "+ftos(ph-dd[1])+" m\n");
					lastpoint.x = dd[0];
					lastpoint.y = dd[1];
					nflag = true;
				}
				break;
				
				case PathIterator.SEG_QUADTO:{
					
					float d0 = (dd[0]*2+lastpoint.x)/3.0f;
					float d1 = (dd[1]*2+lastpoint.y)/3.0f;
					float d2 = (dd[0]*2+dd[2])/3.0f;
					float d3 = (dd[1]*2+dd[3])/3.0f;
					float d4 = dd[2];
					float d5 = dd[3];
					
					ret.append(ftos(d0)+" "+ftos(ph-d1)+" "+ftos(d2)+" "+ftos(ph-d3)+" "+ftos(d4)+" "+ftos(ph-d5)+" c\n");
					
					lastpoint.x = d4;
					lastpoint.y = d5;
					nflag = false;
				}
				break;
				
				default:
				
				System.err.println("exception in Font calculation. Undefined Method. ");
				break;
				
			}
			pite.next();
		}
		return ret;
	}
	
	public static GeneralPath makeTextPath(String text,Font rFont,Point2D.Double basePoint){
		return makeTextPath(text,rFont,basePoint,false,false);
	}
	public static double getTextWidth(String str,Font rFont){
		FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
		GlyphVector gv = rFont.createGlyphVector(frc,str);
		Shape s = gv.getOutline(0,0);
		return s.getBounds2D().getWidth();
	}
	public static GeneralPath makeTextPath(String text,Font rFont,Point2D.Double basePoint,boolean rotate90,boolean vertical){
		FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
		String strarray[] = text.split("[\r\n]");
		int jump = rFont.getSize();
		GeneralPath outline = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		outline.reset();
		for(int ii = 0;ii < strarray.length;ii++){
			if(vertical){
				char[] kss = strarray[ii].toCharArray();
				for(int jj = 0;jj < kss.length;jj++){
					GlyphVector gv = rFont.createGlyphVector(frc,String.valueOf(kss[jj]));

					Shape s = gv.getOutline(0,0);
					double wid = s.getBounds().width;
					double x = s.getBounds().x;
					AffineTransform af = new AffineTransform(1,0,0,1,basePoint.getX()-ii*jump*1.5f-wid/2-x,basePoint.getY()+jj*jump);
					
					Shape ss = af.createTransformedShape(s);
					//Shape ss = gv.getOutline((float)basePoint.getModiX()-ii*jump,(float)basePoint.getModiY()+jj*jump);
					
					outline.append(ss,false);
				}
				
			}else{
				
				GlyphVector gv = rFont.createGlyphVector(frc,strarray[ii]);
				Shape s = gv.getOutline(0,0);
				if(rotate90){
					AffineTransform af = new AffineTransform(0,1,-1,0,(float)basePoint.getX()-ii*jump,(float)basePoint.getY());
					s =  af.createTransformedShape(s);
				}else{
					AffineTransform af = new AffineTransform(1,0,0,1,(float)basePoint.getX(),(float)basePoint.getY()+ii*jump);
					s =  af.createTransformedShape(s);
				}
				outline.append(s,false);
			}
		}
		return outline;
	}
	
	
	
}