/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class Cylinder3D {

	private Point3D center;
	private float r, height;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Cylinder3D(float _x, float _y, float _z, float _r, int _stacks, int _slices, float _h)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		height = _h;
		stacks = _stacks;
		slices = _slices;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_height(float _h)
	{
		height = _h;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_stacks(int _stacks)
	{
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_slices(int _slices)
	{
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return slices;
	}
	
	public int get_m()
	{
		return stacks;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(stacks,slices);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		
		double PI = Math.PI;
		double h, theta;
		double d_height = height/(stacks-1);
		double dtheta = 2*PI/(slices - 1);
		int i, j;
		
		for (i = 0, h = -height/2; i < stacks; i++, h += d_height) {
		
			// bottom of the cylinder
			if (i == 0) {
				for (j= 0; j < slices; j++) {
					mesh.v[i][j].x = center.x;
					mesh.v[i][j].y = center.y;
					mesh.v[i][j].z = center.z + (float)h;
					
					mesh.n[i][j].x = 0.0f;
					mesh.n[i][j].y = 0.0f;
					mesh.n[i][j].z = -1.0f;
				}
			} else if ( i == stacks - 1) { 				// top of the cylinder
				for (j= 0; j < slices; j++) {
					mesh.v[i][j].x = center.x;
					mesh.v[i][j].y = center.y;
					mesh.v[i][j].z = center.z + (float)h;
					
					mesh.n[i][j].x = 0.0f;
					mesh.n[i][j].y = 0.0f;
					mesh.n[i][j].z = 1.0f;
				}
			} else {
				for (j= 0, theta = -PI; j< slices; j++, theta += dtheta) {
					double cos_theta = Math.cos(theta);
					double sin_theta = Math.sin(theta);
					
					mesh.v[i][j].x = center.x + r*(float)cos_theta;
					mesh.v[i][j].y = center.y + r*(float)sin_theta;
					mesh.v[i][j].z = center.z + (float)h;
					
					mesh.n[i][j].x = (float)cos_theta;
					mesh.n[i][j].y = (float)sin_theta;
					mesh.n[i][j].z = 0.0f;
				}
			}
		
		}
	}

}
