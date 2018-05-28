/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waves;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author rohan
 */
public class Waves {

	
	public static final int COLD = -1;
public static final AtomicReference<Double> power = new AtomicReference<>(20000.0);
	public static final AtomicBoolean flipped = new AtomicBoolean(false);
	public static final AtomicBoolean hasClicked = new AtomicBoolean(false);
	public static final AtomicInteger xLoc = new AtomicInteger();
	public static final AtomicInteger yLoc = new AtomicInteger();
	public static final int WIDTH = 380;
	public static final int HEIGHT = 380;
	public static final double[][] heightMap = new double[WIDTH][HEIGHT];
	//Factor of 1000, ideally
	public static final int FPS = 50000;

	public static class WaveFrame extends javax.swing.JFrame {

		private static final long serialVersionUID = 1L;
public javax.swing.JPanel p;
		public WaveFrame() {
			super("Waves!");
			this.add(p = new WavePanel());
			this.pack();
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);
this.addKeyListener(new KeyListener(){

				@Override
				public void keyTyped(KeyEvent e) {
char b = ' ';
if((b=Character.toLowerCase(e.getKeyChar()))=='a'){
	flipped.set(false);
	hasClicked.set(true);
}else if( b=='s'){
	flipped.set(true);
	hasClicked.set(true);
}

if(b=='a'||b=='s'){
	Point mouseOnScreen = MouseInfo.getPointerInfo().getLocation();
Point 	mouseOnFrame = javax.swing.SwingUtilities.convertPoint(null, mouseOnScreen, Waves.w.p);
int x = (int) mouseOnFrame.getX();
int y = (int) mouseOnFrame.getY();
xLoc.set(x);
yLoc.set(y);
}else if(b=='q'){
	power.set(power.get()/2);
}else if(b == 'e'){
	power.set(power.get()*2);
}
				}

				@Override
				public void keyPressed(KeyEvent e) {

				}

				@Override
				public void keyReleased(KeyEvent e) {

				}
	
});
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {

				}

				@Override
				public void mousePressed(MouseEvent e) {
					hasClicked.set(true);
					xLoc.set(e.getX() - 10);
					yLoc.set(e.getY() - 10);
				}

				@Override
				public void mouseReleased(MouseEvent e) {

				}

				@Override
				public void mouseEntered(MouseEvent e) {

				}

				@Override
				public void mouseExited(MouseEvent e) {
flipped.set(!flipped.get());
				}

			});
		}

		public static class WavePanel extends javax.swing.JPanel {

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(Waves.WIDTH - 10, Waves.HEIGHT - 10);
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (int i = 0; i < Waves.WIDTH; i++) {
					for (int j = 0; j < Waves.WIDTH; j++) {
						double here = heightMap[i][j];
						Color c = new Color((int) Math.min(255, Math.max(0, here)), (int) Math.max(0, 235 - Math.abs(here / 50)), (int) Math.min(255, Math.max(0, -here)));
						g.setColor(c);
						g.fillRect(i, j, 1, 1);
					}
				}
			}
		}
	}
public static final WaveFrame w = new WaveFrame();
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws InterruptedException {
//Initialize state

		for (int i = 0; i < Waves.WIDTH; i++) {
			for (int j = 0; j < Waves.HEIGHT; j++) {
				double height = (6400 * Math.cos(i * Math.PI * 12.0 / Waves.WIDTH) + 6400 * Math.sin(j * Math.PI * 12.0 / Waves.HEIGHT+Math.PI/2));
//height = 0;		
//	height = 100*(i * i + j *j) - 10*Waves.HEIGHT*Waves.HEIGHT;
			
			int x = 1;
			//height = 5000 / Math.PI * Math.atan((j-Waves.HEIGHT/2)/(x=(i-Waves.WIDTH/2)==0?100000:x));
				//	height = 10000.0 * Math.atan((double) j/(i==0?1000.0:i))- 5000;

				heightMap[i][j] = height;
			}
		}



		new Thread(new Runnable() {

			public void run() {
				Timer t = new Timer(1000 / FPS, new ActionListener() {
					double[][] heightMap = new double[Waves.WIDTH][Waves.HEIGHT];

					@Override
					public void actionPerformed(ActionEvent e) {

						for (int k = 0; k < Waves.WIDTH; k++) {
							heightMap[k] = Arrays.copyOf(Waves.heightMap[k], Waves.HEIGHT);
						}
						if (hasClicked.get()) {

							hasClicked.set(false);
							if (get(xLoc.get(), yLoc.get(), heightMap) != COLD) {
								Waves.heightMap[xLoc.get()][yLoc.get()] += (flipped.get()?-1:1)*power.get();
							}
						}

						for (int i = 0; i < Waves.WIDTH; i++) {
							for (int j = 0; j < Waves.HEIGHT; j++) {

								double here = heightMap[i][j];

								double left = get(i - 1, j, heightMap);
								double right = get(i + 1, j, heightMap);
								double secondPartialX = right - here * 2 + left;
								double down = get(i, j - 1, heightMap);
								double up = get(i, j + 1, heightMap);
								double secondPartialY = up - here * 2 + down;

								Waves.heightMap[i][j] += (secondPartialX + secondPartialY) / 4.2;

							}
						}

					}

				});
				t.setRepeats(true);
				t.start();
			}

		}).start();

		w.setVisible(true);
		for (;;) {
			w.repaint();
		}
	}

	public static double get(int i, int j, double[][] heightMap) {
		if (i < 0 || i >= Waves.WIDTH || j < 0 || j >= Waves.HEIGHT) {
			return COLD;
		} else {
			return heightMap[i][j];
		}

	}
}
