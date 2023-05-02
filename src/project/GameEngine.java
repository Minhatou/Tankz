package project;

import java.util.ArrayList;

public class GameEngine {
	public static int player1_score;
	public static int player2_score;
	public static boolean player1_dead = false;
	public static boolean player2_dead = false;
	public static final int tankWidth = 60;//Kích thước xe tăng của Player
	public static final int bulletWidth = 12;//Kích thước đạn
	public static final int squareWidth = 100;//Kích thước ô vuông
	public static final int wallWidth = 10;//Kích thước tường
	public Maze maze;
	public Player player1;
	public Player player2;
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>(10);//Tạo mảng chứa đạn
	public GameEngine() {
		// Khởi tạo
		player1_score = 0;
		player2_score = 0;
		maze = new Maze();
		// Tạo người chơi và spawn ở vị trí ngẫu nhiên
		int x = (int) (Math.random() * 7);
		int y = (int) (Math.random() * 7);
		int x1 = (int) (Math.random() * 7);
		int y1 = (int) (Math.random() * 7);
		while (x1 == x && y1==y) {//nếu vị trí spawn giống nhau thì random lại
			x1 = (int) (Math.random() * 7);
			y1 = (int) (Math.random() * 7);
		}
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
		this.player1 = new Player(0, x, y,this);
		this.player2 = new Player(1, x1, y1,this);
	}

	public void roundOver() {//Khởi tạo game mới
	
		try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		if (GameEngine.player1_dead) {
			GameEngine.player2_score++;
			GameEngine.player1_dead=false;
		} else if (GameEngine.player2_dead) {
			GameEngine.player1_score++;
			GameEngine.player2_dead=false;
		}
		
		// Tạo người chơi và spawn ở vị trí ngẫu nhiên
		int x = (int) (Math.random() * 7);
		int y = (int) (Math.random() * 7);
		int x1 = (int) (Math.random() * 7);
		int y1 = (int) (Math.random() * 7);
		while (x1 == x && y1==y) {//nếu vị trí spawn giống nhau thì random lại
			x1 = (int) (Math.random() * 7);
			y1 = (int) (Math.random() * 7);
		}
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
		this.player1 = new Player(0, x, y,this);
		this.player2 = new Player(1, x1, y1,this);
		GameEngine.bulletList = new ArrayList<Bullet>(10);
		maze = new Maze();
	}
}