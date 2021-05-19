/*
 * CS480 - Vivarium
 * 11/4/20
 * Spencer Vilicic
 * 
 * Collab with Angel Kim, Justin Taylor
 */

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.util.*;

public class Vivarium
{
  private Tank tank;
  public Fish fish;
  public Fish fish2; 
  public Fish fish3; 
  public Fish fish4; 
  public Fish fish5;
  public Shark shark;
  private ArrayList<Fish> fishes = new ArrayList<Fish>();
  
  private static final float SHARK_RADIUS = 0.25f;
  private static final float FISH_RADIUS = 0.07f;

  public Vivarium()
  {
    tank = new Tank( 4.0f, 4.0f, 4.0f );
    fish = new Fish(0.05f, 0.0f, -1.5f, 0.0f);
    fish2 = new Fish(0.05f, 0.5f, -1.0f, 0.0f);
    fish3 = new Fish(0.05f, 0.5f, -1.3f, 0.0f);
    fish4 = new Fish(0.05f, 0.2f, -1.0f, 0.0f);
    fish5 = new Fish(0.05f, -0.5f, -1.0f, 0.0f);
    shark = new Shark(0.2f, 1.0f, 1.0f, 0.0f);
    
    fishes.add(fish);
    fishes.add(fish2);
    fishes.add(fish3);
    fishes.add(fish4);
    fishes.add(fish5);
  }

  public void init( GL2 gl )
  {
    tank.init( gl );
    shark.init(gl);
    fish.init(gl);
    fish2.init(gl);
    fish3.init(gl);
    fish4.init(gl);
    fish5.init(gl);
    
    for (Fish f : fishes)
    	f.setPredator(shark);
    
    fish.setFriendA(fish2);
    fish.setFriendB(fish3);
    fish.setFriendC(fish4);
    fish.setFriendD(fish5);
    
    fish2.setFriendA(fish);
    fish2.setFriendB(fish3);
    fish2.setFriendC(fish4);
    fish2.setFriendD(fish5);
    
    fish3.setFriendA(fish);
    fish3.setFriendB(fish2);
    fish3.setFriendC(fish4);
    fish3.setFriendD(fish5);
    
    fish4.setFriendA(fish);
    fish4.setFriendB(fish2);
    fish4.setFriendC(fish3);
    fish4.setFriendD(fish5);
    
    fish5.setFriendA(fish);
    fish5.setFriendB(fish2);
    fish5.setFriendC(fish3);
    fish5.setFriendD(fish4);
    
    shark.setPrey(fish);
    shark.setPrey2(fish2);
    shark.setPrey3(fish3);
    shark.setPrey4(fish4);
    shark.setPrey5(fish5);
  }
  
  public boolean collision_detection(Fish f, Shark shark) {
	  Coord fc = new Coord(f.x, f.y, f.z);
	  Coord sc = new Coord(shark.x, shark.y, shark.z);
	  
	  float distance = distance(fc,sc);
	  
	  if (distance < (SHARK_RADIUS + FISH_RADIUS)) {
		  f.isDead = true;
		  return true;
	  }
	  else
		  return false;
  }
  
  private float distance(Coord c1, Coord c2) {
		return (float) Math.sqrt(Math.pow(c1.x-c2.x, 2) + Math.pow(c1.y-c2.y, 2) + Math.pow(c1.z-c2.z, 2));
	}

  public void update( GL2 gl )
  {
    tank.update( gl );
    fish.update(gl);
    fish2.update(gl);
    fish3.update(gl);
    fish4.update(gl);
    fish5.update(gl);
    shark.update(gl);
    	
    //check for collisions
    collision_detection(fish, shark);
    collision_detection(fish2, shark);
    collision_detection(fish3, shark);
    collision_detection(fish4, shark);
    collision_detection(fish5, shark);
  }

  public void draw( GL2 gl )
  {
    tank.draw( gl );
    shark.draw(gl);

    if (!fish.isDead)
    	fish.draw(gl);
    else {
    	shark.deletePrey();
    	fish2.deleteFriendA();
    	fish3.deleteFriendA();
    	fish4.deleteFriendA();
    	fish5.deleteFriendA();
    }
    
    if (!fish2.isDead)
    	fish2.draw(gl);
    else {
    	shark.deletePrey2();
    	fish.deleteFriendA();
    	fish3.deleteFriendB();
    	fish4.deleteFriendB();
    	fish5.deleteFriendB();
    }
    
    if (!fish3.isDead)
    	fish3.draw(gl);
    else {
    	shark.deletePrey3();
    	fish.deleteFriendB();
    	fish2.deleteFriendB();
    	fish4.deleteFriendC();
    	fish5.deleteFriendC();
    }
    if (!fish4.isDead)
    	fish4.draw(gl);
    else {
    	shark.deletePrey4();
    	fish.deleteFriendC();
    	fish2.deleteFriendC();
    	fish3.deleteFriendC();
    	fish5.deleteFriendD();
    }
    if (!fish5.isDead)
    	fish5.draw(gl);
    else {
    	shark.deletePrey5();
    	fish.deleteFriendD();
    	fish2.deleteFriendD();
    	fish3.deleteFriendD();
    	fish4.deleteFriendD();
    }
  }

  
//  public static void respawnFish() {
//	  if (fish.isDead()) {
//		  fish = new Fish(0.05f, 0.0f, 0.0f, 0.0f);
//		  shark.setPrey(fish);
//		  fish.isDead = false;
//	  }
//	  if (fish2.isDead()) {
//		  fish2 = new Fish(0.05f, 0.5f, -1.0f, 0.0f);
//		  shark.setPrey2(fish);
//		  fish2.isDead = false;
//	  }
//	
//  }
}
