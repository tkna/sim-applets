import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import lib.*;

/*
<applet code="Geostrophic" width=720 height=560></Applet>
*/

public class Geostrophic extends Applet implements Runnable, ActionListener, 
                                               ItemListener, AdjustmentListener {

  static final double PI = Math.acos(-1.0);
  double OMEGA = 2.0*PI / (3600.0 * 24.0);
  double THETA = PI / 6.0;
  double F = 2.0 * OMEGA * Math.sin(THETA);
  //double F = 6.67e-5;
  double Gx = 0.0;
  double Gy = 1.0e-3;
  double GAMMA = 0.000008;
  double dt = 500.0;
  
  class GeoMolecule extends Molecule {
    public GeoMolecule() {
      super();
    }
  
    public void draw(Graphics g, Field f) {
      g.setColor(Color.blue);
      if (marker == 1) g.setColor(Color.red);
      if (axisMode == AXISMODE_XY) {
        g.fillOval( f.transX(r[0]) - 5, f.transY(r[1]) -5, 10, 10);
      } else if (axisMode == AXISMODE_XZ) {
        g.fillOval( f.transX(r[0]) - 5, f.transY(r[2]) - 5, 10, 10);
      }
    }
  }
  
  class Molecules {
    int Nm;
    GeoMolecule[] molecule = new GeoMolecule[100];
    Forces[] force = new Forces[100];
    
    public Molecules () {
      Nm = 1;
      
      molecule[0] = new GeoMolecule();
      molecule[0].r[0] = 0.0;
      molecule[0].r[1] = 0.0;
      molecule[0].r[2] = 0.0;
      molecule[0].v[1] = 50.0;
      molecule[0].v[0] = 0.0;
      double[] g = new double[3];
      g[0] = Gx; g[1] = Gy; g[2] = 0.0;
      force[0] = new Forces(3);
      force[0].setForce(0, g);
      //for (int i = 0; i < 3; i++) {
      //  force[0].setVisible(i, false);
      //}
    }
    
    public void draw(Graphics g, Field f) {
      for (int i = 0; i < Nm; i++) {
        molecule[i].draw(g, f);
        force[i].draw(g, f);
      }
    }
    
    public void nextStep() {
      // 各moleculeに対して
      double [] v = new double [3];
      double [] r = new double [3];
      double uu, vv;
      double[] k1 = new double[4];
      double[] k2 = new double[4];
      double[] k3 = new double[4];
      double[] k4 = new double[4];
      double [] g = new double[3];
      double [] coriolis = new double[3];
      double [] masatu = new double [3];
      
      for (int i = 0; i < Nm; i++ ) {
        v = null; r = null;
        v = molecule[i].v;
        r = molecule[i].r;
        uu = v[0]; vv = v[1];
    
        k1[0] = dt * f1(uu, vv);
        k1[1] = dt * f2(uu, vv);
        k1[2] = dt * f3(uu, vv);
        k1[3] = dt * f4(uu, vv);
        
        k2[0] = dt * f1(uu + k1[2] / 2.0, vv + k1[3] / 2.0);
        k2[1] = dt * f2(uu + k1[2] / 2.0, vv + k1[3] / 2.0);
        k2[2] = dt * f3(uu + k1[2] / 2.0, vv + k1[3] / 2.0);
        k2[3] = dt * f4(uu + k1[2] / 2.0, vv + k1[3] / 2.0);

        k3[0] = dt * f1(uu + k2[2] / 2.0, vv + k2[3] / 2.0);
        k3[1] = dt * f2(uu + k2[2] / 2.0, vv + k2[3] / 2.0);
        k3[2] = dt * f3(uu + k2[2] / 2.0, vv + k2[3] / 2.0);
        k3[3] = dt * f4(uu + k2[2] / 2.0, vv + k2[3] / 2.0);
        
        k4[0] = dt * f1(uu + k3[2], vv + k3[3]);
        k4[1] = dt * f2(uu + k3[2], vv + k3[3]);
        k4[2] = dt * f3(uu + k3[2], vv + k3[3]);
        k4[3] = dt * f4(uu + k3[2], vv + k3[3]);
        
        r[0] = r[0] + (k1[0] + 2.0*k2[0] + 2.0*k3[0] + k4[0]) / 6.0;
        r[1] = r[1] + (k1[1] + 2.0*k2[1] + 2.0*k3[1] + k4[1]) / 6.0;
        v[0] = v[0] + (k1[2] + 2.0*k2[2] + 2.0*k3[2] + k4[2]) / 6.0;
        v[1] = v[1] + (k1[3] + 2.0*k2[3] + 2.0*k3[3] + k4[3]) / 6.0;
        
        //v[0] = v[0] + dt * (F * vv + Gx);
        //v[1] = v[1] + dt * (- F * uu + Gy);
        //molecule[i].r = vadd(molecule[i].r, vscale(molecule[i].v, dt));
        g[0] = Gx; g[1] = Gy; g[2] = 0.0;
        coriolis[0] = F * v[1]; coriolis[1] = - F * v[0]; coriolis[2] = 0.0;
        masatu[0] = - GAMMA * v[0]; masatu[1] = - GAMMA * v[1]; masatu[2] = 0.0;
        force[i].setR(r);
        force[i].setForce(0, g);
        force[i].setForce(1, coriolis);
        force[i].setForce(2, masatu);
      }
    }
    
    public double f1(double u, double v) {
      return u;
    }
    public double f2(double u, double v) {
      return v;
    }
    public double f3(double u, double v) {
      return Gx + F * v - GAMMA * u;
    }
    public double f4(double u, double v) {
      return Gy - F * u - GAMMA * v;
    }
    
    public void newMolecule() {
      newMolecule(0.0, 50.0);
    }
    
    public void newMolecule(double u0, double v0) {
      molecule[Nm] = new GeoMolecule();
      molecule[Nm].r[0] = 0.0;
      molecule[Nm].r[1] = 0.0;
      molecule[Nm].r[2] = 0.0;
      molecule[Nm].v[0] = u0;
      molecule[Nm].v[1] = v0;
      force[Nm] = new Forces(3);
      force[Nm].setR(molecule[Nm].r);
      
      Nm++;
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
	
	Field f;
	Molecules m;
	Image img, fimg;
	Button bn;
	TextField tfu0, tfv0, tfdp, tffr, tfom, tfphi;
	Label lbu0, lbv0, lbu0u, lbv0u, lbdp, lbdpu, lbfr, lbom, lbomu, lbphi, lbphiu;
	Scrollbar sbu0, sbv0, sbdp, sbfr, sbom, sbphi;
	Label lbt;
	
	public void init() {
	  setLayout(null);
	  img = createImage(getWidth(), getHeight());
	  fimg = createImage(getWidth(), getHeight());
	  f = new Field(0, 0, 720, 480);
	  f.setArea(-15.0e5, 75.0e5, -30.0e5, 30.0e5);
	  f.setXtic(15.0e5);
	  f.setYtic(15.0e5);
	  
	  bn = new Button("New");
	  bn.setBounds(10, 490, 50, 20);
	  bn.addActionListener(this);
	  add(bn);
	  
	  lbu0 = new Label("u0 = ");
	  lbu0.setBounds(70, 480, 40, 20);
	  add(lbu0);
	  
	  lbv0 = new Label("v0 = ");
	  lbv0.setBounds(70, 500, 40, 20);
	  add(lbv0);
	  
	  lbt = new Label("t = 0 [h]");
	  lbt.setBounds(50, 530, 100, 20);
	  add(lbt);
	  
	  tfu0 = new TextField("0.0");
	  tfu0.setBounds(110, 480, 50, 20);
	  add(tfu0);
	  
	  tfv0 = new TextField("50.0");
	  tfv0.setBounds(110, 500, 50, 20);
	  add(tfv0);
	  
	  sbu0 = new Scrollbar(Scrollbar.VERTICAL, 0, 1, -100, 100);
	  sbu0.setBounds(160, 480, 10, 20);
	  sbu0.addAdjustmentListener(this);
	  add(sbu0);
	  
	  sbv0 = new Scrollbar(Scrollbar.VERTICAL, -50, 1, -100, 100);
	  sbv0.setBounds(160, 500, 10, 20);
	  sbv0.addAdjustmentListener(this);
	  add(sbv0);
	  
	  lbu0u = new Label("m/s");
	  lbu0u.setBounds(170, 480, 30, 20);
	  add(lbu0u);
	  
	  lbv0u = new Label("m/s");
	  lbv0u.setBounds(170, 500, 30, 20);
	  add(lbv0u);
	  
	  lbdp = new Label("気圧差：");
	  lbdp.setBounds(210, 480, 50, 20);
	  add(lbdp);
	  
	  tfdp = new TextField("1.0");
	  tfdp.setBounds(260, 480, 40, 20);
	  add(tfdp);
	  
	  lbdpu = new Label("hPa/100km");
	  lbdpu.setBounds(300, 480, 70, 20);
	  add(lbdpu);
	  
	  sbdp = new Scrollbar(Scrollbar.HORIZONTAL, 100, 0, 0, 500);
	  sbdp.setBounds(210, 500, 160, 10);
	  sbdp.addAdjustmentListener(this);
	  add(sbdp);
	  
	  lbfr = new Label("摩擦：");
	  lbfr.setBounds(210, 520, 50, 20);
	  add(lbfr);
	  
	  tffr = new TextField("0.000008");
	  tffr.setBounds(260, 520, 60, 20);
	  add(tffr);
	  
	  sbfr = new Scrollbar(Scrollbar.HORIZONTAL, 8, 0, 0, 20);
	  sbfr.setBounds(210, 540, 160, 10);
	  sbfr.addAdjustmentListener(this);
	  add(sbfr);
	  
	  lbom = new Label("自転速度：");
	  lbom.setBounds(400, 480, 60, 20);
	  add(lbom);
	  
	  tfom = new TextField("1.0");
	  tfom.setBounds(460, 480, 40, 20);
	  add(tfom);
	  
	  lbomu = new Label("回転/日");
	  lbomu.setBounds(500, 480, 70, 20);
	  add(lbomu);
	  
	  sbom = new Scrollbar(Scrollbar.HORIZONTAL, 10, 0, 0, 50);
	  sbom.setBounds(400, 500, 170, 10);
	  sbom.addAdjustmentListener(this);
	  add(sbom);
	  
	  lbphi = new Label("緯度：");
	  lbphi.setBounds(400, 520, 60, 20);
	  add(lbphi);
	  
	  tfphi = new TextField("30.0");
	  tfphi.setBounds(460, 520, 40, 20);
	  add(tfphi);
	  
	  lbphiu = new Label("°N");
	  lbphiu.setBounds(500, 520, 70, 20);
	  add(lbphiu);
	  
	  sbphi = new Scrollbar(Scrollbar.HORIZONTAL, 30, 0, -90, 90);
	  sbphi.setBounds(400, 540, 170, 10);
	  sbphi.addAdjustmentListener(this);
	  add(sbphi);
	  
	  m = new Molecules();
	  
	  
	  Thread th = new Thread(this);
	  th.start();
	}

	public void paint(Graphics g) {
	  Graphics gimg = img.getGraphics();
	  gimg.clearRect(0, 0, getWidth(), getHeight());
	  m.draw(gimg, f);
	  f.drawCoordinate(gimg);
	  g.drawImage(img, 0, 0, this);
	  gimg.dispose();
	}
	
	public void update(Graphics g) {
	  paint(g);
	}
	
	public void run() {
	  double t = 0.0;
	  try {
	    while(true) {
	      m.nextStep();
	      t += dt;
	      lbt.setText("t = " + String.valueOf((int)(t / 3600.0 * 10.0) / 10.0) + " [h]");
	      //m.print();
	      
	      repaint();
	      Thread.sleep(10);
	    }
	  } catch(Exception err) {
	  }
	  
	}
	
	public void actionPerformed(ActionEvent e) {
	  if (e.getSource() == bn) {
	    double u0, v0;
	    u0 = Double.parseDouble(tfu0.getText());
	    v0 = Double.parseDouble(tfv0.getText());
	    m.newMolecule(u0, v0);
	  }
	}
	
	public void itemStateChanged(ItemEvent e) {
	  
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
	  if (e.getSource() == sbu0) {
	    tfu0.setText(String.valueOf(-sbu0.getValue()));
	  }
	  if (e.getSource() == sbv0) {
	    tfv0.setText(String.valueOf(-sbv0.getValue()));
	  }
	  if (e.getSource() == sbdp) {
	    tfdp.setText(String.valueOf(sbdp.getValue() / 100.0));
	    Gy = sbdp.getValue() / 100.0 / 1000.0;
	  }
	  if (e.getSource() == sbfr) {
	    tffr.setText(String.valueOf(sbfr.getValue() / 1000000.0));
	    GAMMA = sbfr.getValue() / 1000000.0;
	  }
	  if (e.getSource() == sbom) {
	    tfom.setText(String.valueOf(sbom.getValue() / 10.0));
	    OMEGA = sbom.getValue() / 10.0 * 2.0 * PI / (3600.0 * 24.0);
	    F = 2.0 * OMEGA * Math.sin(THETA);
	  }
	  if (e.getSource() == sbphi) {
	    tfphi.setText(String.valueOf(sbphi.getValue()));
	    THETA = sbphi.getValue() * PI / 180.0;
	    F = 2.0 * OMEGA * Math.sin(THETA);
	  }
	}
}


