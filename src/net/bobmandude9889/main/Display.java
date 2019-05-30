package net.bobmandude9889.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.sun.javafx.geom.Vec2f;

import net.bobmandude9889.main.Map.Sector;

public class Display extends JPanel implements KeyListener, MouseMotionListener {
	private static final long serialVersionUID = 7879792515264156929L;
	
	List<Integer> keys;

	Robot mouse;
	Point screenCenter;
	
	@Override
	public void keyTyped(KeyEvent e) {
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
		if (e.getX() < screenMidP.x) {
			xAngle -= rotationSpeed;
		} else if (e.getX() > screenMidP.x) {
			xAngle += rotationSpeed;
		}
//		System.out.println(e.getY() + "," + screenMidP.y);
//		if (e.getY() < screenMidP.y + 15) {
//			yAngle -= rotationSpeed;
//		} else if (e.getY() > screenMidP.y + 15) {
//			yAngle += rotationSpeed;
//		}
//		System.out.println(yAngle);
		centerMouse();
	}
	
	public void centerMouse() {
		mouse.mouseMove(screenCenter.x, screenCenter.y);
	}

	int width;
	int height;
	
	List<Wall> walls;
	
	Vec2f player;
	float xAngle;
	//float yAngle;
	
	int pointerLength = 20;
	float rotationSpeed = 1f;
	float movementSpeed = 0.3f;
	int frame = 0;
	float mapSize = 2f/8f;
	float mapZoom = 30;
	int renderDistance = 800;
	int wallHeight = 2;
	int eyeHeight = 5;
	float hFov;
	float vFov;
	Point screenMidP;
	Color wallColor;
	Color floorColor;
	BufferedImage background;
	Map map;
	
	public Display(int width, int height, Robot mouse) {
		super(true);
		this.mouse = mouse;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenCenter = new Point(screenSize.width / 2, screenSize.height /2);
		centerMouse();
		this.width = width;
		this.height = height;
		this.hFov = 0.73f*height;
		this.vFov = 0.2f*height;
		try {
			this.map = MapLoader.load("map.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		walls = new ArrayList<>();
		walls.add(new Wall(-10, 10, 10, 10));
		walls.add(new Wall(10, 10, 10, -10));
//		walls.add(new Wall(-300, -100, 300, -100));
//		walls.add(new Wall(500, 100, 500, -500));
//		walls.add(new Wall(300, -100, 300, -500));
//		walls.add(new Wall(600,600,300,200));
		player = map.playerLocation;
		xAngle = 0;
		//yAngle = 0;
		keys = new ArrayList<>();
		screenMidP = new Point(width/2, height/2);
		wallColor = Color.CYAN;
		floorColor = Color.RED;
		background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = background.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		int loopMax = height / 2 - 50;
		for (int i = 0; i < loopMax; i++) {
			//System.out.printf("%d,%d,%d,%f\n\n\n",floorColor.getRed(), floorColor.getGreen(), floorColor.getBlue(), 255 - map(i, 0, loopMax, 0, 255));
			Color c = new Color(floorColor.getRed(), floorColor.getGreen(), floorColor.getBlue(), (int) (255 - map(i, 0, loopMax, 0, 255)));
			g.setColor(c);
			g.fillRect(0, i, width, 1);
			g.fillRect(0, height - i, width, 1);
		}
	}
	
	boolean isBetween(float n, float a, float b) {
		float min = a;
		float max = b;
		if (min > max) {
			min = b;
			max = a;
		}
		boolean result = (min <= n && n <= max);
//		System.out.println("min: " + min);
//		System.out.println("max: " + max);
//		System.out.println("n: " + n);
//		System.out.println(result);
		return result;
	}
	
	public float map(float x, float min1, float max1, float min2, float max2) {
		return (((x - min1) / max1) * max2) + min2;
	}
	
	public float wAvg(float p, float a, float b) {
		//System.out.println("a: " + a);
		return (p * a) + ((1 - p) * b);
	}
	
	public Color wAvg(float p, Color a, Color b) {
//		System.out.println("p: " + p);
//		System.out.printf("%d,%d,%d\n",(int) wAvg(p, a.getRed(), b.getRed()),(int) wAvg(p, a.getGreen(), b.getGreen()),(int) wAvg(p, a.getBlue(), b.getBlue()));
		return new Color((int) wAvg(p, a.getRed(), b.getRed()),(int) wAvg(p, a.getGreen(), b.getGreen()),(int) wAvg(p, a.getBlue(), b.getBlue()));
	}
	
	public Vec2f translatePoint(Vec2f point) {
		double rAngle = Math.toRadians(xAngle);
		return new Vec2f((float) (((point.x - player.x) * Math.cos(rAngle)) - ((point.y - player.y) * Math.sin(rAngle))), 
				(float) (((point.y - player.y) * Math.cos(rAngle)) + ((point.x - player.x) * Math.sin(rAngle))));
	}
	
	public float fNCross(Vec2f p1, Vec2f p2) {
		return p1.x * p2.y - p1.y * p2.x;
	}
	
	public Vec2f screenIntersect(Vec2f p1, Vec2f p2, Vec2f p3, Vec2f p4) {
		float x = fNCross(p1, p2);
		float y = fNCross(p3, p4);
		float det = fNCross(new Vec2f(p1.x - p2.x, p1.y - p2.y), new Vec2f(p3.x - p4.x, p3.y - p4.y));
		x = fNCross(new Vec2f(x, p1.x - p2.x), new Vec2f(y, p3.x - p4.x)) / det;
		y = fNCross(new Vec2f(x, p1.y - p2.y), new Vec2f(y, p3.y - p4.y)) / det;
		return new Vec2f(x,y);
	}
	
	public boolean onSegment(Vec2f p, Vec2f q, Vec2f r) { 
	    if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && 
	        q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y)) 
	       return true; 
	  
	    return false; 
	} 
	
	public int orientation(Vec2f p, Vec2f q, Vec2f r) {
	    float val = (q.y - p.y) * (r.x - q.x) -
	              (q.x - p.x) * (r.y - q.y);
	  
	    if (val == 0) return 0;
	  
	    return (val > 0)? 1: 2;
	} 
	
	public boolean intersect(Vec2f p1, Vec2f q1, Vec2f p2, Vec2f q2) {
	    int o1 = orientation(p1, q1, p2); 
	    int o2 = orientation(p1, q1, q2); 
	    int o3 = orientation(p2, q2, p1); 
	    int o4 = orientation(p2, q2, q1);
	    
	    if (o1 != o2 && o3 != o4) 
	        return true; 
	   
	    if (o1 == 0 && onSegment(p1, p2, q1)) return true; 
	    if (o2 == 0 && onSegment(p1, q2, q1)) return true; 
	    if (o3 == 0 && onSegment(p2, p1, q2)) return true; 
	    if (o4 == 0 && onSegment(p2, q1, q2)) return true; 
	  
	    return false; 
	}
	
	public void drawSector(Graphics g, int sectorId, Vec2f p1, Vec2f p2, Vec2f p3, Vec2f p4, List<Integer> drawn) {
		Sector sector = map.sectors.get(sectorId);
		g.setClip(new Polygon(new int[] {(int) p1.x, (int) p2.x, (int) p3.x, (int) p4.x}, new int[] {(int) p1.y, (int) p2.y, (int) p3.y, (int) p4.y}, 4));
		int[] sectVerts = sector.vertices;
		for (int i = 0; i < sectVerts.length; i++) {
			int j = i==0 ? sectVerts.length-1 : i-1;
			Vec2f p1Trans = translatePoint(map.vertices.get(sectVerts[j]));
			Vec2f p2Trans = translatePoint(map.vertices.get(sectVerts[i]));
			
			if (p1Trans.y > 0 || p2Trans.y > 0) {
				Vec2f int1 = screenIntersect(p1Trans, p2Trans, new Vec2f(-0.0001f,0.0001f), new Vec2f(-(width/2), 5));
				Vec2f int2 = screenIntersect(p1Trans, p2Trans, new Vec2f(0.0001f,0.0001f), new Vec2f((width/2), 5));
				if (p1Trans.y <= 0) {
					if (int1.y > 0)
						p1Trans = int1;
					else
						p1Trans = int2;
				}
				if (p2Trans.y <= 0) {
					if (int1.y > 0)
						p2Trans = int1;
					else
						p2Trans = int2;
				}
				
				Vec2f scale1 = new Vec2f(hFov / p1Trans.y, vFov / p1Trans.y);
				Vec2f scale2 = new Vec2f(hFov / p2Trans.y, vFov / p2Trans.y);
				
				//System.out.println(p1Trans.y);
				float x1Proj = p1Trans.x * scale1.x;
				float x2Proj = p2Trans.x * scale2.x;
				int heightHalf = this.height / 2;
				int widthHalf = this.width / 2;
				float playerY = sector.floorHeight + eyeHeight;
				Vec2f p1ProjTop = new Vec2f(x1Proj, -(sector.ceilingHeight - playerY) * scale1.y);
				Vec2f p1ProjBot = new Vec2f(x1Proj, eyeHeight * scale1.y);
				Vec2f p2ProjTop = new Vec2f(x2Proj, -(sector.ceilingHeight - playerY) * scale2.y);
				Vec2f p2ProjBot = new Vec2f(x2Proj, eyeHeight * scale2.y);
				
				g.setColor(Color.WHITE);
				Polygon pWall = new Polygon(
						new int[] {(int) p1ProjTop.x + widthHalf, (int) p2ProjTop.x + widthHalf, (int) p2ProjBot.x + widthHalf, (int) p1ProjBot.x + widthHalf},
						new int[] {(int) p1ProjTop.y + heightHalf, (int) p2ProjTop.y + heightHalf, (int) p2ProjBot.y + heightHalf, (int) p1ProjBot.y + heightHalf},
						4);
				g.fillPolygon(pWall);
				g.setColor(Color.BLACK);
				g.drawPolygon(pWall);
				
				if (sector.sectors[i] != -1 && !drawn.contains(i)) {
					Sector pSector = map.sectors.get(sector.sectors[i]);
					Vec2f p3ProjTop = new Vec2f(x1Proj, -(pSector.ceilingHeight - playerY) * scale1.y);
					Vec2f p3ProjBot = new Vec2f(x1Proj, (playerY - pSector.floorHeight) * scale1.y);
					Vec2f p4ProjTop = new Vec2f(x2Proj, -(pSector.ceilingHeight - playerY) * scale2.y);
					Vec2f p4ProjBot = new Vec2f(x2Proj, (playerY - pSector.floorHeight) * scale2.y);
					Polygon pPortal = new Polygon(
							new int[] {(int) p3ProjTop.x + widthHalf, (int) p4ProjTop.x + widthHalf, (int) p4ProjBot.x + widthHalf, (int) p3ProjBot.x + widthHalf},
							new int[] {(int) p3ProjTop.y + heightHalf, (int) p4ProjTop.y + heightHalf, (int) p4ProjBot.y + heightHalf, (int) p3ProjBot.y + heightHalf},
							4);
					drawn.add(sectorId);
					g.setColor(Color.RED);
					g.fillPolygon(pPortal);
					g.setColor(Color.BLACK);
					g.drawPolygon(pPortal);
					//drawSector(g, i, p3ProjTop, p4ProjTop, p4ProjBot, p3ProjBot, drawn);
				}
//				mainG.drawLine((int) p1ProjTop.x + widthHalf, (int) p1ProjTop.y + heightHalf, (int) p2ProjTop.x + widthHalf, (int) p2ProjTop.y + heightHalf);
//				mainG.drawLine((int) p1ProjBot.x + widthHalf, (int) p1ProjBot.y + heightHalf, (int) p2ProjBot.x + widthHalf, (int) p2ProjBot.y + heightHalf);
//				mainG.drawLine((int) p1ProjTop.x + widthHalf, (int) p1ProjTop.y + heightHalf, (int) p1ProjBot.x + widthHalf, (int) p1ProjBot.y + heightHalf);
//				mainG.drawLine((int) p2ProjTop.x + widthHalf, (int) p2ProjTop.y + heightHalf, (int) p2ProjBot.x + widthHalf, (int) p2ProjBot.y + heightHalf);
			}
		}
	}
	
	public void drawWalls(Graphics mainG, Graphics mapG, Point mapMidP) {
		mapG.setColor(Color.YELLOW);
		for (Sector sector : map.sectors) {
			int[] sectVerts = sector.vertices;
			for (int i = 0; i < sectVerts.length; i++) {
				int j = i==0 ? sectVerts.length-1 : i-1;
				Vec2f p1Trans = translatePoint(map.vertices.get(sectVerts[j]));
				Vec2f p2Trans = translatePoint(map.vertices.get(sectVerts[i]));
				mapG.drawLine((int) (p1Trans.x * mapSize * mapZoom) + mapMidP.x, (int) (-p1Trans.y * mapSize * mapZoom) + mapMidP.y, (int) (p2Trans.x * mapSize * mapZoom) + mapMidP.x, (int) (-p2Trans.y * mapSize * mapZoom) + mapMidP.y);
			}
		}
		
		drawSector(mainG, map.playerSector, new Vec2f(0,0), new Vec2f(width, 0), new Vec2f(width, height), new Vec2f(0, height), new ArrayList<>());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		double rAngle = Math.toRadians(xAngle);
		Vec2f moveTo = new Vec2f(player.x,player.y);
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
		
		Sector sector = map.sectors.get(map.playerSector);
		int[] sectVerts = sector.vertices;
		for (int i = 0; i < sectVerts.length; i++) {
			int j = i==0 ? sectVerts.length-1 : i-1;
			if (intersect(map.vertices.get(sectVerts[j]), map.vertices.get(sectVerts[i]), player, moveTo)) {
				if (sector.sectors[i] != -1)
					map.playerSector = sector.sectors[i];
				else {
					moveTo = player;
				}
			}
		}
		
		player = moveTo;
		
		BufferedImage map = new BufferedImage((int) (width * mapSize),(int) (height * mapSize), BufferedImage.TYPE_INT_ARGB);
		Graphics mapG = map.getGraphics();
		Point midP = new Point(map.getWidth() / 2, map.getHeight() / 2);
		
		mapG.setColor(new Color(52, 52, 52, 100));
		mapG.fillRect(0, 0, map.getWidth(), map.getHeight());
		mapG.setColor(Color.GRAY);
		mapG.fillOval(midP.x - 2, midP.y - 2, 4, 4);
		mapG.drawLine(midP.x, midP.y, midP.x, midP.y - pointerLength);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		//g.drawImage(background, 0, /*(int) -map(yAngle, 0, 90, 0, this.height/2)*/ 0, null);
		
		drawWalls(g, mapG, midP);
		
		g.drawImage(map, 0, 0, null);
		
		frame++;
	}
	
}
