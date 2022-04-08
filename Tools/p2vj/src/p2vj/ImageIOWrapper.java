package p2vj;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter; 
import java.io.ByteArrayOutputStream;
import javax.imageio.stream.ImageOutputStream; 
import java.net.*;  
import java.io.*;

public class ImageIOWrapper{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	
	public static void saveToFile(BufferedImage image, String filename) {
		String ext = filename.substring(filename.lastIndexOf(".")+1);
		
		
		Iterator writers = ImageIO.getImageWritersByFormatName(ext);
		if (writers.hasNext()) {
			ImageWriter writer = (ImageWriter)writers.next();
			try {
				ImageOutputStream stream = ImageIO.createImageOutputStream(new File(filename));
				writer.setOutput(stream);
				
				writer.write(image);
				writer.dispose();
				stream.close();
				return;
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		System.err.println("Couldn't make imagefile!");
	}
	public static String sendToCGI(BufferedImage image,String cgiURL){
		String rttt = sendToCGI(null,image,cgiURL,"png");
		return rttt;
	}
	public static String sendToCGI(BufferedImage image,String cgiURL,String extr){
		return sendToCGI(null,image,cgiURL,extr);
	}
	public static String sendToCGI(byte[] fcode,BufferedImage image,String cgiURL,String extr){
	
		Iterator writers = ImageIO.getImageWritersByFormatName(extr);
		if (writers.hasNext()) {
			ImageWriter writer = (ImageWriter)writers.next();
			try {
				ByteArrayOutputStream outbytes = new ByteArrayOutputStream(1024);
				ImageOutputStream stream = ImageIO.createImageOutputStream(outbytes);
				writer.setOutput(stream);
				
				writer.write(image);
				writer.dispose();
				
				
				
				
				URL address=new URL(cgiURL);
				URLConnection cgil=address.openConnection();
				cgil.setUseCaches(false);
				
				cgil.setDoOutput(true);            	
				cgil.setRequestProperty("Content-type","text/plain"); 
				cgil.setDoInput(true);
				cgil.connect();
				                  
				           
				
				 
				PrintStream outStream = new PrintStream(cgil.getOutputStream() ); 
				
				byte password[] = new byte[8]; 
				password[0] = 'S';
				password[1] = 'E';
				password[2] = 'N';
				password[3] = 'D';
				password[4] = '1';
				password[5] = '1';
				password[6] = '2';
				password[7] = 'A';
				
				if(fcode != null){
					outStream.write(fcode);
				}
				
				outStream.write(password);
				outStream.write(outbytes.toByteArray());
				outStream.close();
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(cgil.getInputStream()));
				
				
				 String line="";
				String answer= "";
				while((line = br.readLine()) != null){
				
				
				    answer += line + "\n";
				
				}
				
				br.close();
				
				return answer;
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		System.err.println("Couldn't make imagefile!");
		return "ERROR";
		            
	}

	
}

