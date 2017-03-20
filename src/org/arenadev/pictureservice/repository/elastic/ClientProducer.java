package org.arenadev.pictureservice.repository.elastic;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

@Default
@ApplicationScoped
public class ClientProducer {

	private TransportClient client;
	private PreBuiltTransportClient preBuiltTransportClient;
	
	public ClientProducer() {
		try {
			preBuiltTransportClient = new PreBuiltTransportClient(Settings.EMPTY);
			client = preBuiltTransportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(System.getenv("ELASTICSEARCH_ADDRESS")), Integer.valueOf(System.getenv("ELASTICSEARCH_PORT"))));
		} catch (NumberFormatException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TransportClient getClient() {
		return client;
	}

	public void close() {
		client.close();
		preBuiltTransportClient.close();
	}
}