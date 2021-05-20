/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class SpotLight extends Light {

	public Point3D vector_L;
	public float d; 
	public float radatten = 1.0f;
	public float a0 = 1f;
	public float a1 = 2f;
	public float a2 = 3f;
	public float angatten;
	public float a_L = 2f;
	
	public SpotLight(ColorType _c, Point3D _direction, Point3D _position, float _a) 
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
		position = new Point3D(_position);
		a_L = _a;
	}
	
	public SpotLight() 
	{
		color = new ColorType();
		direction = new Point3D();
		position = new Point3D();
	}
	
	public Point3D getPosition() {
		return this.position;
	}
	
	
	public Point3D calculate_L(Point3D light_position, Point3D object) {
		Point3D temp = light_position.minus(object);
		temp.normalize();
		return temp;
	}
	
	public float calcRadAttenuation(float _d) {	
		return (float) (1 / (a0 + a1*d + a2*Math.pow(_d, 2)));
	}
	
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Boolean doDif, Boolean doSpec){
		
		//Radial Attenuation
		d = (float)position.minus(v).magnitude();
		radatten = calcRadAttenuation(d);
		
		//Angular Attenuation
		vector_L = calculate_L(position, v);

		// -L
		Point3D vObj = vector_L.scale(-1);
		Point3D vL = direction;
		vL.normalize();
		Point3D cs = vL.minus(vObj);
		//float cos_alpha = vObj.dotProduct(vL);
		float cos_alpha = cs.dotProduct(vL);

		float PI = (float) Math.PI;
		float limit = PI/20;
		float cos_theta = (float)Math.cos(limit);
		
		
		if (Math.abs(cos_alpha) >= Math.abs(cos_theta)) {
			angatten = (float)Math.pow(cos_alpha, a_L);
			//System.out.println("IN BOUNDS: " + cos_alpha);
		} else {
			angatten = 0.0f;
			//System.out.println("out of cone: " + cos_alpha);
		}
		
		ColorType res = new ColorType();
		
		ColorType dif = new ColorType();
		
		if (n.dotProduct(vector_L) > 0 && doDif) {
			dif.r = mat.kd.r*color.r*n.dotProduct(vector_L)*radatten*angatten;
			dif.g = mat.kd.g*color.g*n.dotProduct(vector_L)*radatten*angatten;
			dif.b = mat.kd.b*color.b*n.dotProduct(vector_L)*radatten*angatten; 
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
			spec.r = mat.ks.r*color.r*temp*radatten*angatten;
			spec.g = mat.ks.g*color.g*temp*radatten*angatten;
			spec.b = mat.ks.b*color.b*temp*radatten*angatten;
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
