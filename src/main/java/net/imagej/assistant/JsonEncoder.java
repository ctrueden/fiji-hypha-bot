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
import java.util.HashMap;
import java.util.Map;

public final class JsonEncoder {

	private JsonEncoder() { }

	public static String encodeQueryToJson(String query) {
		Map<String, String> jsonMap = new HashMap<>();
		jsonMap.put("question", query);

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		boolean firstEntry = true;

		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			if (!firstEntry) {
				jsonBuilder.append(",");
			}
			jsonBuilder.append("\"").append(entry.getKey()).append("\":\"");
			jsonBuilder.append(escapeSpecialCharacters(entry.getValue())).append("\"");
			firstEntry = false;
		}

		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

	private static String escapeSpecialCharacters(String input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (char c : input.toCharArray()) {
			switch (c) {
				case '\\':
					stringBuilder.append("\\\\");
					break;
				case '\"':
					stringBuilder.append("\\\"");
					break;
				case '\b':
					stringBuilder.append("\\b");
					break;
				case '\f':
					stringBuilder.append("\\f");
					break;
				case '\n':
					stringBuilder.append("\\n");
					break;
				case '\r':
					stringBuilder.append("\\r");
					break;
				case '\t':
					stringBuilder.append("\\t");
					break;
				default:
					if (Character.isISOControl(c)) {
						stringBuilder.append(String.format("\\u%04X", (int) c));
					} else {
						stringBuilder.append(c);
					}
					break;
			}
		}
		return stringBuilder.toString();
	}
}
