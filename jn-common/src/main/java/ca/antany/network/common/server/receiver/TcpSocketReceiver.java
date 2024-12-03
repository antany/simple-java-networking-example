package ca.antany.network.common.server.receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import ca.antany.network.common.exception.InitializationError;
import ca.antany.network.common.server.fn.OnReceive;
import ca.antany.network.common.server.po.ClientInfo;

public class TcpSocketReceiver implements Runnable, SocketReceiver<Socket> {

	private BufferedReader reader = null;
	private boolean runnable = true;
	OnReceive<Socket> onReceiveFunction = null;
	ClientInfo<Socket> clientInfo = null;

	public TcpSocketReceiver(ClientInfo<Socket> clientInfo, OnReceive<Socket> onReceiveFunction) throws IOException {
		reader = new BufferedReader(new InputStreamReader(clientInfo.getClientSocket().getInputStream()));
		this.clientInfo = clientInfo;
		this.onReceiveFunction = onReceiveFunction;
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		while (runnable && onReceiveFunction!=null) {
			try {
				String data = reader.readLine();
				onReceiveFunction.onReceive(clientInfo, data);
			} catch (IOException e) {
				throw new InitializationError(e.getMessage(), e);
			}
		}
	}

}
