package quickget;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 多线程下载工具类,多线程写入单个文件
 * @author book
 *@tempSize表示已经下载的部分,@start表示输入流开始位置
 */
class DownThread extends Thread{
	final int BUFF_LEN =1024;
	private InputStream fin;
	private RandomAccessFile fout;
	private long start;
	private long end;
	long tempSize;
	long totalRead=0;//这个线程的实际读取的大小
	DownThread(long tempSize,long start,long end,InputStream fin,RandomAccessFile fout){
		super();
		
		this.fin = fin;
		this.fout = fout;
		this.start = start;
		this.end = end;
		this.tempSize=tempSize;
	}
	public void run(){
		try{
			//判断是否还需要下载
			if(0<(end-start)){//问题在这里
				//输入流跳过部分,设置分段请求可略
				//fin.skip(start+tempSize);
				fout.seek(tempSize);
				//long buffTimes = (end-start)/BUFF_LEN;
				//写入数据
				byte[] buf = new byte[BUFF_LEN] ;
				int hasRead=0;
	
				long length = end -start;//剩余写入长度
				/********Test*************/
				//System.out.println("这个部分应该写入"+length);
				/******************/
				//实际写入的总是大于应该写入的,totalread表示实际读入,length表示应该写入
				while(((hasRead=fin.read(buf))!=-1) && (totalRead<length)){
					fout.write(buf,0,hasRead);
					totalRead+=hasRead;
	
					//buf = new byte[BUFF_LEN] ;
					//空出线程资源,限制下载速度,一般要禁止
					//try{
						//Thread.sleep((int)(Math.random()*50));	
					//}
					//catch(InterruptedException e){
						//System.out.println("线程异常终止");
					//}
				}
				//实际写入内容大小
				//System.out.println("线程实际写入"+totalRead);
			}
			else
			{
				totalRead=tempSize;
				//System.out.println("这个线程已经写入"+totalRead);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				//关闭输入输出流
				fin.close();
				fout.close();
			}catch(IOException e){
				System.out.println("cannot download the file!");
				e.printStackTrace();
			}
			
		}
	}
	
	//long displayNumber(){
		//System.out.println("这个线程已经写入"+finished);
		//return finished;
	//}
}
