/* Program to find LCA of n1 and n2 using one DFS on
the Tree */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.IntStream;

public class FamilyTree {
    int flag1 = 0;
    TreeSize t = new TreeSize();
    List<Integer>[] tree = new ArrayList[t.getTotalNodes()];
    List<Integer>[] addDescendants = new ArrayList[t.getTotalNodes()];
    List<Integer>[] addAncestors = new ArrayList[t.getTotalNodes()];
    //Stores descendants of a person upto generation mentioned
    List<Integer> findDes = new ArrayList<>();
    //Stores ancestors of a person upto generation mentioned
    List<Integer> findAns = new ArrayList<>();
    //Stores all ancestors of a peron
    List<Integer> allAncestorsNode1 = new ArrayList<>();
    List<Integer> allAncestorsNode2 = new ArrayList<>();
    //Map to store ancestors of node1 according to the levels stored in values
    Map<Integer, Integer> levelMapNode1 = new HashMap<>();
    //Map to store ancestors of node2 according to levels stored in values
    Map<Integer, Integer> levelMapNode2 = new HashMap<>();
    //BiologicalRelation object to return cousinship and removal
    BiologicalRelation relation = new BiologicalRelation();
    //Define level for adding nodes in map
    int levelNode1 = 2;
    int levelNode2 = 2;

    //Getter setter for finding list which contains all ancestors of Node1
    public List<Integer> getAllAncestorsNode1() {
        return allAncestorsNode1;
    }

    public void setAllAncestorsNode1(List<Integer> allAncestorsNode1) {
        this.allAncestorsNode1 = allAncestorsNode1;
    }

    public List<Integer> getAllAncestorsNode2() {
        return allAncestorsNode2;
    }

    public void setAllAncestorsNode2(List<Integer> allAncestorsNode2) {
        this.allAncestorsNode2 = allAncestorsNode2;
    }

    //Function returns ancestors of person upto restricted level
    List<Integer> getAncestors(int node, int level) {
        List<Integer> findD = new ArrayList<>();
        for (int i = 0; i < tree[node].size(); i++) {
            if (!addAncestors[node].contains(tree[node].get(i))) {
                findAns.add(tree[node].get(i));
            }
        }
        if (level > 1) {
            findD = recurAns(findAns);
            for (int d = 0; d < level - 2; d++) {
                recurAns(findD);
            }
        }
        return findAns;

    }

    ////////////////////////////This part of the program finds ancestors and descendants upto particular generation//////////////////////////
    //Recursive function for ancestors more than level 1
    List<Integer> recurAns(List<Integer> call) {
        List<Integer> findB = new ArrayList<>();
        for (int j : call) {
            if (tree[j].get(0) != null) {
                for (int f = 0; f < tree[j].size(); f++)
                    if (!findAns.contains(tree[j].get(f))) {
                        if (!addAncestors[j].contains(tree[j].get(f))) {
                            findB.add(tree[j].get(f));
                        }
                    }
            }
        }

        findAns.addAll(findB);
        return findB;
    }

    //Function returns descendants of a person upto restricted level
    List<Integer> getDescendants(int node, int level) {
        List<Integer> findC = new ArrayList<>();
        for (int i = 0; i < tree[node].size(); i++) {
            if (!addDescendants[node].contains(tree[node].get(i))) {
                findDes.add(tree[node].get(i));
            }
        }
        if (level > 1) {
            findC = recurDescendant(findDes);

        }
        return findDes;
    }

    //Recursive function called for descendants for generations more than
    List<Integer> recurDescendant(List<Integer> call) {
        List<Integer> findB = new ArrayList<>();
        for (int j : call) {

            if (tree[j].get(0) != null) {
                for (int f = 0; f < tree[j].size(); f++)
                    if (!findDes.contains(tree[j].get(f))) {
                        if (!addDescendants[j].contains(tree[j].get(f))) {
                            findB.add(tree[j].get(f));
                        }
                    }
            }
        }
        findDes.addAll(findB);
        return findB;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    List<Integer> person1LCA(List<Integer> call) {
        //Returns all ancestors of person1 , for later finding lowest common ancestor between person 1 and 2
        List<Integer> findN1 = new ArrayList<>();
        for (int j : call) {
            if (tree[j].get(0) != null) {
                for (int f = 0; f < tree[j].size(); f++)
                    if (!allAncestorsNode1.contains(tree[j].get(f))) {
                        if (!addAncestors[j].contains(tree[j].get(f))) {
                            findN1.add(tree[j].get(f));
                        }
                    }
            }
        }
        IntStream.range(0, findN1.size()).forEach(x -> levelMapNode1.put(findN1.get(x), levelNode1));
        allAncestorsNode1.addAll(findN1);
        //Increment level after adding for one level
        levelNode1++;
        return findN1;
    }

    List<Integer> person2LCA(List<Integer> call) {
        //Returns all ancestors of person2 , for later finding lowest common ancestor between person 1 and 2
        List<Integer> findN2 = new ArrayList<>();
        for (int j : call) {
            if (tree[j].get(0) != null) {
                for (int f = 0; f < tree[j].size(); f++)
                    if (!allAncestorsNode2.contains(tree[j].get(f))) {
                        if (!addAncestors[j].contains(tree[j].get(f))) {
                            findN2.add(tree[j].get(f));
                        }
                    }
            }
        }
        for (Integer integer : findN2) {
            levelMapNode2.put(integer, levelNode2);
        }
        allAncestorsNode2.addAll(findN2);
        //Increment level after adding for one level
        levelNode2++;
        return findN2;
    }

    //Function to find biological relation between two persons
    BiologicalRelation findBiologicalRelation(int person1, int person2) {
        int node2Level = 0;
        int node1Level = 0;
        List<Integer> findD = new ArrayList<>();

        //Find Ancestors of person1 along with levels assigned
        for (int i = 0; i < tree[person1].size(); i++) {
            if (!addAncestors[person1].contains(tree[person1].get(i))) {
                allAncestorsNode1.add(tree[person1].get(i));
                levelMapNode1.put(tree[person1].get(i), 1);
            }
        }
        findD = person1LCA(allAncestorsNode1);
        while (!findD.isEmpty()) {
            findD = person1LCA(allAncestorsNode1);
        }

        //Find ancestors of person2 along with levels assigned
        List<Integer> findE = new ArrayList<>();
        for (int i = 0; i < tree[person2].size(); i++) {
            if (!addAncestors[person2].contains(tree[person2].get(i))) {
                allAncestorsNode2.add(tree[person2].get(i));
                levelMapNode2.put(tree[person2].get(i), 1);
            }
        }
        findE = person2LCA(allAncestorsNode2);
        while (!findE.isEmpty()) {
            findE = person2LCA(allAncestorsNode2);
        }

        //Check if person2 is an ancestors of person1, then LCA is person2
        if (allAncestorsNode1.contains(person2)) {
            for (int n : levelMapNode1.keySet()) {
                if (n == person2) {
                    node1Level = levelMapNode1.get(n);
                    break;
                }
            }
            //Set relation
            relation.setCousinship(-1);
            relation.setRemoval(Math.abs(node1Level - node2Level));
        }
        //Check if person1 is an ancestors of person2 , then LCA is person1
        if (allAncestorsNode2.contains(person1)) {
            for (int n : levelMapNode2.keySet()) {
                if (n == person1) {
                    node2Level = levelMapNode2.get(n);
                }
            }
            //Set Relation
            relation.setCousinship(-1);
            relation.setRemoval(Math.abs(node1Level - node2Level));
        }

        //If person1 and person2 are not each other's ancestors or descendants
        if (!allAncestorsNode2.contains(person1) && !allAncestorsNode1.contains(person2)) {
            for (int n1 : allAncestorsNode1) {
                for (int n2 : allAncestorsNode2) {
                    if (n1 == n2) {
                        //Find LCA from their ancestors list , and find their levels
                        flag1++;
                        int leveln1 = levelMapNode1.get(n1);
                        int leveln2 = levelMapNode2.get(n2);
                        if (flag1 < 2) {
                            //Set relation
                            relation = relationship(leveln1, leveln2);
                            return relation;
                        }
                    }

                }

            }
            //No common ancestor found the return null
            if (flag1 == 0) {
                return null;
            }
        }
        return relation;
    }

    BiologicalRelation relationship(int nl1, int nl2) {
        //Function called after finding distance from LCA
        //Find cousinship and degree of removal
        if (flag1 < 2) {
            int cousinship;
            int removal;
            if (nl1 > nl2) {
                cousinship = nl2 - 1;
            } else {
                cousinship = nl1 - 1;
            }

            removal = Math.abs(nl1 - nl2);
            relation.setRemoval(removal);
            relation.setCousinship(cousinship);

        }
        return relation;
    }

    void createMemoryForNodes() {
        //Size of Vector array defined by size of tree, which is found by size of parent child table
        for (int i = 0; i < (t.getTotalNodes()); i++) {
            tree[i] = new ArrayList<Integer>();
            addDescendants[i] = new ArrayList<Integer>();
            addAncestors[i] = new ArrayList<Integer>();
        }

    }

    void addParenChild(int a, int b) {
        //Add to Vector array.
        //Add child for each parent
        //Maintain separate data structure for ancestor and descendants
        tree[a].add(b);
        tree[b].add(a);
        addDescendants[b].add(a);
        addAncestors[a].add(b);

    }

    void readParentChildTable() {
        //Read data from parent child table and add to the data structure
        try {
            Connection con = DataConnection.createConnection();
            //Query to get all parent child relation
            PreparedStatement pst = con.prepareStatement("SELECT parentchildrelation.parent, parentchildrelation.child from parentchildrelation");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                addParenChild(rs.getInt(1), rs.getInt(2));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
