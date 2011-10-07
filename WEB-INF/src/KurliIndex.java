import java.io.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class KurliIndex extends HttpServlet {

    final String dbDriver="oracle.jdbc.OracleDriver";
    final String dbServer= "jdbc:oracle:thin:@bodbacka.cs.helsinki.fi:1521:test";
    final String dbUser= "USERNAME";
    final String dbPassword ="PASSWORD";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        displayData(res.getOutputStream());

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    }

    private void displayData(ServletOutputStream out) throws IOException {
        out.println("[");
        Connection con=null;
        con = createDbConnection(dbDriver,dbServer,dbUser,dbPassword,out);
        if (con == null)
            return;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM opintojakso WHERE tyyppi = 'K' AND opintopisteet != 0");
            while(rs.next()) {
                out.print("{");
                out.print("code: '" +rs.getString("kurssikoodi") + "',");
                out.print("name: {");
                out.print("fi: '" +rs.getString("nimi_suomi") + "',");
                out.print("en: '" +rs.getString("nimi_englanti") + "',");
                out.print("se: '" +rs.getString("nimi_ruotsi") + "',");
                out.print("},");
                out.print("credits: '" + rs.getString("opintopisteet") + "',");
                out.print("level: '" + rs.getString("taso") + "'");
                out.println("},");
            }
        } catch (SQLException e) {}
        out.println("]");
    }

    private Connection createDbConnection(String dbDriver, String dbServer, String dbUser, String dbPassword, ServletOutputStream out) throws IOException {

        // establish a database connection
        try{
            Class.forName(dbDriver);               // load driver
        } catch (ClassNotFoundException e) {
            return null;
        }
        Connection con=null;
        try {
            con = DriverManager.getConnection(dbServer,dbUser,dbPassword);
        } catch (SQLException se) {}
        return con;
    }

}
