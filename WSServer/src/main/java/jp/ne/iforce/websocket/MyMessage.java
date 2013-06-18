package jp.ne.iforce.websocket;

public class MyMessage {
	private String target;
	private String message;

	public MyMessage(String t, String m) {
		this.target = t;
		this.message = m;
	}

	@Override
	public String toString() {
		return String.format("MyMessage(%s, %s)", target, message);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}