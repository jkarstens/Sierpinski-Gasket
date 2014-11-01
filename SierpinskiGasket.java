import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class SierpinskiGasket extends Applet implements AdjustmentListener, ComponentListener, Runnable{

	private Thread thread;
	private Graphics dbGraphics;
	private Image dbImage;

	private Point[] triangle;
	private boolean recalculate;
	private ArrayList<Point> gasket;

	private Scrollbar iterationsScroll;
	private Checkbox keepDrawingBox;

	public void init(){

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(d.width-30, d.height-30);
		setBackground(new Color(176, 224, 230));
		addComponentListener(this);

		triangle = new Point[3];
		createTriangle();
		recalculate = false;
		gasket = new ArrayList<Point>();

		iterationsScroll = new Scrollbar(Scrollbar.VERTICAL, 50000, 5, 1, 100005);
		iterationsScroll.addAdjustmentListener(this);
		iterationsScroll.setFocusable(false);
		iterationsScroll.setBackground(new Color(0, 128, 128));
		add(iterationsScroll);

		keepDrawingBox = new Checkbox("",false);
		keepDrawingBox.setFocusable(false);
		add(keepDrawingBox);
	}

	public void paint(Graphics g){

		setComponentBounds();
		drawStrings(g);

		if(keepDrawingBox.getState()) recalculate = true;

		if(recalculate){

			gasket.clear();

			//initial points
			int a = (int)(3.0 * Math.random());
			int b = (int)(3.0 * Math.random());
			while(b == a) b = (int)(3.0 * Math.random());
			gasket.add(triangle[a]);
			gasket.add(triangle[b]);

			fillGasket(triangle[a], triangle[b]);

			for(int i=2; i<iterationsScroll.getValue(); i++) fillGasket(gasket.get(i), randomPoint());

			if(!keepDrawingBox.getState()) recalculate = false;
		}

		for(int i=0; i<gasket.size(); i++) g.drawLine(gasket.get(i).x, gasket.get(i).y, gasket.get(i).x, gasket.get(i).y); //draw point
	}

	private void fillGasket(Point a, Point b){ //ah! recursion is too large for stack at 3000, much less 50000 iterations -> Stack Overflow exception

		Point midpoint = new Point( (a.x+b.x) / 2, (a.y+b.y) / 2 );
		gasket.add(midpoint);
	}

	private void createTriangle(){

		int w = getSize().width;
		int h = getSize().height;

		triangle[0] = new Point(50, h-50);
		triangle[1] = new Point(w/2, 50);
		triangle[2] = new Point(w-50, h-50);
	}

	private Point randomPoint(){

		int i = (int)(3.0 * Math.random());
		return triangle[i];
	}

	public void update(Graphics g){

		if(dbImage == null){

			dbImage = createImage(getSize().width, getSize().height);
			dbGraphics = dbImage.getGraphics();
		}

		dbGraphics.setColor(getBackground());
		dbGraphics.fillRect(0, 0, getSize().width, getSize().height);
		dbGraphics.setColor(getForeground());
		paint(dbGraphics);

		g.drawImage(dbImage, 0, 0, this);
	}

	public void adjustmentValueChanged(AdjustmentEvent e){

		Object source = e.getSource();

		if(source == iterationsScroll) recalculate = true;
	}

	public void componentHidden(ComponentEvent e){
	}

	public void componentShown(ComponentEvent e){
	}

	public void componentMoved(ComponentEvent e){
	}

	public void componentResized(ComponentEvent e){

		Component source = e.getComponent();

		if(source == this){

			createTriangle();
			recalculate = true;
		}
	}

	private void setComponentBounds(){

		iterationsScroll.setBounds(5,25,20,100);
		keepDrawingBox.setBounds(27,82,13,13);
	}

	private void drawStrings(Graphics g){

		g.setFont(new Font("Georgia", Font.BOLD, 11));
		g.drawString("Iterations: " + iterationsScroll.getValue(), 30, 65);
		g.drawString("Continuous",42,90);
		g.drawString("Randomization",42,100);
	}

	public void start(){

		if(thread == null){

			thread = new Thread(this);
			thread.start();
		}
	}

	public void run(){

		while(thread != null){

			repaint();

			try{

				Thread.sleep(20);
			}
			catch(InterruptedException e){
			}
		}
	}

	public void stop(){

		thread = null;
	}

	public static void main(String[] args){

		Applet thisApplet = new SierpinskiGasket();
		thisApplet.init();
		thisApplet.start();

		JFrame frame = new JFrame("Sierpinski's Gasket - Randomly Drawn");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(thisApplet.getSize().width, thisApplet.getSize().height);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(thisApplet, BorderLayout.CENTER);
		frame.setVisible(true);
	}
}
