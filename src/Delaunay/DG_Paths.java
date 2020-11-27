package Delaunay;

import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import processing.core.PApplet;
import wblut.geom.WB_PolyLine;
import wblut.hemesh.HE_Path;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-25 14:34
 */
public class DG_Paths {
    List<HE_Path> he_paths;
    WB_Render3D render;
    List<WB_PolyLine> pathLines = new ArrayList<>();
    public double lengthSum = 0;
    public int nodeNum = 0;

    //道路评价指标
    private double cosSum = 0;

    public DG_Paths(DG_Nodes sewers) {
        he_paths = sewers.tree.paths;
        nodeNum = sewers.points.size();
        for (HE_Path path : he_paths) {
            pathLines.add(Tools.gf.createPolyLine(path.getPathVertices()));
            lengthSum += path.getPathLength();
        }
    }
    public void optimize() {

    }

    private void angleOpt(){
//        DouglasPeuckerSimplifier

    }

    private double getCosSumOfLines(List<WB_PolyLine> polyLines){
        double sum = 0;
        for (int i = 0; i < polyLines.size(); i++) {
            polyLines.get(i).a(1);
        }

        return sum;
    }

    public void show(PApplet app) {
        if (render == null)
            render = new WB_Render3D(app);

        app.pushStyle();
        app.stroke(0, 255, 0);
        app.noFill();
        for (WB_PolyLine line : pathLines)
            render.drawPolylineEdges(line);
        app.popStyle();
    }
}
