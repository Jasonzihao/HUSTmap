import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

class Location {
    int id;
    String name;
    String type;
    int xCoordinate;
    int yCoordinate;

    public Location(int id, String name, int xCoordinate, int yCoordinate, String type) {
        this.id = id;
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.type = type;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", name=" + name+ "}";
    }
}


class CampusMap {
    int[][] adjacencyMatrix;
    List<Location> locations;

    public CampusMap(int size) {
        adjacencyMatrix = new int[size][size];
        locations = new ArrayList<>();
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public void addEdge(int source, int destination, int distance) {
        adjacencyMatrix[source][destination] = distance;
        adjacencyMatrix[destination][source] = distance;  // 如果是无向图

    }
    public Location getLocationByName(String name) {
        for (Location location : locations)
        {
            if ((location.name).equals(name)) {
                return location;
            }
        }
        Location error = new Location(0,"error",0,0,"error");
        return error;
    }


}

class FileHandler {
    public static CampusMap loadMapFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int size = Integer.parseInt(reader.readLine().trim());
            CampusMap campusMap = new CampusMap(size);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length < 6) {
                    System.err.println("Invalid format in the map file. Skipping line: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                int xCoordinate = Integer.parseInt(parts[2]);
                int yCoordinate = Integer.parseInt(parts[3]);
                String type = parts[parts.length - 1].trim();

                Location location2 = new Location(id, name, xCoordinate, yCoordinate, type);
                campusMap.addLocation(location2);
            }

            //System.out.println(campusMap.locations);
            for (int i = 0; i < campusMap.adjacencyMatrix.length; i++) {
                for (int j = 0; j < campusMap.adjacencyMatrix[i].length; j++) {
                    // System.out.print(campusMap.adjacencyMatrix[i][j] + " ");
                }
                //System.out.println();
            }
            campusMap = loadMapFromFile1(filePath,campusMap);
            return campusMap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static CampusMap loadMapFromFile1(String filePath,CampusMap campusMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int size = Integer.parseInt(reader.readLine().trim());

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length < 6) {
                    System.err.println("Invalid format in the map file. Skipping line: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                int xCoordinate = Integer.parseInt(parts[2]);
                int yCoordinate = Integer.parseInt(parts[3]);
                String type = parts[parts.length - 1].trim();

                Location location2 = new Location(id, name, xCoordinate, yCoordinate, type);
                //campusMap.addLocation(location2);

                for (int i = 4; i < parts.length - 1; i++) {
                    Location location1 = campusMap.getLocationByName(parts[i].trim());

                    int distance = Math.abs(location1.xCoordinate - location2.xCoordinate) + Math.abs(location1.yCoordinate - location2.yCoordinate);

                    if (location1.id!=0){
                        campusMap.addEdge(location1.id - 1, location2.id - 1, distance);
                    }

                }
            }
            // System.out.println(campusMap.locations);
            for (int i = 0; i < campusMap.adjacencyMatrix.length; i++) {
                for (int j = 0; j < campusMap.adjacencyMatrix[i].length; j++) {
                    System.out.print(campusMap.adjacencyMatrix[i][j] + " ");
                }
                System.out.println();
            }
            return campusMap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}






class ShortestPathAlgorithm {

    public static List<Location> findShortestPath1(CampusMap campusMap, Location start) {
        List<Location> culturalSpots = getCulturalSpots(campusMap);
        culturalSpots.add(0,start);
        int numLocations = culturalSpots.size();
        // 构建只包含起点和所有人文景观的邻接矩阵
        int[][] culturalSpotsMatrix = buildCulturalSpotsMatrix(campusMap, culturalSpots);
        for (int i = 0; i < culturalSpotsMatrix.length; i++) {
            for (int j = 0; j < culturalSpotsMatrix[i].length; j++) {
                // System.out.print(culturalSpotsMatrix[i][j] + " ");
            }
            //  System.out.println();
        }
        // 使用动态规划解决 TSP 问题
        int[][] dp = new int[numLocations][1 << numLocations];
        int mask = (1 << numLocations) - 1;

        for (int i = 0; i < numLocations; i++) {
            Arrays.fill(dp[i], -1);
        }

        int minDistance = tsp(culturalSpotsMatrix, 0, 1, dp, mask);
        //System.out.println(dp[8][1<<9]);
        // 构建路径
        List<Location> shortestPath = new ArrayList<>();
        reconstructPath(culturalSpotsMatrix, 0, 1, dp, mask, culturalSpots, shortestPath);
        //添加起点
        shortestPath.add(0,start);
        // 添加起点到路径的最后，形成闭环
        shortestPath.add(start);
        List<Location> completePath1 = findCompleteShortestPath(campusMap, shortestPath);
        System.out.println("模式一最短距离："+minDistance);
        return completePath1;
    }

    public static List<Location> findCompleteShortestPath(CampusMap campusMap, List<Location> culturalSpotsPath) {
        List<Location> completePath = new ArrayList<>();

        // 遍历 culturalSpotsPath 中的每个点（包括起点和终点）
        for (int i = 0; i < culturalSpotsPath.size() - 1; i++) {
            Location currentLocation = culturalSpotsPath.get(i);
            Location nextLocation = culturalSpotsPath.get(i + 1);

            // 计算当前点到下一个点的最短路径
            List<Location> shortestPath = findShortestPath(campusMap, currentLocation, nextLocation);

            // 添加最短路径中的所有点到 completePath
            completePath.addAll(shortestPath.subList(0, shortestPath.size() - 1)); // 排除路径中的最后一个点，避免重复添加
        }

        // 添加 culturalSpotsPath 中的最后一个点（终点）到 completePath
        completePath.add(culturalSpotsPath.get(culturalSpotsPath.size() - 1));

        return completePath;
    }

    private static int tsp(int[][] culturalSpotsMatrix, int current, int visited, int[][] dp, int mask) {
        int numLocations = culturalSpotsMatrix.length;
        // 如果已经访问了所有节点，返回起点到当前节点的距离
        if (visited == mask) {
            //System.out.println("debug"+visited);
            return culturalSpotsMatrix[current][0];
        }

        // 如果已经计算过当前状态，直接返回结果
        if (dp[current][visited] != -1) {
            return dp[current][visited];
        }
        int minDistance = Integer.MAX_VALUE;

        // 遍历所有未访问的节点
        for (int i = 0; i < numLocations; i++) {
            int maskBit = 1 << i;
            //System.out.println("dddebug"+numLocations);
            if ((visited & maskBit) == 0) {
                int newVisited = visited | maskBit;

                int distance = culturalSpotsMatrix[current][i] + tsp(culturalSpotsMatrix, i, newVisited, dp, mask);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        // 保存当前状态的计算结果
        dp[current][visited] = minDistance;

        return minDistance;
    }

    private static void reconstructPath(int[][] culturalSpotsMatrix, int current, int visited, int[][] dp, int mask, List<Location> culturalSpots, List<Location> path) {
        int numLocations = culturalSpotsMatrix.length;

        // 如果已经访问了所有节点，返回
        if (visited == mask) {
            return;
        }

        // 遍历所有未访问的节点
        for (int i = 0; i < numLocations; i++) {
            int maskBit = 1 << i;

            if ((visited & maskBit) == 0) {
                int newVisited = visited | maskBit;
                int distance = culturalSpotsMatrix[current][i] + dp[i][newVisited];

                // 如果距离等于最短距离，且该节点不在路径中，则将节点加入路径并递归
                if (distance == dp[current][visited] && !path.contains(culturalSpots.get(i))) {
                    path.add(culturalSpots.get(i));
                    reconstructPath(culturalSpotsMatrix, i, newVisited, dp, mask, culturalSpots, path);
                }
            }
        }
    }


    private static int[][] buildCulturalSpotsMatrix(CampusMap campusMap, List<Location> culturalSpots) {
        int numLocations = culturalSpots.size(); // 包含起点
        //System.out.println(culturalSpots);
        int[][] culturalSpotsMatrix = new int[numLocations][numLocations];

        // 构建邻接矩阵
        for (int i = 0; i < numLocations; i++) {
            for (int j = 0; j < numLocations; j++) {
                if (i != j) {
                    Location start = culturalSpots.get( i );
                    Location end = culturalSpots.get( j );

                    List<Location> shortestPath = findShortestPath(campusMap, start, end);
                    int distance = calculatePathDistance(campusMap, shortestPath);
                    culturalSpotsMatrix[i][j] = distance;
                }
            }
        }
        return culturalSpotsMatrix;
    }


    private static int calculatePathDistance(CampusMap campusMap,List<Location> path) {
        // 计算路径的总距离
        int distance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Location currentLocation = path.get(i);
            Location nextLocation = path.get(i + 1);
            distance += campusMap.adjacencyMatrix[currentLocation.id - 1][nextLocation.id - 1];
        }
        return distance;
    }
    public static List<Location> findShortestPath(CampusMap campusMap, Location start, Location end) {
        // 使用 Dijkstra 算法计算最短路径
        Map<Location, Integer> distanceMap = new HashMap<>();
        Map<Location, Location> predecessorMap = new HashMap<>();
        Set<Location> unvisitedLocations = new HashSet<>(campusMap.locations);

        // 初始化距离和前驱节点
        for (Location location : campusMap.locations) {
            distanceMap.put(location, Integer.MAX_VALUE);
            predecessorMap.put(location, null);
        }

        distanceMap.put(start, 0);

        while (!unvisitedLocations.isEmpty()) {
            Location currentLocation = getMinimumDistanceLocation(unvisitedLocations, distanceMap);
            unvisitedLocations.remove(currentLocation);

            for (Location neighbor : getNeighbors(currentLocation, campusMap)) {
                int tentativeDistance = distanceMap.get(currentLocation) + campusMap.adjacencyMatrix[currentLocation.id-1][neighbor.id-1];
                // System.out.println("Debug - currentLocation.id: " + currentLocation.id);
                // System.out.println("Debug - neighbor.id: " + neighbor.id);

                if (tentativeDistance < distanceMap.get(neighbor)) {
                    distanceMap.put(neighbor, tentativeDistance);
                    predecessorMap.put(neighbor, currentLocation);
                }
            }
        }

        // 构建路径
        List<Location> shortestPath = new ArrayList<>();
        Location currentLocation = end;
        while (currentLocation != null) {
            shortestPath.add(currentLocation);
            currentLocation = predecessorMap.get(currentLocation);
        }

        Collections.reverse(shortestPath);
        return shortestPath;
    }

    private static Location getMinimumDistanceLocation(Set<Location> unvisitedLocations, Map<Location, Integer> distanceMap) {
        Location minLocation = null;
        int minDistance = Integer.MAX_VALUE;

        for (Location location : unvisitedLocations) {
            int distance = distanceMap.get(location);
            if (distance < minDistance) {
                minDistance = distance;
                minLocation = location;
            }
        }

        return minLocation;
    }

    private static List<Location> getNeighbors(Location location, CampusMap campusMap) {
        List<Location> neighbors = new ArrayList<>();

        if (location != null) {
            for (int i = 0; i < campusMap.adjacencyMatrix[location.id - 1].length; i++) {
                if (campusMap.adjacencyMatrix[location.id - 1][i] > 0) {
                    neighbors.add(campusMap.locations.get(i));
                }
            }
        }

        return neighbors;
    }


    public static List<Location> findShortestPathWithWaypoint(CampusMap campusMap, Location start, Location end) {
        List<Location> culturalSpots = getCulturalSpots(campusMap);

        List<Location> shortestPath = null;
        int shortestDistance = Integer.MAX_VALUE;
        int counter = 1;
        for (Location waypoint : culturalSpots) {
            // 计算起点到人文景观的距离
            List<Location> firstPath = findShortestPath(campusMap, start, waypoint);

            // 计算人文景观到终点的距离
            List<Location> secondPath = findShortestPath(campusMap, waypoint, end);

            // 合并两段路径
            firstPath.addAll(secondPath.subList(1, secondPath.size())); // 从第二段路径的第二个元素开始加入，避免重复添加中间点

            // 计算总距离
            int totalDistance = calculateTotalDistance(campusMap, firstPath);
            System.out.println(counter+"路径:"+totalDistance);

            // 更新最短路径和距离
            if (totalDistance < shortestDistance) {
                shortestDistance = totalDistance;
                shortestPath = new ArrayList<>(firstPath);
            }
            counter++;
        }
        System.out.println("模式二最短距离："+shortestDistance);
        return shortestPath;
    }

    private static List<Location> getCulturalSpots(CampusMap campusMap) {
        List<Location> culturalSpots = new ArrayList<>();
        for (Location location : campusMap.locations) {
            if ("CulturalSpot".equals(location.type)) {
                culturalSpots.add(location);
            }
        }
        return culturalSpots;
    }


    private static int calculateTotalDistance(CampusMap campusMap, List<Location> path) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Location current = path.get(i);
            Location next = path.get(i + 1);
            totalDistance += campusMap.adjacencyMatrix[current.id-1][next.id-1];
        }
        return totalDistance;
    }
}

public class Algorithm {
    public static void main(String[] args) {
        // 从文件加载地图
        CampusMap campusMap = FileHandler.loadMapFromFile("campus_map.txt");


        // 解决问题1
        Location start1 = campusMap.locations.get(1);  //id号-1
        List<Location> shortestPath1 = ShortestPathAlgorithm.findShortestPath1(campusMap, start1);
        System.out.println("Shortest Path 1: " + shortestPath1);
        // 输出结果

        // 解决问题2
        Location start2 = campusMap.locations.get(0);
        Location end2 = campusMap.locations.get(4);
        List<Location> shortestPath2 = ShortestPathAlgorithm.findShortestPathWithWaypoint(campusMap, start2, end2);
        System.out.println("Shortest Path 2: " + shortestPath2);
        // 输出结果
    }
}