import memoryManagers.InMemoryHistoryManager;
import memoryManagers.InMemoryTaskManager;
import pattern.StatusType;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        EpicTask epicTask1 = new EpicTask("Учиться программировать",
                "Пройти весь десятимесячный курс обучения Я.Практикума");
        Subtask subtask1_E1 = new Subtask("Учить теорию и выполнять задания",
                "Ежедневно изучать теорию которую дает платформа Я.Практикум и практиковаться с её помощью");
        Subtask subtask2_E1 = new Subtask("Соблюдать дедлайны",
                "Своевременно выполнять все необходимые задачи спринтов");

        EpicTask epicTask2 = new EpicTask("Сходить в театр", "Сходить в театр оперы и балета");
        Subtask subtask1_E2 = new Subtask("Купить билеты", "Заказать на сайте электронный билет");

        CommonTask commonTask1 = new CommonTask("Пить чай", "Заварить чай, достать стакан, налить чай");

        inMemoryTaskManager.createATask(commonTask1);
        inMemoryTaskManager.getAllTaskList();
        inMemoryTaskManager.getByID(commonTask1.getId()).setStatus(StatusType.IN_PROGRESS);
        inMemoryTaskManager.updateTask(commonTask1);
        inMemoryTaskManager.getAllTaskList();
        inMemoryTaskManager.clearTaskList();

        inMemoryTaskManager.createATask(epicTask1, subtask1_E1, subtask2_E1);
        inMemoryTaskManager.createATask(epicTask2, subtask1_E2);

        inMemoryTaskManager.getAllTaskList();
        inMemoryTaskManager.getByID(subtask1_E1.getId()).setStatus(StatusType.DONE);
        inMemoryTaskManager.updateTask(subtask1_E1);
        inMemoryTaskManager.getByID(subtask2_E1.getId()).setStatus(StatusType.DONE);
        inMemoryTaskManager.updateTask(subtask2_E1);
        inMemoryTaskManager.getSubtask(epicTask1);

        inMemoryTaskManager.deleteByID(epicTask2.getId());
        inMemoryTaskManager.deleteByID(subtask1_E1.getId());
        inMemoryTaskManager.getAllTaskList();


        CommonTask commonTask2 = new CommonTask("Задача для теста №1", "Пояснение к задаче №1");
        inMemoryTaskManager.createATask(commonTask2);
        CommonTask commonTask3 = new CommonTask("Задача для теста №2", "Пояснение к задаче №2");
        inMemoryTaskManager.createATask(commonTask3);
        CommonTask commonTask4 = new CommonTask("Задача для теста №3", "Пояснение к задаче №3");
        inMemoryTaskManager.createATask(commonTask4);
        CommonTask commonTask5 = new CommonTask("Задача для теста №4", "Пояснение к задаче №4");
        inMemoryTaskManager.createATask(commonTask5);
        CommonTask commonTask6 = new CommonTask("Задача для теста №5", "Пояснение к задаче №5");
        inMemoryTaskManager.createATask(commonTask6);
        CommonTask commonTask7 = new CommonTask("Задача для теста №6", "Пояснение к задаче №6");
        inMemoryTaskManager.createATask(commonTask7);
        CommonTask commonTask8 = new CommonTask("Задача для теста №7", "Пояснение к задаче №7");
        inMemoryTaskManager.createATask(commonTask8);
      //  inMemoryTaskManager.getAllTaskList();

        CommonTask taskTest = inMemoryHistoryManager.getTask(commonTask2.getId());
        System.out.println("Получили задачу из истории: " + taskTest);
        inMemoryHistoryManager.getTask(commonTask3.getId());
        inMemoryHistoryManager.getTask(commonTask4.getId());
        inMemoryHistoryManager.getTask(commonTask5.getId());
        inMemoryHistoryManager.getTask(commonTask6.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getTask(commonTask7.getId());
        inMemoryHistoryManager.getEpic(epicTask1.getId());
        inMemoryHistoryManager.getSubtask(subtask2_E1.getId());

        inMemoryHistoryManager.getHistory();
    }
}
