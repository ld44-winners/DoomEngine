package net.bobmandude9889.doomEngine.main;

public class Vec2f {

	public float x;
	public float y;
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2f divide(float x, float y) {
		return new Vec2f(this.x / x, this.y / y);
	}
	
	public Vec2f multiply(float x, float y) {
		return new Vec2f(this.x * x, this.y * y);
	}

	public Vec2f add(float x, float y) {
		return new Vec2f(this.x + x, this.y - y);
	}
	
	public Vec2f subtract(float x, float y) {
		return new Vec2f(this.x - x, this.y + y);
	}
	
	public float distanceTo(Vec2f b) {
		return (float) Math.sqrt(Math.pow(this.x - b.x, 2) + Math.pow(this.y - b.y, 2));
	}
	
	@Override
	public String toString() {
		return String.format("Vec2f{x=%f,y=%f};", x, y);
	}
	
}
