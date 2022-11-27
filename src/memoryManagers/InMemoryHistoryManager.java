package memoryManagers;

import pattern.HistoryManager;
import pattern.Managers;
import pattern.TaskManager;
import pattern.TaskType;
import task.CommonTask;
import task.EpicTask;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final LinkedHashMap<Integer, Node> nodeMap = new LinkedHashMap<>();
    private Node first;
    private Node last;

    private static final TaskManager taskManager = Managers.getDefault();

    @Override
    public List<CommonTask> getHistory() {
        System.out.println("История просмотренных задач:");
        return getTasks();
    }

    @Override
    public void add(CommonTask task) {

        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        if (taskManager.getTaskList().containsValue(task)) {
            nodeMap.put(task.getId(), linkLast(task));
        } else {
            System.out.println("Задача отсутстует");
        }

    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            if(nodeMap.get(id).item.getType() == TaskType.EPIC_TASK) {
                EpicTask epicTask = (EpicTask) nodeMap.get(id).item;
                for (CommonTask x : epicTask.getSubtasksList()) {
                    nodeMap.remove(x.getId());
                }
            }
            Node node = nodeMap.get(id);
            removeNode(node);

        }
    }

    private Node linkLast(CommonTask task) {

        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        return newNode;

    }

    private List<CommonTask> getTasks() {
        LinkedList<CommonTask> tasks = new LinkedList<>();
        for (Node test : nodeMap.values()) {
            tasks.add(test.item);
            System.out.println(test.item.getName());
        }
        System.out.println(tasks.size());
        return tasks;
    }

    private void removeNode(Node x) {

        final Node node = nodeMap.remove(x.item.getId());
        final CommonTask element = x.item;
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
        x.item = null;
    }

    static class Node {
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