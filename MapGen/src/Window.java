import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Map.Map;
import Map.WorldMap;

public class Window extends JFrame implements Runnable, MouseListener, MouseWheelListener, MouseMotionListener, KeyListener {
	
	private int height;
	private int width;
	private boolean isRunning = false;
	private int mod = 4;
	private int scrollCount = 0;
	private Map map;
	private int cameraX = 0, cameraY = 0;
	private int sCamX, sCamY;
	private boolean isPressed = false;
	JPanel panel = new JPanel();
	private boolean tick = false;
	private WorldMap wm;
	
	public Window(int height , int width)
	{
		this.height = height;
		this.width = width;
		init();
	}
	
	public Window()
	{ 
		this.height = 40;
		this.width = 100;
		init();
	}
	
	
	
	private void init()
	{
		map = new Map();
		
		this.add(panel);
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		panel.setPreferredSize(new Dimension(width,height));
		//map.mountainGen(100,100);
		panel.addMouseWheelListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseListener(this);
		panel.addKeyListener(this);
		wm = new WorldMap();
	//	wm = map.saveToWorldMap();
	//	wm.saveMap();
		//wm.test();
	}

	//private int s = 4000;
	
	public void draw()
	{
		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		double sWater = 0;
		
		Graphics g = buffer.createGraphics();
		int tileX, tileY;
		int min = 255;
		int max = 1;
		
		
		
		for (int i = 0; i < width/mod+2; i++)
            for (int j = 0; j < height/mod+2; j++)
            {    
                
                tileX = (i + cameraX/mod);
                tileY = (j + cameraY/mod);
                
                
                
            
                int d = 0;
                try
                {
                 //d = (map.map[tileX][tileY].getHeight());
                d = (wm.getTile(tileX, tileY).getHeight());
                
               // System.out.println(d);
                 if (d>255)
                	 d = 255;
                 if (d < 0)
                	 d = 0;
                }
                catch(Exception e){
              //  	e.printStackTrace();
                }
                
                g.setColor(new Color(0,d,0));
                
                
              if (tileX == 0 || tileY == 0 || tileX == map.gridSize || tileY == map.gridSize)
            	  g.setColor(Color.RED);
               
         
                g.fillRect(i*mod - cameraX % mod, j*mod - cameraY % mod, mod, mod);
            }
		try
		{
			g.setColor(Color.red);
			int x = ((int)(cameraX+panel.getMousePosition().getX())/mod)%map.getGridSize();
			if(x < 0)
				x+=map.getGridSize();
			int y = ((int)(cameraY+panel.getMousePosition().getY())/mod)%map.getGridSize();
			if(y < 0)
				y+=map.getGridSize();
			int h = map.map[x][y].getHeight();
			double w = map.map[x][y].getWaterLevel();
		g.drawString( "MOUSE POS "+panel.getMousePosition().getX() + " " + panel.getMousePosition().getY(), 10, 20); 
		g.drawString("TILE POS [" + x + ";" + y + "] H [" + h + "] W [" + w+"]"+"; SumWater: "+sWater,10, 30);
		
		
		 g.drawRect(x*mod - cameraX % mod, y*mod - cameraY % mod, mod, mod);
		}
		catch (Exception e)
		{
			
		}
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		panel.getGraphics().drawImage(buffer, 0, 0, null);
		panel.setPreferredSize(new Dimension(width,height));
		panel.setVisible(true);
	//	this.setTitle("Chunks loaded: "+wm.getLoadedChunks());
		try
		{
		System.out.println(wm.clearChunkmemory());
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void start()
	{
		isRunning = true;
		new Thread(this).start();
	}
	
	private void check()
	{
		width = panel.getWidth();
		height = panel.getHeight();
	//	System.out.println(width + " " + height);
	}

	
	@Override
	public void run() {
	
		map.start();
		while(isRunning)
		{
			check();
			draw();
		//	System.out.println(this.getMousePosition().getX()+ " " + this.getMousePosition().getY());
			try {
				Thread.sleep(10);
				tick = false;
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(e.BUTTON1 == e.getButton())
		{
			int x = ((cameraX+e.getX())/mod)%map.getGridSize();
			if(x<0)
				x+=map.getGridSize();
			int y = ((cameraY+e.getY())/mod)%map.getGridSize();
			if(y<0)
				y+=map.getGridSize();

		}
		
	}

	//@SuppressWarnings("static-access")
	@Override
	public void mousePressed(MouseEvent e) {
		isPressed = e.BUTTON3 == e.getButton();
		sCamX = e.getX();
		sCamY = e.getY();
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isPressed = false;
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
		if (!tick)
		{
			
		if (e.getWheelRotation() >  0)
		{
			e.consume();
			mod--;
		}
		else 
		{
			e.consume();
			mod++;
		}
		
		if (mod < 2)
			mod = 2;
		tick = true;
		//System.out.println(mod);
		}
	
		
	 
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isPressed)
		{
			
			cameraX = cameraX - (e.getX() - sCamX);
			cameraY = cameraY - (e.getY() - sCamY);
			sCamX = e.getX();
			sCamY = e.getY();
			
		}
		
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
		if(61 == e.getKeyCode())
			map.waterLevel += 10;
		if(45 == e.getKeyCode())
			map.waterLevel -= 10;
		
			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
