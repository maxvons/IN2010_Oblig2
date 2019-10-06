import java.util.LinkedList;
import java.util.ArrayList;
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
    public void isRealizable() {
        if (containsCycle(getFirstTask())) {
            System.out.println("Project is not realizable.");
        }   else {
            System.out.println("Project is realizable.");
        }
    }

    // TODO...
    public LinkedList<Task> topologicalSort() {
        LinkedList<Task> stack = new LinkedList<>();
        int inCounter = 0;
        for (Task t : tasks) {
            inCounter = t.getDependencyEdges().size();
            System.out.println(inCounter);
            if (inCounter == 0) {
                stack.push(t);
                System.out.println("Pushed " + t.getId() + " onto the stack");
            }
        }

        int i = 0;

        while (!(stack.isEmpty())) {
            Task v = stack.pop();
            v.setId(i);
            System.out.println("ID: " + v.getId());
            i++;

            for (Task d : v.getOutEdges()) {
                System.out.println("Outedge id: " + d.getId());
                inCounter = d.getDependencyEdges().size();
                inCounter = inCounter - 1;
                if (inCounter == 0) {
                    stack.push(d);
                    System.out.println("Pushed " + d.getId() + " onto the stack");
                }
            }
        }
        for (Task t : stack) {
            System.out.println(t.getId());
        }
        System.out.println(i);
        if (i > tasks.size()) {
            return stack;
        }
        System.out.println("The graph contains a cycle.");;
        return null;
    }
    

    // Print method for testing purposes
    public void printTasks() {
        for (Task t : tasks) {
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

    // Adding edge to make cycle for specific graph for testing purposes.
    public void addEdge(int id1, int id2) {
        Task t1 = getTaskById(id1);
        Task t2 = getTaskById(id2);

        t1.addOutEdge(t2);
    }

    public void addDependencyEdge(int id1, int id2) {
        Task t1 = getTaskById(id1);
        Task t2 = getTaskById(id2);

        t1.getDependencyEdges().add(id2);
    }
}
