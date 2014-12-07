package com.zhuang;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/3.
 */
public class INode {
    public boolean isLeaf = false;
    public INode parent = null;
    public ArrayList<Integer> keys = new ArrayList<Integer>();
    public ArrayList<INode> childNodes = new ArrayList<INode>();
}
