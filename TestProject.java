class TestProject {
    public static void main(String[] args) {
        Project p = Project.buildProject("buildhouse1.txt");
        p.addOutEdges();
        p.printTasks();
        p.printOutEdges();
        // p.topologicalSort();
        
        // Test without cycle.
        p.isRealizable();
        p.resetVisited();
        p.resetActive();

        // Test with more advanced cycle
        // p.addEdge(4, 3);
        // p.addEdge(3, 4);
        // System.out.println("\n\nADVANCED CYCLE");
        // p.isRealizable();
        // p.resetActive();
        // p.resetVisited();
        

        // Test with cycle
        // p.addEdge(3, 1);
        // p.isRealizable();
        // p.resetVisited();
        // p.resetActive();
        // Test with cycle
        p.addEdge(3, 5);
        p.isRealizable();
        p.resetVisited();
        p.resetActive();

        // Test with another cycle
        // p.addEdge(8, 7);
        // p.addDependencyEdge(7, 8);
        // p.isRealizable();
    }
}