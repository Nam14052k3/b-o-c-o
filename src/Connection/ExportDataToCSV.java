
package Connection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportDataToCSV {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ThuVien;encrypt=true;trustServerCertificate=true;";
        String user = "sa";
        String password = "123";
        // Tạo thư mục nếu chưa tồn tại
        File exportDir = new File("D:\\ExportedData");
        if (!exportDir.exists()) {
            if (exportDir.mkdirs()) {
                System.out.println("Đã tạo thư mục: " + exportDir.getAbsolutePath());
            } else {
                System.err.println("Không thể tạo thư mục: " + exportDir.getAbsolutePath());
                return; // Kết thúc chương trình nếu không thể tạo thư mục
            }
        }
        // Các bảng cần xuất
        String[] tables = {"TaiKhoan", "Sach", "SV", "PM"};
        String outputPath = "D:/ExportedData/";

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            for (String table : tables) {
                exportTableToCSV(con, table, outputPath + table + ".csv");
            }
            System.out.println("Dữ liệu đã được xuất thành công!");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private static void exportTableToCSV(Connection con, String tableName, String filePath) {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(filePath)) {

            // Ghi header
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Ghi dữ liệu
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(rs.getString(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }
            System.out.println("Xuất dữ liệu từ bảng " + tableName + " thành công!");
        } catch (SQLException | IOException e) {
            System.err.println("Lỗi xuất dữ liệu từ bảng " + tableName + ": " + e.getMessage());
        }
    }
}

