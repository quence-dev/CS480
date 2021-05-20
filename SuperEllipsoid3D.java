/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class SuperEllipsoid3D {

	private Point3D center;
	private float ra, rb, rc, alpha, beta;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public SuperEllipsoid3D(float _x, float _y, float _z, float _ra, float _rb, float _rc, float _a, float _b, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		ra = _ra;
		rb = _rb;
		rc = _rc;
		alpha = _a;
		beta = _b;
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
	
	public void set_radiusA(float _ra)
	{
		ra = _ra;
		fillMesh(); // update the triangle mesh
	}
	public void set_radiusB(float _rb)
	{
		rb = _rb;
		fillMesh(); // update the triangle mesh
	}
	public void set_radiusC(float _rc)
	{
		rc = _rc;
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
				
				mesh.v[i][j].x = center.x + ra * (float)Math.pow(cos_theta, beta) * (float)Math.pow(Math.abs(cos_phi), alpha) * (float)Math.signum(cos_phi);
				mesh.v[i][j].y = center.y + rb * (float)Math.pow(cos_theta, beta) * (float)Math.pow(Math.abs(sin_phi), alpha) * (float)Math.signum(sin_phi);
				mesh.v[i][j].z = center.z + rc * (float)Math.pow(Math.abs(sin_theta), beta) * (float)Math.signum(sin_theta);
				
				mesh.n[i][j].x = (float) (beta*alpha*(float)cos_theta*(float)cos_phi);
				mesh.n[i][j].y = (float) (beta*alpha*(float)sin_theta*(float)cos_phi);
				mesh.n[i][j].z = (float) (beta*(float)sin_phi);
			}
			
		}
	}

}
