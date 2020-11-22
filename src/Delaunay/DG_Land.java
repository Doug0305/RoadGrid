package Delaunay;

import DxfReader.DXFImporter;
import processing.core.PApplet;
import sun.font.TrueTypeFont;
import wblut.geom.*;
import wblut.hemesh.*;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-13 18:35
 */
public class DG_Land {
    DXFImporter importer;
    List<WB_Point> GCD;
    WB_Render3D render;
    HE_Mesh terrain,terrainSliced;
    WB_AABB2D range;
    WB_Polygon rangePolygon;
    HEM_MultiSlice modifier;

    public DG_Land(String path, String layer) {
        importer = new DXFImporter(path, "GBK");
        GCD = importer.getWBPointFromGCD(layer);
        WB_Triangulation2D triangulation = WB_Triangulate.triangulate2D(GCD);
        terrain = new HE_Mesh(new HEC_FromTriangulation().setTriangulation(triangulation).setPoints(GCD));
        terrainSliced = terrain.get();
        range = new WB_AABB2D(GCD);
        rangePolygon = Tools.aabbToWBPolygon(range);
    }


    public void setAABB(int offset) {
        ArrayList<WB_Plane> planes = new ArrayList<WB_Plane>();
        for (int i = 0; i < rangePolygon.getNumberOfPoints(); i++) {
            WB_Plane a = new WB_Plane(rangePolygon.getPoint(i), rangePolygon.getPoint((i + 1) % (rangePolygon.getNumberOfPoints())),
                    rangePolygon.getPoint((i + 1) % (rangePolygon.getNumberOfPoints())).add(0, 0, 10000));
            a.flipNormal();
            planes.add(a);
        }
        modifier = new HEM_MultiSlice();
        modifier.setPlanes(planes);
        modifier.setOffset(offset);
        terrainSliced.modify(modifier);
    }

    public void show(PApplet app) {
        if (render == null)
            render = new WB_Render3D(app);
        app.pushStyle();
        app.fill(100);
        render.drawFaces(terrainSliced);
//        render.drawPolygon(rangePolygon);
        app.popStyle();
    }
    public void smooth(int n) {
        //Laplacian modifier without adding vertices
        HEM_Smooth modifier=new HEM_Smooth();
        modifier.setIterations(n);
        modifier.setKeepBoundary(true);
        terrainSliced.modify(modifier);
    }

    public void getHeight(){
//        HE_MeshOp.getClosestIntersection()
    }
}
