package com.ftp.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;

import com.ftp.client.FTPClient;
import com.ftp.client.FTPCommand;

public class QuitActionListener implements ActionListener{
	
	private JLabel msgText;
	private JList list;
	
	public QuitActionListener(JLabel msgText, JList list) {
		// TODO Auto-generated constructor stub
		this.msgText = msgText;
		this.list = list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		FTPClient ftpClient=new FTPClient();
		ftpClient.clientCommand(FTPCommand.QUIT.getValue(), new String[]{});
		DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
		listModel.removeAllElements();
		msgText.setText("User logged out successfully");
		
	}

}
