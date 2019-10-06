import java.util.LinkedList;

class Task {
    private int id;
    private int time;
    private int staff;
    private String name;
    private int earliestStart;
    private int latestStart;
    private LinkedList<Task> outEdges = new LinkedList<>();
    private int cntPredecessors;
    private LinkedList<Integer> dependencyEdges = new LinkedList<>();
    private boolean DFSActive = false;
    private boolean visited = false;

    public Task(int id, int time, int staff, String name, int cntPredecessors, LinkedList<Integer>depencyEdges) {
        this.id = id;
        this.time = time;
        this.staff = staff;
        this.name = name;
        this.cntPredecessors = cntPredecessors;
        this.dependencyEdges = depencyEdges;
    }
    
    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public int getStaff() {
        return staff;
    }

    public String getName() {
        return name;
    }

    public int getEarliestStart() {
        return earliestStart;
    }

    public int getLatestStart() {
        return latestStart;
    }

    public LinkedList<Task> getOutEdges() {
        return outEdges;
    }

    public LinkedList<Integer> getDependencyEdges() {
        return dependencyEdges;
    }

    public void setDFSActive() {
        DFSActive = true;
    }

    public void setDFSNotActive() {
        DFSActive = false;
    }

    public boolean isActive() {
        return DFSActive;
    }

    public void visit() {
        visited = true;
    }

    public void resetVisit() {
        visited = false;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Add outedge from this task to given task
    public void addOutEdge(Task t) {
        if (!outEdges.contains(t)) {
            outEdges.add(t);
        }
        System.out.println("Added outedge from " + this.getId() + " to " + t.getId());
    }

    // Remove outedge from this task to given task
    public void removeOutEdge(Task t) {
        outEdges.remove(t);
        System.out.println("Removed outedge from: "  + this.getId() + " to " + t.getId());
    }

    @Override
    public String toString() {
        return "ID: " + id + " Time: " + time + " Staff: " + staff + " Name: " + name + " Count predecessors: " + cntPredecessors;
    }
}