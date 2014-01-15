package edu.osu.lapis.transmission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import edu.osu.lapis.Logger;
import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.transmission.ClientCall.RestMethod;

public class LapisTransmissionApacheHttpClientImpl extends LapisTransmissionBaseImpl {

	private final Logger logger = Logger.getLogger(getClass());
	
	private final HttpClientConnectionManager connectionManager;
	
	public LapisTransmissionApacheHttpClientImpl(HttpClientConnectionManager connManager) {
		this.connectionManager = connManager;
	}
	
	@Override
	public ClientResponse executeClientCall(final ClientCall clientCall) {
		StopWatch sw = new StopWatch();
		sw.start();
		CloseableHttpResponse httpResponse = null;
		HttpUriRequest httpRequest = null;
		try {	
			httpRequest = getHttpUriRequest(clientCall);
			httpResponse = getHttpClient().execute(httpRequest);
			return getClientResponse(httpResponse);
		} catch (URISyntaxException e) {
			throw new LapisClientException("Error parsing call uri: " + clientCall.getUri(), e);
		} catch (IOException e) {
			throw new LapisClientException(e);
		} finally {
			closeHttpResponse(httpResponse);
			releaseRequestResources(httpRequest);
			sw.stop();
			logger.trace("Took %d millis to execute %s.", sw.getTime(), clientCall);
		}
	}
	
	private HttpUriRequest getHttpUriRequest(ClientCall call) throws URISyntaxException {
		URI uri = new URI(call.getUri());
		switch(call.getMethod()) {
		case GET:
			return new HttpGet(uri);
		case DELETE:
			return new HttpDelete(uri);
		case PUT:
		case POST: 
			return getHttpRequestWithEntity(uri, call.getMethod(), call.getPayload());
		default:
			throw new IllegalArgumentException("Unable to handle client call with method " + call.getMethod());
		}
	}

	private HttpUriRequest getHttpRequestWithEntity(URI uri, RestMethod method, byte[] payload) {
		final HttpEntityEnclosingRequestBase entityRequest;
		switch(method) {
		case PUT: 
			entityRequest = new HttpPut(uri);
			break;
		case POST:
			entityRequest = new HttpPost(uri);
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot create entity request with method " +method);
		}
		entityRequest.setEntity(new ByteArrayEntity(payload));
		return entityRequest;
	}
	
	private CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(connectionManager).build();
	}
	
	private ClientResponse getClientResponse(HttpResponse httpResponse) throws IOException {
		HttpEntity entity = httpResponse.getEntity();
		final byte[] payload = entity == null ? null : getEntityBytes(entity);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if(statusCode >= 200 && statusCode < 300) {
			return new ClientResponse(statusCode, payload);
		} else {
			throw new LapisClientExceptionWithStatusCode("Encountered " + statusCode 
					+ " error with the following response entity: "
					+ new String(payload), statusCode);
		}
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
	
	private void closeHttpResponse(CloseableHttpResponse resp) {
		if(resp != null) {
			try {
				resp.close();
			} catch (IOException e) {
				//do nothing
			}
		} else {
			logger.trace("Did not close response because it was null.");
		}		
	}
	
	private void releaseRequestResources(HttpUriRequest httpRequest) {
		if(httpRequest instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase entityReq = (HttpEntityEnclosingRequestBase)httpRequest;
			logger.trace("Releasing resources for request: %s.", entityReq);
			entityReq.releaseConnection();
			entityReq.abort();
			entityReq.completed();
		} else {
			logger.trace("HttpUriRequest was not an HttpEntityEnclosingRequestBase: %s. No resources released.", httpRequest);
		}
	}
}