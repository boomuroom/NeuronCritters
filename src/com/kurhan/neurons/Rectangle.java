package com.kurhan.neurons;

public class Rectangle {
	private final Vector topLeft;
	private final Vector dimensions;

	public Rectangle(Vector topLeft, Vector dimensions) {
		this.topLeft = topLeft;
		this.dimensions = dimensions;
	}

	public Rectangle(double left, double top, double right, double bottom) {
		this.topLeft = new Vector(left, top);
		this.dimensions = new Vector(right - left, top - bottom);
	}

	public boolean contains(Vector point) {
		boolean insideX = left() <= point.x && point.x <= right();
		boolean insideY = bottom() <= point.y && point.y <= top();
		return insideX && insideY;
	}

	public double left() {
		return topLeft.x;
	}

	public double right() {
		return topLeft.x + dimensions.x;
	}

	public double top() {
		return topLeft.y;
	}

	public double bottom() {
		return topLeft.y - dimensions.y;
	}

	public double width() {
		return dimensions.x;
	}

	public double height() {
		return dimensions.y;
	}

	public Vector dimensions() {
		return dimensions;
	}
}
