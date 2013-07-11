package jp.ne.iforce.websocket;

public class Message {
	private String cmd;
	private String message;

	public Message(String c, String m) {
		this.cmd = c;
		this.message = m;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}