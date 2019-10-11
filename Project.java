import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Queue;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class Project {
    private String name;
    private int numTasks;
    private LinkedList<Task> tasks;
    private LinkedList<Task> whiteSet = new LinkedList<>();
    private LinkedList<Task> graySet = new LinkedList<>();
    private LinkedList<Task> blackSet = new LinkedList<>();

    public Project(String name, int numTasks, LinkedList<Task> tasks) {
        this.name = name;
        this.numTasks = numTasks;
        this.tasks = tasks;
    }
    
    public static Project buildProject(String filename) {
        File projectFile = new File(filename);
        Scanner scan = null;
        int taskId = 0;
        String taskName = null;
        int taskTime = 0;
        int taskManPower = 0;
        int dependencyEdge = 0;
        LinkedList<Task> taskList = new LinkedList<>();
        LinkedList<Integer> dependencyEdges = new LinkedList<>();
        
        try {
            scan = new Scanner(projectFile);
        }   catch (FileNotFoundException e) {
            System.out.println("Could not find the file.");
        }

        int numTasks = scan.nextInt();

        // Creating tasks and adding to list without adding dependencies
        while (scan.hasNextLine()) {
            taskId = scan.nextInt();
            taskName = scan.next();
            taskTime = scan.nextInt();
            taskManPower = scan.nextInt();
            dependencyEdge = scan.nextInt();
            dependencyEdges = new LinkedList<>();

            while (dependencyEdge != 0) {
                dependencyEdges.add(dependencyEdge);
                dependencyEdge = scan.nextInt();
            }

            taskList.add(new Task(taskId, taskTime, taskManPower, taskName, dependencyEdges.size(), dependencyEdges));              
        }

        Project p = new Project(filename, numTasks, taskList);
        return p;
    }

    // Add outedges to all the tasks in the project
    public void addOutEdges() {
        for (Task t : tasks) {
            for (int e : t.getDependencyEdges()) {
                getTaskById(e).addOutEdge(t);
            }
        }
    }

    // Add inEdges to all the tasks in the project
    public void addInEdges () {
        for (Task t : tasks) {
            for (int i : t.getDependencyEdges()) {
                Task p = getTaskById(i);
                t.addInEdge(p);
            }
        }
    }

    // Return task with given id
    public Task getTaskById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public Task getFirstTask() {
        return tasks.getFirst();
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }

    public void resetVisited() {
        for (Task t : tasks) {
            t.resetVisit();
        }
    }

    public void resetActive() {
        for (Task t : tasks) {
            t.setDFSNotActive();
        }
    }

    // Reset the sets used in findCycle method
    public void resetSets() {
        whiteSet = new LinkedList<>();
        graySet = new LinkedList<>();
        blackSet = new LinkedList<>();
    }

    // Detect cycle in graph
    public boolean containsCycle(Task s) {
        s.setDFSActive();

        for (Task t : s.getOutEdges()) {
            if (t.isActive()) {
                // Backedge exists
                return true;
            }   else if (!t.isVisited() && containsCycle(t)) {
                return true;
            }
        }
 
        s.setDFSNotActive();
        s.visit();
        return false;
    }

    public void fillWhiteSet() {
        for (Task t : tasks) {
            whiteSet.add(t);
        }
    }

    // Check if two tasks are codependent
    public boolean checkCodependency(LinkedList<Task> taskSet) {
        for (Task t : taskSet) {
            for (Task t2 : t.getOutEdges()) {
                if (t2.getOutEdges().contains(t)) {
                    System.out.println("Codependency between tasks detected.");
                    System.out.println("Task with ID: " + t2.getId());
                    System.out.println("Task with ID: " + t.getId());
                    return true;
                }
            }
        }
        return false;
    }

    // Find and print cycle in graph if such a cycle exists
    public boolean findCycle(Task s) {
        if (!containsCycle(getFirstTask())) {
            System.out.println("No cycle exists.");
            return false;
        }

        graySet.add(s);

        for (Task t : s.getOutEdges()) {
            if (graySet.contains(t)) {
                // Cycle found
                System.out.println("Cycle found.");
                if (checkCodependency(graySet)) {
                    // Let the checkCodependency method print info
                }   else {
                    for (Task ta : graySet) {
                        System.out.println(ta.getId());
                    }
                }
                return true;
            }   else if (whiteSet.contains(t) && findCycle(t)) {
                return true;
            }
        }
 
        graySet.remove(s);
        whiteSet.remove(s);
        blackSet.add(s);
        return false;
    }

    // Use cycle detection method to determine if project is realizable
    public boolean isRealizable() {
        if (containsCycle(getFirstTask())) {
            System.out.println("Project is not realizable.");
            resetVisited();
            resetActive();
            return false;
        }   else {
            System.out.println("Project is realizable.");
            resetVisited();
            resetActive();
            return true;
        }
    }

    // Return a sorted queue if no cycle present
    public Queue<Task> topologicalSort() {
        Stack<Task> stack = new Stack<>();
        Queue<Task> queue = new LinkedList<>();
        int inCounter = 0;

        if (isRealizable()) {
            for (Task t : tasks) {
                inCounter = t.getDependencyEdges().size();
                if (inCounter == 0) {
                    stack.push(t);
                }
            }

            int i = 1;

            while (!(stack.isEmpty())) {
                Task v = stack.pop();
                int temp = v.getId();
                v.setId(i);
                queue.add(v);
                i++;

                for (Task d : v.getOutEdges()) {
                    d.reduceInCounter();
                    if (d.getInCounter() == 0) {
                        d.setInCounter(d.getDependencyEdges().size());
                        stack.push(d);
                    }
                }
            }

            System.out.println("Sorted project.");
            return queue;
        }   else {
            System.out.println("Project contains cycle. Could not sort.");
            return null;
        }
    }

    // Add the earliest start time to all tasks in a sorted project
    public void addEarliestStart(Queue<Task> sortedProject) {
        int time = 0;
        int temp = 0;
        Task slowest = null;

        for (Task t : sortedProject) {
            if (t.getInCounter() == 0) {
                t.setEarliestStart(0);
            }   else {
                time = 0;
                for (Task p : t.getInEdges()) {
                    temp = p.getTime();
                    if (temp > time) {
                        time = temp; 
                        slowest = p;
                    }
                }
                t.setEarliestStart(time + slowest.getEarliestStart());
            }
        }
    }

    // Add the earliest finishing time for each task in sorted project
    public void addEarliestFinish(Queue<Task> sortedProject) {
        int time = 0;
        int temp = 0;
        Task slowest = null;

        for (Task t : sortedProject) {
            if (t.getInCounter() == 0) {
                t.setEarliestFinish(t.getTime());
            }   else {
                time = 0;
                for (Task p : t.getInEdges()) {
                    temp = p.getTime();
                    if (temp > time) {
                        time = temp; 
                        slowest = p;
                    }
                }
                t.setEarliestFinish(time + slowest.getEarliestStart() + t.getTime());
            }
        }
    }

    // Get the fastest task in a given list 
    public Task getFastestSameStart(LinkedList<Task> taskList) {
        int temp = 0;
        Task fastest = null;

        if (taskList.size() == 1) {
            return taskList.get(0);
        }

        int time = taskList.get(0).getTime();
        for (Task t : taskList) {
            temp = t.getTime();
            if (temp <= time) {
                time = temp;
                fastest = t;
            }
        }
        
        return fastest;
    }

    // Get the slowest task in a given list
    public Task getSlowestSameStart(LinkedList<Task> taskList) {
        int temp = 0;
        Task slowest = null;

        if (taskList.size() == 1) {
            return taskList.get(0);
        }

        int time = taskList.get(0).getTime();
        for (Task t : taskList) {
            temp = t.getTime();
            if (temp >= time) {
                time = temp;
                slowest = t;
            }
        }

        return slowest;
    }

    /* Helper method for figuring out if a list contains tasks with different 
    start times
    */
    public boolean differentStarts(LinkedList<Task> taskList) {
        boolean different = false;
        int start = taskList.get(0).getEarliestStart();

        if (taskList.size() == 1) {
            return false;
        }
        for (Task t : taskList) {
            if (t.getEarliestStart() != start) {
                different = true;
            }
        }

        return different; 
    }

    // Prints the shortest possible project execution time
    public void printTimeSchedule(Queue<Task> sortedProject) {
        int time = 0;
        int start = 0;
        int currentStaff = 0;
        Task task = null;
        boolean timePrint = false;
        boolean done = false;
        LinkedList<Task> timeTasks = null;
        LinkedList<Task> startedTasks = new LinkedList<>();
        LinkedList<Task> finishedTasks = new LinkedList<>();
        LinkedList<Task> tasks = new LinkedList<>();

        // Fill temp list
        int i = 0;
        for (Task t : sortedProject) {
            tasks.add(i, t);
            i++;
        }

        while (!done) {
                if (sortedProject.peek() != null) {
                    task = sortedProject.remove();
                }

                timeTasks = new LinkedList<>();
                currentStaff = 0;
                start = task.getEarliestStart();

                timeTasks.add(task);
                if (!(startedTasks.contains(task))) {
                    startedTasks.add(task);
                }

                for (Task t : sortedProject) {
                    if (t.getEarliestStart() == start) {
                        timeTasks.add(t);
                        startedTasks.add(t);
                    }
                }

                for (Task t : timeTasks) {
                    sortedProject.remove(t);
                }

                // Print info
                timePrint = false;
                for (Task t : tasks) {
                    if (startedTasks.contains(t) || finishedTasks.contains(t)) {
                        if ((startedTasks.contains(t)) && (!finishedTasks.contains(t))) {
                            if (!timePrint) {
                                System.out.print("Time: " + t.getEarliestStart());
                                timePrint = true;
                            }
                            if (t.getEarliestStart() <= time && !t.isPrintedStart()) {
                                System.out.println("       Starting task: " + t.getId());
                                t.setPrintedStart();
                                if (!finishedTasks.contains(t)) {
                                    finishedTasks.add(t);
                                }
                            }
                            if (t.isPrintedStart()) {
                                startedTasks.remove(t);
                            }
                        }   else if (finishedTasks.contains(t) && (!startedTasks.contains(t)) && (!(t == tasks.getLast()))) {
                            if (!timePrint && !differentStarts(finishedTasks)) { 
                                Task fastest = getFastestSameStart(finishedTasks);
                                time = fastest.getEarliestFinish();
                                System.out.print("Time: " + time);
                                timePrint = true;
                            }   else if (!timePrint && differentStarts(finishedTasks)) {
                                time = t.getEarliestFinish();
                                System.out.print("Time: " + time);
                                timePrint = true;
                            }
                            if (t.getEarliestFinish() <= time) {
                                System.out.println("      Finishing task: " + t.getId());
                                t.setPrintedFinish();
                            }
                            if (t.isPrintedFinish()) {
                                finishedTasks.remove(t);
                            }
                        }   else if (finishedTasks.contains(t) && (t == tasks.getLast())) {
                            time = t.getEarliestFinish();
                            System.out.print("Time: " + time);
                            timePrint = true;
                            finishedTasks.remove(t);
                            System.out.println("      Finishing task: " + t.getId());
                            t.setPrintedFinish();
                            done = true;
                        }
                    }
                }
    
                for (Task t : tasks) {
                    if (t.isPrintedStart() && !t.isPrintedFinish()) {
                        currentStaff += t.getStaff();
                    }
                    
                }

                System.out.println("              Current staff: " + currentStaff);
                System.out.println();
            }
        System.out.println("**** Shortest possible project execution is " + (tasks.getLast().getEarliestFinish() + " ****"));
    }

    // Mark tasks in the critical path as critical
    public void markCritical(Queue<Task> longestPath) {
        for (Task t : longestPath) {
            t.setCritical();
        }
    }

    // Get the longest path in a graph (critical path)
    public Queue<Task> getLongestPath(Queue<Task> sortedProject) {
        LinkedList<Task> timeTasks = null;
        Queue<Task> longestPath = new LinkedList<>();
        LinkedList<Task> tasks = new LinkedList<>();
        Task task = null;
        Task slowest = null;
        int start = 0;
        boolean done = false;
        
        for (Task t : sortedProject) {
            tasks.add(t);
        }

        while (!done) {
            if (sortedProject.peek() != null) {
                task = sortedProject.remove();
            }
            
            if (tasks.getLast() == task) {
                done = true;
            }

            timeTasks = new LinkedList<>();
            start = task.getEarliestStart();

            timeTasks.add(task);
            
            // Add tasks with the same start time to a list
            for (Task t : sortedProject) {
                if (t.getEarliestStart() == start && !timeTasks.contains(t)) {
                    timeTasks.add(t);
                }
            }
            
            slowest = getSlowestSameStart(timeTasks);

            if (!longestPath.contains(slowest) && !slowest.isAdded()) {
                longestPath.add(slowest);
            }

            for (Task t : timeTasks) {
                t.setAdded();
            }
        }

        markCritical(longestPath);

        return longestPath;
    }   

    // Get the task with the smallest latestStart value in a given list
    public Task getFastestLatestStart(LinkedList<Task> taskList) {
        int temp = 0;
        Task fastest = null;

        if (taskList.size() == 1) {
            return taskList.get(0);
        }

        int time = taskList.get(0).getTime();
        for (Task t : taskList) {
            temp = t.getTime();
            if (temp <= time) {
                time = temp;
                fastest = t;
            }
        }
        
        return fastest;
    }

    // Add latestStart to tasks in sorted project
    public void addLatestStart(Queue<Task> sortedProject) {
        LinkedList<Task> sortedTasks = new LinkedList<>();
        LinkedList<Task> outEdges = new LinkedList<>();
        Queue<Task> criticalPath = new LinkedList<>();
        int latestFinish = 0;
        Task task = null;
        
        // Add to different list for easier operation access
        for (Task t : sortedProject) {
            sortedTasks.add(t);
        }

        // Last task in sorted list
        Task last = sortedTasks.getLast();

        // Get the critical path
        criticalPath = getLongestPath(sortedProject);

        // Set a boolean for last task in sorted project
        sortedTasks.getLast().setLastSorted();

        while (!sortedTasks.isEmpty()) {
            task = sortedTasks.getLast();
            
            if (task.isLastSorted()) {  // If last task in sorted project
                task.setLatestFinish(task.getEarliestFinish());
                task.setLatestStart(task.getEarliestStart());
            }   else {
                outEdges = task.getOutEdges();
                if (outEdges.size() == 0 && !task.isLastSorted()) {
                    latestFinish = last.getEarliestFinish();
                    task.setLatestFinish(latestFinish);
                    task.setLatestStart(latestFinish - task.getTime());
                }   else if (outEdges.size() == 1) {
                    if (criticalPath.contains(task)) {
                        task.setLatestFinish(task.getEarliestFinish());
                        task.setLatestStart(task.getEarliestStart());
                    }   else {
                        latestFinish = outEdges.getFirst().getLatestStart();
                        task.setLatestFinish(latestFinish);
                        task.setLatestStart(latestFinish - task.getTime());
                    }
                }   else if (outEdges.size() > 1) {
                    if (criticalPath.contains(task)) {
                        task.setLatestFinish(task.getEarliestFinish());
                        task.setLatestStart(task.getEarliestStart());
                    }   else {
                        latestFinish = getFastestLatestStart(outEdges).getLatestStart();
                        task.setLatestFinish(latestFinish);
                        task.setLatestStart(latestFinish - task.getTime());
                    }

                }
            }
            sortedTasks.remove(task);
        }
    }

    // Calculate slack for all the tasks in the project
    public void calculateSlack(Queue<Task> sortedProject) {
        for (Task t : sortedProject) {
            if (t.isCritical()) {
                t.setSlack(0);
            }   else {
                t.setSlack(t.getLatestStart() - t.getEarliestStart());
            }
        }
    }

    public void printProjectInfo(Queue<Task> sortedProject) {
        for (Task t : sortedProject) {
            if (t.isCritical()) {
                System.out.println("Task ID: " + t.getId() + " (critical)");
            }   else {
                System.out.println("Task ID: " + t.getId());
            }
            System.out.println("Task name: " + t.getName());
            System.out.println("Task time: " + t.getTime());
            System.out.println("Task staff required: " + t.getStaff());
            System.out.println("Earliest starting time: " + t.getEarliestStart());
            System.out.println("Slack: " + t.getSlack());
            if (t.getOutEdges().size() > 0) {
                System.out.println("Tasks that depend on this task:");
                for (Task o : t.getOutEdges()) {
                    System.out.println("ID: " + o.getId());
                }   
            }   else {
                System.out.println("No tasks depend on this task.");
            }
            System.out.println("\n\n");
        }
    }


    // Print method for testing purposes
    public void printTasks() {
        for (Task t : tasks) {
            System.out.println(t);
        }
    }

    // Print method for testing purposes
    public void printSortedTasks(Queue<Task> taskQueue) {
        for (Task t : taskQueue) {
            System.out.println(t);
        }
    }

    // Print method for testing purposes
    public void printOutEdges() {
        for (Task t : tasks) {
            System.out.print(t.getId() + " has outedges: ");
            for (Task e : t.getOutEdges()) {
                System.out.print(e.getId() + " ");
            }
            System.out.println();
        }
    }

    // Method for adding outedge for testing purposes
    public void addEdge(int id1, int id2) {
        Task t1 = getTaskById(id1);
        Task t2 = getTaskById(id2);

        t1.addOutEdge(t2);
    }

    // Method for adding dependency edge for testing purposes
    public void addDependencyEdge(int id1, int id2) {
        Task t1 = getTaskById(id1);
        Task t2 = getTaskById(id2);

        t1.getDependencyEdges().add(id2);
    }

    // For fuck sake m8
    public void autism() {
        String yeet = "YEE";

        while (true) {
            yeet += "E";
            System.out.println(yeet);
        }
    }
}
