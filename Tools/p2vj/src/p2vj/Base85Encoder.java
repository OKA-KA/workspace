package p2vj;


import java.io.*;
import java.awt.image.BufferedImage;

public class Base85Encoder{
	
	
	//Pagesオブジェクトに、Page番号、Page数全部入れるのを忘れないように
	//PageオブジェクトにはそれぞれFont,Resourceを指定しなければならないようだ
	//xrefはそんなに正しくなくても動く。エラーは出ているかもしれない。
	//lengthは、改行も空白も入れる。
	//括弧はデータの終わりだけに入れる/A85なら~>、/AHxなら>
	public static String encodeString(String str,String enc){
		try{
		InputStream in = new ByteArrayInputStream(str.getBytes(enc));
		byte[] b = new byte[4];
		StringBuffer sb = new StringBuffer();
		sb.append("<~");
		while(true){
			
			int len = in.read(b);
			if(len < 1){
				break;
			}
			sb.append(encode4Bytes(b,len));
		}
		
		sb.append("~>");
		return sb.toString();
		}catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}
	public static long writeToBuffer(InputStream in,BufferedWriter out) throws IOException{
		long ret = 4;
		out.write("<~");
		
		byte[] b = new byte[4];
		while(true){
			
			int len = in.read(b);
			if(len < 1){
				break;
			}
			String ps = encode4Bytes(b,len);
			out.write(ps);
			ret += ps.length();
			
		}
		out.write("~>");
		return ret;
	}
	public static String encodeImage(BufferedImage image){
		StringBuffer ret = new StringBuffer();
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
					ret.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
				
				b[pos] = (byte)((cc[xx] & 0x00FF00)/256);
				pos++;
				if(pos == 4){
					ret.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
				
				b[pos] = (byte)(cc[xx] & 0x0000FF);
				pos++;
				if(pos == 4){
					ret.append(Base85Encoder.encode4Bytes(b,4));
					pos = 0;
				}
			}
		}
		if(pos > 0){
			ret.append(Base85Encoder.encode4Bytes(b,pos));	
		}
		
	
		return ret.toString();
	}
	public static String encode4Bytes(byte[] b,int len){
		long sum = 0;
		for(int ii = 0;ii < len;ii++){
			sum += Math.pow(256,3-ii)*(b[ii] & 0xFF);
		}
		char[] c = new char[5-4+len];
		for(int ii = 0;ii < c.length;ii++){
			long bn =  sum/(long)Math.pow(85,4-ii);
			sum -= bn*(long)Math.pow(85,4-ii);
			c[ii] = ((char)(bn+33));
		}
		return String.copyValueOf(c);
	}
	
	
	public static void main(String args[]){
		byte[] b ={(byte)253};
		//System.out.println(encodeString("The advantage over Base64 is that","UTF-8"));
		try{
		FileInputStream fi = new FileInputStream("test.png");
		BufferedWriter bw = new BufferedWriter(new FileWriter("testoutc.dat"));
		writeToBuffer(fi,bw);
		fi.close();
		bw.close();
		}catch(Exception exx){
			exx.printStackTrace();
		}
	}
	
}