package p1_navigation;

import java.util.*;

public class BreadthFirstSearch {

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
        List<String> path = breadthFirstSearch("F", "M");
        long end = System.nanoTime();
        System.out.println("Duration: " + ((double)(end - start) / 1000000000) + " seconds");
        printPath(path);



    }

    private static void printPath(List<String> path) {
        System.out.print("Final solution: ");
        for (String node : path) System.out.printf("%s ", node);
        System.out.println();
    }

    public static ArrayList<String> breadthFirstSearch(String start, String end) {
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        ArrayList<String> startingPath = new ArrayList<>();


        startingPath.add(start);
        paths.add(startingPath);

        if (start.equals(end)) {
            return paths.getLast();
        } else {
            boolean solutionFound = false;
            while (paths.size() > 0 && !solutionFound) {
                System.out.println(paths);
                ArrayList<String> oldPath = paths.removeFirst();
                System.out.println(oldPath);
                ArrayList<MapData.Destination> connectedNodes = adjacencyList.get(oldPath.getLast());
                for (MapData.Destination node : connectedNodes) {
                    ArrayList<String> newPath = (ArrayList<String>) oldPath.clone();
                    newPath.add(node.node());
                    paths.add(newPath);

                    if (Objects.equals(node.node(), end)) {
                        solutionFound = true;
                        break;
                    }
                }


            }
            System.out.println(paths);
            return paths.isEmpty() ? null : paths.getLast();
        }
    }
}
