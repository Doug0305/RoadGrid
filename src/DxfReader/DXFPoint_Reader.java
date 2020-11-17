package DxfReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import wblut.geom.WB_Point;

public class DXFPoint_Reader {

    private final String filePath;
    private List<DXF_Point> pts;

    public DXFPoint_Reader(String filePath) {
        this.filePath = filePath;
        setDXFPoints();
    }

    public List<WB_Point> getPts(String layer) {
        return pts.stream().filter(e -> e.getLayerName().equalsIgnoreCase(layer)).map(DXF_Point::getPt).collect(Collectors.toList());
    }

    private void setDXFPoints() {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        List<String> list = new ArrayList<String>();
        try {
            reader = new FileReader(filePath);
            bufferedReader = new BufferedReader(reader);
            String line = "";
            int mark = 0;
            int index = 0;
            String tempval = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (mark == 1) {
                    tempval += line + ",";
                    index++;
                    if (index == 11) {
                        mark = 0;
                        index = 0;
                        list.add(tempval);
                        tempval = "";
                    }
                }
                if (line.equals("POINT")) {
                    mark = 1;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pts = parseAllPoints(list);
    }

    private List<DXF_Point> parseAllPoints(List<String> list) {
        List<DXF_Point> pts = new ArrayList<>();
        for (String string : list)
            pts.add(parsePoint(string));
        return pts;
    }

    private DXF_Point parsePoint(String eString) {
        String[] eles = eString.split(",");
        String layer_name = eles[3];
        double x = Double.parseDouble(eles[5]);
        double y = Double.parseDouble(eles[7]);
        double z = Double.parseDouble(eles[9]);
        DXF_Point pt = new DXF_Point();
        pt.setLayerName(layer_name);
        pt.setPt(new WB_Point(x, y, z));
        return pt;
    }
}
