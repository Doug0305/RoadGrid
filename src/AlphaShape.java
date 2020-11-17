import wblut.geom.WB_AlphaTriangulation3D;
import wblut.hemesh.HEC_Creator;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HE_Mesh;

/**
 * @author FVH
 *
 */
class HEC_AlphaShape extends HEC_Creator {

    private WB_AlphaTriangulation3D alphaTri;
    private double alpha;

    /**
     *
     */
    public HEC_AlphaShape() {
        alphaTri = null;
        alpha = 10;
        setOverride(true);
    }

    public HEC_AlphaShape setTriangulation(final WB_AlphaTriangulation3D alphaTri) {
        this.alphaTri = alphaTri;
        return this;
    }

    public HEC_AlphaShape setAlpha(final double a) {
        this.alpha = a;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HEC_Creator#createBase()
     */
    @Override
    protected HE_Mesh createBase() {
        if (alpha <= 0.0 || alphaTri == null) {
            return new HE_Mesh();
        }
        int[] tris = alphaTri.getAlphaTriangles(alpha);
        HEC_FromFacelist ffl = new HEC_FromFacelist().setFaces(tris).setVertices(alphaTri.getPoints())
                .setCheckDuplicateVertices(false);
        ffl.setCheckManifold(true);
        return new HE_Mesh(ffl);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // TODO Auto-generated method stub

    }

}