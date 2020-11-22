package Delaunay;

import DxfReader.DXFImporter;
import Findpath.Edge;
import Findpath.MinSpanTree_kruskal;
import processing.core.PApplet;
import wblut.geom.WB_KDTree3D;
import wblut.geom.WB_Point;
import wblut.hemesh.HE_MeshOp;
import wblut.hemesh.HE_Path;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RoadNetwork
 * @author: Donggeng
 * @create: 2020-11-12 21:25
 */
public class DG_Nodes {
    DXFImporter importer;
    List<WB_Point> sewers = new ArrayList<>();
    List<HE_Vertex> closestVertex = new ArrayList<>();
    int[] vertex;
    WB_Render3D render;

    public DG_Nodes(String path, String[] layers) {
        importer = new DXFImporter(path, "GBK");
        for (String layer : layers)
            sewers.addAll(importer.getCircleCenters(layer));
    }

    public void constrainAABB(DG_Network network) {
        List<WB_Point> newSewers = new ArrayList<>();
        for (WB_Point point : sewers) {
            if (network.boundary.contains(point))
                newSewers.add(point);
        }
        sewers = newSewers;
    }

    ArrayList<HE_Path> paths = new ArrayList<>( );
    ArrayList<HE_Path> pathsTree = new ArrayList<>( );

    public void getClosestPointOnDelaunay(DG_Delaunay delaunay) {
        WB_KDTree3D tree3DT = delaunay.delaunayWithHoles.getVertexTree();
        for (int i = 0; i < sewers.size(); i++) {
            closestVertex.add(HE_MeshOp.getClosestVertex(delaunay.delaunayWithHoles, sewers.get(i), tree3DT));
        }
        paths = getPaths(delaunay);
    }


    Edge[] edgesForTree;
    ArrayList<int[]> results;

    private ArrayList<HE_Path> getPaths(DG_Delaunay delaunay) {
        ArrayList<HE_Path> pa = new ArrayList<>();
        edgesForTree = new Edge[sewers.size()*(sewers.size()-1)/2];
        int n = 0;
        for (int i = 0; i < sewers.size() - 1; i++) {
            for (int j = i + 1; j < sewers.size(); j++) {
                pa.add(HE_Path.getShortestPath(closestVertex.get(i), closestVertex.get(j), delaunay.delaunayWithHoles));
                edgesForTree[n] = new Edge(i,j,HE_Path.getShortestPath(closestVertex.get(i), closestVertex.get(j), delaunay.delaunayWithHoles).getPathLength());
                n++;
            }
        }
        return pa;
    }


    public void MinSpanTree(DG_Delaunay delaunay) {
        MinSpanTree_kruskal tree = new MinSpanTree_kruskal(edgesForTree);
        results = tree.createMinSpanTreeKruskal();
        for (int i = 0; i < results.size(); i++) {
            pathsTree.add(HE_Path.getShortestPath(closestVertex.get(results.get(i)[0]), closestVertex.get(results.get(i)[1]), delaunay.delaunayWithHoles));
        }
    }

    public void show(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.fill(0, 50);
        render.drawPoint(sewers, 1);

        app.fill(220, 200, 100);
        for (HE_Vertex v : closestVertex)
            render.drawVertex(v, 0.5);

        app.stroke(0, 255, 0);
        app.noFill();
        for (HE_Path path : paths)
            render.drawPath(path);

        app.popStyle();
    }

    public void showTree(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.fill(0, 50);
        render.drawPoint(sewers, 1);

        app.fill(220, 200, 100);
        for (HE_Vertex v : closestVertex)
            render.drawVertex(v, 0.5);

        app.stroke(0, 255, 0);
        app.noFill();
        for (HE_Path path : pathsTree)
            render.drawPath(path);

        app.popStyle();
    }


}
