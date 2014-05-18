package quickget;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

/**
* 文件合并,合并完后,删除零时文件,文件必须要有后缀名
* @param file[],dir,name
*/
class fileMerger{
	File file[];
	String dir;
	String name;
	fileMerger(File file[],String dir,String name){
		this.file=file;
		this.dir=dir;
		this.name=name;
	}
	/*
	 * @param fTarget,ftout,perLen,maxSize
	 */
	protected boolean merger(long perLen,long maxSize){
		try{
			int n=file.length;//分段零时文件的大小
			//下载目标文件
			RandomAccessFile[] fTarget = new RandomAccessFile[n];
			FileInputStream[] fnArray= new FileInputStream[n];
			byte[] b = new byte[1024];
			for(int i=0;i<n;i++){
				int hasRead=0;
				//int size=0;
				fnArray[i]=new FileInputStream(file[i]);
				//一个文件,五个引用,便于分段跳跃,省去片段超过长度检查
				fTarget[i] = new RandomAccessFile(new File(dir,name),"rw");
				//合并前跳越,开始合并
				fTarget[i].seek(i*perLen);
				while((hasRead=fnArray[i].read(b))!=-1){
					fTarget[i].write(b,0,hasRead);
					//size+=hasRead;					
					//当超过指定大小时退出
					//if(size>maxSize)
						//break;
				}
				//写完一个关闭一个输入输出流,不然下一步无法删除零时文件
				fTarget[i].close();
				fnArray[i].close();
			}
			System.out.println(name+"文件合并完成");
			//删除合并前的文件夹
			for(int i=0;i<n;i++)
				file[i].delete();

			new File(dir,name.substring(0,name.lastIndexOf('.'))).delete();
		}catch(Exception e){
			System.out.println("合并文件失败,请检察时候文件有后缀");
			return false;
		}
		return true;
	}
}
