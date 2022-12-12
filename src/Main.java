import managers.Managers;
import managers.TaskManager;
import task.CommonTask;
import task.EpicTask;
import task.Subtask;

import static enums.StatusType.IN_PROGRESS;
import static enums.TaskType.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        EpicTask epicTask1 = taskManager.createEpicTask("Epic", "descriptionEpic", EPIC_TASK);
        CommonTask commonTask1 = taskManager.createATask("Task", "descriptionTask", COMMONTASK);
        Subtask subtask1 = taskManager.createASubtask("Subtask1", "descriptionSubtask1",
                SUBTASK, epicTask1.getId());
        Subtask subtask2 = taskManager.createASubtask("Subtask2", "descriptionSubtask2",
                SUBTASK, epicTask1.getId());

        subtask1.setStatus(IN_PROGRESS);
        taskManager.updateTask(epicTask1);

        taskManager.deleteById(epicTask1.getId());
        taskManager.getAllTaskList();

        taskManager.getEpic(epicTask1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(commonTask1.getId());
        taskManager.getEpic(epicTask1.getId());
//
        for (CommonTask task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
//(на вход от пользователя так или иначе будут приходить не ссылки,а данные для их создания,
//        поэтому даже если эти данные будут дублироваться,это всегда новый объект)
//
//        p.s. исключил возможность возникновения случая из замечания из 5 ТЗ):
//        taskManager.createATask(epicTask1,subtask3_E1,subtask3_E1,subtask3_E1,subtask3_E1);
//        taskManager.createATask(epicTask1,subtask3_E1,subtask3_E1,subtask3_E1,subtask3_E1);
//        (Добавление тех же самых тасок)

// Пока не увидел большого смысла внедрять дженерики, как понимаю сильно не оптимизирую тут этим что-то
