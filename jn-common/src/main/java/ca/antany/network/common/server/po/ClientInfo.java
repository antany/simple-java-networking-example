package ca.antany.network.common.server.po;

import java.util.Objects;

import ca.antany.network.common.server.sender.ClientSender;

public class ClientInfo<T> {

	private String ip;
	private int port;
	private String hostname;
	private T clientSocket;
	private ClientSender sender;

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getHostname() {
		return hostname;
	}

	public T getClientSocket() {
		return clientSocket;
	}

	public ClientSender getSender() {
		return sender;
	}

	@Override
	public String toString() {
		return "ClientInfo [ip=" + ip + ", port=" + port + ", hostname=" + hostname + "]";
	}

	public static class Builder<T> {
		ClientInfo<T> clientInfo;

		public Builder() {
			clientInfo = new ClientInfo<>();
		}

		public Builder<T> withIp(String ip) {
			clientInfo.ip = ip;
			return this;
		}

		public Builder<T> withPort(int port) {
			clientInfo.port = port;
			return this;
		}

		public Builder<T> withHostname(String hostname) {
			clientInfo.hostname = hostname;
			return this;
		}

		public Builder<T> withClientSocket(T clientSocket) {
			clientInfo.clientSocket = clientSocket;
			return this;
		}

		public Builder<T> withSender(ClientSender sender) {
			clientInfo.sender = sender;
			return this;
		}

		public ClientInfo<T> build() {

			return clientInfo;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(ip, port);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (obj instanceof ClientInfo<?> other) {
			return Objects.equals(ip, other.ip) && port == other.port;
		}

		return false;
	}

}
