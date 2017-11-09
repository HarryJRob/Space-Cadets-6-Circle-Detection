public final class Circle {
	
	private int x;
	private int y;
	private int radius;
	private int priority;

	
	public Circle(int x, int y, int radius, int priority) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
