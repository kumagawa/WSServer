package jp.ne.iforce.websocket;

public class ConnectMessage {
	private String cmd;
	private String sessionid;

	public ConnectMessage(String c,String s){
		setCmd(c);
		setSessionid(s);
	}

	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

}