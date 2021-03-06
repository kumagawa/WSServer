package jp.ne.iforce.websocket;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class MyWebSocketServlet extends WebSocketServlet {

	// クライアント接続リスト
	private static Map<String, MyWebSocket> socketQueue = new HashMap<String, MyWebSocket>();
	private static final long serialVersionUID = 1L;

	/**
	 * Clientから接続時にCallbackされる
	 */
	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest request, String string) {

		//接続を識別するための重複のないIDを生成（ランダム数字4桁）
		//request.getSession().getId()でもOKかと。

		String sessionId;
		while(true){
			sessionId = RandomStringUtils.randomNumeric(4);
			if (!MyWebSocketServlet.getSocketQueue().containsKey(sessionId)) {
				break;
			}
		}
		return new MyWebSocket(sessionId);
	}

	/**
	 * ConcurrentLinkedQueue getter
	 *
	 * @return
	 */
	public static Map<String, MyWebSocket> getSocketQueue() {
		return socketQueue;
	}

	/**
	 * ConcurrentLinkedQueue setter
	 *
	 * @param socketQueue
	 */
	public static void setSocketQueue(Map<String, MyWebSocket> socketQueue) {
		MyWebSocketServlet.socketQueue = socketQueue;
	}

	/**
	 * クライアント接続リスト追加
	 *
	 * @param myWebSocket
	 */
	public static void addSocketQueue(String key, MyWebSocket myWebSocket) {
		MyWebSocketServlet.socketQueue.put(key, myWebSocket);
	}

	/**
	 * クライアント接続リスト削除
	 *
	 * @param myWebSocket
	 */
	public static void removeSocketQueue(String key) {
		MyWebSocketServlet.socketQueue.remove(key);
	}

}
