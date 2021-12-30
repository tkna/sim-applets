package lib;
import java.awt.*;

public class Forces {

  static final double PI = Math.acos(-1.0);
  int Fn;
  Force [] force;
  Color [] fcolor = {Color.red, Color.green, Color.yellow};
  double scale = 1.0e5;
  double [] r = new double [3];
  
  public Forces(int n) {
    Fn = n;
    force = new Force[Fn];
    for (int i = 0; i < Fn; i++) {
      force[i] = new Force(fcolor[i]);
    }
  }
  
  public void setForce(int n, double[] _f) {
    force[n].f[0] = _f[0];
    force[n].f[1] = _f[1];
    force[n].f[2] = _f[2];
  }
  
  public void setR(double [] _r) {
    r[0] = _r[0];
    r[1] = _r[1];
    r[2] = _r[2];
  }
  
  public void draw(Graphics g, Field f) {
    for (int i = 0; i < Fn; i++) {
      if (force[i].visible) force[i].draw(g, f);
    }
  }
  
  public void setVisible(int n, boolean b) {
    force[n].visible = b;
  }
  
  public void setScale(double s) {
    scale = s;
  }
  
  class Force {
	  //double [] r = new double[3];
	  double [] f = new double[3];
	  Color color;
	  boolean visible = true;
	  final double arrow = 5.0;
	  final double theta = PI/4.0;
	  final double cos = Math.cos(theta);
	  final double sin = Math.sin(theta);
	  
	  public Force(Color c) {
	    color = c;
	  }
	  
	  public void draw(Graphics g, Field fi) {
	    int [] xpts = new int[4];
	    int [] ypts = new int[4];
	    double fl;
	    double cosphi, sinphi;
	    double dx1, dy1, dx2, dy2;
	    
	    fl = vnorm(f); 
	    cosphi = f[0]/fl; sinphi = f[1]/fl;
	    xpts[0] = fi.transX(r[0]);
	    ypts[0] = fi.transY(r[1]);
	    xpts[1] = xpts[0] + (int)(scale * f[0]);
	    ypts[1] = ypts[0] - (int)(scale * f[1]);
	    
	    dx1 = -arrow * (cosphi * cos + sinphi * sin);
	    dy1 = -arrow * (sinphi * cos - cosphi * sin);
	    dx2 = -arrow * (cosphi * cos - sinphi * sin);
	    dy2 = -arrow * (sinphi * cos + cosphi * sin);
	    
	    xpts[2] = xpts[1] + (int)dx1;
	    ypts[2] = ypts[1] - (int)dy1;
	    xpts[3] = xpts[1] + (int)dx2;
	    ypts[3] = ypts[1] - (int)dy2;
	    
	    g.setColor(color);
	    g.drawLine(xpts[0], ypts[0], xpts[1], ypts[1]);
	    g.drawLine(xpts[1], ypts[1], xpts[2], ypts[2]);
	    g.drawLine(xpts[1], ypts[1], xpts[3], ypts[3]);
	  }
	  
	  public double vnorm2(double[] a) {
		  return a[0]*a[0] + a[1]*a[1] + a[2]*a[2];
	  }
	  public double vnorm(double[] a) {
	    return Math.sqrt( vnorm2( a ) );
	  }
  }
}