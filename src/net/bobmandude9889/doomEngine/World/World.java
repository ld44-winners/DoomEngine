package net.bobmandude9889.doomEngine.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.bobmandude9889.doomEngine.World.World.Sector;
import net.bobmandude9889.main.Vec2f;

public class World {

	public static class Sector {

		public float floorHeight;
		public float ceilingHeight;
		public int[] vertices;
		public int[] sectors;

		public Sector(float floorHeight, float ceilingHeight, int[] vertices, int[] sectors) {
			this.floorHeight = floorHeight;
			this.ceilingHeight = ceilingHeight;
			this.vertices = vertices;
			this.sectors = sectors;
		}

		@Override
		public String toString() {
			return this.floorHeight + " " + this.ceilingHeight + " ; " + Arrays.toString(vertices) + " ; "
					+ Arrays.toString(sectors);
		}

	}

	public List<Vec2f> vertices;
	public List<Sector> sectors;
	public Vec2f playerLocation;
	public Vec2f playerAngle;
	public int playerSector;

	public World(List<Vec2f> vertices, List<Sector> sectors, Vec2f playerLocation, Vec2f playerAngle,
			int playerSector) {
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

	private boolean sectorContainsPoint(Sector sector, Vec2f point) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = sector.vertices.length - 1; i < sector.vertices.length; j = i++) {
			if ((vertices.get(sector.vertices[i]).y > point.y) != (vertices.get(sector.vertices[j]).y > point.y)
					&& (point.x < (vertices.get(sector.vertices[j]).x - vertices.get(sector.vertices[i]).x) * (point.y - vertices.get(sector.vertices[i]).y) / (vertices.get(sector.vertices[j]).y - vertices.get(sector.vertices[i]).y)
							+ vertices.get(sector.vertices[i]).x)) {
				result = !result;
			}
		}
		return result;
	}

	public List<Integer> getPossibleSectors(Vec2f point) {
		List<Integer> possible = new ArrayList<>();
		for (Sector sector : sectors) {
			if (sectorContainsPoint(sector, point)) {
				possible.add(sectors.indexOf(sector));
			}
		}
		return possible;
	}

	public boolean onSegment(Vec2f p, Vec2f q, Vec2f r) {
		if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y)
				&& q.y >= Math.min(p.y, r.y))
			return true;

		return false;
	}

	public int orientation(Vec2f p, Vec2f q, Vec2f r) {
		float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

		if (val == 0)
			return 0;

		return (val > 0) ? 1 : 2;
	}

	public boolean intersect(Vec2f p1, Vec2f q1, Vec2f p2, Vec2f q2) {
		int o1 = orientation(p1, q1, p2);
		int o2 = orientation(p1, q1, q2);
		int o3 = orientation(p2, q2, p1);
		int o4 = orientation(p2, q2, q1);

		if (o1 != o2 && o3 != o4)
			return true;

		if (o1 == 0 && onSegment(p1, p2, q1))
			return true;
		if (o2 == 0 && onSegment(p1, q2, q1))
			return true;
		if (o3 == 0 && onSegment(p2, p1, q2))
			return true;
		if (o4 == 0 && onSegment(p2, q1, q2))
			return true;

		return false;
	}

	public void movePlayer(Vec2f moveTo) {
		Sector sector = sectors.get(playerSector);
		int[] sectVerts = sector.vertices;
		for (int i = 0; i < sectVerts.length; i++) {
			int j = i == 0 ? sectVerts.length - 1 : i - 1;
			if (intersect(vertices.get(sectVerts[j]), vertices.get(sectVerts[i]), playerLocation, moveTo)) {
				if (sector.sectors[i] != -1) {
					System.out.println("move to sector: " + sector.sectors[i]);
					playerSector = sector.sectors[i];
				} else {
					moveTo = playerLocation;
				}
			}
		}
		
		if (!getPossibleSectors(moveTo).contains(playerSector)) {
			moveTo = playerLocation;
		}
		
		this.playerLocation = moveTo;
	}

}
