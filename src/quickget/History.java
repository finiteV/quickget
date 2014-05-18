package quickget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
*记录,读取,下载历史,以及下载参数
* @author tzterryz@gmail.com
*/
class History{
	final File fl = new File("recordfile");
	static String urlname[];
	String filename[];
	int id[];
	//一共下载了多少次
	int times;
	History(){
		//this.fl=fl;
	}
	public void record(String url,String name){

		String temp=url+","+name;
		String now;
		try {
			if(fl.exists()){
			  FileReader fReader = new FileReader(fl);
			  //BufferedReader bfReader = new BufferedReader(fReader);
			  String before="";
			  int tmp;
			  while((tmp=fReader.read())!=-1)
				  before+=(char)tmp;
			  //before=bfReader.readLine();
			  if(before==""){
			    now=temp;
			    /*******Test********/
			    //System.out.println("The begining"+now);
			    /*****************/
			    
			  }  
			  else
				  now=before+"\n"+temp;
			  fReader.close();
			  //bfReader.close();
			}
			else{
				now=temp;
				/*******Test********/
			    //System.out.println("The Start"+now);
			    /*****************/
				fl.createNewFile();				
			}			
			FileWriter fWriter = new FileWriter(fl);
			/************Test*************/
			//System.out.println("Final"+now);
			/************************/
			fWriter.write(now);
			fWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	//写入系统文件,包括下载路径和下载线程数
	void record(int threadNumber,String downloadDir) throws IOException{
		System.out.println("Set "+threadNumber+" thread, Save to "+downloadDir);
		File sysFile = new File("sysFile");
		String temp=threadNumber+","+downloadDir;
		FileWriter fWriter=new FileWriter(sysFile);
		fWriter.write(temp);
		fWriter.close();
	}
	//返回下载线程数
	int retnThreadNumber() throws IOException{
		File fl=new File("sysFile");
		int threadNumber=0;
		FileReader fReader = new FileReader(fl);
		//封装
		BufferedReader bfReader = new BufferedReader(fReader);
		String s = bfReader.readLine();
		threadNumber=Integer.parseInt(s.substring(0, s.indexOf(',',0)));
		System.out.println("Set "+threadNumber+" Thread.");
		bfReader.close();
		fReader.close();
		return threadNumber;
	}
	//返回上次下载路径
	String retndownloadDir() throws IOException{
		File fl=new File("sysFile");
		String downloadDir=null;
		FileReader fReader = new FileReader(fl);
		BufferedReader bfReader = new BufferedReader(fReader);
		String s = bfReader.readLine();
		downloadDir=s.substring(s.indexOf(',',0)+1,s.length());
		System.out.println("Save to "+downloadDir);
		bfReader.close();
		fReader.close();
		return downloadDir;
	}
	//返回历史字符串
	String retnString() throws IOException{
		FileReader fReader = new FileReader(fl);
		//BufferedReader bfReader = new BufferedReader(fReader);
		int c;
		String s="";
		while((c=fReader.read())!=-1)
			s+=(char)c;
		//bfReader.close();
		fReader.close();
		return s;
	}
	//返回下载次数
	int retnTimes(String s){
		if(s=="")
			times=-1;
		for(int k=0;k<s.length();k++){
				if((s.charAt(k)=='\n')){
					times++;
				}
		}
		return times;
	}
	//返回下载url列表和对应文件列表,Strings不为空且合法
	public String[][]  retnFileInfo(int times,String s){	
			int j=0;
			String fileInfo[][]=new String[2][times+1];
			String urlname[]=new String[times+1];
			String filename[]=new String[times+1];
			//String id[]=new String[times+1];
			int urlnameEPos,filenameEPos;
			for(int i=0;i<s.length();i++){
				if((i==0) || (s.charAt(i-1)=='\n')){
					/******Test*************/
					//System.out.println(i);
					/****************/
					urlnameEPos=s.indexOf(',',i);
					urlname[j]=s.substring(i,urlnameEPos);
					filenameEPos=(j==times)?(s.length()):s.indexOf('\n',i);
					filename[j]=s.substring(urlnameEPos+1,filenameEPos);
					//idEPos=(s.charAt(i)==(s.length()-1))?(s.length()-1):s.indexOf('\n',i);
					//id[j]=s.substring(filenameEPos+1,idEPos);
					/******Test******/
					//System.out.println(urlname[j]+","+filename[j]+","+j);
					/**************/
					j++;
				}						
			}	
			fileInfo[0]=urlname;
			fileInfo[1]=filename;
			//fileInfo[2]=id;
		return fileInfo;
	}	
}