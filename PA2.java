/*
 * CS480 PA2
 * Ant Model
 * 
 * Name: Spencer Vilicic
 * Date: 10/13/2020
 * 
 * I have created an ant using spheres and cylinders,
 * which are each located in the LegSegment, Thorax, Head,
 * Abdomen, and Antenna java files.
 * 
 * The test cases show an ant walking after some meticulous
 * rotation calculations.
 * 
 */


/**
 * PA2.java - driver for the hand model simulation
 * 
 * History:
 * 
 * 19 February 2011
 * 
 * - added documentation
 * 
 * (Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>)
 * 
 * 16 January 2008
 * 
 * - translated from C code by Stan Sclaroff
 * 
 * (Tai-Peng Tian <tiantp@gmail.com>)
 * 
 */


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.jogamp.opengl.util.gl2.GLUT;//for new version of gl

/**
 * The main class which drives the hand model simulation.
 * 
 * @author Tai-Peng Tian <tiantp@gmail.com>
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2008
 */
public class PA2 extends JFrame implements GLEventListener, KeyListener,
    MouseListener, MouseMotionListener {

  /**
   * A finger which has a palm joint, a middle joint, and a distal joint.
   * 
   * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
   * @since Spring 2011
   */
  private class Leg {
    /** The distal joint of this finger. */
    private final Component tibiaJoint;
    /** The list of all the joints in this finger. */
    private final List<Component> joints;
    /** The middle joint of this finger. */
    private final Component femurJoint;
    /** The palm joint of this finger. */
    private final Component coxaJoint;

    /**
     * Instantiates this leg with the three specified joints.
     * 
     * @param coxaJoint
     *          The palm joint of this finger.
     * @param femurJoint
     *          The middle joint of this finger.
     * @param tibiaJoint
     *          The distal joint of this finger.
     */
    public Leg(final Component coxaJoint, final Component femurJoint,
        final Component tibiaJoint) {
      this.coxaJoint = coxaJoint;
      this.femurJoint = femurJoint;
      this.tibiaJoint = tibiaJoint;

      this.joints = Collections.unmodifiableList(Arrays.asList(this.coxaJoint,
          this.femurJoint, this.tibiaJoint));
    }

    /**
     * Gets the tibia joint of this leg.
     * 
     * @return The tibia joint of this leg.
     */
    Component tibiaJoint() {
      return this.tibiaJoint;
    }

    /**
     * Gets an unmodifiable view of the list of the joints of this finger.
     * 
     * @return An unmodifiable view of the list of the joints of this finger.
     */
    List<Component> joints() {
      return this.joints;
    }

    /**
     * Gets the femur joint of this leg.
     * 
     * @return The femur joint of this leg.
     */
    Component femurJoint() {
      return this.femurJoint;
    }

    /**
     * Gets the coxa joint of this leg.
     * 
     * @return The coxa joint of this leg.
     */
    Component coxaJoint() {
      return this.coxaJoint;
    }
  }

  /** The color for components which are selected for rotation. */
  public static final FloatColor ACTIVE_COLOR = FloatColor.RED;
  /** The default width of the created window. */
  public static final int DEFAULT_WINDOW_HEIGHT = 512;
  /** The default height of the created window. */
  public static final int DEFAULT_WINDOW_WIDTH = 512;
  /** The radius of the head. */
  public static final double HEAD_RADIUS = 0.25;
  /** The radius of the abdomen. */
  public static final double ABDOMEN_RADIUS = 0.5;
  /** The radius of the thorax. */
  public static final double THORAX_RADIUS = 0.5;
  /** The radius of each joint which comprises the leg. */
  public static final double LEG_RADIUS = 0.08;
  /** The radius of each antennae segment. */
  public static final double ANTENNA_RADIUS = 0.02;
  /** The radius of each antennae segment. */
  public static final double ANTENNA_JOINT_HEIGHT = 0.5;
  /** The color for components which are not selected for rotation. */
  public static final FloatColor INACTIVE_COLOR = FloatColor.ORANGE;
  /** The initial position of the top level component in the scene. */
  public static final Point3D INITIAL_POSITION = new Point3D(0, 0, 0);
  /** The height of the coxa joint on each of the legs. */
  public static final double COXA_JOINT_HEIGHT = 0.25;
  /** The height of the middle joint on each of the legs. */
  public static final double FEMUR_JOINT_HEIGHT = 0.5;
  /** The height of the tibia joint on each of the fingers. */
  public static final double TIBIA_JOINT_HEIGHT = 0.8;
  /** The angle by which to rotate the joint on user request to rotate. */
  public static final double ROTATION_ANGLE = 2.0;
  /** Randomly generated serial version UID. */
  private static final long serialVersionUID = -7060944143920496524L;

  /**
   * Runs the hand simulation in a single JFrame.
   * 
   * @param args
   *          This parameter is ignored.
   */
  public static void main(final String[] args) {
    new PA2().animator.start();
  }

  /**
   * The animator which controls the framerate at which the canvas is animated.
   */
  final FPSAnimator animator;
  /** The canvas on which we draw the scene. */
  private final GLCanvas canvas;
  /** The capabilities of the canvas. */
  private final GLCapabilities capabilities = new GLCapabilities(null);
  /** The legs on the body to be modeled. */
  private final Leg[] legs;
  /** The head to be modeled. */
  private final Component head;
  /** The OpenGL utility object. */
  private final GLU glu = new GLU();
  /** The OpenGL utility toolkit object. */
  private final GLUT glut = new GLUT();
  /** The thorax to be modeled. */
  private final Component thorax;
  /** The last x and y coordinates of the mouse press. */
  private int last_x = 0, last_y = 0;
  /** Whether the world is being rotated. */
  private boolean rotate_world = false;
  /** The axis around which to rotate the selected joints. */
  private Axis selectedAxis = Axis.X;
  /** The set of components which are currently selected for rotation. */
  private final Set<Component> selectedComponents = new HashSet<Component>(20);
  /**
   * The set of fingers which have been selected for rotation.
   * 
   * Selecting a joint will only affect the joints in this set of selected
   * fingers.
   **/
  private final Set<Leg> selectedLegs = new HashSet<Leg>(6);
  /** Whether the state of the model has been changed. */
  private boolean stateChanged = true;
  /**
   * The top level component in the scene which controls the positioning and
   * rotation of everything in the scene.
   */
  private final Component topLevelComponent;
  /** The abdomen to be modeled. */
  private final Component abdomen;
  /** The quaternion which controls the rotation of the world. */
  private Quaternion viewing_quaternion = new Quaternion();
  /** The set of all components. */
  private final List<Component> components;

  
  public static String LEFT_FRONT_COXA_NAME = "left front coxa";
  public static String LEFT_FRONT_FEMUR_NAME = "left front femur";
  public static String LEFT_FRONT_TIBIA_NAME = "left front tibia";
  public static String LEFT_MIDDLE_COXA_NAME = "left middle coxa";
  public static String LEFT_MIDDLE_FEMUR_NAME = "left middle femur";
  public static String LEFT_MIDDLE_TIBIA_NAME = "left middle tibia";
  public static String LEFT_REAR_COXA_NAME = "left rear coxa";
  public static String LEFT_REAR_FEMUR_NAME = "left rear femur";
  public static String LEFT_REAR_TIBIA_NAME = "left rear tibia";
  public static String RIGHT_FRONT_COXA_NAME = "right front coxa";
  public static String RIGHT_FRONT_FEMUR_NAME = "right front femur";
  public static String RIGHT_FRONT_TIBIA_NAME = "right front tibia";
  public static String RIGHT_MIDDLE_COXA_NAME = "right middle coxa";
  public static String RIGHT_MIDDLE_FEMUR_NAME = "right middle femur";
  public static String RIGHT_MIDDLE_TIBIA_NAME = "right middle tibia";
  public static String RIGHT_REAR_COXA_NAME = "right rear coxa";
  public static String RIGHT_REAR_FEMUR_NAME = "right rear femur";
  public static String RIGHT_REAR_TIBIA_NAME = "right rear tibia";
  public static String LEFT_ANTENNA_SCAPE_NAME = "left antenna scape";
  public static String LEFT_ANTENNA_FUNICLE_NAME = "left antenna funicle";
  public static String RIGHT_ANTENNA_SCAPE_NAME = "right antenna scape";
  public static String RIGHT_ANTENNA_FUNICLE_NAME = "right antenna funicle";
  public static String THORAX_NAME = "thorax";
  public static String HEAD_NAME = "head";
  public static String ABDOMEN_NAME = "abdomen";
  public static String TOP_LEVEL_NAME = "top level";

  /**
   * Initializes the necessary OpenGL objects and adds a canvas to this JFrame.
   */
  public PA2() {
    this.capabilities.setDoubleBuffered(true);

    this.canvas = new GLCanvas(this.capabilities);
    this.canvas.addGLEventListener(this);
    this.canvas.addMouseListener(this);
    this.canvas.addMouseMotionListener(this);
    this.canvas.addKeyListener(this);
    // this is true by default, but we just add this line to be explicit
    this.canvas.setAutoSwapBufferMode(true);
    this.getContentPane().add(this.canvas);

    // refresh the scene at 60 frames per second
    this.animator = new FPSAnimator(this.canvas, 60);

    this.setTitle("CS480/CS680 : Ant Simulator");
    this.setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    
    
    final Component practice = new Component(new Point3D(0,0,0), new exampractice(0.2,1, this.glut), "PRACTICE");
    
    
    
    
    // all the tibia joints
    final Component tibia1 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		LEFT_FRONT_TIBIA_NAME);
    final Component tibia2 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		LEFT_MIDDLE_TIBIA_NAME);
    final Component tibia3 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		LEFT_REAR_TIBIA_NAME);
    final Component tibia4 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		RIGHT_FRONT_TIBIA_NAME);
    final Component tibia5 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		RIGHT_MIDDLE_TIBIA_NAME);
    final Component tibia6 = new Component(new Point3D(0, 0, FEMUR_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.6, TIBIA_JOINT_HEIGHT, this.glut),
    		RIGHT_REAR_TIBIA_NAME);

    // all the middle joints
    final Component femur1 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		LEFT_FRONT_FEMUR_NAME);
    final Component femur2 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		LEFT_MIDDLE_FEMUR_NAME);
    final Component femur3 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		LEFT_REAR_FEMUR_NAME);
    final Component femur4 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		RIGHT_FRONT_FEMUR_NAME);
    final Component femur5 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		RIGHT_MIDDLE_FEMUR_NAME);
    final Component femur6 = new Component(new Point3D(0, 0, COXA_JOINT_HEIGHT),
    		new LegSegment(LEG_RADIUS*0.8, FEMUR_JOINT_HEIGHT, this.glut),
    		RIGHT_REAR_FEMUR_NAME);

    // all the coxa joints, displaced by various amounts from the thorax
    final Component coxa1 = new Component(new Point3D(-0.25, 0, 0.7),
        new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
        LEFT_FRONT_COXA_NAME);
    final Component coxa2 = new Component(new Point3D(0, 0, 0.7),
        new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
        LEFT_MIDDLE_COXA_NAME);
    final Component coxa3 = new Component(new Point3D(0.25, 0, 0.7),
        new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
        LEFT_REAR_COXA_NAME);
    final Component coxa4 = new Component(new Point3D(-0.25, 0, 0.3),
        new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
        RIGHT_FRONT_COXA_NAME);
    final Component coxa5 = new Component(new Point3D(0, 0, 0.3),
        new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
        RIGHT_MIDDLE_COXA_NAME);
    final Component coxa6 = new Component(new Point3D(0.25, 0, 0.3),
            new LegSegment(LEG_RADIUS, COXA_JOINT_HEIGHT, this.glut),
            RIGHT_REAR_COXA_NAME);
    
    // all the antenna joints
    final Component antenna1 = new Component(new Point3D(0,0, HEAD_RADIUS),
    		new Antenna(ANTENNA_RADIUS, ANTENNA_JOINT_HEIGHT, this.glut),
    		LEFT_ANTENNA_SCAPE_NAME);
    final Component antenna2 = new Component(new Point3D(0,0, ANTENNA_JOINT_HEIGHT),
    		new Antenna(ANTENNA_RADIUS, ANTENNA_JOINT_HEIGHT*2, this.glut),
    		LEFT_ANTENNA_FUNICLE_NAME);
    final Component antenna3 = new Component(new Point3D(0, 0, HEAD_RADIUS),
    		new Antenna(ANTENNA_RADIUS, ANTENNA_JOINT_HEIGHT, this.glut),
    		RIGHT_ANTENNA_SCAPE_NAME);
    final Component antenna4 = new Component(new Point3D(0,0, ANTENNA_JOINT_HEIGHT),
    		new Antenna(ANTENNA_RADIUS, ANTENNA_JOINT_HEIGHT*2, this.glut),
    		RIGHT_ANTENNA_FUNICLE_NAME);
    

    // put the legs together for easier selection by keyboard input later on
    this.legs = new Leg[] {
    	new Leg(coxa1, femur1, tibia1),
        new Leg(coxa2, femur2, tibia2),
        new Leg(coxa3, femur3, tibia3),
        new Leg(coxa4, femur4, tibia4),
        new Leg(coxa5, femur5, tibia5),
        new Leg(coxa6, femur6, tibia6), };

    // the thorax, which models the body
    this.thorax = new Component(new Point3D(0, 0, 0),
    		new Thorax(THORAX_RADIUS, this.glut),
    		THORAX_NAME);
    
    // the head, which models the head
    this.head = new Component(new Point3D(-0.6f, -0.1f, 0.25f),
        new Head(HEAD_RADIUS, this.glut),
        HEAD_NAME);

    // the abdomen which models the abdomen
    this.abdomen = new Component(new Point3D(0.7f, 0.1f, 0),
    		new Abdomen(ABDOMEN_RADIUS, this.glut),
    		ABDOMEN_NAME);

    
    // the top level component which provides an initial position and rotation
    // to the scene (but does not cause anything to be drawn)
    this.topLevelComponent = new Component(INITIAL_POSITION, TOP_LEVEL_NAME);

    //connect body to top component
    this.topLevelComponent.addChild(this.thorax);
    this.topLevelComponent.addChild(practice);
    
    // connect legs and body segments to thorax
    this.thorax.addChildren(coxa1, coxa2, coxa3, coxa4, coxa5, coxa6, this.head, this.abdomen);
    coxa1.addChild(femur1);
    coxa2.addChild(femur2);
    coxa3.addChild(femur3);
    coxa4.addChild(femur4);
    coxa5.addChild(femur5);
    coxa6.addChild(femur6);
    femur1.addChild(tibia1);
    femur2.addChild(tibia2);
    femur3.addChild(tibia3);
    femur4.addChild(tibia4);
    femur5.addChild(tibia5);
    femur6.addChild(tibia6);

    
    // connect antenna to head
    this.head.addChildren(antenna1, antenna3);
    antenna1.addChild(antenna2);
    antenna3.addChild(antenna4);
    
    // turn the ant to correct viewing angle
    this.topLevelComponent.rotate(Axis.Y, -90);
    this.topLevelComponent.rotate(Axis.X, 180);
    
    // turn head to correct angle
    this.head.rotate(Axis.Z, -45);
    
    // set rotation limits for the head
    this.head.setXPositiveExtent(60);
    this.head.setXNegativeExtent(0);
    this.head.setYPositiveExtent(60);
    this.head.setYNegativeExtent(0);
    this.head.setZPositiveExtent(0);
    this.head.setZNegativeExtent(-60);
    
    // turn abdomen to correct angle
    this.abdomen.rotate(Axis.Z, 20);
    // set rotation limits for the abdomen
    this.abdomen.setXPositiveExtent(60);
    this.abdomen.setXNegativeExtent(0);
    this.abdomen.setYPositiveExtent(0);
    this.abdomen.setYNegativeExtent(0);
    this.abdomen.setZPositiveExtent(30);
    this.abdomen.setZNegativeExtent(0);

    // initialize rotation of coxa leg segments
    coxa1.rotate(Axis.Y, -10);
    coxa2.rotate(Axis.Y, 0);
    coxa3.rotate(Axis.Y, 10);
    coxa4.rotate(Axis.Y, 190);
    coxa5.rotate(Axis.Y, 180);
    coxa6.rotate(Axis.Y, 170);
    
    //set Y coxa rotation limits for left legs
    coxa1.setYPositiveExtent(15);
    coxa1.setYNegativeExtent(-25);
    coxa2.setYPositiveExtent(20);
    coxa2.setYNegativeExtent(-20);
    coxa3.setYPositiveExtent(25);
    coxa3.setYNegativeExtent(-15);
    
    //set Y coxa rotation limits for right legs
    coxa4.setYPositiveExtent(205);
    coxa4.setYNegativeExtent(165);
    coxa5.setYPositiveExtent(200);
    coxa5.setYNegativeExtent(160);
    coxa6.setYPositiveExtent(195);
    coxa6.setYNegativeExtent(155);
    
    //rotate antennae
    antenna3.rotate(Axis.Y, 180);
    antenna1.rotate(Axis.Z, -45);
    antenna3.rotate(Axis.Z, 45);
    antenna2.rotate(Axis.X, 100);
    antenna4.rotate(Axis.X, 100);
    
    // set X and Z rotation limits for coxa joints
    for (final Component coxaJoint : Arrays.asList(coxa1, coxa2, coxa3,
    		coxa4, coxa5, coxa6)) {
      coxaJoint.setXPositiveExtent(5);
      coxaJoint.setXNegativeExtent(-5);
      coxaJoint.setZPositiveExtent(0);
      coxaJoint.setZNegativeExtent(0);
    }

    // set rotation limits for the middle joints of the finger
    for (final Component femurJoint : Arrays.asList(femur1, femur2,
        femur3, femur4, femur5, femur6)) {
      femurJoint.setXPositiveExtent(80);
      femurJoint.setXNegativeExtent(15);
      femurJoint.setYPositiveExtent(0);
      femurJoint.setYNegativeExtent(0);
      femurJoint.setZPositiveExtent(0);
      femurJoint.setZNegativeExtent(0);
      //set initial rotation
      femurJoint.rotate(Axis.X, 45);
    }

    // set rotation limits for the distal joints of the finger
    for (final Component tibiaJoint : Arrays.asList(tibia1, tibia2,
        tibia3, tibia4, tibia5, tibia6)) {
      tibiaJoint.setXPositiveExtent(-5);
      tibiaJoint.setXNegativeExtent(-120);
      tibiaJoint.setYPositiveExtent(0);
      tibiaJoint.setYNegativeExtent(0);
      tibiaJoint.setZPositiveExtent(0);
      tibiaJoint.setZNegativeExtent(0);
      //set initial rotation
      tibiaJoint.rotate(Axis.X, -90);
    }

    // create the list of all the components for debugging purposes
    this.components = Arrays.asList(coxa1, femur1, tibia1, coxa2, femur2,
        tibia2, coxa3, femur3, tibia3, coxa4, femur4, tibia4, coxa5,
        femur5, tibia5, coxa6, femur6, tibia6, this.thorax, this.head, this.abdomen);
  }

  /**
   * Redisplays the scene containing the hand model.
   * 
   * @param drawable
   *          The OpenGL drawable object with which to create OpenGL models.
   */
  public void display(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // clear the display
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    // from here on affect the model view
    gl.glMatrixMode(GL2.GL_MODELVIEW);

    // start with the identity matrix initially
    gl.glLoadIdentity();

    // rotate the world by the appropriate rotation quaternion
    gl.glMultMatrixf(this.viewing_quaternion.toMatrix(), 0);

    // update the position of the components which need to be updated
    // TODO only need to update the selected and JUST deselected components
    if (this.stateChanged) {
      this.topLevelComponent.update(gl);
      this.stateChanged = false;
    }

    // redraw the components
    this.topLevelComponent.draw(gl);
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param drawable
   *          This parameter is ignored.
   * @param modeChanged
   *          This parameter is ignored.
   * @param deviceChanged
   *          This parameter is ignored.
   */
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged) {
    // intentionally unimplemented
  }

  /**
   * Initializes the scene and model.
   * 
   * @param drawable
   *          {@inheritDoc}
   */
  public void init(final GLAutoDrawable drawable) {
    final GL2 gl = (GL2)drawable.getGL();

    // perform any initialization needed by the hand model
    this.topLevelComponent.initialize(gl);

    // initially draw the scene
    this.topLevelComponent.update(gl);

    // set up for shaded display of the hand
    final float light0_position[] = { 1, 1, 1, 0 };
    final float light0_ambient_color[] = { 0.25f, 0.25f, 0.25f, 1 };
    final float light0_diffuse_color[] = { 1, 1, 1, 1 };

    gl.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glShadeModel(GL2.GL_SMOOTH);

    // set up the light source
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0_position, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0_ambient_color, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0_diffuse_color, 0);

    // turn lighting and depth buffering on
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);
  }

  /**
   * Interprets key presses according to the following scheme:
   * 
   * up-arrow, down-arrow: increase/decrease rotation angle
   * 
   * @param key
   *          The key press event object.
   */
  public void keyPressed(final KeyEvent key) {
    switch (key.getKeyCode()) {
    case KeyEvent.VK_KP_UP:
    case KeyEvent.VK_UP:
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, ROTATION_ANGLE);
      }
      this.stateChanged = true;
      break;
    case KeyEvent.VK_KP_DOWN:
    case KeyEvent.VK_DOWN:
      for (final Component component : this.selectedComponents) {
        component.rotate(this.selectedAxis, -ROTATION_ANGLE);
      }
      this.stateChanged = true;
      break;
    default:
      break;
    }
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param key
   *          This parameter is ignored.
   */
  public void keyReleased(final KeyEvent key) {
    // intentionally unimplemented
  }

  private final TestCases testCases = new TestCases();

  private void setModelState(final Map<String, Angled> state) {
    this.thorax.setAngles(state.get(THORAX_NAME));
    this.head.setAngles(state.get(HEAD_NAME));
    this.abdomen.setAngles(state.get(ABDOMEN_NAME));
    this.legs[0].coxaJoint().setAngles(state.get(LEFT_FRONT_COXA_NAME));
    this.legs[0].femurJoint().setAngles(state.get(LEFT_FRONT_FEMUR_NAME));
    this.legs[0].tibiaJoint().setAngles(state.get(LEFT_FRONT_TIBIA_NAME));
    this.legs[1].coxaJoint().setAngles(state.get(LEFT_MIDDLE_COXA_NAME));
    this.legs[1].femurJoint().setAngles(state.get(LEFT_MIDDLE_FEMUR_NAME));
    this.legs[1].tibiaJoint().setAngles(state.get(LEFT_MIDDLE_TIBIA_NAME));
    this.legs[2].coxaJoint().setAngles(state.get(LEFT_REAR_COXA_NAME));
    this.legs[2].femurJoint().setAngles(state.get(LEFT_REAR_FEMUR_NAME));
    this.legs[2].tibiaJoint().setAngles(state.get(LEFT_REAR_TIBIA_NAME));
    this.legs[3].coxaJoint().setAngles(state.get(RIGHT_FRONT_COXA_NAME));
    this.legs[3].femurJoint().setAngles(state.get(RIGHT_FRONT_FEMUR_NAME));
    this.legs[3].tibiaJoint().setAngles(state.get(RIGHT_FRONT_TIBIA_NAME));
    this.legs[4].coxaJoint().setAngles(state.get(RIGHT_MIDDLE_COXA_NAME));
    this.legs[4].femurJoint().setAngles(state.get(RIGHT_MIDDLE_FEMUR_NAME));
    this.legs[4].tibiaJoint().setAngles(state.get(RIGHT_MIDDLE_TIBIA_NAME));
    this.legs[5].coxaJoint().setAngles(state.get(RIGHT_REAR_COXA_NAME));
    this.legs[5].femurJoint().setAngles(state.get(RIGHT_REAR_FEMUR_NAME));
    this.legs[5].tibiaJoint().setAngles(state.get(RIGHT_REAR_TIBIA_NAME));
    
    this.stateChanged = true;
  }

  /**
   * Interprets typed keys according to the following scheme:
   * 
   * 1 : toggle the first finger (thumb) active in rotation
   * 
   * 2 : toggle the second finger active in rotation
   * 
   * 3 : toggle the third finger active in rotation
   * 
   * 4 : toggle the fourth finger active in rotation
   * 
   * 5 : toggle the fifth finger active in rotation
   * 
   * 6 : toggle the hand for rotation
   * 
   * 7 : toggle the forearm for rotation
   * 
   * 8 : toggle the upper arm for rotation
   * 
   * X : use the X axis rotation at the active joint(s)
   * 
   * Y : use the Y axis rotation at the active joint(s)
   * 
   * Z : use the Z axis rotation at the active joint(s)
   * 
   * C : resets the hand to the stop sign
   * 
   * P : select joint that connects leg to thorax
   * 
   * M : select middle joint
   * 
   * D : select last joint
   * 
   * R : resets the view to the initial rotation
   * 
   * K : prints the angles of the six legs for debugging purposes
   * 
   * Q, Esc : exits the program
   * 
   */
  public void keyTyped(final KeyEvent key) {
    switch (key.getKeyChar()) {
    case 'Q':
    case 'q':
    case KeyEvent.VK_ESCAPE:
      new Thread() {
        @Override
        public void run() {
          PA2.this.animator.stop();
        }
      }.start();
      System.exit(0);
      break;

    // print the angles of the components
    case 'K':
    case 'k':
      printJoints();
      break;

    // resets to the stop sign
    case 'C':
    case 'c':
      this.setModelState(this.testCases.stop());
      break;

    // set the state of the hand to the next test case
    case 'T':
    case 't':
      this.setModelState(this.testCases.next());
      break;

    // set the viewing quaternion to 0 rotation
    case 'R':
    case 'r':
      this.viewing_quaternion.reset();
      break;

    // Toggle which finger(s) are affected by the current rotation
    case '1':
      toggleSelection(this.legs[0]); //left front
      break;
    case '2':
      toggleSelection(this.legs[1]); //left mid
      break;
    case '3':
      toggleSelection(this.legs[2]); //left rear
      break;
    case '4':
      toggleSelection(this.legs[3]); //right front
      break;
    case '5':
      toggleSelection(this.legs[4]); //right mid
      break;
    case '6':
        toggleSelection(this.legs[5]); //right rear
        break;

    // toggle which joints are affected by the current rotation
    case 'D':
    case 'd':
      for (final Leg legSeg : this.selectedLegs) {
        toggleSelection(legSeg.tibiaJoint());
      }
      break;
    case 'M':
    case 'm':
      for (final Leg legSeg : this.selectedLegs) {
        toggleSelection(legSeg.femurJoint());
      }
      break;
    case 'P':
    case 'p':
      for (final Leg legSeg : this.selectedLegs) {
        toggleSelection(legSeg.coxaJoint());
      }
      break;
    
    case '7':
      toggleSelection(this.head);
      break;
    case '8':
      toggleSelection(this.abdomen);
      break;

    // change the axis of rotation at current active joint
    case 'X':
    case 'x':
      this.selectedAxis = Axis.X;
      break;
    case 'Y':
    case 'y':
      this.selectedAxis = Axis.Y;
      break;
    case 'Z':
    case 'z':
      this.selectedAxis = Axis.Z;
      break;
    default:
      break;
    }
  }

  /**
   * Prints the joints on the System.out print stream.
   */
  private void printJoints() {
    this.printJoints(System.out);
  }

  /**
   * Prints the joints on the specified PrintStream.
   * 
   * @param printStream
   *          The stream on which to print each of the components.
   */
  private void printJoints(final PrintStream printStream) {
    for (final Component component : this.components) {
      printStream.println(component);
    }
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseClicked(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * Updates the rotation quaternion as the mouse is dragged.
   * 
   * @param mouse
   *          The mouse drag event object.
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
		final double magnitude = Math.sqrt(dx * dx + dy * dy);
		final float[] axis = magnitude == 0 ? new float[]{1,0,0}: // avoid dividing by 0
			new float[] { (float) (dy / magnitude),(float) (dx / magnitude), 0 };
	
		// calculate appropriate quaternion
		final float viewing_delta = 3.1415927f / 180.0f;
		final float s = (float) Math.sin(0.5f * viewing_delta);
		final float c = (float) Math.cos(0.5f * viewing_delta);
		final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
				* axis[2]);
		this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
	
		// normalize to counteract acccumulating round-off error
		this.viewing_quaternion.normalize();
	
		// save x, y as last x, y
		this.last_x = x;
		this.last_y = y;
	}
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseEntered(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseExited(MouseEvent mouse) {
    // intentionally unimplemented
  }

  /**
   * This method is intentionally unimplemented.
   * 
   * @param mouse
   *          This parameter is ignored.
   */
  public void mouseMoved(MouseEvent mouse) {
	  //optional for extra credit
	  //final int x = mouse.getX();
	  //final int y = mouse.getY();
  }

  /**
   * Starts rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse press event object.
   */
  public void mousePressed(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.last_x = mouse.getX();
      this.last_y = mouse.getY();
      this.rotate_world = true;
    }
  }

  /**
   * Stops rotating the world if the left mouse button was released.
   * 
   * @param mouse
   *          The mouse release event object.
   */
  public void mouseReleased(final MouseEvent mouse) {
    if (mouse.getButton() == MouseEvent.BUTTON1) {
      this.rotate_world = false;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @param drawable
   *          {@inheritDoc}
   * @param x
   *          {@inheritDoc}
   * @param y
   *          {@inheritDoc}
   * @param width
   *          {@inheritDoc}
   * @param height
   *          {@inheritDoc}
   */
  public void reshape(final GLAutoDrawable drawable, final int x, final int y,
      final int width, final int height) {
    final GL2 gl = (GL2)drawable.getGL();

    // prevent division by zero by ensuring window has height 1 at least
    final int newHeight = Math.max(1, height);

    // compute the aspect ratio
    final double ratio = (double) width / newHeight;

    // reset the projection coordinate system before modifying it
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // set the viewport to be the entire window
    gl.glViewport(0, 0, width, newHeight);

    // set the clipping volume
    this.glu.gluPerspective(25, ratio, 0.1, 100);

    // camera positioned at (0,0,6), look at point (0,0,0), up vector (0,1,0)
    this.glu.gluLookAt(0, 0, 12, 0, 0, 0, 0, 1, 0);

    // switch back to model coordinate system
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

  private void toggleSelection(final Component component) {
    if (this.selectedComponents.contains(component)) {
      this.selectedComponents.remove(component);
      component.setColor(INACTIVE_COLOR);
    } else {
      this.selectedComponents.add(component);
      component.setColor(ACTIVE_COLOR);
    }
    this.stateChanged = true;
  }

  private void toggleSelection(final Leg finger) {
    if (this.selectedLegs.contains(finger)) {
      this.selectedLegs.remove(finger);
      this.selectedComponents.removeAll(finger.joints());
      for (final Component joint : finger.joints()) {
        joint.setColor(INACTIVE_COLOR);
      }
    } else {
      this.selectedLegs.add(finger);
    }
    this.stateChanged = true;
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
	// TODO Auto-generated method stub
	
  }
}
