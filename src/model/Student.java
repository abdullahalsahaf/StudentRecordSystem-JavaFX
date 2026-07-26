package model;

import javafx.beans.property.*;

public class Student {
    private final StringProperty name;
    private final StringProperty id;
    private final StringProperty dept;
    private final StringProperty email;
    private final DoubleProperty cgpa;

    public Student(String name, String id, String dept, String email, double cgpa) {
        this.name  = new SimpleStringProperty(name);
        this.id    = new SimpleStringProperty(id);
        this.dept  = new SimpleStringProperty(dept);
        this.email = new SimpleStringProperty(email);
        this.cgpa  = new SimpleDoubleProperty(cgpa);
    }

    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    public String getId() { return id.get(); }
    public void setId(String v) { id.set(v); }
    public StringProperty idProperty() { return id; }

    public String getDept() { return dept.get(); }
    public void setDept(String v) { dept.set(v); }
    public StringProperty deptProperty() { return dept; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { email.set(v); }
    public StringProperty emailProperty() { return email; }

    public double getCgpa() { return cgpa.get(); }
    public void setCgpa(double v) { cgpa.set(v); }
    public DoubleProperty cgpaProperty() { return cgpa; }
}
