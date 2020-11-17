package DxfReader;

import wblut.geom.WB_Point;

public class DXF_Point {
    private String layerName;
    private WB_Point pt;

    public void setPt(WB_Point pt) {
        this.pt = pt;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public WB_Point getPt() {
        return pt;
    }

    public String getLayerName() {
        return layerName;
    }

    @Override
    public String toString() {
        return "[ " + layerName + " , " + pt.toString() + " ]";
    }
}
