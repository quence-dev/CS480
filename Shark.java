
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Shark {

	public static final int DEFAULT_SLICES = 36;
	public static final int DEFAULT_STACKS = 28;
	public static final boolean ENABLE_ROTATION = true;

	// how strong shark potential is
	private static final float FISH_FORCE = 0.2f;
	private static final float WALL_FORCE = -0.1f;

	// Set Tank Boundaries
	private static final float X_MAX = 1.8f;
	private static final float Y_MAX = 1.8f;
	private static final float Z_MAX = 1.8f;
	private static final float X_MIN = -1.8f;
	private static final float Y_MIN = -1.8f;
	private static final float Z_MIN = -1.8f;

	private Random rand;
	private Quaternion q = new Quaternion();
	private Quaternion u = new Quaternion();
	private float[] rotation = new float[16];
	
	private int shark_object;
	private int tail_object;

	/////// ANGLES ////////////
	private float scale;
	private float body_angle; // initial angle of shark body
	private float delta = 1.0f; //
	private float tail_angle; // initial angle of the tail
	private float tail_delta; // change in angle of tail

	/////// POSITION /////////
	public float x;
	public float y;
	public float z;
	private float x_speed = 0.004f;
	private float y_speed = 0.0025f;
	private float z_speed = 0.004f;
	private float x_direction, y_direction, z_direction;

	// previous position
	private float prev_x, prev_y, prev_z;

	// Fish object
	private Fish fish = null;
	private Fish fish2 = null;
	private Fish fish3 = null;
	private Fish fish4 = null;
	private Fish fish5 = null;
	
	public Shark(float scale_, float x_, float y_, float z_) {
		rand = new Random();

		scale = scale_;
		body_angle = 0;
		tail_angle = 0;
		tail_delta = 1;

		x = prev_x = x_;
		y = prev_y = y_;
		z = prev_z = z_;
		x_direction = rand.nextFloat();
		y_direction = rand.nextFloat();
		z_direction = rand.nextFloat();
	}

	public void init(GL2 gl) {
		GLUT glut = new GLUT();

		initBody(glut, gl);
		initTail(glut, gl);
	}
	
	public void update(GL gl) {
		moveFins();
		updatePotential();

		// store current position
		prev_x = x;
		prev_y = y;
		prev_z = z;

		// apply velocity to shark
		x += (x_speed*x_direction);
		y += (y_speed*y_direction);
		z += (z_speed*z_direction);
		
		Coord prev = new Coord(prev_x, prev_y, prev_z);
		Coord current = new Coord(x, y, z);

		//setAngle(prev, current);
		setAngleQ(prev, current);

		checkForWall();
	}
	
	public void draw(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		if (ENABLE_ROTATION) gl.glMultMatrixf(rotation, 0);
		gl.glRotatef(180, 0, 1, 0);
		drawShark(gl);
		drawTail(gl);

		gl.glPopMatrix();
	}
	
	/////// functions for creating fish /////////////////////

	// helper method to initialize front half of shark
	private void initBody(GLUT glut, GL2 gl) {
		shark_object = gl.glGenLists(1);
		gl.glNewList(shark_object, GL2.GL_COMPILE);

		gl.glPushMatrix();
		gl.glScalef(1.0f, 0.5f, 1.0f);
		glut.glutSolidCylinder(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPushMatrix();
		gl.glRotated(180, 0.0f, 1.0f, 0.0f);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);

		gl.glPushMatrix();
		gl.glTranslated(0, scale * 0.5, -scale * 0.5);
		gl.glRotated(-90, 1.0f, 0.0f, 0.0f);
		gl.glScalef(0.5f, 1.0f, 1.0f);
		glut.glutSolidCone(scale, scale * 3, DEFAULT_SLICES, DEFAULT_STACKS);

		gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glPopMatrix();

		// right fin
		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 0.5);
		gl.glRotated(90, 0.0f, 1.0f, 0.0f);
		gl.glScalef(1.0f, 0.25f, 1.0f);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();

		// left fins
		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 0.5);
		gl.glRotated(-90, 0.0f, 1.0f, 0.0f);
		gl.glScalef(1.0f, 0.25f, 1.0f);
		glut.glutSolidCone(scale, scale * 2, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();

		gl.glEndList();
	}

	// helper method to initialize tail of shark
	private void initTail(GLUT glut, GL2 gl) {
		tail_object = gl.glGenLists(1);
		gl.glNewList(tail_object, GL2.GL_COMPILE);

		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 2.0);
		gl.glScalef(1.0f, 0.5f, 1.0f);
		glut.glutSolidCone(scale, scale * 3, DEFAULT_SLICES, DEFAULT_STACKS);

		gl.glPushMatrix();
		gl.glTranslated(0, 0, scale * 3);
		gl.glRotated(180, 0.0f, 1.0f, 0.0f);
		gl.glScalef(0.5f, 2.0f, 1.0f);
		glut.glutSolidCone(scale, scale * 3, DEFAULT_SLICES, DEFAULT_STACKS);
		gl.glPopMatrix();
		gl.glPopMatrix();

		gl.glEndList();
	}
	
	// helper method for displaying shark body
	private void drawShark(GL2 gl) {
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glRotatef(body_angle, 0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.27f, 0.51f, 0.71f); // grey-blue
		gl.glCallList(shark_object);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}
	
	// helper method for displaying shark tail
	private void drawTail(GL2 gl) {
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glRotatef(tail_angle, 0.0f, 1.0f, 0.0f);
		gl.glColor3f(0.27f, 0.51f, 0.71f); // grey-blue
		gl.glCallList(tail_object);
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	// Articulates motion of fins
	private void moveFins() {
		if (body_angle > 15 || body_angle < -15)
			delta *= -1;
		body_angle -= delta;

		if (tail_angle > 15 || tail_angle < -15)
			tail_delta *= -1;
		tail_angle += tail_delta;
	}

	private void updatePotential() {
		Coord totalForces, q1 = new Coord(), q2 = new Coord(), q3 = new Coord(), q4 = new Coord(), q5 = new Coord();
		Coord p = new Coord(x,y,z);
		
		if (fish != null) q1 = new Coord(fish.x, fish.y, fish.z);
		if (fish2 != null) q2 = new Coord(fish2.x, fish2.y, fish2.z);
		if (fish3 != null) q3 = new Coord(fish3.x, fish3.y, fish3.z);
		if (fish4 != null) q4 = new Coord(fish4.x, fish4.y, fish4.z);
		if (fish5 != null) q5 = new Coord(fish5.x, fish5.y, fish5.z);
		
		Coord x1 = new Coord(X_MIN, y, z);
		Coord x2 = new Coord(X_MAX, y, z);
		Coord y1 = new Coord(x, Y_MIN, z);
		Coord y2 = new Coord(x, Y_MAX, z);
		Coord z1 = new Coord(x, y, Z_MIN);
		Coord z2 = new Coord(x, y, Z_MAX);

		Coord[] wall_forces = 
					{ calcPotential(p, x1, WALL_FORCE),
					calcPotential(p, x2, WALL_FORCE),
					calcPotential(p, y1, WALL_FORCE),
					calcPotential(p, y2, WALL_FORCE),
					calcPotential(p, z1, WALL_FORCE),
					calcPotential(p, z2, WALL_FORCE) };

		totalForces = addCoords(wall_forces);
		
		if (fish != null)
			totalForces = addCoord(totalForces, calcPotential(p, q1, FISH_FORCE));
		if (fish2 != null)
			totalForces = addCoord(totalForces, calcPotential(p, q2, FISH_FORCE));
		if (fish3 != null)
			totalForces = addCoord(totalForces, calcPotential(p, q3, FISH_FORCE));
		if (fish4 != null)
			totalForces = addCoord(totalForces, calcPotential(p, q4, FISH_FORCE));
		if (fish5 != null)
			totalForces = addCoord(totalForces, calcPotential(p, q5, FISH_FORCE));
		
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
		float x = (float) (force * (q.x-p.x) * Math.pow(Math.E, -1*(Math.pow(q.x-p.x, 2) + Math.pow(q.y-p.y, 2) + Math.pow(q.z-p.z, 2))));
		float y = (float) (force * (q.y-p.y) * Math.pow(Math.E, -1*(Math.pow(q.x-p.x, 2) + Math.pow(q.y-p.y, 2) + Math.pow(q.z-p.z, 2))));
		float z = (float) (force * (q.z-p.z) * Math.pow(Math.E, -1*(Math.pow(q.x-p.x, 2) + Math.pow(q.y-p.y, 2) + Math.pow(q.z-p.z, 2))));
		Coord out = new Coord(x,y,z);
		return out;
	}

	private void checkForWall() {
		float randomNum = rand.nextFloat();

		// flip direction if near wall to a new random direction

		if (x > X_MAX || x < X_MIN) {
			if (x_direction < 0) 
				x_direction = randomNum;
			else
				x_direction = -1*randomNum;
		}
		if (y > Y_MAX || y < Y_MIN) { 
			if (y_direction < 0) 
				y_direction = randomNum;
			else
				y_direction = -1*randomNum;
		}
		if (z > Z_MAX || z < Z_MIN) {
			if (z_direction < 0) 
				z_direction = randomNum;
			else
				z_direction = -1*randomNum;
		}
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
		Coord vector = new Coord();
		Quaternion t = new Quaternion();
		
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

		Coord a = new Coord();
		a = crossProduct(prev, current);
		a = normalize(a);
		float w = dotProduct(prev, current) + (float) Math.sqrt(Math.pow(magnitude(prev), 2) * Math.pow(magnitude(current), 2));
//		
		
		q.set(w,(float)a.x, (float)a.y, (float)a.z);
		q.normalize();
		q = q.multiply(u);
		
		rotation = q.to_matrix();
		u = q;
		
//		u.set(theta, (float) vector.x, 0, 0);
//		u.normalize();
//		q.set(theta, 0, (float) vector.y, 0);
//		q = u.multiply(q);
//		q.normalize();
//		t.set(theta, 0, 0, (float) vector.z);
//		t = q.multiply(t);
//		t.normalize();
//		
//		rotation = t.to_matrix();
//		u = t;
	}

	private float[] makeRotationMatrix(Coord u, Coord v, Coord n, Coord c) {
		float[] out = { (float)u.x, (float)u.y, (float)u.z, 0,
						(float)v.x, (float)v.y, (float)v.z, 0,
						(float)n.x, (float)n.y, (float)n.z, 0,
						(float)c.x, (float)c.y, (float)c.z, 1 };
		return out;
	}
	
	
	// Helper functions for finding angle
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
	
	private float dotProduct(Coord c1, Coord c2) {
		return (float) (c1.x * c2.x + c1.y * c2.y + c1.z * c2.z);
	}
	
	private float magnitude(Coord c) {
		return (float) Math.sqrt(Math.pow(c.x, 2) + Math.pow(c.y, 2) + Math.pow(c.z, 2));
	}


	// to access fish position
	public void setPrey(Fish f) {
		fish = f;
	}
	
	public void setPrey2(Fish f) {
		fish2 = f;
	}
	
	public void setPrey3(Fish f) {
		fish3 = f;
	}
	public void setPrey4(Fish f) {
		fish4 = f;
	}
	public void setPrey5(Fish f) {
		fish5 = f;
	}

	public void deletePrey() {
		fish = null;
	}
	
	public void deletePrey2() {
		fish2 = null;
	}
	
	public void deletePrey3() {
		fish3 = null;
	}
	public void deletePrey4() {
		fish4 = null;
	}
	public void deletePrey5() {
		fish5 = null;
	}

}
