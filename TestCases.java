/**
 * 
 */


import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeffrey Finkelstein <jeffrey.finkelstein@gmail.com>
 * @since Spring 2011
 */
public class TestCases extends CyclicIterator<Map<String, Angled>> {

  Map<String, Angled> stop() {
    return this.stop;
  }

  private final Map<String, Angled> stop;

  @SuppressWarnings("unchecked")
  TestCases() {
    this.stop = new HashMap<String, Angled>();
    final Map<String, Angled> walk1 = new HashMap<String, Angled>();
    final Map<String, Angled> walk2 = new HashMap<String, Angled>();
    final Map<String, Angled> walk3 = new HashMap<String, Angled>();
    final Map<String, Angled> walk4 = new HashMap<String, Angled>();
    final Map<String, Angled> walk5 = new HashMap<String, Angled>();

    super.add(stop, walk1, walk2, walk3, walk4, walk5);

    // the upper arm, forearm, and hand angles do not change through any of the
    // test cases
    stop.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    walk1.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    walk2.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    walk3.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    walk4.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    walk5.put(PA2.THORAX_NAME, new BaseAngled(0, 0, 0));
    
    stop.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -45));
    walk1.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -45));
    walk2.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -60));
    walk3.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -45));
    walk4.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -30));
    walk5.put(PA2.HEAD_NAME, new BaseAngled(0, 0, -30));
    
    stop.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 20));
    walk1.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 20));
    walk2.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 30));
    walk3.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 20));
    walk4.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 10));
    walk5.put(PA2.ABDOMEN_NAME, new BaseAngled(0, 0, 10));

    // the stop test case
    stop.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(0, -10, 0));
    stop.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(0, 0, 0));
    stop.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(0, 10, 0));
    stop.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(0, 190, 0));
    stop.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(0, 180, 0));
    stop.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    stop.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    stop.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(0, 170, 0));

    /////////////////////////////////////////////////////////////////
    
    
    
    
    // left front leg
    walk1.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk1.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk1.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(0, -10, 0));
    walk2.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-70, 0, 0));
    walk2.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(20, 0, 0));
    walk2.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(0, 5, 0));
    walk3.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk3.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk3.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(5, 5, 0));
    walk4.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-110, 0, 0));
    walk4.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(70, 0, 0));
    walk4.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(5, -25, 0));
    walk5.put(PA2.LEFT_FRONT_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk5.put(PA2.LEFT_FRONT_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk5.put(PA2.LEFT_FRONT_COXA_NAME, new BaseAngled(0, -25, 0));
    
    // left rear leg
    walk1.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk1.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk1.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(0, 10, 0));
    walk2.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-70, 0, 0));
    walk2.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(20, 0, 0));
    walk2.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(0, 25, 0));
    walk3.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk3.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk3.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(5, 25, 0));
    walk4.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-110, 0, 0));
    walk4.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(70, 0, 0));
    walk4.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(5, 5, 0));
    walk5.put(PA2.LEFT_REAR_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk5.put(PA2.LEFT_REAR_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk5.put(PA2.LEFT_REAR_COXA_NAME, new BaseAngled(0, 5, 0));
    
    
    // left middle leg
    walk1.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-100, 0, 0));
    walk1.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(60, 0, 0));
    walk1.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(5, 0, 0));
    walk2.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk2.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk2.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(5, -10, 0));
    walk3.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk3.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk3.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(0, -10, 0));
    walk4.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk4.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk4.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(0, 10, 0));
    walk5.put(PA2.LEFT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk5.put(PA2.LEFT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk5.put(PA2.LEFT_MIDDLE_COXA_NAME, new BaseAngled(0, 10, 0));
    
    
    
    //right front leg
    walk1.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-110, 0, 0));
    walk1.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(70, 0, 0));
    walk1.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(-5, 190, 0));
    walk2.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk2.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk2.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(-5, 205, 0));
    walk3.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk3.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk3.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(0, 205, 0));
    walk4.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-70, 0, 0));
    walk4.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(20, 0, 0));
    walk4.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(0, 185, 0));
    walk5.put(PA2.RIGHT_FRONT_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk5.put(PA2.RIGHT_FRONT_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk5.put(PA2.RIGHT_FRONT_COXA_NAME, new BaseAngled(0, 185, 0));
    
    
    //right rear leg
    walk1.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-110, 0, 0));
    walk1.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(70, 0, 0));
    walk1.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(-5, 170, 0));
    walk2.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk2.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk2.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(-5, 185, 0));
    walk3.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk3.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk3.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(0, 185, 0));
    walk4.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-70, 0, 0));
    walk4.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(20, 0, 0));
    walk4.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(0, 155, 0));
    walk5.put(PA2.RIGHT_REAR_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk5.put(PA2.RIGHT_REAR_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk5.put(PA2.RIGHT_REAR_COXA_NAME, new BaseAngled(0, 155, 0));
    
    
    //right middle leg
    walk1.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk1.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk1.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(0, 180, 0));
    walk2.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-70, 0, 0));
    walk2.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(20, 0, 0));
    walk2.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(0, 170, 0));
    walk3.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-90, 0, 0));
    walk3.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(45, 0, 0));
    walk3.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(-5, 170, 0));
    walk4.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-100, 0, 0));
    walk4.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(60, 0, 0));
    walk4.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(-5, 190, 0));
    walk5.put(PA2.RIGHT_MIDDLE_TIBIA_NAME, new BaseAngled(-80, 0, 0));
    walk5.put(PA2.RIGHT_MIDDLE_FEMUR_NAME, new BaseAngled(30, 0, 0));
    walk5.put(PA2.RIGHT_MIDDLE_COXA_NAME, new BaseAngled(0, 190, 0));
    
    // walk 1 = lift two right, one left
    // walk 2 = bring lifted legs (two right, one left) forward,
    // move other legs back
    // walk 3 = lower legs and lift two left, one right
    // walk 4 = move lower legs back, lifted legs forward
    // walk 5 = lower lifted legs
    
    
    
  }
}
