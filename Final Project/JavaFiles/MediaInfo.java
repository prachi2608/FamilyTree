import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaInfo {
    Map<Integer, FileIdentifier> fileIdentifierMap = new HashMap<>();

    FileIdentifier addMediaFile(String fileLocation) {
        if (fileLocation == null || fileLocation.isBlank() || fileLocation.isEmpty() || fileLocation.equalsIgnoreCase("null")) {
            return null;
        }
        FileIdentifier fileIdentifier = null;
        try {
            //Get file name from file location
            //Example : fileLocation : C:\Users\prach\OneDrive\Documents\\filename.png
            // Fetching file name as : filename.png
            int index = fileLocation.lastIndexOf("\\");
            String fileName = fileLocation.substring(index + 1);

            Connection con = DataConnection.createConnection();
            PreparedStatement pst = con.prepareStatement("SELECT fileLocation from mediainfo ");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //Validation for duplicate file location
                if (rs.getString(1).equalsIgnoreCase(fileLocation)) {
                    System.out.println("duplicate file location");
                    return null;
                }
            }
            //Insert in media info table
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO mediainfo (filename,fileLocation) VALUES (?,?)");
            pst1.setString(1, fileName);
            pst1.setString(2, fileLocation);
            pst1.executeUpdate();
            PreparedStatement pst2 = con.prepareStatement("SELECT max(mediaid) FROM mediainfo");
            ResultSet rs1 = pst2.executeQuery();
            //Create new object for unique media file, using id as identifier for the object
            while (rs1.next()) {
                int id = rs1.getInt(1);
                fileIdentifier = new FileIdentifier(fileLocation, fileName, id);
                fileIdentifierMap.put(id, fileIdentifier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileIdentifier;
    }

    Boolean recordMediaAttributes(FileIdentifier fileIdentifier, Map<String, String> attributes) {
        int flag = 0;
        if (fileIdentifier == null || attributes.isEmpty()) { //Validation
            return false;
        }
        for (String str : attributes.keySet()) {
            if (str == null) {
                return false;
            }
        }
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement ps = con.prepareStatement("SELECT mediaid from mediaattributes");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //Check if person id for media object already exists then update the values
                if (rs.getInt(1) == fileIdentifier.getId()) {
                    flag = 1;
                }
            }
            if (flag == 1) {
                //Person attributes row already exists, update particular rows entered by user in
                update(fileIdentifier, attributes, con);
            } else {
                //Insert new row, media information entered in attributes for first time
                PreparedStatement pst1 = con.prepareStatement("INSERT INTO mediaattributes (mediaid) VALUES (?)");
                pst1.setInt(1, fileIdentifier.getId());
                pst1.executeUpdate();
                update(fileIdentifier, attributes, con);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Method to update existing attributes
    public void update(FileIdentifier fileIdentifier, Map<String, String> attributes, Connection con) throws SQLException {
        for (String s : attributes.keySet()) {
            String value = attributes.get(s);
            PreparedStatement pst = con.prepareStatement("UPDATE mediaattributes SET " + s + " = ? WHERE mediaid = ?");
            pst.setString(1, value);
            pst.setInt(2, fileIdentifier.getId());
            pst.executeUpdate();
        }
    }

    Boolean tagMedia(FileIdentifier fileIdentifier, String tag) {
        if (fileIdentifier == null || tag == null || tag.isEmpty() || tag.isBlank() || tag.equalsIgnoreCase("null")) { //Validation
            return false;
        }
        try {
            //Insert tags for media file
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordtags(mediaid,tags) VALUES (?,?)");
            pst1.setInt(1, fileIdentifier.getId());
            pst1.setString(2, tag);
            pst1.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    Boolean peopleInMedia(FileIdentifier fileIdentifier, List<PersonIdentity> people) {
        if (fileIdentifier == null || people.isEmpty()) { //Validation
            return false;
        }
        //Check if any object in people list is null
        for (PersonIdentity per : people) {
            if (per == null) {
                return false;
            }
        }
        try {
            //Insert id of all person present in media file object
            //For ease, file identifier id and person identifier id is used to record people in a media file
            Connection con = DataConnection.createConnection();
            for (PersonIdentity i : people) {
                PreparedStatement pst1 = con.prepareStatement("INSERT INTO personinmedia (personid,mediaid) VALUES (?,?)");
                pst1.setInt(1, i.getPersonId());
                pst1.setInt(2, fileIdentifier.getId());
                pst1.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
