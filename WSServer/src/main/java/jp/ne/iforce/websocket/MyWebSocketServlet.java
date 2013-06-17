package jp.ne.iforce.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWebSocketServlet extends WebSocketServlet {

	//クライアント接続リスト
	private static ConcurrentLinkedQueue<MyWebSocket> socketQueue = new ConcurrentLinkedQueue<MyWebSocket>();
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(MyWebSocketServlet.class);

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest hsr, String string) {
		return new MyWebSocket();
	}

	class MyWebSocket implements WebSocket.OnTextMessage {

		private final Integer IDLE_TIME = 60 * 60 * 1000;	//Connection有効時間（1h）
		private final Integer MAX_TEXTSIZE = 2048;	//Connection有効時間（1h）
		private Connection myConnection;

		public Connection getMyConnection() {
			return myConnection;
		}

		public void setMyConnection(Connection connection) {
			connection.setMaxIdleTime(IDLE_TIME);
			connection.setMaxTextMessageSize(MAX_TEXTSIZE);
			this.myConnection = connection;
		}

		/**
		 * 接続時CallBack
		 */
		@Override
		public void onOpen(Connection connection) {
			this.setMyConnection(connection);
			socketQueue.add(this);			//接続をリストに追加
			logger.info("open connection:"+socketQueue.size());
		}

		/**
		 * 切断時CallBack
		 */
		@Override
		public void onClose(int closeCode, String message) {
			socketQueue.remove(this);		//接続をリストから削除
			logger.info("close connection:"+socketQueue.size());
		}

		/**
		 * メッセージ受信時
		 */
		@Override
		public void onMessage(String data) {
			logger.info("message:"+data);
			for(MyWebSocket mws:socketQueue){
				try {
					mws.getMyConnection().sendMessage(data);	//メッセージ配信
				} catch (IOException e) {
					socketQueue.remove(mws);					//エラーの接続を削除
					e.printStackTrace();
				}

			}
		}

	}

}