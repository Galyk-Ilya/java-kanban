import pattern.Managers;
import pattern.TaskManager;
import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        EpicTask epicTask1 = new EpicTask("Заголовок №1",
                "тело задачи №1");
        Subtask subtask1_E1 = new Subtask("Заголовок №2",
                "тело задачи №2");
        Subtask subtask2_E1 = new Subtask("Заголовок №3",
                "тело задачи №3");
        Subtask subtask3_E1 = new Subtask("Заголовок №4",
                "тело задачи №4");
        EpicTask epicTask2 = new EpicTask("Заголовок №5",
                "тело задачи №6");

//Исправления функционала TaskManager
        EpicTask eTask1 = taskManager.createATask(epicTask1, subtask1_E1, subtask2_E1, subtask3_E1);
        // taskManager.createATask сделал возвращающим epic для доступа к id вновь созданным объектам
        // (при создании новых(но дублирующих уже имеющихся) теряю ссылка на объект).

        taskManager.createATask(epicTask1, subtask1_E1, subtask1_E1, subtask1_E1);
        EpicTask eTask2 = taskManager.createATask(epicTask2);
        taskManager.getListSubtasks(eTask1);
        taskManager.getAllTaskList();

//Функционал HistoryManager
        taskManager.getEpic(eTask1.getId());
        taskManager.getSubtask(eTask1.getSubtasksList().get(0).getId());
        taskManager.getSubtask(eTask1.getSubtasksList().get(1).getId());
        taskManager.getSubtask(eTask1.getSubtasksList().get(2).getId());
        taskManager.getEpic(eTask2.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }
// Как избежать создания  метода getHistory() в taskManager чтобы добраться до истории?
// (Или в создании геттера и заключалась задача)

        taskManager.getSubtask(eTask1.getSubtasksList().get(2).getId());
        taskManager.getEpic(eTask1.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        taskManager.deleteByID(eTask1.getId());
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
