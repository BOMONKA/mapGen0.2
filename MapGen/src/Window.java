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
		map.generate(16);
		this.add(panel);
		this.setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		panel.setPreferredSize(new Dimension(width,height));
		//map.mountainGen(100,100);
		this.addMouseWheelListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseListener(this);
		this.addKeyListener(this);
	}

	//private int s = 4000;
	
	public void draw()
	{
		BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = buffer.createGraphics();
		int tileX, tileY;
		
				
		for (int i = 0; i < width/mod+2; i++)
            for (int j = 0; j < height/mod+2; j++)
            {    
                
                tileX = (i + cameraX/mod)%map.getGridSize();
                tileY = (j + cameraY/mod)%map.getGridSize();
                
                if(tileX<0)
                	tileX = (i + cameraX/mod)%(map.getGridSize()) + map.getGridSize();
                
                if(tileY<0)
                	tileY = (j + cameraY/mod)%(map.getGridSize()) + map.getGridSize();
                
                try
                {
                int d = (map.map[tileX][tileY].getHeight()*255/map.maxheight);
                
                
                if (map.map[tileX][tileY].getHeight()>map.waterLevel)
                {
                    if (map.map[tileX][tileY].getHeight()>5050)
                    {
                        
                    
                    if (map.map[tileX][tileY].getHeight()>7050)
                    {
                        if (map.map[tileX][tileY].getHeight()>8000)
                        {
                            g.setColor(new Color(d,d,d));
                        } else
                        {
                            d = d /2;
                            g.setColor(new Color(d,d,d));
                        }
                    }
                    else 
                    {
                        g.setColor(new Color(0,255-d,0));
                        
                    }
                    }
                    else {
                        g.setColor(Color.ORANGE);
                    }
                }
                else 
                {
                	d*=2;
                    g.setColor(new Color(0,0,d));
                }
                
                if(tileX == 0 || tileY == 0) //draws red lines on edges
                		g.setColor(Color.RED);
                
                g.fillRect(i*mod - cameraX % mod, j*mod - cameraY % mod, mod, mod);
                }
                catch
                ( Exception ex)
                {
                    
                }
            }
		panel.getGraphics().drawImage(buffer, 0, 0, null);
		panel.setPreferredSize(new Dimension(width,height));
		panel.setVisible(true);
	
		//map.waterLevel = s;
		//s+=1;
		//this.setTitle("water = "+s+"m");
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
		
		//map.mountainGen((cameraX+e.getX())/mod, (cameraY+e.getY())/mod);
		
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
