public class ISAM {
    private long offset;
    private int artnr;
    
    public ISAM(){}
    
    public ISAM(int artnr, long offset){
        this.offset = offset;
        this.artnr = artnr;
    }

    public long getOffset() {
        return offset;
    }

    public int getArtnr() {
        return artnr;
    }
    
    public void print() {
        System.out.println(artnr+"\t"+offset);
    }
}