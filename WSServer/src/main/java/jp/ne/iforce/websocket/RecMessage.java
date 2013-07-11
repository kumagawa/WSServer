package jp.ne.iforce.websocket;

public class RecMessage extends Message{
	private String target;

	public RecMessage(String c, String m,String t) {
		super(c, m);
		this.target = t;
	}
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}