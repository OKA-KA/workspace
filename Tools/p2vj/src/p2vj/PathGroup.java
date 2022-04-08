package p2vj;

import java.awt.*;
import java.util.ArrayList;
import java.awt.geom.*;

public class PathGroup extends PolycoItem{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	boolean openFlag = true;
	static final int pointSize = 6;
	static final BasicStroke boneStroke = new BasicStroke(1.0f);
	ArrayList<PathChain> chainList = new ArrayList<PathChain>();
	ArrayList<PathChain> selectedList = new ArrayList<PathChain>();
	LAnchorPoint clickedPoint = null;
	LAnchorPoint floatingPoint = null;
	PathChain currentChain = null;
	PathChain floatingChain = null;
	boolean refreshFlag = false;
	PathGroup(){
		super();
		name = "Path No."+String.valueOf(number);
		classCode = CODE_PATH;
		setName(name);
		bStroke = new BasicStroke(1.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f);
	}
	PathGroup(PathGroup pi){
		this(pi,false,false);
	}
	PathGroup(PathGroup pi,boolean historyflag){
		this(pi,historyflag,false);
	}
	PathGroup(PathGroup pg,boolean historyflag,boolean selection){
		copyFrom(pg,historyflag,selection);
		setName(name);
		
	}
	public PolycoItem getCopy(){
		PathGroup ret = new PathGroup();
		ret.copyFrom(this,false,false);
		return ret;
	}
	public double getLength(){
		double ret = 0.0;
		for(int ii = 0;ii < chainList.size();ii++){
			ret += chainList.get(ii).getLength();
		}
		return ret;
	}
	public void copyFrom(PathGroup pg,boolean historyflag,boolean selection){
		super.copyFrom(pg,historyflag);
		windingRule = pg.windingRule;
		classCode = CODE_PATH;
		chainList.clear();
		for(int ii = 0;ii < pg.chainList.size();ii++){
			PathChain pic = pg.chainList.get(ii);
			if(!selection || (selection && pg.selectedList.contains(pic))){
				PathChain newpc = new PathChain(pic);
				newpc.refreshPath();
				chainList.add(newpc);
			}
		}
		makePathLine();
		
	}
	
	
	public ArrayList<LAnchorPoint> getAllPoints_L(){
		ArrayList<LAnchorPoint> ret = new ArrayList<LAnchorPoint>();
		for(int ii = 0;ii < chainList.size();ii++){
			ret.addAll(chainList.get(ii).getAllPoints_L());
		}
		return ret;
	}
	
	
	
	public ArrayList<FlexPoint> getAllPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		for(int ii = 0;ii < chainList.size();ii++){
			ret.addAll(chainList.get(ii).getAllPoints());
		}
		return ret;
	}
	public ArrayList<FlexPoint> getSelectedPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		for(int ii = 0;ii < selectedList.size();ii++){
			ret.addAll(selectedList.get(ii).getSelectedPoints());
		}
		return ret;
	}
	public void transPoints(AffineTransform af){
		for(int ii = 0;ii < chainList.size();ii++){
			ArrayList<FlexPoint> al = chainList.get(ii).getAllPoints();
			for(int jj = 0;jj < al.size();jj++){
				FlexPoint fx = al.get(jj);
				Point2D.Double src = new Point2D.Double(fx.x,fx.y);
				Point2D.Double dest = new Point2D.Double(fx.x,fx.y);
				Point2D dd = af.transform(src,dest);
				fx.set(dd.getX(),dd.getY());
			}
			chainList.get(ii).refreshAll();
		}
		makePathLine();
		if(gradation != null){
			gradation.transPoints(af);
		}
	}
	public void screwTo(double mx,double my){
		if(clickedPoint != null){
			clickedPoint.screwTo(mx,my);
		}
	}
	public void screwTo(FlexPoint fp){
		screwTo(fp.x,fp.y);
	}
	
	public boolean selectAll(){
		selectedList.clear();
		for(int ii = 0;ii < chainList.size();ii++){
			chainList.get(ii).selectAll();
			selectedList.add(chainList.get(ii));
		}
		return true;
	}
	public boolean selectAll(Shape ss){
		boolean ret = select(ss);
		if(ret){
			selectAll();
		}
		return ret;
	}
	public boolean deleteSelected(){
		for(int ii = 0;ii < selectedList.size();ii++){
			PathChain pc = selectedList.get(ii);
			chainList.remove(pc);
		}
		currentChain = null;
		clickedPoint = null;
		makePathLine();
		if(chainList.size() == 0){
			deadFlag = true;
			return true;
		}
		return false;
	}
	public boolean select(Shape ss){
		boolean ret = false;
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			if(pc == currentChain){
				pc.makePointShape();
			}
			if(selectedList.indexOf(pc) == -1){
				if(pc.select(ss)){
					selectedList.add(pc);
					ret = true;
				}
			}
			
		}
		return ret;
	}
	public boolean deselectAll(Shape ss){
		boolean ret = false;
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			if(pc == currentChain){
				pc.makePointShape();
			}
			if(selectedList.indexOf(pc) != -1){
				if(pc.pointIn(ss)){
					ret = true;
					break;
				}
			}
			
		}
		if(ret == true){
			deselectAll();
		}
		return ret;
	}
	public boolean deselectAll(){
		for(int ii = 0;ii < chainList.size();ii++){
			chainList.get(ii).deselectAll();
		}
		selectedList.clear();
		return true;
	}
	
	public boolean deselect(Shape ss){
		
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			if(pc == currentChain){
				pc.makePointShape();
			}
			if(selectedList.indexOf(pc) != -1){
				if(pc.deselect(ss)){
					selectedList.remove(pc);
				}
			}
			
		}
		
		
		if(selectedList.size() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public void paint(Graphics2D g2){
		g2.setStroke(bStroke);
		if(refreshFlag){
			makePathLine();
			refreshFlag = false;
		}
		if(fillFlag){
			if(gradation != null){
				g2.setPaint(gradation.getGradation());
			}else{
				g2.setColor(fillColor);
			}
			g2.fill(outline);
		}
		if(drawFlag){
			g2.setColor(drawColor);
			g2.draw(outline);
		}
	}
	
	public void paintBone(Graphics2D g2){
		g2.setColor(Color.blue);
		g2.setStroke(boneStroke);
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			if(pc == currentChain){
				pc.paintBone_E(g2);
				if(floatingPoint != null){
					g2.setColor(Color.red);
					floatingPoint.paintBoneLine(g2,floatingPoint.lastFloat);
					
					g2.setColor(Color.blue);
				}
			}else{
				pc.paintBone(g2);
			}
		}
	}
	public void paintSelectedBone(Graphics2D g2){
		g2.setStroke(boneStroke);
		for(int ii = 0;ii < selectedList.size();ii++){
			PathChain pc = selectedList.get(ii);
			if(pc == currentChain){
				pc.paintBone_E(g2);
			}else{
				pc.paintBone(g2);
			}
		}
	}
	public void makeSelectedPathLine(){
		
		makePathLine(outline_Selected,selectedList);
	}
	
	public void makePathLine(){
		makePathLine(outline,chainList);
		
	}
	public void reverseChain(){
		currentChain.reverseChain();
	}
	public void reverseChain(int ii){
		chainList.get(ii).reverseChain();
	}
	public PathChain getChain(int ii){
		return chainList.get(ii);
	}/*
	public PathGroup makeBoldPath(){
		FlexPoint.setModiValues(1.0,0,0);
		adjustZoom();
		return LAnchorBird.changeShapeToPath(trueStroke.createStrokedShape(outline));
	}
	public PathGroup makeBoldPath(BasicStroke bs){
		FlexPoint.setModiValues(1.0,0,0);
		adjustZoom();
		return LAnchorBird.changeShapeToPath(bs.createStrokedShape(outline));
	}*/
	public void makePathLine(GeneralPath gp,ArrayList<PathChain> al){
		
		gp.reset();
		for(int ii = 0;ii < al.size();ii++){
			PathChain pc = al.get(ii);
			if(pc == currentChain){
				pc.refreshAll();
			}
			gp.append(pc.bone,false);
		}
	}
	public void refreshAll(){
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			pc.refreshAll();
		}
	}
	
	public LAnchorPoint intercept(double xx,double yy){
		Rectangle2D.Double modirect = new Rectangle2D.Double(FlexPoint.getModiX(xx)-pointSize/2
		,FlexPoint.getModiY(yy)-pointSize/2
		,pointSize
		,pointSize);
		
		LAnchorPath lap = null;
		if(currentChain != null)lap = currentChain.checkPath_modi_E(modirect);
		if(lap == null && floatingChain != null){
			lap = floatingChain.checkPath_modi_E(modirect);
			setCurrentChain(floatingChain);
		}
		if(lap != null){
			LAnchorPoint newlapoi = LAnchorPoint.getIntraPoint(xx,yy,lap.prev,lap.next);
			if(newlapoi != null){
				clickedPoint = newlapoi;
				currentChain.makePointShape();
				LAnchorPoint ret = currentChain.addIntraPoint(lap.prev,newlapoi,lap.next);
				ret.makeLine();
				return ret;
			}
		}
		
		return null;
	}
	
	
	
	
	public LAnchorPath checkPath_modi_F(double xx,double yy){
		
		Rectangle2D.Double rect = new Rectangle2D.Double(xx-pointSize/2,yy-pointSize/2,pointSize,pointSize);
		for(int ii = 0;ii < chainList.size();ii++){
			PathChain pc = chainList.get(ii);
			LAnchorPath lp = null;
			if(pc == currentChain){
				lp = pc.checkPath_modi_E(rect);
				if(lp != null)return lp;
			}else if((lp = pc.checkPath_modi(rect)) != null){
				return lp;
			}
		}
		return null;
	}
	public LAnchorPoint addPoint(LAnchorPoint newpo){
		return addPoint(newpo,false);
	}
	public LAnchorPoint addPoint(LAnchorPoint newpo,boolean newchainflag){
		if(newpo == null){
			return null;
		}
		if(clickedPoint == null || newchainflag || currentChain == null){
			
			setCurrentChain(new PathChain(newpo));
			chainList.add(currentChain);
			clickedPoint = newpo;
		}else{
			if(clickedPoint.next == null || clickedPoint.next == newpo){
				clickedPoint = currentChain.addPoint(newpo,true);
			}else if(clickedPoint.prev == null || clickedPoint.prev == newpo){
				//clickedPoint = currentChain.addPoint(newpo,false);
				currentChain.reverseChain();
				clickedPoint = currentChain.addPoint(newpo,true);
			}else{
				setCurrentChain(new PathChain(newpo));
				clickedPoint = newpo;
				chainList.add(currentChain);
			}
		}
		//currentChain.makePointShape();
		refreshFlag = true;
		return newpo;
	}
	public LAnchorPoint addPoint(double xx,double yy,boolean newchainflag){
		LAnchorPoint newpo = new LAnchorPoint(xx,yy);
		return addPoint(newpo,newchainflag);
	}
	public LAnchorPoint addPoint(double xx,double yy){
		LAnchorPoint newpo = new LAnchorPoint(xx,yy);
		return addPoint(newpo,false);
	}
	public void adjustZoom(){
		super.adjustZoom();
		for(int ii = 0;ii < chainList.size();ii++){
			chainList.get(ii).adjustZoom();
			
		}
		refreshAll();
		makePathLine();
		
	}
	public void moveAll(double xx,double yy){
		super.moveAll(xx,yy);
		for(int ii = 0;ii < chainList.size();ii++){
			chainList.get(ii).moveAll(xx,yy);
		}
		makePathLine();
	}
	public int deletePoint(double xx,double yy){
		int knum = -1;
		if(checkPoint(xx,yy) != null){
			knum = currentChain.deletePoint(clickedPoint,xx,yy);
			if(currentChain.start == null){
				PathChain ppc = currentChain;
				floatingChain = null;
				currentChain = null;
				chainList.remove(ppc);
			}else{}
			if(knum == LAnchorPoint.MAIN){
				clickedPoint = null;
			}
		}
		refreshFlag = true;
		return knum;
	}
	public void connect(){
		connect(clickedPoint,floatingPoint);
	}
	public void closePath(){
		currentChain.closePath();
	}
	public void connect(LAnchorPoint nc,LAnchorPoint fc){
		if(nc != null && fc != null && nc != fc){
			if(nc.next == null && fc.prev == null){
				nc.setNext(fc);
				if(currentChain != floatingChain){
					chainList.remove(floatingChain);
				}
			}else if(fc.next == null && nc.prev == null){
				fc.setNext(nc);
				if(currentChain != floatingChain){
					chainList.remove(currentChain);
					setCurrentChain(floatingChain);
				}
			}else if(fc.next == null && nc.next == null){
				currentChain.reverseChain();
				fc.setNext(nc);
				if(currentChain != floatingChain){
					chainList.remove(currentChain);
					setCurrentChain(floatingChain);
				}
			}else if(fc.prev == null && nc.prev == null){
				currentChain.reverseChain();
				floatingChain.reverseChain();
				fc.setNext(nc);
				
				if(currentChain != floatingChain){
					chainList.remove(currentChain);
					setCurrentChain(floatingChain);
				}
			}
			
			
			floatingChain = null;
			
			currentChain.refreshAll();
			makePathLine();
		}
	}
	public LAnchorPoint checkPoint(double xx,double yy){
		return checkPoint(xx,yy,false);
	}
	
	public void removeChains(ArrayList<PathChain> al){
		for(int ii = 0;ii < al.size();ii++){
			chainList.remove(al.get(ii));
		}
		setCurrentChain(null);
	}
	public void removeSelectedChains(){
		removeChains(selectedList);
	}
	public void removeChain(PathChain pc){
		chainList.remove(pc);
		setCurrentChain(null);
	}
	public void removeAllChains(){
		chainList.clear();
		setCurrentChain(null);
	}
	public void removeCurrentChain(){
		chainList.remove(currentChain);
		setCurrentChain(null);
	}
	public void deletePath_M(double xx,double yy){
		Rectangle2D.Double rect = new Rectangle2D.Double(xx-pointSize/2,yy-pointSize/2,pointSize,pointSize);
		PathChain dchain = currentChain;
		LAnchorPath la = null;
		if(currentChain != null)la = currentChain.checkPath_modi_E(rect);
		if(la == null && floatingChain != null){
			la = floatingChain.checkPath_modi_E(rect);
			setCurrentChain(floatingChain);
		}
		if(la == null){
			for(int ii = 0;ii < chainList.size();ii++){
				PathChain pc = chainList.get(ii);
				if(pc != currentChain){
					la = pc.checkPath_modi(rect);
					if(la != null){
						dchain = pc;
						break;
					}
				}
			}
		}
		
		if(la != null){
			LAnchorPoint nex = la.next;
			LAnchorPoint pre = la.prev;
			if(dchain.start.prev != null){
				dchain.start = nex;
				dchain.last = pre;
				pre.setNext(null);
			}else{
				pre.setNext(null);
				PathChain npc = new PathChain(nex);
				npc.refreshAll();
				chainList.add(npc);
			}
		}
		if(dchain != null)dchain.refreshAll();
		setCurrentChain(null);
		clickedPoint = null;
		refreshFlag = true;
		return ;
	}
	
	
	public void movePointTo(double mx,double my){
		if(clickedPoint != null){
			clickedPoint.movePoint(mx,my);
		}
	}
	public void movePointTo(FlexPoint fp){
		movePointTo(fp.x,fp.y);
	}
	
	public LAnchorPoint checkClickedPoint(){
		if(currentChain != null){
			currentChain.refreshAll();
			clickedPoint = currentChain.last;
		}
		return clickedPoint;
	}
	
	public void setCurrentChain(PathChain pc){
		if(currentChain != null && currentChain != pc)currentChain.makePointShape();
		currentChain = pc;
	}
	public void addChain(PathChain pc){
		chainList.add(pc);
	}
	public void addChains(ArrayList<PathChain> al){
		if(al == chainList)return;
		for(int ii = 0;ii < al.size();ii++){
			chainList.add(al.get(ii));
		}
	}
	public void curveCheck(double xx,double yy){
		if(clickedPoint != null){
			clickedPoint.curveCheck(xx,yy);
		}
	}
	public LAnchorPoint checkPoint(double xx,double yy,boolean floatingflag){
		LAnchorPoint ret = null;
		
		if(currentChain != null && (ret = currentChain.checkPoint_E(xx,yy,floatingflag)) != null){
			if(floatingflag){
				floatingChain = currentChain;
				floatingPoint = ret;
			}else{
				floatingChain = currentChain;
				clickedPoint = ret;
				floatingPoint = ret;
			}
			return ret;
		}else{
			for(int ii = 0;ii < chainList.size();ii++){
				PathChain pc = chainList.get(ii);
				if(pc != currentChain){
					if((ret = pc.checkPoint(xx,yy,floatingflag)) != null){
						if(!floatingflag){
							clickedPoint = ret;
							floatingPoint = ret;
							setCurrentChain(pc);
							floatingChain = pc;
						}else{
							floatingChain = pc;
							floatingPoint = ret;
						}
						return ret;
					}
				}
			}
		}
		return null;
	}

	public String toString(){
		return listHtml;
	}/*
	public static void main(String args[]){
		PolycoCanvas pc = new PolycoCanvas();
		PPLayer pl = new PPLayer(pc,false);
		pc.addLayer(pl);
		PathGroup pg = new PathGroup();
		pg.setFlags(true,false);
		LAnchorPoint lp1 = new LAnchorPoint(200,200);
		lp1.screwTo(220,200);
		LAnchorPoint lp2 = new LAnchorPoint(200,100);
		lp2.screwTo(220,100);
		pg.addPoint(lp1);
		pg.addPoint(lp2);
		
		LAnchorPoint lp3 = new LAnchorPoint(400,600);
		lp3.screwTo(400,620);
		LAnchorPoint lp4 = new LAnchorPoint(600,400);
		lp4.screwTo(620,400);
		pg.addPoint(lp3);
		pg.addPoint(lp4);
		pg.closePath();
		
		
		pl.addItem(pg);
		
		PathGroup pg2 = pg.makeBoldPath(new BasicStroke(20.0f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));
		pg2.setColors(new Color(128,128,128,128),new Color(128,128,128,128));
		pl.addItem(pg2);
		
		
		StringBuffer sb = SVGParser.makeSVGData(pc);
		new SimpleTextFrame("test.svg",sb.toString());
		
	}*/
}
class PathChain{
	LAnchorPoint start = null;
	LAnchorPoint last = null;
	ArrayList<LAnchorPath> pathList = new ArrayList<LAnchorPath>();
	GeneralPath bone = new GeneralPath();
	GeneralPath pointShape = new GeneralPath(GeneralPath.WIND_NON_ZERO);
	PathChain(LAnchorPoint la){
		start = la;
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		while(now != null){
			last = now;
			now = now.next;
			if(now == null || now == start){
				break;
			}
			next = now.next;
			
		}
		
	}
	PathChain(PathChain pc){
		start = new LAnchorPoint(pc.start);
		LAnchorPoint pnow = pc.start;
		LAnchorPoint pnext = pc.start.next;
		LAnchorPoint now = start;
		LAnchorPoint next = null;
		while(pnext != null){
			if(pnext != pc.start){
				next = new LAnchorPoint(pnext);
			}else{
				next = start;
			}
			
			last = now;
			now.setNext(next);
			now = next;
			pnow = pnext;
			if(pnow == pc.start){
				break;
			}
			pnext = pnow.next;
			next = null;
		}
		refreshPath();
		
	}
	public void closePath(){
		last.setNext(start);
		refreshAll();
	}
	public void meltWith(PathChain pc){
		if(last.meltWith(pc.start) != null){
			last = pc.last;
		}
	}
	public LAnchorPoint getLastPoint(){
		if(last == start || last.next == start){
			return last;
		}else if(last.next != null){
			refreshAll();
		}
		return last;
	}
	public void adjustZoom(){
		LAnchorPoint now = start;
		while(now != null){
			now.adjustZoom();
			now = now.next;
			if(now == start){
				break;
			}
		}
	}
	public void refreshAll(){
		LAnchorPoint now = start;
		while(now != null){
			last = now;
			now = now.next;
			if(now == start){
				break;
			}
		}
		refreshPath();
	}
	
	public boolean pointIn(Shape ss){
		if(ss == null)return false;
		LAnchorPoint now = start;
		while(now != null){
			if(ss.contains(now.shapePoint[LAnchorPoint.MAIN].modix,now.shapePoint[LAnchorPoint.MAIN].modiy)){
				return true;
			}
			now = now.next;
			if(now == start){
				break;
			}else{
				
			}
		}
		return false;
	}
	public ArrayList<FlexPoint> getAllPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		LAnchorPoint now = start;
		while(now != null){
			ret.add(now.shapePoint[0]);
			ret.add(now.shapePoint[1]);
			ret.add(now.shapePoint[2]);
			now = now.next;
			if(now == start){
				break;
			}else{
			}
		}
		
		return ret;
	}
	public ArrayList<LAnchorPoint> getAllPoints_L(){
		ArrayList<LAnchorPoint> ret = new ArrayList<LAnchorPoint>();
		LAnchorPoint now = start;
		while(now != null){
			ret.add(now);
			now = now.next;
			if(now == start){
				break;
			}else{
			}
		}
		
		return ret;
	}
	public ArrayList<FlexPoint> getSelectedPoints(){
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		LAnchorPoint now = start;
		while(now != null){
			if(now.isSelected()){
				ret.add(now.shapePoint[0]);
				ret.add(now.shapePoint[1]);
				ret.add(now.shapePoint[2]);
			}
			now = now.next;
			if(now == start){
				break;
			}else{
			}
		}
		
		return ret;
	}
	public ArrayList<FlexPoint> getInsidePoints(Shape ss){
		LAnchorPoint now = start;
		ArrayList<FlexPoint> ret = new ArrayList<FlexPoint>();
		while(now != null){
			if(ss.contains(now.shapePoint[LAnchorPoint.MAIN].modix,now.shapePoint[LAnchorPoint.MAIN].modiy)){
				ret.add(now.shapePoint[LAnchorPoint.MAIN]);
				ret.add(now.shapePoint[LAnchorPoint.NEXT]);
				ret.add(now.shapePoint[LAnchorPoint.PREV]);
			}
			now = now.next;
			if(now == start){
				break;
			}else{
				
			}
		}
		return ret;
	}
	
	public boolean deselect(Shape ss){
		return setSelected(ss,false);
	}
	public boolean setSelected(Shape ss,boolean f){
		LAnchorPoint now = start;
		boolean ret = false;
		while(now != null){
			if(ss.contains(now.shapePoint[LAnchorPoint.MAIN].modix,now.shapePoint[LAnchorPoint.MAIN].modiy)){
				now.setSelected(f);
			}
			if(now.isSelected()){
				ret = f;//deselectのときセレクトされていたのがあるとfalse、selectのときはtrue
			}
			now = now.next;
			if(now == start){
				break;
			}else{
				
			}
		}
		return ret;
	}
	public boolean select(Shape ss){
		return setSelected(ss,true);
	}
	public void selectAll(){
		LAnchorPoint now = start;
		while(now != null){
			now.select();
			now = now.next;
			if(now == start){
				break;
			}else{
			}
		}
	}
	public void deselectAll(){
		LAnchorPoint now = start;
		while(now != null){
			now.deselect();
			now = now.next;
			if(now == start){
				break;
			}else{
			}
		}
	}
	public void reverseChain(){
		LAnchorPoint now = start;
		while(now != null){
			last = now;
			now.reverse();
			now = now.prev;
			if(now == start){
				break;
			}else{
				
			}
		}
		
		
		LAnchorPoint tmp = last;
		last = start;
		start = tmp;
		
		refreshPath();
		
	}
	public LAnchorPoint addPoint(double xx,double yy,boolean lastflag){
		return addPoint(new LAnchorPoint(xx,yy),lastflag);
	}
	public LAnchorPoint addPoint(LAnchorPoint lap,boolean lastflag){
		if(start != null){
			if(lastflag){
				last.setNext(lap);
				last = lap;
				pathList.add(new LAnchorPath(last.prev,last));
			}else{
				lap.setNext(start);
				start = lap;
				pathList.add(new LAnchorPath(start,start.next));
			
			}
		}else{
			start = lap;
		}
		return lap;
	}
	public void paintBone(Graphics2D g2){
		g2.draw(bone);
		g2.fill(pointShape);
	}
	public void paintBone_E(Graphics2D g2){
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		while(now != null){
			now.paintBone(g2);
			now.paintBoneLine(g2);
			if(now.p_next != null){
				now.p_next.paintBone(g2);
			}
			now = now.next;
			if(now == null || now == start){
				break;
			}
			next = now.next;
			
		}
	}
	public void moveAll(double xx,double yy){
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		while(now != null){
			now.rMoveTo(xx,yy);
			now = now.next;
			if(now == null || now == start){
				break;
			}
			next = now.next;
		}
	}
	public int deletePoint(LAnchorPoint lap,double xx,double yy){
		int knum = -1;
		if((knum = lap.checkPoint(xx,yy)) == LAnchorPoint.MAIN){
			if(lap == start){
				start = lap.next;
				if(start != null)pathList.remove(lap.p_next);
				lap.setNext(null);
			}else if(lap == last){
				last = last.prev;
				if(last != null)pathList.remove(lap.p_prev);
				if(last != null)last.setNext(null);
			}else{
				pathList.remove(lap.p_prev);
				pathList.remove(lap.p_next);
				pathList.add(new LAnchorPath(lap.prev,lap.next));
			}
		}else{
			lap.deletePoint(knum);
			lap.makeLine();
		}
		return knum;
	}
	public LAnchorPoint addIntraPoint(LAnchorPoint prev,LAnchorPoint newlap,LAnchorPoint next){
		pathList.remove(prev.p_next);
		pathList.add(new LAnchorPath(prev,newlap));
		pathList.add(new LAnchorPath(newlap,next));
		return newlap;
	}
	public void refreshPath(){
		pathList.clear();
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		while(next != null){
			pathList.add(new LAnchorPath(now,next));
			
			now = next;
			last = now;
			if(now == start){
				break;
			}
			next = now.next;
		}
		makePointShape();
	}
	public LAnchorPoint checkPoint_E(double xx, double yy,boolean floatingflag){
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		while(now != null){
			if(now.checkPoint(xx,yy,floatingflag) > -1){
				
				return now;
			}
			now = now.next;
			if(now == null || now == start){
				break;
			}
			next = now.next;
		}
		return null;
	}
	public LAnchorPath checkPath_modi_E(Rectangle2D rect){
		for(int ii = 0;ii < pathList.size();ii++){
			LAnchorPath lap = pathList.get(ii);
			if(lap.path.intersects(rect)){
				return lap;
			}
		}
		return null;
	}
	public LAnchorPath checkPath_modi(Rectangle2D rect){
		if(bone.intersects(rect)){
			return checkPath_modi_E(rect);
		}
		return null;
		
	}
	public double getLength(){
		LAnchorPoint now = start;
		LAnchorPoint nex = now.next;
		double ret = 0.0;
		while(nex != null){
			ret += LAnchorPoint.getPathLength(now,nex);
			now = now.next;
			nex = now.next;
			if(now == start){
				break;
			}else{
				
			}
		}
		return ret;
		
	}
	public boolean isLongerThan(double breaklen){
		LAnchorPoint now = start;
		LAnchorPoint nex = now.next;
		double ret = 0.0;
		while(nex != null){
			ret += LAnchorPoint.getPathLength(now,nex);
			now = now.next;
			nex = now.next;
			if(ret > breaklen){
				return true;
			}else if(now == start){
				return false;
			}
			
		}
		return false;
		
	}
	public boolean isClosed(){
		return start.prev != null;
	}
	public LAnchorPoint checkPoint(double xx,double yy){
		return checkPoint(xx,yy,false);
	}
	
	public LAnchorPoint checkPoint(double xx,double yy,boolean floatingflag){
		double mx = FlexPoint.getModiX(xx);
		double my = FlexPoint.getModiY(yy);
		if(pointShape.contains(mx,my)){
			return checkPoint_E(xx,yy,floatingflag);
		}else{
			
			return null;
		}
	}
	public void makePointShape(){
		LAnchorPoint now = start;
		LAnchorPoint next = start.next;
		pointShape.reset();
		pointShape.moveTo(0,0);
		bone.reset();
		while(now != null){
			pointShape.append(new Rectangle2D.Double(now.shapePoint[LAnchorPoint.MAIN].modix-PathGroup.pointSize/2,now.shapePoint[LAnchorPoint.MAIN].modiy-PathGroup.pointSize/2,PathGroup.pointSize,PathGroup.pointSize),false);
			
			now = now.next;
			if(now == null || now == start){
				break;
			}
			next = now.next;
			
		}
		LAnchorPoint tstart = start;
		LAnchorPoint tnow = start;
		LAnchorPoint tnext = start.next;
		bone.moveTo(tnow.shapePoint[LAnchorPoint.MAIN].modix,tnow.shapePoint[LAnchorPoint.MAIN].modiy);
		while(tnext != null){
			bone.curveTo(tnow.shapePoint[LAnchorPoint.NEXT].modix,tnow.shapePoint[LAnchorPoint.NEXT].modiy
			,tnext.shapePoint[LAnchorPoint.PREV].modix,tnext.shapePoint[LAnchorPoint.PREV].modiy
			,tnext.shapePoint[LAnchorPoint.MAIN].modix,tnext.shapePoint[LAnchorPoint.MAIN].modiy
			);
			tnow = tnow.next;
			
			if(tnow == tstart){
				bone.closePath();
				tnext = null;
				break;
			}else{
				tnext = tnow.next;
			}
		}
	}
	
	
}





