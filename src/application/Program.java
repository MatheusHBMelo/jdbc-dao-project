package application;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class Program {
    public static void main(String[] args) {
        SellerDao sellerDao = DaoFactory.createSellerDao();

        System.out.println("========== TEST 1: Seller finById ==========");
        Seller seller = sellerDao.findById(8);
        System.out.println(seller);
    }
}
