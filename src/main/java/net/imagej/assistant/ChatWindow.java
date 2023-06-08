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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * A GUI for the hypha-bot.
 * 
 * @author Curtis Rueden
 */
public class ChatWindow extends JFrame {
	private HyphaBot bot;
	private ScriptRunner runner;

	private JTextField inputField;
	private JTextArea responseArea;

	public ChatWindow(String title, HyphaBot bot, ScriptRunner runner) {
		this.bot = bot;
		this.runner = runner;
		setTitle(title);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		responseArea = new JTextArea();
		responseArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(responseArea);
		add(scrollPane, BorderLayout.CENTER);

		JPanel bottomBar = new JPanel();
		bottomBar.setLayout(new BorderLayout());
		JButton showCode = new JButton("Show code");
		bottomBar.add(showCode, BorderLayout.EAST);
		inputField = new JTextField();
		inputField.addActionListener(e -> {
			String words = inputField.getText();
			responseArea.append("> " + words + "\n\n");
			inputField.setText("");
			say(words);
		});
		bottomBar.add(inputField, BorderLayout.CENTER);
		add(bottomBar, BorderLayout.SOUTH);

		// Set the initial size and font based on the screen's resolution
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
		double scaleFactor = graphicsDevice.getDefaultConfiguration().getDefaultTransform().getScaleX();
		scaleFactor = 2; // CTR TEMP: hardcode for my screen. Stupid Java 8.
		System.out.println(scaleFactor);
		int screenWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width / scaleFactor);
		int screenHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().height / scaleFactor);
		setSize(screenWidth / 2, screenHeight / 2);

		Font defaultFont = responseArea.getFont().deriveFont(16f); // Set the default font size to 16
		Font scaledFont = defaultFont.deriveFont((float) (defaultFont.getSize() * scaleFactor));
		responseArea.setFont(scaledFont);
		responseArea.setFocusable(false);
		inputField.setFont(scaledFont);
		showCode.setFont(scaledFont);

		setVisible(true);
	}

	private void say(String input) {
		String promptEngineering = ""; // FIXME
		String query = promptEngineering + "\n\n" + input;
		bot.ask(query).thenAccept(response -> {
			responseArea.append(response.description + "\n\n");
			if (runner.isSupportedLanguage(response.language)) {
				runner.run(response.language, response.code);
			}
			else {
				responseArea.append("<SORRY, UNSUPPORTED LANGUAGE '" + response.language + "'\n\n");
			}
		});
	}
}
