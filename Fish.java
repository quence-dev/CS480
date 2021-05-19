
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Fish {

	public static final int DEFAULT_SLICES = 36;
	public static final int DEFAULT_STACKS = 28;
	public static final boolean ENABLE_ROTATION = true;

	// how strong fish potential is
	private static final float SHARK_FORCE = 0.5f;
	private static final float WALL_FORCE = 0.1f;
	private static final float FRIEND_FORCE = -0.0f;

	// Set Tank Boundaries
	private static final float X_MAX = 1.8f;
	private static final float X_MIN = -1.8f;
	private static final float Y_MAX = 1.8f;
	private static final float Y_MIN = -1.8f;
	private static final float Z_MAX = 1.8f;
	private static final float Z_MIN = -1.8f;
	

	private Random rand;
	private Quaternion q = new Quaternion();
	private Quaternion u = new Quaternion();
	private float[] rotation = new float[16];
	private int fish_object;
	private int tail_object;
	private int fin_object;

	/////// ANGLES ////////////
	private float scale;
	private float body_angle; // initial angle of fish body
	private float tail_angle; // initial angle of the tail
	private float tail_delta; // change in angle of tail
	private float fin_angle;
	private float fin_delta;

	/////// POSITION /////////
	public float x;
	public float y;
	public float z;
	private float x_speed = 0.01f;
	private float y_speed = 0.005f;
	private float z_speed = 0.01f;
	private float x_direction, y_direction, z_direction;

	// previous position
	private float prev_x, prev_y, prev_z;


	private Shark shark = null;
	public boolean isDead = false;
	private Fish friend = null;
	private Fish friend2 = null;
	private Fish friend3 = null;
	private Fish friend4 = null;
	

	public Fish(float scale_, float x_, float y_, float z_) {
		rand = new Random();

		scale = scale_;
		body_angle = 0;
		tail_angle = 0;
		tail_delta = 1;
		fin_angle = 0;
		fin_delta = 1;

		x = prev_x = x_;
		y = prev_y = y_;
		z = prev_z = z_;
		x_direction = rand.nextFloat();
		y_direction = rand.nextFloat();
		z_direction = rand.nextFloat();
	}

	public void init(GL2 gl) {
		GLUT glut = new GLUT();

		initFish(glut, gl);
		initTail(glut, gl);
		initFins(glut, gl);
	}

	public void update(GL gl) {
		moveFins();
		updatePotential();

		// store current position
		prev_x = x;
		prev_y = y;
		prev_z = z;

		// apply velocity to fish
		x += (x_speed * x_direction);
		y += (y_speed * y_direction);
		z += (z_speed * z_direction);

		Coord prev = new Coord(prev_x, prev_y, prev_z);
		Coord current = new Coord(x, y, z);

		//setAngleQ(prev, current);
		setAngle(prev, current);
		checkForWall();
	}

	public void draw(GL2 gl) {

		gl.glPushMatrix();
		//gl.glTranslatef(x, y, z);
		if (ENABLE_ROTATION) gl.glMultMatrixf(rotation, 0);
		gl.glRotatef(180, 0, 1, 0);
		drawFish(gl);
		drawTail(gl);
		drawFins(gl);
		gl.glPopMatrix();
	}

	/////// functions for creating fish /////////////////////

	private void initFish(GLUT glut, GL2 gl) {
		fish_object = gl.glGenLists(1);
		gl.glNewList(fish_object, GL2.GL_COMPILE);
		gl.glPushMatrix();
		glut.glutSolidCylinder(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPushMatrix();
		glut.glutSolidSphere(scale, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glEndList();
	}

	private void initTail(GLUT glut, GL2 gl) {
		tail_object = gl.glGenLists(1);
		gl.glNewList(tail_object, GL2.GL_COMPILE);

		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 2);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);

		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 2.0);
		gl.glRotated(180, 0.0, 1.0, 0.0);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glPopMatrix();

		gl.glEndList();
	}

	private void initFins(GLUT glut, GL2 gl) {
		fin_object = gl.glGenLists(1);
		gl.glNewList(fin_object, GL2.GL_COMPILE);

		gl.glPushMatrix();

		gl.glTranslated(scale * 2, 0, scale * 0.5);
		gl.glRotatef(-90, 0, 1, 0);
		gl.glScalef(0.15f, 1.0f, 1.0f);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(-scale * 2, 0, scale * 0.5);
		gl.glRotatef(90, 0, 1, 0);
		gl.glScalef(0.15f, 1.0f, 1.0f);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();

		gl.glEndList();
	}

	// helper method for displaying fish body
	private void drawFish(GL2 gl) {
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glRotatef(body_angle, 0, 1, 0);
		gl.glColor3f(0.85f, 0.55f, 0.20f); // Orange
		gl.glCallList(fish_object);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	// helper method for displaying fish tail
	private void drawTail(GL2 gl) {
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glRotatef(tail_angle, 0, 1, 0);
		gl.glColor3f(0.85f, 0.55f, 0.20f); // Orange
		gl.glCallList(tail_object);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	// helper method for displaying fish fins
	private void drawFins(GL2 gl) {
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glRotatef(fin_angle, 0, 1, 0);
		gl.glColor3f(0.85f, 0.55f, 0.20f); // Orange
		gl.glCallList(fin_object);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	// Articulates motion for body of fish
	private void moveFins() {
		if (tail_angle > 15 || tail_angle < -15)
			tail_delta *= -1;
		tail_angle += tail_delta;

		if (fin_angle > 15 || fin_angle < -15)
			fin_delta *= -1;
		fin_angle -= fin_delta;
	}
	///////////////////////////////////////////////////////

	// calculating gradients
	private void updatePotential() {
		Coord p = new Coord(x, y, z), q2 = new Coord(), q3 = new Coord(), q4 = new Coord(), q5 = new Coord();
		Coord q = new Coord(shark.x, shark.y, shark.z);
		
		if (friend != null ) q2 = new Coord(friend.x,friend.y, friend.z);
		if (friend2 != null ) q3 = new Coord(friend2.x,friend2.y, friend2.z);
		if (friend3 != null ) q4 = new Coord(friend3.x,friend3.y, friend3.z);
		if (friend4 != null ) q5 = new Coord(friend4.x,friend4.y, friend4.z);
		
		Coord x1 = new Coord(X_MIN - 0.1, y, z);
		Coord x2 = new Coord(X_MAX + 0.1, y, z);
		Coord y1 = new Coord(x, Y_MIN - 0.1, z);
		Coord y2 = new Coord(x, Y_MAX + 0.1, z);
		Coord z1 = new Coord(x, y, Z_MIN - 0.1);
		Coord z2 = new Coord(x, y, Z_MAX + 0.1);

		Coord[] forces = { calcPotential(p, q, SHARK_FORCE), calcPotential(p, x1, WALL_FORCE),
				calcPotential(p, x2, WALL_FORCE), calcPotential(p, y1, WALL_FORCE), calcPotential(p, y2, WALL_FORCE),
				calcPotential(p, z1, WALL_FORCE), calcPotential(p, z2, WALL_FORCE) };

		Coord totalForces = addCoords(forces);

		if (friend != null ) totalForces = addCoord(totalForces, calcPotential(p, q2, FRIEND_FORCE));
		if (friend2 != null ) totalForces = addCoord(totalForces, calcPotential(p, q3, FRIEND_FORCE));
		if (friend3 != null ) totalForces = addCoord(totalForces, calcPotential(p, q4, FRIEND_FORCE));
		if (friend4 != null ) totalForces = addCoord(totalForces, calcPotential(p, q5, FRIEND_FORCE));
		
		x_direction += totalForces.x;
		y_direction += totalForces.y;
		z_direction += totalForces.z;

	}

	private Coord addCoords(Coord[] coords) {
		Coord total = new Coord();

		for (Coord coord : coords) {
			total.x += coord.x;
			total.y += coord.y;
			total.z += coord.z;
		}

		return total;
	}
	
	private Coord addCoord(Coord c1, Coord c2) {
		Coord total = new Coord();
		total.x = c1.x + c2.x;
		total.y = c1.y + c2.y;
		total.z = c1.z + c2.z;
		return total;
	}

	private Coord calcPotential(Coord p, Coord q, float force) {
		float x = (float) (-force * (q.x - p.x)
				* Math.pow(Math.E, -1 * (Math.pow(q.x - p.x, 2) + Math.pow(q.y - p.y, 2) + Math.pow(q.z - p.z, 2))));
		float y = (float) (-force * (q.y - p.y)
				* Math.pow(Math.E, -1 * (Math.pow(q.x - p.x, 2) + Math.pow(q.y - p.y, 2) + Math.pow(q.z - p.z, 2))));
		float z = (float) (-force * (q.z - p.z)
				* Math.pow(Math.E, -1 * (Math.pow(q.x - p.x, 2) + Math.pow(q.y - p.y, 2) + Math.pow(q.z - p.z, 2))));
		Coord out = new Coord(x, y, z);
		return out;
	}

	private void setAngle(Coord prev, Coord current) {
		float dx, dy, dz;
		Coord vector = new Coord();
		Coord up = new Coord(0,1,0);
		Coord left = new Coord();
		Coord perp = new Coord();

		dx = (float) (current.x - prev.x);
		dy = (float) (current.y - prev.y);
		dz = (float) (current.z - prev.z);

		vector.x = dx;
		vector.y = dy;
		vector.z = dz;
		vector = normalize(vector);

		left = crossProduct(vector, up);
		left = normalize(left);
		
		perp = crossProduct(vector, left);
		perp = normalize(perp);
		
		rotation = makeRotationMatrix(left, perp, vector, current);	
	}
	
	private void setAngleQ(Coord prev, Coord current) {
		float dx, dy, dz;
		Quaternion t = new Quaternion();
		Coord vector = new Coord();
		
		dx = (float) (current.x - prev.x);
		dy = (float) (current.y - prev.y);
		dz = (float) (current.z - prev.z);
		
		vector.x = dx;
		vector.y = dy;
		vector.z = dz;
		vector = normalize(vector);
	
		// angle of rotation
		float cos = dotProduct(prev, current) / (magnitude(prev) * magnitude(current));
		float theta = (float) Math.toDegrees(Math.acos(cos))*2.0f;
		
		//float sin = magnitude(crossProduct(prev, current)) / (magnitude(prev) * magnitude(current));
		//float thetaAlt = (float) Math.toDegrees(Math.asin(sin))*2.0f;
		
//		q.set(theta, (float) vector.x, (float) vector.y, (float) vector.z);
//		q.normalize();
//		rotation = q.to_matrix();
//		
		u.set(theta, (float) vector.x, 0, 0);
		u.normalize();
		q.set(theta, 0, (float) vector.y, 0);
		q = u.multiply(q);
		q.normalize();
		t.set(theta, 0, 0, (float) vector.z);
		t = q.multiply(t);
		t.normalize();
		
		rotation = t.to_matrix();
		u = t;
	}

	private float[] makeRotationMatrix(Coord u, Coord v, Coord n, Coord c) {
		float[] out = { (float)u.x, (float)u.y, (float)u.z, 0,
						(float)v.x, (float)v.y, (float)v.z, 0,
						(float)n.x, (float)n.y, (float)n.z, 0,
						(float)c.x, (float)c.y, (float)c.z, 1 };
		return out;
	}

	// Helper functions for finding angle	
	private float magnitude(Coord c) {
		return (float) Math.sqrt(Math.pow(c.x, 2) + Math.pow(c.y, 2) + Math.pow(c.z, 2));
	}

	private Coord normalize(Coord c) {
		Coord out = new Coord(0, 0, 0);
		double mag = magnitude(c);

		out.x = c.x / mag;
		out.y = c.y / mag;
		out.z = c.z / mag;

		return out;
	}

	private Coord crossProduct(Coord c1, Coord c2) {
		Coord out = new Coord();
		out.x = c1.y * c2.z - c1.z * c2.y;
		out.y = c1.x * c2.z - c1.z * c2.x;
		out.z = c1.x * c2.y - c1.y * c2.x;
		return out;
	}

	private void checkForWall() {
		float randomNum = rand.nextFloat();

		// flip direction if near wall to a new random direction

		if (x > X_MAX || x < X_MIN) {
			if (x_direction < 0)
				x_direction = randomNum;
			else
				x_direction = -1 * randomNum;
		}
		if (y > Y_MAX || y < Y_MIN) {
			if (y_direction < 0)
				y_direction = randomNum;
			else
				y_direction = -1 * randomNum;
		}
		if (z > Z_MAX || z < Z_MIN) {
			if (z_direction < 0)
				z_direction = randomNum;
			else
				z_direction = -1 * randomNum;
		}
	}

	// to access shark position
	public void setPredator(Shark shark) {
		this.shark = shark;
	}

	public void setFriendA(Fish f) {
		friend = f;
	}
	
	public void setFriendB(Fish f) {
		friend2 = f;
	}
	
	public void setFriendC(Fish f) {
		friend3 = f;
	}
	
	public void setFriendD(Fish f) {
		friend4 = f;
	}
	
	public void deleteFriendA() {
		friend = null;
	}
	public void deleteFriendB() {
		friend2 = null;
	}
	public void deleteFriendC() {
		friend3 = null;
	}
	public void deleteFriendD() {
		friend4 = null;
	}
	
	
	public boolean isDead() {
		return isDead;
	}
	
	/// UNUSED HELPERS ////////
	@SuppressWarnings("unused")
	private float dotProduct(Coord c1, Coord c2) {
		return (float) (c1.x * c2.x + c1.y * c2.y + c1.z * c2.z);
	}
	@SuppressWarnings("unused")
	private float magnitude(float x, float y, float z) {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}
	@SuppressWarnings("unused")
	private float distance(Coord c1, Coord c2) {
		return (float) Math.sqrt(Math.pow(c1.x - c2.x, 2) + Math.pow(c1.y - c2.y, 2) + Math.pow(c1.z - c2.z, 2));
	}


}
