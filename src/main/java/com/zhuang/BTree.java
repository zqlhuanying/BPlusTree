package com.zhuang;

/**
 * Created by Administrator on 2014/12/3.
 */
public class BTree {
    private int m = 4;    //B树的阶数，则关键字数范围[[m/2]-1,m-1]
    private INode root;

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public INode getRoot() {
        return root;
    }

    public void setRoot(INode root) {
        this.root = root;
    }

    public void setRoot(int m, INode root) {
        this.m = m;
        this.root = root;
    }

    public void insert(int key) {
        INode cur = get(key);
        //insert
        int index = getKeyInTree(cur, key);
        cur.keys.add(index, key);
        updateI(cur);
    }

    public void delete(int key) {
        if (!exist(key)) {
            System.out.println("The Key " + key + " can not found.");
        } else {
            INode cur = get(key);
            //delete
            int index = cur.keys.indexOf(key);
            cur.keys.remove(index);
            updateD(cur);
        }
    }

    //定位key所在的节点
    private INode get(int key) {
        INode search = root;

        if (search == null) return null;

        //定位key所在的叶子节点
        while (true) {
            if (search.isLeaf) break;
            int index = getKeyInTree(search, key);
            search = search.childNodes.get(index);
        }

        return search;
    }

    //key是否存在
    public Boolean exist(int key) {
        INode search = get(key);
        if (search == null) return false;
        for (int temp : search.keys) {
            if (temp == key) return true;
        }
        return false;
    }

    //返回key在节点中的index,如3在节点1,5,9的index为1
    private int getKeyInTree(INode search, int key) {
        for (int i = 0; i < search.keys.size(); i++) {
            if (key < search.keys.get(i)) return i;
        }
        return search.keys.size();
    }

    //返回key在节点中的index，key必须存在
    private int getKeyLocation(INode search, int key) {
        if (search == null) return -1;
        return search.keys.indexOf(key);
    }

    //返回节点在父节点中的index，节点必须存在
    private int getNodeLocation(INode search, INode node) {
        if (search == null) return -1;
        return search.childNodes.indexOf(node);
    }

    private void updateI(INode current) {
        int max = getM() - 1;    //最大关键字数
        int min = ((int) Math.ceil(getM() / 2)) - 1;   //最小关键字数

        TreeLeaf treeLeaf = null;
        INode iNode = null;
        INode newINode = null;
        Integer key = null;
        //对当前节点进行调整
        if (current.keys.size() > max) {
            if (current.isLeaf == true) {
                treeLeaf = new TreeLeaf();
                treeLeaf.isLeaf = true;
                treeLeaf.parent = current.parent;
                treeLeaf.rightBrother = ((TreeLeaf) current).rightBrother;
                ((TreeLeaf) current).rightBrother = treeLeaf;

                for (int i = min + 1; i < current.keys.size(); i++) {
                    treeLeaf.keys.add(current.keys.get(i));
                }

                for (int i = current.keys.size() - 1; i > min; i--) {
                    current.keys.remove(i);
                }
                key = treeLeaf.keys.get(0);
                newINode = treeLeaf;
            } else {
                iNode = new INode();
                iNode.isLeaf = false;
                iNode.parent = current.parent;

                for (int i = min + 1; i < current.keys.size(); i++) {
                    iNode.keys.add(current.keys.get(i));
                    iNode.childNodes.add(current.childNodes.get(i + 1));
                    current.childNodes.get(i + 1).parent = iNode;
                }

                for (int i = current.keys.size() - 1; i > min; i--) {
                    current.keys.remove(i);
                    current.childNodes.remove(i + 1);
                }
                key = iNode.keys.get(0);
                iNode.keys.remove(0);
                newINode = iNode;
            }

            //对父节点进行调整
            INode curParent = current.parent;
            //当根进行分裂时
            if (curParent == null) {
                INode theRootNode = new INode();
                theRootNode.keys.add(key);
                theRootNode.childNodes.add(current);
                theRootNode.childNodes.add(newINode);
                current.parent = theRootNode;
                newINode.parent = theRootNode;
                root = theRootNode;
                return;
            } else {
                curParent.keys.add(getKeyInTree(curParent, key), key);
                curParent.childNodes.add(getKeyInTree(curParent, key), newINode);
                updateI(curParent);
            }
        } else {
            //否则不需要调整
            return;
        }
    }

    private void updateD(INode current) {
        int min = ((int) Math.ceil(getM() / 2)) - 1;   //最小关键字数

        if (current.keys.size() < min) {
            INode curParent = current.parent;
            int nodeIndex = getNodeLocation(curParent, current);
            INode leftSib = null;
            INode rightSib = null;
            if (nodeIndex > 0) leftSib = curParent.childNodes.get(nodeIndex - 1);
            if (nodeIndex < curParent.childNodes.size() - 1) rightSib = curParent.childNodes.get(nodeIndex + 1);
            if (leftSib != null && leftSib.keys.size() > min) {
                if (current.isLeaf) {
                    current.keys.add(0, leftSib.keys.get(leftSib.keys.size() - 1));
                    //leftSib.keys.remove(leftSib.keys.size() - 1);
                    curParent.keys.set(nodeIndex - 1, current.keys.get(0));
                } else {
                    current.keys.add(0, curParent.keys.get(nodeIndex - 1));
                    curParent.keys.set(nodeIndex - 1, leftSib.keys.get(leftSib.keys.size() - 1));
                    //leftSib.keys.remove(leftSib.keys.size() - 1);
                    current.childNodes.add(0, leftSib.childNodes.get(leftSib.childNodes.size() - 1));
                    leftSib.childNodes.get(leftSib.childNodes.size() - 1).parent = current;
                    leftSib.childNodes.remove(leftSib.childNodes.size() - 1);
                }
                leftSib.keys.remove(leftSib.keys.size() - 1);
                return;
            }
            if (rightSib != null && rightSib.keys.size() > min) {
                if (current.isLeaf) {
                    current.keys.add(rightSib.keys.get(0));
                    //rightSib.keys.remove(0);
                    curParent.keys.set(nodeIndex, rightSib.keys.get(1));
                } else {
                    current.keys.add(curParent.keys.get(nodeIndex));
                    curParent.keys.set(nodeIndex, rightSib.keys.get(0));
                    //rightSib.keys.remove(0);
                    current.childNodes.add(rightSib.childNodes.get(0));
                    rightSib.childNodes.get(0).parent = current;
                    rightSib.childNodes.remove(0);
                }
                rightSib.keys.remove(0);
                return;
            }
            if (leftSib == null || leftSib.keys.size() <= min || rightSib == null || rightSib.keys.size() <= min) {
                if (leftSib != null) {
                    if (current.isLeaf) {
                        for (int key : current.keys) {
                            leftSib.keys.add(key);
                        }
                        ((TreeLeaf) leftSib).rightBrother = ((TreeLeaf) current).rightBrother;
                        if (curParent != null) {
                            curParent.childNodes.remove(nodeIndex);
                            curParent.keys.remove(nodeIndex - 1);
                            current = null;
                            //if(curParent == root && curParent.keys.size() <= 0) root = leftSib;
                            //else updateD(curParent);
                        }
                    } else {
                        if (curParent != null) {
                            leftSib.keys.add(curParent.keys.get(nodeIndex - 1));
                            for (int key : current.keys) {
                                leftSib.keys.add(key);
                            }
                            for (INode node : current.childNodes) {
                                leftSib.childNodes.add(node);
                                node.parent = leftSib;
                            }
                            curParent.keys.remove(nodeIndex - 1);
                            curParent.childNodes.remove(nodeIndex);
                            current = null;
                            //if(curParent == root && curParent.keys.size() <= 0) root = leftSib;
                            //else updateD(curParent);
                        }
                    }
                    if (curParent == root && curParent.keys.size() <= 0) root = leftSib;
                    else updateD(curParent);
                    return;
                }
                if (rightSib != null) {
                    if (current.isLeaf) {
                        for (int key : rightSib.keys) {
                            current.keys.add(key);
                        }
                        ((TreeLeaf) current).rightBrother = ((TreeLeaf) rightSib).rightBrother;
                        if (curParent != null) {
                            curParent.childNodes.remove(nodeIndex + 1);
                            curParent.keys.remove(nodeIndex);
                            rightSib = null;
                            //if(curParent == root && curParent.keys.size() <= 0) root = current;
                            //else updateD(curParent);
                        }
                    } else {
                        if (curParent != null) {
                            current.keys.add(curParent.keys.get(nodeIndex));
                            for (int key : rightSib.keys) {
                                current.keys.add(key);
                            }
                            for (INode node : rightSib.childNodes) {
                                current.childNodes.add(node);
                                node.parent = current;
                            }
                            curParent.childNodes.remove(nodeIndex + 1);
                            curParent.keys.remove(nodeIndex);
                            rightSib = null;
                            //if(curParent == root && curParent.keys.size() <= 0) root = current;
                            //else updateD(curParent);
                        }
                    }
                    if (curParent == root && curParent.keys.size() <= 0) root = current;
                    else updateD(curParent);
                    return;
                }
            }
        } else {
            //否则无需调整
            return;
        }
    }

    /*private void updateD(INode current) {
        int min = ((int) Math.ceil(getM() / 2)) - 1;   //最小关键字数

        if (current.keys.size() < min) {
            INode curParent = current.parent;
            int nodeIndex = getNodeLocation(curParent,current);
            INode leftSib = null;
            INode rightSib = null;
            if(nodeIndex > 0) leftSib = curParent.childNodes.get(nodeIndex - 1);
            if(nodeIndex < curParent.childNodes.size() - 1) rightSib= curParent.childNodes.get(nodeIndex + 1);
            if(current.isLeaf){
                TreeLeaf leftSib1 = (TreeLeaf)leftSib;
                TreeLeaf rightSib1 = (TreeLeaf)rightSib;
                TreeLeaf current1 = (TreeLeaf)current;
                if (leftSib1 != null && leftSib1.keys.size() > min){
                    current1.keys.add(0,leftSib1.keys.get(leftSib1.keys.size() - 1));
                    leftSib1.keys.remove(leftSib1.keys.size() - 1);
                    if(curParent != null)curParent.keys.set(nodeIndex - 1,current1.keys.get(0));
                    return;
                }
                if (rightSib1 != null && rightSib1.keys.size() > min){
                    current1.keys.add(rightSib1.keys.get(0));
                    rightSib1.keys.remove(0);
                    if(curParent != null)curParent.keys.set(nodeIndex,rightSib1.keys.get(0));
                    return;
                }
                if(leftSib1 == null || leftSib1.keys.size() <= min || rightSib1 == null || rightSib1.keys.size() <= min){
                    //合并节点
                    if(leftSib1 != null){
                        for(int key : current1.keys){
                            leftSib1.keys.add(key);
                        }
                        leftSib1.rightBrother = current1.rightBrother;
                        if(curParent != null){
                            curParent.childNodes.remove(nodeIndex);
                            curParent.keys.remove(nodeIndex - 1);
                            current1 = null;
                            if(curParent == root && curParent.keys.size() <= 0) root = leftSib1;
                            else updateD(curParent);
                        }
                        return;
                    }
                    if(rightSib1 != null){
                        for(int key : rightSib1.keys){
                            current1.keys.add(key);
                        }
                        current1.rightBrother = rightSib1.rightBrother;
                        if(curParent != null){
                            curParent.childNodes.set(nodeIndex + 1,curParent.childNodes.get(nodeIndex));
                            curParent.childNodes.remove(nodeIndex);
                            curParent.keys.remove(nodeIndex);
                            rightSib1 = null;
                            if(curParent == root && curParent.keys.size() <= 0) root = current;
                            else updateD(curParent);
                        }
                        return;
                    }
                }
            }else {
                if (leftSib != null && leftSib.keys.size() > min){
                    current.keys.add(0,curParent.keys.get(nodeIndex));
                    curParent.keys.set(nodeIndex,leftSib.keys.get(leftSib.keys.size() - 1));
                    leftSib.keys.remove(leftSib.keys.size());
                    current.childNodes.add(0,leftSib.childNodes.get(leftSib.childNodes.size() - 1));
                    leftSib.childNodes.remove(leftSib.childNodes.size() - 1);
                    return;
                }
                if (rightSib != null && rightSib.keys.size() > min){
                    current.keys.add(curParent.keys.get(nodeIndex));
                    curParent.keys.set(nodeIndex,rightSib.keys.get(0));
                    rightSib.keys.remove(0);
                    current.childNodes.add(rightSib.childNodes.get(0));
                    rightSib.childNodes.remove(0);
                    return;
                }
                if(leftSib == null || leftSib.keys.size() <= min || rightSib == null || rightSib.keys.size() <= min){
                    if(leftSib != null){
                        if(curParent != null){
                            leftSib.keys.add(curParent.keys.get(nodeIndex));
                            for(int key : current.keys){
                                leftSib.keys.add(key);
                            }
                            for(INode node : current.childNodes){
                                leftSib.childNodes.add(node);
                            }
                            curParent.keys.remove(nodeIndex);
                            curParent.childNodes.remove(nodeIndex + 1);
                            current = null;
                            if(curParent == root && curParent.keys.size() <= 0) root = leftSib;
                            else updateD(curParent);
                        }
                        return;
                    }
                    if(rightSib != null){
                        if(curParent != null){
                            current.keys.add(curParent.keys.get(nodeIndex));
                            for(int key : rightSib.keys){
                                current.keys.add(key);
                            }
                            for(INode node : rightSib.childNodes){
                                current.childNodes.add(node);
                            }
                            curParent.keys.remove(nodeIndex);
                            curParent.childNodes.remove(nodeIndex + 1);
                            rightSib = null;
                            if(curParent == root && curParent.keys.size() <= 0) root = current;
                            else updateD(curParent);
                        }
                        return;
                    }
                }
            }
        } else {
            //否则无需调整
            return;
        }
    }*/
}
