package quickget;


import ui.MainUI;

/**
* @author tzterryz@gmail.com
*/
public class Quickget{
	
	
	public Quickget(){

	}

	public static void main(String args[]){
		try {
			MainUI ui=new MainUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/**
 * log 2013/04
 * 需要修改的类为DownThread--->DuoXC,后者改变较大,前者依赖后者,
 * 每个子下载线程的开始部分为i*i*perLen,已下载部分为tempSize[i],结束部分为(i+1)*perLen
 * 主要是添加http头请求信息来获得专用输入流,然后取消DownThread中输入流的跳跃
 * 合并零时文件存在和不存在代码重复部分
 * 改写redown的decide函数的返回值优化DuoXC类的代码
 * 完善History管理类
 * --过滤
 * historymanager的delete函数改变
 * 获取下载文件名使用url.getFile()函数
 */
