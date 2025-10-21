package p1_navigation;

import java.util.*;

public class DijkstraSearch {

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
        List<String> path = dijkstraSearch("D", "Z");
        long end = System.nanoTime();
        System.out.println("Duration: " + ((double)(end - start) / 1000000000) + " seconds");
        printPath(path);
    }

    private static void printPath(List<String> path) {
        System.out.print("Final solution: ");
        for (String node : path) System.out.printf("%s ", node);
        System.out.println();
    }

    public static ArrayList<String> dijkstraSearch(String start, String end) {
        Hashtable<String, Double> distances = new Hashtable<>();
        ArrayList<String> visited = new ArrayList<>();
        Map<String, String> previous = new HashMap<>();
        ArrayList<String> result = new ArrayList<>();
        PriorityQueue<MapData.Destination> queue = new PriorityQueue<>();

        distances.put(start, 0.0);
        queue.add(new MapData.Destination(start, 0));

        for (String key: adjacencyList.keySet()) {
            if (!key.equals(start)) {
                distances.put(key, Double.MAX_VALUE);
            }
        }

        while (!queue.isEmpty()) {
            MapData.Destination toRemove = queue.poll();
            if (visited.contains(toRemove.node())) {
                continue;
            }
            visited.add(toRemove.node());

            if (toRemove.node().equals(end)) {
                break;
            }

            for (MapData.Destination d: adjacencyList.get(toRemove.node())) {
                double newDistance = toRemove.distance() + d.distance();
                if (newDistance < distances.get(d.node())) {
                    distances.put(d.node(), newDistance);
                    previous.put(d.node(), toRemove.node());
                    queue.add(new MapData.Destination(d.node(), newDistance));
                }

            }
        }

        String step = end;
        if (previous.get(step) != null || step.equals(start)) {
            while (step != null) {
                result.add(step);
                step = previous.get(step);
            }
            Collections.reverse(result);
        }

        return result;
    }

}
