/*
 * Copyright (c) 2019, jplsek <github.com/jplsek>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.launcher.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import net.runelite.launcher.ui.LauncherFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LauncherSocket
{
	private static final Logger logger = LoggerFactory.getLogger(LauncherSocket.class);

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader in;
	private final LauncherFrame frame;

	public LauncherSocket(LauncherFrame frame)
	{
		this.frame = frame;
	}

	public void start() throws IOException
	{
		serverSocket = new ServerSocket(0);

		final Thread socketThread = new Thread(() ->
		{
			try
			{
				clientSocket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String clientMessage;
				while ((clientMessage = in.readLine()) != null)
				{
					if (clientMessage.equals("\0"))
					{
						break;
					}
					frame.setMessage(clientMessage);
					logger.info(clientMessage);
				}

				stop();
			}
			catch (IOException e)
			{
				logger.warn("Unexpected socket error", e);
			}
		});

		socketThread.start();
	}

	private void stop() throws IOException
	{
		in.close();
		clientSocket.close();
		serverSocket.close();
		logger.info("Socket closed");
		frame.close();
	}

	public int getPort()
	{
		return serverSocket.getLocalPort();
	}
}
