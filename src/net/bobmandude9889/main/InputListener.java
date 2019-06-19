package net.bobmandude9889.main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import net.bobmandude9889.doomEngine.World.World;

public class InputListener implements KeyListener, MouseMotionListener {
	
	List<Integer> keys;

	Point mouse;
	Point mouseOnScreen;
	Point windowCenter;
	Point monitorCenter;
	
	World world;
	
	Robot robot;
	
	static float rotationSpeed = 1f;
	static float movementSpeed = 0.3f;
	
	public InputListener(World world) throws AWTException {
		this.keys = new ArrayList<>();
		this.world = world;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		monitorCenter = new Point(screenSize.width / 2, screenSize.height /2);
		windowCenter = new Point(Main.width/2, Main.height/2);
		robot = new Robot();
		centerMouse();
		mouseOnScreen = monitorCenter;
	}

	public boolean isPressed(char key) {
		return keys.contains(KeyEvent.getExtendedKeyCodeForChar(key));
	}
	
	public Point getMouseLocation() {
		return mouse;	
	}
	
	public Point getMouseLocationOnScreen() {
		return mouseOnScreen;
	}

	public void centerMouse() {
		robot.mouseMove(monitorCenter.x, monitorCenter.y);
	}
	
	public void checkInput() {
		double rAngle = Math.toRadians(world.playerAngle.x);
		Vec2f moveTo = new Vec2f(world.playerLocation.x,world.playerLocation.y);
		if (keys.contains(KeyEvent.VK_A)) {
			moveTo.x -= Math.sin(rAngle + Math.toRadians(90)) * movementSpeed;
			moveTo.y -= Math.cos(rAngle + Math.toRadians(90)) * movementSpeed;
		}
		if (keys.contains(KeyEvent.VK_D)) {
			moveTo.x += Math.sin(rAngle + Math.toRadians(90)) * movementSpeed;
			moveTo.y += Math.cos(rAngle + Math.toRadians(90)) * movementSpeed;
		}
		if (keys.contains(KeyEvent.VK_W)) {
//			System.out.println("X: " + Math.sin(rAngle) * movementSpeed);
//			System.out.println("Y: " + Math.cos(rAngle) * movementSpeed);
			moveTo.x += Math.sin(rAngle) * movementSpeed;
			moveTo.y += Math.cos(rAngle) * movementSpeed;
		}
		if (keys.contains(KeyEvent.VK_S)) {
			moveTo.x -= Math.sin(rAngle) * movementSpeed;
			moveTo.y -= Math.cos(rAngle) * movementSpeed;
		}
		
		if (keys.contains(KeyEvent.VK_LEFT)) {
			world.playerAngle = world.playerAngle.subtract(rotationSpeed, 0);
		}
		if (keys.contains(KeyEvent.VK_RIGHT)) {
			world.playerAngle = world.playerAngle.add(rotationSpeed, 0);
		}
		
		world.movePlayer(moveTo);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'l') {
			Main.renderer.lineMode = !Main.renderer.lineMode;
		}
		if (e.getKeyChar() == ';') {
			Main.renderer.slowMode = !Main.renderer.slowMode;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.contains(e.getKeyCode()))
			keys.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove((Integer) e.getKeyCode());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
		mouseOnScreen = e.getLocationOnScreen();
		if (mouseOnScreen.x < monitorCenter.x) {
			world.playerAngle.x -= rotationSpeed;
		} else if (mouseOnScreen.x > monitorCenter.x) {
			world.playerAngle.x += rotationSpeed;
		}
		centerMouse();
	}

}
