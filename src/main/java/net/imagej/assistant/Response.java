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

public class Response {
	public final String query;
	public final String description;
	public final String language;
	public final String code;

	public Response(String query, String s) {
		this.query = query;

		// FIXME: Remove this debug override.
		s = "" +
			"This Groovy script shows a dialog box with the message 'Hello world' displayed.\n" +
			"```groovy\n" +
		  "#@ UIService ui\n" +
			"ui.showDialog(\"Hello world\")\n" +
			"```\n" +
			"And there you have it!\n" +
			"";

		// Input format is Markdown-style, with description (maybe) followed by a
		// code fence with language annotation, then the code itself.
		int backticks = s.indexOf("```");
		if (backticks < 0) {
			// No code given.
			description = s;
			language = null;
			code = null;
			return;
		}

		// Parse language tag.
		String text = s.substring(0, backticks);
		String codeAnd = s.substring(backticks + 3);
		String[] lines = codeAnd.split("\\n", 2);
		if (lines.length > 1 && lines[0].matches("^[a-z]*$")) {
			language = lines[0];
			lines = lines[1].split("```", 2);
			code = lines[0];
			description = lines.length > 1 ? text + lines[1] : text;
			return;
		}
		throw new IllegalArgumentException("No language specified in code block");
	}
}
