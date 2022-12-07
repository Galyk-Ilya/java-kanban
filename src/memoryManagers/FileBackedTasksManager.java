package memoryManagers;

import pattern.*;
import task.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    @Override
    public CommonTask createATask(CommonTask task) {
        CommonTask newTask = super.createATask(task);
        save();
        return newTask;
    }

    @Override
    public EpicTask createEpicTask(EpicTask task) {
        EpicTask newTask = super.createEpicTask(task);
        save();
        return newTask;
    }

    @Override
    public EpicTask createEpicAndSubtask(EpicTask task, Subtask... subtask) {
        EpicTask newTask = super.createEpicAndSubtask(task, subtask);
        save();
        return newTask;
    }

    @Override
    public CommonTask getTask(Integer ID) {
        CommonTask task = super.getTask(ID);
        save();
        return task;
    }

    @Override
    public EpicTask getEpic(Integer ID) {
        EpicTask task = super.getEpic(ID);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(Integer ID) {
        Subtask task = super.getSubtask(ID);
        save();
        return task;
    }

    public void save() {

        try (FileWriter writer = new FileWriter(file)) {
            String header = "id, type, name, status, description, epicNumber \n";
            writer.write(header);
            for (CommonTask task : taskList.values()) {
                switch (task.getType()) {
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        writer.write(subtask.toStringToWriteToFile() + "\n");
                        break;
                    default:
                        writer.write(task.toStringToWriteToFile() + "\n");
                        break;
                }
            }
            writer.write("\n");
            StringBuilder history = new StringBuilder();

            int testKey = 0;
            for (int key : getDefaultHistory.getNodeMap().keySet()) {
                testKey++;
                if (getDefaultHistory.getNodeMap().size() != testKey) {
                    history.insert(0, key).insert(1,", ");
                } else {
                    history.append(key);
                }
            }
            String test = history.toString();
            writer.write(test);
        } catch (Exception e) {
            try {
                throw new ManagerSaveException("Error saving to database");
            } catch (ManagerSaveException exception) {
                System.out.println(e.getMessage());
            }
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

        } catch (Exception e) {
            try {
                throw new ManagerStartDatabaseException("Error processing existing files");
            } catch (ManagerStartDatabaseException exception) {
                System.out.println(exception.getMessage());
            }
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
                task.setType(TaskType.valueOf(lineArray[1]));
                task.setStatus(StatusType.valueOf(lineArray[3]));
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("EPIC")) {
                EpicTask task = new EpicTask(lineArray[2], lineArray[4]);
                task.setId(Integer.valueOf(lineArray[0]));
                task.setType(TaskType.valueOf(lineArray[1]));
                task.setStatus(StatusType.valueOf(lineArray[3]));
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("SUBTASK")) {
                Subtask task = new Subtask(lineArray[2], lineArray[4]);
                task.setId(Integer.valueOf(lineArray[0]));
                task.setType(TaskType.valueOf(lineArray[1]));
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

        CommonTask commonTask1 = new CommonTask("Заголовок №1",
                "Тело задачи №1");
        EpicTask epicTask1 = new EpicTask("Заголовок №2",
                "Тело задачи №2");
        Subtask subtask1_E1 = new Subtask("Заголовок №3",
                "Тело задачи №3");
        Subtask subtask2_E1 = new Subtask("Заголовок №4",
                "Тело задачи №4");
        EpicTask epicTask2 = new EpicTask("Заголовок №5",
                "Тело задачи №5");
        EpicTask createEpic1 = taskManager.createEpicAndSubtask(epicTask1, subtask1_E1, subtask2_E1);
        EpicTask createEpic2 = taskManager.createEpicTask(epicTask2);
        CommonTask createCommonTask1 = taskManager.createATask(commonTask1);

        taskManager.getTask(createCommonTask1.getId());
        taskManager.getEpic(createEpic2.getId());
        taskManager.getEpic(createEpic1.getId());
        taskManager.getSubtask(createEpic1.getSubtasksList().get(0).getId());
    }
    //много дублирующего кода в taskConverter, так как не смог привести таску к эпику и наполнить несуществующее поле,
    // попробую добавить дженерик в следющий push, буду рад услышать комментарии по местам,
    // где можно оптимизировать или где-то использовать что-то другое, что будет корректнее, актуальнее)

    // Я использую эти методы(под комментарием) для внедрения таски в менеджер,
    // нужно ли их переписать для дальнейшшей логичной реализации т.е. отдельно три create под эпик, таск, сабтаск?
    /// EpicTask createEpicTask(EpicTask task);
    /// CommonTask createATask(CommonTask commonTask);
    // EpicTask createEpicAndSubtask(EpicTask task, Subtask... subtask);
}

