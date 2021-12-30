package lib;
import java.awt.*;
import java.math.*;
import java.applet.*;

public class GraphCanvas extends Canvas{
  private static final int offset = 30;
  private double gawidth, gaheight, memwidth, memheight;
  private static final int memparts = 4;
  private int xdecimal = 1, ydecimal = 3;
  private boolean isintx = false, isinty = false;
  private double x1,y1,x2,y2;
  private Image buf;
  private Image axisim;
  
  public GraphCanvas() {
  }
  
  public void paint(Graphics g) {
  	g.drawImage(buf, 0, 0, this);
  }
  
  public void update(Graphics g) {
    paint(g);
  }
  
  public void init() {
  	buf = createImage(getWidth(), getHeight() );
  	axisim = createImage(getWidth(), getHeight() );
  }
  
  public void plot(double[] x, double[] y, Color c) {
 	Graphics g = buf.getGraphics();
    
	g.setColor(c);
	for (int i = 0; i < x.length-1; i++) {
	    if (x[i] >= x1 && x[i] <= x2 && x[i+1] >= x1 && x[i+1] <= x2
	        && y[i] >= y1 && y[i] <= y2 && y[i+1] >= y1 && y[i+1] <= y2) {
		  g.drawLine(transx(x[i]),transy(y[i]),transx(x[i+1]),transy(y[i+1]));
		}
	}
	g.dispose();
  }
  
  public void plot(double[] x, double[] y) {
  	plot(x, y, Color.blue);
  }
  
  public void plot(double[] y) {
    plot(y, Color.blue);
  }
  
  public void plot(double[] y, Color c) {
    double [] x = new double[y.length];
    for (int i = 1; i < y.length; i++) {
      x[i] = (double)i;
    }
    plot(x, y, c);
  }
  
  public void plot(double x, double y, Color c) {
    Graphics g = buf.getGraphics();
    g.setColor(c);
    g.fillOval(transx(x)-2, transy(y)-2, 4, 4);
    g.dispose();
  }
  
  public void clear() {
		Graphics g = buf.getGraphics();
		g.clearRect(0,0,getWidth(),getHeight());
		g.dispose();
		//setAxis(x1, y1, x2, y2);
		drawAxis();
	}
  
  public void setAxis(double x1, double y1, double x2, double y2) {
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
		int width = getWidth(); int height = getHeight();
		
		gawidth = width - offset*2;  gaheight = height - offset*2;
		memwidth = gawidth / memparts; memheight = gaheight / memparts;
		
       	//Graphics g = buf.getGraphics();
       	Graphics g = axisim.getGraphics();
       	g.clearRect(0,0,getWidth(),getHeight());
       
		g.setColor(Color.black);
		g.drawLine(offset, height-offset, width-offset, height-offset);
		g.drawLine(offset, offset, offset, height-offset);
		
		int x_x, x_y, y_y;
		double v; String st;
		for (int i = 0; i <= memparts; i++) {
			x_x = offset+(int)(memwidth*(double)i);
			x_y = height-offset;
			y_y = height-offset-(int)(memheight*(double)i);
			g.drawLine(x_x, x_y - 5, x_x, x_y);
			v = (x2 - x1)/memparts*i + x1;
			v = new BigDecimal(v).setScale(xdecimal, BigDecimal.ROUND_HALF_UP).doubleValue();
			if (isintx) g.drawString(String.valueOf((int)v), x_x-8, x_y+12);
			else g.drawString(String.valueOf(v), x_x-8, x_y+12);
			g.drawLine(offset, y_y, offset+5, y_y);
			v = (y2 - y1)/memparts*i + y1;
			v = new BigDecimal(v).setScale(ydecimal, BigDecimal.ROUND_HALF_UP).doubleValue();
			if (isinty) g.drawString(String.valueOf((int)v), 0, y_y+5);
			else g.drawString(String.valueOf(v), 0, y_y+5);
		}
		drawAxis();
		g.dispose();
	}
    public void drawAxis() {
		Graphics g = buf.getGraphics();
		g.drawImage(axisim,0,0,this);
		g.dispose();
	}
  
  public void xlabel(String s) {
    int x, y;
    x = offset + (int)(gawidth / 2.0);
    y = getHeight();
    Graphics g = axisim.getGraphics();
    g.drawString(s, x, y);
    g.dispose();
    drawAxis();
  }
  
  public void ylabel(String s) {
    int x, y;
    x = offset/2;
    y = offset/2;
    Graphics g = axisim.getGraphics();
    g.drawString(s, x, y);
    g.dispose();
    drawAxis();
  }
  
  public void xdecimal(int i) {
    this.xdecimal = i;
  }
  
  public void ydecimal(int i) {
    this.ydecimal = i;
  }
  
  public void setXint(boolean b) {
    isintx = b;
  }
  
  public void setYint(boolean b) {
    isinty = b;
  }
  
  private int transx(double x) {
	double d;
	d = offset + (double)gawidth/(x2 - x1)*(x - x1);
	return (int)d;
  }
  private int transy(double y) {
	double d;
	d = getHeight() - offset - (double)gaheight/(y2 - y1)*(y - y1);
	return (int)d;
  }
}
