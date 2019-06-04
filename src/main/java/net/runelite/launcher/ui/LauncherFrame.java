/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package net.runelite.launcher.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LauncherFrame extends JFrame
{
	private final JProgressBar bar;
	private final JLabel messageLabel;
	private JLabel spinner;

	public LauncherFrame()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception ex)
		{
			log.warn("Unable to set cross platform look and feel", ex);
		}

		UIManager.put("ProgressBar.background", new Color(61, 53, 45));
		UIManager.put("ProgressBar.foreground", new Color(244, 139, 0));
		UIManager.put("ProgressBar.selectionBackground", new Color(244, 139, 0));
		UIManager.put("ProgressBar.selectionForeground", new Color(61, 53, 45));
		UIManager.put("ProgressBar.border", new EmptyBorder(0, 0, 0, 0));
		UIManager.put("ProgressBar.horizontalSize", new Dimension(25, 25));
		UIManager.put("ProgressBarUI", BasicProgressBarUI.class.getName());

		setTitle("RuneLite");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(220, 290);
		setUndecorated(true);

		final Color foreground = new Color(165, 165, 165);

		messageLabel = new JLabel("Loading...");

		// main panel setup
		final JPanel panel = new JPanel();
		panel.setBackground(new Color(25, 25, 25));
		final GridBagLayout layout = new GridBagLayout();
		layout.columnWeights = new double[]{1};
		layout.rowWeights = new double[]{0, 0, 0, 0, 0};
		panel.setLayout(layout);

		// logo
		synchronized (ImageIO.class)
		{
			try
			{
				final BufferedImage logo = ImageIO.read(LauncherFrame.class.getResourceAsStream("runelite.png"));
				setIconImage(logo);

				final BufferedImage logoTransparent = ImageIO.read(LauncherFrame.class.getResourceAsStream("runelite_transparent.png"));
				final GridBagConstraints logoConstraints = new GridBagConstraints();
				logoConstraints.anchor = GridBagConstraints.SOUTH;
				logoConstraints.insets = new Insets(20, 0, 0, 0);
				panel.add(new JLabel(new ImageIcon(logoTransparent.getScaledInstance(96, 96, Image.SCALE_SMOOTH))), logoConstraints);
			}
			catch (IOException e)
			{
				log.warn("Error loading logo", e);
			}
		}

		// runelite title
		final JLabel title = new JLabel("RuneLite");
		title.setForeground(Color.WHITE);
		title.setFont(FontManager.getRunescapeFont());
		final GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.gridy = 1;
		panel.add(title, titleConstraints);

		// main message
		messageLabel.setForeground(foreground);
		messageLabel.setFont(FontManager.getRunescapeSmallFont());
		final GridBagConstraints messageConstraints = new GridBagConstraints();
		messageConstraints.gridy = 2;
		messageConstraints.insets = new Insets(30, 0, 30, 0);
		panel.add(messageLabel, messageConstraints);

		// progressbar
		final GridBagConstraints progressConstraints = new GridBagConstraints();
		progressConstraints.insets = new Insets(0, 30, 0, 30);
		progressConstraints.fill = GridBagConstraints.HORIZONTAL;
		progressConstraints.anchor = GridBagConstraints.SOUTH;
		progressConstraints.gridy = 3;

		bar = new JProgressBar();
		bar.setFont(FontManager.getRunescapeSmallFont());
		bar.setStringPainted(true);
		panel.add(bar, progressConstraints);

		// spinner
		// ImageIO does not handle gifs properly
		spinner = new JLabel(new ImageIcon(getClass().getResource("loading_spinner_darker.gif")));
		spinner.setVisible(false);
		panel.add(spinner, progressConstraints);

		setContentPane(panel);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void progress(String filename, int bytes, int total)
	{
		if (total == 0)
		{
			return;
		}

		int percent = (int) (((float) bytes / (float) total) * 100f);
		bar.setString(filename + " (" + percent + "%)");
		bar.setValue(percent);
	}

	public void setMessage(final String message)
	{
		messageLabel.setText(message);
	}

	public void switchToSpinner()
	{
		bar.setVisible(false);
		setMessage("Launching client...");
		spinner.setVisible(true);
	}

	public void close()
	{
		setVisible(false);
		dispose();
		log.info("Frame closed");
	}
}
