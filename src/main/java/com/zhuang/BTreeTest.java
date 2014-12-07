package com.zhuang;

/**
 * Created by Administrator on 2014/12/4.
 */
public class BTreeTest {
    public static void main(String[] args) {
        BTree bTree = new BTree();
        TreeLeaf root = new TreeLeaf();
        root.keys.add(2);
        root.rightBrother = null;
        root.isLeaf = true;
        bTree.setRoot(root);

        int[] data = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 40, 41, 43, 47};

        for (int i : data) {
            bTree.insert(i);
        }

        for (int i : data) {
            bTree.delete(i);
        }
        /*for(int i =0;i<6;i++){
            bTree.delete(data[i]);
        }
        bTree.delete(19);*/
        if (bTree.exist(17)) System.out.println("Yes");
        System.out.println(bTree.getRoot().keys.size());
        System.out.println(bTree.getRoot().childNodes.size());
        for (int i : bTree.getRoot().keys) {
            System.out.println(i);
        }

        /*for (int i : bTree.getRoot().childNodes.get(2).keys) {
            System.out.println(i);
        }*/
        /*for (int i : bTree.getRoot().childNodes.get(0).keys) {
            System.out.println(i);
        }*/
    }
}
