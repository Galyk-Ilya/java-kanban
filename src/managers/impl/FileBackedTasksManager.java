package managers.impl;

import enums.*;
import exception.*;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static enums.TaskType.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public CommonTask createATask(String nameTask, String description, TaskType type) {
        CommonTask newTask = super.createATask(nameTask, description, type);
        save();
        return newTask;

    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type) {
        EpicTask newTask = super.createEpicTask(nameTask, description, type);
        save();
        return newTask;
    }

    @Override
    public Subtask createASubtask(String nameTask, String description, TaskType type, int epicId) {
        Subtask newTask = super.createASubtask(nameTask, description, type, epicId);
        save();
        return newTask;
    }

    @Override
    public CommonTask getTask(int id) {
        CommonTask task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpic(int id) {
        EpicTask task = super.getEpic(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask task = super.getSubtask(id);
        save();
        return task;
    }

    public void save() {

        try (FileWriter writer = new FileWriter(file)) {
            String header = "id, type, name, status, description, epicNumber \n";
            writer.write(header);
            for (CommonTask task : taskList.values()) {
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    writer.write(subtask.toStringToWriteToFile() + "\n");
                } else {
                    writer.write(task.toStringToWriteToFile() + "\n");
                }
            }
            writer.write("\n");

            if (getDefaultHistory.getNodeMap().size() > 0) {
                StringBuilder history = new StringBuilder();
                for (CommonTask task : getHistory()) {
                    history.insert(0, task.getId() + ", ");
                }
                String test = history.toString();
                writer.write(test);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving to database");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        List<String> listTasks = new ArrayList<>();
        try (FileReader words = new FileReader(file)) {
            BufferedReader br = new BufferedReader(words);

            while (br.ready()) {
                String line = (br.readLine());
                listTasks.add(line);
            }

        } catch (IOException e) {
            throw new ManagerStartDatabaseException("Error processing existing files");
        }
        return taskConverter(listTasks, fileBackedTasksManager);
    }

    public static FileBackedTasksManager taskConverter(List<String> StringTaskList,
                                                       FileBackedTasksManager fileBackedTasksManager) {

        for (String line : StringTaskList) {
            String[] lineArray;
            lineArray = line.split(", ");
            if (line.contains("type") || line.isEmpty()) {
                continue;
            } else if (line.contains("COMMONTASK")) {
                CommonTask task = new CommonTask(lineArray[2], lineArray[4]);
                task.setId(Integer.valueOf(lineArray[0]));
                task.setStatus(StatusType.valueOf(lineArray[3]));
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("EPIC")) {
                EpicTask task = new EpicTask(lineArray[2], lineArray[4]);
                task.setId(Integer.valueOf(lineArray[0]));
                task.setStatus(StatusType.valueOf(lineArray[3]));
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("SUBTASK")) {
                Subtask task = new Subtask(lineArray[2], lineArray[4]);
                task.setId(Integer.valueOf(lineArray[0]));
                task.setStatus(StatusType.valueOf(lineArray[3]));

                EpicTask parentEpic = (EpicTask) fileBackedTasksManager.taskList.get(Integer.valueOf(lineArray[5]));
                task.setEpicId(Integer.valueOf(lineArray[5]));
                parentEpic.getSubtasksList().add(task);
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else {
                String[] allHistory = line.split(", ");
                for (String history : allHistory) {
                    fileBackedTasksManager.getTask(Integer.parseInt(history));
                }
            }
        }
        return fileBackedTasksManager;
    }


    public static void main(String[] args) {
        File file = new File("C:\\Users\\Илья\\IdeaProjects\\java-kanban\\src\\resources\\archive.txt");
        FileBackedTasksManager taskManager = loadFromFile(file);

        CommonTask commonTask1 = taskManager.createATask("Заголовок №1", "Тело задачи №1", COMMONTASK);
        EpicTask epicTask1 = taskManager.createEpicTask("Заголовок №2", "Тело задачи №2", EPIC_TASK);
        Subtask subtask1_E1 = taskManager.createASubtask("Заголовок №3", "Тело задачи №3", SUBTASK,
                epicTask1.getId());
        Subtask subtask2_E1 = taskManager.createASubtask("Заголовок №4", "Тело задачи №4", SUBTASK,
                epicTask1.getId());
        EpicTask epicTask2 = taskManager.createEpicTask("Заголовок №5", "Тело задачи №5", EPIC_TASK);


        taskManager.getTask(commonTask1.getId());
        taskManager.getEpic(epicTask2.getId());
        taskManager.getEpic(epicTask1.getId());
        taskManager.getSubtask(subtask1_E1.getId());
        taskManager.getEpic(epicTask1.getId());

        for (CommonTask h : taskManager.getHistory()) {
            System.out.println(h);
        }
    }
}

