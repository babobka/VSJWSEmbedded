package ru.babobka.vsjws.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.babobka.vsjws.model.HttpResponse;

/**
 * Created by dolgopolov.a on 29.12.15.
 */
public class HttpUtil {

	private HttpUtil() {

	}

	public static void writeResponse(OutputStream os, HttpResponse response,
			boolean noContent) throws IOException {
		if (response != null) {
			StringBuilder header = new StringBuilder("HTTP/1.1 "
					+ response.getResponseCode() + "\n");
			Map<String, String> headers = new LinkedHashMap<>();
			headers.put("Server:", "vsjws");
			headers.put("Content-Type:", response.getContentType());
			headers.put("Content-Length:",
					String.valueOf(response.getContentLength()));
			headers.put("Connection:", "close");	
			headers.putAll(response.getHttpCookieHeaders());
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				header.append(entry.getKey());
				header.append(" ");
				header.append(entry.getValue());
				header.append("\r\n");
			}
			header.append("\r\n");
			os.write(header.toString().getBytes(HttpResponse.MAIN_ENCODING));
			if (!noContent) {
				if (response.getFile() != null) {
					byte[] buf = new byte[8192];
					int c;
					InputStream is = null;
					try {
						is = new FileInputStream(response.getFile());
						while ((c = is.read(buf, 0, buf.length)) > 0) {
							os.write(buf, 0, c);
							os.flush();
						}
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					os.write(response.getContent());
				}
			}
		}
		os.flush();
	}

	public static String getContent(int contentLength, BufferedReader br)
			throws IOException {
		StringBuilder body = new StringBuilder();
		if (contentLength != 0) {
			for (int i = 0; i < contentLength; i++) {
				int a = br.read();
				body.append((char) a);
			}
		}
		return body.toString();
	}

	public static Map<String, String> getCookies(String s) {
		HashMap<String, String> cookies = new HashMap<>();
		String[] cookiesArray = s.substring(s.indexOf(':') + 2, s.length())
				.split("; ");
		for (int i = 0; i < cookiesArray.length; i++) {
			String[] cookie = cookiesArray[i].split("=");
			cookies.put(cookie[0], cookie[1]);
		}
		return cookies;
	}

	public static String generateSessionId() {
		return String.valueOf((long) (Math.random() * Long.MAX_VALUE));
	}

	public static Map<String, String> getParams(String paramText) {
		HashMap<String, String> params = new HashMap<>();
		if (paramText != null && paramText.length() > 0) {
			String[] paramsArray = paramText.split("&");
			for (int i = 0; i < paramsArray.length; i++) {
				String[] keyValue = paramsArray[i].split("=");
				if (keyValue.length > 1) {
					params.put(keyValue[0], keyValue[1]);
				} else {
					params.put(keyValue[0], null);
				}
			}
		}
		return params;
	}
}
