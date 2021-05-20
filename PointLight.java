/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class PointLight extends Light {
	
	public Point3D vector_L;	
	public float d; 
	public float radatten = 1.0f;
	public float a0 = 1f;
	public float a1 = 2f;
	public float a2 = 3f;	
	
	public PointLight(ColorType _c, Point3D _position) 
	{
		color = new ColorType(_c);
		position = new Point3D(_position);
	}
	
	public PointLight() 
	{
		color = new ColorType();
		position = new Point3D();
	}
	
	public Point3D getPosition() {
		return this.position;
	}
	
	
	public Point3D calculate_L(Point3D light, Point3D object) {
		Point3D temp = light.minus(object);
		temp.normalize();
		return temp;
	}
	
	public float applyAttenuation(float _d) {
		return (float) (1 / (a0 + a1*d + a2*Math.pow(_d, 2)));
	}
	
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Boolean doDif, Boolean doSpec){
		
		vector_L = calculate_L(position, v);
		
		d = (float)position.minus(v).magnitude();
		radatten = applyAttenuation(d);
		
		
		ColorType res = new ColorType();
		
		ColorType dif = new ColorType();
		
		if (n.dotProduct(vector_L) > 0 && doDif) {
			dif.r = mat.kd.r*color.r*n.dotProduct(vector_L)*radatten;
			dif.g = mat.kd.g*color.g*n.dotProduct(vector_L)*radatten;
			dif.b = mat.kd.b*color.b*n.dotProduct(vector_L)*radatten; 
		} 
		else {
			dif.r = 0f;
			dif.g = 0f;
			dif.b = 0f; 
		}
		
		ColorType spec = new ColorType();
		
		Point3D r = vector_L.reflect(n);
		float temp = (float)Math.pow((double)v.dotProduct(r), (double)mat.ns);
		
		if ((temp > 0 && n.dotProduct(vector_L) > 0) && doSpec) {
			spec.r = mat.ks.r*color.r*temp*radatten;
			spec.g = mat.ks.g*color.g*temp*radatten;
			spec.b = mat.ks.b*color.b*temp*radatten;
		}
		else {
			spec.r = 0f;
			spec.g = 0f;
			spec.b = 0f;
		}
		
		res.r = dif.r + spec.r;
		res.g = dif.g + spec.g;
		res.b = dif.b + spec.b;
		
		res.clamp();
		
		return res;
	}

}
