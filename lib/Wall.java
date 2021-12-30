package lib;
import java.awt.*;

public class Wall {
    private static final int AXISMODE_XY = 0;
    private static final int AXISMODE_XZ = 1;

    public double[] dir = new double[3];
    public double r;
    public double v = 0.0;
    public boolean canMove = false;
    double dt = 0.0002;
    int axisMode = AXISMODE_XY;
    
    public Wall() {
    
    }
    
    public Wall(String s, double _r, int mode) {
      if (s == "+x") dir[0] = 1.0;
      if (s == "-x") dir[0] = -1.0;
      if (s == "+y") dir[1] = 1.0;
      if (s == "-y") dir[1] = -1.0;
      if (s == "+z") dir[2] = 1.0;
      if (s == "-z") dir[2] = -1.0;
      
      r = _r;
      axisMode = mode;
    }
    
    public void nextStep() {
       r += dt * v;
    }
    
    public void draw(Graphics g, Field f) {
      g.setColor(Color.black);
      
      if (dir[0] == 1.0) {
        g.fillRect( f.transX(r), f.y, 5, f.height+5);
      }
      else if (dir[0] == -1.0) {
        g.fillRect( f.transX(r), f.y, 5, f.height+5);
      } else {
        if (axisMode == AXISMODE_XY) {
          if (dir[1] == 1.0 || dir[1] == -1.0) {
            g.fillRect( f.x, f.transY(r), f.width, 5);
          }
        } else if (axisMode == AXISMODE_XZ) {
          if (dir[2] == 1.0) {
            g.fillRect( f.x, f.transY(r), f.width+5, 5);
          }
          if (dir[2] == -1.0) {
            g.fillRect( f.x, f.transY(r)+5, f.width+5, 5);
          }
        }
      }
    }
}
  