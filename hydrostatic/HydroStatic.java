//import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import lib.*;

/*
<applet code="HydroStatic" width=300 height=540></Applet>
*/

public class HydroStatic extends Applet implements Runnable, ActionListener, 
                                               ItemListener, AdjustmentListener {

  double Rd = 287.0;
  double G = 9.8;
  double T = 300.0;
  double RHO = 1.2;
  //static final double G = 1.0;
  static final double PI = Math.acos(-1.0);
  static final double dt = 0.2;
  static final double dz = 50.0;
  static final double RE = 200.0;
  double MASS = 300.0;
  
  static final int T_FIXED = 0;
  static final int RHO_FIXED = 1;
  
  int FIX_MODE = T_FIXED;
  int GRAPH_MODE = 0;

  class Molecules {
    int Nm, Nw;
    Molecule[] molecule = new Molecule[100];
    Wall[] wall;
    int mode = 1;
    
    public Molecules (int n) {
      Nw = 5;
      wall = new Wall[Nw];
      wall[0] = new Wall("+x", 0.1, 1);
      wall[1] = new Wall("-x", -0.1, 1);
      wall[2] = new Wall("+y", 0.1, 1);
      wall[3] = new Wall("-y", -0.1, 1);
      wall[4] = new Wall("-z", 0.0, 1);
      
      Nm = 1;
      //Nm = n;
      //molecule = new Molecule[Nm];
      /*for (int i = 0; i < Nm; i++) {
        molecule[i] = new Molecule(1.0, 1);
        
        molecule[i].r[0] = 0.1 + (i % 10) / 11.0;
        molecule[i].r[1] = 0.5;
        molecule[i].r[2] = 0.0 + (i / 10);
        /*molecule[i].r[0] = Math.random();
        molecule[i].r[1] = 0.5;
        molecule[i].r[2] = Math.random();*/
        /*for (int j = 0; j < 3; j++) {
          molecule[i].v[j] = 0.0;
        }
      }*/
      //for (int i = 0; i < 
      molecule[0] = new Molecule(MASS, 1);
      molecule[0].r[0] = 0.0;
      molecule[0].r[1] = 0.0;
      molecule[0].r[2] = 0.0;
      molecule[0].E = 75000.0;
      molecule[0].canMove = false;
      
      /*molecule[1] = new Molecule(1.0, 1);
      molecule[1].r[0] = 0.1;
      molecule[1].r[1] = 0.5;
      molecule[1].r[2] = 3.0;*/
      molecule[0].marker = 1;
      printP();
    }
    
    public void draw(Graphics g, Field f) {
      for (int i = 0; i < Nm; i++) {
        molecule[i].draw(g, f);
      }
      for(int i = 0; i < Nw; i++) {
        wall[i].draw(g, f);
      }
      
    }
    
    public void nextStep() {
      // 各moleculeに対して
      double[] v = new double[3];
      double[] r = new double[3];
      double a, rho;
      double dist2;
      for (int i = 0; i < Nm; i++) {
        //if (!molecule[i].canMove) continue;
        v = molecule[i].v;
        r = molecule[i].r;
        rho = getRho(r);
        //v[2] = v[2] - dt * G - dt * 1.0 / getRho(r) * getGradP(r);
        a = - dt * G - dt * 1.0 / rho * getGradP(r) - 0.05 * v[2];
        if (molecule[i].canMove) {
          v[2] += a;
          //v[2] += a / 2.0;
          molecule[i].r = vadd(molecule[i].r, vscale(molecule[i].v, dt));
          //v[2] += a / 2.0;
        }
        
          //rho -= dt * getAdvMass(molecule[i].r);
          //System.out.println(" " + rho);
          //setM(i, rho);
        
        // 壁との衝突判定
        for (int j = 0; j < Nw; j++) {
          for (int k = 0; k < 3; k++) {
            if (wall[j].dir[k]*(molecule[i].r[k] - wall[j].r) > 0.0) {
              molecule[i].r[k] = wall[j].r - (molecule[i].r[k] - wall[j].r);
              
              molecule[i].v[k] *= -1.0;
            }
          }
        }
      }
      
    }
    
    public void newMolecule() {
      molecule[Nm] = new Molecule(MASS, 1);
      molecule[Nm].r[0] = 0.0;
      molecule[Nm].r[1] = 0.0;
      molecule[Nm].r[2] = 5000.0;
      molecule[Nm].E = 75000.0;
      //molecule[Nm].rho = getRho(molecule[Nm].r);
      Nm++;
    }
    
    public double getRho(double[] r) {
      if (FIX_MODE == T_FIXED) {
        double dist, msum = 0.0;
        for (int i = 0; i < Nm; i++) {
          dist = vnorm(vsub(molecule[i].r, r));
          msum += molecule[i].M * W(dist);
        }
        return msum;
      } else if (FIX_MODE == RHO_FIXED) {
        return RHO;
      }
      return 0.0;
    }
    
    public double getT(double[] r) {
      if (FIX_MODE == T_FIXED) {
        return T;
        
      } else if (FIX_MODE == RHO_FIXED) {
        double dist, esum = 0.0;
        for (int i = 0; i < Nm; i++) {
          dist = vnorm(vsub(molecule[i].r, r));
          esum += molecule[i].E * W(dist);
        }
        return esum;
      }
      return 0.0;
    }
    
    public double getGradP(double[] r) {
      double[] r2 = new double[3];
      double[] r1 = new double[3];
      double rho1, rho2, p1, p2;
      
      r2[0] = r[0];
      r2[1] = r[1];
      r2[2] = r[2] + dz;
      r1[0] = r[0];
      r1[1] = r[1];
      r1[2] = r[2] - dz;
      
      //rho2 = getRho(r2);
      //rho1 = getRho(r1);
      p2 = getP(r2);
      p1 = getP(r1);
      
      return (p2 - p1)/(2.0 * dz);
    }
    
    public double getP(double rho) {
      return rho * Rd * T;
    }
    
    public double getP(double[] r) {
      if (FIX_MODE == T_FIXED) {
        double rho;
        rho = getRho(r);
        return getP(rho);
      } else if (FIX_MODE == RHO_FIXED) {
        return RHO * Rd * getT(r);
      }
      return 0.0;
    }
    
    public void setM(int idx, double rho) {
      double w0 = W(0.0);
      double msum = 0.0;
      double dist;
      
      for (int i = 0; i < Nm; i++) {
        if (i != idx) {
          dist = vnorm(vsub(molecule[i].r, molecule[idx].r));
          msum += molecule[i].M * W(dist);
        }
      }
      molecule[idx].M = (rho - msum) / w0;
    }
    
    public double[] getV(double[] r, int idx) {
      double[] vsum = new double[3];
      double dist = 0.0;
      //for (int i = 0; i < Nm; i++) {
        int i = idx;
        dist = vnorm(vsub(molecule[i].r, r));
        vsum = vadd(vsum, vscale(molecule[i].v, W(dist) ));
      //}
      return vsum;
    }
    
    public double[] getVall(double[] r) {
      double[] vsum = new double[3];
      
      for (int i = 0; i < Nm; i++) {
        vsum = vadd(vsum, getV(r, i));
      }
      return vsum;
    }
    
    public double[] getGradVall(double[] r) {
      double[] vsum = new double[3];
      
      for (int i = 0; i < Nm; i++) {
        vsum = vadd(vsum, getGradV(r, i));
      }
      return vsum;
    }
    
    public double[] getGradV(double[] r, int idx) {
      double[] r2 = new double[3];
      double[] r1 = new double[3];
      double[] v1 = new double[3];
      double[] v2 = new double[3];
      double rho1, rho2, p1, p2;
      
      r2[0] = r[0];
      r2[1] = r[1];
      r2[2] = r[2] + dz;
      r1[0] = r[0];
      r1[1] = r[1];
      r1[2] = r[2] - dz;
      
      v2 = getV(r2, idx);
      v1 = getV(r1, idx);
      
      return vscale(vsub(v2, v1), 1.0 / (2.0*dz) );
    }
    
    public double getAdvMass(double[] r) {
      double msum = 0.0;
      double dist;
      for (int i = 0; i < Nm; i++) {
        dist = vnorm(vsub(molecule[i].r, r));
        msum += molecule[i].M * W(dist) * getGradV(r, i)[2];
      }
      return msum;
    }
    
    public double W(double r) {
      if (mode == 1) {
        return 1.0 / (Math.sqrt(PI)*RE) * Math.exp(- r * r / (RE*RE));
        //if (r > 1.0) return 0.0;
        //else return 1.0 - r;
      } else if (mode == 2) {
        if (r != 0.0) {
          return 1.0 / r / 8.0;
        } else {
          return 0.0;
        }
      }
      return 0.0;
    }
    
    public void printP() {
      double [] r = new double[3];
      r[0] = 0.0; r[1] = 0.0;
      for (int i = 0; i < 5000; i+=100) {
        r[2] = (double)i;
        System.out.println(r[2] + " " + getRho(r) + " " + getP(r) + " " + getGradP(r));
      }
      System.out.println(integralRho());
      /*for (int i = 0; i < Nm; i++) {
        System.out.println(molecule[i].r[2] + " " + getRho(molecule[i].r) + " " + getP(molecule[i].r) + " " + getGradP(molecule[i].r));
      }*/
    }
    
    public double[] getRhoProfile(double [] h) {
      double [] r = new double[3];
      double [] d = new double[h.length];
      r[0] = 0.0; r[1] = 0.0;
      for (int i = 0; i < h.length; i++) {
        r[2] = h[i];
        d[i] = getRho(r);
      }
      return d;
    }
    
    public double[] getPProfile(double [] h) {
      double [] r = new double[3];
      double [] d = new double[h.length];
      r[0] = 0.0; r[1] = 0.0;
      for (int i = 0; i < h.length; i++) {
        r[2] = h[i];
        d[i] = getP(r) / 101325.0;
      }
      return d;
    }
    
    public double[] getTProfile(double[] h) {
      double [] r = new double[3];
      double [] d = new double[h.length];
      r[0] = 0.0; r[1] = 0.0;
      for (int i = 0; i < h.length; i++) {
        r[2] = h[i];
        d[i] = getT(r);
      }
      return d;
    }
    
    public double getAnalyticRho0() {
      double maxz = 0.0;
      int maxidx = 0;
      double weight = 0.0;
      double rho0;
      
      for (int i = 0; i < Nm; i++) {
        weight += molecule[i].M;
        if (molecule[i].r[2] > maxz) {
          maxz = molecule[i].r[2];
          maxidx = i;
        }
      }
      weight -= molecule[0].M / 2.0;
      weight -= molecule[maxidx].M / 2.0;
      
      rho0 = weight * G / (Rd * T * (1.0 - Math.exp(- G / (Rd * T) * maxz) ) );
      return rho0;
    }
    
    public double getAnalyticT0() {
      double maxz = 0.0;
      int maxidx = 0;
      double weight = 0.0;
      double t0;
      
      for (int i = 0; i < Nm; i++) {
        weight += molecule[i].E;
        if (molecule[i].r[2] > maxz) {
          maxz = molecule[i].r[2];
          maxidx = i;
        }
      }
      weight -= molecule[0].E / 2.0;
      weight -= molecule[maxidx].E / 2.0;
      
      t0 = weight / maxz + G * maxz / (2.0 * Rd);
      return t0;
    }
    
    public double[] getAnalyticRho(double [] h) {
      double [] d = new double[h.length];
      if (FIX_MODE == T_FIXED) {
	      double rho0;
	      
	      rho0 = getAnalyticRho0();
	      
	      for (int i = 0; i < h.length; i++) {
	        d[i] = rho0 * Math.exp(- G / (Rd * T) * h[i]);
	      }
      } else if (FIX_MODE == RHO_FIXED) {
        for (int i = 0; i < h.length; i++) {
	        d[i] = RHO;
	      }
      }
      return d;
    }
    
    public double [] getAnalyticP(double[] h) {
      double [] d = new double[h.length];
      double rho0, p0, t0;
      
      if (FIX_MODE == T_FIXED) {
	      rho0 = getAnalyticRho0();
	      p0 = getP(rho0);
	      
	      for (int i = 0; i < h.length; i++) {
	        d[i] = p0 * Math.exp(- G / (Rd * T) * h[i]) / 101325.0;
	      }
      } else if (FIX_MODE == RHO_FIXED) {
        t0 = getAnalyticT0();
        p0 = RHO * Rd * t0;
        
        for (int i = 0; i < h.length; i++) {
	        d[i] = p0 - RHO * G * h[i];
	        d[i] /= 101325.0;
	      }
      }
      return d;
    }
    
    public double[] getAnalyticT(double[] h) {
      double [] d = new double[h.length];
      if (FIX_MODE == T_FIXED) {
        for (int i = 0; i < h.length; i++) {
          d[i] = T;
        }
      } else if (FIX_MODE == RHO_FIXED) {
	      double t0;
	      
	      t0 = getAnalyticT0();
	      for (int i = 0; i < h.length; i++) {
	        d[i] = t0 - G / Rd * h[i];
	      }
      }
      return d;
    }
    
    public double getScaleHeight() {
      double sh = 0.0;
      if (FIX_MODE == T_FIXED) {
        sh = Rd * T / G;
      } else if (FIX_MODE == RHO_FIXED) {
        sh = Rd * getAnalyticT0() / G;
      }
      return sh;
    }
    
    public void printV() {
      double [] r = new double[3];
      r[0] = 0.0; r[1] = 0.0;
      for (int i = 0; i < 50; i++) {
        r[2] = (double)i / 10.0;
        System.out.println(r[2] + " " + getVall(r)[2] + " " + getGradVall(r)[2] + " " + getAdvMass(r));
      }
    }
    
    public double integralRho() {
      double[] r = new double[3];
      double sum = 0.0;
      r[0] = 0.0;
      r[1] = 0.0;
      for (int i = -5000; i < 5000; i++) {
        r[2] = (double)i;
        sum += getRho(r);
      }
      //sum *= 0.01;
      return sum;
    }
    
    public void printM() {
      for (int i = 0; i < Nm; i++) {
        System.out.println(i + ": " + molecule[i].M);
      }
    }
    
    public void print() {
      for (int i = 0; i < Nm; i++) {
        System.out.println(molecule[i].r[0] + " " + molecule[i].r[1] + " " + molecule[i].r[2] + " " + 1.0/molecule[i].r[2]);
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
	Image img;
	Button btmol;
	Label lbn, lbt1, lbt2, lbrho1, lbrho2, lbmu1, lbmu2, lbg1, lbg2;
	TextField tft, tfrho, tfmu, tfg;
	Scrollbar sbt, sbrho, sbmu, sbg;
	Choice cholab, chofix;
	GraphCanvas gc;
	
	public void init() {
	  setLayout(null);
	  img = createImage(getWidth(), getHeight());
	  f = new Field(0, 30, 100, 370);
	  f.setArea(-0.1, 0.1, -1.0, 10000.0);
	  
	  m = new Molecules(200);
	  
	  btmol = new Button("New");
	  btmol.setBounds(30, 420, 50, 20);
	  btmol.addActionListener(this);
	  add(btmol);
	  
	  lbn = new Label("1");
	  lbn.setBounds(100, 420, 30, 20);
	  add(lbn);
	  
	  cholab = new Choice();
	  cholab.setBounds(160, 420, 100, 20);
	  cholab.add("密度 [kg/m^3]");
	  cholab.add("圧力 [atm]");
	  cholab.add("温度 [K]");
	  cholab.addItemListener(this);
	  add(cholab);
	  
	  chofix = new Choice();
	  chofix.setBounds(200, 450, 100, 20);
	  chofix.add("温度一定");
	  chofix.add("密度一定");
	  chofix.addItemListener(this);
	  add(chofix);
	  
	  lbt1 = new Label("温度：");
	  lbt1.setBounds(0, 450, 80, 20);
	  add(lbt1);
	  
	  lbrho1 = new Label("密度：");
	  lbrho1.setBounds(0, 450, 80, 20);
	  add(lbrho1);
	  lbrho1.setVisible(false);
	  
	  lbmu1 = new Label("平均分子量：");
	  lbmu1.setBounds(0, 480, 80, 20);
	  add(lbmu1);
	  
	  lbg1 = new Label("重力加速度：");
	  lbg1.setBounds(0, 510, 80, 20);
	  add(lbg1);
	  
	  tft = new TextField("300");
	  tft.setBounds(80, 450, 50, 20);
	  add(tft);
	  
	  tfrho = new TextField("1.2");
	  tfrho.setBounds(80, 450, 50, 20);
	  add(tfrho);
	  tfrho.setVisible(false);
	  
	  tfmu = new TextField("29.0");
	  tfmu.setBounds(80, 480, 50, 20);
	  add(tfmu);
	  
	  tfg = new TextField("9.8");
	  tfg.setBounds(80, 510, 50, 20);
	  add(tfg);
	  
	  lbt2 = new Label("K");
	  lbt2.setBounds(140, 450, 50, 20);
	  add(lbt2);
	  
	  lbrho2 = new Label("kg/m^3");
	  lbrho2.setBounds(140, 450, 50, 20);
	  add(lbrho2);
	  lbrho2.setVisible(false);
	  
	  lbmu2 = new Label("g/mol");
	  lbmu2.setBounds(140, 480, 50, 20);
	  add(lbmu2);
	  
	  lbg2 = new Label("m/s^2");
	  lbg2.setBounds(140, 510, 50, 20);
	  add(lbg2);
	  
	  sbt = new Scrollbar(Scrollbar.VERTICAL, -300, 10, -1000, -100);
	  sbt.setBounds(130, 450, 10, 20);
	  sbt.addAdjustmentListener(this);
	  add(sbt);
	  
	  sbrho = new Scrollbar(Scrollbar.VERTICAL, -12, 1, -100, -1);
	  sbrho.setBounds(130, 450, 10, 20);
	  sbrho.addAdjustmentListener(this);
	  add(sbrho);
	  sbrho.setVisible(false);
	  
	  sbmu = new Scrollbar(Scrollbar.VERTICAL, -290, 1, -1000, -10);
	  sbmu.setBounds(130, 480, 10, 20);
	  sbmu.addAdjustmentListener(this);
	  add(sbmu);
	  
	  sbg = new Scrollbar(Scrollbar.VERTICAL, -98, 1, -1000, -10);
	  sbg.setBounds(130, 510, 10, 20);
	  sbg.addAdjustmentListener(this);
	  add(sbg);
	  
	  gc = new GraphCanvas();
	  add(gc);
	  gc.setBounds(130, 0, 150, 430);
	  gc.init();
	  gc.setAxis(0.0, 0.0, 1.6, 10.0);
	  gc.ylabel("高度 [km]");
	  
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
	  double [] h = new double [500];
	  double [] hkm = new double [500];
	  double [] val = new double [500];
	  double [] val_an = new double [500];
	  double [] xscl = {0.0, 1.2};
	  double [] yscl = new double [2];
	  for (int i = 0; i < h.length; i++) {
	    h[i] = (double)i * 20.0;
	    hkm[i] = (double)i * 20.0 / 1000.0;
	  }
	  try {
	    while(true) {
	      m.nextStep();
	      
	      if (GRAPH_MODE == 0) {
	        val = m.getRhoProfile(h);
	        val_an = m.getAnalyticRho(h);
	      } else if(GRAPH_MODE == 1) {
	        val = m.getPProfile(h);
	        val_an = m.getAnalyticP(h);
	      } else if (GRAPH_MODE == 2) {
	        val = m.getTProfile(h);
	        val_an = m.getAnalyticT(h);
	      }
	      yscl[0] = m.getScaleHeight() / 1000.0;
	      yscl[1] = yscl[0];
	      
	      gc.clear();
	      gc.plot(val, hkm);
	      gc.plot(val_an, hkm, Color.red);
	      gc.plot(xscl, yscl, Color.green);
	      gc.repaint();
	      repaint();
	      Thread.sleep(10);
	    }
	  } catch(Exception err) {
	  }
	  
	}
	
	public void actionPerformed(ActionEvent e) {
	  if (e.getSource() == btmol) {
	    m.newMolecule();
	    lbn.setText(String.valueOf(m.Nm));
	  }
	}
	
	public void itemStateChanged(ItemEvent e) {
	  if (e.getSource() == cholab) {
		  if (cholab.getSelectedIndex() == 0) { // 密度
		    gc.clear();
		    gc.setXint(false);
		    gc.setAxis(0.0, 0.0, 1.6, 10.0);
		    gc.ylabel("高度 [km]");
		    GRAPH_MODE = 0;
		  }
		  if (cholab.getSelectedIndex() == 1) { // 圧力
		    gc.clear();
		    gc.setXint(false);
		    gc.setAxis(0.0, 0.0, 1.2, 10.0);
		    gc.ylabel("高度 [km]");
		    GRAPH_MODE = 1;
		  }
		  if (cholab.getSelectedIndex() == 2) { // 温度
		    gc.clear();
		    gc.setXint(true);
		    gc.setAxis(0.0, 0.0, 400.0, 10.0);
		    gc.ylabel("高度 [km]");
		    GRAPH_MODE = 2;
		  }
	  }
	  if (e.getSource() == chofix) {
	    if (chofix.getSelectedIndex() == 0) {
		    FIX_MODE = T_FIXED;
		    lbt1.setVisible(true);
		    lbt2.setVisible(true);
		    tft.setVisible(true);
		    sbt.setVisible(true);
		    lbrho1.setVisible(false);
		    lbrho2.setVisible(false);
		    tfrho.setVisible(false);
		    sbrho.setVisible(false);
		  }
		  if (chofix.getSelectedIndex() == 1) {
		    FIX_MODE = RHO_FIXED;
		    lbt1.setVisible(false);
		    lbt2.setVisible(false);
		    tft.setVisible(false);
		    sbt.setVisible(false);
		    lbrho1.setVisible(true);
		    lbrho2.setVisible(true);
		    tfrho.setVisible(true);
		    sbrho.setVisible(true);
		  }
	  }
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
	  if (e.getSource() == sbt) {
	    tft.setText(String.valueOf(sbt.getValue()*-1));
	    T = sbt.getValue()*-1;
	  }
	  if (e.getSource() == sbrho) {
	    tfrho.setText(String.valueOf(sbrho.getValue()*-1 / 10.0));
	    RHO = sbrho.getValue()*-1 / 10.0;
	  }
	  if (e.getSource() == sbmu) {
	    tfmu.setText(String.valueOf(sbmu.getValue()*-1 / 10.0));
	    Rd = 8.314472d / (sbmu.getValue()*-1 / 10.0 / 1000.0);
	  }
	  if (e.getSource() == sbg) {
	    tfg.setText(String.valueOf(sbg.getValue()*-1 / 10.0));
	    G = sbg.getValue()*-1 / 10.0;
	  }
	}
}


