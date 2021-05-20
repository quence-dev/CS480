/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class InfiniteLight extends Light{
	
	public InfiniteLight(ColorType _c, Point3D _direction) 
	{
		color = new ColorType(_c);
		direction = new Point3D(_direction);
	}
	
	public InfiniteLight() 
	{
		color = new ColorType();
		direction = new Point3D();
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Boolean doDif, Boolean doSpec){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//
		
		ColorType dif = new ColorType();
		
		if (n.dotProduct(direction) > 0 && doDif) {
			dif.r = mat.kd.r*color.r*n.dotProduct(direction);
			dif.g = mat.kd.g*color.g*n.dotProduct(direction);
			dif.b = mat.kd.b*color.b*n.dotProduct(direction);
		} 
		else {
			dif.r = 0f;
			dif.g = 0f;
			dif.b = 0f;
		}
		
		ColorType spec = new ColorType();
		
		Point3D r = direction.reflect(n);
		float temp = (float)Math.pow((double)v.dotProduct(r), (double)mat.ns);
		
		if ((temp > 0 && n.dotProduct(direction) > 0) && doSpec) {
			spec.r = mat.ks.r*color.r*temp;
			spec.g = mat.ks.g*color.g*temp;
			spec.b = mat.ks.b*color.b*temp;
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
