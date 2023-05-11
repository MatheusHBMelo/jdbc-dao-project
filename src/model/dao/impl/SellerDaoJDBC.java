package model.dao.impl;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {
    private final Connection conn;

    public SellerDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Seller object cannot be null.");
        } else {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("INSERT INTO seller(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
                        "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, obj.getName());
                ps.setString(2, obj.getEmail());
                ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
                ps.setDouble(4, obj.getBaseSalary());
                ps.setInt(5, obj.getDepartment().getId());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        obj.setId(id);
                    }
                } else {
                    throw new DbException("Failed to insert seller " + obj.getName() + ". No rows affected.");
                }
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            } finally {
                DB.closeStatement(ps);
                DB.closeResultSet(rs);
                DB.closeConnection();
            }
        }
    }

    @Override
    public void update(Seller obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Seller object cannot be null.");
        } else {
            PreparedStatement ps = null;
            String sql = "UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, obj.getName());
                ps.setString(2, obj.getEmail());
                ps.setDate(3, (Date) obj.getBirthDate());
                ps.setDouble(4, obj.getBaseSalary());
                ps.setInt(5, obj.getDepartment().getId());
                ps.setInt(6, obj.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            } finally {
                DB.closeStatement(ps);
                DB.closeConnection();
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement ps = null;
        String sql = "DELETE FROM seller WHERE Id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rowAffected = ps.executeUpdate();
            if (rowAffected == 0){
                throw new DbIntegrityException("The id entered does not exist in the database!");
            }
        } catch (SQLException e){
            throw new DbIntegrityException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeConnection();
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT seller.*, department.Name as DepName " +
                "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id " +
                "WHERE seller.Id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()){
                Department dep = instantiateDepartment(rs);
                return instantiateSeller(rs, dep);
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
            DB.closeConnection();
        }
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT seller.*, department.Name as DepName " +
                "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id " +
                "ORDER BY Name";
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();
            while (rs.next()){
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                }
                Seller seller = instantiateSeller(rs, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
            DB.closeConnection();
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department object cannot be null.");
        } else {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "SELECT seller.*, department.Name as DepName " +
                    "FROM seller INNER JOIN department ON seller.DepartmentId = department.Id " +
                    "WHERE department.Id = ? " +
                    "ORDER BY Name";
            try {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, department.getId());
                rs = ps.executeQuery();
                List<Seller> list = new ArrayList<>();
                Map<Integer, Department> map = new HashMap<>(); // Estrutura de map para validar se um departamento existe
                while (rs.next()) {
                    Department dep = map.get(rs.getInt("DepartmentId")); // Armazenando o departamento salvo no map
                    if (dep == null) { // Verificando se o departamento existe, se não, cria-se e armazena-se no map
                        dep = instantiateDepartment(rs);
                        map.put(rs.getInt("DepartmentId"), dep);
                    }
                    Seller seller = instantiateSeller(rs, dep);
                    list.add(seller);
                }
                return list;
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            } finally {
                DB.closeStatement(ps);
                DB.closeResultSet(rs);
                DB.closeConnection();
            }
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller();
        seller.setId(rs.getInt("Id"));
        seller.setName(rs.getString("Name"));
        seller.setEmail(rs.getString("Email"));
        seller.setBirthDate(rs.getDate("BirthDate"));
        seller.setBaseSalary(rs.getDouble("BaseSalary"));
        seller.setDepartment(dep);
        return seller;
    }
}
