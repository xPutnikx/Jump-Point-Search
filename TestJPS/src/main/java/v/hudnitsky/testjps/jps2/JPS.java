package v.hudnitsky.testjps.jps2;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v.hudnitsky
 */
public class JPS {
    private static JPS jps;
    Grid grid;
    int startX, startY, endX, endY;  //variables for reference grid
    int[] tmpXY;
    int[][] neighbors;
    float ng;
    Node tmpNode, cur;
    Node[] successors, possibleSuccess;
    private float fromFinish;
    private int deep;


    public JPS() {
    }

    /**
     * Orchestrates the Jump Point Search; it is explained further in comments below.
     */
    /**
     * Initializer; sets up variables, creates reference grid and actual grid, gets start and end points, initiates search
     *
     * @param start (Node) start Node
     * @param end   (Node)end Node
     */
    public List<Node> search(Node start, Node end, Grid grid) {
        this.grid = grid;
        this.startX = start.x;   //the start point x value
        this.startY = start.y;      //the start point y value
        this.ng = grid.xMax*32;
        this.fromFinish = grid.xMax*32;
        this.endX = end.x;      //the end point x value
        this.endY = end.y;      //the end point y value

        Node node = grid.getNode(startX, startY);
        int iteration = 0;
        if(node!=null){
            node.updateGHFP(0, 0, null);
            deep = 1;
            grid.heapAdd(grid.getNode(startX, startY));  //Start node is added to the heap
            while (grid.heapSize() > 0) {
                cur = grid.heapPopNode();              //the current node is removed from the heap.
                if (cur.x == endX && cur.y == endY) {        //if the end node is found
                    return grid.pathCreate(cur,deep);    //the path is then created
//                return new ArrayList<>();
                }
                possibleSuccess = identifySuccessors(cur);  //get all possible successors of the current node
                for (int i = 0; i < possibleSuccess.length; i++) {     //for each one of them
                    if (possibleSuccess[i] != null) {                //if it is not null
                        grid.heapAdd(possibleSuccess[i]);        //add it to the heap for later use (a possible future cur)
                    }
                }
                if (grid.heapSize() == 0) {                        //if the grid size is 0, and we have not found our end, the end is unreachable
                    break;                                        //loop is done
                }
                iteration++;
                if(iteration > 50){
                    break;
                }
                Log.d("JPS","ITERATION : "+ iteration);
            }
        }
        return new ArrayList<>();
    }

    /**
     * returns all nodes jumped from given node
     *
     * @param node
     * @return all nodes jumped from given node
     */
    public Node[] identifySuccessors(Node node) {
        successors = new Node[8];                //empty successors list to be returned
        neighbors = getNeighborsPrune(node);    //all neighbors after pruned //TODO ERROR 8 1
        for (int i = 0; i < neighbors.length; i++) { //for each of these neighbors
            tmpXY = jump(neighbors[i][0], neighbors[i][1], node.x, node.y); //get next jump point
            if (tmpXY[0] != -1) {                                //if that point is not null( {-1,-1} )
                int x = tmpXY[0];
                int y = tmpXY[1];
                double d = Heuristic.euclidean(Math.abs(x - node.x), Math.abs(y - node.y));
                ng = (float) (node.g + d);
                if (grid.getNode(x, y).f <= 0 || ng < grid.getNode(x, y).g || grid.getNode(x, y).h < fromFinish) {  //if this node is not already found, or we have a shorter distance from the current node
                    grid.getNode(x, y).g = ng;   /*get the distance from start*/
                    fromFinish = grid.getNode(x, y).h;
                    grid.getNode(x, y).updateGHFP(grid.toPointApprox(x, y, node.x, node.y) + node.g, grid.toPointApprox(x, y, endX, endY), node); //then update the rest of it
                    deep++;
                    successors[i] = grid.getNode(x, y);  //add this node to the successors list to be returned
                }
            }
        }
        return successors;  //finally, successors is returned
    }

    /**
     * jump method recursively searches in the direction of parent (px,py) to child, the current node (x,y).
     * It will stop and return its current position in three situations:
     * <p/>
     * 1) The current node is the end node. (endX, endY)
     * 2) The current node is a forced neighbor.
     * 3) The current node is an intermediate step to a node that satisfies either 1) or 2)
     *
     * @param x  (int) current node's x
     * @param y  (int) current node's y
     * @param px (int) current.parent's x
     * @param py (int) current.parent's y
     * @return (int[]={x, y}) node which satisfies one of the conditions above, or null if no such node is found.
     */
    public int[] jump(int x, int y, int px, int py) {
        int[] jx = {-1, -1}; //used to later check if full or null
        int[] jy = {-1, -1}; //used to later check if full or null
        int dx = (x - px) / Math.max(Math.abs(x - px), 1); //because parents aren't always adjacent, this is used to find parent -> child direction (for x)
        int dy = (y - py) / Math.max(Math.abs(y - py), 1); //because parents aren't always adjacent, this is used to find parent -> child direction (for y)

        if (!grid.walkable(x, y)) { //if this space is not grid.walkable, return a null.
            return tmpInt(-1, -1); //in this system, returning a {-1,-1} equates to a null and is ignored.
        }
        if (x == this.endX && y == this.endY) {   //if end point, return that point. The search is over! Have a beer.
            return tmpInt(x, y);
        }
        if (dx != 0 && dy != 0) {  //if x and y both changed, we are on a diagonally adjacent square: here we check for forced neighbors on diagonals
            if ((grid.walkable(x - dx, y + dy) && !grid.walkable(x - dx, y)) || //we are moving diagonally, we don't check the parent, or our next diagonal step, but the other diagonals
                    (grid.walkable(x + dx, y - dy) && !grid.walkable(x, y - dy))) {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
                return tmpInt(x, y);
            }
        } else { //check for horizontal/vertical
            if (dx != 0) { //moving along x
                if ((grid.walkable(x + dx, y + 1) && !grid.walkable(x, y + 1)) || //we are moving along the x axis
                        (grid.walkable(x + dx, y - 1) && !grid.walkable(x, y - 1))) {  //we check our side nodes to see if they are forced neighbors
                    return tmpInt(x, y);
                }
            } else {
                if ((grid.walkable(x + 1, y + dy) && !grid.walkable(x + 1, y)) ||  //we are moving along the y axis
                        (grid.walkable(x - 1, y + dy) && !grid.walkable(x - 1, y))) {     //we check our side nodes to see if they are forced neighbors
                    return tmpInt(x, y);
                }
            }
        }

        if (dx != 0 && dy != 0) { //when moving diagonally, must check for vertical/horizontal jump points
            jx = jump(x + dx, y, x, y);
            jy = jump(x, y + dy, x, y);
            if (jx[0] != -1 || jy[0] != -1) {
                return tmpInt(x, y);
            }
        }
        if (grid.walkable(x + dx, y) || grid.walkable(x, y + dy)) { //moving diagonally, must make sure one of the vertical/horizontal neighbors is open to allow the path
            return jump(x + dx, y + dy, x, y);
        } else { //if we are trying to move diagonally but we are blocked by two touching corners of adjacent nodes, we return a null
            return tmpInt(-1, -1);
        }
    }

    /**
     * Encapsulates x,y in an int[] for returning. A helper method for the jump method
     *
     * @param x (int) point's x coordinate
     * @param y (int) point's y coordinate
     * @return ([]int) bundled x,y
     */
    public int[] tmpInt(int x, int y) {
        int[] tmpIntsTmpInt = {x, y};  //create the tmpInt's tmpInt[]
        return tmpIntsTmpInt;         //return it
    }

    /**
     * Returns nodes that should be jumped based on the parent location in relation to the given node.
     *
     * @param node (Node) node which has a parent (not the start node)
     * @return (ArrayList<Node>) list of nodes that will be jumped
     */
    public int[][] getNeighborsPrune(Node node) {
        Node parent = node.parent;    //the parent node is retrieved for x,y values
        int h = node.x;
        int w = node.y;
        int px, py, dx, dy;
        int[][] neighbors = new int[5][2];
        //directed pruning: can ignore most neighbors, unless forced
        if (parent != null) {
            px = parent.x;
            py = parent.y;
            //get the normalized direction of travel
            dx = (h - px) / Math.max(Math.abs(h - px), 1);
            dy = (w - py) / Math.max(Math.abs(w - py), 1);
            //search diagonally
            if (dx != 0 && dy != 0) {
                if (grid.walkable(h, w + dy)) {
                    neighbors[0] = (tmpInt(h, w + dy));
                }
                if (grid.walkable(h + dx, w)) {
                    neighbors[1] = (tmpInt(h + dx, w));
                }
                if (grid.walkable(h, w + dy) || grid.walkable(h + dx, w)) {
                    neighbors[2] = (tmpInt(h + dx, w + dy));
                }
                if (!grid.walkable(h - dx, w) && grid.walkable(h, w + dy)) {
                    neighbors[3] = (tmpInt(h - dx, w + dy));
                }
                if (!grid.walkable(h, w - dy) && grid.walkable(h + dx, w)) {
                    neighbors[4] = (tmpInt(h + dx, w - dy));
                }
            } else {
                if (dx == 0) {
                    if (grid.walkable(h, w + dy)) {
                        if (grid.walkable(h, w + dy)) {
                            neighbors[0] = (tmpInt(h, w + dy));
                        }
                        if (!grid.walkable(h + 1, w)) {
                            neighbors[1] = (tmpInt(h + 1, w + dy));
                        }
                        if (!grid.walkable(h - 1, w)) {
                            neighbors[2] = (tmpInt(h - 1, w + dy));
                        }
                    }
                } else {
                    if (grid.walkable(h + dx, w)) {
                        if (grid.walkable(h + dx, w)) {
                            neighbors[0] = (tmpInt(h + dx, w));
                        }
                        if (!grid.walkable(h, w + 1)) {
                            neighbors[1] = (tmpInt(h + dx, w + 1));
                        }
                        if (!grid.walkable(h, w - 1)) {
                            neighbors[2] = (tmpInt(h + dx, w - 1));
                        }
                    }
                }
            }
        } else {//return all neighbors
            return grid.getNeighbors(node); //adds initial nodes to be jumped from!
        }

        return neighbors; //this returns the neighbors, you know
    }
}
