package ca.antany.network.common.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.antany.network.common.exception.InitializationError;
import ca.antany.network.common.server.fn.OnConnect;
import ca.antany.network.common.server.fn.OnReceive;
import ca.antany.network.common.server.po.ClientInfo;
import ca.antany.network.common.server.receiver.TcpSocketReceiver;
import ca.antany.network.common.server.sender.ClientSender;
import ca.antany.network.common.util.SafeClose;

public class TcpNetworkServer implements NetworkServer<Socket> {

	List<ClientInfo<Socket>> clients = new ArrayList<>();
	ServerSocket socket;
	int port;
	boolean running = true;
	Thread serverThread;
	InetAddress bindAddress = null;
	OnConnect<Socket> onConnectFunction = sc->{};
	OnReceive<Socket> onReceiveFunction = null; 
	
	private TcpNetworkServer(int port) {
		this.port = port;
	}

	public static NetworkServer<Socket> init(int port) {
		return new TcpNetworkServer(port);
	}

	
	@Override
	public List<ClientInfo<Socket>> getConnectedClients() {
		return clients;
	}

	@Override
	public NetworkServer<Socket> withBindAddress(String ipAddress) {
		try {
			bindAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new InitializationError(e.getMessage(), e);
		}
		return this;
	}

	@Override
	public NetworkServer<Socket> onConnect(OnConnect<Socket> onConnectFunction) {
		this.onConnectFunction = onConnectFunction;
		return this;
	}

	@Override
	public NetworkServer<Socket> startAsync() {
		var server = new Runnable() {

			@Override
			public void run() {
				try {
					socket = new ServerSocket();
					socket.bind(new InetSocketAddress(bindAddress, port));
					while (running) {
						Socket clientSocket = socket.accept();
						onConnectFunction.process(clientSocket);

						ClientInfo.Builder<Socket> clientInfoBuilder = new ClientInfo.Builder<>();
						ClientInfo<Socket> clientInfo = clientInfoBuilder.withPort(clientSocket.getPort())
								.withHostname(clientSocket.getInetAddress().getCanonicalHostName())
								.withClientSocket(clientSocket)
								.withIp(clientSocket.getInetAddress().getHostAddress())
								.withSender(new ClientSender() {
									@SuppressWarnings("resource")
									PrintWriter pw  = new PrintWriter(clientSocket.getOutputStream(),true);
									@Override
									public void send(String str) {
										pw.println(str);
									}
								})
								.build();
						
						new TcpSocketReceiver(clientInfo, onReceiveFunction);
						
						clients.add(clientInfo);

					}
				} catch (IOException e) {
					throw new InitializationError(e.getMessage(), e);
				}

			}

		};

		serverThread = new Thread(server);
		serverThread.start();
		return this;

	}

	@Override
	public void stop() {
		running = false;
		SafeClose.close(socket);

	}

	@Override
	public NetworkServer<Socket> onReceive(OnReceive<Socket> onReceiveFunction) {
		this.onReceiveFunction = onReceiveFunction;
		return this;
	}

}
