package quickget;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.*;
import java.net.*;

/**
* @author tzterryz@gmail.com
*/
public class Quickget extends WindowAdapter implements ActionListener{
	//系统文件,下载保存路径和线程数
	String downloadDir="";//为空表示下载路径为空
	int threadNumber;
	File fl= new File("recordfile");
	File sysfl=new File("sysFile");
	String[] fileName,urlA;//下载文件信息
	/**********不变的按钮*************/
	JButton bt1 = new JButton("Download");
	JButton Previous = new JButton("Previous");
	JButton Next = new JButton("Next");
	JButton bt4 = new JButton("Exit");
	JButton tabPan2But = new JButton("Ok");
	JButton clearThis = new JButton("clear(2)");
	JButton clearAll =new JButton("clearAll(1)");
	//两个文本框用来输入网址和名称,一个下载按钮	
	JTextField urltf = new JTextField("");
	JTextField nametf = new JTextField("");
	JTextField threadNum = new JTextField("5");//default is 5
	JTextField saveTo = new JTextField();
	//有用的显示信息的标签
	JLabel urlLab = new JLabel("Get From");
	JLabel nameLab= new JLabel("File Name(without commas in the name)");
	JLabel threadLab = new JLabel("Thread Number");
	JLabel savetoLab = new JLabel("Save to");
	JPanel newPan = new JPanel(new GridLayout(4,1));	
	JPanel showPan = new JPanel(new CardLayout());
	JPanel basePan = new JPanel(new GridLayout(1,4));
	/**********End*************/
	//times表示下载总数,也表示信息显示panel总数,默认值,第一次初始化的值
	int times=-1,change =0;
	JFrame  fr = new JFrame("小猫快跑V0.1");
	
	public Quickget() throws Exception{
		Container contentPane = fr.getContentPane();
		contentPane.setLayout(new GridLayout(1,1));
		//读入下载线程数和下载保存目录
		if(sysfl.exists()){
			FileReader fReader=new FileReader(sysfl);
			BufferedReader bf= new BufferedReader(fReader);
			if((sysfl.exists())&&(bf.readLine()!=null)){
				//读入记录
				History hs=new History();
				threadNumber=hs.retnThreadNumber();
				downloadDir=hs.retndownloadDir();
			}
			fReader.close();
			bf.close();
			threadNum.setText(String.valueOf(threadNumber));
			saveTo.setText(downloadDir);
		}
		//初始化时读入历史记录,并添加panel组到showPan中,继续上次下载,如果不存在,创建空文件
		if(fl.exists()){
			FileReader fReader;
				fReader = new FileReader(fl);
				BufferedReader bf= new BufferedReader(fReader);
				if((fl.exists())&&(bf.readLine()!=null)){
					/*********读入记录开始*************/
					History hs = new History();
					String s=hs.retnString();
					times=hs.retnTimes(s);//从0开始,-1表示无历史文件

					System.out.println("There are "+(times+1)+" files.");
					/************读取历史记录并下载**************/
					if(times!=-1){
						change = times;
						//String[] ;
						String[][] fileInfo = hs.retnFileInfo(times,s);
						urlA=fileInfo[0];
						fileName=fileInfo[1];
						int[] id=new int[times+1];
	                    /************结束读取历史**************/
						JPanel[] hisPan = new JPanel[times+1];
						JLabel[][] lbA= new JLabel[times+1][3];
						int i;
						for(i=0;i<times+1;i++){//例如两个历史i<times时只显示一个	
			              id[i]=i;
							new DuoXC(urlA[i],fileName[i],downloadDir,threadNumber).start();
							System.out.println(urlA[i]+","+fileName[i]+","+id[i]);
							hisPan[i]=new JPanel(new GridLayout(3,1));
							lbA[i][0]=new JLabel();
							lbA[i][1]=new JLabel();
							lbA[i][2]=new JLabel();
							lbA[i][0].setText(String.valueOf(id[i]));
							lbA[i][1].setText(urlA[i]);
							lbA[i][2].setText(fileName[i]);	
							hisPan[i].add(lbA[i][0]);
							hisPan[i].add(lbA[i][1]);
							hisPan[i].add(lbA[i][2]);
							showPan.add(String.valueOf(i),hisPan[i]);
							((CardLayout)showPan.getLayout()).show(showPan,String.valueOf(i));
						}	
						fReader.close();
						bf.close();
					}
				}
				/**********读取历史记录结束****************/
		}	
		//如果times等于-1,即无历史记录,禁止pervious,和next两个button
		if(times==-1){
			Previous.setEnabled(false);
			Next.setEnabled(false);
		}
		/**********3个标签设计******************/
		//标签一设计
		basePan.add(bt1);
		basePan.add(Previous);
		basePan.add(Next);
		basePan.add(bt4);
		basePan.setBorder(BorderFactory.createTitledBorder("Operation"));
		newPan.add(urlLab);
		newPan.add(urltf);
		newPan.add(nameLab);
		newPan.add(nametf);
		newPan.setBorder(BorderFactory.createTitledBorder("New Download"));
		showPan.setBorder(BorderFactory.createTitledBorder("Basic Information"));
		bt1.addActionListener(this);
		Previous.addActionListener(this);
		Next.addActionListener(this);	
		bt4.addActionListener(this);	
		
		JPanel priPan= new JPanel(new GridLayout(2,1));
		priPan.add(basePan);
		priPan.add(newPan);
		//标签二的设计
		JPanel tabPan2 = new JPanel(new GridLayout(4,1));	
		JPanel threadPan = new JPanel(new GridLayout(1,2));
		threadPan.add(threadLab);
		threadPan.add(threadNum);		
		JPanel savetoPan = new JPanel(new GridLayout(1,2));
		savetoPan.add(savetoLab);
		savetoPan.add(saveTo);		
		JPanel thdsavPan=new JPanel(new GridLayout(3,1));
		thdsavPan.setBorder(BorderFactory.createTitledBorder("Perference"));
		JPanel buttonPan =new JPanel();
		JLabel emptyLab = new JLabel();
		JLabel clearLab=new JLabel("2.Only delete the current history,restart");
		JLabel clearAllLab=new JLabel("1.Erase entire history record,restart");
		JPanel hisPan = new JPanel(new GridLayout(3,1));
		JPanel subhisPan=new JPanel(new FlowLayout());
		hisPan.setBorder(BorderFactory.createTitledBorder("History Manage"));
		JPanel emptyPan1 =new JPanel();
		
		buttonPan.add(tabPan2But);
		buttonPan.add(emptyLab);
		tabPan2But.addActionListener(this);
		clearThis.addActionListener(this);
		clearAll.addActionListener(this);
		thdsavPan.add(threadPan);
		thdsavPan.add(savetoPan);
		thdsavPan.add(buttonPan);
		subhisPan.add(clearAll);
		subhisPan.add(clearThis);
		hisPan.add(subhisPan);
		hisPan.add(clearAllLab);
		hisPan.add(clearLab);

		tabPan2.add(thdsavPan);
		tabPan2.add(hisPan);
		tabPan2.add(emptyPan1);
		
		//标签3,用来显示帮助信息
		String help="a.安装Java,去SUN官网下载jre;\n" +
				     "b.设置下载默认路径以及下载的线程数;\n" +
					  "c.文件名必须要有后缀(例如.mp3);\n"+
					  "d.Call ME at 555.^.^";
		JTextArea helpText = new JTextArea(help,15,15);		
		JPanel tabPan3=new JPanel();
		tabPan3.add(helpText);
		tabPan3.setBorder(BorderFactory.createTitledBorder("Help"));
		
		//TabelPanel显示
		JPanel tabPan1 = new JPanel(new GridLayout(2,1));
		tabPan1.add(priPan);
		tabPan1.add(showPan);
		JTabbedPane tabbedPan = new JTabbedPane();
		tabbedPan.addTab("Start",tabPan1);
		tabbedPan.addTab("Setting",tabPan2);
		tabbedPan.addTab("Help",tabPan3);
		contentPane.add(tabbedPan);
		/************结束标签设计********************/
		//由下载地址即时更新文件名称
		urltf.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e){
					String urlnm = urltf.getText();
					if(urlnm.length()!=0){
					  /******Test*******/
						//System.out.println(urlnm);
					  /*******test******/
					  try{
					    URL url=new URL(urlnm);
					    //用来获得文件名称
					    String named=url.getFile();
					    named=URLDecoder.decode(named,"UTF-8");
					    named=named.replace('/', ' ');
					    named=named.trim();
					    /*******Test********/
					    //System.out.println(named);
					    /*******test******/
					    nametf.setText(named);
					  }
					  catch(Exception e1){
						  //System.out.println("Illegal url address!");
						  //urltf.setText("");
						  nametf.setText("");
					  }
					}
				}
			});

		fr.pack();
		fr.setSize(400,500);
		fr.setVisible(true);
		fr.addWindowListener(this);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==Previous){
			//Runtime r=Runtime.getRuntime();
			//r.gc();
			change--;
			if(change==-1){
				change=times;
			}				
			System.out.println(change);
			((CardLayout)showPan.getLayout()).show(showPan,String.valueOf(change));
			fr.validate();
		}
		if(e.getSource()==Next){
			//垃圾回收
			//Runtime r=Runtime.getRuntime();
			//r.gc();
			change++;
			if(change==times+1){
				change=0;
			}				
			System.out.println(change);
			((CardLayout)showPan.getLayout()).show(showPan,String.valueOf(change));
			fr.validate();
		}
		if(e.getSource()==bt4){
			System.exit(0);
		}
		if(e.getSource()==bt1){	
			//当下载路径正确时下载
			if(downloadDir.length()!=0){
				try{
					String urlnm = urltf.getText();				
					URL url=new URL(urlnm);
					//用来获得下载文件名称
					String fileName=url.getFile();					
					fileName=URLDecoder.decode(fileName,"UTF-8");//解析url字符
				    fileName=fileName.replace('/', ' ');
				    fileName=fileName.trim();
					String filenm=nametf.getText();
					if((filenm.length()==0))
						filenm = fileName;
					urltf.setText("");
					nametf.setText("");
					if((filenm.length()>0) && (fileName.length()>0)){
						//写入历史
						new History().record(urlnm,filenm);
						times++;
						
						JPanel pal = new JPanel(new GridLayout(3,1));
						JLabel nameLab = new JLabel(filenm);
						JLabel timesLab = new JLabel(String.valueOf(times));
						JLabel urlLab = new JLabel(urlnm);
						pal.add(timesLab);
						pal.add(urlLab);
						pal.add(nameLab);
						showPan.add(String.valueOf(times),pal);
						((CardLayout)showPan.getLayout()).show(showPan,String.valueOf(times));
						//times++;
						fr.validate();
							
						new DuoXC(urlnm,filenm,downloadDir,threadNumber).start();
					}	
				}
				catch(Exception e2){
					/***************地址非法******************/
					int results=JOptionPane.showConfirmDialog(null,"Valid Address or something!!", 
							"CAUTION!!",JOptionPane.DEFAULT_OPTION);
					
					if(results==0){
						//System.out.println(results);
						urltf.setText("");
						nametf.setText("");
					}
					/***********End*******************/
				}
			}
			else{
				/***************下载目录非法******************/
				int results=JOptionPane.showConfirmDialog(null,"Check your storage path!", 
						"CAUTION!!",JOptionPane.DEFAULT_OPTION);
				
				if(results==0){
					System.out.println("下载路径未设置或非法!");					
				}
				/***********End*******************/
			}
			
			if(times>0){
				Previous.setEnabled(true);
				Next.setEnabled(true);
			}
		}
		if(e.getSource()==tabPan2But){
			//创建系统文件,记录下载保存目录和线程数
			String downloadDirTemp = saveTo.getText();
			String threadNumberTemp = threadNum.getText();
			if((downloadDirTemp.length()>0)&&(threadNumberTemp.length()>0)){
				downloadDir=downloadDirTemp;
				
				try{
					File dirFile=new File(downloadDir);
					if(!dirFile.exists()){
						dirFile.mkdirs();
					}					
				}
				catch(Exception e1){
					int results=JOptionPane.showConfirmDialog(null,"dir is not validate!", 
							"dir is not validate!",JOptionPane.OK_OPTION);
					if(results==JOptionPane.OK_OPTION){
						saveTo.setText("");
						//threadNum.setText("");
					}
				}
				threadNumber=Integer.parseInt(threadNumberTemp);
				//记录下载路径和线程数,保存在程序目录下
				History hs = new History();
				try {
					hs.record(threadNumber,downloadDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		}
		if(e.getSource()==clearThis){
			//clear current record and restart
			try {
				String delete_file=urlA[change]+","+fileName[change];
				new HistoryManager().hisManager(delete_file);
			} catch (Exception e1) {
				System.out.println("record file does not exist");
				System.exit(1);
			}
			//restart the prgm
			Runtime r=Runtime.getRuntime();
			//Process p =null;
			try {
				/*将class文件打包成jar文件放在程序目录中（下同）
				 * 也可以换成相对路径（例）jdk/jre/bin/java
				 * Quickget.jar中编辑主类
				 */
				r.exec("java -jar quickget.jar");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		  System.exit(0);
		}
		if(e.getSource()==clearAll){
			//delete history file and restart progam
			new HistoryManager().hisManager();
			Runtime r=Runtime.getRuntime();
			//Process p =null;
			try {
				r.exec("java -jar quickget.jar");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    System.exit(0);
		}
	}
	
	public void windowClosing(WindowEvent e){
		System.exit(0);
	}
	public static void main(String args[]){
		try {
			Quickget qk=new Quickget();
			//设置应用程序图标
			Image ic=Toolkit.getDefaultToolkit().getImage("a.png");
			qk.fr.setIconImage(ic);
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
