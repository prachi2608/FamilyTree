import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Genealogy extends PersonInfo {
    //TreeSize class finds size of family tree from the parent child table and creates memory to store the data
    //in java class and used to find biological relation , ancestors and descendants
    TreeSize t = new TreeSize();

    Genealogy() {

        try {
            //Constructor of Genealogy called to fetch data values from existing data base and store objects in map with
            //corresponding object id for person in family tree and media file in media archive
            Connection con = DataConnection.createConnection();
            String sql = "SELECT * FROM mediainfo";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String location = rs.getString(2);
                String name = rs.getString(3);
                FileIdentifier fileIdentifier = new FileIdentifier(location, name, id);
                fileIdentifierMap.put(id, fileIdentifier);
            }
            PreparedStatement pst1 = con.prepareStatement("SELECT * FROM personinfo");
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                int id1 = rs1.getInt(1);
                String name1 = rs1.getString(2);
                PersonIdentity p2 = new PersonIdentity(name1, id1);
                personIdentityMap.put(id1, p2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PersonIdentity findPerson(String name) {
        if (name == null || name.isEmpty() || name.isBlank() || name.equalsIgnoreCase("null")) { //Validation
            return null;
        }
        int i = 0;
        PersonIdentity per = null;
        try {
            for (PersonIdentity person : personIdentityMap.values()) {
                if (person.getName().equalsIgnoreCase(name)) {
                    per = person;
                    i++;

                }
            }
            //If two people with same name are found
            if (i > 1) {
                throw new IllegalArgumentException();
            }
            //Name of person does not exist
            if (per == null) {
                System.out.println("no such person exists");
                return null;
            }

        } catch (Exception e) {
            System.out.println("More than one person exists with the same name");
            return null;
        }
        return per;
    }


    FileIdentifier findMediaFile(String name) {
        if (name == null || name.isEmpty() || name.isBlank() || name.equalsIgnoreCase("null")) { //Validation
            return null;
        }
        int i = 0;
        FileIdentifier f = null;
        for (FileIdentifier file : fileIdentifierMap.values()) {
            if (file.getFileName().equalsIgnoreCase(name)) {
                f = file;
                i++;

            }
        }
        try {//Duplicate file Name
            if (i > 1) {
                throw new IllegalArgumentException();

            }
        } catch (Exception e) {
            System.out.println("More than one file exists with same name");
            return null;
        }
        if (f == null) {
            System.out.println("no such file exists");
            return null;
        }
        return f;
    }


    String findName(PersonIdentity id) {
        if (id == null) {
            return null;
        }//Validation
        String personName = null;
        try {
            //get person name from object
            personName = id.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return personName;
    }

    String findMediaFile(FileIdentifier fileId) {
        if (fileId == null) {
            return null;
        }
        String fileName = null;
        try {
            //get filename from object
            fileName = fileId.getFileName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }


    BiologicalRelation findRelation(PersonIdentity person1, PersonIdentity person2) {
        if (person1 == null || person2 == null) {
            return null;
        }
        int id1 = person1.getPersonId();
        int id2 = person2.getPersonId();
        FamilyTree obj = new FamilyTree();
        //Everytime the find realtion method is called, data is fetched from database and tree is built
        obj.createMemoryForNodes();
        //Add edges from parent child table from table
        obj.readParentChildTable();
        //findBiologicalRelation returns object of BiologicalRelation Class which has cousinship and degree of removal
        return obj.findBiologicalRelation(id1, id2);

    }

    Set<PersonIdentity> descendents(PersonIdentity person, Integer generations) {
        Set<PersonIdentity> descendantsSet = new HashSet();
        if (person == null) { //Validation
            return null;
        }
        if (generations <= 0) {
            return descendantsSet;
        }
        //FamilyTree class contains information about ancestors and descendants
        FamilyTree obj = new FamilyTree();
        //Build tree as per memory from parent child table
        obj.createMemoryForNodes();
        //Add edges between parent and child
        obj.readParentChildTable();
        List<Integer> descList = obj.getDescendants(person.getPersonId(), generations);
        for (int i : descList) {
            for (PersonIdentity p : personIdentityMap.values()) {
                //Check for objects in Map from values
                //Add objects of descendants from id.
                if (p.getPersonId() == i) {
                    descendantsSet.add(p);
                }
            }
        }
        return descendantsSet;
    }

    Set<PersonIdentity> ancestors(PersonIdentity person, Integer generations) {
        Set<PersonIdentity> ancestorsSet = new HashSet();
        if (person == null) {
            return null;
        }
        if (generations <= 0) {
            return ancestorsSet;
        }
        //FamilyTree class contains information about ancestors and descendants
        FamilyTree obj = new FamilyTree();
        //Build tree as per memory from parent child table
        obj.createMemoryForNodes();
        //Add edges between parent and child
        obj.readParentChildTable();
        List<Integer> anscList = obj.getAncestors(person.getPersonId(), generations);
        for (int i : anscList) {
            //Check for objects in Map from values
            //Add objects of ancestors from id.
            for (PersonIdentity p : personIdentityMap.values()) {
                if (p.getPersonId() == i) {
                    ancestorsSet.add(p);
                }
            }

        }
        return ancestorsSet;
    }

    List<String> notesAndReferences(PersonIdentity person) {
        if (person == null) { //Validation
            return null;
        }
        List<String> notesandreference = new ArrayList<>();
        try {
            //Find notes and references is order in which they are entered using timestamp
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1 = con.prepareStatement("SELECT recordreferences.personreferences, recordreferences.lastUpdated " +
                    "from recordreferences where personid ='" + person.getPersonId() + "'\n" +
                    "UNION\n" +
                    "SELECT recordnotes.note , recordnotes.lastUpdated FROM recordnotes  WHERE personid = '" + person.getPersonId() + "' order by lastUpdated ;");
            ResultSet rs = pst1.executeQuery();
            while (rs.next()) {
                notesandreference.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notesandreference;
    }

    Set<FileIdentifier> findMediaByTag(String tag, String startDate, String endDate) {
        Set<FileIdentifier> mediaTag = new HashSet<>();
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        startDate = checkDate(startDate);
        endDate = checkDate(endDate);
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1;
            //If startDate and endDate are null, no range of dates
            if (startDate == null || endDate == null) {
                pst1 = con.prepareStatement("select recordtags.mediaid,recordtags.tags," +
                        " mediaattributes.dateofpicture from mediaattributes" +
                        " inner join recordtags on mediaattributes.mediaid = recordtags.mediaid " +
                        " where recordtags.tags='" + tag + "';");
                //If start date is null all files selected upto end date
            } else if (startDate == null && endDate != null) {
                pst1 = con.prepareStatement("select recordtags.mediaid,recordtags.tags," +
                        " mediaattributes.dateofpicture from mediaattributes" +
                        " inner join recordtags on mediaattributes.mediaid = recordtags.mediaid " +
                        "  and mediaattributes.dateofpicture <= '" + endDate + "' where recordtags.tags='" + tag + "' ;");
                //If end date is null all files selected from start date to no restriction
            } else if (startDate != null && endDate == null) {
                pst1 = con.prepareStatement("select recordtags.mediaid,recordtags.tags," +
                        " mediaattributes.dateofpicture from mediaattributes" +
                        "join recordtags on mediaattributes.mediaid = recordtags.mediaid " +
                        "  and  mediaattributes.dateofpicture  where recordtags.tags='" + tag + "' " +
                        "where mediaattributes.dateofpicture >= '" + startDate + "' ;");
            } else {
                //Start and end date range given
                pst1 = con.prepareStatement("select recordtags.mediaid,recordtags.tags,mediaattributes.dateofpicture from " +
                        "mediaattributes inner join recordtags on mediaattributes.mediaid = recordtags.mediaid inner join mediainfo on mediainfo.mediaid = mediaattributes.mediaid and  " +
                        "mediaattributes.dateofpicture between '" + startDate + "' and '" + endDate + "' where recordtags.tags='" + tag + "';");
            }
            getFileObject(mediaTag, pst1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaTag;
    }

    Set<FileIdentifier> findMediaByLocation(String location, String startDate, String endDate) {
        Set<FileIdentifier> mediaLocation = new HashSet<>();
        if (location == null || location.isEmpty()) {
            return null;
        }
        startDate = checkDate(startDate);
        endDate = checkDate(endDate);
        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1;
            //If startDate and endDate are null, no range of dates
            if (startDate == null && endDate == null) {
                pst1 = con.prepareStatement("select mediaattributes.mediaid, mediaattributes.location," +
                        "mediaattributes.dateofpicture from mediaattributes inner join mediainfo on" +
                        "mediaattributes.mediaid= mediainfo.mediaid  where mediaattributes.location = '" + location + "';");
            }
            //If start date is null all files selected upto end date
            else if (startDate == null && endDate != null) {
                pst1 = con.prepareStatement("select mediaattributes.mediaid, mediaattributes.location," +
                        "mediaattributes.dateofpicture from mediaattributes inner join mediainfo on" +
                        "mediaattributes.mediaid= mediainfo.mediaid  where mediaattributes.location = '" + location + "'" +
                        " where mediaattributes.dateofpicture <= '" + endDate + "'  ;");
                //If end date is null all files selected from start date to no restriction
            } else if (startDate != null && endDate == null) {
                pst1 = con.prepareStatement("select mediaattributes.mediaid, mediaattributes.location," +
                        "mediaattributes.dateofpicture from mediaattributes inner join mediainfo on" +
                        "mediaattributes.mediaid= mediainfo.mediaid  where mediaattributes.location = '" + location + "'" +
                        " where mediaattributes.dateofpicture >= '" + startDate + "'  ;");
            } else { //Start and end date range given
                pst1 = con.prepareStatement("select mediaattributes.mediaid, mediaattributes.location,mediaattributes.dateofpicture " +
                        "from mediaattributes inner join mediainfo on mediaattributes.mediaid= mediainfo.mediaid and " +
                        "mediaattributes.dateofpicture between '" + startDate + "' and '" + endDate + "'  where mediaattributes.location = '" + location + "';");
            }
            getFileObject(mediaLocation, pst1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaLocation;
    }

    //Get file objects based on
    Set<FileIdentifier> getFileObject(Set<FileIdentifier> fileSet, PreparedStatement pst1) throws SQLException {
        ResultSet rs = pst1.executeQuery();
        int id;
        while (rs.next()) {
            id = (rs.getInt(1));
            for (FileIdentifier file : fileIdentifierMap.values()) {
                //Add file objects to set from values of map corresponding to id's in key
                if (file.getId() == id) {
                    fileSet.add(file);
                }
            }
        }
        return fileSet;
    }

    List<FileIdentifier> findIndividualsMedia(Set<PersonIdentity> people, String startDate, String endDate) {
        List<FileIdentifier> peopleInMedia = new ArrayList<>();
        //Check if list people is not null
        if (people.isEmpty()) {
            return null;
        }
        //Check if all objects of list people are not null
        //If one object is null return null;
        for (PersonIdentity per : people) {
            if (per == null) {
                System.out.println("Please enter correct person in the list");
                return null;
            }
        }
        //Check if dates are null or empty
        startDate = checkDate(startDate);
        endDate = checkDate(endDate);

        try {
            Connection con = DataConnection.createConnection();
            PreparedStatement pst1;
            //Find media files which have person from list people
            for (PersonIdentity per : people) {
                //If startDate and endDate are null, no range of dates
                if (startDate == null && endDate == null) {
                    pst1 = con.prepareStatement("select personinmedia.personid,personinmedia.mediaid from " +
                            "personinmedia inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid " +
                            "inner join mediaattributes on mediaattributes.mediaid = personinmedia.mediaid " +
                            " where personinmedia.personid = '" + per.getPersonId() + "'" +
                            " order by mediaattributes.dateofpicture, mediainfo.filename;");
                    //If start date is null all files selected upto end date
                } else if (startDate == null && endDate != null) {
                    pst1 = con.prepareStatement("select personinmedia.personid,personinmedia.mediaid from " +
                            "personinmedia inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid " +
                            "inner join mediaattributes on mediaattributes.mediaid = personinmedia.mediaid " +
                            " where personinmedia.personid = '" + per.getPersonId() + "'" +
                            " and mediaattributes.dateofpicture <='" + endDate + "' order by mediaattributes.dateofpicture, mediainfo.filename ;");
                }//If end date is null all files selected from start date to no restriction
                else if (startDate != null && endDate == null) {
                    pst1 = con.prepareStatement("select personinmedia.personid,personinmedia.mediaid from " +
                            "personinmedia inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid " +
                            "inner join mediaattributes on mediaattributes.mediaid = personinmedia.mediaid " +
                            " where personinmedia.personid = '" + per.getPersonId() + "'" +
                            " and  mediaattributes.dateofpicture >= '" + startDate + "' order by mediaattributes.dateofpicture, mediainfo.filename ;");
                } else {
                    //Start and end date range given
                    pst1 = con.prepareStatement("select personinmedia.personid,personinmedia.mediaid " +
                            "from personinmedia inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid " +
                            "inner join mediaattributes on mediaattributes.mediaid = personinmedia.mediaid  " +
                            "and mediaattributes.dateofpicture between '" + startDate + "' and '" + endDate + "' where " +
                            "personinmedia.personid = '" + per.getPersonId() + "' order by mediaattributes.dateofpicture, mediainfo.filename;");
                }
                //Add files with null dates at the end
                PreparedStatement pst2 = con.prepareStatement("select personinmedia.personid,personinmedia.mediaid from " +
                        "personinmedia inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid " +
                        "inner join mediaattributes on mediaattributes.mediaid = personinmedia.mediaid " +
                        " where personinmedia.personid = '" + per.getPersonId() + "' and mediaattributes.dateofpicture is null" +
                        " order by mediaattributes.dateofpicture, mediainfo.filename;");

                ResultSet rs = pst1.executeQuery();
                ResultSet rs1 = pst2.executeQuery();
                addMediaFile(peopleInMedia, rs);
                addMediaFile(peopleInMedia, rs1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return peopleInMedia;
    }

    List<FileIdentifier> findBiologicalFamilyMedia(PersonIdentity person) {
        if (person == null) {
            return null;
        }
        //Make the family tree and add edges
        FamilyTree obj = new FamilyTree();
        obj.createMemoryForNodes();
        obj.readParentChildTable();
        //Get descendants from the tree upto level 1 i.e. direct children
        List<Integer> descList = obj.getDescendants(person.getPersonId(), 1);
        List<FileIdentifier> biologicalFamilyMedia = new ArrayList<>();
        System.out.println();
        try {

            Connection con = DataConnection.createConnection();
            //Query to find media which contains immediate children of person
            PreparedStatement pst1 = con.prepareStatement("select mediaattributes.dateofpicture," +
                    " mediainfo.mediaid , parentchildrelation.child , personinmedia.personid, personinmedia.mediaid " +
                    "from parentchildrelation inner join personinmedia on personinmedia.personid= parentchildrelation.child " +
                    "inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid inner join  mediaattributes on" +
                    " mediainfo.mediaid = mediaattributes.mediaid  where parentchildrelation.parent = '" + person.getPersonId() + "' and mediaattributes.dateofpicture != 'null'" +
                    " order by mediaattributes.dateofpicture ,mediainfo.filename;\n");
            //Query to add files with null dates at the end of list
            PreparedStatement pst2 = con.prepareStatement("select mediaattributes.dateofpicture, mediainfo.mediaid , " +
                    "parentchildrelation.child , personinmedia.personid, personinmedia.mediaid from parentchildrelation inner join personinmedia on" +
                    " personinmedia.personid= parentchildrelation.child inner join mediainfo on mediainfo.mediaid = personinmedia.mediaid inner join " +
                    " mediaattributes on mediainfo.mediaid = mediaattributes.mediaid  where parentchildrelation.parent = '" + person.getPersonId() + "' and " +
                    "mediaattributes.dateofpicture is null ;\n");

            ResultSet rs = pst1.executeQuery();
            ResultSet rs1 = pst2.executeQuery();
            //Add files
            addMediaFile(biologicalFamilyMedia, rs);
            addMediaFile(biologicalFamilyMedia, rs1);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return biologicalFamilyMedia;
    }

    //Method to add get media file objects from media id and add to list
    private void addMediaFile(List<FileIdentifier> mediaList, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int fileId = (rs.getInt(2));
            for (FileIdentifier file : fileIdentifierMap.values()) {
                if (file.getId() == fileId) {
                    //Check if file object already added once
                    if (!mediaList.contains(file)) {
                        mediaList.add(file);
                        System.out.println(file.getId());
                    }
                }
            }
        }
    }

    //Check date for reporting functions
    String checkDate(String startDate) {
        if (startDate == null || startDate.isEmpty() || startDate.isBlank() || startDate.equalsIgnoreCase("null") || startDate.equalsIgnoreCase("empty")) {
            startDate = null;
        }
        return startDate;
    }
}

