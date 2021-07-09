package com.edencoding.controllers;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class DragHelper {

    private static final Map<Integer, Node> storedNodes = new HashMap<>();
    private static final Map<Node, Pane> nodeParents = new HashMap<>();
    private static int count;

    private DragHelper() {
    }

    public static int storeNode(Node node) {
        storedNodes.put(++count, node);
        return count;
    }

    public static Node getNode(Integer serial) {
        return storedNodes.get(serial);
    }

    public static void removeNode(Integer serial) {
        storedNodes.remove(serial);
    }

    public static void storeNodeParent(Node child, Pane parent) {
        nodeParents.put(child, parent);
    }

    public static Pane getParent(Node child) {
        return nodeParents.get(child);
    }

    public static void removeNodeParentPair(Node child) {
        nodeParents.remove(child);
    }
}
