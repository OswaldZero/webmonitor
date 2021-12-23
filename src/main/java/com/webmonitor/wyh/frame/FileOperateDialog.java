package com.webmonitor.wyh.frame;


import com.webmonitor.wyh.deal.ExampleFileFilter;
import com.webmonitor.wyh.deal.PacketDumper;
import com.webmonitor.wyh.utils.Properties;
import org.jnetpcap.packet.PcapPacket;
import javax.swing.*;
import java.io.File;
import java.util.List;

public class FileOperateDialog extends JDialog{

	private String path;
	private int flag;
	private File tempfile = null;
	private ExampleFileFilter filter = new ExampleFileFilter();
	private JFileChooser chooser;
	private List<PcapPacket> packets;
	
	public FileOperateDialog(List<PcapPacket> packets) {
		this.packets = packets;
		init();
	}
	
	private void init() {
		filter.addExtension("pcap");
		filter.setDescription("pcap文件");
		// //设置要保存的文件目录与当前编辑文件所在目录一致
		// if(file != null) {
		// chooser = new JFileChooser(file);
		// }else{
		// chooser = new JFileChooser();
		// }
		chooser = new JFileChooser(Properties.defaultFindFilepath);

		chooser.setFileFilter(filter);
		flag = chooser.showSaveDialog(this);
	}
	
	public int doSave() {
		
		if (flag == JFileChooser.APPROVE_OPTION) {// 如果用户按下了对话框中的保存
			// 获取用户指定的文件名和目录的信息
			tempfile = chooser.getSelectedFile();
			
			// 开始做容错处理
			if (tempfile.exists()) {
				if (JOptionPane.showConfirmDialog(this, "文件已经存在，是否覆盖？", "警告",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					flag = 1;
				} else {
					flag = 0;
				}

			}else {
				flag = 1;
			}
		}
		if(flag == 1) {
			writeToFile(tempfile.getPath());
		}
		return flag;
	}

	public String doOpen() {
		if (flag == JFileChooser.APPROVE_OPTION) {// 如果用户按下了对话框中的保存
			// 获取用户指定的文件名和目录的信息
			tempfile = chooser.getSelectedFile();
			
		}
		return tempfile.getPath();
	}
	


	private void writeToFile(String path) {
		String result = PacketDumper.newInstance(path).dumperPacket(packets);
		if(result!="null"&&result!=null) {
			JOptionPane.showMessageDialog(this,result);
		}
		
	}
	
}
