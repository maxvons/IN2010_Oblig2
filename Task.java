import java.util.LinkedList;

class Task {
    private int id;
    private int sortedId;
    private int time;
    private int staff;
    private int earliestStart;
    private int earliestFinish;
    private int latestStart;
    private int latestFinish;
    private int slack;
    private int inCounter;
    private String name;
    private boolean printedFinish = false;
    private boolean printedStart = false;
    private boolean critical = false;
    private boolean addedToList = false;
    private boolean lastSorted = false;
    private boolean DFSActive = false;
    private boolean visited = false;
    private LinkedList<Task> outEdges = new LinkedList<>();
    private LinkedList<Task> inEdges = new LinkedList<>();
    private LinkedList<Integer> dependencyEdges = new LinkedList<>();

    public Task(int id, int time, int staff, String name, int inCounter, LinkedList<Integer>depencyEdges) {
        this.id = id;
        this.time = time;
        this.staff = staff;
        this.name = name;
        this.inCounter = inCounter;
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

    public int getInCounter() {
        return inCounter;
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

    public int getEarliestFinish() {
        return earliestFinish;
    }

    public int getLatestFinish() {
        return latestFinish;
    }

    public LinkedList<Task> getOutEdges() {
        return outEdges;
    }

    public LinkedList<Task> getInEdges() {
        return inEdges;
    }

    public LinkedList<Integer> getDependencyEdges() {
        return dependencyEdges;
    }

    public int getSortedId() {
        return sortedId;
    }

    public void setSortedId(int id) {
        sortedId = id;
    }

    public int getSlack() {
        return slack;
    }

    public void setSlack(int time) {
        slack = time;
    }

    public void setLastSorted() {
        lastSorted = true;
    }

    public boolean isLastSorted() {
        return lastSorted;
    }

    public void setAdded() {
        addedToList = true;
    }

    public boolean isAdded() {
        return addedToList;
    }

    public void reduceInCounter() {
        inCounter--;
    }

    public void setInCounter(int newCount) {
        inCounter = newCount;
    }

    public void setEarliestStart(int time) {
        earliestStart = time;
    }

    public void setLatestStart(int time) {
        latestStart = time;
    }

    public void setEarliestFinish(int time) {
        earliestFinish = time;
    }

    public void setLatestFinish(int time) {
        latestFinish = time;
    }

    public void setPrintedFinish() {
        printedFinish = true;
    }

    public boolean isPrintedFinish() {
        return printedFinish;
    }

    public void setPrintedStart() {
        printedStart = true;
    }

    public boolean isPrintedStart() {
        return printedStart;
    }

    public void setCritical() {
        critical = true;
    }

    public boolean isCritical() {
        return critical;
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

    // Add inedge from given task to this task
    public void addInEdge(Task t) {
        if (!inEdges.contains(t)) {
            inEdges.add(t);
        }
        System.out.println("Added inedge from " + t.getId() + " to " + this.getId());
    }

    // Remove outedge from this task to given task
    public void removeOutEdge(Task t) {
        outEdges.remove(t);
        System.out.println("Removed outedge from: "  + this.getId() + " to " + t.getId());
    }

    @Override
    public String toString() {
        return "ID: " + id + " Time: " + time + " Staff: " + staff + " Name: " + name + " Count predecessors: " + inCounter;
    }
}
