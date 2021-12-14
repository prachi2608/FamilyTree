import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PersonInfo extends MediaInfo {
    Map<Integer, PersonIdentity> personIdentityMap = new HashMap<>();

    PersonIdentity addPerson(String name) {
        //Validation
        if (name == null || name.isEmpty() || name.equalsIgnoreCase("null") || name.isBlank()) {
            System.out.println("Null value not accepted");
            return null;
        } else {
            PersonIdentity person = null;
            try {
                Connection con = DataConnection.createConnection();
                String sql = "INSERT INTO personinfo (name) VALUES (?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.executeUpdate();
                //Get id for person to create identifier for person
                PreparedStatement pst1 = con.prepareStatement("SELECT personid FROM personinfo WHERE name = ?");
                pst1.setString(1, name);
                ResultSet rs = pst1.executeQuery();
                int id;
                while (rs.next()) {
                    //New object for the person is created
                    //unique identifier for th object (id ) is assigned to the person
                    id = rs.getInt(1);
                    person = new PersonIdentity(name, id);
                    personIdentityMap.put(id, person);

                }
            } catch (Exception e) {
                return null;
            }

            return person;
        }
    }

    Boolean recordAttributes(PersonIdentity person, Map<String, String> attributes) {
        //Validation
        if (person == null || attributes.isEmpty()) {
            return false;
        }
        for (String str : attributes.keySet()) {
            if (str == null) {
                return false;
            }
        }


        int flag = 0;
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement ps = con.prepareStatement("SELECT personid from personattributes");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //Check if person id for person object already exists then update the values
                //Otherwise add new row for new person
                if (rs.getInt(1) == person.getPersonId()) {
                    flag = 1;

                }
            }
            if (flag == 1) {
                //Person attributes row already exists, update particular rows entered by user in map
                update(person, attributes, con);
            } else {
                //Insert new row, person entering attributes for first time
                PreparedStatement pst1 = con.prepareStatement("INSERT INTO personattributes (personid) VALUES (?)");
                pst1.setInt(1, person.getPersonId());
                pst1.executeUpdate();
                update(person, attributes, con);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //Method to update existing attribute
    public void update(PersonIdentity person, Map<String, String> attributes, Connection con) throws SQLException {
        for (String s : attributes.keySet()) {
            String value = attributes.get(s);
            PreparedStatement pst = con.prepareStatement("UPDATE personattributes SET " + s + " = ? WHERE personid = ?");
            pst.setString(1, value);
            pst.setInt(2, person.getPersonId());
            pst.executeUpdate();
        }
    }

    Boolean recordNote(PersonIdentity person, String note) {
        if (person == null || note == null || note.equalsIgnoreCase("null") || note.isEmpty() || note.isBlank()) { //Validation
            return false;
        }
        try {
            //Insert Notes for a person
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordnotes (note, personid) VALUES (?,?)");
            pst1.setString(1, note);
            pst1.setInt(2, person.getPersonId());
            pst1.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;
    }

    Boolean recordReference(PersonIdentity person, String reference) {
        if (person == null || reference == null || reference.equalsIgnoreCase("null") || reference.isBlank()) { //Validation
            System.out.println("Null value not accepted");
            return false;
        }
        //Insert references for a person
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO recordreferences (personreferences, personid) VALUES (?,?)");
            pst1.setString(1, reference);
            pst1.setInt(2, person.getPersonId());
            pst1.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean recordChild(PersonIdentity parent, PersonIdentity child) {
        if (parent == null || child == null || parent == child) {
            return false;
        }
        // Validation Check ancestors of parent, ancestors cannot be recorded as child
        FamilyTree t1 = new FamilyTree();
        TreeSize treeSize = new TreeSize();
        if (treeSize.getTotalNodes() > 0) {
            t1.createMemoryForNodes();
            t1.readParentChildTable();
            t1.findBiologicalRelation(parent.getPersonId(), child.getPersonId());
            for (int x : t1.getAllAncestorsNode1()) {
                if (x == child.getPersonId()) {
                    System.out.println("Cannot add child from ancestors");
                    return false;
                }
            }
        }
        try {
            Connection con = DataConnection.createConnection();
            int j = 0;
            //Query to check duplicate entry
            PreparedStatement pst4 = con.prepareStatement("select parentchildrelation.parent , " +
                    "parentchildrelation.child from parentchildrelation  where parentchildrelation.parent='" + parent.getPersonId() + "' " +
                    "and parentchildrelation.child= '" + child.getPersonId() + "';");
            ResultSet rs4 = pst4.executeQuery();
            while (rs4.next()) {
                j++;
            }
            if (j > 0) {
                System.out.println("Duplicate entry");
                return false;
            }
            int i = 0;
            //Validation Parent child swapped entries not possible
            PreparedStatement pst1 = con.prepareStatement("select parentchildrelation.parent as parent, parentchildrelation.child  as child from" +
                    " parentchildrelation where parent = '" + child.getPersonId() + "' and child = '" + parent.getPersonId() + "'");
            ResultSet rs = pst1.executeQuery();
            while (rs.next()) {
                return false;
            }
            //One parent can have multiple children
            //One child can have two parents at max as only blood relation is considered
            PreparedStatement pst2 = con.prepareStatement("select parentchildrelation.child , parentchildrelation.parent " +
                    "from parentchildrelation where parentchildrelation.child= '" + child.getPersonId() + "';");
            ResultSet rs1 = pst2.executeQuery();
            //Validation, if a child has 2 parents, third parent cannot be recorded
            while (rs1.next()) {
                i++;
            }
            if (i >= 2) {
                System.out.println("");
                return false;
            }

            //Store parent child relationship in table
            PreparedStatement pst3 = con.prepareStatement("INSERT INTO parentchildrelation(parent,child) VALUES (?,?)");
            pst3.setInt(1, parent.getPersonId());
            pst3.setInt(2, child.getPersonId());
            pst3.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    Boolean recordPartnering(PersonIdentity partner1, PersonIdentity partner2) {
        try {
            //Check for null objects
            if (partner1.getPersonId() == partner2.getPersonId() || partner1 == null || partner2 == null) {
                return false;
            }
            Connection con = DataConnection.createConnection();
            PreparedStatement pst = con.prepareStatement("SELECT partneringstatus.partner1, partneringstatus.partner2," +
                    " partneringstatus.relationstatus from partneringstatus");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                //Validation for if already partnered
                // will not be stored unless the latest relationship status is dissolved in the table
                if (rs.getInt(1) == partner1.getPersonId() || rs.getInt(2) == partner1.getPersonId() || rs.getInt(2) == partner2.getPersonId() || rs.getInt(1) == partner2.getPersonId()) {
                    if (rs.getString(3).equalsIgnoreCase("partnered")) {
                        System.out.println("Person already partnered, partnering cannot be recorded");
                        return false;
                    }

                }
            }
            //If currently not partnered , then add partnership
            PreparedStatement pst1 = con.prepareStatement("INSERT INTO partneringstatus (partner1,partner2,relationstatus) VALUES (?,?,?)");
            pst1.setInt(1, partner1.getPersonId());
            pst1.setInt(2, partner2.getPersonId());
            pst1.setString(3, "partnered");
            pst1.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    Boolean recordDissolution(PersonIdentity partner1, PersonIdentity partner2) {
        try { //Validation
            if (partner1.getPersonId() == partner2.getPersonId() || partner1 == null || partner2 == null) {
                return false;
            }
            Connection con = DataConnection.createConnection();
            PreparedStatement pst = con.prepareStatement("SELECT partneringstatus.partner1, partneringstatus.partner2," +
                    " partneringstatus.relationstatus from partneringstatus");
            ResultSet rs = pst.executeQuery();
            //Validation to check if current status is dissolved then nothing updated or Added in database
            while (rs.next()) {
                if ((rs.getInt(1) == partner1.getPersonId() && rs.getInt(2) == partner2.getPersonId()) ||
                        (rs.getInt(2) == partner1.getPersonId() && rs.getInt(1) == partner2.getPersonId())) {
                    if (rs.getString(3).equalsIgnoreCase("dissolved")) {
                        System.out.println("Already dissolved");
                        return false;
                    }
                }

            }
            while (rs.next()) {
                //Update status from partnered to dissolved if current relationship in table exists
                if ((rs.getInt(1) == partner1.getPersonId() && rs.getInt(2) == partner2.getPersonId()) ||
                        (rs.getInt(2) == partner1.getPersonId() && rs.getInt(1) == partner2.getPersonId())) {
                    if ((rs.getString(3).equalsIgnoreCase("partnered"))) {
                        PreparedStatement pst1 = con.prepareStatement("UPDATE partneringstatus SET relationstatus = '" + "dissolved" + "' " +
                                "WHERE partneringstatus.partner1 = '" + partner1.getPersonId() + "' and partneringstatus.partner2 = '" +
                                partner2.getPersonId() + "' or partneringstatus.partner2 = '" + partner1.getPersonId() + "' " +
                                "and partneringstatus.partner1 = '" + partner2.getPersonId() + "';");
                        pst1.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
