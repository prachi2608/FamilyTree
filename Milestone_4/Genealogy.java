import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Genealogy {
    List<PersonIdentity> personList = new ArrayList<>();
    List<FileIdentifier> mediaList = new ArrayList();

    PersonIdentity addPerson(String name) throws Exception {
        if (name != null || !name.isEmpty()) {
            System.out.println("Person name is not appropriate");
            throw new IllegalArgumentException();
        } else {
            PersonIdentity person = null;
            try {
                Connection con = DataConnection.createConnection();
                String sql = "INSERT INTO personinfo (name) VALUES (?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.executeUpdate();
                PreparedStatement pst1 = con.prepareStatement("SELECT personid FROM personinfo WHERE name = ?");
                pst1.setString(1, name);
                //Statement st = con.createStatement();
                ResultSet rs = pst1.executeQuery();
                int id = 0;
                while (rs.next()) {
                    id = rs.getInt(1);
                    person = new PersonIdentity(name, id);
                    personList.add(person);
                }
                System.out.println("Person id is " + id);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return person;
        }
    }

    Boolean recordAttributes(PersonIdentity person, Map<String, String> attributes) {
        int id = person.getPersonId();
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * from personattributes");
            ResultSet rs = ps.executeQuery();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO personattributes (attributeid) VALUES (?)");
            pst1.setInt(1, id);
            pst1.executeUpdate();

            for (String s : attributes.keySet()) {
                String value = attributes.get(s);
                PreparedStatement pst = con.prepareStatement("UPDATE personattributes SET " + s + " = ? WHERE attributeid = ?");
                pst.setString(1, value);
                pst.setInt(2, id);
                pst.executeUpdate();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean recordNote(PersonIdentity person, String note) {

        int currentPersonId = person.getPersonId();
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordnotes (note, personid) VALUES (?,?)");
            pst1.setString(1, note);
            pst1.setInt(2, currentPersonId);
            pst1.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean recordReference(PersonIdentity person, String reference) {

        int currentPersonId = person.getPersonId();
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordreferences (reference) VALUES (?)");
            pst1.setString(1, reference);
            pst1.executeUpdate();
            PreparedStatement pst2 = con.prepareStatement("SELECT max(id) FROM recordreferences");
            ResultSet rs = pst2.executeQuery();
            int currentreferenceId = 0;
            if (rs.next()) {
                currentreferenceId = rs.getInt(1);
            }
            PreparedStatement pst3 = con.prepareStatement("INSERT INTO personreferences (referenceid,personid) VALUES (?,?)");
            pst3.setInt(1, currentreferenceId);
            pst3.setInt(2, currentPersonId);
            pst3.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean recordChild(PersonIdentity parent, PersonIdentity child) {
        int ParentId = parent.getPersonId();
        int ChildId = child.getPersonId();
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO relation(person1,person2) VALUES (?,?)");
            pst1.setInt(1, ParentId);
            pst1.setInt(2, ChildId);
            pst1.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    FileIdentifier addMediaFile(String fileLocation) {
        FileIdentifier file = null;

        try {
            int index = fileLocation.lastIndexOf("\\");
            String fileName = fileLocation.substring(index + 1);
            System.out.println(fileName);
            Connection con = DataConnection.createConnection();
            String sql = "INSERT INTO mediainfo (filename,fileLocation) VALUES (?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fileName);
            //Make fileLocation unique
            pst.setString(2, fileLocation);
            pst.executeUpdate();
            PreparedStatement pst1 = con.prepareStatement("SELECT max(mediaid) FROM mediainfo");
            ResultSet rs = pst1.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                file = new FileIdentifier(fileLocation, fileName, id);
                mediaList.add(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return file;
    }


    Boolean recordMediaAttributes(FileIdentifier fileIdentifier, Map<String, String> attributes) {
        int id = fileIdentifier.getId();

        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO mediaattributes (attributeid) VALUES (?)");
            pst1.setInt(1, id);
            pst1.executeUpdate();

            for (String s : attributes.keySet()) {
                System.out.println(s);
                String value = attributes.get(s);
                PreparedStatement pst = con.prepareStatement("UPDATE mediaattributes SET " + s + " = ? WHERE attributeid = ?");
                pst.setString(1, value);
                pst.setInt(2, id);
                pst.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean tagMedia(FileIdentifier fileIdentifier, String tag) {
        int id = fileIdentifier.getId();
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordtags (tags) VALUES (?)");
            pst1.setString(1, tag);
            pst1.executeUpdate();
            PreparedStatement pst2 = con.prepareStatement("SELECT max(tagid) FROM recordtags");
            ResultSet rs = pst2.executeQuery();
            int currentTagId = 0;
            if (rs.next()) {
                currentTagId = rs.getInt(1);
            }
            PreparedStatement pst3 = con.prepareStatement("INSERT INTO mediatags(mediaid,tagid) VALUES (?,?)");
            pst3.setInt(1, id);
            pst3.setInt(2, currentTagId);
            pst3.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    Boolean peopleInMedia(FileIdentifier fileIdentifier, List<PersonIdentity> people) {
        int id = fileIdentifier.getId();
        try {
            Connection con = DataConnection.createConnection();
            for (PersonIdentity i : people) {
                PreparedStatement pst1 = con.prepareStatement("INSERT INTO personinmedia (personid,mediaid) VALUES (?,?)");
                pst1.setInt(1, i.getPersonId());
                pst1.setInt(1, id);
                pst1.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
