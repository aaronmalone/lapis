package edu.osu.lapis.client;

import com.google.common.base.Preconditions;
import edu.osu.lapis.Logger;
import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ApacheHttpClientImpl extends AbstractClient {

	private final Logger logger = Logger.getLogger(getClass());

	private final HttpClientConnectionManager connectionManager;

	public ApacheHttpClientImpl(HttpClientConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public byte[] doCall(ClientMethod method, byte[] messageBody, String uri) {
		StopWatch sw = new StopWatch();
		sw.start();
		CloseableHttpResponse httpResponse = null;
		HttpUriRequest httpRequest = null;
		try {
			httpRequest = getHttpUriRequest(method, messageBody, uri);
			httpResponse = getHttpClient().execute(httpRequest);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			byte[] responseEntity = getResponseMessageBody(httpResponse);
			if (statusCode >= 400) {
				String message = "Encountered " + statusCode + " error."
						+ ((responseEntity != null) ? " Response entity: " + new String(responseEntity) : "");
				throw new LapisClientExceptionWithStatusCode(message, statusCode);
			}
			return responseEntity;
		} catch (URISyntaxException e) {
			throw new LapisClientException("Error parsing call uri: " + uri, e);
		} catch (IOException e) {
			throw new LapisClientException(e);
		} finally {
			closeHttpResponse(httpResponse);
			releaseRequestResources(httpRequest);
			sw.stop();
			logger.debug("Took %d millis to execute %s %s", sw.getTime(), method, uri);
		}
	}

	@Override
	public byte[] doCall(ClientMethod method, String uri) {
		return doCall(method, null, uri);
	}

	private HttpUriRequest getHttpUriRequest(ClientMethod method, byte[] messageBody, String uri) throws URISyntaxException {
		URI u = new URI(uri);
		switch (method) {
			case GET:
				return new HttpGet(u);
			case DELETE:
				return new HttpDelete(u);
			case POST:
			case PUT:
				HttpEntityEnclosingRequestBase entityRequest = method == ClientMethod.POST ? new HttpPost(u) : new HttpPut(u);
				entityRequest.setEntity(new ByteArrayEntity(messageBody));
				return entityRequest;
			default:
				throw new IllegalArgumentException("unknown method: " + method);
		}
	}

	private CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(connectionManager).build();
	}

	private byte[] getResponseMessageBody(CloseableHttpResponse httpResponse) throws IOException {
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			return getEntityBytes(entity);
		} else {
			return null;
		}
	}

	private byte[] getEntityBytes(HttpEntity entity) throws IOException {
		long length = entity.getContentLength();
		if (length == 0) {
			return null;
		} else {
			ByteArrayOutputStream baos = getByteArrayOutputStreamForLength(length);
			entity.writeTo(baos);
			return baos.toByteArray();
		}
	}

	private ByteArrayOutputStream getByteArrayOutputStreamForLength(long length) {
		Preconditions.checkArgument(length < Integer.MAX_VALUE, "Entity length too long: %d", length);
		if (length > 0) {
			return new ByteArrayOutputStream((int) length);
		} else {
			return new ByteArrayOutputStream();
		}
	}

	private void closeHttpResponse(CloseableHttpResponse resp) {
		if (resp != null) {
			try {
				resp.close();
			} catch (IOException e) {
				//do nothing
			}
		} else {
			logger.warn("Did not close response because it was null.");
		}
	}

	private void releaseRequestResources(HttpUriRequest req) {
		if (req instanceof HttpEntityEnclosingRequestBase) {
			HttpEntityEnclosingRequestBase entityReq = (HttpEntityEnclosingRequestBase) req;
			logger.trace("Releasing resources for request: %s.", entityReq);
			entityReq.releaseConnection();
			entityReq.abort();
			entityReq.completed();
		} else {
			logger.trace(
					"HttpUriRequest was not an HttpEntityEnclosingRequestBase: %s. No resources released.", req);
		}
	}
}
