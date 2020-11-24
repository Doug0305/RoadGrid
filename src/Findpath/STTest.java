package Findpath;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Triangle;
import wblut.hemesh.HEC_FromTriangles;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-23 10:39
 */
public class STTest extends PApplet {

    HE_Mesh mesh;
    WB_Render render;
    List<HE_Vertex> vertexes = new ArrayList<>();
    SteinerTree_DG tree;


    public static void main(String[] args) {
        PApplet.main("Findpath.STTest");
    }

    public void settings() {
        size(1600, 1200, P3D);
    }

    public void setup() {
        WB_Point[] points = new WB_Point[121];
        int index = 0;
        for (int j = 0; j < 11; j++) {
            for (int i = 0; i < 11; i++) {
                points[index] = new WB_Point(-200 + i * 40 + (((i != 0) && (i != 10)) ? random(-20, 20) : 0), -200 + j * 40 + (((j != 0) && (j != 10)) ? random(-20, 20) : 0), sin(TWO_PI / 20 * i) * 40 + cos(TWO_PI / 10 * j) * 40);
                index++;
            }
        }

        //create triangles from point grid
        WB_Triangle[] tris = new WB_Triangle[200];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tris[2 * (i + 10 * j)] = new WB_Triangle(points[i + 11 * j], points[i + 1 + 11 * j], points[i + 11 * j + 11]);
                tris[2 * (i + 10 * j) + 1] = new WB_Triangle(points[i + 1 + 11 * j], points[i + 11 * j + 12], points[i + 11 * j + 11]);
            }
        }

        HEC_FromTriangles creator = new HEC_FromTriangles();

        creator.setTriangles(tris);
        //alternatively tris can be any Collection<WB_Triangle>
        mesh = new HE_Mesh(creator);
        render = new WB_Render(this);
        for (int i = 0; i < 5; i++) {
            vertexes.add(mesh.getVertexWithIndex((int)random(mesh.getNumberOfVertices())));
        }
        tree = new SteinerTree_DG(mesh, vertexes);
    }

    public void draw() {
        pushStyle();
        background(255);
        background(55);
        directionalLight(255, 255, 255, 1, 1, -1);
        directionalLight(127, 127, 127, -1, -1, 1);
        translate(width / 2, height / 2);
        rotateY(mouseX * 1.0f / width * TWO_PI);
        rotateX(mouseY * 1.0f / height * TWO_PI);
        stroke(0);
        render.drawEdges(mesh);
        noStroke();
        noFill();
        render.drawFaces(mesh);
        fill(255,0,0);
        for (HE_Vertex v : vertexes)
            render.drawVertex(v, 10);

        tree.show(this);
        popStyle();
    }


}
