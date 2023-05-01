package project;

public class Square {
	private int xCoord;
	private int yCoord;//Tọa độ ô vuông là (xCoord, yCoord)
	
	//Hàm tạo
	public Square(int a, int b){
		this.xCoord=a;
		this.yCoord=b;
	}

	//Getter và Setter
	public int getxCoord() {
		return xCoord;
	}
	
	public int getyCoord() {
		return yCoord;
	}
	
	public String toString(){
		return "" + xCoord + "," + yCoord;
	}
	
	public boolean equals(Square a){
		return this.getxCoord()==a.getxCoord()&&this.getyCoord()==a.getyCoord();
	}
}


