package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;
import model.StudentStore;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MainController {

    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField deptField;
    @FXML private TextField emailField;
    @FXML private TextField cgpaField;
    @FXML private TextField searchField;

    @FXML private TableView<Student> tableView;
    @FXML private TableColumn<Student, String>  colName;
    @FXML private TableColumn<Student, String>  colId;
    @FXML private TableColumn<Student, String>  colDept;
    @FXML private TableColumn<Student, String>  colEmail;
    @FXML private TableColumn<Student, Double>  colCgpa;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final StudentStore store = new StudentStore();

    private FilteredList<Student> filtered;
    private SortedList<Student> sorted;

    @FXML
    public void initialize() {
        // map columns using PropertyValueFactory 
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDept.setCellValueFactory(new PropertyValueFactory<>("dept"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCgpa.setCellValueFactory(new PropertyValueFactory<>("cgpa"));

        // load data from CSV
        studentList.setAll(store.load());

        // search + sort pipeline
        filtered = new FilteredList<Student>(studentList, new Predicate<Student>() {
            @Override public boolean test(Student s) { return true; } // show all initially
        });
        sorted = new SortedList<Student>(filtered);
        sorted.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sorted);
        tableView.setPlaceholder(new Label("No students yet — add above."));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // live search listener
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> obs, String ov, String nv) {
                applyFilter(nv);
            }
        });
    }

    private void applyFilter(String query) {
        final String q = (query == null) ? "" : query.trim().toLowerCase(Locale.ROOT);
        filtered.setPredicate(new Predicate<Student>() {
            @Override public boolean test(Student s) {
                return q.isEmpty()
                        || s.getName().toLowerCase(Locale.ROOT).contains(q)
                        || s.getId().toLowerCase(Locale.ROOT).contains(q)
                        || s.getDept().toLowerCase(Locale.ROOT).contains(q);
            }
        });
    }

    @FXML
    private void addStudent() {
        String err = validateForm(null);
        if (err != null) { warn("Invalid Input", err); return; }

        Student s = new Student(
                nameField.getText().trim(),
                idField.getText().trim(),
                deptField.getText().trim(),
                emailField.getText().trim(),
                parseDouble(cgpaField.getText().trim())
        );
        studentList.add(s);
        store.save(studentList);
        clearInputFields();
        applyFilter(searchField.getText());
    }

    @FXML
    private void updateStudent() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("No Selection", "Please select a student from the table."); return; }

        String err = validateForm(selected);
        if (err != null) { warn("Invalid Input", err); return; }

        selected.setName(nameField.getText().trim());
        selected.setId(idField.getText().trim());
        selected.setDept(deptField.getText().trim());
        selected.setEmail(emailField.getText().trim());
        selected.setCgpa(parseDouble(cgpaField.getText().trim()));

        tableView.refresh();
        store.save(studentList);
        clearInputFields();
    }

    @FXML
    private void deleteStudent() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("No Selection", "Please select a student to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete selected student?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            studentList.remove(selected);
            store.save(studentList);
            clearInputFields();
        }
    }

    @FXML
    private void onTableClicked() {
        Student s = tableView.getSelectionModel().getSelectedItem();
        if (s != null) {
            nameField.setText(s.getName());
            idField.setText(s.getId());
            deptField.setText(s.getDept());
            emailField.setText(s.getEmail());
            cgpaField.setText(String.valueOf(s.getCgpa()));
        }
    }

    /** Called by Main when window is closing. */
    public void onAppClose() { store.save(studentList); }

    
    private void clearInputFields() {
        nameField.clear();
        idField.clear();
        deptField.clear();
        emailField.clear();
        cgpaField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void warn(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle(title);
        a.showAndWait();
    }

    private String validateForm(Student current) {
        String name = safe(nameField.getText());
        String id   = safe(idField.getText());
        String dept = safe(deptField.getText());
        String email= safe(emailField.getText());
        String cgpa = safe(cgpaField.getText());

        if (name.isEmpty() || id.isEmpty() || dept.isEmpty() || email.isEmpty() || cgpa.isEmpty())
            return "All fields are required.";

        Pattern EMAIL = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
        if (!EMAIL.matcher(email).matches()) return "Please enter a valid email address.";

        double g;
        try { g = Double.parseDouble(cgpa); }
        catch (Exception e) { return "CGPA must be a number (e.g., 3.75)."; }
        if (g < 0.0 || g > 4.0) return "CGPA must be between 0.00 and 4.00.";

        // unique ID (ignore the same object when updating)
        boolean duplicate = false;
        for (Student s : studentList) {
            if (s.getId() != null && s.getId().equalsIgnoreCase(id) && s != current) {
                duplicate = true; break;
            }
        }
        if (duplicate) return "ID must be unique.";

        return null;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }
}
