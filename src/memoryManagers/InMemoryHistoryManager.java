package memoryManagers;

import pattern.HistoryManager;
import task.CommonTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    Node head;
    private static final HashMap<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public List<CommonTask> getHistory() {
        System.out.println("\nИстория просмотренных задач:");
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node currentNode = nodeMap.get(id);
        removeNode(currentNode.item);
        nodeMap.remove(id);
    }

    @Override
    public void addLast(CommonTask t) {
        if (head == null) {
            head = new Node(t);
            nodeMap.put(t.getId(), head);
            return;
        }
        Node currentNode = head;
        while (currentNode.next != null) {
            currentNode = currentNode.next;
        }

        if (nodeMap.containsKey(t.getId())) {
            removeNode(nodeMap.get(t.getId()).item);
        }
        currentNode.next = new Node(t);
        nodeMap.put(t.getId(), currentNode.next);

    }

    private List<CommonTask> getTasks() {
        ArrayList<CommonTask> test = new ArrayList<>();
        Node currentNode = head;

        while (currentNode.next != null) {
            test.add(currentNode.item);
            currentNode = currentNode.next;
        }
        test.add(currentNode.item);
        return test;
    }

    public void removeNode(CommonTask t) {
        if (head == null) {
            return;
        }
        if (head.item == t) {
            head = head.next;
            return;
        }
        Node currentNode = head;
        while (currentNode.next != null) {
            if (currentNode.next.item == t) {
                currentNode.next = currentNode.next.next;
                return;
            }
            currentNode = currentNode.next;
        }
    }

    static class Node {
        public CommonTask item;
        public Node next;

        public Node(CommonTask item) {
            this.item = item;
        }
    }
}
