package project;

public class Bullet{
	
	private int timer;
	private int owner;
	private int angle;
	private Point position;
	private static final double bulletSpeed = 4.5;
	
	private GameEngine engine;
	
	public Bullet(int player, Point position, int angle, GameEngine e) {
		this.owner = player;
		this.angle = angle;
		this.position = position;		
		this.timer = 1000; //Thời gian của đạn là 1000 frame / 60 fps = 16.67 s
		this.engine=e;
	}
	
	public Point getPosition() {
		return position;
	}
	public int getAngle(){
		return angle;
	}
	
	public void removeBullet(){//Xóa đạn
		if (this.owner==0){
			engine.player1.decreaseNumberOfBulletsFired();
		}else{
			engine.player2.decreaseNumberOfBulletsFired();
		}
		GameEngine.bulletList.remove(this);
	}
	
	public void moveBullet(){
		Point nextPoint = new Point(this.position.getxCoord()
				+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
				this.position.getyCoord()
				- (bulletSpeed * Math.sin(Math
						.toRadians(90-this.angle))));
		if (wallCrashHorizontal(nextPoint, GameEngine.bulletWidth)){ //Nếu đạn đập vào tường ngang
			flipBulletH();
			nextPoint = new Point(this.position.getxCoord()
					+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
					this.position.getyCoord());
		}else if (wallCrashVertical(nextPoint, GameEngine.bulletWidth)){ //Nếu đạn đập vào tường dọc
			flipBulletV();
			nextPoint = new Point(this.position.getxCoord(),
					this.position.getyCoord()
					- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
		}else if(cornerCrash(nextPoint,GameEngine.bulletWidth)){
			if(this.currentYSquare()==(int)this.currentYSquare()){
				flipBulletH();
				nextPoint = new Point(this.position.getxCoord()
						+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
						this.position.getyCoord());
			}else{
				flipBulletV();
				nextPoint = new Point(this.position.getxCoord(),
						this.position.getyCoord()
						- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
			}
		}
		this.position=nextPoint;
	}
	
	public boolean reduceTimer(){
		timer--;
		if (timer<0){
			this.removeBullet();//Xóa đạn
			return true;
		}
		return false;
	}
	
	private void flipBulletV(){
		this.angle=(-this.angle) + 360;
	}
	
	private void flipBulletH(){
		if (this.angle>180){
			this.angle = -this.angle+540;
		}else{
			this.angle=-this.angle+180;
		}
	}	
	
	public void tankCollision(){
		//Kiểm tra nếu đạn đã va chạm với player1 hoặc player2 chưa
			if (collision(engine.player1)){
				this.removeBullet();
				engine.player1.hit();
			}else if (collision(engine.player2)){
				this.removeBullet();
				engine.player2.hit();
			}
		
	}
	
	private boolean collision(Player player){//Kiểm tra va chạm giữa đạn và Player
		//tính khoảng cách từ viên đạn với Player
		double distance = Point.distance(player.getCoordinates(),this.position);
		//trả về nếu có va chạm hay chưa
		return (distance<=(GameEngine.tankWidth/2+GameEngine.bulletWidth/2));
	}
	
	public double currentXSquare() {
		// Trả về giá trị x của ô vuông mà vị trí Player đang ở
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.position.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getxCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.position.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getxCoord()){
				return i-0.5;
			}
		}
		return -1;
	}

	public double currentYSquare() {
		// Trả về giá trị y của ô vuông mà vị trí Player đang ở
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.position.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getyCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.position.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getyCoord()){
				return i-0.5;
			}
		}
		return -1;

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
		//Tương tự với tường bên phải
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	private boolean wallCrashHorizontal(Point p, int w){
		//Trả về nếu điểm có tọa độ p, bán kính w có đang nằm trên tường ngang không
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
}



