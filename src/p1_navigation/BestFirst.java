package p1_navigation;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class BestFirst {

    /**
     * We use an inner class "Path" because our paths not only contain a list of nodes visited,
     * and also a heuristic value: how close is the last node to the final goal?
     */
    private record Path(ArrayList<String> nodes, long distanceToGoal) {};

    private static Map<String, ArrayList<MapData.Destination>> adjList;
    private static Map<String, MapData.GPS> nodeList;

    public static void main(String[] args) {
        MapData data = null;
        try {
            data = new MapData();
        } catch (Exception e) {
            System.out.println("Error reading map data");
        }
        adjList = data.getAdjacencyList();
        nodeList = data.getNodes();
        long start = System.nanoTime();
        Path path = bestFirst("J", "Z");
        long end = System.nanoTime();
        System.out.println("Duration: " + ((double)(end - start) / 1000000000) + " seconds");
        printPath(path.nodes);
    }

    private static void printPath(ArrayList<String> path) {
        System.out.print("Final solution: ");
        for (String node : path) System.out.printf("%s ", node);
        System.out.println();
    }

    private static Path bestFirst(String start, String end) {
        // Create path list and add a starting path to the list
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<String> startingNodeList = new ArrayList<>(Arrays.asList(start));
        paths.add(new Path(startingNodeList, distanceBetween(start, end))); // Add starting path to list

        boolean solutionFound = false;
        while (!solutionFound && paths.size() > 0) {
            // Find the path whose end-node is closest to our goal
            // Remove this path from the list, and use it for the next step
            int bestIndex = bestPath(paths, end);
            Path oldPath = paths.remove(bestIndex);

            // Extend it in all possible ways, adding each new path to the end of the list
            // Omit any paths that would re-visit an old node
            ArrayList<String> oldNodes = oldPath.nodes;
            String lastNode = oldNodes.get(oldNodes.size() - 1);
            ArrayList<MapData.Destination> connectedNodes = adjList.get(lastNode);
            for (MapData.Destination d : connectedNodes) {
                if (!oldNodes.contains(d.node())) {
                    ArrayList<String> newNodes = (ArrayList<String>) oldNodes.clone();
                    newNodes.add(d.node());
                    paths.add(new Path(newNodes, distanceBetween(d.node(), end)));
                    if (d.node().equals(end)) {
                        solutionFound = true;
                        break; // Otherwise continue searching
                    }
                }
            }
        }
        return paths.size() == 0 ? null : paths.get(paths.size() - 1);
    }

    /**
     * We assume that the list of paths is not empty. Find the path whose end-node is
     * closest to the goal
     */
    private static int bestPath(ArrayList<Path> paths, String goal) {
        int bestPath = 0;
        long smallestDistance = paths.get(0).distanceToGoal;
        for (int i = 1; i < paths.size(); i++) {
            if (paths.get(i).distanceToGoal < smallestDistance) {
                bestPath = i;
                smallestDistance = paths.get(i).distanceToGoal;
            }
        }
        return bestPath;
    }

    /**
     * Return the distance between the end-node of the path and the stated goal.
     * We calculate X*X + Y*Y, but we do not bother with the square root. Why?
     * Because calculating the square-root is slow, and doesn't change anything
     * for this heuristic. So we really return the *square* of the distance.
     */
    private static long distanceBetween(String node, String goal) {
        MapData.GPS lastPos = nodeList.get(node);
        MapData.GPS goalPos = nodeList.get(goal);
        long xDiff = lastPos.east() - goalPos.east();
        long yDiff = lastPos.north() - goalPos.north();
        return xDiff * xDiff + yDiff * yDiff;
    }
}
