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
			this.getConnection().sendMessage(this.sessionId);
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
			MyMessage mm = gson.fromJson(data, MyMessage.class);
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
	public void sendMessage(MyMessage mm) {
		// Connection存在確認
		if (MyWebSocketServlet.getSocketQueue().containsKey(mm.getTarget())) {
			try {
				Connection cpnnection = MyWebSocketServlet.getSocketQueue().get(mm.getTarget()).getConnection();
				cpnnection.sendMessage(mm.getMessage());
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
	public void sendAllMessage(MyMessage mm) {
		for (Map.Entry<String, MyWebSocket> mws : MyWebSocketServlet.getSocketQueue().entrySet()) {
			try {
				mws.getValue().getConnection().sendMessage(mm.getMessage()); // メッセージ配信
			} catch (IOException e) {
				MyWebSocketServlet.getSocketQueue().remove(mws.getKey()); //
				e.printStackTrace();
			}
		}
	}

}