package Delaunay;

import DxfReader.DXFImporter;
import Findpath.SteinerTree_DG;
import processing.core.PApplet;
import wblut.geom.WB_KDTree3D;
import wblut.geom.WB_Point;
import wblut.hemesh.HE_MeshOp;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @program: RoadNetwork
 * @author: Donggeng
 * @create: 2020-11-12 21:25
 */
public class DG_Nodes {
    DXFImporter importer;
    List<WB_Point> points = new ArrayList<>();
    List<WB_Point> pointsInHouse = new ArrayList<>();
    List<WB_Point> pointsOutOfHouses = new ArrayList<>();
    List<WB_Point> closestPointsOnBoundary = new ArrayList<>();
    List<HE_Vertex> closestVertex = new ArrayList<>();
    WB_Render3D render;
    DG_Delaunay delaunay;
    DG_Network network;

    public DG_Nodes(String path, String[] layers) {
        importer = new DXFImporter(path, "GBK");
        for (String layer : layers)
            points.addAll(importer.getCircleCenters(layer));
    }

    public void constrainAABB(DG_Network network) {
        this.network = network;
        List<WB_Point> newSewers = new ArrayList<>();
        for (WB_Point point : points) {
            if (Tools.toJTSPolygon(network.boundaryPolygon).contains(Tools.toJTSpoint(point))) {
                newSewers.add(point);
                if(Tools.checkIntersection(point,network.houses)) {
                    closestPointsOnBoundary.add(Tools.getClosestPointOnPolygons(point,Tools.createBufferedPolygons(network.innerUnionPolys,0.5)));
                    pointsInHouse.add(point);
                }
                else {
                    pointsOutOfHouses.add(point);
                }
            }
        }
        closestPointsOnBoundary = new ArrayList<>(new HashSet<>(closestPointsOnBoundary));
        points = newSewers;
    }

    public void getClosestPointOnDelaunay(DG_Delaunay delaunay) {
        this.delaunay = delaunay;
        WB_KDTree3D tree3DT = delaunay.delaunayWithHoles.getVertexTree();
        for (int i = 0; i < points.size(); i++) {
            closestVertex.add(HE_MeshOp.getClosestVertex(delaunay.delaunayWithHoles, points.get(i), tree3DT));
        }
    }

    SteinerTree_DG tree;

    public void SteinerTree() {
        tree = new SteinerTree_DG(delaunay.delaunayWithHoles, closestVertex);
    }

    public void show(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.fill(0, 50);
        render.drawPoint(points, 1);

        app.fill(220, 200, 100);
        for (HE_Vertex v : closestVertex)
            render.drawVertex(v, 0.5);

        app.popStyle();
    }

    public void showTree(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.fill(0, 50);
        render.drawPoint(points, 1);


        app.fill(220, 200, 100);
        for (HE_Vertex v : closestVertex)
            render.drawVertex(v, 0.5);
//        render.drawPoint(closestPointsOnBoundary,2);

        app.stroke(0, 255, 0);
        app.noFill();
        app.strokeWeight(2);
        tree.show(app);
        app.popStyle();
    }

}
