//import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import lib.*;

/*
<applet code="Gas" width=520 height=450></Applet>
*/

public class Gas extends Applet implements Runnable, AdjustmentListener, ItemListener{

  static final double R = 8.3;

  class Molecules {
    int Nm, Nw = 6;
    Molecule[] molecule;
    Wall[] wall;
    Pressure pressure;
    double dp;
    int count = 0;
    double dt = 0.00002;
    
    public Molecules(int n) {
      Nm = n;
      molecule = new Molecule[Nm];
      for (int i = 0; i < Nm; i++) {
        molecule[i] = new Molecule();
        for (int j = 0; j < 3; j++) {
          molecule[i].r[j] = Math.random();
          molecule[i].v[j] = (Math.random() - 0.5);
        }
        
        molecule[i].v = vscale(molecule[i].v, 2733.0/vnorm(molecule[i].v));
        //System.out.println(vnorm2(molecule[i].v));
      }
      
      wall = new Wall[Nw];
      for (int i = 0; i < Nw; i++) {
        wall[i] = new Wall();
      }
      wall[0].dir[0] = 1;  wall[0].r = 1.0;
      wall[1].dir[0] = -1;  wall[1].r = 0.0;
      wall[2].dir[1] = 1;  wall[2].r = 1.0;
      wall[3].dir[1] = -1;  wall[3].r = 0.0;
      wall[4].dir[2] = 1;  wall[4].r = 1.0;
      wall[5].dir[2] = -1;  wall[5].r = 0.0;
      
      pressure = new Pressure(0.13, wall[0].r);
    }
    
    public void draw(Graphics g, Field f) {
      for (int i = 0; i < Nm; i++) {
        molecule[i].draw(g, f);
      }
      for(int i = 0; i < Nw; i++) {
        wall[i].draw(g, f);
      }
      pressure.draw(g, f);
    }
    
    public void nextStep() {
      double[][] vsum = new double[3][2];
       
      for (int i = 0; i < Nm; i++) {
        molecule[i].r = vadd(molecule[i].r, vscale(molecule[i].v, dt));
        for (int j = 0; j < Nw; j++) {
          for (int k = 0; k < 3; k++) {
            if (wall[j].dir[k]*(molecule[i].r[k] - wall[j].r) > 0.0) {
              molecule[i].r[k] = wall[j].r - (molecule[i].r[k] - wall[j].r);
              
              //vsum[k] += Math.pow(molecule[i].v[k], 2);
              if (wall[j].dir[k] == 1) vsum[k][0] += molecule[i].v[k];
              else                     vsum[k][1] += molecule[i].v[k];
              molecule[i].v[k] *= -1;
            }
          }
        }
      }
      //System.out.println(vsum[0]);
      //P = molecule[0].M / 1.0 * vsum[0];
      dp += 2.0 * molecule[0].M * vsum[0][0];
      count++;
      
      if (wall[0].canMove) {
          wall[0].v += dt * (pressure.p - pressure.p2) * 1.0 / 1.0 - 0.6 * wall[0].v;
          wall[0].nextStep();
          pressure.r = wall[0].r;
      }
      
      if (count == 100) {
        pressure.p = dp / 100.0 / dt;
        //System.out.println(P);
        dp = 0.0;
        count = 0;
        
      }
    }
    
    public void lockP() {
      pressure.p2 = pressure.p;
      wall[0].canMove = true;
    }
    public void lockV() {
      wall[0].canMove = false;
    }
    
    public void setWallR(int idx, double r) {
      wall[idx].r = r;
      pressure.r = r;
    }
    
    public void setP2(double p) {
      pressure.p2 = p;
    }
    public void setT(double t) {
      double vn;
      vn = Math.sqrt(3.0 * R * t / molecule[0].M);
      for (int i = 0; i < Nm; i++) {
        molecule[i].v = vscale(molecule[i].v, vn / vnorm(molecule[i].v));
      }
    }
    public double getP() {
      return pressure.p;
    }
    public double getV() {
      return wall[0].r;
    }
    public double getT() {
      double v2sum = 0.0;
      for (int i = 0; i < Nm; i++) {
        v2sum += vnorm2(molecule[i].v);
      }
      v2sum /= Nm;
      return molecule[0].M * v2sum / (3.0 * R);
    }
    public void printP() {
      System.out.println(pressure.p);
    }
    public void print() {
      for (int i = 0; i < Nm; i++) {
        System.out.println(molecule[i].r[0] + " " + molecule[i].r[1] + " " + molecule[i].r[2]);
      }
    }
    public void print(int i) {
      System.out.println(molecule[i].r[0] + " " + molecule[i].r[1] + " " + molecule[i].r[2]);
    }
  }
  
  class Pressure {
    double p;
    double r;
    double p2;
    static final int MAXSIZE = 70;
    static final double MAXP = 1000000.0;
    
    public Pressure(double _p, double _r) {
      p2 = _p;
      p = p2;
      r = _r;
    }
    
    public void draw(Graphics g, Field f) {
      int [] xpts = new int[3];
      int [] ypts = new int[3];
      
      xpts[0] = f.transX(r) + 5;
      ypts[0] = f.transY((f.y2 - f.y1)/2.0);
      xpts[1] = xpts[0] + (int)((double)MAXSIZE * p / MAXP);
      ypts[1] = ypts[0] - (int)((double)MAXSIZE * p / MAXP);
      xpts[2] = xpts[1];
      ypts[2] = ypts[0] + (int)((double)MAXSIZE * p / MAXP);
      
      g.setColor(Color.red);
      g.fillPolygon(xpts, ypts, 3);
      g.fillRect(xpts[1], ypts[0] - (int)(0.5*(double)MAXSIZE * p / MAXP), 
            (int)((double)MAXSIZE * p / MAXP), (int)(2.0*0.5*(double)MAXSIZE * p / MAXP) );
    }
  }
 
  Field f;
  Molecules m;
  Image img;
  GraphCanvas gc, gc2;
  TextField tfVol, tfPres, tfTemp;
  Label lVol, lPres, lTemp;
  Scrollbar sbVol, sbPres, sbTemp;
  Checkbox chbPres, chbVol, chbTemp;
  CheckboxGroup chbGroup = new CheckboxGroup();
 
  public void init() {
    setLayout(null);
    img = createImage(getWidth(), getHeight());
    f = new Field(10, 10, 500, 100);
    f.setArea(0.0, 5.0, 0.0, 1.0);
    
    gc = new GraphCanvas();
    add(gc);
    gc.setBounds(10, 120, 250, 150);
    gc.init();
    gc.setAxis(0, 0.0, 50, 10.0);
    gc.ylabel("P [×10^5 N / m^2]");
    
    gc2 = new GraphCanvas();
    add(gc2);
    gc2.setBounds(10, 270, 150, 150);
    gc2.init();
    gc2.setAxis(0.0, 0.0, 5.0, 10.0);
    gc2.xlabel("V [m^3]");
    gc2.ylabel("P [×10^5 N / m^2]");
    
    lVol = new Label("V: ");
    add(lVol);
    lVol.setBounds(300, 200, 20, 20);
    
    tfVol = new TextField("1.0");
    add(tfVol);
    tfVol.setBounds(320, 200, 100, 20);
    
    sbVol = new Scrollbar(Scrollbar.HORIZONTAL, 1000, 0, 500, 5000);
    add(sbVol);
    sbVol.setBounds(300, 220, 120, 20);
    sbVol.addAdjustmentListener(this);
    
    chbVol = new Checkbox("V 固定", chbGroup, true);
    chbVol.setBounds(450, 210, 50, 20);
    chbVol.addItemListener(this);
    add(chbVol);
    
    lPres = new Label("P: ");
    add(lPres);
    lPres.setBounds(300, 150, 20, 20);
    
    tfPres = new TextField();
    add(tfPres);
    tfPres.setBounds(320, 150, 100, 20);
    
    sbPres = new Scrollbar(Scrollbar.HORIZONTAL, 1000, 0, 90000, 900000);
    add(sbPres);
    sbPres.setBounds(300, 170, 120, 20);
    sbPres.addAdjustmentListener(this);
    
    chbPres = new Checkbox("P 固定", chbGroup, false);
    chbPres.setBounds(450, 160, 50, 20);
    chbPres.addItemListener(this);
    add(chbPres);
    
    lTemp = new Label("T: ");
    add(lTemp);
    lTemp.setBounds(300, 250, 20, 20);
    
    tfTemp = new TextField("300");
    add(tfTemp);
    tfTemp.setBounds(320, 250, 100, 20);
    
    sbTemp = new Scrollbar(Scrollbar.HORIZONTAL, 300, 0, 200, 600);
    add(sbTemp);
    sbTemp.setBounds(300, 270, 120, 20);
    sbTemp.addAdjustmentListener(this);
    
    chbTemp = new Checkbox("T 固定", chbGroup, false);
    chbTemp.setBounds(450, 260, 50, 20);
    chbTemp.addItemListener(this);
    add(chbTemp);
    
    m = new Molecules(200);
    //m.print(0);
    
    Thread th = new Thread(this);
    th.start();
  }
 
  public void paint(Graphics g) {
    Graphics gimg = img.getGraphics();
    gimg.clearRect(0, 0, getWidth(), getHeight());
    m.draw(gimg, f);
    g.drawImage(img, 0, 0, this);
    gimg.dispose();
  }
 
  public void update(Graphics g) {
    paint(g);
  }
  
  public void run() {
    int count = 0;
    double[] ps = new double[51];
    double[] pss = new double[51];
    double [] p = new double[50];
    double [] v = new double[50];
    double t;
    
    for (int i = 0; i < 50; i++) {
      v[i] = ((double)(i+1)) /10.0;
      //p[i] = m.Nm * 1.0 / (v[i] * 1200.0);
      //p[i] = m.Nm * R * 300.0 / v[i] / 100000.0;
    }
    
    try {
      while(true) {
        m.nextStep();
        count++;
        t = m.getT();
        if (count == 100) {
          
          for (int i = 0; i <= 49; i++) {
            ps[i] = ps[i+1];
            pss[i] = pss[i+1];
          }
          ps[50] = m.getP() / 100000.0;
          pss[50] = m.Nm * R * t / m.getV() / 100000.0;
          gc.clear();
          gc.plot(ps, Color.red);
          gc.plot(pss, Color.blue);
          gc.repaint();
          count = 0;
          
          if (chbPres.getState() != true) {
            tfPres.setText(String.valueOf(ps[50]*100000.0));
            sbPres.setValue((int)(ps[50]*100000.0));
          }
          if (chbVol.getState() != true) {
            tfVol.setText(String.valueOf(m.getV()));
            sbVol.setValue((int)(m.getV()*1000.0));
          }
       
          tfTemp.setText(String.valueOf(t));
          sbTemp.setValue((int)t);
        }
        
        
        // P-V グラフ
        for (int i = 0; i < 50; i++) {
          p[i] = m.Nm * R * t / v[i] / 100000.0;
        }
        gc2.clear();
        gc2.plot(v, p);
        gc2.plot(m.getV(), m.getP() / 100000.0, Color.red);
        gc2.repaint();
        repaint();
        Thread.sleep(10);
        
      }
    } catch(Exception err) {
    }
  }
  
  public void adjustmentValueChanged(AdjustmentEvent e) {
    if (e.getSource() == sbVol) {
      double a = sbVol.getValue() / 1000.0;
      tfVol.setText(String.valueOf(a));
      m.setWallR(0, a);
    } else if (e.getSource() == sbPres) {
      if (chbVol.getState() != true) {
        double a = sbPres.getValue();
        tfPres.setText(String.valueOf(a));
        m.setP2(a);
      }
    } else if (e.getSource() == sbTemp) {
      double a = sbTemp.getValue();
      tfTemp.setText(String.valueOf(a));
      m.setT(a);
    }
  }
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == chbPres) {
      m.lockP();
    } else if(e.getSource() == chbVol) {
      m.lockV();
    }
  }
  
  public double vnorm2(double[] a) {
		return a[0]*a[0] + a[1]*a[1] + a[2]*a[2];
	}
	public double vnorm(double[] a) {
	  return Math.sqrt( vnorm2( a ) );
	}
	public double[] vadd(double[] a, double[] b) {
		double[] c = new double[3];
		for (int i = 0; i < 3; i++) {
			c[i] = a[i] + b[i];
		}
		return c;
	}
	public double[] vsub(double[] a, double[] b) {
		double[] c = new double[3];
		for (int i = 0; i < 3; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}
	public double[] vscale(double[] a, double s) {
		double[] c = new double[3];
		for (int i = 0; i < 3; i++) {
			c[i] = a[i] * s;
		}
		return c;
	}
	public double[] vset(double x, double y, double z) {
		double[] c = new double[3];
		c[0] = x; c[1] = y; c[2] = z;
		return c;
	}
	public double[] vmul(double[] a, double[] b) {
	  double[] c = new double[3];
	  for (int i = 0; i < 3; i++) {
	    c[i] = a[i] * b[i];
	  }
	  return c;
	}
}


