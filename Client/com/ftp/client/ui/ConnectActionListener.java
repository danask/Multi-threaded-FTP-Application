package com.ftp.client.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.ftp.client.FTPClient;
import com.ftp.client.FTPCommand;
import com.ftp.client.ReplyCodeConstants;

public class ConnectActionListener implements ActionListener{
	
	public JTextField hostURL, username;
	public DefaultListModel<String> listValues;
	public JPasswordField password;
	public JList list;
	public JLabel msgText;
	
	public JLabel getMsgText() {
		return msgText;
	}

	public void setMsgText(JLabel msgText) {
		this.msgText = msgText;
	}

	public JTextField getHostURL() {
		return hostURL;
	}

	public void setHostURL(JTextField hostURL) {
		this.hostURL = hostURL;
	}

	public JTextField getUsername() {
		return username;
	}

	public void setUsername(JTextField username) {
		this.username = username;
	}
	
	/*public ConnectActionListener(JTextField hostURL, JTextField username) {
		// TODO Auto-generated constructor stub
		this.hostURL = hostURL;
		this.username = username;
		
	}*/
	
	public JPasswordField getPassword() {
		return password;
	}

	public void setPassword(JPasswordField password) {
		this.password = password;
	}

	public DefaultListModel<String> getListValues() {
		return listValues;
	}

	public void setListValues(DefaultListModel<String> listValues) {
		this.listValues = listValues;
	}
	
	public JList getList() {
		return list;
	}

	public void setList(JList list) {
		this.list = list;
	}

	public ConnectActionListener() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// on click of connect
		System.out.println(hostURL.getText());
		FTPClient ftpClient=new FTPClient();
		String result=ftpClient.connectToControlPort(hostURL.getText());
		if (result.equals(ReplyCodeConstants.READY_FOR_NEW_USER)){
			ftpClient.clientCommand(FTPCommand.PASV.getValue(), null);
			String msg=ftpClient.clientCommand(FTPCommand.USER.getValue(), new String[]{username.getText(),password.getText()});
			if(msg.equals(ReplyCodeConstants.LOGGED_IN)){
				String filesList=ftpClient.clientCommand(FTPCommand.LIST.getValue(), null);
				for(String str :filesList.split(",")){
					listValues.addElement(str);
					list.setVisible(true);
				}
				System.out.println(ReplyCodeConstants.READY_FOR_NEW_USER_MSG);
				msgText.setText(ReplyCodeConstants.READY_FOR_NEW_USER_MSG);
			}else{
				System.out.println(ReplyCodeConstants.LOGGED_IN_FAILED_MSG);
				msgText.setText(ReplyCodeConstants.LOGGED_IN_FAILED_MSG);
			}
		}
	}

}
