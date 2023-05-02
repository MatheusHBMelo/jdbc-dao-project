package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.List;

public class Program {
    public static void main(String[] args) {
        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("========== TEST 1: Seller finById ==========");
        Seller seller = sellerDao.findById(8);
        System.out.println(seller);

        System.out.println("\n========== TEST 2: Seller finByDepartment ==========");
        Department department = new Department(3, null);
        List<Seller> list = sellerDao.findByDepartment(department);
        for (Seller sell: list){
            System.out.println(sell);
        }
    }
}
