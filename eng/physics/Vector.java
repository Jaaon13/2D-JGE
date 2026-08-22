package physics;

public class Vector {
	
	public float x = 0f, y = 0f;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector() {}
	
	public Vector normalize() {
		
		float magnitude = (float) Math.sqrt( (Math.pow(x, 2) + Math.pow(y, 2)) );
		
		Vector nVec = new Vector();
		
		nVec.x = (x != 0f) ? x / magnitude : 0f;
		nVec.y = (y != 0f) ? y / magnitude : 0f;
		
		return nVec;
		
	}

	public boolean isZero() {
		
		if(Math.abs(x) < 0.001f && Math.abs(y) < 0.001f) {
			return true;
		}
		
		return false;
	}
	
	public void clamp(float f) {
		
		if(x > 0) { // Is positive
			if(x > f) {
				x = f;
			}
		} else {
			if(x < (f * -1)) {
				x = f * -1;
			}
		}
		
		if(y > 0) { // Is positive
			if(y > f) {
				y = f;
			}
		} else {
			if(y < (f * -1)) {
				y = f * -1;
			}
		}
		
	}

	public float magnitude() {
		return (float) Math.sqrt( (Math.pow(x, 2) + Math.pow(y, 2)) );
	}
	
	public void reset() {
		this.x = 0f;
		this.y = 0f;
	}

	public void add(Vector other) {
		this.x += other.x;
		this.y += other.y;
	}
	
	public Vector getInverse() {
		return new Vector(this.x * -1f, this.y * -1f);
	}

	public Vector copy() {
		return new Vector(this.x, this.y);
	}
	
	@Override
	public String toString() {
		return this.x + ", " + this.y;
	}
	
}
