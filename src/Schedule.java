import java.awt.event.MouseAdapter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class Schedule {

    public static void main(String[] args) {
        String type = args[0];
        String file = args[1];

        List<String> tasks = new ArrayList<>();

        Path currentRelativePath = Paths.get("");
        currentRelativePath.toAbsolutePath().toString();

        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(currentRelativePath.toAbsolutePath().toString()+"/"+file));
            String line = reader.readLine();
            while (line!=null ){
                if (line.equals("")){
                    line = reader.readLine();
                    continue;
                }

                tasks.add(line.replaceAll(",", ""));
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


// Creat output file
        try {
            File myFile = new File("output.txt");
            if(myFile.createNewFile()) {
            } else {
                // overwrite output file
                FileWriter myWriter = new FileWriter("output.txt", false);

            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        HashMap<String,String> titles = new HashMap();
        titles.put("fcfs","First Come First Served Scheduling");
        titles.put("sjf","Shortest Job First Scheduling");
        titles.put("pri","Priority Scheduling");
        titles.put("rr","Round Robin Scheduling");
        titles.put("pri-rr","Priority Round Robin Scheduling");

        try {
            FileWriter myWriter = new FileWriter("output.txt", true);
            myWriter.write(titles.get(type) + "\n\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (type) {
            case ("fcfs"):
                fcfs(tasks);
                break;
            case ("sjf"):
                sjf(tasks);
                break;
            case ("pri"):
                pri(tasks);
                break;
            case ("rr"):
                rr(tasks,10);
                break;
            case ("pri-rr"):
                pri_rr(tasks,10);
                break;
        }
    }
    //priority round-robin
    private static void pri_rr(List<String> taskList, int quantum) {
        Collections.sort(taskList, new Comparator<String>() {
            @Override
            public int compare(String task1, String task2) {
                int arrivalTime1 = Integer.parseInt(task1.split(" ")[1]);
                int arrivalTime2 = Integer.parseInt(task2.split(" ")[1]);

                if(arrivalTime1 < arrivalTime2) {
                    return -1;
                } else if(arrivalTime1 > arrivalTime2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        Map<Integer,ArrayList<String>> priorityMap =new HashMap<>();
        Map<String,Integer> taskMap =new HashMap<>();
        ArrayList<Integer> orderList = new ArrayList<>();
        ArrayList<String> checkTask = new ArrayList<>();
        ArrayList<Integer> arrivalTimes = new ArrayList<>();

        int taskCounter = 0;
        int timeCounter = 0;
        double totTurnaroundTime = 0;
        double totWaitingTime = 0;
        double totBurstTime = 0;

        //first arrival time
        for(String task : taskList) {
            arrivalTimes.add(Integer.parseInt(task.split(" ")[1]));
            totBurstTime += Integer.parseInt(task.split(" ")[3]);
        }
        timeCounter = Integer.parseInt(taskList.get(0).split(" ")[1]);


        while (taskCounter != taskList.size()) {
            for(String task : taskList) {
                String[] taskArray = task.split(" ");
                int arrivalTime = Integer.parseInt(taskArray[1]);
                String taskName = taskArray[0];
                int priority = Integer.parseInt(taskArray[2]);
                int burstTime = Integer.parseInt(taskArray[3]);

                if(arrivalTime<=timeCounter && !checkTask.contains(taskName)) {
                    priorityMap.putIfAbsent(priority, new ArrayList<>());
                    if(!orderList.contains(priority)) orderList.add(priority);
                    if(!priorityMap.get(priority).contains(task)) {
                        priorityMap.get(priority).add(task);
                        arrivalTimes.remove(0);
                        taskMap.putIfAbsent(taskName, burstTime);
                    }

                }
            }
            Collections.sort(orderList,Collections.reverseOrder());
            //System.out.println(orderList);
            ArrayList<String> readyQ = new ArrayList<>();
            for(int i : orderList) {
                if(!priorityMap.get(i).isEmpty()) {
                    readyQ = priorityMap.get(i);
                    break;
                }
            }
            String taskCheck = null;
            my:
            while (!readyQ.isEmpty()) {
                String currentTask = readyQ.remove(0);
                String[] curTaskArray = currentTask.split(" ");
                String taskName = curTaskArray[0];
                Integer burstTime = taskMap.get(taskName);

                int arrivalTime = Integer.parseInt(curTaskArray[1]);
                int priority = Integer.parseInt(curTaskArray[2]);
                if (taskCheck == null){
                    try {
                        FileWriter myWriter = new FileWriter("output.txt", true);
                        myWriter.write("Will run Name: " + curTaskArray[0] + "\n");
                        myWriter.write("Priority: " + curTaskArray[2] + "\n");
                        myWriter.write("Burst: " + curTaskArray[3] + "\n\n");
                        myWriter.close();
                        taskCheck = taskName;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (!taskCheck.equals(taskName)){
                    try {
                        FileWriter myWriter = new FileWriter("output.txt", true);
                        myWriter.write("Will run Name: " + curTaskArray[0] + "\n");
                        myWriter.write("Priority: " + curTaskArray[2] + "\n");
                        myWriter.write("Burst: " + curTaskArray[3] + "\n\n");
                        myWriter.close();
                        taskCheck = taskName;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(burstTime < quantum){
                    timeCounter += burstTime;
                    for(int arTime : arrivalTimes) {
                        if(  arTime < timeCounter && checkPriorityByArrivalTime(taskList, arTime, priority)) {
                            //System.out.println(taskName);
                            timeCounter -= quantum;
                            taskMap.put(taskName,taskMap.get(taskName)-(arTime-timeCounter));
                            timeCounter = arTime;

                            readyQ.add(currentTask);
                            try {
                                FileWriter myWriter = new FileWriter("output.txt", true);
                                myWriter.write( "There is a context switch "+ curTaskArray[0] +" is going back to ready queue" + "\n\n");
                                myWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break my;
                        }
                    }
                    taskMap.put(taskName,0);
                    totTurnaroundTime += timeCounter - arrivalTime;
                    taskCounter++;
                    checkTask.add(taskName);
                    try {
                        FileWriter myWriter = new FileWriter("output.txt", true);
                        myWriter.write("Task " + curTaskArray[0] +" finished"+ "\n\n");
                        myWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {

                    timeCounter += quantum;
                    for(int arTime : arrivalTimes) {
                        if(  arTime < timeCounter && checkPriorityByArrivalTime(taskList, arTime, priority)) {
                            //System.out.println(taskName);
                            timeCounter -= quantum;
                            taskMap.put(taskName,taskMap.get(taskName)-(arTime-timeCounter));
                            timeCounter = arTime;

                            readyQ.add(currentTask);
                            try {
                                FileWriter myWriter = new FileWriter("output.txt", true);
                                myWriter.write( "There is a context switch "+ curTaskArray[0] +" is going back to ready queue" + "\n\n");
                                myWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break my;
                        }
                    }

                    taskMap.put(taskName,taskMap.get(taskName)-quantum);
                    if(taskMap.get(taskName) == 0){
                        taskCounter++;
                        taskMap.put(taskName,0);
                        totTurnaroundTime += timeCounter-arrivalTime;
                        checkTask.add(taskName);
                        try {
                            FileWriter myWriter = new FileWriter("output.txt", true);
                            myWriter.write("Task " + curTaskArray[0] +" finished"+ "\n\n");
                            myWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        if(!readyQ.isEmpty()) {
                            try {
                                FileWriter myWriter = new FileWriter("output.txt", true);
                                myWriter.write( "There is a context switch "+ curTaskArray[0] +" is going back to ready queue" + "\n\n");
                                myWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        readyQ.add(currentTask);

                    }
                }
            }

        }

        System.out.println("Average Turnaround Time : "+ (totTurnaroundTime/taskList.size()));
        System.out.println("Average Waiting Time : "+(totTurnaroundTime-totBurstTime)/taskList.size());

    }

    public static boolean checkPriorityByArrivalTime(List<String> taskList, int arrivalTime, int currentPriority) {
        for(String task : taskList) {
            String[] taskArr = task.split(" ");
            if(Integer.parseInt(taskArr[1]) == arrivalTime) {
                if(Integer.parseInt(taskArr[2]) > currentPriority) {
                    return true;
                }
            }
        }

        return false;
    }
    //round-robin
    private static void rr(List<String> taskList, int quantum) {
        Collections.sort(taskList, new Comparator<String>() {
            @Override
            public int compare(String task1, String task2) {
                int arrivalTime1 = Integer.parseInt(task1.split(" ")[1]);
                int arrivalTime2 = Integer.parseInt(task2.split(" ")[1]);

                if(arrivalTime1 < arrivalTime2) {
                    return 1;
                } else if(arrivalTime1 > arrivalTime2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Map<String, Boolean> checkTask = new HashMap<>();
        Map<String,Integer> taskMap =new HashMap<>();
        int timeCounter = 0;
        int taskCounter = 0;
        double totTurnaroundTime = 0;
        double totWaitingTime = 0;
        double totBurstTime = 0;
        ArrayList<String> readyQ = new ArrayList<>();
        while (taskCounter != taskList.size()) {
            //System.out.println(readyQ);
            for(String task:taskList){

                String[] taskArray = task.split(" ");
                int arrival = Integer.parseInt(taskArray[1]);
                String taskName = taskArray[0];
                taskMap.putIfAbsent(taskArray[0],Integer.parseInt(taskArray[3]));
                checkTask.putIfAbsent(taskName, false);
                if(arrival<= timeCounter && !checkTask.get(taskName) && !readyQ.contains(task)) {
                    readyQ.add(0, task);
                    totBurstTime += Integer.parseInt(taskArray[3]);
                }


            }

            if(!readyQ.isEmpty()){

                String currentTask = readyQ.remove(0);
                String[] curTaskArray = currentTask.split(" ");
                String taskName = curTaskArray[0];
                Integer burstTime = taskMap.get(taskName);
                int arrivalTime = Integer.parseInt(curTaskArray[1]);

                try {
                    FileWriter myWriter = new FileWriter("output.txt", true);
                    myWriter.write("Will run Name: " + curTaskArray[0] + "\n");
                    myWriter.write("Priority: " + curTaskArray[2] + "\n");
                    myWriter.write("Burst: " + curTaskArray[3] + "\n\n");
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(burstTime < quantum){
                    timeCounter += burstTime;
                    taskMap.put(taskName,0);
                    totTurnaroundTime += timeCounter - arrivalTime;
                    taskCounter++;
                    checkTask.put(taskName, true);
                    try {
                        FileWriter myWriter = new FileWriter("output.txt", true);
                        myWriter.write("Task " + curTaskArray[0] +" finished"+ "\n\n");
                        myWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {

                    timeCounter += quantum;
                    taskMap.put(taskName,taskMap.get(taskName)-quantum);
                    if(taskMap.get(taskName) == 0){
                        taskCounter++;
                        taskMap.put(taskName,0);
                        totTurnaroundTime += timeCounter-arrivalTime;
                        checkTask.put(taskName, true);
                        try {
                            FileWriter myWriter = new FileWriter("output.txt", true);
                            myWriter.write("Task " + curTaskArray[0] +" finished"+ "\n\n");
                            myWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        readyQ.add(currentTask);
                        try {
                            FileWriter myWriter = new FileWriter("output.txt", true);
                            myWriter.write( "There is a context switch "+ curTaskArray[0] +" is going back to ready queue" + "\n\n");
                            myWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                timeCounter++;
            }
        }
        System.out.println("Average Turnaround Time : "+ (totTurnaroundTime/taskList.size()));
        System.out.println("Average Waiting Time : "+(totTurnaroundTime-totBurstTime)/taskList.size());

    }

    //priority
    private static void pri(List<String> taskList) {
        Map<String, Boolean> checkTask = new HashMap<>();
        int timeCounter = 0;
        int taskCounter = 0;

        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;

        int taskSize = taskList.size();

        while (taskCounter!=taskSize) {
            int taskIndex =taskSize;
            int shortestPriority = Integer.MIN_VALUE;

            for(int i=0; i<taskSize; i++) {
                String task = taskList.get(i);
                checkTask.putIfAbsent(task, false);
                String[] taskArray = task.split(" ");
                int arrivalTime = Integer.parseInt(taskArray[1]);
                int priority = Integer.parseInt(taskArray[2]);
                if(arrivalTime <= timeCounter && !checkTask.get(task) && priority > shortestPriority) {
                    shortestPriority = priority;
                    taskIndex = i;
                }
            }
            if(taskIndex == taskSize ){
                timeCounter++;
            }else
            {
                String[] taskArray=taskList.get(taskIndex).split(" ") ;

                try {
                    FileWriter myWriter = new FileWriter("output.txt", true);
                    myWriter.write("Will run Name: " + taskArray[0] + "\n");
                    myWriter.write("Priority: " + taskArray[2] + "\n");
                    myWriter.write("Burst: " + taskArray[3] + "\n\n");
                    myWriter.write("Finished " + taskArray[0] + "\n\n");
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int burstTime = Integer.parseInt(taskArray[3]);
                int compTime = timeCounter+ burstTime;
                timeCounter += burstTime;
                int turnaroundTime = compTime - Integer.parseInt(taskArray[1]);
                int waitTime = turnaroundTime - burstTime;
                checkTask.put(taskList.get(taskIndex),true);
                taskCounter++;

                totalTurnaroundTime += turnaroundTime;
                totalWaitingTime += waitTime;
            }
        }

        totalTurnaroundTime = totalTurnaroundTime / taskList.size();
        totalWaitingTime = totalWaitingTime / taskList.size();

        System.out.println("Average turnaround: " + totalTurnaroundTime);
        System.out.println("Average waiting: " + totalWaitingTime);
    }

    //shortest job first
    public static void sjf(List<String> taskList) {

        Map<String, Boolean> checkTask = new HashMap<>();
        int timeCounter = 0;
        int taskCounter = 0;

        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;

        int taskSize = taskList.size();

        while (taskCounter!=taskSize) {
            int taskIndex =taskSize;
            int shortestBurst = Integer.MAX_VALUE;

            for(int i=0; i<taskSize; i++) {
                String task = taskList.get(i);
                checkTask.putIfAbsent(task, false);
                String[] taskArray = task.split(" ");
                int arrivalTime = Integer.parseInt(taskArray[1]);
                int burstTime = Integer.parseInt(taskArray[3]);
                if(arrivalTime <= timeCounter && !checkTask.get(task) && burstTime < shortestBurst) {
                    shortestBurst = burstTime;
                    taskIndex = i;
                }
            }
            if(taskIndex == taskSize ){
                timeCounter++;
            }else
            {
                String[] taskArray=taskList.get(taskIndex).split(" ") ;

                try {
                    FileWriter myWriter = new FileWriter("output.txt", true);
                    myWriter.write("Will run Name: " + taskArray[0] + "\n");
                    myWriter.write("Priority: " + taskArray[2] + "\n");
                    myWriter.write("Burst: " + taskArray[3] + "\n\n");
                    myWriter.write("Finished " + taskArray[0] + "\n\n");
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int burstTime = Integer.parseInt(taskArray[3]);
                int compTime = timeCounter+ burstTime;
                timeCounter += burstTime;
                int turnaroundTime = compTime - Integer.parseInt(taskArray[1]);
                int waitTime = turnaroundTime - burstTime;
                checkTask.put(taskList.get(taskIndex),true);
                taskCounter++;

                totalTurnaroundTime += turnaroundTime;
                totalWaitingTime += waitTime;
            }
        }

        totalTurnaroundTime = totalTurnaroundTime / taskList.size();
        totalWaitingTime = totalWaitingTime / taskList.size();

        System.out.println("Average turnaround: " + totalTurnaroundTime);
        System.out.println("Average waiting: " + totalWaitingTime);

    }

    //first come first served
    public static void fcfs(List<String> taskList) {

        Collections.sort(taskList, new Comparator<String>() {
            @Override
            public int compare(String task1, String task2) {
                int arrivalTime1 = Integer.parseInt(task1.split(" ")[1]);
                int arrivalTime2 = Integer.parseInt(task2.split(" ")[1]);

                if(arrivalTime1 < arrivalTime2) {
                    return -1;
                } else if(arrivalTime1 > arrivalTime2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        //  "TaskName arTime priority burstTime compTime"
        //
        int i = 0;

        double totalTurnaround = 0;
        double totalWaitingTime = 0;

        for(String task : taskList) {

            String[] taskArray = new String[5];
            int j = 0;
            for(String str : task.split(" ")) {
                taskArray[j] = str;
                j++;
            }
            //write task to a file
            try {
                FileWriter myWriter = new FileWriter("output.txt", true);
                myWriter.write("Will run Name: " + taskArray[0] + "\n");
                myWriter.write("Priority: " + taskArray[2] + "\n");
                myWriter.write("Burst: " + taskArray[3] + "\n\n");
                myWriter.write("Finished " + taskArray[0] + "\n\n");
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int arivalTime = Integer.parseInt(taskArray[1]);
            int burstTime = Integer.parseInt(taskArray[3]);
            if(i==0) {
                //completion time
                taskArray[4] = Integer.toString(arivalTime + burstTime);
            } else {
                int prevCompTime = Integer.parseInt(taskList.get(i-1).split(" ")[4]);
                if(arivalTime > prevCompTime) {
                    taskArray[4] = Integer.toString(arivalTime + burstTime);
                } else {
                    taskArray[4] = Integer.toString(prevCompTime + burstTime);
                }
            }
            //
            int turnaround = Integer.parseInt(taskArray[4]) - arivalTime;
            totalTurnaround += turnaround;
            int waitingTime = turnaround - burstTime;
            totalWaitingTime += waitingTime;

            taskList.set(i, String.join(" ", taskArray));
            i++;
        }

        totalTurnaround = totalTurnaround / taskList.size();
        totalWaitingTime = totalWaitingTime / taskList.size();

        System.out.println("Average turnaround: " + totalTurnaround);
        System.out.println("Average waiting: " + totalWaitingTime);

    }

}
