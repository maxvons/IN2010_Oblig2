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

        // Skip line.
        String line = scan.nextLine();
        line = scan.nextLine();

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

    public void resetSets() {
        whiteSet = new LinkedList<>();
        graySet = new LinkedList<>();
        blackSet = new LinkedList<>();
    }

    public int getActiveTasks() {
        int active = 0;
        
        for (Task t : tasks) {
            if (t.isActive()) {
                active++;
            }
        }

        return active;
    }

    public int getVisitedTasks() {
        int visited = 0;

        for (Task t : tasks) {
            if (t.isVisited()) {
                visited++;
            }
        }

        return visited;
    }

    // Get the predecessors of a task from id/integer depedencyEdges
    public void addPredecessors () {
        LinkedList<Task> predecessors = new LinkedList<>();

        for (Task t : tasks) {
            for (int i : t.getDependencyEdges()) {
                Task p = getTaskById(i);
                t.addInEdge(p);
            }
        }
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
                System.out.println("Added task with original ID: " + temp);
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
        int earliestStart = 0;
        LinkedList<Task> predecessors = new LinkedList<>();

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

    // Get the earliestStart value of the task with the highest value
    public int getLastStart() {
        int lastStart = 0;
        int temp = 0;

        for (Task t : tasks) {
            temp = t.getEarliestStart();
            if (temp > lastStart) {
                lastStart = temp;
            }
        }

        return lastStart;
    }

    // Prints the shortest possible project execution time
    public void printTimeSchedule(Queue<Task> sortedProject, int lastStart) {
        int totTime = 0;
        int start = 0;
        int currentStaff = 0;
        Task task = null;
        LinkedList<Task> timeTasks = null;

        while (start < lastStart) {
                if (sortedProject.peek() != null) {
                    task = sortedProject.remove();
                }

                timeTasks = new LinkedList<>();
                currentStaff = 0;
                start = task.getEarliestStart();

                timeTasks.add(task);
                
                for (Task t : sortedProject) {
                    if (t.getEarliestStart() == start) {
                        timeTasks.add(t);
                    }
                }

                for (Task t : timeTasks) {
                    sortedProject.remove(t);
                }

                System.out.print("Time: " + totTime);
                for (Task t : timeTasks) {
                    currentStaff += t.getStaff();
                    System.out.println("       Starting: Task " + t.getId() + "(" + t.getName() + ")");
                    totTime += t.getTime();
                }
                System.out.println("       Current staff: " + currentStaff);
                System.out.println();
            }
    }

    // Print method for testing purposes
    public void printTasks() {
        for (Task t : tasks) {
            System.out.println(t);
        }
    }

    public void printSortedTasks(Queue<Task> taskQueue) {
        for (Task t : taskQueue) {
            System.out.println(t);
        }
    }

    // Print method for testing purposes.
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
