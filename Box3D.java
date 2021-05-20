/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
import java.util.ArrayList;

//TODO
public class Box3D {

	private Point3D center;
	private float side, exp;
	private int stacks, slices;
	public Mesh3D mesh, meshFront, meshBack, meshTop, meshBottom, meshLeft, meshRight;
	public ArrayList<Mesh3D> m_array = new ArrayList<>();

	public Box3D(float _x, float _y, float _z, float _side, float _exp, int _stacks, int _slices) {
		center = new Point3D(_x, _y, _z);
		side = _side;
		exp = _exp;
		stacks = _stacks;
		slices = _slices;
		initMesh();
	}

	public void set_center(float _x, float _y, float _z) {
		center.x = _x;
		center.y = _y;
		center.z = _z;
		fillMesh(); // update the triangle mesh
	}

	public void set_side(float _side) {
		side = _side;
		fillMesh(); // update the triangle mesh
	}

	public void set_stacks(int _stacks) {
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
	}

	public void set_slices(int _slices) {
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}

	public int get_n() {
		return slices;
	}

	public int get_m() {
		return stacks;
	}
	
	public ArrayList<Mesh3D> get_array() {
		return m_array;
	}

	private void initMesh() {
//		meshFront = new Mesh3D(stacks, slices);
//		meshBack = new Mesh3D(stacks, slices);
//		meshTop = new Mesh3D(stacks, slices);
//		meshBottom = new Mesh3D(stacks, slices);
//		meshLeft = new Mesh3D(stacks, slices);
//		meshRight = new Mesh3D(stacks, slices);
		mesh = new Mesh3D(stacks, slices);
		fillMesh(); // set the mesh vertices and normals
	}

	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh() {
		//uses a single superellipse to model a cube
		double PI = Math.PI;
		double phi, theta;
		double dphi = 2*PI/(stacks-1);
		double dtheta = PI/(slices - 1);
		
		int i, j;
		
		for (i = 0, phi = 0; i < stacks; i++, phi += dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
			
			for (j= 0, theta = -PI/2; j < slices; j++, theta += dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = center.x + side * (float)Math.pow(cos_theta, exp) * (float)Math.pow(Math.abs(cos_phi), exp) * (float)Math.signum(cos_phi);
				mesh.v[i][j].y = center.y + side * (float)Math.pow(cos_theta, exp) * (float)Math.pow(Math.abs(sin_phi), exp) * (float)Math.signum(sin_phi);
				mesh.v[i][j].z = center.z + side * (float)Math.pow(Math.abs(sin_theta), exp) * (float)Math.signum(sin_theta);
				
				mesh.n[i][j].x = (float) (exp*(float)cos_theta*(float)cos_phi);
				mesh.n[i][j].y = (float) (exp*(float)sin_theta*(float)cos_phi);
				mesh.n[i][j].z = (float) (exp*(float)sin_phi);
			}
			
		}
		
		
		
		
		// ****************below is an implementation using 6 meshes that doesn't work*******************//
		/*
		double dside = s / (stacks - 1);
		double side;
		int i, j;

		// front
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshFront.v[i][j].x = center.x + (float) side;
				meshFront.v[i][j].y = center.y + (float) side;
				meshFront.v[i][j].z = center.z + (float) s / 2;

				meshFront.n[i][j].x = 0;
				meshFront.n[i][j].y = 0;
				meshFront.n[i][j].z = 1;
			}
		}

		// back
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshBack.v[i][j].x = center.x + (float) side;
				meshBack.v[i][j].y = center.y + (float) side;
				meshBack.v[i][j].z = center.z - (float) s / 2;

				meshBack.n[i][j].x = 0;
				meshBack.n[i][j].y = 0;
				meshBack.n[i][j].z = -1;
			}
		}

		// left
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshLeft.v[i][j].x = center.x - (float) s / 2;
				meshLeft.v[i][j].y = center.y + (float) side;
				meshLeft.v[i][j].z = center.z + (float) side;

				meshLeft.n[i][j].x = -1;
				meshLeft.n[i][j].y = 0;
				meshLeft.n[i][j].z = 0;
			}
		}

		// right
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshRight.v[i][j].x = center.x + (float) s / 2;
				meshRight.v[i][j].y = center.y + (float) side;
				meshRight.v[i][j].z = center.z + (float) side;

				meshRight.n[i][j].x = 1;
				meshRight.n[i][j].y = 0;
				meshRight.n[i][j].z = 0;
			}
		}

		// bottom
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshBottom.v[i][j].x = center.x + (float) side;
				meshBottom.v[i][j].y = center.y - (float) s / 2;
				meshBottom.v[i][j].z = center.z + (float) side;

				meshBottom.n[i][j].x = 0;
				meshBottom.n[i][j].y = -1;
				meshBottom.n[i][j].z = 0;
			}
		}

		// top
		for (i = 0, side = -s / 2; i < stacks; i++, side += dside) {
			for (j = 0; j < slices; j++) {
				meshTop.v[i][j].x = center.x + (float) side;
				meshTop.v[i][j].y = center.y + (float) s / 2;
				meshTop.v[i][j].z = center.z + (float) side;

				meshTop.n[i][j].x = 0;
				meshTop.n[i][j].y = 1;
				meshTop.n[i][j].z = 0;
			}
		}
		
		m_array.add(meshFront);
		m_array.add(meshBack);
		m_array.add(meshTop);
		m_array.add(meshBottom);
		m_array.add(meshLeft);
		m_array.add(meshRight);
		*/
	}
}
