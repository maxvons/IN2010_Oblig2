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
        
        if (p.isRealizable()) {
            System.out.println();
            Queue<Task> test = p.topologicalSort();
            p.printSortedTasks(test);
            p.addEarliestStartFinish(test);
            System.out.println("\n\n\n");
            System.out.println("--- TIME SCHEDULE ---");
            System.out.println();
            p.printTimeScheduleFixed(test);
            System.out.println("\n\n\n");
            test = p.topologicalSort();
            p.addLatestStart(test);
            test = p.topologicalSort();
            p.calculateSlack(test);
            System.out.println("\n\n\n");
            System.out.println("--- TASK INFO FOR SORTED PROJECT ---");
            System.out.println();
            p.printProjectInfo(test);
        }   else {
            System.out.println("Project can not be realized due to containing at least one cycle.");
            p.fillWhiteSet();
            p.findCycle(p.getFirstTask());
        }
    }
}
