package ca.antany.network.common.server;

import java.net.DatagramPacket;
import java.net.Socket;
import java.util.List;

import ca.antany.network.common.server.fn.OnConnect;
import ca.antany.network.common.server.fn.OnReceive;
import ca.antany.network.common.server.po.ClientInfo;

public interface NetworkServer<T> {
	
	public static NetworkServer<Socket> createTCPbuilder(int port) {
		return TcpNetworkServer.init(port);
	}
	
	public static NetworkServer<DatagramPacket> createUDPbuilder(int port) {
		return UdpNetworkServer.init(port);
	}

	public List<ClientInfo<T>> getConnectedClients();

	public NetworkServer<T> withBindAddress(String ipAddress);

	public NetworkServer<T> onConnect(OnConnect<T> onConnectFunction);
	
	public NetworkServer<T> onReceive(OnReceive<T> onReceiveFunction);

	public NetworkServer<T> startAsync();

	public void stop();
	
	public default NetworkServer<T> withBufferSize(int buffererSize){
		throw new UnsupportedOperationException("BufferSize is only appicable for UDP");
		
	}

}
