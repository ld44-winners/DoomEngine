package net.bobmandude9889.doomEngine.main;

import com.aparapi.Kernel;

import net.bobmandude9889.doomEngine.World.World;

public class GPURenderer extends Kernel {

	// World
	public float[] vertices;
	public float[] playerLocation;
	public float[] playerAngle;
	public int playerSector;
	
	// World Sectors
	public int[] sectorVerticeCounts;
	public float[] sectorHeights;
	public int[][] sectorVertices;
	public int[][] sectorPortals;
	
	// Constants
	float mapSize = 2f/8f;
	float mapZoom = 30;
	int pointerLength = 20;
	int ceilingHeight = 20;
	int eyeHeight = 5;
	int width, height;
	float hFov;
	float vFov;
	
	// Drawing instructions (top, ceil, floor, bottom)
	int[][] lines;
	
	public GPURenderer(World world, int width, int height) {
		this.width = width;
		this.height = height;
		this.hFov = 0.73f*height;
		this.vFov = 0.1f*height;
		lines = new int[][];
	}
	
	@Override
	public void run() {
		
	}

}
