package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class Program2 {
    public static void main(String[] args) {
        DepartmentDao depDao = DaoFactory.createDepartmentDao();

        System.out.println("===== Test 1: Department findAll");
        List<Department> list = depDao.findAll();
        for (Department dep: list){
            System.out.println(dep);
        }

        System.out.println("\n===== Test 2: Department findById");
        Department dep1 = depDao.findById(1);
        System.out.println(dep1);

        System.out.println("\n===== Test 3: Department insert");
        Department dep2 = new Department(null, "Cook");
        depDao.insert(dep2);
        System.out.println("Success! New department created - Id: " + dep2.getId());

        System.out.println("\n===== Test 4: Department update");
        Department dep3 = depDao.findById(3);
        dep3.setName("Business");
        depDao.update(dep3);
        System.out.println("Done! Update Completed.");

        System.out.println("\n===== Test 5: Department deleteById");
        depDao.deleteById(8);
        System.out.println("Done!");
    }
}
