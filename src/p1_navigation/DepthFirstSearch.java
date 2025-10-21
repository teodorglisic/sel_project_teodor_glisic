package p1_navigation;

import java.util.*;

public class DepthFirstSearch {


    private static Map<String, ArrayList<MapData.Destination>> adjacencyList = new HashMap<>();


    public static void main(String[] args) {

        MapData data = null;
        try {
            data = new MapData();
        } catch (Exception e) {
            System.out.println("Error reading map data");
        }

        adjacencyList = data.getAdjacencyList();

        long start = System.nanoTime();
        List<String> path = depthFirst("F", "M");
        long end = System.nanoTime();
        System.out.println("Duration: " + ((double)(end - start) / 1000000000) + " seconds");
        printPath(path);
    }

    private static void printPath(List<String> path) {
        System.out.print("Final solution: ");
        for (String node : path) System.out.printf("%s ", node);
        System.out.println();
    }


    private static ArrayList<String> depthFirst(String start, String end) {
        ArrayList<String> path = new ArrayList<>();
        path.add(start);
        System.out.println(path);
        return depthFirstRecursive(path, start, end);
    }


    // Start with B and end with G
    private static ArrayList<String> depthFirstRecursive(ArrayList<String> path, String current, String end) {

        if (Objects.equals(current, end)) {

        } else {
            ArrayList<MapData.Destination> possibleConnections = adjacencyList.get(current);
            for (MapData.Destination destination: possibleConnections) {
                if (!path.contains(destination.node())) {
                    path.add(destination.node());
                    System.out.println(path);
                    depthFirstRecursive(path, destination.node(), end);
                    if (Objects.equals(path.getLast(), end)) {
                        break;
                    } else {
                        path.removeLast();
                    }
                }
            }
        }

        return path;

    }
}
