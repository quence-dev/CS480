/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
public class AmbientLight extends Light {

	//only need 1 ambient light per scene
	public AmbientLight(ColorType _c) {
		color = new ColorType(_c); 
	}
	
	public AmbientLight() {
		color = new ColorType();
	}
	
	public ColorType applyLight(Material mat, Point3D v, Point3D n){
		ColorType amb = new ColorType();
		
		amb.r = mat.ka.r*color.r;
		amb.g = mat.ka.g*color.g;
		amb.b = mat.ka.b*color.b;
		
		amb.clamp();
		
		return amb;
	}

}
