package Delaunay;

import DxfReader.DXFImporter;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: RoadNetwork
 * @author: Donggeng
 * @create: 2020-11-12 21:30
 */
public class DG_Network {
    DXFImporter importer;
    List<WB_Polygon> houses;
    WB_AABB2D aabbBoundary;
    WB_Render3D render;
    WB_Polygon boundaryPolygon;
    List<WB_Polygon> innerUnionPolys;
    DG_Nodes sewers;

    public DG_Network(String path, String layer, double minDis) {
        importer = new DXFImporter(path, "GBK");
        houses = importer.getPolygons(layer);
//        houses = subRepetitivePoint(houses);
        aabbBoundary = new WB_AABB2D(houses.stream().map(e -> e.getPoints().toList()).flatMap(Collection::stream).collect(Collectors.toList()));
        aabbBoundary.expandBy(10);
        innerUnionPolys = Tools.unionClosePolygonConvexHull(houses, minDis);
        boundaryPolygon = Tools.aabbToWBPolygon(aabbBoundary);
    }

    public DG_Network(String path, String layer, double minDis, DG_Nodes sewers,double buffer) {
        importer = new DXFImporter(path, "GBK");
        this.sewers = sewers;

        houses = importer.getPolygons(layer);
//        houses = subRepetitivePoint(houses);
        aabbBoundary = new WB_AABB2D(houses.stream().map(e -> e.getPoints().toList()).flatMap(Collection::stream).collect(Collectors.toList()));
        aabbBoundary.expandBy(10);
        innerUnionPolys = Tools.unionClosePolygonConvexHull(houses, minDis);

        //先用AABB选择范围内的点，再根据convex hull生成外轮廓
        List<WB_Point> points = new ArrayList(Tools.getAllPoints(innerUnionPolys));
        boundaryPolygon = Tools.aabbToWBPolygon(aabbBoundary);
        sewers.constrainAABB(this);
        points.addAll(sewers.points);
        boundaryPolygon = Tools.createBufferFromPoints(points,buffer*2);
    }

    private List<WB_Polygon> subRepetitivePoint(List<WB_Polygon> polygons) {
        List<WB_Polygon> newPoly = new ArrayList<>();
        for (WB_Polygon poly : polygons) {
            List<WB_Coord> points = poly.getPoints().toList();
            List<WB_Coord> newList = points.stream().distinct().collect(Collectors.toList());
            newPoly.add(new WB_Polygon(newList));
        }
        return newPoly;
    }


    public void show(PApplet app) {
        if (render == null)
            render = new WB_Render3D(app);
        app.pushStyle();
        app.fill(100);
        app.noStroke();
        render.drawPolygon(boundaryPolygon);
        app.stroke(0);
        app.noFill();
        render.drawPolygonEdges(boundaryPolygon);
        app.stroke(255, 0, 0);
        app.noFill();
        for (WB_Polygon poly : houses) {
            render.drawPolygonEdges(poly);
        }
        app.popStyle();
    }

    public WB_AABB2D getAabbBoundary() {
        return aabbBoundary;
    }

}
