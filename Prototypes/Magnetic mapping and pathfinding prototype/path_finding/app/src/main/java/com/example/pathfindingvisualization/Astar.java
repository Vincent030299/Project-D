package com.example.pathfindingvisualization;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Astar {
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private int hvCost;
    private int diagonalCost;
    private CustomNode[][] searchArea;
    private PriorityQueue<CustomNode> openList;
    private Set<CustomNode> closedSet;
    private CustomNode initialNode;
    private CustomNode finalNode;
    static String direction;

    public Astar(int rows, int cols, CustomNode initialNode, CustomNode finalNode, int hvCost, int diagonalCost) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialNode(initialNode);
        setFinalNode(finalNode);
        this.searchArea = new CustomNode[rows][cols];
        this.openList = new PriorityQueue<CustomNode>(new Comparator<CustomNode>() {
            @Override
            public int compare(CustomNode node0, CustomNode node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        setNodes();
        this.closedSet = new HashSet<>();
    }

    public Astar(int rows, int cols, CustomNode initialNode, CustomNode finalNode) {
        this(rows, cols, initialNode, finalNode, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
    }

    private void setNodes() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                CustomNode node = new CustomNode(i, j);
                node.calculateHeuristic(getFinalNode());
                this.searchArea[i][j] = node;
            }
        }
    }

    public void setBlocks(int[][] blocksArray) {
        for (int i = 0; i < blocksArray.length; i++) {
            int row = blocksArray[i][0];
            int col = blocksArray[i][1];
            setBlock(row, col);
        }
    }

    public List<CustomNode> findPath(String direction) {
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            CustomNode currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode,direction);
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return new ArrayList<CustomNode>();
    }

    private List<CustomNode> getPath(CustomNode currentNode, String startdirection) {
        List<CustomNode> path = new ArrayList<CustomNode>();
        path.add(currentNode);
        CustomNode parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            currentNode = parent;
        }
        Astar.direction = startdirection;
        String currdirection  = "";
        int xmeter = 0;
        int ymeter = 0;
        for (int i= 0; i < path.size(); i++) {
            if (i == path.size() - 1) {
                Log.d("test",getDirections(xmeter, ymeter));
                break;
            }
            int xDif = path.get(i).x - path.get(i + 1).x;
            int yDif = path.get(i).y - path.get(i+1).y;

            if (xDif > 0){
                if (!(currdirection == "E"))
                {
                    Log.d("test",getDirections(xmeter, ymeter));
                    xmeter=ymeter=0;
                }
                xmeter += xDif;
                currdirection = "E";
            }
            else if (xDif < 0){
                if (!(currdirection == "W"))
                {
                    Log.d("test",getDirections(xmeter, ymeter));
                    xmeter=ymeter=0;
                }
                xmeter += xDif;
                currdirection = "W";
            }
            else if (yDif > 0){
                if (!(currdirection == "N"))
                {
                    Log.d("test",getDirections(xmeter, ymeter));
                    xmeter=ymeter=0;
                }
                ymeter += yDif;
                currdirection = "N";
            }
            else if (yDif < 0){
                if (!(currdirection == "S"))
                {
                    Log.d("test",getDirections(xmeter, ymeter));
                    xmeter=ymeter=0;
                }
                ymeter += yDif;
                currdirection = "S";
            }


        }

        return path;
    }

    public String getDirections(int xmeter, int ymeter) {
        if(xmeter == 0 && ymeter == 0){
            return "";
        }
        if(xmeter == 0){
            switch(Astar.direction) {
                case "N":
                    if(ymeter > 0) {
                        Astar.direction = "N";
                        return("Walk " + Integer.toString(ymeter) + "Steps Forward" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + Integer.toString(ymeter *-1) + "Steps Backwards" );
                    }

                case "E":
                    if(ymeter > 0) {
                        Astar.direction = "N";
                        return("Walk " + Integer.toString(ymeter) + "Steps Left" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + Integer.toString(ymeter * -1) + "Steps Right" );
                    }

                case "S":
                    if(ymeter > 0) {
                        Astar.direction = "N";
                        return("Walk " + Integer.toString(ymeter) + "Steps Backwards" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + Integer.toString(ymeter * -1) + "Steps Forwards" );
                    }

                case "W":
                    if(ymeter > 0) {
                        Astar.direction = "N";
                        return("Walk " + Integer.toString(ymeter) + "Steps Right" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + Integer.toString(ymeter * -1) + "Steps Left" );
                    }

            }
        } else {
            switch(Astar.direction) {
                case "N":
                    if(xmeter > 0) {
                        Astar.direction = "O";
                        return("Walk " + Integer.toString(xmeter) + "Steps Right" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + Integer.toString(xmeter* -1) + "Steps Left" );
                    }

                case "E":
                    if(xmeter > 0) {
                        Astar.direction = "O";
                        return("Walk " + Integer.toString(xmeter) + "Steps Forward" );
                    } else {
                        Astar.direction = "W";

                        return("Walk " + Integer.toString(xmeter *-1) + "Steps Backwards" );
                    }

                case "S":
                    if(xmeter > 0) {
                        Astar.direction = "O";
                        return("Walk " + Integer.toString(xmeter) + "Steps Left" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + Integer.toString(xmeter * -1) + "Steps Right" );
                    }

                case "W":
                    if(xmeter > 0) {
                        Astar.direction = "O";
                        return("Walk " + Integer.toString(xmeter) + "Steps Backwards" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + Integer.toString(xmeter * -1) + "Steps Forward" );
                    }


            }

        }
        return"";
    }

    private void addAdjacentNodes(CustomNode currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }

    private void addAdjacentLowerRow(CustomNode currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
            if (col - 1 >= 0) {
                //checkNode(currentNode, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                //checkNode(currentNode, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            checkNode(currentNode, col, lowerRow, getHvCost());
        }
    }

    private void addAdjacentMiddleRow(CustomNode currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= 0) {
            checkNode(currentNode, col - 1, middleRow, getHvCost());
        }
        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, getHvCost());
        }
    }

    private void addAdjacentUpperRow(CustomNode currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= 0) {
            if (col - 1 >= 0) {
                //checkNode(currentNode, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                //checkNode(currentNode, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            checkNode(currentNode, col, upperRow, getHvCost());
        }
    }

    private void checkNode(CustomNode currentNode, int col, int row, int cost) {
        CustomNode adjacentNode = getSearchArea()[row][col];
        if (!adjacentNode.isBlock() && !getClosedSet().contains(adjacentNode)) {
            if (!getOpenList().contains(adjacentNode)) {
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }

    private boolean isFinalNode(CustomNode currentNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isEmpty(PriorityQueue<CustomNode> openList) {
        return openList.size() == 0;
    }

    private void setBlock(int row, int col) {
        this.searchArea[row][col].setBlock(true);
    }

    public CustomNode getInitialNode() {
        return initialNode;
    }

    public void setInitialNode(CustomNode initialNode) {
        this.initialNode = initialNode;
    }

    public CustomNode getFinalNode() {
        return finalNode;
    }

    public void setFinalNode(CustomNode finalNode) {
        this.finalNode = finalNode;
    }

    public CustomNode[][] getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(CustomNode[][] searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<CustomNode> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<CustomNode> openList) {
        this.openList = openList;
    }

    public Set<CustomNode> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<CustomNode> closedSet) {
        this.closedSet = closedSet;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    private int getDiagonalCost() {
        return diagonalCost;
    }

    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }


}
