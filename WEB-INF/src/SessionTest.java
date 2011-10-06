import java.io.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SessionTest extends HttpServlet { 

//   final String dbDriver="org.postgresql.Driver";                
 final String dbDriver="oracle.jdbc.OracleDriver";

 final String dbServer= "jdbc:oracle:thin:@bodbacka.cs.helsinki.fi:1521:test";
// nothing to replace here

   final String dbUser= "tikas";         // replace with your db user account
   final String dbPassword ="tikas";     // replace with your password 
                                         // A better solution would be to
                                         // give these as configuration 
                                         // parameters

public void doGet(HttpServletRequest req, HttpServletResponse res)
   throws ServletException, IOException {

/*******
* session stuff
*
*/

  int monesko=0;
  HttpSession session = req.getSession(true);
  if (session.isNew()) {
      session.setAttribute("monesko","1");
      session.setMaxInactiveInterval(300);
  } else {
     String m= (String) session.getAttribute("monesko");
     try {
        monesko=Integer.parseInt(m);
        m= Integer.toString(monesko+1);
        session.setAttribute("monesko",m);
     } catch (Exception e) {
       monesko=9999;
     }
  }


   
   res.setContentType("text/html");




   ServletOutputStream out= res.getOutputStream();        
   out.println("<html><head><title>SessionTest</title>"+
       "<link rel=\"stylesheet\" type=\"text/css\" href=\"../tyyli.css\">"+
       "</head>");
   out.println("<body bgcolor=\"white\">");
   boolean ERRORS=false;
   
   Connection con=null;
   con= createDbConnection(dbDriver,dbServer,dbUser,dbPassword,out);
   if (con==null) {
       out.println("</body></html>");
       return;
   }

   // connection established

   Statement stmt = null;
   Statement stmt2 =null;
   ResultSet rs = null;
   ResultSet vs =null;
   ResultSet ders =null;
   StringBuffer valStr= new StringBuffer("insert into emp values (");
   // we build up the insertion statement in valStr
   String eQuery = "select EMPNO, ENAME, JOB, "+
                       "MGR, to_char(HIREDATE,'DD.MM.YYYY') HIREDATE, SAL,  COMM, DEPTNO "+
                       "From EMP "+
                       "ORDER BY ENAME ";
  
   String depts = "select deptno, dname from dept order by deptno";
   String toDayQuery ="select to_char(sysdate,'DD.MM.YYYY') TODAY from sys.dual";
   
   // processing of the potential new employee

   String empNo=null;
   String eName=null; 
   String job= null;
   String mgr = null;
   String hDate = null;
   String salary = null;
   String comm = null;
   String deptno = null;
   
   String ERRORCOLOR="bgcolor=\"#FF9999\"";
   String OKC="bgcolor=\"white\"";
   String HDC="bgcolor=\"#99CCFF\"";
   String empnoBg=OKC;
   String enameBg=OKC;
   String jobBg=OKC;
   String mgrBg=OKC;
   String hiredateBg=OKC;
   String salBg=OKC;
   String commBg=OKC;
   String deptnoBg=OKC;
   String failMessage= null;

   String subbi= req.getParameter("SUBMIT");
   if (subbi != null) {
      empNo= req.getParameter("EMPNO");
      eName = req.getParameter("ENAME");
      job= req.getParameter("JOB");
      mgr = req.getParameter("MGR");
      hDate= req.getParameter("HIREDATE");
      salary = req.getParameter("SAL");
      comm = req.getParameter("COMM");
      deptno = req.getParameter("DEPTNO");
    
      if (notnull(empNo) && isInt(empNo)) {
         valStr.append(empNo);
         valStr.append(",");
      } else {
         empNo=null;
         empnoBg=ERRORCOLOR;
         ERRORS=true;
      }
      if (notnull(eName)) {
	  if (eName.length()>=10) {
              enameBg=ERRORCOLOR;
             ERRORS=true;
          } else { 
             valStr.append("'");
             valStr.append(eName);
             valStr.append("',");
          }
      } else {
        eName=null;
        enameBg=ERRORCOLOR;
        ERRORS=true;
      }
      if (notnull(job)) {
         valStr.append("'");
         valStr.append(job);
         valStr.append("',");
      } else {
         valStr.append("NULL,");
         job=null;
      }
      if (notnull(mgr)) {
         if (isInt(mgr)) {
            valStr.append(mgr);
            valStr.append(',');
         } else {
           mgrBg=ERRORCOLOR;
           ERRORS=true;
         }
      } else {
        valStr.append("NULL,");
        mgr=null;
      }      
      if (notnull(hDate)) {
         if  (isDate(hDate)) {
            valStr.append("to_date('");
            valStr.append(hDate);
            valStr.append("','DD.MM.YYYY'),");
         } else {
           hiredateBg=ERRORCOLOR;
           ERRORS=true;
         }
      } else {
	 valStr.append("NULL,");
         hDate=null;
      }
      if (notnull(salary)) {
         if (isMoney(salary)) {
            valStr.append(salary);
            valStr.append(',');
         } else {
           salBg=ERRORCOLOR;
           ERRORS=true;
         }
      } else {
        valStr.append("NULL,");
        salary=null;
      }
      if (notnull(comm)) {
         if (isMoney(comm)) {
            valStr.append(comm);
            valStr.append(',');
         } else {
           commBg=ERRORCOLOR;
           ERRORS=true;
         }
      } else {
        valStr.append("NULL,");
        comm=null;
      }
      if (notnull(deptno)) {
         if (isInt(deptno)) {
	     if (deptno.equals("0")) 
		 valStr.append("NULL)");
	     else {
                valStr.append(deptno);
                valStr.append(")");
             }
         } else {
            empnoBg=ERRORCOLOR;
            ERRORS=true;
         }
      } else {
         valStr.append("NULL)");
         deptno=null;
      }
      if (!ERRORS) {
         try {
             stmt2 = con.createStatement();
             stmt2.executeUpdate("alter session set nls_language='american'");
            int rc= stmt2.executeUpdate(valStr.toString());
         } catch (SQLException ex1) {
             failMessage= "<b>Insertion failed: "+ ex1.getMessage()+"</b>";
             ERRORS=true;
         } finally {
             try {
                if (stmt2!=null) stmt2.close();
             } catch(SQLException e) {
                out.println("An SQL Exception was thrown when closing statement.");
             }
         }
      } else {
        failMessage= "Insertion failed because of missing or faulty data.";
        ERRORS=true;
      }
  }
 

  out.println( "<table border=1 width=\"85%\""+ HDC+ ">"+
       "<tr><td width=\"90\"><img src=\"../testing.gif\"><td>"+
       "<td valign=\top\"><h1>Servlet Example</h1><td></tr>"+
       "</table><p>");

  // list the employees
  out.println("<H2>Current employees ("+monesko+")</H2>");
  out.println("<table border=1>"+
       "<tr>"+
       "<th "+HDC+">NAME</th><th "+HDC+">EMPNO</TH><TH "+HDC+">JOB</th>"+
       "<TH "+HDC+">MGR</TH><TH "+HDC+">HIREDATE</TH><TH "+HDC+">SAL</TH><TH "+HDC+
       ">COMM</TH><TH "+HDC+">DEPTNO"+
       "</TH></TR>");
 
   int bigNo=0;  // the biggest empno
   int curNo=0;
   try { 
        stmt = con.createStatement(); 
        rs = stmt.executeQuery(eQuery);  
        // list the employees
        while(rs.next()) { 
           out.println("<TR>");
           out.println("<TD>"+rs.getString("ENAME")+"</TD>");
           curNo= rs.getInt("EMPNO");
           if (curNo>bigNo)
              bigNo=curNo;
           out.println("<TD>"+curNo+"</TD>"); 
           out.println("<TD>"+rs.getString("JOB")+"</TD>"); 
           out.println("<TD>"+rs.getString("MGR")+"</TD>"); 
           out.println("<TD>"+rs.getString("HIREDATE")+"</TD>"); 
           out.println("<TD>"+rs.getString("SAL")+"</TD>"); 
           out.println("<TD>"+rs.getString("COMM")+"</TD>"); 
           out.println("<TD>"+rs.getString("DEPTNO")+"</TD>"); 
           out.println("</TR>");
        }
       out.println("</table><p><hr>");
       // employees listed

       // get the current date - there are surely  better ways to do this
       // but this is an example of secondary database queries
       vs = stmt.executeQuery(toDayQuery);
       String toDay= null;
       if (vs.next())
          toDay= vs.getString("TODAY");


       // build the form to insert a new employee record or 
       // to correct a faulty insertion record
       if (ERRORS) {
	   out.println("<font color=\"red\">"+failMessage+"</font><hr>");
       } else {
	   if  (subbi!=null) {
               out.println("<font color=\"green\"><b>Insertion succeeded!</b></font><hr>");
               empNo=null;
               eName=null;
               job= null;
               mgr = null;
               hDate = null;
               salary = null;
               comm = null;
               deptno = null;
           }
       } 
       out.println(
           "<FORM ACTION=\"SessionTest\" Method=\"GET\">"+
           "<table border=1>"+
           "<tr><TD COLSPAN=2 align=\"CENTER\" "+ 
           (ERRORS?ERRORCOLOR:OKC)+"><b>New employee</b></TD></TR>"+
           "<tr><TH>EmpNO</TH><TD>");
       out.println(bigNo+1);
       out.print("<INPUT TYPE=\"HIDDEN\" NAME=\"EMPNO\" VALUE=\"");
       out.print(bigNo+1);
       out.println("\"></TD></TR>");
       out.println("<TR><TH>Name</TH>");
       out.println("<TD "+enameBg+">");
       out.println("<INPUT TYPE=\"TEXT\" NAME=\"ENAME\" SIZE=\"30\" VALUE=\""+
           (eName==null?"":eName)+ "\">"+  
           "</TD<</TR>");
       out.println("<TR><TH>JOB</TH>");
       out.println("<TD "+jobBg+">");
       out.println("<SELECT NAME=\"Job\" SIZE=\"1\">"); 
       out.println("<OPTION"+
          (job!=null && job.equals("UNKNOWN")?" SELECTED":"")+
          ">UNKNOWN</OPTION>");      
       out.println("<OPTION" +
          (job!=null && job.equals("ANALYST")?" SELECTED":"")+
          ">ANALYST</OPTION>");
       out.println("<OPTION" +
          (job!=null && job.equals("CLERK")?" SELECTED":"")+ 
          ">CLERK</OPTION>");
       out.println("<OPTION" +
           (job!=null && job.equals("MANAGER")?" SELECTED":"")+ 
          ">MANAGER</OPTION>");
       out.println("<OPTION"+
          (job!=null && job.equals("PRESIDENT")?" SELECTED":"")+ 
          ">PRESIDENT</OPTION>");
       out.println("<OPTION"+
          (job!=null && job.equals("SALESMAN")?" SELECTED":"")+
          ">SALESMAN</OPTION>");

       out.println("</select></TD></TR>");
       out.println("<TR><TH>Manager</TH>");
       out.println("<TD "+mgrBg+">");
       out.println("<INPUT TYPE=\"TEXT\" NAME=\"MGR\" SIZE=\"4\" VALUE=\""+ 
          (mgr!=null?mgr:"")+
          "\"> </TD></TR>");
       out.println("<TR><TH>Date hired</TH>");
       out.println(" <TD "+hiredateBg+">");
       out.println("<INPUT TYPE=\"TEXT\" NAME=\"HIREDATE\" SIZE=\"10\" "+ 
              "VALUE=\""+
              (hDate!=null?hDate:toDay)+
              "\"></TD</TR>"); 
       out.println("<TR><TH>Salary</TH>");
       out.println("<TD "+salBg+">");
       out.println("<INPUT TYPE=\"TEXT\" NAME=\"SAL\" SIZE=\"10\""+
           "VALUE=\""+  
           (salary!=null?salary:"")+
           "\"></TD></TR>");
       out.println("<TR><TH>Commission</TH>");
       out.println("<TD "+commBg+">");
       out.println("<INPUT TYPE=\"TEXT\" NAME=\"COMM\" SIZE=\"10\""+ 
           "VALUE=\""+
           (comm!=null?comm:"")+
           "\"></TD></TR>");
       out.println("<TR><TH>Department</TH>");
       out.println("<TD "+deptnoBg+">");
       out.println("<select name=\"DEPTNO\" SIZE=1>");
       out.println("<option value=\"0\" "+
           (deptno!=null && deptno.equals("0")?"selected":"")+
           ">No Dept</option>");
     
       ders= stmt.executeQuery(depts);
       while (ders.next()) {
           out.println("<option value=\""+
               ders.getString("DEPTNO")+"\""+
               (deptno!=null && deptno.equals(ders.getString("DEPTNO"))?" selected":"")+
               ">"+
              ders.getString("DNAME")+"</option>"); 
       }
       out.println("</TD></TR><TR><TD colspan=2> "+
              "<INPUT TYPE=\"SUBMIT\" VALUE=\"Insert\" "+ 
              "NAME=\"SUBMIT\"> "+
              "<Input type=\"RESET\" Value=\"Clear Form\"> "+
              "</TD></TR></TABLE>");
         
       out.println("</body></html>");
     } catch (SQLException ee) {
             out.println("Tietokantavirhe "+ee.getMessage());
     } finally {
             try {
                if (rs!=null) rs.close();
                if (vs!=null) vs.close();
                if (ders!=null) ders.close();
                if (stmt!=null) stmt.close();
                con.close();
             } catch(SQLException e) {
                out.println("An SQL Exception was thrown.");
             }
     }

       
   } 

public void doPost(HttpServletRequest req, HttpServletResponse res)
   throws ServletException, IOException {
    // do the same as doget
   doGet(req,res);
}

private boolean notnull(String some) {
    // check if null or empty string
   if (some==null || some.length()==0) 
      return false;
   else
      return true;
} 

private boolean isInt(String some) {
    // check if integer
   try {
     int abc= Integer.parseInt(some);
     return true;
   } catch (Exception n) {
     return false;
   }
}

private boolean isMoney (String some) {
    // check that  at most 2 decimals
   try {
      float abc = Float.parseFloat(some);
      if (some.indexOf(".")>0 && some.indexOf(".")<some.length()-3)
          return false;
      else
          return true;
   } catch (Exception e) {
     return false;
   }
}

private boolean isDate(String some) {
    // check date correctness 

  SimpleDateFormat df= new SimpleDateFormat("dd.MM.yyyy");
  df.setLenient(false);
  try {
     java.util.Date t= df.parse(some);
     return true;   
  } catch (Exception e) { 
     return false;
  }
}


// connection management
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
