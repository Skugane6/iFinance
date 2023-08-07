package se2203b.assignments.ifinance;

import java.sql.*;
import java.util.*;


public class AccountGroupsAdapter {
    static Connection con;
    public AccountGroupsAdapter(Connection conn, Boolean reset) throws SQLException {
        con = conn;

        if (reset) {
            Statement stmt = con.createStatement();
            try {
                // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                stmt.execute("DROP TABLE AccountGroups");
            } catch (SQLException ex) {
            } finally {
                // Create the table
                stmt.execute("CREATE TABLE AccountGroups ("
                        + "ID INT,"
                        + "GroupName VARCHAR(64),"
                        + "Parent INT,"
                        + "Element VARCHAR(64)"
                        + ")");
                populateAccountGroups();
            }
        }
    }

    public static void insert(Group data) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO AccountGroups (ID,  GroupName,  Parent, Element) "
                + "VALUES ('"
                + data.getID() + "', '"
                + data.getName() + "', "
                + data.getParent().getID() + ", '"
                + data.getElement().getName() + "' )"
        );
    }


    public static void createGroup(String GroupName, int Parent, String Element) throws SQLException{
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO AccountGroups(ID, GroupName, Parent, Element) " +
                "VALUES (" + getMax() + ", '"+GroupName+"', "+Parent+", '"+Element+"')");
    }

    public static void updateGroup(String oldName, String newName) throws SQLException {
        Statement myStmt = con.createStatement();
        myStmt.executeUpdate("UPDATE AccountGroups "
                + "SET GroupName = '" + newName + "' "
                + "WHERE GroupName = '" + oldName + "'");
    }


    public static void deleteGroup(int id) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM AccountGroups " +
                "WHERE ID = "+ id +"");
    }

    public static int getParent(String parentName) throws SQLException{
        ResultSet resultSet;
        Statement stmt = con.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE GroupName = '"+parentName+"'";
        resultSet = stmt.executeQuery(sqlStatement);
        int parentID = 0;
        while(resultSet.next()){
            parentID = resultSet.getInt(3);
        }
        return parentID;
    }

    public static int getMax() throws SQLException {
        int num = 0;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM AccountGroups");
        if (rs.next()) num = rs.getInt(1);
        return num+1;
    }

    public static int getID(String name) throws SQLException{
        int id = 0;
        ResultSet resultSet;
        Statement stmt = con.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE GroupName = '"+name+"'";
        resultSet = stmt.executeQuery(sqlStatement);
        while(resultSet.next()){
            id = resultSet.getInt(1);
        }
        return id;
    }

    public static String getElement(String name) throws SQLException{
        ResultSet resultSet;
        Statement stmt = con.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE GroupName = '"+name+"'";
        resultSet = stmt.executeQuery(sqlStatement);
        String element = null;
        while(resultSet.next()){
            element = resultSet.getString(4);
        }
        return element;
    }

    public static Group getGroup(int id) throws SQLException{
        ResultSet resultSet;
        Statement stmt = con.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE ID = "+id+"";
        resultSet = stmt.executeQuery(sqlStatement);
        Group grp = null;


        while(resultSet.next()){
            if (resultSet.getInt(3) != 0){
                int id1 = resultSet.getInt(3);
                ResultSet resultSet1;
                Statement stmt1 = con.createStatement();
                String sqlStatement1 = "SELECT * FROM AccountGroups WHERE ID = "+id1+"";
                resultSet1 = stmt1.executeQuery(sqlStatement1);
                while(resultSet1.next()){
                    Group group = new Group(resultSet.getInt(3), resultSet1.getString(2),null,null );
                    grp = new Group(id, resultSet.getString(2), group,null);
                }

            }

            else{
                grp = new Group(id, resultSet.getString(2), null,null);
            }

        }
        return grp;
    }


    public static ArrayList<Group> GroupsList() throws SQLException{
        ArrayList<Group> list = new ArrayList<>();
        ResultSet resultSet;
        Statement stmt = con.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups";
        resultSet = stmt.executeQuery(sqlStatement);


        while(resultSet.next()){
            list.add(new Group(resultSet.getInt(1), resultSet.getString(2),
                    getGroup(resultSet.getInt(1)).getParent(),
                    AccountCategoryAdapter.getAccountCategory(resultSet.getString(4))));
        }
        return list;
    }

    public static void populateAccountGroups() throws SQLException {
        Statement stmt = con.createStatement();


        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",1,"Fixed assets",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",2, "Investments",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",3, "Branch/divisions",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",4, "Cash in hand",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",5, "Bank accounts",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",6, "Deposit",0,"Assets"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",7, "Advance",0,"Assets"));

        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",8, "Capital account",0,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",9, "Long term loans",0,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",10, "Current liabilities",0,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",11, "Reserves and surplus",0,"Liabilities"));

        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",12, "Sales account",0,"Income"));

        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",13, "Purchase account",0,"Expenses"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",14, "Expenses (Direct)",0,"Expenses"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",15, "Expenses (InDirect)",0,"Expenses"));

        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",16, "Secured loans",9,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",17, "Unsecured loans",9,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",18, "Duties taxes payable",10,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",19, "Provisions",10,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",20, "Sundry creditors",10,"Liabilities"));
        stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",21, "Bank od & limits",10,"Liabilities"));

    }
}