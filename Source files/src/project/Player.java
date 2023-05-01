package project;

//import java.util.Map;

public class Player {
	private int playerNumber; // Player1 hoặc Player2
	private int numberOfBulletsFired; // Lưu số đạn đã bắn của xe tăng của Player
	private int direction; // hướng mà Player đang nhìn
							// 0 là góc 12h, 180 là góc 6h
	private Point coordinates; // Tọa độ điểm trung tâm của xe tăng của Player
	private static final int rotationSpeed = 3; // Tốc độ xoay xe tăng của Player
	private static final double forwardSpeed = 2; // Tốc độ tiến xe tăng của Player
	private static final double reverseSpeed = -1.3; // Tốc độ lùi xe tăng của Player
	
	private GameEngine engine;
	
	public void turnRight() {// Xoay sang phải
		this.direction += rotationSpeed;
		if (this.direction>359){
			this.direction-=360;
		}
	}

	public void turnLeft() {// Xoay sang trái
		this.direction -= rotationSpeed;
		if (this.direction<0){
			this.direction+=360;
		}
	}
	private void move(double speed) {
		Point nextPoint = new Point(this.coordinates.getxCoord()
				+ (speed * Math.cos(Math.toRadians(90-this.direction))),
				this.coordinates.getyCoord()
				- (speed * Math.sin(Math
						.toRadians(90-this.direction))));
		if (cornerCrash(nextPoint,GameEngine.tankWidth)){//Nếu Player di chuyển vào góc
			nextPoint = new Point(this.coordinates.getxCoord()
					+ (speed * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord());//Đẩy theo hướng ngang 
			if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint = new Point(this.coordinates.getxCoord(),//Nếu không thể đẩy sang ngang thì đẩy theo hướng dọc
						this.coordinates.getyCoord()
						- (speed * Math.sin(Math.toRadians(90-this.direction))));
				if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
					nextPoint= new Point(this.coordinates.getxCoord(),//Nếu không đẩy được thì đứng yên
							this.coordinates.getyCoord());
				}
			}
		}else {
			if (wallCrashVertical(nextPoint,GameEngine.tankWidth)&&wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint= new Point(this.coordinates.getxCoord(),//Nếu không đẩy được thì đứng yên
						this.coordinates.getyCoord());
			}
			if (wallCrashVertical(nextPoint, GameEngine.tankWidth)){ //Nếu Player đi vào tường dọc

				nextPoint = new Point(this.coordinates.getxCoord(),//Đẩy Player theo hướng dọc
						this.coordinates.getyCoord()
						- (speed * Math.sin(Math.toRadians(90-this.direction))));
			}
			if(wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){//Nếu Player đi vào tường ngang
				nextPoint = new Point(this.coordinates.getxCoord()//Đẩy Player theo hướng ngang
						+ (speed * Math.cos(Math.toRadians(90-this.direction))),
						this.coordinates.getyCoord());
			}
		} 
		this.coordinates=nextPoint;
	}
	public void goForward(){//Tiến lên
		move(forwardSpeed);
	}

	public void reverse() {//Lùi lại
		move(reverseSpeed);
	}
	
	private boolean wallCrashVertical(Point p, int w){
		//Trả về nếu vật thể có tọa độ p, bán kính w có đang nằm trên tường dọc không
		w=w/2;
		int xWall=0;
		if (this.currentXSquare()!=(int)this.currentXSquare()){
			xWall=1;
		}
		//Kiểm tra có tường bên trái ô vuông không
		boolean byLeftWall = engine.maze.isWallLeft((int)this.currentXSquare(), (int)this.currentYSquare());
		//và vật thể đó có nằm trên tường trái không
		boolean inLeftWall = (p.getxCoord()-w<=
				((int)this.currentXSquare())*GameEngine.squareWidth+((int)this.currentXSquare()+1)*GameEngine.wallWidth);
		//Tương tự với bên phải
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	private boolean wallCrashHorizontal(Point p, int w){
		//Trả về nếu vật thể có tọa độ p, bán kính w có đang nằm trên tường ngang không
		w=w/2;
		int yWall=0;
		if (this.currentYSquare()!=(int)this.currentYSquare()){
			yWall=1;
		}
		//Kiểm tra có tường bên trên ô vuông không
		boolean byTopWall = engine.maze.isWallAbove((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inTopWall = (p.getyCoord()-w<=
				((int)this.currentYSquare())*GameEngine.squareWidth+((int)this.currentYSquare()+1)*GameEngine.wallWidth);
		//Kiểm tra có tường bên dưới ô vuông không
		boolean byBottomWall = engine.maze.isWallBelow((int) this.currentXSquare(), (int) this.currentYSquare());
		boolean inBottomWall = (p.getyCoord()+w>=
				((int)this.currentYSquare()+1+yWall)*GameEngine.squareWidth+((int)this.currentYSquare()+1+yWall)*GameEngine.wallWidth);
		return ((byTopWall&&inTopWall)|(byBottomWall&&inBottomWall));
	}
	
	private boolean cornerCrash(Point p, int w){
		//Trả về nếu vật thể có tọa độ p, bán kính w có đang nằm trên góc gần nhất không
		w=w/2;
		//Tìm góc gần điểm tọa độ p nhất
		int x=0;
		int y=0;
		int xCounter=(int)p.getxCoord();
		while (xCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			xCounter=xCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			x++;
		}
		int yCounter=(int)p.getyCoord();
		while (yCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			yCounter=yCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			y++;
		}
		//Tìm xem góc gần nhất đó có tường không, nếu không có trả về false
		boolean isWallInCorner=engine.maze.isWallAbove(x,y)||engine.maze.isWallLeft(x,y);
		if(x>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallAbove(x-1,y);
		}
		if(y>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallLeft(x,y-1);
		}
		if(!isWallInCorner){
			return false;
		}
		//Tính khoảng cách giữa điểm tọa độ p và góc gần nhất đó
		Point p1=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p2=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p3=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		Point p4=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		double distance1=Point.distance(p1,p);
		double distance2=Point.distance(p2,p);
		double distance3=Point.distance(p3,p);
		double distance4=Point.distance(p4,p);
		double distance=Math.min(distance1, Math.min(distance2,Math.min(distance3,distance4)));
		return distance<w;
	
	}
	
	// Phương thức bắn đạn
	public void shoot() {
		if (this.numberOfBulletsFired < 5) {
			//Nơi đạn xuất phát
			Point bulletStart = new Point(this.coordinates.getxCoord()
					+ ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord()
					- ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) * Math.sin(Math.toRadians(90-this.direction)))); 
			
			
			if (wallCrashVertical(bulletStart, GameEngine.bulletWidth)||wallCrashHorizontal(bulletStart,GameEngine.bulletWidth)||cornerCrash(bulletStart,GameEngine.bulletWidth)){ //if the bullet would start in/over any of the walls
				this.hit();
			} else {
				GameEngine.bulletList.add(new Bullet(playerNumber, bulletStart, direction,engine));
				//Tạo thực thể đạn mới, với playerNumber là Player bắn, di chuyển theo direction hiện tại và tọa độ xuất phát bulletStart
				this.numberOfBulletsFired += 1;//Tăng số đạn đã bắn lên
		    }
		}
	}

	// Phương thức trúng đạn
	public void hit() {
		if (this.playerNumber == 0) {
			GameEngine.player1_dead=true;
		} else {
			GameEngine.player2_dead=true;
		}
		engine.roundOver();
	}

	public double currentXSquare() {
		//  Trả về giá trị x của ô vuông mà vị trí Player đang ở
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.coordinates.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getxCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.coordinates.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getxCoord()){
				return i-0.5;
			}
		}
		return -1;
	}

	public double currentYSquare() {
		// Trả về giá trị y của ô vuông mà vị trí Player đang ở
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.coordinates.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getyCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<this.coordinates.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getyCoord()){
				return i-0.5;
			}
		}
		return -1;

	}

	// Hàm tạo
	public Player(int playerNo, double x, double y,GameEngine e) {
		this.playerNumber = playerNo;
		this.numberOfBulletsFired = 0;
		this.direction = (int) (Math.random() * 360);//Player quay về hướng ngẫu nhiên
		this.coordinates = new Point(x, y);
		this.engine=e;
	}
	
	// Getter và Setter
	
	public void decreaseNumberOfBulletsFired() {
		while (this.numberOfBulletsFired!=0){
			this.numberOfBulletsFired -= 1;
		}
	}
	
	public Point getCoordinates(){
		return this.coordinates;
	}
	
	public int getDirection(){
		return this.direction;
	}

	public void setCoordinates(double x, double y){
		this.coordinates=(new Point(x,y));
	}
	public int getBulletsFired(){
		return this.numberOfBulletsFired;
	}

}



