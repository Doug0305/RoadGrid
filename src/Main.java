import Delaunay.DG_Delaunay;
import Delaunay.DG_Land;
import Delaunay.DG_Network;
import Delaunay.DG_Nodes;
import gzf.gui.CameraController;
import processing.core.PApplet;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-12 22:09
 */
public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Main");
    }

    DG_Network network;
    DG_Land land;
    DG_Delaunay delaunay;
    DG_Nodes sewer;
    CameraController cam;

    public void settings() {
        size(1600, 1200, P3D);
    }

    public void setup() {
        String path = "src\\01-01-11-雄州街道-龙虎营-兴隆.dxf";
        cam = new CameraController(this, 300);
//        network = new DG_Network("src\\01-01-11-雄州街道-龙虎营-兴隆.dxf","TEST");
        network = new DG_Network(path,"JMDTEST");
//        network = new DG_Network(path,"JMD1");
//        land = new DG_Land(path,"GCD");
//        land.smooth(15);
//        land.setAABB(20);
        delaunay = new DG_Delaunay(network,(int)network.getBoundary().getArea()*5);
        sewer = new DG_Nodes(path, new String[]{"KITCHEN", "TOILET", "SHOWER"});
        sewer.constrainAABB(network);
        sewer.getClosestPointOnDelaunay(delaunay);
        sewer.MinSpanTree(delaunay);
    }

    public void draw() {
        background(255);
        cam.drawSystem(1000);
        network.show(this);
//        land.show(this);
        delaunay.show(this);
//        sewer.show(this);
        sewer.showTree(this);
    }

//    public void mouseClicked() {
//
//    }
}