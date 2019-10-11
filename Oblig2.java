import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;

class Oblig2 {
    public static void main(String[] args) {
        Project p = Project.buildProject(args[0]);
        p.addOutEdges();
        System.out.println("\n");
        p.addInEdges();
        System.out.println("\n\n");
        p.printTasks();
        System.out.println("\n\n");
        p.printOutEdges();
        System.out.println("\n\n\n\n\n");
        // p.addEdge(3, 1);
        p.addEdge(3, 5);
        
        // Test without cycle.
        if (p.isRealizable()) {
            System.out.println();
            Queue<Task> test = p.topologicalSort();
            p.addEarliestStart(test);
            p.addEarliestFinish(test);
            System.out.println("\n\n\n");
            System.out.println("TIME SCHEDULE:");
            System.out.println();
            p.printTimeSchedule(test);
            System.out.println("\n\n\n");
            test = p.topologicalSort();
            p.addLatestStart(test);
            test = p.topologicalSort();
            p.calculateSlack(test);
            p.printProjectInfo(test);
        }   else {
            System.out.println("Project can not be realized due to containing at least one cycle.");
            p.fillWhiteSet();
            p.findCycle(p.getFirstTask());
        }
        // p.addEarliestStart(test);
        // p.addEarliestFinish(test);
        // p.printSortedTasks(test);
        
        // p.printTimeSchedule(test);
        // test = p.topologicalSort();
        // p.addLatestStart(test);
        // test = p.topologicalSort();
        // p.calculateSlack(test);
          
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