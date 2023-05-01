package project;
import java.util.ArrayList;

public class Maze {
	private static final int gridWidth = 7;
	//Số ô vuông mỗi cạnh = 7
	private static boolean[][][] walls = new boolean[2][gridWidth + 1][gridWidth + 1];
	//mảng trả về true nếu có tường, trả về false nếu không có tường
	//walls[0=ngang, 1=dọc][x][y]
	//Ô vuông vị trí (a,b) sẽ có:
	//tường trái = [1][a][b]
	//tường phải = [1][a+1][b]
	//tường trên = [0][b][a]
	//tường dưới = [0][b+1][a]
	
	public Maze() {
		new Maze(30);
		//số tường = 30
	}
	public Maze(int density) {
		walls = generateMaze(density);
		//tạo một bảng tường ngẫu nhiên
		Square[][] areas = this.mergeAll();
		while (areas.length>1){//tìm tất cả các khu vực cách biệt nhau
			removeWall(areas[0]);//xóa bỏ tường giữa 2 ô vuông cạnh nhau
			areas = this.mergeAll();
		}
	}

	public boolean isWallLeft(int x, int y) {
		//Kiểm tra xem ô vuông có tường trái không, có thì trả về true, không có trả về false
		return walls[1][x][y];
	}

	public boolean isWallRight(int x, int y) {
		//Kiểm tra xem ô vuông có tường phải không, có thì trả về true, không có trả về falsee
		return walls[1][x + 1][y];
	}

	public boolean isWallAbove(int x, int y) {
		//Kiểm tra xem ô vuông có tường trên không, có thì trả về true, không có trả về false
		return walls[0][y][x];
	}

	public boolean isWallBelow(int x, int y) {
		//Kiểm tra xem ô vuông có tường dưới không, có thì trả về true, không có trả về false
		return walls[0][y + 1][x];
	}

	private Square[] neighbors(Square square) {
		// trả về tất cả các ô vuông có cùng cạnh với ô vuông đang kiểm tra và bao gồm cả chính nó
		Square[] neighbors = new Square[5];
		int numberOfNeighbors = 0;
		if (!isWallLeft(square.getxCoord(), square.getyCoord())) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord() - 1,
					square.getyCoord());
			numberOfNeighbors++;
		}
		if (!isWallRight(square.getxCoord(), square.getyCoord())) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord() + 1,
					square.getyCoord());
			numberOfNeighbors++;
		}
		if (!isWallAbove(square.getxCoord(), square.getyCoord())) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord(),
					square.getyCoord() - 1);
			numberOfNeighbors++;
		}
		if (!isWallBelow(square.getxCoord(), square.getyCoord())) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord(),
					square.getyCoord() + 1);
			numberOfNeighbors++;
		}
		neighbors[numberOfNeighbors] = square;
		
		// Xóa bỏ giá trị null
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = neighbors[i];
			}
			return cutReturnValue;
		} else {
			return neighbors;
		}
	}
	private static boolean isNeighbour(Square a, Square b){
		//Kiểm tra xem 2 ô vuông có là hàng xóm của nhau không
		int x = a.getxCoord();
		int y = a.getyCoord();
		int x2 = b.getxCoord();
		int y2 = b.getyCoord();
		return (x2-1==x && y2==y)||(x2+1==x && y2==y)||(x2==x && y2-1==y)||(x2==x && y2+1==y);
	}
	private void removeWall(Square[] area){
		// Kết nối một khu vực với khu vực khác
		Square[] neighbors = areaNeighbors(area);
		//Tìm tất cả các hàng xóm của khu vực đó
		int numberToRemove=2;//Xóa bỏ 1 đến 2 bức tường
		for (int j = 0; j<numberToRemove;j++){		
		int randomNum = (int) (Math.random()*neighbors.length);
		Square chosen = neighbors[randomNum];
		//Lựa chọn ô vuông ngẫu nhiên trong danh sách hàng xóm
		Square areaSquareChosen = new Square(0,0);
		for (int i = 0; i<area.length; i++){
			if (isNeighbour(area[i],chosen)){
				areaSquareChosen=area[i];
				break;
			}
		}
		//Tìm ô vuông trong khu vực kề với ô vuông đã chọn
		int x = chosen.getxCoord();
		int y = chosen.getyCoord();
		int x2 = areaSquareChosen.getxCoord();
		int y2 = areaSquareChosen.getyCoord();
		if (x==x2){
			walls[0][Math.max(y,y2)][x]=false;
		}else{
			walls[1][Math.max(x2, x)][y]=false;
		}
		}
		//Xóa bỏ tường giữa 2 ô vuông đó
	}
	private static Square[] merge(Square[] a, Square[] b) {
		Square[] returnValue = new Square[b.length + a.length];
		for (int i = 0; i < a.length; i++) {
			if (!(a[i] == null)) {
				returnValue[i] = a[i];
			}
		}// Trả về các giá trị trong a khác null
		for (int i = 0; i < b.length; i++) {// Kiểm tra tất cả giá trị trong b
			boolean found = false;
			for (int j = 0; j < a.length; j++) {
				if (!(a[j] == null) && !(b[i] == null) && b[i].equals(a[j])) {
					found = true;
				}
			}// Kiểm tra liệu giá trị đó cũng có trong a không
			if (!found && !(b[i] == null)) {// Nếu không thì trả về giá trị đó
				for (int j = 0; j < returnValue.length; j++) {
					if (returnValue[j] == null) {
						returnValue[j] = b[i];
						break;
					}
				}
			}
		}
		// Xóa bỏ giá trị null
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < returnValue.length; i++) {
			if (returnValue[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = returnValue[i];
			}
			return cutReturnValue;
		} else {
			return returnValue;
		}
	}

	private static Square[] intersection(Square[] a, Square[] b) {
		Square[] returnValue = new Square[b.length];
		for (int i = 0; i < b.length; i++) {//Kiểm tra tất cả giá trị trong b
			boolean found = false;
			for (int j = 0; j < a.length; j++) {
				if (!(a[j] == null) && !(b[i] == null) && b[i].equals(a[j])) {
					found = true;
				}
			}// Kiểm tra liệu giá trị đó cũng có trong a không
			if (found && !(b[i] == null)) {// Nếu có thì trả về giá trị đó
				for (int j = 0; j < returnValue.length; j++) {
					if (returnValue[j] == null) {
						returnValue[j] = b[i];
						break;
					}
				}
			}
		}
		// Xóa bỏ giá trị null
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < returnValue.length; i++) {
			if (returnValue[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = returnValue[i];
			}
			return cutReturnValue;
		} else {
			return returnValue;
		}
	}
	private Square[] areaNeighbors(Square[] area){
		//Trả về tất cả hàng xóm của khu vực đang được chọn
		Square[] neighbors = new Square[49];
		//Trả về tất cả hàng xóm có chung tường
		for (int i = 0; i<area.length;i++){
			neighbors =  merge(neighbors,walledNeighbours(area[i]));	
		}
		//Xóa ô vuông đã có trong khu vực
		Square[] cutNeighbors = new Square[neighbors.length];
		int cutNeighborsLength=0;
		for (int i = 0;i<neighbors.length;i++){
			boolean found = false;
			for (int j=0;j<area.length;j++){
				if (neighbors[i].equals(area[j])){
					found = true;
				}
			}
			if (!found){
				cutNeighbors[cutNeighborsLength]=neighbors[i];
				cutNeighborsLength++;
			}
		}	
		//Xóa bỏ giá trị null
		Square[] returnValue = new Square[cutNeighborsLength];
		for (int i = 0; i<cutNeighborsLength;i++){
			returnValue[i]=cutNeighbors[i];
		}
		return returnValue;
	}
	private Square[] walledNeighbours(Square square){
		//Trả về tất cả ô vuông kề cạnh và có chung tường với ô vuông được chọn
		Square[] neighbors = new Square[4];
		int numberOfNeighbors = 0;
		if (isWallLeft(square.getxCoord(), square.getyCoord())&&square.getxCoord()-1>=0) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord() - 1,
					square.getyCoord());
			numberOfNeighbors++;
		}
		if (isWallRight(square.getxCoord(), square.getyCoord())&&square.getxCoord()+1<7) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord() + 1,
					square.getyCoord());
			numberOfNeighbors++;
		}
		if (isWallAbove(square.getxCoord(), square.getyCoord())&&square.getyCoord()-1>=0) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord(),
					square.getyCoord() - 1);
			numberOfNeighbors++;
		}
		if (isWallBelow(square.getxCoord(), square.getyCoord())&&square.getyCoord()+1<7) {
			neighbors[numberOfNeighbors] = new Square(square.getxCoord(),
					square.getyCoord() + 1);
			numberOfNeighbors++;
		}
		// Xóa bỏ giá trị null
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = neighbors[i];
			}
			return cutReturnValue;
		} else {
			return neighbors;
		}
	}

	private Square[][] connections() {
		// Trả về danh sách các ô vuông mà mỗi ô vuông kết nối
		Square[][] connections = new Square[49][5];
		for (int i = 0; i < 7; i++) { 
			for (int j = 0; j < 7; j++) { // Tìm hàng xóm của mỗi ô vuông
				connections[i + 7 * j] = neighbors(new Square(i, j));
			}
		}
		return connections;
	}

	private Square[][] mergeAll() {//Trả về danh sách các khu vực tách biệt
		Square[][] set = connections();//Cho danh sách các ô vuông mà mỗi ô vuông kết nối vào một mảng rồi chuyển sang List
		ArrayList<Square[]> list = new ArrayList<Square[]>(49);
		for (int i = 0; i < 49; i++) {
			list.add(set[i]);
		}
		//Hợp các khu vực có chung ô vuông
		for (int i = 0; i<list.size()-1;i++){
			for (int j = (i+1); j<list.size();j++){
				if (intersection(list.get(i), list.get(j)).length != 0) {
					//Nếu kết nối có chung ô vuông thì hợp chúng thành một
					list.set(i, merge(list.get(i), list.get(j)));
					list.remove(j);
					i=0;
					j=0;
				}
			}
		}
		//chuyển tất cả về dạng mảng
		Square[][] returnValue = new Square[list.size()][49];
		for (int i = 0; i < list.size(); i++) {
			returnValue[i]=list.get(i);
		}
		return returnValue;
	}
	
	private boolean[][][] generateEmptyMaze() { //Tạo mê cung rỗng
		boolean[][][] walls = new boolean[2][gridWidth + 1][gridWidth + 1];
		for (int i = 0; i < 7; i++) {
			walls[0][0][i] = true;
			walls[0][7][i] = true;
			walls[1][0][i] = true;
			walls[1][7][i] = true;
		}
		return walls;
	}
	private boolean[][][] generateMaze(int density) { //Thêm tường vào mê cung
		boolean[][][] walls2 = generateEmptyMaze();
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < density; i++) {
				int randomNumber = (int) (Math.random() * (7));
				int randomNumber2 = (int) (Math.random() * (7));
				walls2[j][randomNumber][randomNumber2] = true;
			}
		}
		return walls2;
	}

	public String toString() { //In ra mê cung
		String[] lines = new String[8];
		lines[0] = " ";
		for (int i = 0; i < 7; i++) {
			lines[0] = lines[0] + (walls[0][0][i] ? "_" : " ") + " ";
		}
		for (int i = 1; i < 8; i++) {
			lines[i] = "";
			for (int j = 0; j < 8; j++) {
				lines[i] = lines[i] + (walls[1][j][i - 1] ? "|" : " ")
						+ (walls[0][i][j] ? "_" : " ");
			}
		}
		String toReturn = "";
		for (int i = 0; i < 8; i++) {
			toReturn = toReturn + lines[i] + "\n";
		}
		return toReturn;
	}

	public static void main(String[] args) {
		Maze maze = new Maze(30);
		System.out.print(maze);
	}
}



