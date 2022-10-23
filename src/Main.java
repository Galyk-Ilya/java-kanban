import ManagerAndStorage.Manager;
import ManagerAndStorage.StatusType;
import task.CommonTask;
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
        Subtask subtask1_E2 = new Subtask("Купить билеты", "Заказать на сайте электронный билет");

        CommonTask commonTask1 = new CommonTask("Пить чай", "Заварить чай, достать стакан, налить чай");

        manager.createATask(commonTask1);
        manager.getAllTaskList();
        manager.getByID(commonTask1.getId()).setStatus(StatusType.IN_PROGRESS);
        manager.updateTask(commonTask1);
        manager.getAllTaskList();
        manager.clearTaskList();

        manager.createATask(epicTask1, subtask1_E1, subtask2_E1);
        manager.createATask(epicTask2, subtask1_E2);

        manager.getAllTaskList();
        manager.getByID(subtask1_E1.getId()).setStatus(StatusType.DONE);
        manager.updateTask(subtask1_E1);
        manager.getByID(subtask2_E1.getId()).setStatus(StatusType.DONE);
        manager.updateTask(subtask2_E1);
        manager.getSubtask(epicTask1);

        manager.deleteByID(epicTask2.getId());
        manager.deleteByID(subtask1_E1.getId());
        manager.getAllTaskList();

        // Сначала протестил все методы, потом удалил чтобы соответствовало ТЗ)
        // Надо будет потом фидбек дать чтобы дали волю студенту и не писали конкретно какие он должен давать тесты))
    }
}
