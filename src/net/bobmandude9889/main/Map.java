package net.bobmandude9889.main;

import java.util.Arrays;
import java.util.List;

import com.sun.javafx.geom.Vec2f;

public class Map {

	public static class Sector {
		
		float floorHeight;
		float ceilingHeight;
		int[] vertices;
		int[] sectors;
		
		public Sector(float floorHeight, float ceilingHeight, int[] vertices, int[] sectors) {
			this.floorHeight = floorHeight;
			this.ceilingHeight = ceilingHeight;
			this.vertices = vertices;
			this.sectors = sectors;
		}
		
		@Override
		public String toString() {
			return this.floorHeight + " " + this.ceilingHeight + " ; " + Arrays.toString(vertices) + " ; " + Arrays.toString(sectors);
		}
		
	}
	
	List<Vec2f> vertices;
	List<Sector> sectors;
	Vec2f playerLocation;
	float playerAngle;
	int playerSector;
	
	public Map(List<Vec2f> vertices, List<Sector> sectors, Vec2f playerLocation, float playerAngle, int playerSector) {
		this.vertices = vertices;
		this.sectors = sectors;
		this.playerLocation = playerLocation;
		this.playerAngle = playerAngle;
		this.playerSector = playerSector;
		
		System.out.println("Vertices: \n" + vertices);
		System.out.println("Sectors: \n" + sectors);
		System.out.println("PlayerLocation: " + playerLocation);
		System.out.println("playerAngle: " + playerAngle);
		System.out.println("playerSector: " + playerSector);
	}
	
}
