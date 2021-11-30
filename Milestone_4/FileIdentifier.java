import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FileIdentifier {
    String fileLocation;
    String fileName;

    public Map<Integer, FileIdentifier> getFileIdentifierMap() {
        return fileIdentifierMap;
    }

    public void setFileIdentifierMap(Map<Integer, FileIdentifier> fileIdentifierMap) {
        this.fileIdentifierMap = fileIdentifierMap;
    }

    Map<Integer, FileIdentifier> fileIdentifierMap = new HashMap<>();
    int id;

    FileIdentifier() throws SQLException {

        Connection con = DataConnection.createConnection();
        String sql = "SELECT * FROM mediainfo";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        int id = 0;
        String name;
        String location;
        while (rs.next()) {
            id = rs.getInt(1);
            location = rs.getString(2);
            name = rs.getString(3);
            FileIdentifier m2 = new FileIdentifier(location, name, id);
            fileIdentifierMap.put(id, m2);
        }
    }

    public FileIdentifier(String fileLocation, String fileName, int id) {
        this.fileLocation = fileLocation;
        this.fileName = fileName;
        this.id = id;


    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
