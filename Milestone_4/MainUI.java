import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

public class MainUI {


    public static void main(String[] args) throws Exception {
        // Define variables to manage user input
        String userCommand = "";
        int command;
        int command1;
        int command2;
        BufferedReader stream = null;
        PrintWriter outStream = null;
        Scanner userInput = new Scanner(System.in);

        //Map<Integer, PersonIdentity> personIdentityMap = new HashMap<>();
        Genealogy gen = new Genealogy();
        Reporting r1 = new Reporting();
        FileIdentifier m1 = new FileIdentifier();
        FileIdentifier m2 = null;
        PersonIdentity p1 = new PersonIdentity();
        PersonIdentity p2 = null;


        do {
            // Find out what the user wants to do
            userCommand = userInput.next();
            /* Do what the user asked for. */

            if (userCommand.equalsIgnoreCase("PersonInfo")) {
                System.out.println("Enter 1. for - Add person name");
                System.out.println("Ent 2. for - Add person attributes");
                System.out.println("Enter 3. for - Add person notes");
                System.out.println("Enter 4. for - Add person references");
                System.out.println("Enter 5. for - Add relation");
                System.out.println("Enter 6. for - Exit personInfo");

                do {

                    command = userInput.nextInt();
                    if (command == 1) {
                        String personName;
                        System.out.println("Enter person name");
                        personName = userInput.next();
                        p2 = gen.addPerson(personName);
                        p1.getPersonIdentityMap().put(p2.getPersonId(), p2);

                    } else if (command == 2) {
                        int personId;
                        String key, value;
                        Map<String, String> map1 = new HashMap<>();
                        System.out.println("Enter person id");
                        personId = userInput.nextInt();
                        System.out.println("Enter attribute name and details");
                        key = userInput.next();
                        value = userInput.next();
                        map1.put(key, value);
                        gen.recordAttributes(p1.getPersonIdentityMap().get(personId), map1);
                    } else if (command == 3) {
                        int personId;
                        String notes;
                        System.out.println("Enter person id");
                        personId = userInput.nextInt();
                        System.out.println("Enter notes");
                        notes = userInput.next();
                        gen.recordNote(p1.getPersonIdentityMap().get(personId), notes);
                    } else if (command == 4) {
                        int personId;
                        String references;
                        System.out.println("Enter person id");
                        personId = userInput.nextInt();
                        System.out.println("Enter notes");
                        references = userInput.next();
                        gen.recordReference(p1.getPersonIdentityMap().get(personId), references);
                    } else if (command == 5) {
                        int idParent;
                        int idChild;
                        System.out.println("Enter parent id");
                        idParent = userInput.nextInt();
                        System.out.println("Enter child id");
                        idChild = userInput.nextInt();
                        gen.recordChild(p1.getPersonIdentityMap().get(idParent), p1.personIdentityMap.get(idChild));
                    }
                } while (command != 6);
            } else if (userCommand.equalsIgnoreCase("mediaArchive")) {
                System.out.println("Enter 1. for - Add media name");
                System.out.println("Enter 2. for - Add media attributes");
                System.out.println("Enter 3. for - Add person tags");
                System.out.println("Enter 4. for - Add people in media");
                System.out.println("Enter 6. for - Exit mediaArchive");

                do {

                    command1 = userInput.nextInt();
                    if (command1 == 1) {

                        String mediaLocation;
                        System.out.println("Enter media location");
                        mediaLocation = userInput.next();
                        m2 = gen.addMediaFile(mediaLocation);
                        m1.getFileIdentifierMap().put(m2.getId(), m2);

                    } else if (command1 == 2) {
                        int mediaId;
                        String key, value;
                        Map<String, String> map1 = new HashMap<>();
                        System.out.println("Enter media id");
                        mediaId = userInput.nextInt();
                        System.out.println("Enter attribute name and details");
                        key = userInput.next();
                        value = userInput.next();
                        map1.put(key, value);
                        gen.recordMediaAttributes(m1.getFileIdentifierMap().get(mediaId), map1);
                    } else if (command1 == 3) {
                        int mediaId;
                        String tag;
                        System.out.println("Enter person id");
                        mediaId = userInput.nextInt();
                        System.out.println("Enter tags");
                        tag = userInput.next();
                        gen.tagMedia(m1.getFileIdentifierMap().get(mediaId), tag);
                    } else if (command1 == 4) {
                        int totalPeople;
                        int mediaId;
                        int personId;
                        System.out.println("Enter number of people to enter");
                        totalPeople = userInput.nextInt();
                        List<PersonIdentity> l1 = new ArrayList<>();
                        System.out.println("Enter media id");
                        mediaId = userInput.nextInt();
                        for (int i = 0; i < totalPeople; i++) {
                            System.out.println("Enter person id");
                            personId = userInput.nextInt();
                            l1.add(p1.getPersonIdentityMap().get(personId));
                        }
                        gen.peopleInMedia(m1.getFileIdentifierMap().get(mediaId), l1);
                    }
                } while (command1 != 6);
            } else if (userCommand.equalsIgnoreCase("Reporting")) {
                System.out.println("Enter 1. for - Find Person");
                System.out.println("Enter 2. for - Find Media File");
                System.out.println("Enter 3. for - Find person name");
                System.out.println("Enter 4. for - Find media file name");
                System.out.println("Enter 6. for - Exit mediaArchive");
                command2 = userInput.nextInt();

                if (command2 == 1) {
                    String name;
                    System.out.println("Enter person name");
                    name = userInput.next();
                    r1.findPerson(name);

                } else if (command2 == 2) {
                    String name;
                    System.out.println("Enter media name");
                    name = userInput.next();
                    r1.findMediaFile(name);
                } else if (command2 == 3) {
                    BiologicalRelation rel = r1.findRelation(p1.getPersonIdentityMap().get(2), p1.getPersonIdentityMap().get(7));
                    System.out.println(rel.getDegreeofcousinship());
                    System.out.println(rel.getDegreeofremoval());

                }
            }
        } while (!userCommand.equalsIgnoreCase("quit"));
    }

}
