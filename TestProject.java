import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

class TestProject {
    public static void main(String[] args) {
        Project p = Project.buildProject("buildhouse1.txt");
        p.addOutEdges();
        p.addInEdges();
        p.printTasks();
        p.printOutEdges();
        // p.topologicalSort();
        
        // Test without cycle.
        p.isRealizable();
        Queue<Task> test = p.topologicalSort();
        p.addEarliestStart(test);
        p.addEarliestFinish(test);
        p.printSortedTasks(test);
        
        p.printTimeSchedule(test, p.getLastStart());

        // Test with more advanced cycle
        // p.addEdge(4, 3);
        // p.addEdge(3, 4);
        // System.out.println("\n\nADVANCED CYCLE");
        // // p.isRealizable();
        // p.resetActive();
        // p.resetVisited();
        // p.fillWhiteSet();
        // p.findCycle(p.getFirstTask());
        // p.checkCodependency(p.getTasks());
        

        // Test with cycle
        // p.addEdge(3, 1);
        // p.isRealizable();
        // p.fillWhiteSet();
        // p.findCycle(p.getFirstTask());
        // Queue<Task> test = p.topologicalSort();


        // Test with cycle
        // p.fillWhiteSet();
        // p.addEdge(3, 5);
        // p.isRealizable();
        // p.findCycle(p.getFirstTask());
        // Queue<Task> test = p.topologicalSort();
        // Test with another cycle
        // p.addEdge(8, 7);
        // p.addDependencyEdge(7, 8);
        // p.checkCodependency(p.getTasks());
        // p.isRealizable();
        // p.fillWhiteSet();
        // p.findCycle(p.getFirstTask());
    }
}
