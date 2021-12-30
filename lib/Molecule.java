package lib;
import java.awt.*;

public class Molecule {
    protected static final int AXISMODE_XY = 0;
    protected static final int AXISMODE_XZ = 1;

    public double[] r = new double[3];
    public double[] v = new double[3];
    public double M = 0.001;
    public double E;
    public boolean canMove = true;
    public int axisMode = AXISMODE_XY;
    public int marker = 0;
    
    public Molecule() {
      
    }
    
    public Molecule(double m) {
      M = m;
    }
    
    public Molecule(int mode) {
      axisMode = mode;
    }
    
    public Molecule(double m, int mode) {
      M = m;
      axisMode = mode;
    }
    
    public void setAxisMode(int mode) {
      axisMode = mode;
    }
    
    public void draw(Graphics g, Field f) {
      
      g.setColor(Color.blue);
      if (marker == 1) g.setColor(Color.red);
      if (axisMode == AXISMODE_XY) {
        g.fillOval( f.transX(r[0]) - 2, f.transY(r[1]) -2, 5, 5);
      } else if (axisMode == AXISMODE_XZ) {
        g.fillOval( f.transX(r[0]) - 2, f.transY(r[2]) - 2, 5, 5);
      }
    }
}
 

