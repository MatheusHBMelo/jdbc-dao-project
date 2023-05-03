package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.util.Date;
import java.util.List;

public class Program {
    public static void main(String[] args) {
        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("========== TEST 1: Seller findById ==========");
        Seller seller = sellerDao.findById(8);
        System.out.println(seller);

        System.out.println("\n========== TEST 2: Seller findByDepartment ==========");
        Department department = new Department(3, null);
        List<Seller> list = sellerDao.findByDepartment(department);
        for (Seller sell: list){
            System.out.println(sell);
        }

        System.out.println("\n========== TEST 3: Seller findAll ==========");
        List<Seller> list2 = sellerDao.findAll();
        for (Seller sell: list2){
            System.out.println(sell);
        }

        System.out.println("\n========== TEST 4: Seller insert ==========");
        Seller seller2 = new Seller(null, "Alexandre Collins", "sopa@email.com", new Date(), 4000.00, department);
        sellerDao.insert(seller2);
        System.out.println("Inserted! New id: " + seller2.getId());

        System.out.println("\n========== TEST 5: Seller update ==========");
        Seller sellerUp = sellerDao.findById(12);
        sellerUp.setName("Teste Update");
        sellerUp.setEmail("teste@email.com");
        sellerUp.getDepartment().setId(1);
        sellerDao.update(sellerUp);
        System.out.println("Update complete!");

        System.out.println("\n========== TEST 5: Seller delete ==========");
        sellerDao.deleteById(12);
        System.out.println("Done!");
    }
}
