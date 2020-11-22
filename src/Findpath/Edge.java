package Findpath;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-19 15:06
 */
public class Edge {

    public static void main(String[] args) {
        Edge[] edges = new Edge[]{
                new Edge(0,1,10),
                new Edge(0,2,8),
                new Edge(0,3,13),
                new Edge(1,3,2),
                new Edge(1,4,1),
                new Edge(2,3,3),
                new Edge(3,4,3)};
        edges = Edge.getSorted(edges);

        MinSpanTree_kruskal tree = new MinSpanTree_kruskal(edges);
        tree.createMinSpanTreeKruskal();

    }
    public int start;
    public int end;
    public double weight;

    public Edge(int start, int end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public static Edge[] getSorted(Edge[] edges) {
        Edge[] newEdges = new Edge[edges.length];
        for (int i = 0; i < edges.length; i++) {
            Edge minEdge = new Edge(0, 0, Double.MAX_VALUE);
            int n = -1;
            for (int j = 0; j < edges.length; j++) {
                if (edges[j].getWeight() < minEdge.getWeight()) {
                    minEdge = edges[j];
                    n = j;
                }
            }
            newEdges[i] = minEdge;
            edges[n] = new Edge(0, 0, Double.MAX_VALUE);
        }


        return newEdges;
    }
}
