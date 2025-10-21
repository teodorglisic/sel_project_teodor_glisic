package p1_navigation;

import java.util.*;

public class AStar {

    /**
     * We use an inner class "Path" because our paths not only contain a list of nodes visited,
     * and also a heuristic value: how close is the last node to the final goal?
     */
    private record Path(ArrayList<String> nodes, double distanceSoFar, double distanceToGoal) {};

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
        List<String> path = aStar("F", "G");
        long end = System.nanoTime();
        System.out.println("Duration: " + ((double)(end - start) / 1000000000) + " seconds");
        printPath(path);

    }

    private static void printPath(List<String> path) {
        System.out.print("Final solution: ");
        for (String node : path) System.out.printf("%s ", node);
        System.out.println();
    }

    /**
     * We use an inner class that - for a given node - represents the state of a node in our current search.
     * This contains the name of the node,, the distance travelled so far, the heuristic
     * distance to Goal, and the previous node in the path that arrived at this node (with
     * the named distance). Note that a node may be reachable by many paths, but we only
     * retain the information for the path (previous node) that produces the smalled distance.
     */
    private static class NodeInfo {
        double distanceSoFar;
        double distanceToGoal;
        String previousNode;

        public NodeInfo(double distanceSoFar, double distanceToGoal, String previousNode) {
            this.distanceSoFar = distanceSoFar;
            this.distanceToGoal = distanceToGoal;
            this.previousNode = previousNode;
        }
    }

    /**
     * The class above is used in a map, where the key is the name of the node.
     */
    private static Map<String, NodeInfo> searchInfo = new HashMap<>();

    /**
     * This is the list of nodes that we need to expand
     */
    private static ArrayList<String> queue = new ArrayList<>();

    private static List<String> aStar(String start, String end) {
        // Initialize the search-info with the start node
        searchInfo.put(start, new NodeInfo(0, distanceBetween(start, end), null));
        // Initialize the queue
        queue.add(start);

        String currentNode = findNodeLowestCost();
        while (currentNode != null && !currentNode.equals(end)) {
            // Look at all nodes that we can travel to.
            // - If a node is not in searchInfo, add it
            // - If a node is in search info, and we can lower its distanceTravelled, update it
            ArrayList<MapData.Destination> connectedNodes = adjList.get(currentNode);
            for (MapData.Destination d : connectedNodes) {
                double distance = d.distance() + searchInfo.get(currentNode).distanceSoFar;
                if (searchInfo.containsKey(d.node())) {
                    NodeInfo nodeInfo = searchInfo.get(d.node());
                    if (distance < nodeInfo.distanceSoFar) {
                        nodeInfo.distanceSoFar = distance;
                        nodeInfo.previousNode = currentNode;
                        queue.add(d.node()); // Add into the queue, only if cost has been reduced
                    }
                } else {
                    NodeInfo nodeInfo = new NodeInfo(distance, distanceBetween(d.node(), end), currentNode);
                    searchInfo.put(d.node(), nodeInfo);
                    queue.add(d.node());
                }
            }
            currentNode = findNodeLowestCost();
        }

        if (currentNode == null) {
            return null; // no solution found
        } else {
            return reconstructPath(start, end);
        }
    }

    /**
     * Within searchInfo, find the next node to expand. We look at all nodes in the queue,
     * and choose the one whose projected cost (sum of distanceSoFar and distanceToGoal)
     * is the smallest. We remove the selected node from the queue.
     * @return Name of node
     */
    private static String findNodeLowestCost() {
        double shortestDistance = Double.MAX_VALUE;
        String result = null;
        for (String node : queue) {
            NodeInfo nodeInfo = searchInfo.get(node);
            if ( (nodeInfo.distanceSoFar + nodeInfo.distanceToGoal) < shortestDistance) {
                result = node;
                shortestDistance = nodeInfo.distanceSoFar + nodeInfo.distanceToGoal;
            }
        }
        if (result != null) queue.remove(result);
        return result;
    }

    /**
     * Reconstruct the path, from end badk to start.
     * Then reverse it to give the path forward.
     */
    private static List<String> reconstructPath(String start, String end) {
        ArrayList<String> nodeList = new ArrayList<>();
        nodeList.add(end);
        String currentNode = end;
        while (currentNode != start) {
            NodeInfo nodeInfo = searchInfo.get(currentNode);
            currentNode = nodeInfo.previousNode;
            nodeList.add(currentNode);
        }
        Collections.reverse(nodeList);
        return nodeList;
    }

    /**
     * Return the distance between the end-node of the path and the stated goal.
     * We calculate X*X + Y*Y, but we do not bother with the square root. Why?
     * Because calculating the square-root is slow, and doesn't change anything
     * for this heuristic. So we really return the *square* of the distance.
     */
    private static double distanceBetween(String node, String goal) {
        MapData.GPS lastPos = nodeList.get(node);
        MapData.GPS goalPos = nodeList.get(goal);
        long xDiff = lastPos.east() - goalPos.east();
        long yDiff = lastPos.north() - goalPos.north();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
}
