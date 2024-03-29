package managers.impl;

import enums.StatusType;
import enums.TaskType;
import exception.ManagerSaveException;
import exception.ManagerStartDatabaseException;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public FileBackedTasksManager(String String) {
        super();
        file = null;
    }

    @Override
    public CommonTask createATask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        CommonTask newTask = createTaskAndPutToTaskList(nameTask, description, type, duration, startTime);
        save();
        return newTask;
    }

    @Override
    public EpicTask createEpicTask(String nameTask, String description, TaskType type, int duration, LocalDateTime startTime) {
        EpicTask newTask = super.createEpicTask(nameTask, description, type, duration, startTime);
        save();
        return newTask;
    }

    @Override
    public Subtask createASubtask(String nameTask, String description, TaskType type, int epicId, int duration, LocalDateTime startTime) {
        Subtask newTask = super.createASubtask(nameTask, description, type, epicId, duration, startTime);
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
            String header = "id, type, name, status, description, epicNumber, duration, startTime, endTime \n";
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

    public static FileBackedTasksManager taskConverter(List<String> stringTaskList,
                                                        FileBackedTasksManager fileBackedTasksManager) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        for (int i = 1; i < stringTaskList.size(); i++) {
            String line = stringTaskList.get(i);
            String[] lineArray;
            lineArray = line.split(", ");

            if (!line.contains("COMMONTASK") && !line.contains("EPIC") && !line.contains("SUBTASK") && !line.isEmpty()) {
                String[] allHistory = line.split(", ");
                for (String history : allHistory) {
                    fileBackedTasksManager.getTask(Integer.parseInt(history));
                }
                continue;
            } else if (line.isEmpty()) {
                continue;
            }
            int durationParse = Integer.parseInt(String.valueOf(Duration.parse(lineArray[6]).toMinutes()));
            int idParse = Integer.parseInt(lineArray[0]);
            StatusType statusParse = StatusType.valueOf(lineArray[3]);
            LocalDateTime localDateTimeParse = LocalDateTime.parse(lineArray[7], formatter);

            if (line.contains("COMMONTASK")) {
                CommonTask task = new CommonTask(idParse, lineArray[2], statusParse,
                        lineArray[4], durationParse, localDateTimeParse);
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("EPIC")) {
                EpicTask task = new EpicTask(idParse, lineArray[2], statusParse,
                        lineArray[4], durationParse, localDateTimeParse);
                task.setEndTime(LocalDateTime.parse(lineArray[8], formatter));
                fileBackedTasksManager.taskList.put(task.getId(), task);
            } else if (line.contains("SUBTASK")) {
                Subtask task = new Subtask(idParse, lineArray[2], statusParse,
                        lineArray[4], durationParse, localDateTimeParse);

                EpicTask parentEpic = (EpicTask) fileBackedTasksManager.taskList.get(Integer.valueOf(lineArray[5]));
                task.setEpicId(Integer.valueOf(lineArray[5]));
                parentEpic.getSubtasksList().add(task);
                fileBackedTasksManager.taskList.put(task.getId(), task);
            }
        }
        return fileBackedTasksManager;
    }
}