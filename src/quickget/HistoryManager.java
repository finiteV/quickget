package quickget;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 操作历史文件,删除某条特定的历史记录,并记录下来,如果要删除
 * 某个文件删除历史记录,关闭程序,最后删除零时文件
 */
class HistoryManager{
	HistoryManager(){}
	void hisManager(){
		File fl=new File("recordfile");
		fl.delete();
	}
	void hisManager(String change) throws IOException{
		/*******Test**********/
		System.out.println("String to delete:"+change);
		/*******************/
		File fl=new File("recordfile");
		FileReader fReader = new FileReader(fl);
		//BufferedReader bfReader = new BufferedReader(fReader);
		//读出文件列表
		int c;
		String s="";
		while((c=fReader.read())!=-1)
		  s+=(char)c;	
		//bfReader.close();
		/*******Test**********/
		System.out.println("record file contents:"+s);
		/**************/
		fReader.close();
		//如果要删除不存在的历史,则结束程序
		if(s==""){
			System.out.println("Error:删除不存在的历史!");
			System.exit(0);
		}
		int start,end;
		//求出删除字符串的起点和终端
		start=(s.indexOf(change)==0)?0:(s.indexOf(change)-1);
       end=(start+1)+change.length();
       /*******Test************/
       System.out.println("start pos:"+start+",end pos:"+end+",deleted length:"+change.length()+",s length"+s.length());
       /**************/
		//写入到recorddfile中
       String temp;
       if(start==0 && end==s.length()+1){temp="";}//特殊情况,特殊处理
       else{temp=s.substring(0,start)+s.substring(end,s.length());}
		//System.out.println(temp);
		FileWriter fWriter = new FileWriter(fl);
		fWriter.write(temp);
		fWriter.close();
	}
}
