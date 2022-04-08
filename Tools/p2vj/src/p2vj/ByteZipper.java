package p2vj;

import java.io.*;
import java.util.zip.*;
import java.util.ArrayList;
public class ByteZipper{
	//Copyright (C) 2010 sesamecake
	// This software is published under GPLv3.
	// For more detail, please see "readme.txt" and "GPLv3.txt" compressed with this file.
	// If you have some questions, please visit my blog http://sesamecake.blog84.fc2.com/
	// or call me via twitter http://twitter.com/sesamecake
	// THX!
	public static byte[] getDeflatedByteArray(byte[] b){
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(bo);
		zip.setLevel(9);
		byte[] ret = null;
		try{
			zip.putNextEntry(new ZipEntry("ZIPZIP")); 
			zip.write(b,0,b.length);
			zip.close();
			ret = bo.toByteArray();
			
		}catch(Exception exx){
			exx.printStackTrace();
		}
		return ret;
	}	
	public static byte[] getDeflatedByteArray_G(byte[] b){
		byte[] ret = null;
		try{
		ByteArrayOutputStream bo = new ByteArrayOutputStream(1024);
		GZIPOutputStream zip = new GZIPOutputStream(bo);
			zip.write(b,0,b.length);
			zip.close();
			ret = bo.toByteArray();
			
		}catch(Exception exx){
			exx.printStackTrace();
		}
		return ret;
	}
	
	
	public static byte[] getInflatedByteArray(byte[] b){
		ByteArrayInputStream bi = new ByteArrayInputStream(b);
		ZipInputStream zip = new ZipInputStream(bi);
		byte[] buff = new byte[100];
		ArrayList<Byte> reta = new ArrayList<Byte>();
		try{
			int lin = 0;
			zip.getNextEntry();
			while((lin = zip.read(buff,0,100)) > 0){
				for(int ii = 0;ii < lin;ii++){
					reta.add(buff[ii]);
				}
			}
			zip.close();
			
		}catch(Exception exx){
			exx.printStackTrace();
		}
		byte[] ret = new byte[reta.size()];
		for(int ii = 0;ii < reta.size();ii++){
			ret[ii] = reta.get(ii);
		}
		
		return ret;
	}
	
	public static byte[] getInflatedByteArray_G(byte[] b){
		ArrayList<Byte> reta = new ArrayList<Byte>();
		try{
		ByteArrayInputStream bi = new ByteArrayInputStream(b);
		GZIPInputStream zip = new GZIPInputStream(bi);
		byte[] buff = new byte[100];
			int lin = 0;
			while((lin = zip.read(buff,0,100)) > 0){
				for(int ii = 0;ii < lin;ii++){
					reta.add(buff[ii]);
				}
			}
			zip.close();
			
		}catch(Exception exx){
			exx.printStackTrace();
		}
		byte[] ret = new byte[reta.size()];
		for(int ii = 0;ii < reta.size();ii++){
			ret[ii] = reta.get(ii);
		}
		
		return ret;
	}
	
	public static void main(String args[]){
		byte[] test = {1,2,3,4,5,6,77,88,100};
		byte[] go = getDeflatedByteArray(test);
		try{
			FileOutputStream fw = new FileOutputStream("ziptest.zip");
			fw.write(go,0,go.length);
			fw.close();
		}catch(Exception exx){
			exx.printStackTrace();
			
		}
		
		getInflatedByteArray(go);
	}
	
}


