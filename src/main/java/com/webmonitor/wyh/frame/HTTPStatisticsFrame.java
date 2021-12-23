package com.webmonitor.wyh.frame;



import com.webmonitor.wyh.statistics.HttpAnalyse;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class HTTPStatisticsFrame extends JFrame{

	private MutableTreeNode root,rqtNode,rpsNode;
	//	GETNode,HEADNode,POSTNode,PUTNode,PATCHNode,DELETENode,OPTIONSNode,TRACENode,
	//	r2xxNode,r3xxNode,r4xxNode,r5xxNode;
	private MutableTreeNode[] methodNode;
	private MutableTreeNode[] codeNode;
	private HttpAnalyse httpAna;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JScrollPane panel;
	
	public HTTPStatisticsFrame() {
		setTitle("HTTP统计");
		initComponents();
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);

		loadAndShowData();
		
		
	}

	private void initComponents() {

		
		root = new DefaultMutableTreeNode("HTTP Packets");
		rqtNode = new DefaultMutableTreeNode("Request Packets");
		rpsNode = new DefaultMutableTreeNode("Response Packets");
		
		//GETNode,HEADNode,POSTNode,PUTNode,PATCHNode,DELETENode,OPTIONSNode,TRACENode,
		methodNode = new DefaultMutableTreeNode[8];
//		for(int i=0;i<8;i++)
//		GETNode = new DefaultMutableTreeNode("GET");
//		HEADNode = new DefaultMutableTreeNode("HEAD");
//		POSTNode = new DefaultMutableTreeNode("POST");
//		PUTNode = new DefaultMutableTreeNode("PUT");
//		PATCHNode = new DefaultMutableTreeNode("PATCH");
//		DELETENode = new DefaultMutableTreeNode("DELETE");
//		OPTIONSNode = new DefaultMutableTreeNode("OPTIONS");
//		TRACENode = new DefaultMutableTreeNode("TRACE");
		
		//r2xxNode,r3xxNode,r4xxNode,r5xxNode;
		codeNode = new DefaultMutableTreeNode[4];
//		r2xxNode = new DefaultMutableTreeNode("2xx:Success");
//		r3xxNode = new DefaultMutableTreeNode("3xx:Redirection");
//		r4xxNode = new DefaultMutableTreeNode("4xx:Cilent Error");
//		r5xxNode = new DefaultMutableTreeNode("5xx:Server Error");
		
		
		root.insert(rqtNode, 0);
		root.insert(rpsNode, 1);
		
		for(int i=0;i<8;i++) {
			methodNode[i] = new DefaultMutableTreeNode();
			rqtNode.insert(methodNode[i], i);
		}
		for(int i=0;i<4;i++) {
			codeNode[i] = new DefaultMutableTreeNode();
			rpsNode.insert(codeNode[i], i);
		}
		
//		//GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
//		rqtNode.insert(GETNode, 0);
//		rqtNode.insert(HEADNode, 1);
//		rqtNode.insert(POSTNode, 2);
//		rqtNode.insert(PUTNode, 3);
//		rqtNode.insert(PATCHNode, 4);
//		rqtNode.insert(DELETENode, 5);
//		rqtNode.insert(OPTIONSNode, 6);
//		rqtNode.insert(TRACENode, 7);
//		
//		//2xx,3xx,4xx,5xx
//		rpsNode.insert(r2xxNode, 0);
//		rpsNode.insert(r3xxNode, 1);
//		rpsNode.insert(r4xxNode, 2);
//		rpsNode.insert(r5xxNode, 3);
		
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		panel = new JScrollPane(tree);
		add(panel);
	}
	
	private void loadAndShowData() {
		httpAna = HttpAnalyse.newInstance();
		String space = "               ";
		for(int i=0;i<8;i++) {
			methodNode[i].setUserObject(httpAna.getMethodStr()[i]+space+httpAna.getMethod()[i]);
		}
		for(int i=0;i<4;i++) {
			codeNode[i].setUserObject(httpAna.getCodeStr()[i]+space+httpAna.getStatusCode()[i]);
		}
	}

}
