package net.bobmandude9889.doomEngine.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import net.bobmandude9889.doomEngine.World.ResourceLoader;
import net.bobmandude9889.doomEngine.World.World;

public class Main {
	
	static final int FPS = 60;
	
	static final int width = 800;
	static final int height = 800;
	
	public static Renderer renderer;

	public static void main(String[] args) {
		
		
		JFrame frame = new JFrame("Doom Engine");
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
//		frame.add(display);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		frame.getContentPane().setCursor(blankCursor);
		
		World world = null;
		try {
			world = ResourceLoader.loadMap("map.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		InputListener inputs = null;
		try {
			inputs = new InputListener(world, frame);
		} catch (AWTException e) {
			System.exit(0);
		}
		frame.addKeyListener(inputs);
		frame.addMouseMotionListener(inputs);
		
		BufferStrategy bs = frame.getBufferStrategy();
		long lastUpdate = System.currentTimeMillis();
		renderer = new Renderer(world, width, height);
		for(;;) {
			if (System.currentTimeMillis() - lastUpdate >= 1000 / FPS) {
				renderer.render(bs);
				lastUpdate = System.currentTimeMillis();
				inputs.checkInput();
			}
		}
	}

	public static void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, width, height);
	}
	
}
