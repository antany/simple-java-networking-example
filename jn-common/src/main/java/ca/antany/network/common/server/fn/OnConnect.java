package ca.antany.network.common.server.fn;

public interface OnConnect<T> {
	public void process(T t);
}
