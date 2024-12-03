package ca.antany.network.common.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.antany.network.common.exception.InitializationError;
import ca.antany.network.common.server.fn.OnConnect;
import ca.antany.network.common.server.fn.OnReceive;
import ca.antany.network.common.server.po.ClientInfo;
import ca.antany.network.common.server.sender.ClientSender;
import ca.antany.network.common.util.SafeClose;

public class UdpNetworkServer implements NetworkServer<DatagramPacket> {

	List<ClientInfo<DatagramPacket>> clients = new ArrayList<>();
	int port;
	InetAddress bindAddress = null;
	private DatagramSocket socket;
	boolean running = true;
	OnConnect<DatagramPacket> onConnectFunction = sc -> {
	};
	OnReceive<DatagramPacket> onReceiveFunction = null;
	private byte[] buffer = new byte[65000];

	private UdpNetworkServer(int port) {
		this.port = port;
	}

	public static NetworkServer<DatagramPacket> init(int port) {
		return new UdpNetworkServer(port);
	}

	@Override
	public NetworkServer<DatagramPacket> withBufferSize(int buffererSize) {
		buffer = new byte[buffererSize];
		return this;
	}

	@Override
	public List<ClientInfo<DatagramPacket>> getConnectedClients() {
		return clients;
	}

	@Override
	public NetworkServer<DatagramPacket> withBindAddress(String ipAddress) {
		try {
			bindAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new InitializationError(e.getMessage(), e);
		}
		return this;
	}

	@Override
	public NetworkServer<DatagramPacket> onConnect(OnConnect<DatagramPacket> onConnectFunction) {
		this.onConnectFunction = onConnectFunction;
		return this;
	}

	@Override
	public NetworkServer<DatagramPacket> onReceive(OnReceive<DatagramPacket> onReceiveFunction) {
		this.onReceiveFunction = onReceiveFunction;
		return this;
	}

	@Override
	public NetworkServer<DatagramPacket> startAsync() {
		try {
			socket = new DatagramSocket(null);
			socket.bind(new InetSocketAddress(bindAddress, port));
			while (running) {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				

				InetAddress address = packet.getAddress();

				ClientInfo.Builder<DatagramPacket> clientInfoBuilder = new ClientInfo.Builder<>();
				ClientInfo<DatagramPacket> clientInfo = clientInfoBuilder.withPort(packet.getPort())
						.withClientSocket(packet).withIp(address.getHostAddress())
						.withHostname(address.getCanonicalHostName()).withSender(new ClientSender() {

							@Override
							public void send(String str) {
								byte[] sendBytes = str.getBytes();
								var sendPacket = new DatagramPacket(sendBytes, sendBytes.length, address, packet.getPort());
								try {
									socket.send(sendPacket);
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
						}).build();
				
				if(onReceiveFunction!=null) {
					onReceiveFunction.onReceive(clientInfo, new String(packet.getData(), 0, packet.getLength()));
				}	
				if (!clients.contains(clientInfo)) {
					clients.add(clientInfo);
					onConnectFunction.process(packet);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public void stop() {
		running = false;
		SafeClose.close(socket);
	}

}
