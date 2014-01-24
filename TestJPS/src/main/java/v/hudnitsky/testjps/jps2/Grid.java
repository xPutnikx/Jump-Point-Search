package v.hudnitsky.testjps.jps2;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Holds a Node[][] 2d array "grid" for path-finding tests, all drawing is done through here.
 * @author Clint Mullins, v.hudnitsky
 *
 */
public class Grid {
    private final int[][] matrix;
    private Node[][] grid;
	public int xMax, yMax;
	private Heap heap;
	
	/**
	 * Grid is created, Land is generated in either uniform or random fashion, landscape 'Map' is created in printed.
	 * 
	 * 
	 * @param xMax - (int) maximum x coordinate 
	 * @param yMax - (int) maximum y coordinate
	 */
	public Grid(int xMax, int yMax, int[][] matrix){
		this.xMax = xMax;
		this.yMax = yMax;
        this.matrix = matrix;
		grid = new Node[this.yMax][this.xMax];
		landGenerator();
		heap = new Heap();
	}
	
	/**
	 * This is the constuctor used for comparison. It can be passed an entire Node[][] grid.
	 * 
	 * 
	 * @param xMax - (int) maximum x coordinate 
	 * @param yMax - (int) maximum y coordinate
	 * @param grid (Node[][]) an entire grid is passed through for comparison
	 */
	public Grid(int xMax, int yMax, Node[][] grid, int[][] matrix){
		this.xMax = xMax;
		this.yMax = yMax;
		this.grid = grid;
        this.matrix = matrix;
		heap = new Heap();
	}
	
	
	/**
	 * returns all adjacent nodes that can be traversed
	 * 
	 * @param node (Node) finds the neighbors of this node
	 * @return (int[][]) list of neighbors that can be traversed
	 */
	public int[][] getNeighbors(Node node){
		int[][] neighbors = new int[8][2];
		int x = node.x;
		int y = node.y;
		boolean d0 = false; //These booleans are for speeding up the adding of nodes.
		boolean d1 = false;
		boolean d2 = false;
		boolean d3 = false;
		
		if (walkable(x,y-1)){
			neighbors[0] = (tmpInt(x,y-1));
			d0 = d1 = true;
		}
		if (walkable(x+1,y)){
			neighbors[1] = (tmpInt(x+1,y));
			d1 = d2 = true;
		}
		if (walkable(x,y+1)){
			neighbors[2] = (tmpInt(x,y+1));
			d2 = d3 = true;
		}
		if (walkable(x-1,y)){
			neighbors[3] = (tmpInt(x-1,y));
			d3 = d0 = true;
		}
		if (d0 && walkable(x-1,y-1)){
			neighbors[4] = (tmpInt(x-1,y-1));
		}
		if (d1 && walkable(x+1,y-1)){
			neighbors[5] = (tmpInt(x+1,y-1));
		}
		if (d2 && walkable(x+1,y+1)){
			neighbors[6] = (tmpInt(x+1,y+1));
		}
		if (d3 && walkable(x-1,y+1)){
			neighbors[7] = (tmpInt(x-1,y+1));
		}
		return neighbors;
	}

	/**
	 * Tests an x,y node's passability 
	 * 
	 * @param x (int) node's x coordinate
	 * @param y (int) node's y coordinate
	 * @return (boolean) true if the node is obstacle free and on the map, false otherwise
	 */
	public boolean setWalkable(int x, int y){
		return matrix[x][y] == 0;
	}

    public boolean walkable(int x, int y){
        try{
            if(x > -1 && x < grid.length && y > -1 && y < grid.length){
                return getNode(x,y).pass;
            }else{
                return false;
            }
        }
        catch (Exception e){
            return false;
        }
	}
	
	public List<Node> pathCreate(Node node,int deep){
        deep = checkParent(node);
        int lost = deep - 2;
        Node[] nodes = new Node[deep-1];
        int arrayIter = 0;
		while (node.parent!=null){
            nodes[lost] = new Node(node.x,node.y);
            if(node.parent==null){
                break;
            }
            node = node.parent;
            lost--;
            arrayIter++;
            Log.d("JPS", "ARRAY ITERATION : " + arrayIter);
        }
        return Arrays.asList(nodes);
	}

    private int checkParent(Node node){
        Node tmpNode = node;
        int deep = 1;
        while(tmpNode.parent!=null){
            deep++;
            tmpNode = tmpNode.parent;
        }
        return deep;
    }

	/**
	 * Adds a node's (x,y,f) to the heap. The heap is sorted by 'f'.
	 * 
	 * @param node (Node) node to be added to the heap
	 */
	public void heapAdd(Node node){
		float[] tmp = {node.x,node.y,node.f};
		heap.add(tmp);
	}
	
	/**
	 * @return (int) size of the heap
	 */
	public int heapSize(){
		return heap.getSize();
	}
	/**
	 * @return (Node) takes data from popped float[] and returns the correct node
	 */
	public Node heapPopNode(){
		float[] tmp = heap.pop();
		return getNode((int)tmp[0],(int)tmp[1]);
	}

	/**
	 * Generates land based on a formula. Land forms like a checkered pattern.
	 */
	public void landGenerator(){
		for (int i=0; i<this.yMax; i++){
			for (int j=0; j<this.xMax; j++){
				grid[i][j] = new Node(i,j);
                grid[i][j].setPass(setWalkable(i,j));
			}
		}
	}

	/**
	 * Encapsulates x,y in an int[] for returning. A helper method for the jump method
	 * 
	 * @param x (int) point's x coordinate
	 * @param y (int) point's y coordinate
	 * @return ([]int) bundled x,y
	 */
	public int[] tmpInt (int x, int y){
		int[] tmpIntsTmpInt = {x,y};  //create the tmpInt's tmpInt[]
		return tmpIntsTmpInt;         //return it
	}
	
	/**
	 * getFunc - Node at given x, y
	 * 
	 * @param x (int) desired node x coordinate
	 * @param y (int) desired node y coordinate
	 * @return (Node) desired node
	 */
	public Node getNode(int x, int y){
		try{
            if(x > -1 && x<grid.length && y>-1 && y<grid.length){
                return grid[x][y];
            }else{
                return null;
            }
		}
		catch(Exception e){
			return null;
		}
	}
	
	public float toPointApprox(float x, float y, int tx, int ty){
		return (float) Math.sqrt(Math.pow(Math.abs(x-tx),2) + Math.pow(Math.abs(y-ty), 2));		
	}
}

