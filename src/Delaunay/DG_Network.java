package Delaunay;

import DxfReader.DXFImporter;
import jdk.nashorn.internal.ir.WhileNode;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.hemesh.HE_MeshOp;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    WB_AABB2D boundary;
    WB_Render3D render;
    WB_Polygon net;

    List<WB_Polygon>innerPolys;
    public DG_Network(String path, String layer) {
        importer = new DXFImporter(path, "GBK");
        houses = importer.getPolygons(layer);
        houses = subRepetitivePoint(houses);
        boundary = new WB_AABB2D(houses.stream().map(e -> e.getPoints().toList()).flatMap(Collection::stream).collect(Collectors.toList()));
        boundary.expandBy(10);
        innerPolys = Tools.createBufferedPolygons(houses, 0.5);
        net = Tools.aabbToWBPolygon(boundary);
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
        render.drawPolygon(net);
        app.stroke(0);
        app.noFill();
        render.drawPolygonEdges(net);
        app.stroke(255,0,0);
        app.noFill();
        for (WB_Polygon poly : houses) {
            render.drawPolygonEdges(poly);
        }
        app.popStyle();
    }

    public WB_AABB2D getBoundary() {
        return boundary;
    }

}
