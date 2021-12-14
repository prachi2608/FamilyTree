import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//Class to find the total nodes in the tree and determine the size of array
public class TreeSize {
    int totalNodes;

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    TreeSize() {
        try {

            Connection con = DataConnection.createConnection();
            PreparedStatement pst = con.prepareStatement("select max(personinfo.personid) from personinfo;");
            ResultSet rs = pst.executeQuery();
            rs.next();
            totalNodes = rs.getInt(1);
            //Memory to store parent child relation
            setTotalNodes(2*totalNodes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
