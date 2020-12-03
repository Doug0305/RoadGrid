package Delaunay;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.simplify.TopologyPreservingSimplifier;
import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.*;
import wblut.hemesh.HE_Path;
import wblut.processing.WB_Render3D;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-25 14:34
 */
public class DG_Paths {
    List<HE_Path> he_paths;
    WB_Render3D render;

    List<WB_PolyLine> pathLines = new ArrayList<>();
    List<LineString> pathLinesString = new ArrayList<>();
    List<WB_Coord> nodes = new ArrayList<>();
    MultiLineString multiLineString;
    List<WB_Polygon> houses;
    public double lengthSum = 0;
    public int nodeNum = 0;

    //道路评价指标
    private double cosSum = 0;

    //用JTS优化路径布局
    public DG_Paths(DG_Nodes sewers) {
        he_paths = sewers.tree.paths;
        houses = sewers.network.houses;
        nodeNum = sewers.points.size();
        for (HE_Path path : he_paths) {
            pathLines.add(Tools.gf.createPolyLine(path.getPathVertices()));
            pathLinesString.add(Tools.toJTSPolyline(Tools.gf.createPolyLine(path.getPathVertices())));
            lengthSum += path.getPathLength();
        }
        LineMerger lineMerger = new LineMerger();
        lineMerger.add(pathLinesString);
        pathLinesString = (List<LineString>) lineMerger.getMergedLineStrings();
    }

    public void optimize() {
        Geometry g = pathLinesString.get(0);
        for (int i = 1; i < pathLinesString.size(); i++) {
            g = g.union(pathLinesString.get(i));
        }
        TopologyPreservingSimplifier simplifier = new TopologyPreservingSimplifier(g);
        simplifier.setDistanceTolerance(1);
        multiLineString = (MultiLineString) simplifier.getResultGeometry();
        LineMerger lineMerger = new LineMerger();
        lineMerger.add(multiLineString);
        pathLinesString = (List<LineString>) lineMerger.getMergedLineStrings();

        pathLines.clear();
        lengthSum = 0;
        for (LineString line : pathLinesString) {
            pathLines.add(Tools.toWB_PolyLine(line));
            lengthSum += line.getLength();
        }
        nodes = new ArrayList<>(new HashSet<>(pathLines.stream().map(e -> e.getPoints().toList()).flatMap(Collection::stream).collect(Collectors.toList())));
    }

    public void angleOpt() {
        int n = 0;
        while (n < 10) {
            for (int i = 0; i < pathLines.size(); i++) {
                for (int j = 0; j < pathLines.get(i).getNumberOfPoints(); j++) {
                    double past = getSinSumOfLines(pathLines);
                    List<WB_PolyLine> pathLinesNow = moveRandom(pathLines.get(i).getPoint(j), pathLines, 1);
                    double now = getSinSumOfLines(pathLinesNow);
                    //条件尚未调整合适
//                    if (past > now && Tools.checkIntersection(pathLinesNow, houses))
                    if (past > now )
                        pathLines = pathLinesNow;
                }
            }
            n++;
        }
    }

    private List<WB_PolyLine> moveRandom(WB_Coord c, List<WB_PolyLine> lines, double strength) {
        WB_Coord ctemp = new WB_Point(c.xd() + (Math.random() * 2 - 1.0) * 0.1 * strength, c.yd() + (Math.random() * 2 - 1.0) * 0.1 * strength, c.zd());
        List<WB_PolyLine> newlines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            WB_PolyLine l = lines.get(i);
            for (int j = 0; j < lines.get(i).getNumberOfPoints(); j++) {
                if (lines.get(i).getPoint(j).equals(c)) {
                    List<WB_Coord> points = lines.get(i).getPoints().toList();
                    points.set(j, ctemp);
                    l = Tools.gf.createPolyLine(points);
                }
            }
            newlines.add(l);
        }

        return newlines;
    }


    private double getSinSumOfLine(WB_PolyLine polyLine) {
        double sum = 0;
        for (int j = 1; j < polyLine.getNumberOfPoints() - 1; j++) {
            WB_Coord a1 = polyLine.getPoint(j - 1);
            WB_Coord a2 = polyLine.getPoint(j);
            WB_Coord b1 = polyLine.getPoint(j);
            WB_Coord b2 = polyLine.getPoint(j + 1);
            sum += expForAngle(a1, a2, b1, b2);
        }

        return sum;
    }

    //计算经过某一顶点的多段线sin值
    private double getSinOfTwoLines(WB_PolyLine l1, WB_PolyLine l2, WB_Point p) {
        double sin = 0;

        return sin;
    }

    private double getSinSumOfLines(List<WB_PolyLine> polyLines) {
        double sum = 0;
        //线段内的夹角
        for (WB_PolyLine polyLine : polyLines) {
            sum += getSinSumOfLine(polyLine);
        }

        //线段间的夹角
        List<WB_Point> points = new ArrayList<>(new HashSet<>(Tools.getAllPointsOfPolyline(polyLines)));
        for (int i = 0; i < points.size(); i++) {
            List<WB_PolyLine> polyLinesOnPoint = new ArrayList<>();
            for (int j = 0; j <polyLines.size(); j++) {
                if(polyLines.get(j).getPoints().toList().contains(points.get(i)))
                    polyLinesOnPoint.add(polyLines.get(j));
            }
            //找到点，及其相连的多段线，计算两两的夹角
            if(polyLinesOnPoint.size()>1){
                for (int j = 0; j < polyLinesOnPoint.size(); j++) {
                    for (int k = j; k < polyLinesOnPoint.size(); k++) {
                       sum += getSinOfTwoLines(polyLinesOnPoint.get(j),polyLinesOnPoint.get(k),points.get(i));

                    }
                }
            }
        }
        return sum;
    }


    private static double expForAngle(WB_Coord a1, WB_Coord a2, WB_Coord b1, WB_Coord b2) {
        if (WB_Vector.getAngle(new WB_Vector(a1, a2), new WB_Vector(b1, b2)) > Math.PI / 4) {
            return Math.abs(Math.sin(2 * WB_Vector.getAngle(new WB_Vector(a1, a2), new WB_Vector(b1, b2))));
        } else {
            return -4 / Math.PI * WB_Vector.getAngle(new WB_Vector(a1, a2), new WB_Vector(b1, b2)) + 2;
        }
    }

    public void show(PApplet app) {
        if (render == null) {
            render = new WB_Render3D(app);
        }

        app.pushStyle();
//        render.drawPoint(nodes, 0.5);
        for (int i = 0; i < pathLines.size(); i++) {
            app.colorMode(PConstants.HSB);
            app.strokeWeight(2);
            app.stroke(0, 255, 255);
//            WB_Transform3D t = new WB_Transform3D().addTranslate(new WB_Vector(0, 0, i * 0.3));
            render.drawPolylineEdges(pathLines.get(i));
        }
        app.popStyle();
    }
}
