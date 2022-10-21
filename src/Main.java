import ManagerAndStorage.Manager;
import task.EpicTask;
import task.Subtask;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        EpicTask epicTask1 = new EpicTask("Учиться программировать",
                "Пройти весь десятимесячный курс обучения Я.Практикума");
        Subtask subtask1_E1 = new Subtask("Учить теорию и выполнять задания",
                "Ежедневно изучать теорию которую дает платформа Я.Практикум и практиковаться с её помощью");
        Subtask subtask2_E1 = new Subtask("Соблюдать дедлайны",
                "Своевременно выполнять все необходимые задачи спринтов");

        EpicTask epicTask2 = new EpicTask("Сходить в театр", "Сходить в театр оперы и балета");
        Subtask subtask1_E2 = new Subtask("купить билеты", "Заказать на сайте электронный билет");

        manager.createATask(epicTask1, subtask1_E1, subtask2_E1);
        manager.createATask(epicTask2, subtask1_E2);

        manager.getAllTaskList();
        subtask1_E1.setStatus("DONE");
        manager.updateTask(subtask1_E1);
        subtask2_E1.setStatus("DONE");
        manager.updateTask(subtask2_E1);

        manager.getAllTaskList();
        manager.deleteByID(epicTask2.getId());
        manager.deleteByID(subtask1_E1.getId());
        manager.getAllTaskList();
    }
}
