
package com.ftp.client.ui;

import java.awt.Color;

import javax.swing.*;

import com.ftp.client.FTPClient;

public class FTPUserInterface {
	
	final int INITIAL_X = 10;
	final int INITIAL_Y = 10;
	final int WIDTH = 200;
	final int HEIGHT = 30;
	final String HOST = "Host";
	final String USERNAME = "Username";
	final String CONNECT = "Connect";
	final String BROWSE = "Browse and upload";
	final String TITLE = "FTP Client";
	final String DOWNLOAD = "Download";
	final String PASSWORD="Password";
	final String QUIT = "Disconnect";
	
	JFrame frame = new JFrame();
	DefaultListModel<String> listValues = new DefaultListModel<String>();
	JList list = new JList<>(listValues);
	
	JTextField hostTextField = new JTextField();
	JTextField userTextField = new JTextField();
	JPasswordField passwordTextField = new JPasswordField();
	JLabel hostLabel = new JLabel(HOST);
	JLabel userLabel = new JLabel(USERNAME);
	JLabel passwordLabel = new JLabel(PASSWORD);
	JButton connectBtn = new JButton(CONNECT);
	JButton fileBrowserBtn = new JButton(BROWSE);
	JLabel msgLabel = new JLabel();
	JScrollPane jScrollPane = new JScrollPane(list);
	JButton downloadBtn = new JButton(DOWNLOAD);
	JButton quitBtn = new JButton(QUIT);
	//JProgressBar jProgressBar = new JProgressBar();
	JProgressBar jProgressBarUpload = new JProgressBar();
	
	
	public FTPUserInterface(){
		createGUI();
	}
	
	public void createGUI(){
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setSelectedIndex(0);
		list.setVisible(false);
		list.setVisibleRowCount(3);
		
		// Element placements
		hostLabel.setBounds(INITIAL_X, INITIAL_Y, WIDTH, HEIGHT);
		hostTextField.setBounds(INITIAL_X, INITIAL_Y + 30, WIDTH, HEIGHT);
		userLabel.setBounds(INITIAL_X + 250, INITIAL_Y, WIDTH, HEIGHT);
		userTextField.setBounds(INITIAL_X + 250, INITIAL_Y + 30, WIDTH, HEIGHT);
		passwordLabel.setBounds(INITIAL_X + 500, INITIAL_Y, WIDTH - 100, HEIGHT);
		passwordTextField.setBounds(INITIAL_X + 500, INITIAL_Y + 30, WIDTH, HEIGHT);
		connectBtn.setBounds(INITIAL_X, INITIAL_Y + 100, WIDTH - 100, HEIGHT);
		quitBtn.setBounds(INITIAL_X + 250, INITIAL_Y + 100, WIDTH - 100, HEIGHT);
		fileBrowserBtn.setBounds(INITIAL_X + 500, INITIAL_Y + 100, WIDTH - 50, HEIGHT);
		msgLabel.setBounds(INITIAL_X + 250, INITIAL_Y + 400, WIDTH + 400, HEIGHT + 200);
		jScrollPane.setBounds(INITIAL_X, INITIAL_Y + 200, WIDTH, HEIGHT + 200);
		downloadBtn.setBounds(INITIAL_X, INITIAL_Y + 450, WIDTH - 100, HEIGHT);
		//jProgressBar.setBounds(INITIAL_X + 250, INITIAL_Y + 200, WIDTH + 250, HEIGHT);
		jProgressBarUpload.setBounds(INITIAL_X + 250, INITIAL_Y + 300, WIDTH + 250, HEIGHT);
		
		//jProgressBar.setVisible(false);
		//jProgressBar.setForeground(Color.BLUE);
		
		jProgressBarUpload.setVisible(false);
		jProgressBarUpload.setForeground(Color.RED);
		
		frame.add(hostLabel);
		frame.add(hostTextField);
		frame.add(userLabel);
		frame.add(userTextField);
		frame.add(connectBtn);
		frame.add(fileBrowserBtn);
		frame.add(msgLabel);
		frame.add(jScrollPane);
		frame.add(downloadBtn);
		frame.add(passwordLabel);
		frame.add(passwordTextField);
		frame.add(quitBtn);
		//frame.add(jProgressBar);
		frame.add(jProgressBarUpload);
		
		
		// size of JFrame
		frame.setTitle(TITLE);
		frame.setSize(750, 600);
		frame.setLayout(null);
		frame.setVisible(true);
		
		FTPClient ftpClient = new FTPClient();
		
		ConnectActionListener actionListener = new ConnectActionListener();
		actionListener.setHostURL(hostTextField);
		actionListener.setUsername(userTextField);
		actionListener.setPassword(passwordTextField);
		actionListener.setMsgText(msgLabel);
		actionListener.setListValues(listValues);
		actionListener.setList(list);
		connectBtn.addActionListener(actionListener);
		
		FileBrowser fileBrowser = new FileBrowser(msgLabel,listValues, jProgressBarUpload,frame);
		fileBrowserBtn.addActionListener(fileBrowser);
		//DownloadActionListener downloadActionListener=new DownloadActionListener(list, msgLabel,frame);
		
		DownloadActionListener downloadActionListener=new DownloadActionListener(list, msgLabel, frame);
		downloadBtn.addActionListener(downloadActionListener);
		
		QuitActionListener quitActionListener = new QuitActionListener(msgLabel, list);
		quitBtn.addActionListener(quitActionListener);
	}
	
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable(){
            public void run()
            {
                new FTPUserInterface();
            }
        });
	}

}
