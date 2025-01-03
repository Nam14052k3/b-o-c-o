
package baocaothongke;

import Connection.DatabaseConnection;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import java.sql.PreparedStatement;

public class BaoCaoThongKe2 extends javax.swing.JFrame {
public void loadDataToTable() {
    try (Connection con = DatabaseConnection.getConnection()) {
        if (con == null) {
            return;
        }

        // Truy vấn dữ liệu cho bảng 1 (ví dụ: Thống kê kho sách ban đầu)
        String sql1 = "SELECT * FROM sach";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql1)) {
            DefaultTableModel model1 = (DefaultTableModel) jTable4.getModel();
            model1.setRowCount(0); // Xóa tất cả các dòng hiện tại

            while (rs.next()) {
                Object[] row1 = new Object[6]; // Giả sử có 6 cột
                row1[0] = rs.getString("MaSach");
                row1[1] = rs.getString("TenSach");
                row1[2] = rs.getString("TacGia");
                row1[3] = rs.getString("TheLoai");
                row1[4] = rs.getString("NhaXuatBan");
                row1[5] = rs.getInt("SoLuong");
                model1.addRow(row1);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query for jTable4: " + e.getMessage());
            e.printStackTrace();
        }

        

        // Truy vấn dữ liệu cho bảng 4 (ví dụ: Thống kê sách đang mượn)
        String sql4 = "SELECT * FROM PM";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql4)) {
            DefaultTableModel model4 = (DefaultTableModel) jTable1.getModel();
            model4.setRowCount(0); // Xóa tất cả các dòng hiện tại

            while (rs.next()) {
                Object[] row4 = new Object[8];
                row4[0] = rs.getString("MaPM");
                row4[1] = rs.getString("MaSV");
                row4[2] = rs.getString("MaSach");
                row4[3] = rs.getInt("SoLuong");
                row4[4] = rs.getDate("NgayMuon");
                row4[5] = rs.getDate("HanTra");
                row4[6] = rs.getDate("NgayTra");
                row4[7] = rs.getInt("PhiPhat");  // Sử dụng để hiển thị thông tin về số lượng còn lại
                model4.addRow(row4);
            }
        } catch (SQLException e) {
            System.err.println("Error executing query for jTable1: " + e.getMessage());
            e.printStackTrace();
        }

    } catch (SQLException e) {
        System.err.println("Error connecting to the database: " + e.getMessage());
        e.printStackTrace();
    }
}
private void filterAndSortBooks() {
    String selectedCategory = jComboBox2.getSelectedItem().toString(); // Lấy loại sách được chọn
    String selectedSort = jComboBox1.getSelectedItem().toString(); // Lấy kiểu sắp xếp được chọn

    StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM sach");

    // Thêm điều kiện lọc theo thể loại sách nếu không phải "Tất cả"
    if (!"Tất cả".equals(selectedCategory)) {
        sqlBuilder.append(" WHERE TheLoai = ?");
    }

    // Thêm điều kiện sắp xếp
    if ("Số lượng tăng dần".equals(selectedSort)) {
        sqlBuilder.append(" ORDER BY SoLuong ASC");
    } else if ("Số lượng giảm dần".equals(selectedSort)) {
        sqlBuilder.append(" ORDER BY SoLuong DESC");
    }

    String sql = sqlBuilder.toString();

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        // Thiết lập tham số cho PreparedStatement nếu cần
        if (!"Tất cả".equals(selectedCategory)) {
            pstmt.setString(1, selectedCategory);
        }

        try (ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
            model.setRowCount(0); // Xóa tất cả dữ liệu hiện tại

            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("MaSach");
                row[1] = rs.getString("TenSach");
                row[2] = rs.getString("TacGia");
                row[3] = rs.getString("TheLoai");
                row[4] = rs.getString("NhaXuatBan");
                row[5] = rs.getInt("SoLuong");
                model.addRow(row); // Thêm dòng mới vào bảng
            }
        }
    } catch (SQLException e) {
        System.err.println("Error filtering and sorting books: " + e.getMessage());
        e.printStackTrace();
    }
}
private void filterBorrowingRecords() {
    java.util.Date startDate = jDateChooser1.getDate();
    java.util.Date endDate = jDateChooser2.getDate();
    String status = jComboBox3.getSelectedItem().toString();

    // Kiểm tra ngày không null
    if (startDate == null || endDate == null) {
        System.err.println("Vui lòng chọn cả hai ngày bắt đầu và kết thúc.");
        return;
    }

    // Chuyển đổi java.util.Date sang java.sql.Date
    java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
    java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

    StringBuilder sql = new StringBuilder("SELECT * FROM PM WHERE NgayMuon BETWEEN ? AND ?");
    if (!"Tất cả".equals(status)) {
        if ("Đã trả".equals(status)) {
            sql.append(" AND NgayTra IS NOT NULL");
        } else if ("Trả muộn".equals(status)) {
            sql.append(" AND NgayTra > HanTra");
        } else if ("Chưa trả".equals(status)) {
            sql.append(" AND NgayTra IS NULL");
        }
    }

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

        pstmt.setDate(1, sqlStartDate);
        pstmt.setDate(2, sqlEndDate);

        try (ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // Xóa dữ liệu cũ

            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("MaPM");
                row[1] = rs.getString("MaSV");
                row[2] = rs.getString("MaSach");
                row[3] = rs.getInt("SoLuong");
                row[4] = rs.getDate("NgayMuon");
                row[5] = rs.getDate("HanTra");
                row[6] = rs.getDate("NgayTra");
                row[7] = rs.getInt("PhiPhat");
                model.addRow(row);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error filtering borrowing records: " + e.getMessage());
        e.printStackTrace();
    }
}
    public BaoCaoThongKe2()  {
        initComponents();
        loadDataToTable();
        jComboBox2.addActionListener(evt -> filterAndSortBooks());
        jComboBox1.addActionListener(evt -> filterAndSortBooks());
        jComboBox3.addActionListener(evt -> filterBorrowingRecords());
        jDateChooser1.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                filterBorrowingRecords();
            }
        });
        jDateChooser2.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                filterBorrowingRecords();
            }
});

    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        btnXuat1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnXuat2 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("Loại sách:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 51, 51));
        jLabel4.setText("Sắp xếp theo:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Số lượng tăng dần", "Số lượng giảm dần" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Giáo trình CNTT", "Giáo trình ATTT", "Giáo trình DTVT" }));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sách", "Tên Sách", "Tên Tác Gia", "Tên Thể Loại", "Nhà Xuất Bản", "Số Lượng"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        btnXuat1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/1234.png"))); // NOI18N
        btnXuat1.setText("Xuất File");
        btnXuat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuat1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnXuat1)
                        .addGap(64, 64, 64))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 220, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXuat1)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sách", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 51, 51));
        jLabel5.setText("Từ ngày");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("Đến ngày");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Đã trả", "Trả muộn", "Chưa trả" }));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 51, 51));
        jLabel7.setText("Trạng thái");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã PM", "Mã SV", "Mã Sách", "Số Lượng", "Ngày Mượn", "Hạn Trả", "Ngày Trả", "Phí Phạt"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnXuat2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/1234.png"))); // NOI18N
        btnXuat2.setText("Xuất File");
        btnXuat2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuat2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 535, Short.MAX_VALUE)
                        .addComponent(btnXuat2)
                        .addGap(53, 53, 53))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112))
                    .addComponent(jScrollPane1)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXuat2)
                .addGap(9, 9, 9))
        );

        jTabbedPane1.addTab("Phiếu Mượn", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnXuat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuat1ActionPerformed
        // TODO add your handling code here:
        // Mở hộp thoại để chọn đường dẫn lưu file
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Chọn nơi lưu file");
    fileChooser.setSelectedFile(new java.io.File("ThongKe.csv"));
    
    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
            // Ghi dữ liệu từ jTable1 vào file CSV
            writeTableToCSV(jTable1, writer);
            
            // Ghi dữ liệu từ jTable4 vào file CSV
            writeTableToCSV(jTable4, writer);
            // Thông báo hoàn thành
            System.out.println("Xuất file thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi khi xuất file: " + e.getMessage());
        }
    }
}

private void writeTableToCSV(javax.swing.JTable table, BufferedWriter writer) throws IOException {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    // Viết tiêu đề
    for (int i = 0; i < table.getColumnCount(); i++) {
        writer.write(model.getColumnName(i));
        if (i < table.getColumnCount() - 1) writer.write(",");
    }
    writer.newLine();
    // Viết dữ liệu
    for (int i = 0; i < model.getRowCount(); i++) {
        for (int j = 0; j < table.getColumnCount(); j++) {
            writer.write(String.valueOf(model.getValueAt(i, j)));
            if (j < table.getColumnCount() - 1) writer.write(",");
        }
        writer.newLine();
    }
    }//GEN-LAST:event_btnXuat1ActionPerformed

    private void btnXuat2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuat2ActionPerformed
        // TODO add your handling code here:
        // Mở hộp thoại để chọn đường dẫn lưu file
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Chọn nơi lưu file");
    fileChooser.setSelectedFile(new java.io.File("ThongKe.csv"));
    
    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
            // Ghi dữ liệu từ jTable1 vào file CSV
            writeTableToCSV(jTable1, writer);
            
            // Ghi dữ liệu từ jTable4 vào file CSV
            writeTableToCSV(jTable4, writer);
            // Thông báo hoàn thành
            System.out.println("Xuất file thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi khi xuất file: " + e.getMessage());
        }
    }
}

private void writeTableToCSV1(javax.swing.JTable table, BufferedWriter writer) throws IOException {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    // Viết tiêu đề
    for (int i = 0; i < table.getColumnCount(); i++) {
        writer.write(model.getColumnName(i));
        if (i < table.getColumnCount() - 1) writer.write(",");
    }
    writer.newLine();
    // Viết dữ liệu
    for (int i = 0; i < model.getRowCount(); i++) {
        for (int j = 0; j < table.getColumnCount(); j++) {
            writer.write(String.valueOf(model.getValueAt(i, j)));
            if (j < table.getColumnCount() - 1) writer.write(",");
        }
        writer.newLine();
    }
    }//GEN-LAST:event_btnXuat2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BaoCaoThongKe2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BaoCaoThongKe2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BaoCaoThongKe2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BaoCaoThongKe2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BaoCaoThongKe2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnXuat1;
    private javax.swing.JButton btnXuat2;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable4;
    // End of variables declaration//GEN-END:variables
}
