import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class PgTesti extends HttpServlet { 

    //   to use PostgeSQL: 
       final String dbDriver="org.postgresql.Driver";                  
       final String dbServer ="jdbc:postgresql://localhost/user";
    //    replace the above with your postgreSQL database id
    
    // to use Oracle 9 in bodbacka:
    //final String dbDriver="oracle.jdbc.OracleDriver";
    //final String dbServer= "jdbc:oracle:thin:@bodbacka.cs.helsinki.fi:1521:test";
    // nothing to replace
    // classpath must contain /opt/jdbc/oracle/ojdbc14.jar
    
    final String dbUser= "eitoimi";        // replace with your db user account
    final String dbPassword ="salasana";   // replace with your db password 
    
    public void service(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {
	ServletOutputStream out;  
	res.setContentType("text/html");
	out= res.getOutputStream();        
	out.println("<html><head><title>PgTesti</title></head>");
	out.println("<body bgcolor=white><h1>PgTesti</h1>");

	Connection con=null;	
        con= createDbConnection(dbDriver,dbServer,dbUser,dbPassword,out);
	if (con==null) {
	    out.println("</body></html>");
	    return;
	}	
	// connection established
	
	Statement stmt = null;
	ResultSet rs = null; 
	try { 
	    stmt = con.createStatement(); 
	    rs = stmt.executeQuery("select s1, s2, s3 from testi");
	    out.println("<table>");
	    while(rs.next()) { 
                out.println("<tr><td> s1: <b>"+rs.getInt("s1")+"</b></td></tr>");
                out.println("<tr><td> s2:   <b>"+rs.getString("s2")+"</b></td></tr>");
                out.println("<tr><td> s3:  <b>"+rs.getString("s3")+"</b></td></tr>"); 
                out.println("<p>");
	    }
	    out.println("</table>");
	} catch (SQLException ee) {
	    out.println("Tietokantavirhe "+ee.getMessage());
	} finally {
	    try {
                if (rs!=null) rs.close(); 
                if (stmt!=null) stmt.close(); 
                con.close();
	    } catch(SQLException e) { 
                out.println("An SQL Exception was thrown."); 
	    }
	}  
	out.println("");
	out.println("</body></html>");
    } 

    private Connection createDbConnection(
	String dbDriver, String dbServer, String dbUser, String dbPassword, 
	ServletOutputStream out) throws IOException {
	
	// establish a database connection
	try{ 
	    Class.forName(dbDriver);               // load driver
	} catch (ClassNotFoundException e) { 
	    out.println("Couldn't find driver "+dbDriver);
	    return null; 
	}
	Connection con=null;
	try {
	    con = DriverManager.getConnection(dbServer,dbUser,dbPassword); 
	} catch (SQLException se) {
	    out.println("Couldn\'t get connection to "+dbServer+ " for "+ dbUser+"<br>");
	    out.println(se.getMessage());          
	}
	return con;
    }
    
    private void closeConnection(Connection con) {
	// close database connection
	try {
	    con.close();
	}catch (SQLException e) {}
    }
    
}
