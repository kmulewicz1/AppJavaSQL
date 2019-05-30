import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

class A{
    static Scanner sc = new Scanner(System.in);


    static  Set<String> set = new HashSet<>();

    public static String init(ResultSet rs) throws Exception
    {
        set.clear();
        while (rs.next()) {
            set.add(rs.getString(1));
        }//while
        System.out.println("What you want to do? add/edit/delete");

        return sc.nextLine();
    }
    public static void main(String args[]){

        try{

            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/med","root","Qwerty123");
            Statement stmt=con.createStatement();
            System.out.println("Which table? doctors/hospitals/employ");
            String TableChoose=sc.nextLine();
            switch (TableChoose) {
                case "doctors": {
                    ResultSet rs = stmt.executeQuery("select licenseNumber from doctors");

                    String switchS = init(rs);
                    switch (switchS) {
                        case "add":
                            String sqlInsert = "INSERT INTO Med.Doctors VALUES(null,?,?,?,?,null,null,null,null,null,false)";
                            PreparedStatement pst = con.prepareStatement(sqlInsert);
                            String TmpNumberLicence;
                            System.out.println("Enter fisrt name");
                            pst.setString(1, sc.nextLine());
                            System.out.println("Enter last name");
                            pst.setString(2, sc.nextLine());
                            System.out.println("Enter degree");
                            pst.setString(3, sc.nextLine());
                            System.out.println("Enter license number");
                            TmpNumberLicence = sc.nextLine();
                            if (!set.contains(TmpNumberLicence)) {
                                pst.setString(4, TmpNumberLicence);
                                set.add(TmpNumberLicence);
                                pst.executeUpdate();
                                System.out.println("The Doctor was added.");

                            } else {
                                System.out.println("The given number already exists in the database");
                            }
                            break;


                        case "edit":
                            String namecolumn;

                            System.out.println("Enter the license number of the doctor whose data you want to edit.");
                            TmpNumberLicence = sc.nextLine();
                            if (set.contains(TmpNumberLicence)) {

                                System.out.println("Enter which columns you want to edit.");
                                namecolumn = sc.nextLine();
                                String UpdateSQL = "UPDATE med.Doctors SET " + namecolumn + "=? WHERE LicenseNumber = ?";

                                pst = con.prepareStatement(UpdateSQL);
                                pst.setInt(2, Integer.parseInt(TmpNumberLicence));

                                System.out.println("What value do we insert?");
                                pst.setString(1, sc.nextLine());
                                pst.executeUpdate();
                                System.out.println("Update successful.");

                            }//if
                            else {
                                System.out.println("The license number provided does not exist in the database.");
                            }
                            break;


                        case "delete":
                            String sqlDelete = "DELETE FROM med.doctors where licensenumber=?";
                            pst = con.prepareStatement(sqlDelete);
                            System.out.println("Please enter the doctor's license number to delete.");
                            TmpNumberLicence = sc.nextLine();
                            pst.setString(1, TmpNumberLicence);
                            pst.executeUpdate();
                            System.out.println("Doctor delete");
                            set.remove(TmpNumberLicence);
                            break;
                    }//switch action
                }
                break;
                case "hospitals": {

                    ResultSet rs = stmt.executeQuery("select name from med.hospitals");
                    //Set<String> set = new HashSet<>();
                    String name;

                    String switchS = init(rs);

                    switch (switchS) {
                        case "add": {
                            String country;
                            String sqlInsert = "INSERT INTO Med.Hospitals VALUES(null,?,?,null ,null,null,null,null,null,null,false)";
                            PreparedStatement pst = con.prepareStatement(sqlInsert);
                            System.out.println("Enter name");
                            name = sc.nextLine();
                            System.out.println("Enter country");
                            country = sc.nextLine();
                            if (!set.contains(name)) {
                                pst.setString(1, name);
                                set.add(name);
                                pst.setString(2, country);
                                pst.executeUpdate();
                                System.out.println("Hospital was added");
                            } else {
                                System.out.println("The given number already exists in the database");
                            }

                        }//dodawanie szpitala
                        break;

                        case "edit": {
                            String namecolumn;

                            System.out.println("Enter the name of the hospital whose data you want to update.");
                            name = sc.nextLine();
                            if (set.contains(name)) {

                                System.out.println("Enter which columns you want to update.");
                                namecolumn = sc.nextLine();
                                String UpdateSQL = "UPDATE med.hospitals SET " + namecolumn + "=? WHERE name = ?";

                                PreparedStatement pst = con.prepareStatement(UpdateSQL);
                                pst.setString(2, name);

                                System.out.println("what value do we insert?");
                                pst.setString(1, sc.nextLine());
                                pst.executeUpdate();
                                System.out.println("Update successful.");

                            }//if
                            else {
                                System.out.println("The given name does not exist in the database.");
                            }
                        }//switch do edycji
                        break;
                        case "delete": {
                            String sqlDelete = "DELETE FROM med.hospitals where name=?";
                            PreparedStatement pst = con.prepareStatement(sqlDelete);
                            System.out.println("Please give the name of the hospital to be removed.");
                            name = sc.nextLine();
                            pst.setString(1, name);
                            pst.executeUpdate();
                            System.out.println("Hospital was added.");
                            set.remove(name);
                            break;
                        }//switch do usuwania


                    }//switch wyboru dzialania na szpitalu

                }
                break;
                case "employ": {

                    System.out.println("Enter the name of the hospital where the doctor will be employed.");
                    String nameOfHospital = sc.nextLine();
                    System.out.println("Enter the license number of the doctor we employ.");
                    int licensenumber = Integer.parseInt(sc.nextLine());

                    System.out.println("Enter the date of contract start in format yyyy-mm-dd");
                    String StartDate = sc.nextLine();
                    Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(StartDate);
                    System.out.println("Enter the date of contract end in format yyyy-mm-dd");
                    String EndDate = sc.nextLine();
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(EndDate);
                    if (date2.after(date1)) {


                        String sql = "INSERT INTO med.Hospitaldoctors " +
                                "VALUES(null,(select id from med.doctors where licensenumber=?)," +
                                "(select id from med.hospitals where name=?),?,?,null,null,null)";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setInt(1, licensenumber);
                        pst.setString(2, nameOfHospital);
                        pst.setString(3, StartDate);
                        pst.setString(4, EndDate);
                        pst.executeUpdate();
                        System.out.println("Finished");
                    }//if
                    else
                    {
                        System.out.println("The date of the end of the contract must be later than the start date.");
                    }
                }//case zatrudnienia

                    break;
            }//switch table

            con.close();

        }//try
        catch(Exception e)
        {
            System.out.println(e);
        }//catch
    }//main
}//A