package net.netgoogle.seb;

import net.netgoogle.seb.panel.BallPanel;
import net.netgoogle.seb.panel.ControlPanel;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import java.awt.Color;

public class Main {

	public static void main(String[] args) {
		// Tworzenie JFrame
		JFrame frame = new JFrame("Projekt grawitacja");

		// Ustawienie JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		// Tworzenie głównych paneli BallPanel oraz ControlPanel
		// oraz dodanie ich do JFrame
		BallPanel ballCanvas = new BallPanel();
		ControlPanel controlPanel = new ControlPanel(ballCanvas);

		frame.getContentPane().add(ballCanvas);
		frame.getContentPane().add(controlPanel);
		frame.pack();
		frame.setVisible(true);

		// Rozpoczęcie pętli głównej
		ballCanvas.startMainLoop();
	}

}
