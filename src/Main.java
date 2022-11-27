import pattern.HistoryManager;
import pattern.Managers;
import pattern.TaskManager;
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
        Subtask subtask3_E1 = new Subtask("Изучить Spring",
                "Пройти курс Alishev на ютубе");
        EpicTask epicTask2 = new EpicTask("Сходить в театр", "Сходить в театр оперы и балета на щелкунчика");


        taskManager.createATask(epicTask1, subtask1_E1, subtask2_E1, subtask3_E1);
        taskManager.createATask(epicTask2);
        taskManager.getAllTaskList();

        historyManager.add(epicTask1);
        historyManager.add(subtask1_E1);
        historyManager.add(subtask2_E1);
        historyManager.add(subtask3_E1);
        historyManager.add(epicTask2);
        historyManager.getHistory();

        historyManager.add(subtask3_E1);
        historyManager.add(epicTask2);
        historyManager.add(subtask3_E1);
        historyManager.getHistory();

        taskManager.deleteByID(subtask2_E1.getId());
        historyManager.getHistory();
        taskManager.deleteByID(epicTask1.getId());
        historyManager.getHistory();

        //тесты работают, но возможно другой реализации от меня ждут)
        // буду рад если подробнее будут комментарии
    }
}
