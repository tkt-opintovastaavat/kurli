import java.io.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class KurliIndex extends HttpServlet { 

    final String dbDriver="oracle.jdbc.OracleDriver";


    final String dbServer= "jdbc:oracle:thin:@bodbacka.cs.helsinki.fi:1521:test";
    // nothing to replace here

    final String dbUser= "tikas";     // replace with your db user account
    final String dbPassword ="tikas";     // replace with your password 
    // A better solution would be to
    // give these as configuration 
    // parameters

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
   
        res.setContentType("application/json");
        ServletOutputStream out= res.getOutputStream();        
        out.println("{foo: 'bar'}");
       
    } 

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    }

}
    
