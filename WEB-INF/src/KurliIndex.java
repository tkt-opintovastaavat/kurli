import java.io.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.ArrayList;
import java.util.Iterator;

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
      ArrayList<String> results = new ArrayList<String>();
      Connection con=null;
        con = createDbConnection(dbDriver,dbServer,dbUser,dbPassword,out);
        if (con == null)
            return;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM opintojakso WHERE tyyppi = 'K' AND opintopisteet != 0");
            while(rs.next()) {
                results.add(fetchCourseData(rs));
            }
        } catch (SQLException e) {}
        printResults(out, results);
    }

    private void printResults(ServletOutputStream out, ArrayList<String> results) throws IOException {
        out.println("[");
        Iterator iter = results.iterator();
        if (iter.hasNext()) {
            out.print((String) iter.next());
            while(iter.hasNext()) {
                out.println(",");
                out.print((String) iter.next());
            }
        }
        out.println("]");
    }

    private String fetchCourseData(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"code\": \"" +rs.getString("kurssikoodi") + "\",");
        sb.append("\"name\": {");
        sb.append("\"fi\": \"" +rs.getString("nimi_suomi") + "\",");
        sb.append("\"en\": \"" +rs.getString("nimi_englanti") + "\",");
        sb.append("se: \"" +rs.getString("nimi_ruotsi") + "\"");
        sb.append("},");
        sb.append("credits: \"" + rs.getString("opintopisteet") + "\",");
        sb.append("level: \"" + rs.getString("taso") + "\"");
        sb.append("},");
        return sb.toString();
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
