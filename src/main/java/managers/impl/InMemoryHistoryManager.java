package managers.impl;

import managers.HistoryManager;
import task.CommonTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<CommonTask> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.get(id) != null) {
            Node currentNode = nodeMap.get(id);
            removeNode(currentNode);
            nodeMap.remove(id);
        }
    }

    @Override
    public void addLast(CommonTask task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;

        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        nodeMap.put(task.getId(), newNode);
    }

    @Override
    public HashMap<Integer, Node> getNodeMap() {
        return nodeMap;
    }

    private List<CommonTask> getTasks() {
        ArrayList<CommonTask> test = new ArrayList<>();
        if (first == null) {
            return test;
        }
        Node currentNode = first;

        while (currentNode.next != null) {
            test.add(currentNode.item);
            currentNode = currentNode.next;
        }
        test.add(currentNode.item);
        return test;
    }

    private void removeNode(Node x) {

        nodeMap.remove(x.item.getId());
        final Node next = x.next;
        final Node prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
    }

    @Override
    public void clear() {
        first = null;
        nodeMap.clear();
    }

    public static class Node {
        public CommonTask item;
        public Node next;
        public Node prev;

        public Node(Node prev, CommonTask data, Node next) {
            this.item = data;
            this.next = next;
            this.prev = prev;
        }
    }
}