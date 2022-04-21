package io.github.ceragon.datastruct.rbtree;

public class RbRoot {
    private RbNode rbNode;

    public void rbEraseColor(RbNode node, RbNode parent) {

    }

    public void rbInsertColor(RbNode node) {
        RbNode parent, gparent;
        while ((parent = node.rbParent()) != null && parent.rbIsRed()) {
            gparent = parent.rbParent();
            if (parent == gparent.rbLeft) {
                RbNode uncle = gparent.rbRight;
                if (uncle != null && uncle.rbIsRed()) {
                    uncle.rbSetBlack();
                    parent.rbSetBlack();
                    gparent.rbSetRed();
                    node = gparent;
                    continue;
                }
                if (parent.rbRight == node) {
                    RbNode tmp;
                    rbRotateLeft(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }
                parent.rbSetBlack();
                gparent.rbSetRed();
                rbRotateRight(gparent);
            } else {
                RbNode uncle = gparent.rbLeft;
                if (uncle != null && uncle.rbIsRed()) {
                    uncle.rbSetBlack();
                    parent.rbSetBlack();
                    gparent.rbSetRed();
                    node = gparent;
                    continue;
                }
                if (parent.rbLeft == node) {
                    RbNode tmp;
                    rbRotateRight(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }
                parent.rbSetBlack();
                gparent.rbSetRed();
                rbRotateLeft(gparent);
            }
        }
        rbNode.rbSetBlack();
    }

    private void rbRotateLeft(RbNode node) {
        RbNode right = node.rbRight;
        RbNode parent = node.rbParent();
        if ((node.rbRight = right.rbLeft) != null) {
            right.rbLeft.rbSetParent(node);
        }
        right.rbLeft = node;
        right.rbSetParent(parent);

        if (parent != null) {
            if (node == parent.rbLeft) {
                parent.rbLeft = right;
            } else {
                parent.rbRight = right;
            }
        } else {
            rbNode = right;
        }
        node.rbSetParent(right);
    }

    private void rbRotateRight(RbNode node) {
        RbNode left = node.rbLeft;
        RbNode parent = node.rbParent();
        if ((node.rbLeft = left.rbRight) != null) {
            left.rbRight.rbSetParent(node);
        }
        left.rbRight = node;

        left.rbSetParent(parent);

        if (parent != null) {
            if (node == parent.rbRight) {
                parent.rbRight = left;
            } else {
                parent.rbLeft = left;
            }
        } else {
            rbNode = left;
        }
        node.rbSetParent(left);
    }
}
