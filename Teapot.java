

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.util.*;

public class Teapot
{
  private int teapot_object;
  private float scale;
  private float angle;
  private float x_distance;
  private float y_distance;
  private float z_distance;
  public float x;
  public float y;
  public float z;

  public Teapot( float scale_, float x_, float y_, float z_)
  {
    scale = scale_;
    angle = 0;
    x = x_;
    y = y_;
    z = z_;
    
    x_distance = 0.0f;
    y_distance = 0.0f;
    z_distance = 0.0f;
  }

  public void init( GL2 gl )
  {

    teapot_object = gl.glGenLists(1);
    gl.glNewList( teapot_object, GL2.GL_COMPILE );
	    // create the teapot triangles 
	    GLUT glut = new GLUT();
	    glut.glutSolidTeapot( scale ); 
    gl.glEndList();

  }

  public void update( GL gl )
  {
    //angle += 5;
    if (x_distance >= 1.75f)
    	x_distance = 1.75f;
    x_distance += 0.005f;
    
  }

  public void draw( GL2 gl )
  {
    gl.glPushMatrix();
    gl.glPushAttrib( GL2.GL_CURRENT_BIT );
    gl.glTranslatef(x_distance, y, z);
    gl.glRotatef( angle, 0.0f, 1.0f, 0.0f );
    gl.glColor3f( 0.85f, 0.55f, 0.20f); // Orange
    gl.glCallList( teapot_object );
    gl.glPopAttrib();
    gl.glPopMatrix();
  }
}
