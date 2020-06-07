package com.example.swisscom;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;

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
    static ArrayList<String> stringDirections = new ArrayList<String>();
    static String direction;

    @SuppressLint("NewApi")
    public Astar(int rows, int cols, CustomNode initialNode, String productname, int hvCost, int diagonalCost) {
        HashMap<String,CustomNode> products = new HashMap<String , CustomNode>();
        products.put("OPPO Find X2 Pro",new CustomNode(15,13));
        products.put("Samsung Galaxy S20 Ultra 5G",new CustomNode(15,13));
        products.put("Samsung Galaxy S20 5G",new CustomNode(15,13));
        products.put("iPhone SE",new CustomNode(19,2));
        products.put("Apple Watch Series 5 Stainless Steel",new CustomNode(19,2));
        products.put("Apple Watch Series 5 Aluminum",new CustomNode(4,8));
        products.put("Samsung Galaxy Watch Active2 44mm Aluminium",new CustomNode(4,8));

        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialNode(initialNode);
        setFinalNode(products.get(productname));
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

    public Astar(int rows, int cols, CustomNode initialNode, String productname) {
        this(rows, cols, initialNode, productname, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
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

    public void setBlocks(ArrayList<int[]> blocksArray ) {
        for (int i = 0; i < blocksArray.size(); i++) {
            int row = blocksArray.get(i)[0];
            int col = blocksArray.get(i)[1];
            setBlock(row, col);
        }
    }

    public List<CustomNode> findPath(String direction) {
        openList.add(initialNode);
        stringDirections.clear();
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

    public ArrayList<String> getStringDirections(){
        stringDirections.remove("");
        return stringDirections;
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
        int cold = 0;
        int rowd = 0;
        int colDif = 0;
        int rowDif = 0;
        for (int i= 0; i < path.size(); i++) {
            if (i == path.size() - 1) {
                String laststep = getDirections(cold, rowd);
                if(cold == 1 || rowd== 1 || cold == -1 || rowd == -1) {
                    String finallastword = laststep.substring(laststep.lastIndexOf(" ")+1);
                    laststep = "Look to your " + finallastword;
                }
                Astar.stringDirections.add(laststep);
                break;
            }
            colDif = path.get(i).y - path.get(i + 1).y;
            rowDif = path.get(i).x - path.get(i + 1).x;
            if(i == 0){
                String firststep = getDirections(colDif, rowDif);
                String firstlastword = firststep.substring(firststep.lastIndexOf(" ") + 1);
                firststep = "Look to your " + firstlastword;
                Log.v("test",firststep);
                Astar.stringDirections.add(firststep);
            }

            if (colDif > 0){
                if (!(currdirection == "E"))
                {
                    Astar.stringDirections.add(getDirections(cold, rowd));
                    cold=rowd=0;
                }
                cold += colDif;
                currdirection = "E";
            }
            else if (colDif < 0){
                if (!(currdirection == "W"))
                {
                    Astar.stringDirections.add(getDirections(cold, rowd));
                    cold=rowd=0;
                }
                cold += colDif;
                currdirection = "W";
            }
            else if (rowDif > 0){
                if (!(currdirection == "N"))
                {
                    Astar.stringDirections.add(getDirections(cold, rowd));
                    cold=rowd=0;
                }
                rowd += rowDif;
                currdirection = "N";
            }
            else if (rowDif < 0){
                if (!(currdirection == "S"))
                {
                    Astar.stringDirections.add(getDirections(cold, rowd));
                    cold=rowd=0;
                }
                rowd += rowDif;
                currdirection = "S";
            }


        }

        return path;
    }

    public String getDirections(int cold, int rowd) {
        int col = cold;
        int row = rowd;
        if(cold == 0 && rowd == 0){
            return "";
        }
        if(rowd != 0){
            switch(Astar.direction) {
                case "N":
                    if(rowd > 0) {
                        Astar.direction = "N";
                        return("Walk " + String.valueOf(row / 2) + " Steps Forward" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + String.valueOf(row / 2 *-1) + " Steps Backwards" );
                    }

                case "E":
                    if(rowd > 0) {
                        Astar.direction = "N";
                        return("Walk " + String.valueOf(row / 2) + " Steps Left" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + String.valueOf(row / 2 *-1) + " Steps Right" );
                    }

                case "S":
                    if(rowd > 0) {
                        Astar.direction = "N";
                        return("Walk " + String.valueOf(row / 2) + " Steps Backwards" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + String.valueOf(row / 2 *-1) + " Steps Forwards" );
                    }

                case "W":
                    if(rowd > 0) {
                        Astar.direction = "N";
                        return("Walk " + String.valueOf(row / 2) + " Steps Right" );
                    } else {
                        Astar.direction = "S";
                        return("Walk " + String.valueOf(row / 2 *-1) + " Steps Left" );
                    }

            }
        } else {
            switch(Astar.direction) {
                case "N":
                    if(cold > 0) {
                        Astar.direction = "E";
                        return("Walk " + String.valueOf(col / 2) + " Steps Right" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + String.valueOf(col / 2 * -1) + " Steps Left" );
                    }

                case "E":
                    if(cold > 0) {
                        Astar.direction = "E";
                        return("Walk " + String.valueOf(col / 2) + " Steps Forward" );
                    } else {
                        Astar.direction = "W";

                        return("Walk " + String.valueOf(col / 2 * -1) + " Steps Backwards" );
                    }

                case "S":
                    if(cold > 0) {
                        Astar.direction = "E";
                        return("Walk " + String.valueOf(col / 2) + " Steps Left" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + String.valueOf(col / 2 * -1) + " Steps Right" );
                    }

                case "W":
                    if(cold > 0) {
                        Astar.direction = "E";
                        return("Walk " + String.valueOf(col / 2) + " Steps Backwards" );
                    } else {
                        Astar.direction = "W";
                        return("Walk " + String.valueOf(col / 2 * -1) + " Steps Forward" );
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
