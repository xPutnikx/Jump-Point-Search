package v.hudnitsky.testjps.jps2;

/**
 * @author v.hudnitsky
 */
public class Node {
	public int x,y; //x - height, y - width
	float g,h,f;  //g = from start; h = to end, f = both together
	boolean pass;
	Node parent;
	
	public Node(int x,int y){
		this.x = x;
		this.y = y; 
		this.pass = true;
	}
	
	public void updateGHFP(float g, float h, Node parent){
		this.parent = parent;
		this.g = g;
		this.h = h;
		f = g+h;
	}
	
	public boolean setPass(boolean pass){
		this.pass = pass;
		return pass;
	}
}
