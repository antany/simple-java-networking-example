package ca.antany.netowrk.server;

import java.net.DatagramPacket;

import ca.antany.network.common.server.NetworkServer;

public class ServerMain {

	public static void main(String[] args) {

		NetworkServer<DatagramPacket> nc = NetworkServer
				.createUDPbuilder(9001)
				.onConnect(System.out::println)
				.onReceive((ci,message)->{
					ci.getSender().send(ci+"-->"+message);
				})
				.withBindAddress("0.0.0.0")
				.startAsync();
	}
}
