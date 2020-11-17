package Delaunay;

import processing.core.PApplet;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.Collection;
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
    ArrayList<WB_Point> nodes;
    WB_CoordCollection pointsAfterTriangulation;

    int number;
    int[] triangles;
    int[] constrains;

    public DG_Delaunay(DG_Network network, int number) {
        this.network = network;
        this.number = number;
        constrains = new int[network.boundary.numberOfPoints()];
        setNodes();
        setDelaunay();
    }

    private void setNodes() {
        nodes = new ArrayList<>();
        for (int i = 0; i < Tools.aabbToWBPolygon(network.boundary).getNumberOfPoints(); i++) {
            nodes.add(new WB_Point(Tools.aabbToWBPolygon(network.boundary).getPoint(i)));
            constrains[i] = i;
        }

        for (int i = 0; i < number; i++) {
            nodes.add(Tools.randPinPoly(network.net));
        }

        for (int i = 0; i < network.houses.size(); i++) {
            
        }

    }

    private void setDelaunay() {
        delaunay = WB_Triangulate.triangulateConforming2D(nodes, constrains);
        triangles = delaunay.getTriangles();
        pointsAfterTriangulation = delaunay.getPoints();

        HEC_FromTriangles creator = new HEC_FromTriangles();
        List<WB_Triangle> tris = new ArrayList<>();
        for (int i = 0; i < triangles.length; i += 3) {
            if (!Tools.checkIntersections(pointsAfterTriangulation.get(triangles[i]), pointsAfterTriangulation.get(triangles[i + 1]),pointsAfterTriangulation.get(triangles[i + 2]), network.innerPolys)) {
                tris.add(Tools.gf.createTriangle(pointsAfterTriangulation.get(triangles[i]),pointsAfterTriangulation.get(triangles[i+1]),pointsAfterTriangulation.get(triangles[i+2])));
            }
        }
        System.out.println(triangles.length/3);
        System.out.println(tris.size());
        creator.setTriangles(tris);
        delaunayWithHoles=new HE_Mesh(creator);
    }

    public void show(PApplet app) {
        app.pushStyle();
        if (render == null)
            render = new WB_Render3D(app);
        app.noFill();
        app.stroke(0);
        app.strokeWeight(1);
        for (int i = 0; i < triangles.length; i += 3) {
            app.beginShape(app.TRIANGLES);
            render.vertex2D(pointsAfterTriangulation.get(triangles[i]));
            render.vertex2D(pointsAfterTriangulation.get(triangles[i + 1]));
            render.vertex2D(pointsAfterTriangulation.get(triangles[i + 2]));
            app.endShape();
        }
        app.stroke(255,0,0);
        app.strokeWeight(2);
        render.drawEdges(delaunayWithHoles);
        app.stroke(255, 0, 0);
        for (int i = 0; i < constrains.length; i++) {
            app.beginShape(app.LINES);
            render.vertex2D(nodes.get(i));
            render.vertex2D(nodes.get((i + 1) % constrains.length));
            app.endShape();
        }


        app.popStyle();
    }
}
