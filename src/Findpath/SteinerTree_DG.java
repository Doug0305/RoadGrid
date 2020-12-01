package Findpath;

import processing.core.PApplet;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Path;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @program: RoadGrid
 * @author: Donggeng
 * @create: 2020-11-23 10:38
 */
public class SteinerTree_DG {

    HE_Mesh mesh;
    List<HE_Vertex> vertexes = new ArrayList<>();
    List<HE_Vertex> vertexesOnPath = new ArrayList<>();
    public List<HE_Path> paths = new ArrayList<>();
    WB_Render3D render;

    /*
    在HE_Mesh网格上选取特定的点进行斯坦纳树寻径
     */
    public SteinerTree_DG(HE_Mesh mesh, List<HE_Vertex> vertexes) {
        this.mesh = mesh;
        this.vertexes = vertexes;
        long start = System.currentTimeMillis();
        creatSTonMesh();
        long end = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (end - start) / 1000 + "s");
    }

    private void creatSTonMesh() {
        if (vertexes.size() > 1) {
            //从一个节点开始寻找
            vertexesOnPath.add(vertexes.get(0));
            paths.add(findShortestVertex(vertexes.get(0), vertexes));
            vertexesOnPath = getAllVertexesOnPaths(paths);
            //从现有路径节点中寻找最短的外部节点
            int n = 0;
            while (!vertexesOnPath.containsAll(vertexes)) {
                paths.add(findShortestVertex(paths, vertexes));
                vertexesOnPath.addAll(getAllVertexesOnPaths(paths));
                vertexesOnPath = new ArrayList<>(new HashSet<>(vertexesOnPath));
                System.out.println("Finding第" + n++ + "次" + "\t进度: " + checkProgress(vertexesOnPath,vertexes) + " / " + vertexes.size());
            }
        } else {
            System.out.println("所选点数过少");
        }
    }

    private int checkProgress(List<HE_Vertex> vs , List<HE_Vertex> vOrigin){
        int num = 0;
        for (int i = 0; i < vs.size(); i++) {
            for (int j = 0; j < vOrigin.size(); j++) {
                if(vs.get(i).equals(vOrigin.get(j))){
                    num++;
                }
            }
        }
        return num;
    }

    private List<HE_Vertex> getAllVertexesOnPaths(List<HE_Path> paths) {
        List<HE_Vertex> vertexes = new ArrayList<>();
        for (HE_Path path : paths) {
            vertexes.addAll(path.getPathVertices());
        }
//        return new ArrayList<>(new HashSet<>(vertexes));
        return vertexes;
    }


    private List<HE_Vertex> getStartAndEndVertexes(List<HE_Path> paths) {
        List<HE_Vertex> vertexes = new ArrayList<>();
        for (HE_Path path : paths) {
            vertexes.add(path.getPathVertices().get(0));
            vertexes.add(path.getPathVertices().get(path.getPathVertices().size() - 1));
        }
        return new ArrayList<>(new HashSet<>(vertexes));
    }

    //以path的节点作为出发点，寻找与其他未包含节点的最短路径
    private HE_Path findShortestVertex(List<HE_Path> paths, List<HE_Vertex> vs) {
        double shortest = Double.MAX_VALUE;
        HE_Path path = null;
        List<HE_Vertex> vertexes = getAllVertexesOnPaths(paths);
        for (HE_Vertex v : vs) {
            if (!vertexesOnPath.contains(v)) {
                for (HE_Vertex v0 : vertexes) {
                    if (!v0.equals(v)) {
                        try {
                            HE_Path p = HE_Path.getShortestPath(v0, v, mesh);
                            if (p.getPathLength() < shortest) {
                                shortest = p.getPathLength();
                                path = p;
                            }
                        } catch (Exception e) {
                            vertexesOnPath.add(v);
                        }
                    }
                }
            }
        }
        return path;
    }

    //以某单一节点为出发点，寻找与其他未包含节点的最短路径
    private HE_Path findShortestVertex(HE_Vertex start, List<HE_Vertex> vs) {
        double shortest = Double.MAX_VALUE;
        HE_Path path = null;
        for (HE_Vertex v : vs) {
            if (!start.equals(v)) {
                try {
                    HE_Path p = HE_Path.getShortestPath(start, v, mesh);
                    if (p.getPathLength() < shortest) {
                        shortest = p.getPathLength();
                        path = p;
                    }
                } catch (Exception e) {
                    vertexesOnPath.add(v);
                }
            }
        }
        return path;
    }

    public void show(PApplet app) {
        if (render == null)
            render = new WB_Render3D(app);

        app.pushStyle();
        app.stroke(0, 255, 0);
        app.noFill();
        for (HE_Path path : paths)
            render.drawPath(path);
        app.popStyle();
    }
}
