package net.bobmandude9889.doomEngine.main;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.bobmandude9889.doomEngine.World.World;

public class InputListener implements KeyListener, MouseMotionListener {
	
	List<Integer> keys;

	Point mouse;
	boolean ignoreNext;
	
	World world;
	JFrame frame;
	
	Robot robot;
	
	static float rotationSpeed = 0.25f;
	static float movementSpeed = 0.3f;
	
	public InputListener(World world, JFrame frame) throws AWTException {
		this.keys = new ArrayList<>();
		this.world = world;
		this.frame = frame;
		robot = new Robot();
	}

	public boolean isPressed(char key) {
		return keys.contains(KeyEvent.getExtendedKeyCodeForChar(key));
	}
	
	public Point getMouseLocation() {
		return mouse;	
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
//		if (mouseOnScreen.x < monitorCenter.x) {
//			world.playerAngle.x -= rotationSpeed;
//		} else if (mouseOnScreen.x > monitorCenter.x) {
//			world.playerAngle.x += rotationSpeed;
//		}
		
		if (mouse == null || ignoreNext) {
			mouse = e.getPoint();
			ignoreNext = false;
			return;
		}
			
		if (e.getX() < mouse.getX()) {
			world.playerAngle.x -= (mouse.getX() - e.getX()) * rotationSpeed;
		} else if(e.getX() > mouse.getX()) {
			world.playerAngle.x += (e.getX() - mouse.getX()) * rotationSpeed;
		}
		
		if (e.getX() < 100) {
			Point moveTo = new Point(frame.getLocationOnScreen().x + frame.getWidth() - 200, frame.getLocationOnScreen().y + e.getY());
			robot.mouseMove(moveTo.x, moveTo.y);
			mouse = moveTo;
			ignoreNext = true;
		} else if (e.getX() > frame.getWidth() - 100) {
			Point moveTo = new Point(frame.getLocationOnScreen().x + 200, frame.getLocationOnScreen().y + e.getY());
			robot.mouseMove(moveTo.x, moveTo.y);
			mouse = moveTo;
			ignoreNext = true;
		} else if (e.getY() < 100) {
			Point moveTo = new Point(frame.getLocationOnScreen().x + e.getX(), frame.getLocationOnScreen().y + frame.getHeight() - 200);
			robot.mouseMove(moveTo.x, moveTo.y);
			mouse = moveTo;
			ignoreNext = true;
		} else if (e.getY() > frame.getHeight() - 100) {
			Point moveTo = new Point(frame.getLocationOnScreen().x + e.getX(), frame.getLocationOnScreen().y + 200);
			robot.mouseMove(moveTo.x, moveTo.y);
			mouse = moveTo;
			ignoreNext = true;
		} else {
			mouse = e.getPoint();	
		}
	}

}
