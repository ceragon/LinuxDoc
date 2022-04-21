package io.github.ceragon.datastruct.rbtree;

public class RbNode {
    private final static int RB_RED = 0;
    private final static int RB_BLACK = 1;
    private RbNode rbParent;
    private int rbColor;
    RbNode rbRight;
    RbNode rbLeft;

    public void rbSetParent(RbNode p) {
        this.rbParent = p;
    }

    public void rbSetColor(int color) {
        this.rbColor = color;
    }

    public RbNode rbParent() {
        return rbParent;
    }

    public boolean rbIsRed() {
        return rbColor == RB_RED;
    }

    public void rbSetBlack() {
        this.rbColor = RB_BLACK;
    }

    public void rbSetRed() {
        this.rbColor = RB_RED;
    }
}
