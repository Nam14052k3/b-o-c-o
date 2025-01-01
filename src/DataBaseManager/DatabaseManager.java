
package DataBaseManager;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=ThuVien;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; // Tên người dùng SQL Server
    private static final String PASSWORD = "123"; // Mật khẩu SQL Server

    private Connection connection;

    // Kết nối tới database
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Kết nối thành công tới cơ sở dữ liệu!");
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối tới cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // Đóng kết nối
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    // Lấy dữ liệu từ bảng TaiKhoan
    public List<String> getAllTaiKhoan() {
        List<String> taiKhoanList = new ArrayList<>();
        String query = "SELECT * FROM TaiKhoan";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                boolean role = rs.getBoolean("role");
                taiKhoanList.add("Username: " + username + ", Password: " + password + ", Role: " + role);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ bảng TaiKhoan: " + e.getMessage());
        }

        return taiKhoanList;
    }

    // Lấy dữ liệu từ bảng Sach
    public List<String> getAllSach() {
        List<String> sachList = new ArrayList<>();
        String query = "SELECT * FROM Sach";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String maSach = rs.getString("MaSach");
                String tenSach = rs.getString("TenSach");
                String tacGia = rs.getString("TacGia");
                String theLoai = rs.getString("TheLoai");
                String nhaXuatBan = rs.getString("NhaXuatBan");
                int soLuong = rs.getInt("SoLuong");
                String anh = rs.getString("Anh");

                sachList.add("Mã sách: " + maSach + ", Tên sách: " + tenSach + ", Tác giả: " + tacGia +
                        ", Thể loại: " + theLoai + ", Nhà xuất bản: " + nhaXuatBan + ", Số lượng: " + soLuong +
                        ", Ảnh: " + anh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ bảng Sach: " + e.getMessage());
        }

        return sachList;
    }

    // Lấy dữ liệu từ bảng SV
    public List<String> getAllSV() {
        List<String> svList = new ArrayList<>();
        String query = "SELECT * FROM SV";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String maSV = rs.getString("MaSV");
                String hoTen = rs.getString("HoTen");
                String gioiTinh = rs.getString("GioiTinh");
                Date ngaySinh = rs.getDate("NgaySinh");
                String diaChi = rs.getString("DiaChi");
                String email = rs.getString("Email");
                String sdt = rs.getString("Sdt");
                String anh = rs.getString("Anh");

                svList.add("Mã SV: " + maSV + ", Họ tên: " + hoTen + ", Giới tính: " + gioiTinh +
                        ", Ngày sinh: " + ngaySinh + ", Địa chỉ: " + diaChi + ", Email: " + email +
                        ", Số điện thoại: " + sdt + ", Ảnh: " + anh);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ bảng SV: " + e.getMessage());
        }

        return svList;
    }

    // Lấy dữ liệu từ bảng PM
    public List<String> getAllPM() {
        List<String> pmList = new ArrayList<>();
        String query = "SELECT * FROM PM";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int maPM = rs.getInt("MaPM");
                String maSV = rs.getString("MaSV");
                String maSach = rs.getString("MaSach");
                int soLuong = rs.getInt("SoLuong");
                Date ngayMuon = rs.getDate("NgayMuon");
                Date hanTra = rs.getDate("HanTra");
                Date ngayTra = rs.getDate("NgayTra");
                double phiPhat = rs.getDouble("PhiPhat");

                pmList.add("Mã PM: " + maPM + ", Mã SV: " + maSV + ", Mã sách: " + maSach +
                        ", Số lượng: " + soLuong + ", Ngày mượn: " + ngayMuon + ", Hạn trả: " + hanTra +
                        ", Ngày trả: " + ngayTra + ", Phí phạt: " + phiPhat);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dữ liệu từ bảng PM: " + e.getMessage());
        }

        return pmList;
    }
}

