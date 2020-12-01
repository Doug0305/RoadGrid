package Delaunay; /**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-12 22:49
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.algorithm.MinimumDiameter;
import org.locationtech.jts.geom.*;

import igeo.ICurve;
import igeo.IVec;
import processing.core.PApplet;
import wblut.geom.*;

public class Tools {
    public static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    public static GeometryFactory JTSgf = new GeometryFactory();

    /**
     * @param coords
     * @return
     */
    public static Coordinate[] addFirst2Last(Coordinate... coords) {
        Coordinate[] cs = new Coordinate[coords.length + 1];
        int i = 0;
        for (; i < coords.length; i++) {
            cs[i] = coords[i];
        }
        cs[i] = coords[0];
        return cs;
    }

    public static Coordinate toJTScoord(WB_Point p) {
        return new Coordinate(p.xd(), p.yd(), p.zd());
    }

    public static Point toJTSpoint(WB_Point p) {
        Coordinate coord = toJTScoord(p);
        Point g = JTSgf.createPoint(coord);
        return g;
    }

    /**
     * Polygon.getCoordinates包括首尾重合的点 ，用此方法去重
     *
     * @param coords
     * @return
     */
    public static Coordinate[] subLast(Coordinate... coords) {
        Coordinate[] cs = new Coordinate[coords.length - 1];
        int i = 0;
        for (; i < coords.length - 1; i++) {
            cs[i] = coords[i];
            cs[i].z = 0;
        }
        return cs;
    }

    /**
     * @param g
     * @return
     */
    public static WB_Polygon toWB_Polygon(Geometry g) {
        if (g.getGeometryType().equalsIgnoreCase("Polygon")) {
            Polygon p = (Polygon) g;
            Coordinate[] coordOut = p.getExteriorRing().getCoordinates();
            coordOut = Tools.subLast(coordOut);
            WB_Point[] outPt = new WB_Point[coordOut.length];
            for (int i = 0; i < coordOut.length; i++) {
                outPt[i] = new WB_Point(coordOut[i].x, coordOut[i].y, coordOut[i].z);
            }
            int num = p.getNumInteriorRing();

            if (num == 0) {
                return new WB_Polygon(outPt);
            } else {
                WB_Point[][] ptsIn = new WB_Point[num][];
                for (int i = 0; i < num; i++) {
                    Coordinate[] coords = p.getInteriorRingN(i).getCoordinates();
                    /**
                     * LineString 也需sublast
                     */
                    // System.out.println(coords[0]+" &&
                    // "+coords[coords.length-1]);/
                    WB_Point[] pts = new WB_Point[coords.length];
                    for (int j = 0; j < coords.length; j++) {
                        pts[j] = new WB_Point(coords[j].x, coords[j].y, coords[i].z);
                    }
                    ptsIn[i] = pts;
                }
                return new WB_Polygon(outPt, ptsIn);
            }
        } else {
            System.out.println("type is : " + g.getGeometryType());
            System.out.println("this Geometry is not a Polygon!");
            return null;
        }
    }

    /**
     * 将JTS_Polygon转化为WB_Polygon
     *
     * @param line JTS多段线
     * @return HeMesh多段线
     */
    public static WB_PolyLine toWB_PolyLine(LineString line) {
        Coordinate[] coords = line.getCoordinates();
        List<WB_Coord> list = new ArrayList<>();
        for (Coordinate pt : coords) {
            double z = pt.z;
            if (Double.isNaN(z))
                z = 0;
            WB_Coord coordinate = new WB_Point(pt.x, pt.y, z);
            list.add(coordinate);
        }
        return new WB_PolyLine(list);
    }

    /**
     * @param poly
     * @return
     */
    public static Polygon toJTSPolygon(WB_Polygon poly) {
        Coordinate[] coord = new Coordinate[poly.getNumberOfPoints()];
        for (int i = 0; i < poly.getNumberOfPoints(); i++) {
            WB_Point p = poly.getPoint(i);
            Coordinate c = new Coordinate(p.xd(), p.yd(), p.zd());
            coord[i] = c;
        }
        LinearRing ring = JTSgf.createLinearRing(addFirst2Last(coord));
        return JTSgf.createPolygon(ring);
    }

    public static LineString toJTSPolyline(WB_PolyLine poly) {
        Coordinate[] coord = new Coordinate[poly.getNumberOfPoints()];
        for (int i = 0; i < poly.getNumberOfPoints(); i++) {
            WB_Point p = poly.getPoint(i);
            Coordinate c = new Coordinate(p.xd(), p.yd(), p.zd());
            coord[i] = c;
        }
        return JTSgf.createLineString(coord);
    }

    /**
     *
     */
    public static WB_Point randPinPoly(WB_Polygon poly) {
        double minX = poly.getAABB().getMinX();
        double maxX = poly.getAABB().getMaxX();
        double minY = poly.getAABB().getMinY();
        double maxY = poly.getAABB().getMaxY();
        boolean contain = false;
        WB_Point pp = new WB_Point();
        while (!contain) {
            double x = Math.random() * (maxX - minX) + minX;
            double y = Math.random() * (maxY - minY) + minY;
            WB_Point p = new WB_Point(x, y);
            if (WB_GeometryOp.contains2D(p, poly)) {
                pp = p;
                contain = true;
            }
        }
        return pp;
    }

    public static WB_Point randPinAABB(WB_AABB2D poly) {
        double minX = poly.getMinX();
        double maxX = poly.getMaxX();
        double minY = poly.getMinY();
        double maxY = poly.getMaxY();
        boolean contain = false;
        WB_Point pp = new WB_Point();
        while (!contain) {
            double x = Math.random() * (maxX - minX) + minX;
            double y = Math.random() * (maxY - minY) + minY;
            WB_Point p = new WB_Point(x, y);
            if (toJTSPolygon(aabbToWBPolygon(poly)).contains(toJTSpoint(p))) {
                pp = p;
                contain = true;
            }
        }
        return pp;
    }

    public static WB_Point randPinPoly(WB_Polygon poly, Random rand) {
        double minX = poly.getAABB().getMinX();
        double maxX = poly.getAABB().getMaxX();
        double minY = poly.getAABB().getMinY();
        double maxY = poly.getAABB().getMaxY();
        boolean contain = false;
        WB_Point pp = new WB_Point();
        while (!contain) {
            double x = rand.nextFloat() * (maxX - minX) + minX;
            double y = rand.nextFloat() * (maxY - minY) + minY;
            WB_Point p = new WB_Point(x, y);
            if (toJTSPolygon(poly).contains(toJTSpoint(p))) {
                pp = p;
                contain = true;
            }
        }
        return pp;
    }

    /**
     *
     */
    public static ArrayList<WB_Point> randPinPoly(WB_Polygon poly, int num) {
        ArrayList<WB_Point> pp = new ArrayList<WB_Point>();
        for (int i = 0; i < num; i++) {
            WB_Point p = randPinPoly(poly);
            pp.add(p);
        }
        return pp;
    }

    public static ArrayList<Geometry> getGeosAsList(Geometry mGeo) {
        int num = mGeo.getNumGeometries();
        ArrayList<Geometry> geos = new ArrayList<Geometry>();
        for (int i = 0; i < num; i++) {
            geos.add(mGeo.getGeometryN(i));
        }
        return geos;
    }

    /*
     * 导出igeo
     */
    public static void savePolygonAsCurve(WB_Polygon poly, int layer) {
        int num = poly.getPoints().size();
        IVec[] MVecList = new IVec[num];
        for (int i = 0; i < num; i++) {
            WB_Point p = (WB_Point) poly.getPoints().get(i);
            MVecList[i] = new IVec(p.xf(), p.yf(), p.zf());
        }
        // ISurface surf = new ISurface(MVecList).layer(""+layer);
        new ICurve(MVecList, true).layer("" + layer);
    }

    /*
     * aabb转wbWB_Polygon
     */
    public static WB_Polygon aabbToWBPolygon(WB_AABB2D aabb) {
        WB_Point[] ps = aabb.getCorners();
        WB_Point p = ps[2];
        ps[2] = ps[3];
        ps[3] = p;
        return gf.createSimplePolygon(ps);
    }

    /*
    显示wb_polygon角点编号
     */
    public static void showNumberOfPoint(WB_Polygon poly, int size, PApplet app) {
        app.pushStyle();
        app.textSize(10);
        app.fill(0);
        for (int i = 0; i < poly.getNumberOfPoints(); i++) {
            app.text(i, poly.getPoint(i).xf(), poly.getPoint(i).yf());
        }
        app.popStyle();
    }

    /*
    检查WB_Polygon是否相交
     */
    public static boolean checkIntersection(WB_Polygon poly1, WB_Polygon poly2) {
        return toJTSPolygon(poly1).intersects(toJTSPolygon(poly2));
    }

    /*
    检查WB_Polygon与WB_Polyline是否相交
     */
    public static boolean checkIntersection(WB_PolyLine poly1, WB_Polygon poly2) {
        return toJTSPolygon(poly2).intersects(toJTSPolyline(poly1));
    }

    /*
    检查WB_Polygon与WB_Polyline是否相交
     */
    public static boolean checkIntersection(WB_PolyLine poly, List<WB_Polygon> polygons) {
        for (WB_Polygon polygon : polygons) {
            if (toJTSPolygon(polygon).intersects(toJTSPolyline(poly))) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIntersection(List<WB_PolyLine> poly, List<WB_Polygon> polygons) {
        for (WB_Polygon polygon : polygons) {
            for (WB_PolyLine line : poly) {
                if (toJTSPolygon(polygon).intersects(toJTSPolyline(line))) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    判断三角形是否与多边形的集合中任意相交
     */
    public static boolean checkIntersections(WB_Coord a, WB_Coord b, WB_Coord c, List<WB_Polygon> polygons) {
        for (WB_Polygon polygon : polygons) {
            if (toJTSPolygon(polygon).intersects(JTSgf.createLinearRing(new Coordinate[]{toJTScoord((WB_Point) a), toJTScoord((WB_Point) b), toJTScoord((WB_Point) c), toJTScoord((WB_Point) a)}))) {
                return true;
            }
        }
        return false;
    }

    /*
    判断三角形是否与多边形的集合中任意相交
     */
    public static boolean checkIntersection(WB_Coord a, WB_Coord b, WB_Coord c, WB_Polygon polygon) {
        if (toJTSPolygon(polygon).intersects(JTSgf.createLinearRing(new Coordinate[]{toJTScoord((WB_Point) a), toJTScoord((WB_Point) b), toJTScoord((WB_Point) c), toJTScoord((WB_Point) a)}))) {
            return true;
        }
        return false;
    }


    /*
    获取所有多边形的交集
    但更推荐使用gf.createBufferedPolygons
    待完善。。。。
     */
    public static List<WB_Polygon> getPolygonUnion(List<WB_Polygon> polygons) {
        while (true) {
            int num = 0;
            loop:
            for (int i = 0; i < polygons.size(); i++) {
                for (int j = i + 1; j < polygons.size(); j++) {
                    if (Tools.checkIntersection(polygons.get(i), polygons.get(j)) &
                            Tools.gf.unionPolygons2D(polygons.get(i), polygons.get(j)).size() == 1) {
                        polygons.addAll(Tools.gf.unionPolygons2D(polygons.get(i), polygons.get(j)));
                        num++;
                        polygons.remove(j);
                        polygons.remove(i);
                        break loop;
                    }
                }
            }
            if (num == 0)
                break;
        }
        return polygons;
    }

    /*
    生成WB_Polygon的集合生成各自的buffer，
     */

    public static List<WB_Polygon> createBufferedPolygons(List<WB_Polygon> polygons, double d) {
        List<WB_Polygon> newPolygons = new ArrayList<>();
        for (WB_Polygon polygon : polygons) {
            newPolygons.addAll(gf.createBufferedPolygons(polygon, d));
        }
        return newPolygons;
    }


    public static WB_Polygon createBufferFromCoords(List<WB_Coord> points, double d) {
        WB_Polygon poly = getPolygonConvexHullFromCoords(points);
        return gf.createBufferedPolygons(poly, d, 0).get(0);
    }

    public static WB_Polygon createBufferFromPoints(List<WB_Point> points, double d) {
        WB_Polygon poly = getPolygonConvexHullFromPoints(points);
        return gf.createBufferedPolygons(poly, d, 0).get(0);
    }

    /*
    合并相距过近的polygon,d距离阈值
     */
    public static List<WB_Polygon> unionClosePolygons(List<WB_Polygon> polygons, double d) {
        List<WB_Polygon> biggerPolygons = gf.createBufferedPolygons(polygons, d / 2, 0);
        return gf.createBufferedPolygons(biggerPolygons, -d / 2, 0);
    }

    /*
      求集合内每个多边形的凸包
   */
    public static List<WB_Polygon> unionClosePolygonConvexHull(List<WB_Polygon> polygons, double d) {
        List<WB_Polygon> convexHull = new ArrayList<>();
        for (WB_Polygon poly : polygons = unionClosePolygons(polygons, d)) {
            ConvexHull a = new ConvexHull(toJTSPolygon(poly));
            convexHull.add(toWB_Polygon(a.getConvexHull()));
        }
        return convexHull;
    }


    public static WB_Polygon getPolygonConvexHullFromPoints(List<WB_Point> points) {
        ConvexHull a = new ConvexHull(toJTSPolygon(gf.createSimplePolygon(points)));
        return toWB_Polygon(a.getConvexHull());
    }

    public static WB_Polygon getPolygonConvexHullFromCoords(List<WB_Coord> points) {
        ConvexHull a = new ConvexHull(toJTSPolygon(gf.createSimplePolygon(points)));
        return toWB_Polygon(a.getConvexHull());
    }


    /*
    判断点是否与多边形相交
     */
    public static boolean checkIntersection(WB_Point a, WB_Polygon polygon) {
        if (toJTSPolygon(polygon).intersects(toJTSpoint(a))) {
            return true;
        }

        return false;
    }

    /*
    根据外轮廓和内部的洞创建带洞多边形
     */
    public static WB_Polygon getNet(WB_AABB2D boundary, List<WB_Polygon> polygons) {
        List<WB_Coord> outPoints = Tools.aabbToWBPolygon(boundary).getPoints().toList();
        if (Tools.aabbToWBPolygon(boundary).getNormal().zd() < 0)
            Collections.reverse(outPoints);

        List<WB_Coord>[] innerPoints = new List[polygons.size()];
        for (int i = 0; i < polygons.size(); i++) {
            innerPoints[i] = polygons.get(i).getPoints().toList();

            if (polygons.get(i).getNormal().zd() > 0) {
                Collections.reverse(innerPoints[i]);
            }
        }
        WB_Polygon poly = new WB_Polygon(outPoints, innerPoints);
        return poly;
    }

    public static WB_Polygon getNet(WB_Polygon boundary, List<WB_Polygon> polygons) {
        List<WB_Coord> outPoints = boundary.getPoints().toList();
        if (boundary.getNormal().zd() < 0)
            Collections.reverse(outPoints);

        List<WB_Coord>[] innerPoints = new List[polygons.size()];
        for (int i = 0; i < polygons.size(); i++) {
            innerPoints[i] = polygons.get(i).getPoints().toList();

            if (polygons.get(i).getNormal().zd() > 0) {
                Collections.reverse(innerPoints[i]);
            }
        }
        WB_Polygon poly = new WB_Polygon(outPoints, innerPoints);
        return poly;
    }

    /*
    读取多边形集合中所有的点,并且沿边每隔一段距离取点
     */
    public static List<WB_Coord> getAllCoords(List<WB_Polygon> polygons) {
        List<WB_Coord> points = new ArrayList<>();
        for (WB_Polygon poly : polygons) {
            List<WB_Point> pointsForOne = new ArrayList<>();
            for (int i = 0; i < poly.getNumberOfPoints(); i++)
                pointsForOne.add(poly.getPoint(i));
            points.addAll(pointsForOne);
        }
        return points;
    }

    public static List<WB_Point> getAllPoints(List<WB_Polygon> polygons) {
        List<WB_Point> points = new ArrayList<>();
        for (WB_Polygon poly : polygons) {
            List<WB_Point> pointsForOne = new ArrayList<>();
            for (int i = 0; i < poly.getNumberOfPoints(); i++)
                pointsForOne.add(poly.getPoint(i));
            points.addAll(pointsForOne);
        }
        return points;
    }


    /*
    求多边形的最小外接矩形
     */
    public static WB_Polygon getMinimumRectangle(WB_Polygon poly) {
        Polygon toJTS = toJTSPolygon(poly);
        Polygon obbrect = (Polygon) (new MinimumDiameter(toJTS)).getMinimumRectangle();
        return toWB_Polygon(obbrect);
    }

    /*
    求多个点的几何中心
     */
    public static WB_Coord getCenterPoint(List<WB_Coord> points) {
        return getPolygonConvexHullFromCoords(points).getCenter();
    }
}
