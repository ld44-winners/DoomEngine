package net.bobmandude9889.main;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Main {

	static JFrame frame;
	static Display display;
	
	static final int FPS = 60;
	
	static final int width = 800;
	static final int height = 800;
	
	public static void main(String[] args) {
		try {
			display = new Display(width, height, new Robot());
			frame = new JFrame("Doom Engine");
			frame.setSize(width, height);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.add(display);
			frame.setVisible(true);
			frame.addKeyListener(display);
			frame.addMouseMotionListener(display);
			
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");
			frame.getContentPane().setCursor(blankCursor);
			
			new Thread(() -> {
				long lastUpdate = System.currentTimeMillis();
				for(;;) {
					if (System.currentTimeMillis() - lastUpdate >= 1000 / FPS) {
						frame.repaint();
						lastUpdate = System.currentTimeMillis();
					}
				}
			}).start();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
}
