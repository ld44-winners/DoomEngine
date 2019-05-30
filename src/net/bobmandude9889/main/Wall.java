package net.bobmandude9889.main;

import com.sun.javafx.geom.Vec2f;

public class Wall {

	Vec2f p1;
	Vec2f p2;
	
	public Wall(float x1, float y1, float x2, float y2) {
		super();
		this.p1 = new Vec2f(x1, y1);
		this.p2 = new Vec2f(x2, y2);
	}
	
}
