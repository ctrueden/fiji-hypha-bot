/*-
 * #%L
 * Generate and execute code using AI
 * %%
 * Copyright (C) 2023 ImageJ2 developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imagej.assistant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.scijava.Context;
import org.scijava.script.ScriptService;

/** Business logic of the chatbot. */
public class HyphaBot {

	private Context ctx;
	private ScriptService ss;
	private String serverURL;

	public HyphaBot(Context ctx, String serverURL) {
		this.ctx = ctx;
		this.ss = ctx.service(ScriptService.class);
		this.serverURL = serverURL;
	}

	public CompletableFuture<Response> ask(String query) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return askAndWait(query);
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	public Response askAndWait(String query) throws IOException {
		if (true) return new Response(query, ""); // FIXME

		String postData = JsonEncoder.encodeQueryToJson(query);
		byte[] bytes = postData.getBytes(StandardCharsets.UTF_8);
		String endpoint = "/services/hypha-bot/chat";
		HttpURLConnection connection = (HttpURLConnection) new URL(serverURL + endpoint).openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Encoding", "json");
		connection.setDoOutput(true);
		connection.getOutputStream().write(bytes);

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			try (InputStream is = connection.getInputStream()) {
				byte[] responseBytes = readAllBytes(is);
				String response = new String(responseBytes, StandardCharsets.UTF_8);
				return new Response(query, response);
			}
		}
		throw new IOException("Request failed with HTTP error code: " + responseCode);
	}

	public static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = is.read(buffer)) != -1) {
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}
}
