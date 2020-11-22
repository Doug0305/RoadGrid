package Findpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-19 10:37
 */
public class MSTTest {


    public static void main(String[] args) {
        Edge[] edges = new Edge[]{
                new Edge(0,1,10),
                new Edge(0,2,8),
                new Edge(0,3,13),
                new Edge(1,3,2),
                new Edge(1,4,1),
                new Edge(2,3,3),
                new Edge(3,4,3)};

        MinSpanTree_kruskal tree = new MinSpanTree_kruskal(edges);
        tree.createMinSpanTreeKruskal();

    }
}
