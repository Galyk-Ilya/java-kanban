package server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
  //      InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
//        CommonTask task = inMemoryTaskManager.createATask("name", "description", TaskType.COMMONTASK,
//                10, LocalDateTime.of(2000, 10, 20, 10, 20));
//        EpicTask epicTask = inMemoryTaskManager.createEpicTask("name", "description", TaskType.EPIC_TASK,
//                10, LocalDateTime.of(2000, 10, 22, 10, 20));
//        Subtask subtask = inMemoryTaskManager.createASubtask("name", "description", TaskType.SUBTASK,
//                2, 10, LocalDateTime.of(2000, 10, 24, 10, 20));
     //   Gson gson = new Gson();
//        System.out.println(gson.toJson(task));
//        System.out.println(gson.toJson(epicTask));
//        System.out.println(gson.toJson(subtask));
        KVServer kvServer;
        kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
