package ca.antany.network.common.server.fn;

import ca.antany.network.common.server.po.ClientInfo;

public interface OnReceive<T> {

	public void onReceive(ClientInfo<T> clientInfo, String message);
}
