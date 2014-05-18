package quickget;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
* 读取零时文件的大小,给出继续下载的起点和终点
* @author tzterryz@gmail.com
*@param fl
*/
class Redown{
	//包含上次完成的位置
	private File fl;
	Redown(File fl){
		this.fl=fl;
	}
	//判断下载是否结束,若没结束则返回上次下载停止的地方
	long decide() throws IOException {
		//判断上次不完全结束的文件大小
		long i=0;//i=0表示空文件或文件不存在
		if(fl.exists()){
		  FileInputStream recordin = new FileInputStream(fl);
		  byte[] b = new byte[1024];
		  long h=0;
		  while((h=recordin.read(b))!=-1)
			  i+=h;
		  recordin.close();
		}
		return i;
	}	
}
