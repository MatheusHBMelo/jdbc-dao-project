package model.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Department implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;

    public Department(){};

    public Department(Integer id, String name){
        this.id = Objects.requireNonNull(id, "O ID não pode ser nulo");
        this.name = Objects.requireNonNull(name, "O nome não pode ser nulo");
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String toString(){
        return "ID: " + id + " - Name: " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
