package Delaunay;

import processing.core.PApplet;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RoadNetwork
 * @author: Donggeng
 * @create: 2020-11-12 21:28
 */
public class DG_Delaunay {
    DG_Network network;
    WB_Triangulation2DWithPoints delaunay;
    HE_Mesh delaunayWithHoles;
    WB_Render3D render;
    ArrayList<WB_Coord> nodes;
    WB_CoordCollection pointsAfterTriangulation;

    int number;
    int[] triangles;
    int[] constrains;

    public DG_Delaunay(DG_Network network, int number) {
        this.network = network;
        this.number = number;
        constrains = new int[network.aabbBoundary.numberOfPoints()];
        setNodes();
        setDelaunay();
    }

    private void setNodes() {
        nodes = new ArrayList<>();

        //随机点
//        for (int i = 0; i < number; i++) {
//            nodes.add(Tools.randPinPoly(network.boundaryPolygon));
//        }

        //正交方格网
//        for (double i = network.aabbBoundary.getMinX(); i < network.aabbBoundary.getMaxX(); i += 0.5) {
//            for (double j = network.aabbBoundary.getMinY(); j < network.aabbBoundary.getMaxY(); j += 0.5) {
//                nodes.add(new WB_Point(i,j));
//            }
//        }

        //沿建筑方向的正交网格
        for (int i = 0; i < network.houses.size(); i++) {
            for (int j = 1; j < 3; j+=2) {
                WB_Polygon buffer = Tools.createBufferFromCoords(network.houses.get(i).getPoints().toList(),j*0.5);
                nodes.addAll(buffer.getPoints().toList());
//                for(WB_Segment segment: buffer.toSegments()){
//                    for (double k = 1; k < 2; k++) {
//                        nodes.add(segment.getPointOnCurve(k/3));
//                    }
//                }
            }
        }

        nodes.addAll(network.boundaryPolygon.getPoints().toList());
        nodes.addAll(network.sewers.pointsOutOfHouses);
        nodes.addAll(network.sewers.closestPointsOnBoundary);
//        nodes.addAll(Tools.getAllCoords(network.houses));
    }

    private void setDelaunay() {
        delaunay = WB_Triangulate.triangulateConforming2D(nodes, constrains);
        triangles = delaunay.getTriangles();
        pointsAfterTriangulation = delaunay.getPoints();

        HEC_FromTriangles creator = new HEC_FromTriangles();
        List<WB_Triangle> tris = new ArrayList<>();
        for (int i = 0; i < triangles.length; i += 3) {
            if (!Tools.checkIntersections(pointsAfterTriangulation.get(triangles[i]), pointsAfterTriangulation.get(triangles[i + 1]), pointsAfterTriangulation.get(triangles[i + 2]), network.innerUnionPolys) &&
                    Tools.checkIntersection(pointsAfterTriangulation.get(triangles[i]), pointsAfterTriangulation.get(triangles[i + 1]), pointsAfterTriangulation.get(triangles[i + 2]), network.boundaryPolygon)) {
                tris.add(Tools.gf.createTriangle(pointsAfterTriangulation.get(triangles[i]), pointsAfterTriangulation.get(triangles[i + 1]), pointsAfterTriangulation.get(triangles[i + 2])));
            }
        }
        creator.setTriangles(tris);
        creator.setRemoveUnconnectedElements(true);
        delaunayWithHoles = new HE_Mesh(creator);
    }

    public void show(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.noFill();
        app.stroke(150,50);
        app.strokeWeight(1);
        for (int i = 0; i < triangles.length; i += 3) {
            app.beginShape(app.TRIANGLES);
            render.vertex2D(pointsAfterTriangulation.get(triangles[i]));
            render.vertex2D(pointsAfterTriangulation.get(triangles[i + 1]));
            render.vertex2D(pointsAfterTriangulation.get(triangles[i + 2]));
            app.endShape();
        }
        app.stroke(150, 0, 0, 50);
        app.strokeWeight(2);
        render.drawEdges(delaunayWithHoles);
        app.popStyle();
    }
}
