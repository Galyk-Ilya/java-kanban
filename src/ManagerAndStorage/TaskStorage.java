package ManagerAndStorage;

import task.CommonTask;

import java.util.HashMap;

class TaskStorage {
    private HashMap<Integer, CommonTask> taskList = new HashMap<>();

    public HashMap<Integer, CommonTask> getTaskList() {
        return taskList;
    }

}
