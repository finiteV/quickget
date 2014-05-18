package quickget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
* @author tzterryz@gmail.com
*/
public class DuoXC extends Thread{
	
	String urlname;
	String name;
	//long percent;//线程完成百分比
	//long speed;//线程下载速度
	int FILESIZE;//文件大小
	String downloadDir;
	int n;
	DownThread[] dtd;
	/**
	 * @param urlname,下载网址
	 * @param name,下载文件名
	 * @param n,线程数
	 */
	public DuoXC(String urlname,String name,String downloadDir,int n){
		this.urlname=urlname;
		this.name=name;
		this.n=n;
		this.downloadDir=downloadDir;
	}
	public void run(){
		//int percentage[]=new int[n];
		try{
			URL url = new URL(urlname);
			URLConnection connection = url.openConnection();
			//connection.connect();
            //欲下载文件大小
			FILESIZE = connection.getContentLength();
			System.out.println(name+"大小:"+FILESIZE/1024+"kb"+","+FILESIZE);
		    
			/*******若不支持n\个线程下载********/
			n=(rangeSupport(url,n,FILESIZE))?n:1;
			/*******改变为单线程******/
			/*********************/
			System.out.println("Support "+n+" Range");
			/*********************/
			
			String path = downloadDir;			
			String named=name.substring(0,(name.lastIndexOf('.')==-1)?(name.length()):(name.lastIndexOf('.')));
			//判断下载是否完成
			File tarFile = new File(path,name);
			if(!tarFile.exists()){
				//每个线程负责下载的大小
				long perLen = FILESIZE/n;
				System.out.println("平均下载量为"+perLen);
				//零时文件夹,用来判别是否需要下载
				File ftd = new File(path,named);
				//如果零时文件夹不存在,新建
				if(!ftd.exists())
					if(!ftd.mkdir()){
					  System.out.println("Failed to create temp Directory!");
					  System.exit(0);
					} 

				//建立零时文件,如果不完整则全新下载
				String[] tempName = new String[n];			
				File[] flArray = new File[n];
				for(int i=0;i<n;i++){
					tempName[i]=named+i;
					flArray[i]=new File(ftd.getAbsolutePath(),tempName[i]);
					System.out.println(ftd.getAbsolutePath());
				}

				//如果零时文件不完整,删除他们,重新下载
				int k=0;
				boolean flag = true;
				while(flArray[k].exists()){
					k++;
					if(((k>n-1)&&(k<n+1)) || k==1){
						flag = false;
						break;
					}
					try{
						if(flag){
						for(int i=0;i<k;i++)
							flArray[i].delete();
						ftd.delete();
						}
					}
					catch(Exception e){
						System.out.println("下载文件已损坏,请将零时文件夹删除");
					}	
				}

				//long finishedOne=0,finishedTotal=0;
				//建立数组,大小为线程数的多少,读取上次下载结束位置
				long[] tempSize = new long[n];				
                //零时目录存在继续下载
				if(ftd.exists()){
					//调取断点对象
					Redown[] rd = new Redown[n];				
					for(int i=0;i<n;i++){
						rd[i] = new Redown(flArray[i]);
						tempSize[i]=rd[i].decide();
						/**********Test*************/
						System.out.println("tempSize"+i+":"+tempSize[i]);
						/**********test************/
					}
				}	

				//建立多输入输出流
				RandomAccessFile[] rafArray = new RandomAccessFile[n];
				InputStream[] InArray = new InputStream[n];
				
				long start,end;//每个子下载线程的开始结束部分
				dtd = new DownThread[n];
				for(int i=0;i<n;i++){
					rafArray[i]= new RandomAccessFile(flArray[i],"rw");
					/*************在这个地方设置请求长度*********/
					connection=url.openConnection();
					start=i*perLen+tempSize[i];
					end=(i==(n-1))?FILESIZE:(i+1)*perLen;//最后一个特殊处理
					if(tempSize[i]<perLen){
					connection.setRequestProperty("RANGE",
							"bytes="+start+"-"+end);
					/******Test***********/
					System.out.println(named+" file piece "+i+" RANGE:bytes="+start+"-"+end);
					/*********test*******/
					connection.connect();
					
					//System.out.println(connection.getContentLength()+","+connection.getConnectTimeout());
					
					InArray[i] = connection.getInputStream();						
					/*************************/
					/**************使用DownThread类进行下载*************************/
					System.out.println(i);
					//if(i<n-1){
					dtd[i]=new DownThread(tempSize[i],start,end,InArray[i],rafArray[i]);
					dtd[i].start();
					}else{System.out.println("Temp file "+i+" is finised");}
					//}						
					//else{
						//dtd[i]=new DownThread(tempSize[i],i*perLen,(i+1)*perLen+FILESIZE%n,InArray[i],rafArray[i]);
						//dtd[i].start();
					//}
					/********************End*************************/	
				}
				
				/*************合并文件***********************/
				long nowTime=0;
				long startTime= System.currentTimeMillis();
				//合并前等待下载完成
				System.out.println(name);
				for(int itd=0;itd<n;itd++){
					if(itd<n-1){
						while((new Redown(flArray[itd]).decide())<(perLen)){
							/*******计算下载百分比比及速度*********/
							//long hasGet=getDone();
							//nowTime=System.currentTimeMillis();
							//percent=hasGet/FILESIZE*100;
							//speed=hasGet*1000/(nowTime-startTime);
							//System.out.print("speed:"+speed+"percent:"+percent);
							/************完成计算****************/
							try{
								Thread.sleep((int)(Math.random()*500));	//延迟DownThread0~1s再作判断
								//sleep(500);
							}
							catch(InterruptedException e){
								System.out.println("线程异常终止");
							}
						}
					}else{
						while((new Redown(flArray[itd]).decide())<(perLen+FILESIZE%n)){
							/*******计算下载百分比比及速度*********/
							//long hasGet=getDone();
							//nowTime=System.currentTimeMillis();
							//percent=hasGet/FILESIZE*100;
							//speed=hasGet*1000/(nowTime-startTime);
							//System.out.print("speed:"+speed+"percent:"+percent);
							/************完成计算****************/	
							try{
								sleep((int)(Math.random()*500));	//延迟DownThread0~0.5s再作判断
									//sleep(500);
							}
							catch(InterruptedException e){
									System.out.println("线程异常终止");
							}
						}
					}
					long endTime=System.currentTimeMillis();
					if((endTime-startTime)>999999999){
						System.out.println("等待时间到(大约11天),请重新下载");
						break;
					}
				}
				//合并文件
				fileMerger fmgr = new fileMerger(flArray,path,name);
				fmgr.merger(perLen, FILESIZE);
				System.out.println(name+" is finished!");
				/************合并文件完成*************/
			}
			else{
				System.out.println(name+"文件已下载");
			}
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	//判断是否支持多线程,即Range部分是否合法
	boolean rangeSupport(URL url,int n,long FILESIZE){
		try {
			//URL turl=new URL(url);
			URLConnection con;
			long start,end;
			long perLen=FILESIZE/n;
			for(int i=0;i<n;i++){
			  	con=url.openConnection();
			  	start=i*perLen;
				end=(i==(n-1))?FILESIZE:(i+1)*perLen;//最后一个特殊处理
				con.setRequestProperty("RANGE",
						"bytes="+start+"-"+end);
				con.connect();
				//判断此段是否可行
				/******Test********/
				//System.out.println("Practical:"+con.getContentLength()+",Theory:"+(end-start+1));
				/***********/
				long length;
				length=(i==(n-1))?(end-start):(end-start+1);
				if(con.getContentLength()< length)
				    return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true; //默认返回值
	}
	//返回完成大小
	long getDone(){
		int n=dtd.length;
		long hasFinished=0;
		for(int i=0;i<n;i++)
			hasFinished+=dtd[i].totalRead;
		//long percent_long=hasFinished/FILESIZE*100;
		return hasFinished;
	}
}
