package ManagerAndStorage;

import task.CommonTask;

import java.util.HashMap;

class TaskStorage {
    private final HashMap<Integer, CommonTask> taskList = new HashMap<>();

    public HashMap<Integer, CommonTask> getTaskList() {
        return taskList;
    }

}
