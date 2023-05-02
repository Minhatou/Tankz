package project;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
public class GameLauncher extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	private GameEngine engine = new GameEngine();
	
	public static final int xDimension=780;
	public static final int yDimension=780;//độ lớn màn hình
	
	private boolean running=false;
	private Thread thread;
	
	//Khởi tạo biến ảnh:
	private BufferedImage background;
	private BufferedImage tank1;
	private BufferedImage tank2;
	private BufferedImage bullet;
	private BufferedImage hWall;
	private BufferedImage vWall;
	
	private boolean[] instructionsArray = new boolean[10]; //W,A,S,D,Q,Lên,Xuống,Trái,Phải,Enter
	
	private synchronized void start(){//Bắt đầu game
		if (running){
			return;
		}
		running=true;
		thread=new Thread(this);
		thread.start();
	}
	
	private void init() {//Khởi tạo
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			background = loader.loadImage("/background.png");
			tank1 = loader.loadImage("/tank1.png");
			tank2 = loader.loadImage("/tank2.png");
			bullet = loader.loadImage("/bullet.png");
			hWall = loader.loadImage("/hWall.png");
			vWall = loader.loadImage("/vWall.png");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		addKeyListener(new KeyboardInput(this));
		
	}
	
	private synchronized void stop(){//Dừng game
		if (!running){
			return;
		}
		
		running=false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	
	public void run(){//Chạy game
		init();
		long lastTime = System.nanoTime();
		final double amountOfTicks=60.0;//60FPS
		double ns = 1000000000/amountOfTicks;
		double delta = 0;
		while(running){
			long now = System.nanoTime();
			delta+=(now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				tick();
				render();
				delta--;
			}
		}
		stop();
	}
	
	private void tick(){//mỗi frame
		
		
		//Di chuyển player
		if (instructionsArray[0]){
			engine.player1.goForward();//Tiến
		} else if (instructionsArray[2]){
			engine.player1.reverse();//Lùi
		}
		if (instructionsArray[1]){
			engine.player1.turnLeft();//Xoay trái
		}else if (instructionsArray[3]){
			engine.player1.turnRight();//Xoay phải
		}
		if (instructionsArray[5]){
			engine.player2.goForward();//Tiến
		} else if (instructionsArray[7]){
			engine.player2.reverse();//Lùi
		}
		if (instructionsArray[6]){
			engine.player2.turnLeft();//Xoay trái
		}else if (instructionsArray[8]){
			engine.player2.turnRight();//Xoay phải
		}
		//Hành động bắn
		if (instructionsArray[4]){
			engine.player1.shoot();
			instructionsArray[4]=false;//Mỗi lần nhấn nút chỉ bắn 1 lần
		}
		if (instructionsArray[9]){
			engine.player2.shoot();
			instructionsArray[9]=false;//Mỗi lần nhấn nút chỉ bắn 1 lần
		}
		
		//Đạn
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			GameEngine.bulletList.get(i).moveBullet();
			boolean removed = GameEngine.bulletList.get(i).reduceTimer();//đếm ngược timer của đạn
			if (!removed){//Khi đạn chưa hết timer
				GameEngine.bulletList.get(i).tankCollision();//Kiểm tra va chạm
			}
		}

	}
          
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//Vẽ vật thể{
		
		//Vẽ background:
		g.drawImage(background, 0, 0, this);
		
		//Vẽ tường
		for (int x = 0; x<7;x++){
			for (int y = 0; y<7; y++){
				if (engine.maze.isWallBelow(x, y)){
					g.drawImage(hWall,(GameEngine.squareWidth+GameEngine.wallWidth)*x,(GameEngine.squareWidth+GameEngine.wallWidth)*(y+1),this);
				}
				if (engine.maze.isWallRight(x, y)){
					g.drawImage(vWall,(GameEngine.squareWidth+GameEngine.wallWidth)*(x+1),(GameEngine.squareWidth+GameEngine.wallWidth)*y,this);
				}
			}
		}
		

		//Vẽ tank
		double rotationRequired = Math.toRadians(engine.player1.getDirection());
		double locationX = tank1.getWidth() / 2;
		double locationY = tank1.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		g.drawImage(op.filter(tank1, null), (int)(engine.player1.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player1.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
		
		AffineTransform tx2 = AffineTransform.getRotateInstance(Math.toRadians(engine.player2.getDirection()), tank2.getWidth() / 2, tank2.getHeight() / 2);
		AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_BILINEAR);
		g.drawImage(op2.filter(tank2, null), (int)(engine.player2.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player2.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
		//Vẽ đạn
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			g.drawImage(bullet,(int)(GameEngine.bulletList.get(i).getPosition().getxCoord()-GameEngine.bulletWidth/2),(int)(GameEngine.bulletList.get(i).getPosition().getyCoord()-GameEngine.bulletWidth/2),this);
		}
		
		//Hiện vật thể
		g.dispose();
		bs.show();
}
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=true;//W = tiến
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=true;//A = xoay trái
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=true;//S = lùi 
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=true;//D = xoay phải
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=true;//Q = bắn
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=true;// Mũi tên lên = tiến
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=true;// Mũi tên trái = xoay trái
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=true;// Mũi tên xuống = lùi
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=true;// Mũi tên phải = xoay phải
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=true;// Enter = bắn
		}else if (key==KeyEvent.VK_SPACE){
			//In ra điểm
			System.out.println("Player 1: "+GameEngine.player1_score + ", Player 2: "+GameEngine.player2_score);
		}
	}
	public void keyReleased(KeyEvent e){//Dừng lại chức năng của các nút khi nhả
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=false;
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=false;
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=false;
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=false;
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=false;
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=false;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=false;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=false;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=false;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=false;
		}
	}
	

	public static void main(String args[]){
		GameLauncher game = new GameLauncher();

		//Kích thước cửa sổ
		game.setPreferredSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMaximumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMinimumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));

		JFrame frame = new JFrame("Tank");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();

	}
}
