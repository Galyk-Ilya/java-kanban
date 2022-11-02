import pattern.HistoryManager;
import pattern.Managers;
import pattern.StatusType;
import pattern.TaskManager;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        EpicTask epicTask1 = new EpicTask("Учиться программировать",
                "Пройти весь десятимесячный курс обучения Я.Практикума");
        Subtask subtask1_E1 = new Subtask("Учить теорию и выполнять задания",
                "Ежедневно изучать теорию которую дает платформа Я.Практикум и практиковаться с её помощью");
        Subtask subtask2_E1 = new Subtask("Соблюдать дедлайны",
                "Своевременно выполнять все необходимые задачи спринтов");
        EpicTask epicTask2 = new EpicTask("Сходить в театр", "Сходить в театр оперы и балета");
        Subtask subtask1_E2 = new Subtask("Купить билеты", "Заказать на сайте электронный билет");
        CommonTask commonTask1 = new CommonTask("Пить чай", "Заварить чай, достать стакан, налить чай");

        taskManager.createATask(commonTask1);
        taskManager.getAllTaskList();
        taskManager.getByID(commonTask1.getId()).setStatus(StatusType.IN_PROGRESS);
        taskManager.updateTask(commonTask1);
        taskManager.getAllTaskList();
        taskManager.clearTaskList();
        taskManager.createATask(epicTask1, subtask1_E1, subtask2_E1);
        taskManager.createATask(epicTask2, subtask1_E2);
        taskManager.getAllTaskList();


        taskManager.getByID(subtask1_E1.getId()).setStatus(StatusType.NEW);
        taskManager.updateTask(subtask1_E1);
        taskManager.getByID(subtask2_E1.getId()).setStatus(StatusType.DONE);
        taskManager.updateTask(subtask2_E1);
        taskManager.deleteByID(subtask1_E1.getId());
        taskManager.getSubtask(epicTask1);
        taskManager.deleteByID(epicTask2.getId());
        taskManager.deleteByID(subtask1_E1.getId());
        taskManager.getAllTaskList();

        for (int i = 1; i < 12; i++) {
            CommonTask commonTask_test = new CommonTask("Задача для теста №" + i, "Пояснение к задаче №" + i);
            taskManager.createATask(commonTask_test);
            historyManager.getTask(commonTask_test.getId());
        }
        historyManager.getTask(epicTask1.getId());
        historyManager.getSubtask(subtask2_E1.getId());
        historyManager.getHistory();
    }
}
