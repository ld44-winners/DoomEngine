package net.bobmandude9889.main;

public class QueueItem {
	
	public int sector;
	public int viewingSector;
	public int leftX;
	public int rightX;
	
	public QueueItem(int sector, int viewingSector, int leftX, int rightX) {
		super();
		this.sector = sector;
		this.viewingSector = viewingSector;
		this.leftX = leftX;
		this.rightX = rightX;
	}
	
	@Override
	public String toString() {
		return String.format("QueueItem{sector=%d, viewingSector=%d, leftX=%d, rightX=%d}", this.sector, this.viewingSector, this.leftX, this.rightX);
	}
	
}
