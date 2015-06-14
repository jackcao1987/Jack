import java.net.Socket;


public class UserInformation implements Comparable<UserInformation> {
	int id = 0;
	String userName;
	Socket s = null;
	UserInformation(int identity, String name)
	{
		super();
		id = identity;
		userName = name;
	}
	
	public void setIdentity(int identity)
	{
		id = identity;
	}
	
	public int getIdentity()
	{
		return id;
	}
	
	public void setUserName(String name)
	{
		userName = name;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public boolean equals(Object obj)
	{
		if (this.id == ((UserInformation)obj).id)
		{
			return true;
		}
		else
			return false;
		
	}

	public int compareTo(UserInformation o) {
		// TODO Auto-generated method stub
		if (this.id < o.id)
		{
			return -1;
		}
		else if (this.id > o.id)
		{
			return 1;
		}
		else
			return 0;
	}
}
