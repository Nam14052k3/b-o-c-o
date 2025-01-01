
package DataBaseManager;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        // Lấy và in danh sách tài khoản
        System.out.println("Danh sách tài khoản:");
        dbManager.getAllTaiKhoan().forEach(System.out::println);

        // Lấy và in danh sách sách
        System.out.println("\nDanh sách sách:");
        dbManager.getAllSach().forEach(System.out::println);

        // Lấy và in danh sách sinh viên
        System.out.println("\nDanh sách sinh viên:");
        dbManager.getAllSV().forEach(System.out::println);

        // Lấy và in danh sách phiếu mượn
        System.out.println("\nDanh sách phiếu mượn:");
        dbManager.getAllPM().forEach(System.out::println);

        dbManager.disconnect();
    }
}
