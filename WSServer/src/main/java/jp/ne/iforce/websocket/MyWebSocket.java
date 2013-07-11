package jp.ne.iforce.websocket;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jetty.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MyWebSocket implements WebSocket.OnTextMessage {

	private static Logger logger = LoggerFactory.getLogger(MyWebSocket.class);

	private final Integer IDLE_TIME = 60 * 60 * 1000; // Connection有効時間（1h）
	private final Integer MAX_TEXTSIZE = 2048; // メッセージ最大文字数
	private Connection connection;
	private String sessionId;
	
	private static String CMD_SESSIONID = "SESSIONID";
	private static String CMD_WIFICONFIG = "WIFICONFIG";

	/**
	 * コンストラクタ
	 * @param sessionId
	 */
	public MyWebSocket(String sessionId) {
		this.sessionId = sessionId;
		MyWebSocketServlet.addSocketQueue(sessionId, this);
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		connection.setMaxIdleTime(IDLE_TIME);
		connection.setMaxTextMessageSize(MAX_TEXTSIZE);
		this.connection = connection;
	}

	/**
	 * 接続時CallBack
	 */
	@Override
	public void onOpen(Connection connection) {
		try {
			this.setConnection(connection);

			Message mes = new Message(CMD_SESSIONID, this.sessionId);
			Gson gson = new Gson();
			this.getConnection().sendMessage(gson.toJson(mes));

		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("open connection(" + MyWebSocketServlet.getSocketQueue().size() + "):" + this.sessionId);
	}

	/**
	 * 切断時CallBack
	 */
	@Override
	public void onClose(int closeCode, String message) {
		MyWebSocketServlet.removeSocketQueue(this.sessionId); // 接続をリストから削除
		logger.info("close connection(" + MyWebSocketServlet.getSocketQueue().size() + "):" + this.sessionId);
	}

	/**
	 * メッセージ受信時
	 */
	@Override
	public void onMessage(String data) {
		logger.info("message:" + data);
		try {
			Gson gson = new Gson();
			RecMessage mm = gson.fromJson(data, RecMessage.class);
			if (mm.getTarget().equals("all")) {
				sendAllMessage(mm);
			} else {
				sendMessage(mm);
			}

		} catch (JsonSyntaxException e) {
			return;
		}
	}

	/**
	 * メッセージ送信（個別）
	 * @param mm
	 */
	public void sendMessage(RecMessage mm) {
		// Connection存在確認
		if (MyWebSocketServlet.getSocketQueue().containsKey(mm.getTarget())) {
			try {
				Connection conection = MyWebSocketServlet.getSocketQueue().get(mm.getTarget()).getConnection();
				Message mes = new Message(CMD_WIFICONFIG, mm.getMessage());
				Gson gson = new Gson();
				conection.sendMessage(gson.toJson(mes));

			} catch (IOException e) {
				MyWebSocketServlet.getSocketQueue().remove(mm.getTarget());
				e.printStackTrace();
			}
		} else {
			try {
				this.getConnection().sendMessage("その宛先は存在しません");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * メッセージ送信（全体）
	 * @param mm
	 */
	public void sendAllMessage(RecMessage mm) {
		Message mes = new Message(CMD_WIFICONFIG, mm.getMessage());
		Gson gson = new Gson();
		String str = gson.toJson(mes);
		
		for (Map.Entry<String, MyWebSocket> mws : MyWebSocketServlet.getSocketQueue().entrySet()) {
			try {
				mws.getValue().getConnection().sendMessage(str); // メッセージ配信
			} catch (IOException e) {
				MyWebSocketServlet.getSocketQueue().remove(mws.getKey()); //
				e.printStackTrace();
			}
		}
	}

}