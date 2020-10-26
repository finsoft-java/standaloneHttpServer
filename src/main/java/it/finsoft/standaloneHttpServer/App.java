package it.finsoft.standaloneHttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class App {

	private final static int port = 9998;

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

		server.createContext("/test", new HttpHandler() {

			@Override
			public void handle(HttpExchange t) throws IOException {
				String body = "Hello world";
				setBody(t, 200, body);
			}
		});

		server.createContext("/echo", new HttpHandler() {

			@Override
			public void handle(HttpExchange t) throws IOException {

				Headers hd = t.getRequestHeaders();

				String body = "";
				body += "Method: " + t.getRequestMethod() + "\r\n";
				body += "Body: " + convertStreamToString(t.getRequestBody()) + "\r\n";
				body += "Headers: \r\n";
				for (String header : hd.keySet()) {
					body += header + " : " + hd.get(header) + "\r\n";
				}

				setBody(t, 200, body);
			}
		});

		server.createContext("/err", new HttpHandler() {
			@Override
			public void handle(HttpExchange t) throws IOException {
				String body = "Internal server error";
				setBody(t, 500, body);
			}
		});

		server.setExecutor(null); // creates a default executor
		System.out.println("Web server running on port " + port + "...");
		System.out.println("Accepted paths:");
		System.out.println("/test");
		System.out.println("/echo");
		System.out.println("/err");
		server.start();
	}

	static String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String ret = s.hasNext() ? s.next() : "";
		s.close();
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static void setBody(HttpExchange t, int httpStatus, String body) throws IOException {
		t.sendResponseHeaders(httpStatus, body.length());
		OutputStream os = t.getResponseBody();
		os.write(body.getBytes());
		os.close();
		System.out.println(body);
	}
}
