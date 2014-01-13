package edu.osu.lapis.transmission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class LapisTransmissionApacheHttpClientImpl extends LapisTransmissionBaseImpl {

	private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(); //TODO SET
	
	@Override
	public ClientResponse executeClientCall(final ClientCall clientCall) {
//		System.out.println("Using apache http client"); //TODO REMOVE
		StopWatch sw = new StopWatch();
		sw.start();
		URI uri;
		try {
			uri = new URI(clientCall.getUri());
		} catch (URISyntaxException e1) {
			throw new RuntimeException(e1);
		}
		final HttpUriRequest request;
		switch(clientCall.getMethod()) {
		case GET:
			request = new HttpGet(uri);
			break;
		case DELETE:
			request = new HttpDelete(uri);
			break;
		case PUT:
		case POST: 
			request = getHttpRequestWithEntity(clientCall);
			break;
		default:
			throw new IllegalArgumentException("Unable to handle client call with method " + clientCall.getMethod());
		}
		CloseableHttpResponse response = null;
		try {
			//TODO THROW EXCEPTION IN THE EVENT OF A 400 OR 500 STATUS CODE
			
			
			
			response = getHttpClient().execute(request);
			HttpEntity entity = response.getEntity();
			final byte[] payload = entity == null ? null : getEntityBytes(entity);
			int statusCode = response.getStatusLine().getStatusCode();
			return new ClientResponse(statusCode, payload);
		} catch (Exception e) {
			throw new RuntimeException(e); //TODO IMPROVE
		} finally {
			if(response != null) {
				try {
//					System.out.println("response class: " + response.getClass());
					response.getEntity().getContent().close();
					response.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else System.out.println("response was null");
			if(request instanceof HttpEntityEnclosingRequestBase) {
				((HttpEntityEnclosingRequestBase)request).releaseConnection();
				((HttpEntityEnclosingRequestBase)request).abort();
				((HttpEntityEnclosingRequestBase)request).completed();
			} //else System.out.println("not HttpEntityEnclosingRequest");
			sw.stop();
			System.out.println("TOOK " + sw.getTime() + " MILLIS.");
		}
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(connectionManager).build();
	}

	private HttpUriRequest getHttpRequestWithEntity(ClientCall clientCall) {
		final HttpEntityEnclosingRequestBase entityRequest;
		switch(clientCall.getMethod()) {
		case PUT: 
			entityRequest = new HttpPut(clientCall.getUri());
			break;
		case POST:
			entityRequest = new HttpPost(clientCall.getUri());
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot create entity request with method " + clientCall.getMethod());
		}
		entityRequest.setEntity(new ByteArrayEntity(clientCall.getPayload()));
		return entityRequest;
	}

	private byte[] getEntityBytes(HttpEntity entity) throws IOException {
		long length = entity.getContentLength();
		ByteArrayOutputStream baos = getByteArrayOutputStreamForLength(length);
		entity.writeTo(baos);
		return baos.toByteArray();
	}
	
	private ByteArrayOutputStream getByteArrayOutputStreamForLength(long length) {
		Validate.isTrue(length < Integer.MAX_VALUE, "Entity length too long: %d", length);
		if(length > 0) {
			return new ByteArrayOutputStream((int)length);
		} else {
			return new ByteArrayOutputStream();
		}
	}
}
