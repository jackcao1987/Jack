import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class VickyService {
	public static void main(String[] args)
	{
		ServiceWindow serviceWindow = new ServiceWindow("VickeyService");
	}
}

class ServiceWindow extends Frame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UserInformation[] userInformatin = new UserInformation[3];
	String userName[] = {"Vicky", "Jack", "Sjm"};
	//GUI
	TextArea serviceContext = null;
	TextField servicePortText = null;
	TextField serviceClientNumText = null;
	Label servicePortLabel = null;
	Label serviceClientNumLabel = null;
	Button connectButton = null;
	Button stopConnectButton = null;
	Panel panel1 = null;
	Panel panel2 = null;
	//service
	ServerSocket serviceSocket = null;
	Socket client = null;
	int portNum = 4700;
	int maxClients = 10;
	boolean connectStatus = false;
	//Thread
	Thread serviceThread = null;
	
	//constructor
	ServiceWindow(String name)
	{
		super(name);
		for (int i = 0; i < userInformatin.length; i++)
		{
			userInformatin[i] = new UserInformation(i, userName[i]);
		}
		setLocation(900, 300);
		setSize(600, 300);
		
		panel1 = new Panel();
		panel1.setLayout(new GridLayout(1, 1));
		serviceContext = new TextArea();
		panel1.add(serviceContext);
		
		panel2 = new Panel();
		panel2.setLayout(new GridLayout(6, 1));
		servicePortLabel = new Label("Port Numb");
		serviceClientNumLabel = new Label("Max Clients");
		servicePortText = new TextField(Integer.toString(getPortNum()));
		serviceClientNumText = new TextField(Integer.toString(getMaxClientsNum()));
		connectButton = new Button("Connect");
		stopConnectButton = new Button("Stop");
		panel2.add(servicePortLabel);
		panel2.add(servicePortText);
		panel2.add(serviceClientNumLabel);
		panel2.add(serviceClientNumText);
		panel2.add(connectButton);
		panel2.add(stopConnectButton);
		
		setLayout(new BorderLayout());
		add(panel1, BorderLayout.CENTER);
		add(panel2, BorderLayout.EAST);
		setResizable(true);
		setVisible( true);
		
		//listen connectButton event
		connectButton.addActionListener(new ConnectButtonMonitor());
		stopConnectButton.addActionListener(new StopConnectButtonMonitor());
		servicePortText.addFocusListener(new textFocusMonitor(0));
		serviceClientNumText.addFocusListener(new textFocusMonitor(1));
		
		addWindowListener(
		new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		setVisible(false);
		System.exit(-1);
		}
		});
		
	}
	
	//function
	public void setPortNum(int port){		
		portNum = port;
	}
	
	public int getPortNum(){		
		return portNum;
	}
	
	public void setMaxClientsNum(int clients){		
		maxClients = clients;
	}
	
	public int getMaxClientsNum(){		
		return maxClients;
	}
	
	private void searchUserFromId(int identity, UserInformation u)
	{
		for (int i = 0; i < userInformatin.length; i++)
		{
			if (userInformatin[i].getIdentity() == identity)
			{
				u.setUserName(userInformatin[i].getUserName());
				u.s = userInformatin[i].s;
			}
		}
	}
	
	private void setUserClientSocket(int identity, Socket s)
	{
		for (int i = 0; i < userInformatin.length; i++)
		{
			if (userInformatin[i].getIdentity() == identity)
			{
				userInformatin[i].s = s;
			}
		}
	}
	
	//ConnectButton Monitor
	class ConnectButtonMonitor implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	if (false == connectStatus){
	    		connectStatus = true;
	    		serviceThread = new ServiceThread();
	    		serviceThread.start(); 	
	    	}
	    	else{
	    		serviceContext.append("VickeySerivce has been started!!" + "\r\n");
	    	}
	    }
	    
	}
	
	class StopConnectButtonMonitor implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	connectStatus = false;
	    	serviceThread.interrupt();
	    	try{
	    		if (client != null)
	    		{
	    			client.close();
	    		}
	    		if (serviceSocket != null)
	    		{
	    			serviceSocket.close();
	    		}	
	    	}catch(Exception error){
	    		serviceContext.append("close socket error:" + error + "\r\n");
	    	}
	    }
	    
	}
	
	class textFocusMonitor implements FocusListener {
		
		int textType = 0;
		
		textFocusMonitor(int type)
		{
			super();
			textType = type;
		}
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			if (0 == textType)
			{
				setPortNum(Integer.parseInt(servicePortText.getText()));
			}
			else
			{
				setMaxClientsNum(Integer.parseInt(serviceClientNumText.getText()));
			}
		}
		
	}
	
	class ServiceThread extends Thread {
		public void run() {
			try{
				
				serviceSocket = new ServerSocket(portNum);
				serviceContext.append("listen to port :" + portNum + "\r\n");
				serviceContext.append("Max Clients Number :" + maxClients + "\r\n");
			}catch(Exception error){
				
				serviceContext.append("Can't listen to" + error + "\r\n");
			}
			
			try{
				serviceContext.append("Wait for connecting ..." + "\r\n");
				while(true)
				{
					client = serviceSocket.accept();
					new ServiceForClientsThread(client).start();	
				}
			}catch(Exception error){
				
				serviceContext.append("Connection is stopped!" + "\r\n");
			}
		}
	}
	
	class ServiceForClientsThread extends Thread {
		Socket threadClient = null;
		DataInputStream inputData = null;
		UserInformation thisUser = null;
		UserInformation destinationUser = null;
		String buffer = new String();
		
		ServiceForClientsThread(Socket c)
		{
			super();
			thisUser = new UserInformation(-1, "");
			destinationUser = new UserInformation(-1, "");
			threadClient = c;
			try {
				inputData = new DataInputStream(threadClient.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private int getDestinationId(String s)
		{
			int id = -1;
			int index = 0;
			String idString;
			index = s.indexOf("&&");
			idString = s.substring(0, index);
			id = Integer.parseInt(idString);
			System.out.println("idString = " + idString + "id = " + id);
			return id;	
		}
		
		public void run() {
			
			try {
				thisUser.setIdentity(inputData.readInt());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			searchUserFromId(thisUser.getIdentity(), thisUser);
			thisUser.s = threadClient;
			setUserClientSocket(thisUser.getIdentity(), threadClient);
			serviceContext.append(thisUser.userName + " is connected!" + "\r\n");
			while(true)
			{
				try {
					buffer = inputData.readUTF();
					serviceContext.append(buffer + "\r\n");
					destinationUser.id = getDestinationId(buffer);
					if (-1 != destinationUser.id)
					{
						System.out.println("destinationUser.id" + destinationUser.id);
						searchUserFromId(destinationUser.id, destinationUser);
					}
					if (destinationUser.s != null)
					{
						System.out.println("destinationUser.s" + destinationUser.s);
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
	}
}


