import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PersonIdentity {
    String name;
    int personId;
    PersonIdentity id;
    Map<Integer, PersonIdentity> personIdentityMap = new HashMap<>();

    public Map<Integer, PersonIdentity> getPersonIdentityMap() {
        return personIdentityMap;
    }

    public void setPersonIdentityMap(Map<Integer, PersonIdentity> personIdentityMap) {
        this.personIdentityMap = personIdentityMap;
    }

    PersonIdentity(String name, int personId) {
        this.name = name;
        this.personId = personId;


    }
    PersonIdentity() throws SQLException {

        Connection con = DataConnection.createConnection();
        String sql = "SELECT * FROM personinfo";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        int id = 0;
        String name;
        while (rs.next()) {
            id = rs.getInt(1);
            name = rs.getString(2);
            PersonIdentity p2 = new PersonIdentity(name, id);
            personIdentityMap.put(id, p2);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }


}
