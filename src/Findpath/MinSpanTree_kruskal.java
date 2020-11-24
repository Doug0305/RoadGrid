package Findpath;

import wblut.geom.WB_GeometryOp;

import java.util.ArrayList;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-19 10:09
 */
public class MinSpanTree_kruskal {
    private Edge[] edges;
    private int edgeSize;
    public MinSpanTree_kruskal(Edge[] edges) {
        this.edgeSize = edges.length;
        this.edges = Edge.getSorted(edges);
    }


    public ArrayList<int[]> createMinSpanTreeKruskal() {
        // 定义一个一维数组，下标为连线的起点，值为连线的终点
        int[] parent = new int[edgeSize];
        ArrayList<int[]> results = new ArrayList<>();
        for (int i = 0; i < edgeSize; i++) {
            parent[i] = 0;
        }

        int sum = 0;
        for (int i =0;i<edges.length;i++) {

            // 找到起点和终点在临时连线数组中的最后连接点
            int start = find(parent, edges[i].start);
            int end = find(parent, edges[i].end);

            // 通过起点和终点找到的最后连接点是否为同一个点，是则产生回环
            if (start != end) {

                // 没有产生回环则将临时数组中，起点为下标，终点为值
                parent[start] = end;
                System.out.println("访问到了节点：{" + start + "," + end + "}，权值：" + edges[i].weight);
                sum += edges[i].weight;
                results.add(new int[]{start,end});
            }
        }
        System.out.println("最小生成树的权值总和：" + sum);
        return results;
    }

    /**
     * 获取集合的最后节点
     */
    private int find(int parent[], int index) {
        while (parent[index] > 0) {
            index = parent[index];
        }
        return index;
    }


}
