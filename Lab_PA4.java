/* 
 * Spencer Vilicic
 * CS480 PA4
 * 12/8/20
 *  bonus: implements superellipsoids
 */
//****************************************************************************
//       Example Main Program for CS480 PA4
//****************************************************************************
// Description: 
//   
//   This is a lighting and object rendering demonstration  
//
//     The following keys control the program:
//
// Q,q: quit
// C,c: clear polygon (set vertex count=0)
// R,r: randomly change the color
// T,t: show testing examples (toggles between smooth shading and flat shading
// test cases)
// >: increase the step number for examples
// <: decrease the step number for examples
// +,-: increase or decrease spectral exponent
// F,f: flat surface rendering
// G,g: Gouraud rendering
// P,p: Phong rendering
// S,s: Toggle specular lighting
// D,d: Toggle diffuse lighting
// A,a,1: Toggle ambient lighting
// 2: 	Toggle infinite light
// 3: 	Toggle point light
// 4: 	Toggle spot light
// J,j:	Toggle smooth shading 
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//   Nov 2014 modified to include test cases
//   Nov 5, 2019 Updated by Zezhou Sun
//

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl

public class Lab_PA4 extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	enum Shape {
		SPHERE, TORUS, CUBE, CYLINDER, ELLIPSE, SUPERELLIPSE
	}

	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH = 512;
	private final int DEFAULT_WINDOW_HEIGHT = 512;
	private final float DEFAULT_LINE_WIDTH = 1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;

	// specular exponent for materials
	private int ns = 2;

	//lights
	private AmbientLight amb_light;
	private InfiniteLight inf_light;
	private PointLight pt_light;
	private SpotLight s_light;
	
	//shapes
	private Sphere3D sphere;
	private Ellipsoid3D ellipse;
	private Torus3D torus;
	private Cylinder3D cylinder;
	private Box3D cube;
	private SuperEllipsoid3D superEll;
	
	//materials
	private Material mat_shape;
	private Material mat_ellipse;
	private Material mat_torus;
	private Material mat_cylinder;
	private Material mat_cube;
	private Material mat_super;
	
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading, doFlatShading, 
		doGouraudShading, doPhongShading, doDif, doSpec,
		L1, L2, L3, L4;
	private int Nsteps;
	public Mesh3D depthBuffer = new Mesh3D(512, 512);

	/** The quaternion which controls the rotation of the world. */
	private Quaternion viewing_quaternion = new Quaternion();
	private Point3D viewing_center = new Point3D((float) (DEFAULT_WINDOW_WIDTH / 2),
			(float) (DEFAULT_WINDOW_HEIGHT / 2), (float) 0.0);
	/** The last x and y coordinates of the mouse press. */
	private int last_x = 0, last_y = 0;
	/** Whether the world is being rotated. */
	private boolean rotate_world = false;

	/** Random colors **/
	private ColorType[] colorMap = new ColorType[100];
	private Random rand = new Random();

	public Lab_PA4() {
		capabilities = new GLCapabilities(null);
		capabilities.setDoubleBuffered(true); // Enable Double buffering

		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
		canvas.setFocusable(true);
		getContentPane().add(canvas);

		animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

		numTestCase = 3;
		testCase = 0;
		Nsteps = 12;

		setTitle("CS480/680 Lab for PA4");
		setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);

		rng = new Random();
		color = new ColorType(1.0f, 0.0f, 0.0f);
		lineSegs = new ArrayList<Point2D>();
		triangles = new ArrayList<Point2D>();
		doSmoothShading = false;
		doFlatShading = true;
		doGouraudShading = false;
		doPhongShading = false;
		doDif = true;
		doSpec = true;
		L1 = L2 = L3 = L4 = true;
		
		for (int i = 0; i < 100; i++) {
			this.colorMap[i] = new ColorType(i * 0.005f + 0.5f, i * -0.005f + 1f, i * 0.0025f + 0.75f);
		}
	}

	public void run() {
		animator.start();
	}

	public static void main(String[] args) {
		Lab_PA4 P = new Lab_PA4();
		P.run();
	}

	// ***********************************************
	// GLEventListener Interfaces
	// ***********************************************
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glLineWidth(DEFAULT_LINE_WIDTH);
		Dimension sz = this.getContentPane().getSize();
		buff = new BufferedImage(sz.width, sz.height, BufferedImage.TYPE_3BYTE_BGR);
		clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		WritableRaster wr = buff.getRaster();
		DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
		byte[] data = dbb.getData();

		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		gl.glDrawPixels(buff.getWidth(), buff.getHeight(), GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
		drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		// deliberately left blank
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// deliberately left blank
	}

	void clearPixelBuffer() {
		lineSegs.clear();
		triangles.clear();
		Graphics2D g = buff.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
		g.dispose();
	}

	// drawTest
	void drawTestCase() {
		/* clear the window and vertex state */
		clearPixelBuffer();

		// System.out.printf("Test case = %d\n",testCase);

		switch (testCase) {
		case 0:
			shadeTest(doSmoothShading, 0); /* smooth shaded, sphere and torus */
			break;
		case 1:
			shadeTest(doSmoothShading, 1); /* flat shaded, sphere and torus */
			break;
		case 2:
			shadeTest(doSmoothShading, 2);
			break;
		}
	}

	// ***********************************************
	// KeyListener Interfaces
	// ***********************************************
	public void keyTyped(KeyEvent key) {
		// Q,q: quit
		// C,c: clear polygon (set vertex count=0)
		// R,r: randomly change the color
		// T,t: show testing examples (toggles between smooth shading and flat shading
		// test cases)
		// >: increase the step number for examples
		// <: decrease the step number for examples
		// +,-: increase or decrease spectral exponent
		// F,f: flat surface rendering
		// G,g: Gouraud rendering
		// P,p: Phong rendering
		// S,s: Toggle specular lighting
		// D,d: Toggle diffuse lighting
		// A,a,1: Toggle ambient lighting
		// 2: 	Toggle infinite light
		// 3: 	Toggle point light
		// 4: 	Toggle spot light
		// J,j:	Toggle smooth shading 

		switch (key.getKeyChar()) {
		case 'Q':
		case 'q':
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		case 'R':
		case 'r':
			color = new ColorType(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
			break;
		case 'C':
		case 'c':
			clearPixelBuffer();
			break;
		case 'S':
		case 's':
			doSpec = !doSpec;
			drawTestCase();
			break;
		case 'T':
		case 't':
			testCase = (testCase + 1) % numTestCase;
			drawTestCase();
			break;
		case '<':
			Nsteps = Nsteps < 4 ? Nsteps : Nsteps / 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
		case '>':
			Nsteps = Nsteps > 190 ? Nsteps : Nsteps * 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
		case '+':
		case '=':
			ns++;
			drawTestCase();
			break;
		case '-':
		case '_':
			if (ns > 0)
				ns--;
			drawTestCase();
			break;
		case 'J':
		case 'j':
				doSmoothShading = !doSmoothShading;
				drawTestCase();
				break;
		case 'F':
		case 'f':
			doFlatShading = true;
			doGouraudShading = false;
			doPhongShading = false;
			drawTestCase();
			break;
		case 'G':
		case 'g':
			doGouraudShading = true;
			doFlatShading = false;
			doPhongShading = false;
			drawTestCase();
			break;
		case 'P':
		case 'p':
			doPhongShading = true;
			doFlatShading = false;
			doGouraudShading = false;
			drawTestCase();
			break;
		case 'D':
		case 'd':
			// TODO: diffuse lighting
			doDif = !doDif;
			drawTestCase();
			break;
		case 'A':
		case 'a':
		case '1':
			// TODO: ambient lighting
			L1 = !L1;
			drawTestCase();
			break;
		case '2':
			L2 = !L2;
			drawTestCase();
			break;
		case '3':
			L3 = !L3;
			drawTestCase();
			break;
		case '4':
			L4 = !L4;
			drawTestCase();
			break;
		default:
			break;
		}
	}

	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent key) {
		// deliberately left blank
	}

	// **************************************************
	// MouseListener and MouseMotionListener Interfaces
	// **************************************************
	public void mouseClicked(MouseEvent mouse) {
		// deliberately left blank
	}

	public void mousePressed(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			rotate_world = true;
		}
	}

	public void mouseReleased(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			rotate_world = false;
		}
	}

	public void mouseMoved(MouseEvent mouse) {
		// Deliberately left blank
	}

	/**
	 * Updates the rotation quaternion as the mouse is dragged.
	 * 
	 * @param mouse The mouse drag event object.
	 */
	public void mouseDragged(final MouseEvent mouse) {
		if (this.rotate_world) {
			// get the current position of the mouse
			final int x = mouse.getX();
			final int y = mouse.getY();

			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;

			// create a unit vector in the direction of the vector (dy, dx, 0)
			final float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
			if (magnitude > 0.0001) {
				// define axis perpendicular to (dx,-dy,0)
				// use -y because origin is in upper lefthand corner of the window
				final float[] axis = new float[] { -(float) (dy / magnitude), (float) (dx / magnitude), 0 };

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f;
				final float s = (float) Math.sin(0.5f * viewing_delta);
				final float c = (float) Math.cos(0.5f * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);
				this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

				// normalize to counteract acccumulating round-off error
				this.viewing_quaternion.normalize();

				// save x, y as last x, y
				this.last_x = x;
				this.last_y = y;
				drawTestCase();
			}
		}

	}

	public void mouseEntered(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void mouseExited(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	// **************************************************
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	// **************************************************
	
	void initTest() {
		float radius = (float) 50.0;
		amb_light = new AmbientLight();
		inf_light = new InfiniteLight();
		pt_light = new PointLight();
		s_light = new SpotLight();
		mat_shape = new Material(
				new ColorType(0.0f, 0.0f, 0.0f), 
				new ColorType(0.0f, 0.0f, 0.0f), 
				new ColorType(1f, 1f, 1f), 1);
		// these could all have their own materials and reflective properties
		// but for now they're the same
		mat_ellipse = new Material(
				new ColorType(0.8f, 0.0f, 0.0f), 
				new ColorType(1.0f, 1.0f, 1.0f), 
				new ColorType(0.1f, 0.1f, 0.1f), ns);
		mat_torus = new Material(
				new ColorType(0.8f, 0.0f, 0.0f), 
				new ColorType(1.0f, 1.0f, 1.0f), 
				new ColorType(0.1f, 0.1f, 0.1f), ns);
		mat_cylinder = new Material(
				new ColorType(0.8f, 0.0f, 0.0f), 
				new ColorType(1.0f, 1.0f, 1.0f), 
				new ColorType(0.1f, 0.1f, 0.1f), ns);
		mat_cube = new Material(
				new ColorType(0.8f, 0.0f, 0.0f), 
				new ColorType(1.0f, 1.0f, 1.0f), 
				new ColorType(0.1f, 0.1f, 0.1f), ns);
		mat_super = new Material(
				new ColorType(0.8f, 0.0f, 0.0f), 
				new ColorType(1.0f, 1.0f, 1.0f), 
				new ColorType(0.1f, 0.1f, 0.1f), ns);
		
		// test 0
		sphere = new Sphere3D((float) 128.0, (float) 128.0, (float) 128.0, (float) 1.5 * radius, Nsteps, Nsteps);
		torus = new Torus3D((float) 300.0, (float) 300.0, (float) 128.0, radius, 2 * Nsteps, 2 * Nsteps, (float) 2 * radius);
		// test 1
		ellipse = new Ellipsoid3D((float) 128.0, (float) 128.0, (float) 128.0, radius, (float) 2 * radius,
				(float) 0.75 * radius, Nsteps, Nsteps);
		cylinder = new Cylinder3D((float) 300.0, (float) 128.0, (float) 128.0, radius, 3 * Nsteps, 3 * Nsteps,
				(float) 4 * radius);
		// test 2
		cube = new Box3D((float) 128.0, (float) 128.0, (float) 128.0, (float) 1.5 * radius, 0.2f, 2 * Nsteps, 2 * Nsteps);
		superEll = new SuperEllipsoid3D((float) 300.0, (float) 256.0, (float) 128.0, (float) 2 * radius,
				(float) 1.5 * radius, (float) 1.5 * radius, 3.0f, 0.8f, 4 * Nsteps, 2 * Nsteps);
	}

	void shadeTest(boolean doSmooth, int test_case) {
		initTest();
		
		
		// triangle meshes
		//Mesh3D mesh, mesh2;
		Mesh3D[] meshes = new Mesh3D[2];
		
		int i, j, n, m;
		
		Integer[] n_array = new Integer[2];
		Integer[] m_array = new Integer[2];
		
		
		switch (test_case) {
			case 0: //sphere and torus
				meshes[0] = sphere.mesh;
				n_array[0] = sphere.get_n();
				m_array[0] = sphere.get_m();
				meshes[1] = torus.mesh;
				n_array[1] = torus.get_n();
				m_array[1] = torus.get_m();
				break;
			case 1: //cylinder and ellipsoid
				meshes[0] = cylinder.mesh;
				n_array[0] = cylinder.get_n();
				m_array[0] = cylinder.get_m();
				meshes[1] = ellipse.mesh;
				n_array[1] = ellipse.get_n();
				m_array[1] = ellipse.get_m();
				break;
			case 2: //cube and superellipse
				meshes[0] = cube.mesh;
				n_array[0] = cube.get_n();
				m_array[0] = cube.get_m();
				meshes[1] = superEll.mesh;
				n_array[1] = superEll.get_n();
				m_array[1] = superEll.get_m();
				break;
		}
		
		if (L1) amb_light = new AmbientLight(new ColorType(0.5f, 0.5f, 0.5f));
		if (L2) inf_light = new InfiniteLight(new ColorType(0f, 1.0f, 0.0f), new Point3D(-0.8f, 0.2f, 1f));
		if (L3) pt_light = new PointLight(new ColorType(1f, 0.0f, 0.0f), new Point3D(0f,-0.5f, 1f));
		if (L4) s_light = new SpotLight(new ColorType(0f, 0.0f, 1.0f), new Point3D(1f, 1f, -0.5f), new Point3D(1f,1f, 2f), ns);

		// view vector is defined along z axis
		// this example assumes simple othorgraphic projection
		// view vector is used in
		// (a) calculating specular lighting contribution
		// (b) backface culling / backface rejection
		Point3D view_vector = new Point3D((float) 0.0, (float) 0.0, (float) 1.0);

		// normal to the plane of a triangle
		// to be used in backface culling / backface rejection
		Point3D triangle_normal = new Point3D();
		
		//initialize depth buffer z-values to max value
//		for (int r = 0; r < depthBuffer.rows; r++) {
//			for (int c = 0; c < depthBuffer.cols; c++) {
//				depthBuffer.v[r][c].z = Integer.MAX_VALUE;
//			}
//		}

		// rotate the surface's 3D mesh using quaternion
		for (int w = 0; w < 2; w++) {
		meshes[w].rotateMesh(viewing_quaternion, viewing_center);
		
		// temporary variables for triangle 3D vertices and 3D normals
				Point3D v0, v1, v2, n0, n1, n2;
		
		// projected triangle, with vertex colors
		Point2D[] tri = { new Point2D(), new Point2D(), new Point2D() };

		// draw triangles for the current surface, using vertex colors
		for (i = 0; i < m_array[w] - 1; ++i) {
			for (j = 0; j < n_array[w] - 1; ++j) {
				
				v0 = meshes[w].v[i][j];
				v1 = meshes[w].v[i + 1][j];
				v2 = meshes[w].v[i + 1][j + 1];
				
				n0 = meshes[w].n[i][j];
				n1 = meshes[w].n[i+1][j];
				n2 = meshes[w].n[i+1][j+1];
				
				//depth buffer
//				if (v0.z < depthBuffer.v[i][j].z) {
//					depthBuffer.v[i][j].z = v0.z;
//				}
//				if (v1.z < depthBuffer.v[i+1][j].z) {
//					depthBuffer.v[i+1][j].z = v1.z;
//				}
//				if (v2.z < depthBuffer.v[i + 1][j + 1].z) {
//					depthBuffer.v[i+ 1][j+ 1].z = v2.z;
//				}

				triangle_normal = computeTriangleNormal(v0, v1, v2);

				if (view_vector.dotProduct(triangle_normal) > 0.0) // front-facing triangle?
				{
					// flat shading: use the normal to the triangle itself
					if (doFlatShading) {
						//n2 = n1 = n0 = triangle_normal;
						tri[2].c = tri[1].c = tri[0].c = amb_light.applyLight(mat_shape, view_vector, triangle_normal)
								.add(inf_light.applyLight(mat_shape, view_vector, triangle_normal, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, view_vector,triangle_normal, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, view_vector,triangle_normal, doDif, doSpec));
					}
					
					//Gouraud shading
					if (doGouraudShading) {
						tri[0].c = amb_light.applyLight(mat_shape, v0, n0)
								.add(inf_light.applyLight(mat_shape, v0, n0, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v0, n0, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v0,n0, doDif, doSpec));
						tri[1].c = amb_light.applyLight(mat_shape, v1, n1)
								.add(inf_light.applyLight(mat_shape, v1, n1, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v1, n1, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v1,n1, doDif, doSpec));
						tri[2].c = amb_light.applyLight(mat_shape, v2, n2)
								.add(inf_light.applyLight(mat_shape, v2, n2, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v2, n2, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v2,n2, doDif, doSpec));
					}
					
					if (doPhongShading) {
						//TODO
					}

					tri[0].x = (int) v0.x;
					tri[0].y = (int) v0.y;
					tri[1].x = (int) v1.x;
					tri[1].y = (int) v1.y;
					tri[2].x = (int) v2.x;
					tri[2].y = (int) v2.y;

					SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], true, depthBuffer, v0, v1, v2);
				}

				v0 = meshes[w].v[i][j];
				v1 = meshes[w].v[i + 1][j + 1];
				v2 = meshes[w].v[i][j + 1];
				
				n0 = meshes[w].n[i][j];
				n1 = meshes[w].n[i + 1][j + 1];
				n2 = meshes[w].n[i][j + 1];
				
				// update depth buffer
//				if (v0.z < depthBuffer.v[i][j].z) {
//					depthBuffer.v[i][j].z = v0.z;
//				}
//				if (v1.z < depthBuffer.v[i + 1][j + 1].z) {
//					depthBuffer.v[i+ 1][j+ 1].z = v1.z;
//				}
//				if (v2.z < depthBuffer.v[i][j + 1].z) {
//					depthBuffer.v[i][j + 1].z = v2.z;
//				}
				
				triangle_normal = computeTriangleNormal(v0, v1, v2);

				if (view_vector.dotProduct(triangle_normal) > 0.0) // front-facing triangle?
				{
					// flat shading: use the normal to the triangle itself
					if (doFlatShading) {
						//n2 = n1 = n0 = triangle_normal;
						tri[2].c = tri[1].c = tri[0].c = amb_light.applyLight(mat_shape, view_vector, triangle_normal)
								.add(inf_light.applyLight(mat_shape, view_vector, triangle_normal, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, view_vector,triangle_normal, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, view_vector,triangle_normal, doDif, doSpec));
					}
					
					//Gouraud shading
					if (doGouraudShading) {
						tri[0].c = amb_light.applyLight(mat_shape, v0, n0)
								.add(inf_light.applyLight(mat_shape, v0, n0, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v0, n0, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v0, n0, doDif, doSpec));
						tri[1].c = amb_light.applyLight(mat_shape, v1, n1)
								.add(inf_light.applyLight(mat_shape, v1, n1, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v1, n1, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v1,n1, doDif, doSpec));
						tri[2].c = amb_light.applyLight(mat_shape, v2, n2)
								.add(inf_light.applyLight(mat_shape, v2, n2, doDif, doSpec))
								.add(pt_light.applyLight(mat_shape, v2, n2, doDif, doSpec))
								.add(s_light.applyLight(mat_shape, v2,n2, doDif, doSpec));
					}
					
					if (doPhongShading) {
						//TODO
					}

					tri[0].x = (int) v0.x;
					tri[0].y = (int) v0.y;
					tri[1].x = (int) v1.x;
					tri[1].y = (int) v1.y;
					tri[2].x = (int) v2.x;
					tri[2].y = (int) v2.y;

					SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], true, depthBuffer, v0, v1, v2);
				}
			}
		}
		}
	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2) {
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);

		if (norm.magnitude() > 0.000001)
			norm.normalize();
		else // detect degenerate triangle and set its normal to zero
			norm.set((float) 0.0, (float) 0.0, (float) 0.0);

		return norm;
	}

}