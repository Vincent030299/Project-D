package com.example.pathfindingvisualization;

public class CustomNode {
    public CustomNode parent;
    public int hCost;
    public int fCost;
    public boolean included;
    private int g;
    private int f;
    private int h;
    private int x;
    private int y;
    private boolean isBlock;

    public CustomNode(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void calculateHeuristic(CustomNode finalNode) {
        this.h = Math.abs(finalNode.getRow() - getRow()) + Math.abs(finalNode.getCol() - getCol());
    }

    public void setNodeData(CustomNode currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        setParent(currentNode);
        setG(gCost);
        calculateFinalCost();
    }

    public boolean checkBetterPath(CustomNode currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        if (gCost < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getH();
        setF(finalCost);
    }

    @Override
    public boolean equals(Object arg0) {
        CustomNode other = (CustomNode) arg0;
        return this.getRow() == other.getRow() && this.getCol() == other.getCol();
    }

    @Override
    public String toString() {
        return "Node [row=" + x + ", col=" + y + "]";
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public CustomNode getParent() {
        return parent;
    }

    public void setParent(CustomNode parent) {
        this.parent = parent;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }

    public int getRow() {
        return x;
    }

    public void setRow(int row) {
        this.x = row;
    }

    public int getCol() {
        return y;
    }

    public void setCol(int col) {
        this.y = col;
    }

}
