import java.util.*;

public class MainUI {


    public static void main(String[] args) {
        // Define variables to manage user input
        String userCommand;
        int command;
        int command1;
        int command2;

        Scanner userInput = new Scanner(System.in);
        Genealogy gen = new Genealogy();
        FileIdentifier m2;
        PersonIdentity p2;
        BiologicalRelation rel = new BiologicalRelation();

        do {
            System.out.println("Enter p to adding person info");
            System.out.println("Enter m for adding media info");
            System.out.println("Enter r for reporting");
            // Find out what the user wants to do
            userCommand = userInput.next();
            /* Do what the user asked for. */

            if (userCommand.equalsIgnoreCase("p")) {
                System.out.println("Enter 1. for - Add person name");
                System.out.println("Enter 2. for - Add person attributes");
                System.out.println("Enter 3. for - Add person notes");
                System.out.println("Enter 4. for - Add person references");
                System.out.println("Enter 5. for - Add relation");
                System.out.println("Enter 6. for - Record Partnering");
                System.out.println("Enter 7. for - Record Dissolution");

                command = userInput.nextInt();
                if (command == 1) {
                    String personName;
                    System.out.println("Enter person name");
                    personName = userInput.next();
                    p2 = gen.addPerson(personName);
                    System.out.println(gen.personIdentityMap.put(p2.getPersonId(), p2));
                } else if (command == 2) {
                    int personId;
                    String key, value;
                    Map<String, String> map1 = new HashMap<>();
                    System.out.println("Enter person id");
                    personId = userInput.nextInt();
                    userInput.nextLine();
                    System.out.println("Enter attribute name from dateofbirth,placeofbirth" +
                            ",gender,occupation,dateofdeath,placeofdeath ");
                    key = userInput.nextLine();
                    System.out.println("Enter value of attribute");
                    value = userInput.nextLine();
                    map1.put(key, value);
                    System.out.println(gen.recordAttributes(gen.personIdentityMap.get(personId), map1));
                } else if (command == 3) {
                    int personId;
                    String notes;
                    System.out.println("Enter person id");
                    personId = userInput.nextInt();
                    userInput.nextLine();
                    System.out.println("Enter notes");
                    notes = userInput.nextLine();
                    System.out.println(gen.recordNote(gen.personIdentityMap.get(personId), notes));
                } else if (command == 4) {
                    int personId;
                    String references;
                    System.out.println("Enter person id");
                    personId = userInput.nextInt();
                    userInput.nextLine();
                    System.out.println("Enter references");
                    references = userInput.nextLine();
                    System.out.println(gen.recordReference(gen.personIdentityMap.get(personId), references));
                } else if (command == 5) {
                    int idParent;
                    int idChild;
                    System.out.println("Enter parent id");
                    idParent = userInput.nextInt();
                    System.out.println("Enter child id");
                    idChild = userInput.nextInt();
                    System.out.println(gen.recordChild(gen.personIdentityMap.get(idParent), gen.personIdentityMap.get(idChild)));
                } else if (command == 6) {
                    int partner1, partner2;
                    System.out.println("Enter partner 1 id");
                    partner1 = userInput.nextInt();
                    System.out.println("Enter partner 2 id");
                    partner2 = userInput.nextInt();
                    System.out.println(gen.recordPartnering(gen.personIdentityMap.get(partner1), gen.personIdentityMap.get(partner2)));
                } else if (command == 7) {
                    int partner1, partner2;
                    System.out.println("Enter partner 1 id");
                    partner1 = userInput.nextInt();
                    System.out.println("Enter partner 2 id");
                    partner2 = userInput.nextInt();
                    System.out.println(gen.recordDissolution(gen.personIdentityMap.get(partner1), gen.personIdentityMap.get(partner2)));
                }

            } else if (userCommand.equalsIgnoreCase("m")) {
                System.out.println("Enter 1. for - Add media name");
                System.out.println("Enter 2. for - Add media attributes");
                System.out.println("Enter 3. for - Add media tags");
                System.out.println("Enter 4. for - Add people in media");

                command1 = userInput.nextInt();
                if (command1 == 1) {

                    String mediaLocation;
                    System.out.println("Enter media location");
                    mediaLocation = userInput.next();
                    m2 = gen.addMediaFile(mediaLocation);
                    System.out.println(gen.fileIdentifierMap.put(m2.getId(), m2));

                } else if (command1 == 2) {
                    int mediaId;
                    String key, value;
                    Map<String, String> map1 = new HashMap<>();
                    System.out.println("Enter media id");
                    mediaId = userInput.nextInt();
                    userInput.nextLine();
                    System.out.println("Enter attribute name from location , dateofpicture");
                    key = userInput.nextLine();
                    System.out.println("Enter value for attribute");
                    value = userInput.nextLine();
                    map1.put(key, value);
                    System.out.println(gen.recordMediaAttributes(gen.fileIdentifierMap.get(mediaId), map1));
                } else if (command1 == 3) {
                    int mediaId;
                    String tag;
                    System.out.println("Enter media id");
                    mediaId = userInput.nextInt();
                    System.out.println("Enter tags");
                    tag = userInput.next();
                    System.out.println(gen.tagMedia(gen.fileIdentifierMap.get(mediaId), tag));
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
                        l1.add(gen.personIdentityMap.get(personId));
                    }
                    System.out.println(gen.peopleInMedia(gen.fileIdentifierMap.get(mediaId), l1));
                }
            } else if (userCommand.equalsIgnoreCase("r")) {
                System.out.println("Enter 1. for - Find Person");
                System.out.println("Enter 2. for - Find Media File");
                System.out.println("Enter 3. for - Find person name");
                System.out.println("Enter 4. for - Find media file name");
                System.out.println("Enter 5. for - Find Relation");
                System.out.println("Enter 6. for - Find Descendants");
                System.out.println("Enter 7. for - Find Ancestors");
                System.out.println("Enter 8. for - Find references and notes");
                System.out.println("Enter 9. for - Find Media By Tag");
                System.out.println("Enter 10. for - Find Media By Location");
                System.out.println("Enter 11. for - Find people in Media file");
                System.out.println("Enter 12. for - Find media which has children of person");
                command2 = userInput.nextInt();

                if (command2 == 1) {
                    String name;
                    System.out.println("Enter person name");
                    name = userInput.next();
                    System.out.println(gen.findPerson(name));

                } else if (command2 == 2) {
                    String name;
                    System.out.println("Enter media name");
                    name = userInput.next();
                    System.out.println(gen.findMediaFile(name));
                } else if (command2 == 3) {
                    int id;
                    System.out.println("Enter person id");
                    id = userInput.nextInt();
                    System.out.println(gen.findName(gen.personIdentityMap.get(id)));
                } else if (command2 == 4) {
                    int id;
                    System.out.println("Enter media id");
                    id = userInput.nextInt();
                    System.out.println(gen.findMediaFile(gen.fileIdentifierMap.get(id)));
                } else if (command2 == 5) {
                    int id1, id2;
                    System.out.println("Enter person 1 id");
                    id1 = userInput.nextInt();
                    System.out.println("Enter person 2 id");
                    id2 = userInput.nextInt();
                    System.out.println(rel = gen.findRelation(gen.personIdentityMap.get(id1), gen.personIdentityMap.get(id2)));
                    System.out.println(rel.getCousinship());
                    System.out.println(rel.getRemoval());

                } else if (command2 == 6) {
                    int id;
                    int generation;
                    System.out.println("Enter person id");
                    id = userInput.nextInt();
                    System.out.println("Enter generations");
                    generation = userInput.nextInt();
                    System.out.println(gen.descendents(gen.personIdentityMap.get(id), generation));


                } else if (command2 == 7) {
                    int id;
                    int generation;
                    System.out.println("Enter person id");
                    id = userInput.nextInt();
                    System.out.println("Enter generations");
                    generation = userInput.nextInt();
                    System.out.println(gen.ancestors(gen.personIdentityMap.get(id), generation));

                } else if (command2 == 8) {
                    int id;
                    System.out.println("Enter person id");
                    id = userInput.nextInt();
                    System.out.println(gen.notesAndReferences(gen.personIdentityMap.get(id)));

                } else if (command2 == 9) {
                    String start_date;
                    String end_date;
                    String tag;
                    userInput.nextLine();
                    System.out.println("Enter start date in format(yyyy-mm-dd):-");
                    start_date = userInput.nextLine();
                    System.out.println("Enter End date in format(yyyy-mm-dd):-");
                    end_date = userInput.nextLine();
                    System.out.println("Enter Tag");
                    tag = userInput.nextLine();
                    System.out.println(gen.findMediaByTag(tag, start_date, end_date));

                } else if (command2 == 10) {
                    String start_date;
                    String end_date;
                    String location;
                    System.out.println("Enter start date in format(yyyy-mm-dd):-");
                    start_date = userInput.next();
                    System.out.println("Enter End date in format(yyyy-mm-dd):-");
                    end_date = userInput.next();
                    System.out.println("Enter Location");
                    location = userInput.next();
                    System.out.println(gen.findMediaByLocation(location, start_date, end_date));

                } else if (command2 == 11) {
                    int totalPeople;
                    int personId;
                    System.out.println("Enter number of people to enter");
                    totalPeople = userInput.nextInt();
                    Set<PersonIdentity> l1 = new HashSet<>();
                    for (int i = 0; i < totalPeople; i++) {
                        System.out.println("Enter person id");
                        personId = userInput.nextInt();
                        l1.add(gen.personIdentityMap.get(personId));
                    }
                    String start_date;
                    String end_date;
                    System.out.println("Enter start date in format(yyyy-mm-dd):-");
                    start_date = userInput.next();
                    System.out.println("Enter End date in format(yyyy-mm-dd):-");
                    end_date = userInput.next();
                    System.out.println(gen.findIndividualsMedia(l1, start_date, end_date));
                } else if (command2 == 12) {
                    int id;
                    System.out.println("Enter person id");
                    id = userInput.nextInt();
                    System.out.println(gen.findBiologicalFamilyMedia(gen.personIdentityMap.get(id)));

                }
            }

        } while (!userCommand.equalsIgnoreCase("quit"));

    }
}
