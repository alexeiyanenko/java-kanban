package manager;

import tasks.Task;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> historyMap = new HashMap<>();

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());  // Удаляем старое вхождение, если оно есть
        linkLast(task); // Добавляем новое вхождение в конец списка
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node toRemove = historyMap.get(id);
        if (toRemove != null) {
            removeNode(toRemove);
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        historyMap.remove(node.task.getId());
    }

    public void clearHistory() {
        head = null;
        tail = null;
        historyMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public String toString() {
        return "History:\n" + getHistory();
    }
}
