package jp.ne.iforce.websocket;

import java.util.List;

public class ReceiveMessage {
	private String cmd;
	private String sessionid;
	private List<WifiConfig> wificonfig;

	public ReceiveMessage(String c, String s, List<WifiConfig> wc) {
		setCmd(c);
		setSessionid(s);
		setWificonfig(wc);
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

	public List<WifiConfig> getWificonfig() {
		return wificonfig;
	}

	public void setWificonfig(List<WifiConfig> wificonfig) {
		this.wificonfig = wificonfig;
	}

}