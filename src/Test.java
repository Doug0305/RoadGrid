import Delaunay.DG_Delaunay;
import Delaunay.DG_Land;
import Delaunay.DG_Network;
import Delaunay.Tools;
import gzf.gui.CameraController;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

import java.lang.reflect.Array;
import java.util.Collections;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-15 13:51
 */
public class Test extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Test");
    }
    public void settings() {
        size(1600, 1200, P3D);
    }
    CameraController cam;
    WB_Render3D render ;
    WB_Polygon poly,poly1,poly2;
    public void setup() {
        cam = new CameraController(this, 300);
        WB_Point[] out = new WB_Point[]{new WB_Point(0,0),new WB_Point(500,0),new WB_Point(500,500),new WB_Point(0,500)};
        WB_Point[] in1 = new WB_Point[]{new WB_Point(100,100),new WB_Point(300,100),new WB_Point(300,300),new WB_Point(100,300)};
        WB_Point[] in2 = new WB_Point[]{new WB_Point(400,100),new WB_Point(500,100),new WB_Point(500,300),new WB_Point(400,300)};
        in1 = reserve(in1);
        poly1 = new WB_Polygon(in1);
        poly2 = new WB_Polygon(in2);
        poly = new WB_Polygon(out);
        render = new WB_Render3D(this);
    }

    public void draw() {
        background(255);
        cam.drawSystem(1000);
        pushStyle();
        fill(100);
        render.drawPolygon(Tools.gf.unionPolygons2D(poly2,poly1));
        popStyle();
    }

    public WB_Point[] reserve( WB_Point[] arr ){
        WB_Point[] arr1 = new WB_Point[arr.length];
        for( int x=0;x<arr.length;x++ ){
            arr1[x] = arr[arr.length-x-1];
        }
        return arr1 ;
    }
}
