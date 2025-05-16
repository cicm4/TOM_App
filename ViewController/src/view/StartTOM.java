package view;

import java.io.IOException;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import oracle.adf.share.security.AuthenticationService;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

import java.sql.*;
import java.sql.SQLException;

import javax.faces.application.Application;

import javax.faces.application.NavigationHandler;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.binding.BindingContainer;

import oracle.jbo.RowIterator;


public class StartTOM {
    private String email;
    private String password;
    private RichInputText inputTextEmail;
    private RichInputText inputTextPassword;
    private RichOutputText onLoadInicio;
    public Connection conn;
    Statement stmt;
    PreparedStatement pstmt;
    private RichTable tasksTable;
    
    private Integer teamID;
    private Integer studentID;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setInputTextEmail(RichInputText inputTextEmail) {
        this.inputTextEmail = inputTextEmail;
    }

    public RichInputText getInputTextEmail() {
        return inputTextEmail;
    }

    public void setInputTextPassword(RichInputText inputTextPassword) {
        this.inputTextPassword = inputTextPassword;
    }

    public RichInputText getInputTextPassword() {
        return inputTextPassword;
    }

    public String doLogin() throws SQLException {

        String sql;
        Integer r;
        FacesContext ctx = FacesContext.getCurrentInstance();
        System.out.println("The user is: " + email);
        System.out.println("The password is: " + password);

        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        conn = DriverManager.getConnection("jdbc:oracle:thin:@181.205.223.86:1521:dbpaco", "tom_user", "C1m3l405");
        conn.setAutoCommit(true);

        stmt = conn.createStatement();
        sql =
            "Select student_id, student_name, student_email, auth_level, student_password, team_id from users where student_email = '" +
            email + "' and student_password = '" + password + "'";

        ResultSet rset = stmt.executeQuery(sql);
        rset.next();
        r = rset.getRow();
        if (r == 1) {
            System.out.println("The access level is: " + rset.getInt(4));
            teamID = rset.getInt(6);
            studentID = rset.getInt(1);
        } else {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong user email and password",
                                 "The username or password are incorrect");
            ctx.addMessage(null, msg);
            System.out.println("Authentification error");
        }

        FacesContext fctx = FacesContext.getCurrentInstance();
        Application application = fctx.getApplication();
        NavigationHandler navHandler = application.getNavigationHandler();

        if (rset.getInt(4) == 1) {
            navHandler.handleNavigation(fctx, null, "toAdmin");
        } else if (rset.getInt(4) == 2) {
            navHandler.handleNavigation(fctx, null, "toTL");
        } else
            navHandler.handleNavigation(fctx, null, "toStudent");
        return "A";
    }


    public void setTasksTable(RichTable tasksTable) {
        this.tasksTable = tasksTable;
    }

    public RichTable getTasksTable() {
        return tasksTable;
    }

    public void buttonStartPress(ActionEvent actionEvent) {
        // Add event code here...
    }
   
   public void LoadTeamLeader(ClientEvent clientEvent) {
        System.out.println("Team: " + getTeamID());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding TeamIter = (DCIteratorBinding) bindings.get("TeamsViewLv1Iterator");
        TeamIter.findRowsByAttributeValue("TeamId", true, getTeamID());
        String vs = TeamIter.getCurrentRow()
                               .getAttribute("TeamId")
                               .toString();
        System.out.println("ok, find team: " + vs);
    }
   
    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
   
   public Integer getTeamID(){
       return teamID;
   }
   
    public Integer getStudentID(){
        return studentID;
    }

    public void LoadStudent(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding TeamIter = (DCIteratorBinding) bindings.get("TeamsViewLv1Iterator");
        TeamIter.findRowsByAttributeValue("TeamId", true, getTeamID());
        String vs = TeamIter.getCurrentRow()
                            .getAttribute("TeamId")
                            .toString();
        System.out.println("ok, find team: " + vs);

        DCIteratorBinding StudentIter = (DCIteratorBinding) bindings.get("UsersViewLv1Iterator");
        StudentIter.findRowsByAttributeValue("StudentId", true, getStudentID());
        String vs1 = StudentIter.getCurrentRow()
                                .getAttribute("StudentId")
                                .toString();
        System.out.println("StudentId: " + vs1);
    }
}
