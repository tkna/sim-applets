package lib;
import java.awt.*;

public class Field{

    int x, y, width, height;
    public double x1, x2, y1, y2;
    double xtic = 0.0, ytic = 0.0;
    
    public Field(int _x, int _y, int _width, int _height) {
      x = _x;
      y = _y;
      width = _width;
      height = _height;
    }
    
    public void setArea(double _x1, double _x2, double _y1, double _y2) {
      x1 = _x1;
      x2 = _x2;
      y1 = _y1;
      y2 = _y2;
    }
    
    public int transX(double r) {
      return (int)((r - x1) / (x2 - x1) * width) + x;
    }
    
    public int transY(double r) {
      return (int)(-(r - y1) / (y2 - y1) * height) + y + height;
    }
    
    public void setXtic(double d) {
      xtic = d;
    }
    
    public void setYtic(double d) {
      ytic = d;
    }
    
    public void drawCoordinate(Graphics g) {
      int xparts, yparts;
      int gx, gy;
      double val;
    
      if (xtic == 0.0) xtic = (x2 - x1) / 4.0;
      if (ytic == 0.0) ytic = (y2 - y1) / 4.0;
      
      xparts = (int)((x2 - x1) / xtic);
      yparts = (int)((y2 - y1) / ytic);
      
      g.setColor(Color.gray);
      for (int i = 0; i <= xparts; i++) {
        gx = (int)((double)width / (double)xparts * i);
        g.drawLine(gx, y, gx, y + height); 
        val = x1 + (double)xtic * i;
        g.drawString(String.valueOf(val), gx, y+12);
      }
      for (int i = 0; i < yparts; i++) {
        gy = y + height - (int)((double)height / (double)yparts * i);
        g.drawLine(x, gy, x + width, gy); 
        val = y1 + (double)ytic * i;
        g.drawString(String.valueOf(val), x, gy);
      }
    }
}
