package model.dao.impl;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DepartmentDaoJDBC implements DepartmentDao {
    private final Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Department object cannot be null.");
        } else {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("INSERT INTO department(Name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, obj.getName());
                int rowAffected = ps.executeUpdate();
                if (rowAffected > 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        obj.setId(id);
                    }
                } else {
                    throw new DbException("Failed to insert department " + obj.getName() + ". No rows affected.");
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
    public void update(Department obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Department object cannot be null.");
        } else {
            PreparedStatement ps = null;
            String sql = "UPDATE department SET Name = ? WHERE Id = ?";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, obj.getName());
                ps.setInt(2, obj.getId());
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
        String sql = "DELETE FROM department WHERE Id = ?";
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
    public Department findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM department WHERE Id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()){
                return instantiateDepartment(rs);
            } else {
                throw new NoSuchElementException("Department with id " + id + " not found");
            }
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
            DB.closeConnection();
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM department";
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            List<Department> list = new ArrayList<>();
            while (rs.next()){
                Department dep = instantiateDepartment(rs);
                list.add(dep);
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

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
}
