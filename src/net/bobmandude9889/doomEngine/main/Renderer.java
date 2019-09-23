package net.bobmandude9889.doomEngine.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import com.aparapi.Kernel;

import net.bobmandude9889.doomEngine.World.ResourceLoader;
import net.bobmandude9889.doomEngine.World.World;
import net.bobmandude9889.doomEngine.World.World.Sector;

public class Renderer {

	World world;
	
	BufferedImage wallImage;
	
	final int width, height;
	
	float mapSize = 2f/8f;
	float mapZoom = 30;
	int pointerLength = 20;
	float hFov;
	float vFov;
	int ceilingHeight = 20;
	int eyeHeight = 5;
	
	boolean lineMode = false;
	boolean slowMode = false;
	
	public Renderer(World world, int width, int height) {
		this.world = world;
		this.width = width;
		this.height = height;
		this.hFov = 0.73f*height;
		this.vFov = 0.1f*height;
		this.wallImage = ResourceLoader.loadImage("wall.png");
	}
	
	public Graphics show(BufferStrategy bs, Graphics g) {
		g.dispose();
		bs.show();
		return bs.getDrawGraphics();
	}
	
	public void renderWall(Sector sector, int i) {
		Vec2f p1TransVec = translatePoint(world.vertices.get(sector.vertices[(i == 0 ? sector.vertices.length : i) - 1]));
		Vec2f p2TransVec = translatePoint(world.vertices.get(sector.vertices[i]));
		final float[] p1Trans = {p1TransVec.x, p1TransVec.y};
		final float[] p2Trans = {p2TransVec.x, p2TransVec.y};
		
		Sector playerSecto = world.sectors.get(world.playerSector);
		Sector portalSector = world.sectors.get(sector.sectors[i]);
		final float playerFloorHeight = playerSecto.floorHeight;
		final float playerCeilHeight = playerSecto.ceilingHeight;
		final float portalFloorHeight = portalSector.floorHeight;
		final float portalCeilHeight = portalSector.ceilingHeight;
		final float floorHeight = sector.floorHeight;
		final float ceilHeight = sector.ceilingHeight;
		//System.out.println(p1Trans + "\n" + p2Trans + "\n\n");
		
		
		//start kernel

		if ((p1Trans[1] > 0 || p2Trans[1] > 0) && sector.sectors[i] != head.viewingSector) {
			Kernel kernel = new Kernel() {
				
				public float[] translatePoint(float[] point) {
					double rAngle = Math.toRadians(world.playerAngle.x);
					return new float[] {(float) (((point[0] - world.playerLocation.x) * Math.cos(rAngle)) - ((point[1] - world.playerLocation.y) * Math.sin(rAngle))), 
							(float) (((point[1] - world.playerLocation.y) * Math.cos(rAngle)) + ((point[0] - world.playerLocation.x) * Math.sin(rAngle)))};
				}
				
				public float[] screenIntersect(float[] p1, float[] p2, float[] p3, float[] p4) {
					float x = fNCross(p1, p2);
					float y = fNCross(p3, p4);
					float det = fNCross(new float[] {p1[0] - p2[0], p1[1] - p2[1]}, new float[] {p3[0] - p4[0], p3[1] - p4[1]});
					x = fNCross(new float[] {x, p1[0] - p2[0]}, new float[] {y, p3[0] - p4[0]}) / det;
					y = fNCross(new float[] {x, p1[1] - p2[1]}, new float[] {y, p3[1] - p4[1]}) / det;
					return new float[] {x,y};
				}
				
				public float fNCross(float[] p1, float[] p2) {
					return p1[0] * p2[1] - p1[1] * p2[0];
				}
				
				public int clamp(int n, int a, int b) {
					int min = Math.min(a, b);
					int max = Math.max(a, b);
					return n < min ? min : n > max ? max : n;
				}
				
				@Override
				public void run() {
					float[] int1 = screenIntersect(p1Trans, p2Trans, new float[] {-0.0001f,0.0001f}, new float[] {-(width/2), 5});
					float[] int2 = screenIntersect(p1Trans, p2Trans, new float[] {0.0001f,0.0001f}, new float[] {(width/2), 5});
					if (p1Trans[0] <= 0) {
						if (int1[0] > 0)
							p1Trans = int1;
						else
							p1Trans = int2;
					}
					if (p2Trans[0] <= 0) {
						if (int1[0] > 0)
							p2Trans = int1;
						else
							p2Trans = int2;
					}
					
					float scale1x = hFov / p1Trans[1];
					float scale1y = vFov / p1Trans[1];
					float scale2x = hFov / p2Trans[1];
					float scale2y = vFov / p2Trans[1];
//						System.out.print(p1Trans + "\n" + p2Trans + "\n");
//						System.out.print(scale1 + "\n" + scale2 + "\n\n");
					
					//System.out.println(p1Trans.y);
					float x1Proj = p1Trans[0] * scale1x;
					float x2Proj = p2Trans[0] * scale2x;
					int heightHalf = height / 2;
					int widthHalf = width / 2;
					x1Proj += widthHalf; 
					x2Proj += widthHalf;
					//float playerY = map.sectors.get(map.playerSector).floorHeight + eyeHeight;
//						Vec2f p1ProjTop = new Vec2f(x1Proj, -(sector.ceilingHeight - playerY) * scale1.y);
//						Vec2f p1ProjBot = new Vec2f(x1Proj, eyeHeight * scale1.y);
//						Vec2f p2ProjTop = new Vec2f(x2Proj, -(sector.ceilingHeight - playerY) * scale2.y);
//						Vec2f p2ProjBot = new Vec2f(x2Proj, eyeHeight * scale2.y);
					
//						int leftX = (int) Math.min(x1Proj, x2Proj) + widthHalf;
//						int rightX = (int) Math.max(x1Proj, x2Proj) + widthHalf;
					int leftX = (int) x1Proj;
					int rightX = (int) x2Proj;
					float leftScalex = scale1x;
					float leftScaley = scale1y;
					float rightScalex = scale2x;
					float rightScaley = scale2y;
					
					if (rightX < leftX) {
						leftX = (int) x2Proj;
						rightX = (int) x1Proj;
						leftScalex = scale2x;
						leftScaley = scale2y;
						rightScalex = scale1x;
						rightScaley = scale1y;
					}
					
					float heightOff = (floorHeight - playerFloorHeight);
					int leftCeilY = (int) (-((ceilingHeight - floorHeight) - eyeHeight + heightOff) * leftScaley) + heightHalf;
					int leftFloorY = (int) ((eyeHeight - heightOff) * leftScaley) + heightHalf;
					int rightCeilY = (int) (-((ceilingHeight - floorHeight) - eyeHeight + heightOff) * rightScaley) + heightHalf;
					int rightFloorY = (int) ((eyeHeight - heightOff) * rightScaley) + heightHalf;
					
//					System.out.println((p1Trans.y / p1Trans.x) * x1Proj);
//					System.out.println((p2Trans.y / p2Trans.x) * x2Proj);
					
					int leftPortCeilY = 0;
					int leftPortFloorY = 0;
					int rightPortCeilY = 0;
					int rightPortFloorY = 0;
					
					if (sector.sectors[i] != -1) {
						heightOff = (portalFloorHeight - playerFloorHeight);
						leftPortCeilY = (int) (-((portalCeilHeight - portalFloorHeight) - eyeHeight + heightOff) * leftScaley) + heightHalf;
						leftPortFloorY = (int) ((eyeHeight - heightOff) * leftScaley) + heightHalf;
						rightPortCeilY = (int) (-((portalCeilHeight - portalFloorHeight) - eyeHeight + heightOff) * rightScaley) + heightHalf;
						rightPortFloorY = (int) ((eyeHeight - heightOff) * rightScaley) + heightHalf;
					}
					
					//System.out.printf("\n%d\n%d\n%d\n%d\n%d\n%d\n---",leftX,rightX,leftCeilY,leftFloorY,rightCeilY,rightFloorY);
					
					if (lineMode) {
//						System.out.println("line");
//						g.setColor(Color.WHITE);
//						g.drawLine(leftX, leftCeilY, rightX, rightCeilY);
//						g.drawLine(rightX, rightCeilY, rightX, rightFloorY);
//						g.drawLine(rightX, rightFloorY, leftX, leftFloorY);
//						g.drawLine(leftX, leftFloorY, leftX, leftCeilY);
//						
//						if (portalSector != null) {
//							if (!rendered[sector.sectors[i]])
//								renderQueue.add(new QueueItem(sector.sectors[i], head.sector, leftX, rightX));
//							g.setColor(Color.RED);
//							g.drawLine(leftX, leftPortCeilY, rightX, rightPortCeilY);
//							g.drawLine(leftX, leftPortFloorY, rightX, rightPortFloorY);
//							g.drawLine(leftX, leftPortFloorY, leftX, leftPortCeilY);
//							g.drawLine(rightX, rightPortFloorY, rightX, rightPortCeilY);
//						}
					} else {
						
						//System.out.println("right: " + rightX + "left: " + leftX);
						if (rightX > head.leftX && leftX < head.rightX) {
//								System.out.println("right: " + rightX);
//								System.out.println("left: " + leftX + "\n");
//								System.out.println("right: " + rightX);
//								System.out.println("left: " + leftX + "\n----");
							//System.out.println("clamped  right: " + rightX + "left: " + leftX);
							double ceilM = (double) (rightCeilY - leftCeilY) / (double) (rightX - leftX);
							double floorM = (double) (rightFloorY - leftFloorY) / (double) (rightX - leftX);
							double ceilB = rightCeilY - (ceilM * (double) rightX);
							double floorB = rightFloorY - (floorM * (double) rightX);
							
							double portalCeilM = 0;
							double portalFloorM = 0;
							double portalCeilB = 0;
							double portalFloorB = 0;
							
							if (portalSector != null) {
								portalCeilM = (double) (rightPortCeilY - leftPortCeilY) / (double) (rightX - leftX);
								portalFloorM = (double) (rightPortFloorY - leftPortFloorY) / (double) (rightX - leftX);
								portalCeilB = rightPortCeilY - (portalCeilM * (double) rightX);
								portalFloorB = rightPortFloorY - (portalFloorM * (double) rightX);
							}
							
							int leftUnclampX = leftX;
							int rightUnclampX = rightX;
							
							rightX = clamp(rightX, head.leftX, head.rightX);
							leftX = clamp(leftX, head.leftX, head.rightX);
							
							double ceilY = ceilM * (double) leftX + ceilB;
							double floorY = floorM * (double) leftX + floorB;
							double portalCeilY = 0;
							double portalFloorY = 0;
							if (portalSector != null) {
								renderQueue.add(new QueueItem(sector.sectors[i], head.sector, leftX, rightX));
								portalCeilY = portalCeilM * (double) leftX + portalCeilB;
								portalFloorY = portalFloorM * (double) leftX + portalFloorB;
							}
							
							for (int x = leftX; x <= rightX; x++) {
								ceilY += ceilM;
								floorY += floorM;
								
//									if (x == 0) {
//										System.out.println("ceil: " + ceilY);
//										System.out.println("floor: " + floorY + "\n");
//									}
								
								int ceilYClamp = clamp((int) ceilY, yTop[x], yBottom[x]);
								int floorYClamp = clamp((int) floorY, yTop[x], yBottom[x]);
								
								vLine(g, x, yTop[x], (int) ceilYClamp, Color.DARK_GRAY);
								if (portalSector == null) {
									//vLine(g, x, ceilYClamp, floorYClamp, Color.WHITE);
									
									int imgX = (int) (((float) (x - leftUnclampX) / (float) (rightUnclampX - leftUnclampX)) * (float) wallImage.getWidth());
									
//									float m = (p1Trans.y - p2Trans.y) / (p1Trans.x - p2Trans.x);
//									float imgX = (x * (p1Trans.y - p1Trans.x * m)) / (hFov - m * x);
//									float minX = Math.min(p1Trans.x, p2Trans.x);
//									float maxX = Math.max(p1Trans.x, p2Trans.x);
//									imgX = (imgX - minX) / (maxX - minX);
//									imgX = imgX * wallImage.getWidth();
									imgLine(g, x, ceilYClamp, floorYClamp, wallImage, imgX);
								}
								vLine(g, x, floorYClamp, yBottom[x], Color.BLUE);
								
								yTop[x] = 0;
								yBottom[x] = 0;
								
								if (portalSector != null) {
									portalCeilY += portalCeilM;
									portalFloorY += portalFloorM;
									int portalCeilYClamp = clamp((int) portalCeilY, (int) ceilYClamp, (int) floorYClamp);
									int portalFloorYClamp = clamp((int) portalFloorY, (int) ceilYClamp, (int) floorYClamp);

									vLine(g, x, ceilYClamp, portalCeilYClamp, Color.WHITE);
									
									if (slowMode) {
										vLine(g, x, portalCeilYClamp, portalFloorYClamp, Color.RED);
									}
									
									vLine(g, x, portalFloorYClamp, floorYClamp, Color.WHITE);
									
									yTop[x] = portalCeilYClamp;
									yBottom[x] = portalFloorYClamp;
								}
								
								if (x == leftX || x == rightX)
									vLine(g, x, ceilYClamp, floorYClamp, Color.BLACK);
								
								if (slowMode) {
									g = show(bs, g);
									try {
										Thread.sleep(5);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			};
		}
	}
	
	public void render(BufferStrategy bs) {
		Graphics g = bs.getDrawGraphics();
		
		if (slowMode || lineMode) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
		}
		
		int[] yTop = new int[this.width + 1];
		int[] yBottom = new int[this.width + 1];
		for (int i = 0; i < this.width; i++) yBottom[i] = this.height - 1;
		
		Queue<QueueItem> renderQueue = new LinkedList<>();
		renderQueue.add(new QueueItem(world.playerSector, -2, 0, width-1));
		
		boolean[] rendered = new boolean[world.sectors.size()];
		
		QueueItem head = null;
		while ((head = renderQueue.poll()) != null) {
			Sector sector = world.sectors.get(head.sector);
			rendered[head.sector] = true;
			
			//System.out.println("Rendering: " + head);
			
			for (int i = 0; i < sector.vertices.length; i++) {
				
				renderWall(sector, i);
				
			}
			
			boolean complete = true;
			for (int i = 0; i < width; i++) {
				if (yTop[i] != yBottom[i]) {
					complete = false;
					break;
				}
			}
			if (complete)
				break;
		}
		
		BufferedImage map = renderMap();
		g.drawImage(map, 0, 0, null);
		
		g.dispose();
		bs.show();
	}
	
	public BufferedImage renderMap() {
		BufferedImage map = new BufferedImage((int) (width * mapSize),(int) (height * mapSize), BufferedImage.TYPE_INT_ARGB);
		Graphics mapG = map.getGraphics();
		Point midP = new Point(map.getWidth() / 2, map.getHeight() / 2);
		
		mapG.setColor(Color.RED);
		Sector playerSector = world.sectors.get(world.playerSector);
		int[] xs = new int[playerSector.vertices.length];
		int[] ys = new int[playerSector.vertices.length];
		for (int i = 0; i < playerSector.vertices.length; i++) {
			Vec2f vert = world.vertices.get(playerSector.vertices[i]);
			vert = translatePoint(vert);
			xs[i] = (int) (vert.x * mapSize * mapZoom) + midP.x;
			ys[i] = (int) (-vert.y * mapSize * mapZoom) + midP.y;
		}
		mapG.fillPolygon(xs, ys, playerSector.vertices.length);
		
		mapG.setColor(new Color(52, 52, 52, 100));
		mapG.fillRect(0, 0, map.getWidth(), map.getHeight());
		mapG.setColor(Color.GRAY);
		mapG.fillOval(midP.x - 2, midP.y - 2, 4, 4);
		mapG.drawLine(midP.x, midP.y, midP.x, midP.y - pointerLength);
		mapG.setColor(Color.YELLOW);
		
		for (Sector sector : world.sectors) {
			for (int i = 0; i < sector.vertices.length; i++) {
				Vec2f p1Trans = translatePoint(world.vertices.get(sector.vertices[(i == 0 ? sector.vertices.length : i) - 1]));
				Vec2f p2Trans = translatePoint(world.vertices.get(sector.vertices[i]));
				mapG.drawLine((int) (p1Trans.x * mapSize * mapZoom) + midP.x, (int) (-p1Trans.y * mapSize * mapZoom) + midP.y, (int) (p2Trans.x * mapSize * mapZoom) + midP.x, (int) (-p2Trans.y * mapSize * mapZoom) + midP.y);
			}
		}
		
		return map;
	}
	
	public Vec2f translatePoint(Vec2f point) {
		double rAngle = Math.toRadians(world.playerAngle.x);
		return new Vec2f((float) (((point.x - world.playerLocation.x) * Math.cos(rAngle)) - ((point.y - world.playerLocation.y) * Math.sin(rAngle))), 
				(float) (((point.y - world.playerLocation.y) * Math.cos(rAngle)) + ((point.x - world.playerLocation.x) * Math.sin(rAngle))));
	}
	
	public Vec2f screenIntersect(Vec2f p1, Vec2f p2, Vec2f p3, Vec2f p4) {
		float x = fNCross(p1, p2);
		float y = fNCross(p3, p4);
		float det = fNCross(new Vec2f(p1.x - p2.x, p1.y - p2.y), new Vec2f(p3.x - p4.x, p3.y - p4.y));
		x = fNCross(new Vec2f(x, p1.x - p2.x), new Vec2f(y, p3.x - p4.x)) / det;
		y = fNCross(new Vec2f(x, p1.y - p2.y), new Vec2f(y, p3.y - p4.y)) / det;
		return new Vec2f(x,y);
	}
	
	public float fNCross(Vec2f p1, Vec2f p2) {
		return p1.x * p2.y - p1.y * p2.x;
	}
	
	public int clamp(int n, int a, int b) {
		int min = Math.min(a, b);
		int max = Math.max(a, b);
		return n < min ? min : n > max ? max : n;
	}
	
	public void vLine(Graphics g, int x, int y1, int y2, Color color) {
		g.setColor(color);
		g.fillRect(x, y1, 1, y2 - y1);
		g.setColor(Color.BLACK);
		g.fillRect(x,y1,1,1);
		g.fillRect(x,y2,1,1);
	}
	
	public void imgLine(Graphics g, int x, int y1, int y2, BufferedImage image, int imgX) {
		imgX = clamp(imgX, 0, image.getWidth() - 1);
		g.drawImage(image.getSubimage(imgX, 0, 1, image.getHeight() - 1), x, y1, 1, y2 - y1, null);
	}
	
}
