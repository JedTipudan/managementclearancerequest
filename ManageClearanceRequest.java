/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clearancems;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Angkool
 */
public class ManageClearanceRequest extends javax.swing.JFrame {
    private int userID;
private String fullname;
private String type;
private int clearingofficeID; 
 

public ManageClearanceRequest(int userID, String fullname, String type, int clearingofficeID) {
        initComponents();
        setLocationRelativeTo(null);
        this.userID=userID;
        this.fullname=fullname;
        this.type=type;
        this.clearingofficeID=clearingofficeID;
        
        username.setText(fullname);
        
        GetAYSem();
        GetCourse();
        GetPendingClearance("", "", "", "");
        GetClearedStudents("", "", "", "");
    }


//GET ACADEMIC YEAR     
public void GetAYSem(){
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT acadyear FROM clearancerequest GROUP BY acadyear;";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            aycmbbx.removeAllItems(); 
            aycmbbx.addItem("All");
                while(rs.next()){
                    String item=rs.getString("acadyear");
                    aycmbbx.addItem(item);
                }
        }catch(SQLException ae){
        }
    }
    
//GET COURSES    
    public void GetCourse(){
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT course FROM student GROUP BY course ORDER BY course ASC;";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
                coursecmbbx.removeAllItems(); 
                coursecmbbx.addItem("All");
                while(rs.next()){
                    String item=rs.getString("course");
                    coursecmbbx.addItem(item);
            }
        }catch(SQLException ae){
        }
    }

//GET PENDING REQUESTS        
public void GetPendingClearance(String ay, String sem, String course, String search){
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT t2.clearancestatusID, t1.clearancerequestID, t4.idno, t4.fullname, t4.course, CONCAT(t1.acadyear, '-', t1.semester) AS aysem, "
                    + "CONCAT((SELECT COUNT(*) FROM clearancestatus WHERE status='Cleared' AND clearancerequestID=t1.clearancerequestID),'/',(SELECT COUNT(*) FROM clearingoffices)) AS progress,"
                    + "t1.dateRequested, t2.status, t2.remarks FROM clearancerequest AS t1 LEFT JOIN clearancestatus AS t2 ON t1.clearancerequestID=t2.clearancerequestID "
                    + "LEFT JOIN student AS t4 ON t4.studentID=t1.studentID WHERE t2.status='Pending' AND t2.clearingofficesID=? ";
            String condition="";
            if(search.equals("")){
                if(!ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()){
                    condition=" AND t1.acadyear=? AND t1.semester=? AND t4.course=?"; 
                }
                else if(ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()){
                    condition=" AND t1.semester=? AND t4.course=?"; 
                }
                else if(ay.isEmpty() && sem.isEmpty() && !course.isEmpty()){
                    condition=" AND t4.course=?"; 
                }
                else if(!ay.isEmpty() && !sem.isEmpty() && course.isEmpty()){
                    condition=" AND t1.acadyear=? AND t1.semester=?"; 
                }
                else if(!ay.isEmpty() && sem.isEmpty() && course.isEmpty()){
                    condition=" AND t1.acadyear=?"; 
                }
                else if(ay.isEmpty() && !sem.isEmpty() && course.isEmpty()){
                    condition=" AND t1.semester=?"; 
                }
                else if(!ay.isEmpty() && sem.isEmpty() && !course.isEmpty()){
                    condition=" AND t1.acadyear=? AND t4.course=?"; 
                }
            }
            else if(!search.equals("")){
                
                condition=" AND LOWER(CONCAT(t4.fullname, t4.idno)) LIKE ?";
            }
            query+=condition+" GROUP BY t1.clearancerequestID, t4.idno, t4.fullname, t1.acadyear, t1.semester, "
                    + "t1.dateRequested ORDER BY t1.clearancerequestID DESC;";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, clearingofficeID);
                if(search.equals("")){
                    if(!ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()){
                        pst.setString(2, ay);
                        pst.setString(3, sem);
                        pst.setString(4, course);
                    }
                    else if(ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()){
                        pst.setString(2, sem);
                        pst.setString(3, course);
                    }
                    else if(ay.isEmpty() && sem.isEmpty() && !course.isEmpty()){
                        pst.setString(2, course);
                    }
                    else if(!ay.isEmpty() && !sem.isEmpty() && course.isEmpty()){
                        pst.setString(2, ay);
                        pst.setString(3, sem);
                    }
                    else if(!ay.isEmpty() && sem.isEmpty() && course.isEmpty()){
                        pst.setString(2, ay);
                    }
                    else if(ay.isEmpty() && !sem.isEmpty() && course.isEmpty()){
                        pst.setString(2, sem);
                    }
                    else if(!ay.isEmpty() && sem.isEmpty() && !course.isEmpty()){
                        pst.setString(2, ay);
                        pst.setString(3, course);
                    }
                }
                else if(!search.equals("")){
                    pst.setString(2, "%"+search+"%");
                }
                ResultSet rs = pst.executeQuery();
                DefaultTableModel col=new DefaultTableModel();
                col.addColumn("ID Number");
                col.addColumn("Student");
                col.addColumn("Course");
                col.addColumn("AY & Semester");
                col.addColumn("Progress");
                col.addColumn("Date Requested");
                col.addColumn("Status");
                col.addColumn("Remarks");
                col.addColumn("clearancestatusID");
                Boolean found=false;
                    while(rs.next()){
                        found=true;
                        col.addRow(new Object[]{
                        rs.getString("idno"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("aysem"),
                        rs.getString("progress"),
                        rs.getString("dateRequested"),
                        rs.getString("status"),
                        rs.getString("remarks"),
                        rs.getString("clearancestatusID")
                    });
                }
                if (!found) {
                    // Add an empty row or message row
                    col.addRow(new Object[]{"No results found", "", "", "", "", ""});
                }
                pendingclearancetbl.setModel(col);
                pendingclearancetbl.getColumnModel().getColumn(8).setMinWidth(0);
                pendingclearancetbl.getColumnModel().getColumn(8).setMaxWidth(0);
                pendingclearancetbl.getColumnModel().getColumn(8).setWidth(0);
                rs.close();
            }
            conn.close();
        }catch(SQLException ae){
            JOptionPane.showMessageDialog(this,"Error!", "Information",JOptionPane.WARNING_MESSAGE);
        }
    }

//GET CLEARED STUDENTS    
public void GetClearedStudents(String ay, String sem, String course, String search) {
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT t2.dateCleared, t2.clearancestatusID, t1.clearancerequestID, t4.idno, t4.fullname, t4.course, "
                + "CONCAT(t1.acadyear, '-', t1.semester) AS aysem, t1.dateRequested, t2.status, t2.remarks "
                + "FROM clearancerequest AS t1 "
                + "LEFT JOIN clearancestatus AS t2 ON t1.clearancerequestID = t2.clearancerequestID "
                + "LEFT JOIN student AS t4 ON t4.studentID = t1.studentID "
                + "WHERE t2.status = 'Cleared' AND t2.clearingofficesID = ?";

        String condition = "";
        List<Object> params = new java.util.ArrayList<>();
        params.add(clearingofficeID);

        if (search == null || search.isEmpty()) {
            if (!ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()) {
                condition = " AND t1.acadyear = ? AND t1.semester = ? AND t4.course = ?";
                params.add(ay);
                params.add(sem);
                params.add(course);
            } else if (ay.isEmpty() && !sem.isEmpty() && !course.isEmpty()) {
                condition = " AND t1.semester = ? AND t4.course = ?";
                params.add(sem);
                params.add(course);
            } else if (ay.isEmpty() && sem.isEmpty() && !course.isEmpty()) {
                condition = " AND t4.course = ?";
                params.add(course);
            } else if (!ay.isEmpty() && !sem.isEmpty() && course.isEmpty()) {
                condition = " AND t1.acadyear = ? AND t1.semester = ?";
                params.add(ay);
                params.add(sem);
            } else if (!ay.isEmpty() && sem.isEmpty() && course.isEmpty()) {
                condition = " AND t1.acadyear = ?";
                params.add(ay);
            } else if (ay.isEmpty() && !sem.isEmpty() && course.isEmpty()) {
                condition = " AND t1.semester = ?";
                params.add(sem);
            } else if (!ay.isEmpty() && sem.isEmpty() && !course.isEmpty()) {
                condition = " AND t1.acadyear = ? AND t4.course = ?";
                params.add(ay);
                params.add(course);
            }
        } else {
            condition = " AND LOWER(CONCAT(t4.fullname, t4.idno)) LIKE ?";
            params.add("%" + search.toLowerCase() + "%");
        }

        query += condition
                + " GROUP BY t1.clearancerequestID, t4.idno, t4.fullname, t1.acadyear, t1.semester, t1.dateRequested "
                + "ORDER BY t1.clearancerequestID DESC";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            // bind parameters in order
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    pst.setInt(i + 1, (Integer) p);
                } else {
                    pst.setString(i + 1, p == null ? null : p.toString());
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                javax.swing.table.DefaultTableModel col = new javax.swing.table.DefaultTableModel();
                col.addColumn("ID Number");
                col.addColumn("Student");
                col.addColumn("Course");
                col.addColumn("AY & Semester");
                col.addColumn("Date Requested");
                col.addColumn("Progress"); // kept column (empty for now). Replace with real column if available.
                col.addColumn("Status");
                col.addColumn("Remarks");
                col.addColumn("clearancestatusID");
                col.addColumn("dateCleared");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    col.addRow(new Object[] {
                        rs.getString("idno"),
                        rs.getString("fullname"),
                        rs.getString("course"),
                        rs.getString("aysem"),
                        rs.getString("dateRequested"),
                        "", // progress - fill if you have a real column
                        rs.getString("status"),
                        rs.getString("remarks"),
                        rs.getString("clearancestatusID"),
                        rs.getString("dateCleared")
                    });
                }

                if (!found) {
                    // add a full-size empty/message row so column counts match
                    col.addRow(new Object[] {"No results found", "", "", "", "", "", "", "", "", ""});
                }

                clearedclearancetbl.setModel(col);
                // hide the clearancestatusID column
                clearedclearancetbl.getColumnModel().getColumn(8).setMinWidth(0);
                clearedclearancetbl.getColumnModel().getColumn(8).setMaxWidth(0);
                clearedclearancetbl.getColumnModel().getColumn(8).setWidth(0);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Information",
                javax.swing.JOptionPane.WARNING_MESSAGE);
    }
}


//CLEAR FILTER FIELDS
private void clearfields(){
        searchbox.setText("");
        aycmbbx.setSelectedIndex(0);
        firstsemrdobtn.setSelected(false);
        secondsemrdobtn.setSelected(false);
        coursecmbbx.setSelectedIndex(0);
        GetPendingClearance("", "", "", "");
        pendingclearancetbl.clearSelection();
        GetClearedStudents("", "", "", "");
        clearedclearancetbl.clearSelection();
        //CLEARED SELECTED
        clearselectedstudents();
    }

//CLEAR SELECTED STUDENT
    private void clearselectedstudents(){
        idnobox.setText("");
        fullnamebox.setText("");
        yearsembox.setText("");
        coursebox.setText("");
        remarksbox.setText("");
        pendingrdobtn.setSelected(false);
        clearedrdobtn.setSelected(false);
        clearancestatusIDlbl.setText("");
        pendingclearancetbl.clearSelection();
        clearedclearancetbl.clearSelection();
    }






    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        clearancestatusIDlbl = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        aycmbbx = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        firstsemrdobtn = new javax.swing.JRadioButton();
        secondsemrdobtn = new javax.swing.JRadioButton();
        Refresh = new javax.swing.JButton();
        Refresh1 = new javax.swing.JButton();
        searchbox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        coursecmbbx = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        idnobox = new javax.swing.JTextField();
        fullnamebox = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        yearsembox = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        coursebox = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        pendingrdobtn = new javax.swing.JRadioButton();
        clearedrdobtn = new javax.swing.JRadioButton();
        remarksbox = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        pendingclearancetbl = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        clearedclearancetbl = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1130, 720));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1120, 525));

        jPanel3.setBackground(new java.awt.Color(0, 0, 102));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("CLEARANCE MANAGEMENT SYSTEM");

        username.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setText("User Type");

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Sign Out");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        clearancestatusIDlbl.setForeground(new java.awt.Color(0, 0, 102));
        clearancestatusIDlbl.setText("jLabel2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(clearancestatusIDlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(115, 115, 115)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(clearancestatusIDlbl)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(19, 19, 19))))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Academic Year :");

        aycmbbx.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        aycmbbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        aycmbbx.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aycmbbxItemStateChanged(evt);
            }
        });
        aycmbbx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aycmbbxActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Semester :");

        firstsemrdobtn.setBackground(new java.awt.Color(255, 255, 255));
        firstsemrdobtn.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        firstsemrdobtn.setText("First Sem");
        firstsemrdobtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                firstsemrdobtnMouseClicked(evt);
            }
        });
        firstsemrdobtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstsemrdobtnActionPerformed(evt);
            }
        });

        secondsemrdobtn.setBackground(new java.awt.Color(255, 255, 255));
        secondsemrdobtn.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        secondsemrdobtn.setText("Second Sem");
        secondsemrdobtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                secondsemrdobtnMouseClicked(evt);
            }
        });
        secondsemrdobtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondsemrdobtnActionPerformed(evt);
            }
        });

        Refresh.setText("Refresh");
        Refresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RefreshMouseClicked(evt);
            }
        });
        Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshActionPerformed(evt);
            }
        });

        Refresh1.setText("Enter");
        Refresh1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Refresh1MouseClicked(evt);
            }
        });
        Refresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Refresh1ActionPerformed(evt);
            }
        });

        searchbox.setText("Search Bar");

        jLabel5.setFont(new java.awt.Font("Poppins SemiBold", 1, 12)); // NOI18N
        jLabel5.setText("Search :");

        coursecmbbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        coursecmbbx.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coursecmbbxMouseClicked(evt);
            }
        });
        coursecmbbx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coursecmbbxActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Course:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aycmbbx, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstsemrdobtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secondsemrdobtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coursecmbbx, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchbox, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Refresh1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Refresh)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aycmbbx, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstsemrdobtn)
                    .addComponent(secondsemrdobtn)
                    .addComponent(Refresh)
                    .addComponent(Refresh1)
                    .addComponent(searchbox, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coursecmbbx, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Student Information :");

        jLabel9.setText("ID Number :");

        idnobox.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        idnobox.setEnabled(false);
        idnobox.setRequestFocusEnabled(false);
        idnobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idnoboxActionPerformed(evt);
            }
        });

        fullnamebox.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        fullnamebox.setEnabled(false);
        fullnamebox.setRequestFocusEnabled(false);
        fullnamebox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullnameboxActionPerformed(evt);
            }
        });

        jLabel10.setText("Full Name:");

        yearsembox.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        yearsembox.setEnabled(false);
        yearsembox.setRequestFocusEnabled(false);
        yearsembox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearsemboxActionPerformed(evt);
            }
        });

        jLabel11.setText("Academic Year & Semester:");

        coursebox.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        coursebox.setEnabled(false);
        coursebox.setRequestFocusEnabled(false);
        coursebox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                courseboxActionPerformed(evt);
            }
        });

        jLabel13.setText("Course");

        pendingrdobtn.setBackground(new java.awt.Color(255, 255, 255));
        pendingrdobtn.setText("Pending");
        pendingrdobtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pendingrdobtnMouseClicked(evt);
            }
        });
        pendingrdobtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pendingrdobtnActionPerformed(evt);
            }
        });

        clearedrdobtn.setBackground(new java.awt.Color(255, 255, 255));
        clearedrdobtn.setText("Cleared");
        clearedrdobtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearedrdobtnMouseClicked(evt);
            }
        });
        clearedrdobtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearedrdobtnActionPerformed(evt);
            }
        });

        remarksbox.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        remarksbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remarksboxActionPerformed(evt);
            }
        });

        jLabel14.setText("Remarks: ");

        jButton2.setText("Save");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        jButton3.setText("Clear");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(remarksbox, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(coursebox, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearsembox, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fullnamebox, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idnobox, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(pendingrdobtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearedrdobtn)))
                        .addGap(27, 27, 27))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idnobox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullnamebox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(yearsembox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coursebox, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pendingrdobtn)
                    .addComponent(clearedrdobtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remarksbox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(54, Short.MAX_VALUE))
        );

        pendingclearancetbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        pendingclearancetbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pendingclearancetblMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(pendingclearancetbl);

        jTabbedPane1.addTab("Pending Request", jScrollPane2);

        clearedclearancetbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        clearedclearancetbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearedclearancetblMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(clearedclearancetbl);

        jTabbedPane1.addTab("Cleared Request", jScrollPane3);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(23, 23, 23))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1130, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void aycmbbxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aycmbbxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aycmbbxActionPerformed

    private void firstsemrdobtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstsemrdobtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstsemrdobtnActionPerformed

    private void secondsemrdobtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secondsemrdobtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_secondsemrdobtnActionPerformed

    private void RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RefreshActionPerformed

    private void Refresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Refresh1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Refresh1ActionPerformed

    private void coursecmbbxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coursecmbbxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_coursecmbbxActionPerformed

    private void remarksboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remarksboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_remarksboxActionPerformed

    private void clearedrdobtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearedrdobtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearedrdobtnActionPerformed

    private void pendingrdobtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pendingrdobtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pendingrdobtnActionPerformed

    private void courseboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_courseboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_courseboxActionPerformed

    private void yearsemboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearsemboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_yearsemboxActionPerformed

    private void fullnameboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullnameboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fullnameboxActionPerformed

    private void idnoboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idnoboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idnoboxActionPerformed

    private void aycmbbxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aycmbbxItemStateChanged
searchbox.setText("");
    String sem = "";
    if (firstsemrdobtn.isSelected()) {
        sem = "First Sem";
    } else if (secondsemrdobtn.isSelected()) {
        sem = "Second Sem";
    }

    String ay = aycmbbx.getItemCount() > 0 ? aycmbbx.getSelectedItem().toString() : "";
    String course = coursecmbbx.getItemCount() > 0 ? coursecmbbx.getSelectedItem().toString() : "";

    if ((aycmbbx.getSelectedIndex() == 0) && (coursecmbbx.getSelectedIndex() == 0)) {
        GetPendingClearance("", sem, "", "");
        GetClearedStudents("", sem, "", "");
    } else if ((aycmbbx.getSelectedIndex() != 0) && (coursecmbbx.getSelectedIndex() == 0)) {
        GetPendingClearance(ay, sem, "", "");
        GetClearedStudents(ay, sem, "", "");
    } else if ((aycmbbx.getSelectedIndex() == 0) && (coursecmbbx.getSelectedIndex() != 0)) {
        GetPendingClearance("", sem, course, "");
        GetClearedStudents("", sem, course, "");
    } else if ((aycmbbx.getSelectedIndex() != 0) && (coursecmbbx.getSelectedIndex() != 0)) {
        GetPendingClearance(ay, sem, course, "");
        GetClearedStudents(ay, sem, course, "");
    }

    }//GEN-LAST:event_aycmbbxItemStateChanged

    private void firstsemrdobtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_firstsemrdobtnMouseClicked
searchbox.setText("");
        firstsemrdobtn.setSelected(true);
        secondsemrdobtn.setSelected(false);
        if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()==0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()==0)){
            GetPendingClearance("", "First Sem", "","");
            GetClearedStudents("", "First Sem", "","");
        }else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()!=0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()==0)){
            GetPendingClearance(aycmbbx.getSelectedItem().toString(), "First Sem", "","");
            GetClearedStudents(aycmbbx.getSelectedItem().toString(), "First Sem", "","");
        }
        else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()==0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()!=0)){
            GetPendingClearance("", "First Sem",coursecmbbx.getSelectedItem().toString(),"");
            GetClearedStudents("", "First Sem",coursecmbbx.getSelectedItem().toString(),"");
        }
        else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()!=0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()!=0)){
            GetPendingClearance(aycmbbx.getSelectedItem().toString(), "First Sem", coursecmbbx.getSelectedItem().toString(),"");
            GetClearedStudents(aycmbbx.getSelectedItem().toString(), "First Sem", coursecmbbx.getSelectedItem().toString(),"");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_firstsemrdobtnMouseClicked

    private void secondsemrdobtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secondsemrdobtnMouseClicked
searchbox.setText("");
        firstsemrdobtn.setSelected(false);
        secondsemrdobtn.setSelected(true);
        if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()==0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()==0)){
            GetPendingClearance("", "Second Sem", "","");
            GetClearedStudents("", "Second Sem", "","");
        }else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()!=0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()==0)){
            GetPendingClearance(aycmbbx.getSelectedItem().toString(), "Second Sem", "","");
            GetClearedStudents(aycmbbx.getSelectedItem().toString(), "Second Sem", "","");
        }
        else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()==0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()!=0)){
            GetPendingClearance("", "Second Sem",coursecmbbx.getSelectedItem().toString(),"");
            GetClearedStudents("", "Second Sem",coursecmbbx.getSelectedItem().toString(),"");
        }
        else if((aycmbbx.getItemCount()>0 && aycmbbx.getSelectedIndex()!=0) && (coursecmbbx.getItemCount()>0 && coursecmbbx.getSelectedIndex()!=0)){
            GetPendingClearance(aycmbbx.getSelectedItem().toString(), "Second Sem", coursecmbbx.getSelectedItem().toString(),"");
            GetClearedStudents(aycmbbx.getSelectedItem().toString(), "Second Sem", coursecmbbx.getSelectedItem().toString(),"");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_secondsemrdobtnMouseClicked

    private void coursecmbbxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_coursecmbbxMouseClicked

        // TODO add your handling code here:
    }//GEN-LAST:event_coursecmbbxMouseClicked

    private void Refresh1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Refresh1MouseClicked
        aycmbbx.setSelectedIndex(0);
        firstsemrdobtn.setSelected(false);
        secondsemrdobtn.setSelected(false);
        coursecmbbx.setSelectedIndex(0);
        GetPendingClearance("", "", "",searchbox.getText().toLowerCase());
        GetClearedStudents("", "", "",searchbox.getText().toLowerCase());
        // TODO add your handling code here:
    }//GEN-LAST:event_Refresh1MouseClicked

    private void RefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RefreshMouseClicked
clearfields();        // TODO add your handling code here:
    }//GEN-LAST:event_RefreshMouseClicked

    private void pendingrdobtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pendingrdobtnMouseClicked
pendingrdobtn.setSelected(true);
clearedrdobtn.setSelected(false);
        // TODO add your handling code here:
    }//GEN-LAST:event_pendingrdobtnMouseClicked

    private void clearedrdobtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearedrdobtnMouseClicked
pendingrdobtn.setSelected(false);
clearedrdobtn.setSelected(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_clearedrdobtnMouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
try (Connection conn = DBConnection.getConnection()) {
        String remarks = remarksbox.getText().trim();
        if (remarks.isEmpty()) {
            remarks = null;
        }
        String updatequery="";
        if(pendingrdobtn.isSelected()){
            updatequery="UPDATE `clearancestatus` SET `status`='Pending',`remarks`=?,"
                + "`approvedBy`=?,`dateCleared`=NULL WHERE clearancestatusID=?";
        }
        else if(clearedrdobtn.isSelected()){
            updatequery="UPDATE `clearancestatus` SET `status`='Cleared',`remarks`=?,"
                + "`approvedBy`=?,`dateCleared`=NOW() WHERE clearancestatusID=?";
        }
        PreparedStatement pst = conn.prepareStatement(updatequery);
        pst.setString(1, remarks);
        pst.setInt(2, userID);
        pst.setString(3, clearancestatusIDlbl.getText());
        pst.executeUpdate();
        
        clearfields();
        
        JOptionPane.showMessageDialog(this, "Save successfully!", "Information", JOptionPane.INFORMATION_MESSAGE);
        
       }catch(SQLException ae){
           
       }

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
clearselectedstudents();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
SignInForm log = new SignInForm();
        log.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1MouseClicked

    private void pendingclearancetblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pendingclearancetblMouseClicked
        int row = pendingclearancetbl.getSelectedRow();

        if (row == -1){
            return;
        }
        String idno = pendingclearancetbl.getValueAt(row, 0).toString();
        String studentname = pendingclearancetbl.getValueAt(row, 1).toString();
        String course = pendingclearancetbl.getValueAt(row, 2).toString();
        String aysem = pendingclearancetbl.getValueAt(row, 3).toString();
        String status = pendingclearancetbl.getValueAt(row, 6).toString();
        String remarks = pendingclearancetbl.getValueAt(row, 7) == null ? "" : pendingclearancetbl.getValueAt(row, 7).toString();
        String clearancestatusID = pendingclearancetbl.getValueAt(row, 8).toString();

        idnobox.setText(idno);
        fullnamebox.setText(studentname);
        coursebox.setText(course);
        yearsembox.setText(aysem);
        if(status.equals("Pending")){
            pendingrdobtn.setSelected(true);
            clearedrdobtn.setSelected(false);
        }
        else if(status.equals("Cleared")){
            pendingrdobtn.setSelected(false);
            clearedrdobtn.setSelected(true);
        }
        remarksbox.setText(remarks);
        clearancestatusIDlbl.setText(clearancestatusID);
        // TODO add your handling code here:
    }//GEN-LAST:event_pendingclearancetblMouseClicked

    private void clearedclearancetblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearedclearancetblMouseClicked
        int row = clearedclearancetbl.getSelectedRow();

        if (row == -1){
            return;
        }
        String idno = clearedclearancetbl.getValueAt(row, 0).toString();
        String studentname = clearedclearancetbl.getValueAt(row, 1).toString();
        String course = clearedclearancetbl.getValueAt(row, 2).toString();
        String aysem = clearedclearancetbl.getValueAt(row, 3).toString();
        String status = clearedclearancetbl.getValueAt(row, 6).toString();
        String remarks = clearedclearancetbl.getValueAt(row, 7) == null ? "" : clearedclearancetbl.getValueAt(row, 7).toString();
        String clearancestatusID = clearedclearancetbl.getValueAt(row, 8).toString();

        // OUTPUT — you may display in textfields, labels, etc.
        idnobox.setText(idno);
        fullnamebox.setText(studentname);
        coursebox.setText(course);
        yearsembox.setText(aysem);
        if(status.equals("Pending")){
            pendingrdobtn.setSelected(true);
            clearedrdobtn.setSelected(false);
        }
        else if(status.equals("Cleared")){
            pendingrdobtn.setSelected(false);
            clearedrdobtn.setSelected(true);
        }
        remarksbox.setText(remarks);
        clearancestatusIDlbl.setText(clearancestatusID);
        // TODO add your handling code here:
    }//GEN-LAST:event_clearedclearancetblMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        //</editor-fold>
        /* Create and display the form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Refresh;
    private javax.swing.JButton Refresh1;
    private javax.swing.JComboBox<String> aycmbbx;
    private javax.swing.JLabel clearancestatusIDlbl;
    private javax.swing.JTable clearedclearancetbl;
    private javax.swing.JRadioButton clearedrdobtn;
    private javax.swing.JTextField coursebox;
    private javax.swing.JComboBox<String> coursecmbbx;
    private javax.swing.JRadioButton firstsemrdobtn;
    private javax.swing.JTextField fullnamebox;
    private javax.swing.JTextField idnobox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable pendingclearancetbl;
    private javax.swing.JRadioButton pendingrdobtn;
    private javax.swing.JTextField remarksbox;
    private javax.swing.JTextField searchbox;
    private javax.swing.JRadioButton secondsemrdobtn;
    private javax.swing.JLabel username;
    private javax.swing.JTextField yearsembox;
    // End of variables declaration//GEN-END:variables
}
