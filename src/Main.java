import Delaunay.*;
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


    boolean OPTIMIZED = false;
    DG_Network network;
    DG_Land land;
    DG_Delaunay delaunay;
    DG_Nodes sewers;
    DG_Paths pipes;
    CameraController cam;

    public void settings() {
        size(1600, 1200, P3D);
    }

    public void setup() {
        String path = "src\\01-01-11-雄州街道-龙虎营-兴隆.dxf";
        cam = new CameraController(this, 100);
        sewers = new DG_Nodes(path, new String[]{"KITCHEN", "TOILET", "SHOWER"});
        //network = new DG_Network(path, "JMDTEST", 2, sewers, 2);
        network = new DG_Network(path, "JMD1", 0.9, sewers, 2);
//        land = new DG_Land(path,"GCD");
//        land.smooth(15);
//        land.setAABB(20);
        delaunay = new DG_Delaunay(network, (int) network.getAabbBoundary().getArea());
        sewers.getClosestPointOnDelaunay(delaunay);
        sewers.SteinerTree();
        pipes = new DG_Paths(sewers);
    }

    public void draw() {
        background(255);
        cam.begin2d();
        fill(200);
        rect(0, 0, width / 5, height);
        textSize(20);
        fill(0);
        text("Length of pipes: " + String.format("%.2f", pipes.lengthSum) + " m", 10, height / 4);
        text("Number of sewers: " + pipes.nodeNum, 10, height / 4 + 30);

        cam.begin3d();
        cam.drawSystem(1000);
        network.show(this);
//        land.show(this);
        if (!OPTIMIZED) {
            sewers.showTree(this);
            delaunay.show(this);
        } else {
            sewers.show(this);
            pipes.show(this);
        }
    }

    public void keyPressed() {
        if (key == 'o' || key == 'O') {
            pipes.optimize();
            OPTIMIZED = true;
        }
        if (key == 'p' || key == 'P') {
            pipes.angleOpt();
            OPTIMIZED = true;
        }

        if (key == 'r' || key == 'R') {
            OPTIMIZED = false;
        }
    }
}