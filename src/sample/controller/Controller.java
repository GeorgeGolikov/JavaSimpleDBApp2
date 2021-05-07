package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import sample.database.dao.*;
import sample.entity.*;
import sample.service.*;
import sample.service.impl.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class Controller {
    private static GroupService groupService;
    private static StudentService studentService;
    private static TeacherService teacherService;
    private static SubjectService subjectService;
    private static MarkService markService;
    private static AverageMarkService averageMarkService;

    static {
        try {
            groupService = new GroupServiceImpl(new GroupsDao());
            studentService = new StudentServiceImpl(new StudentDao());
            teacherService = new TeacherServiceImpl(new TeacherDao());
            subjectService = new SubjectServiceImpl(new SubjectDao());
            markService = new MarkServiceImpl(new MarkDao());
            averageMarkService = new AverageMarkServiceImpl(new AverageMarkDao());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        initData();
        initGroups();
        initStudents();
        initTeachers();
        initSubjects();
        initMarks();
        initAverage();
    }

    private void initData() {
        groups = groupService.findAll();
        students = studentService.findAll();
        teachers = teacherService.findAll();
        subjects = subjectService.findAll();
    }

    /*
        -------------------------------------------- ГРУППЫ --------------------------------------------
    */
    private void initGroups() {
        tableGroupsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableGroupsColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableGroupsColumn.setOnEditCommit(e -> {
            String newName = e.getNewValue();
            editGroup(e.getTableView().getItems().get(e.getTablePosition().getRow()), newName);
        });
    }

    private ObservableList<Group> groups = FXCollections.observableArrayList();

    @FXML
    private Tab tabGroups;

    @FXML
    private TableView<Group> tableGroups;

    @FXML
    private TableColumn<Group, String> tableGroupsColumn;

    @FXML
    private TextField addGroupTf;

    @FXML
    private Button addGroupBut;

    @FXML
    private Button deleteGroupBut;

    // в момент нажатия на tabGroups
    public void loadGroups() {
        groups = groupService.findAll();
        tableGroups.setItems(groups);
    }

    public void addGroup() {
        String name = addGroupTf.getText();
        if ("".equals(name)) return;

        groupService.add(new Group(name));
        loadGroups();

        loadAverageComboBoxes();
    }

    public void delGroup() {
        Group group = tableGroups.getSelectionModel().getSelectedItem();
        if (group != null) {
            groupService.delete(group);
        }
        loadGroups();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void editGroup(Group group, String newGroupName) {
        if (group == null || "".equals(newGroupName)) return;

        groupService.update(group, new Group(newGroupName));
        group.setName(newGroupName);

        loadAverageComboBoxes();
    }

    /*
        -------------------------------------------- СТУДЕНТЫ --------------------------------------------
    */
    private void initStudents() {
        tableStudentsLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableStudentsNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableStudentsFatherNameColumn.setCellValueFactory(new PropertyValueFactory<>("fatherName"));
        tableStudentsGroupColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));

        tableStudentsLastNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableStudentsNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableStudentsFatherNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableStudentsGroupColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        tableStudentsLastNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Student oldStudent = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editStudent(oldStudent, newVal, oldStudent.getFirstName(),
                    oldStudent.getFatherName(), oldStudent.getGroupName());
        });
        tableStudentsNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Student oldStudent = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editStudent(oldStudent, oldStudent.getLastName(), newVal,
                    oldStudent.getFatherName(), oldStudent.getGroupName());
        });
        tableStudentsFatherNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Student oldStudent = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editStudent(oldStudent, oldStudent.getLastName(), oldStudent.getFirstName(),
                    newVal, oldStudent.getGroupName());
        });
        tableStudentsGroupColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Student oldStudent = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editStudent(oldStudent, oldStudent.getLastName(), oldStudent.getFirstName(),
                    oldStudent.getFatherName(), newVal);
        });
    }

    private ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    private Tab tabStudents;

    @FXML
    private TableView<Student> tableStudents;

    @FXML
    private TableColumn<Student, String> tableStudentsLastNameColumn;

    @FXML
    private TableColumn<Student, String> tableStudentsNameColumn;

    @FXML
    private TableColumn<Student, String> tableStudentsFatherNameColumn;

    @FXML
    private TableColumn<Student, String> tableStudentsGroupColumn;

    @FXML
    private TextField addStudentLastNameTf;

    @FXML
    private TextField addStudentNameTf;

    @FXML
    private TextField addStudentFatherNameTf;

    @FXML
    private TextField addStudentGroupTf;

    @FXML
    private Button addStudentBut;

    @FXML
    private Button deleteStudentBut;

    // в момент нажатия на tabStudents
    public void loadStudents() {
        students = studentService.findAll();
        tableStudents.setItems(students);
    }

    public void addStudent() {
        String lastName = addStudentLastNameTf.getText();
        String name = addStudentNameTf.getText();
        String fatherName = addStudentFatherNameTf.getText();
        String group = addStudentGroupTf.getText();

        if ("".equals(lastName) || "".equals(name) ||
            "".equals(fatherName) || "".equals(group)) return;

        studentService.add(new Student(name, lastName, fatherName, group));
        loadStudents();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void delStudent() {
        Student student = tableStudents.getSelectionModel().getSelectedItem();
        if (student != null) {
            studentService.delete(student);
        }
        loadStudents();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void editStudent(Student student, String lastName, String name,
                            String fatherName, String group) {
        if (student == null || "".equals(lastName) || "".equals(name) ||
            "".equals(fatherName) || "".equals(group)) return;

        studentService.update(student, new Student(name, lastName, fatherName, group));
        student.setLastName(lastName);
        student.setFirstName(name);
        student.setFatherName(fatherName);
        student.setGroupName(group);

        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    /*
        -------------------------------------------- ПРЕПОДАВАТЕЛИ --------------------------------------------
    */
    private void initTeachers() {
        tableTeachersLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableTeachersNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableTeachersFatherNameColumn.setCellValueFactory(new PropertyValueFactory<>("fatherName"));

        tableTeachersLastNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableTeachersNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableTeachersFatherNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));

        tableTeachersLastNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Teacher oldTeacher = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editTeacher(oldTeacher, newVal, oldTeacher.getFirstName(), oldTeacher.getFatherName());
        });
        tableTeachersNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Teacher oldTeacher = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editTeacher(oldTeacher, oldTeacher.getLastName(), newVal, oldTeacher.getFatherName());
        });
        tableTeachersFatherNameColumn.setOnEditCommit(e -> {
            String newVal = e.getNewValue();
            Teacher oldTeacher = e.getTableView().getItems().get(e.getTablePosition().getRow());
            editTeacher(oldTeacher, oldTeacher.getLastName(), oldTeacher.getFirstName(), newVal);
        });
    }

    private ObservableList<Teacher> teachers = FXCollections.observableArrayList();

    @FXML
    private Tab tabTeachers;

    @FXML
    private TableView<Teacher> tableTeachers;

    @FXML
    private TableColumn<Teacher, String> tableTeachersLastNameColumn;

    @FXML
    private TableColumn<Teacher, String> tableTeachersNameColumn;

    @FXML
    private TableColumn<Teacher, String> tableTeachersFatherNameColumn;

    @FXML
    private TextField addTeacherLastNameTf;

    @FXML
    private TextField addTeacherNameTf;

    @FXML
    private TextField addTeacherFatherNameTf;

    @FXML
    private Button addTeacherBut;

    @FXML
    private Button deleteTeacherBut;

    // в момент нажатия на tabTeachers
    public void loadTeachers() {
        teachers = teacherService.findAll();
        tableTeachers.setItems(teachers);
    }

    public void addTeacher() {
        String lastName = addTeacherLastNameTf.getText();
        String name = addTeacherNameTf.getText();
        String fatherName = addTeacherFatherNameTf.getText();

        if ("".equals(lastName) || "".equals(name) || "".equals(fatherName)) return;

        teacherService.add(new Teacher(name, lastName, fatherName));
        loadTeachers();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void delTeacher() {
        Teacher teacher = tableTeachers.getSelectionModel().getSelectedItem();
        if (teacher != null) {
            teacherService.delete(teacher);
        }
        loadTeachers();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void editTeacher(Teacher teacher, String lastName, String name, String fatherName) {
        if (teacher == null || "".equals(lastName) || "".equals(name) || "".equals(fatherName)) return;

        teacherService.update(teacher, new Teacher(name, lastName, fatherName));
        teacher.setLastName(lastName);
        teacher.setFirstName(name);
        teacher.setFatherName(fatherName);

        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    /*
        -------------------------------------------- ПРЕДМЕТЫ --------------------------------------------
    */
    private void initSubjects() {
        tableSubjectsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableSubjectsColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableSubjectsColumn.setOnEditCommit(e -> {
            String newName = e.getNewValue();
            editSubject(e.getTableView().getItems().get(e.getTablePosition().getRow()), newName);
        });
    }

    private ObservableList<Subject> subjects = FXCollections.observableArrayList();

    @FXML
    private Tab tabSubjects;

    @FXML
    private TableView<Subject> tableSubjects;

    @FXML
    private TableColumn<Subject, String> tableSubjectsColumn;

    @FXML
    private TextField addSubjectTf;

    @FXML
    private Button addSubjectBut;

    @FXML
    private Button deleteSubjectBut;

    // в момент нажатия на tabSubjects
    public void loadSubjects() {
        subjects = subjectService.findAll();
        tableSubjects.setItems(subjects);
    }

    public void addSubject() {
        String name = addSubjectTf.getText();
        if ("".equals(name)) return;

        subjectService.add(new Subject(name));
        loadSubjects();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void delSubject() {
        Subject subject = tableSubjects.getSelectionModel().getSelectedItem();
        if (subject != null) {
            subjectService.delete(subject);
        }
        loadSubjects();
        
        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    public void editSubject(Subject subject, String newSubjectName) {
        if (subject == null || "".equals(newSubjectName)) return;

        subjectService.update(subject, new Subject(newSubjectName));
        subject.setName(newSubjectName);

        loadMarksComboBoxes();
        loadAverageComboBoxes();
    }

    /*
        -------------------------------------------- ОЦЕНКИ --------------------------------------------
    */
    private void initMarks() {
        tableMarkSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        tableMarkTeacherLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherLastName"));
        tableMarkTeacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        tableMarkValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        tableMarkSubjectColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableMarkTeacherLastNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableMarkTeacherNameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        tableMarkValueColumn.setCellFactory(
                ComboBoxTableCell.forTableColumn(2, 3, 4, 5));

        tableMarkValueColumn.setOnEditCommit(e -> {
            Integer newVal = e.getNewValue();
            editMark(e.getTableView().getItems().get(e.getTablePosition().getRow()), newVal);
        });

        loadMarksComboBoxes();
        marksValueCombo.setItems(FXCollections.observableArrayList(2, 3, 4, 5));
    }

    private void loadMarksComboBoxes() {
        marksStudentsCombo.setItems(students);
        marksSubjectsCombo.setItems(subjects);
        marksTeachersCombo.setItems(teachers);
    }

    private ObservableList<Mark> marks = FXCollections.observableArrayList();

    @FXML
    private Tab tabMarks;

    @FXML
    private TableView<Mark> tableMarks;

    @FXML
    private TableColumn<Mark, String> tableMarkSubjectColumn;

    @FXML
    private TableColumn<Mark, String> tableMarkTeacherLastNameColumn;

    @FXML
    private TableColumn<Mark, String> tableMarkTeacherNameColumn;

    @FXML
    private TableColumn<Mark, Integer> tableMarkValueColumn;

    @FXML
    private ComboBox<Student> marksStudentsCombo;

    @FXML
    private ComboBox<Subject> marksSubjectsCombo;

    @FXML
    private ComboBox<Teacher> marksTeachersCombo;

    @FXML
    private ComboBox<Integer> marksValueCombo;

    @FXML
    private Button addMarkBut;

    @FXML
    private Button deleteMarkBut;

    // в момент смены value в marksStudentsCombo
    public void loadMarks() {
        marks = markService.findAllByStudent(marksStudentsCombo.getValue());
        tableMarks.setItems(marks);
    }

    public void addMark() {
        Subject subject = marksSubjectsCombo.getValue();
        Teacher teacher = marksTeachersCombo.getValue();
        Integer value = marksValueCombo.getValue();
        Student student = marksStudentsCombo.getValue();

        if (subject == null || teacher == null || value == null || student == null)
            return;

        markService.add(new Mark(student.getId(), subject.getName(), teacher.getId(), value));
        loadMarks();
    }

    public void delMark() {
        Mark mark = tableMarks.getSelectionModel().getSelectedItem();
        if (mark != null) {
            markService.delete(mark);
        }
        loadMarks();
    }

    public void editMark(Mark mark, Integer newValue) {
        Student student = marksStudentsCombo.getValue();
        if (mark == null || newValue == null || student == null) return;

        markService.update(mark, new Mark(newValue));
        mark.setValue(newValue);
    }

    /*
        -------------------------------------------- СРЕДНИЙ БАЛЛ --------------------------------------------
    */
    private void initAverage() {
        filterStudCombo.setOnAction(e -> filterCalcBut.setDisable(false));
        filterTeacherCombo.setOnAction(e -> filterCalcBut.setDisable(false));
        filterSubjCombo.setOnAction(e -> filterCalcBut.setDisable(false));
        filterGroupCombo.setOnAction(e -> filterCalcBut.setDisable(false));

        loadAverageComboBoxes();
        filterCombo.setItems(FXCollections.observableArrayList("Студенты", "Преподаватели",
                                                                "Предметы", "Группы"));
    }

    private void loadAverageComboBoxes() {
        filterStudCombo.setItems(students);
        filterTeacherCombo.setItems(teachers);
        filterSubjCombo.setItems(subjects);
        filterGroupCombo.setItems(groups);
    }

    @FXML
    private Tab tabAverage;

    @FXML
    private ComboBox<String> filterCombo;

    @FXML
    private ComboBox<Student> filterStudCombo;

    @FXML
    private ComboBox<Teacher> filterTeacherCombo;

    @FXML
    private ComboBox<Subject> filterSubjCombo;

    @FXML
    private ComboBox<Group> filterGroupCombo;

    @FXML
    private DatePicker filterStartDate;

    @FXML
    private DatePicker filterEndDate;

    @FXML
    private TextField filterAverageTf;

    @FXML
    private Button filterCalcBut;
    
    private String selectedFilter;

    public void filterSelected() {
        selectedFilter = filterCombo.getValue();
        switch (selectedFilter) {
            case "Студенты":
                filterStudCombo.setDisable(false);
                filterTeacherCombo.setDisable(true);
                filterSubjCombo.setDisable(true);
                filterGroupCombo.setDisable(true);
                break;
            case "Преподаватели":
                filterTeacherCombo.setDisable(false);
                filterStudCombo.setDisable(true);
                filterSubjCombo.setDisable(true);
                filterGroupCombo.setDisable(true);
                break;
            case "Предметы":
                filterSubjCombo.setDisable(false);
                filterStudCombo.setDisable(true);
                filterTeacherCombo.setDisable(true);
                filterGroupCombo.setDisable(true);
                break;
            case "Группы":
                filterGroupCombo.setDisable(false);
                filterStudCombo.setDisable(true);
                filterTeacherCombo.setDisable(true);
                filterSubjCombo.setDisable(true);
                break;
            default:
                break;
        }
    }
    
    public void calculatePerformance() {
        LocalDate start = filterStartDate.getValue();
        LocalDate end = filterEndDate.getValue();
        if (start != null && end != null && start.getYear() <= end.getYear()) {
            String averageMark;
            switch (selectedFilter) {
                case "Студенты":
                    averageMark = averageMarkService.calcPerfStud(start.toString(), end.toString(),
                                                                filterStudCombo.getValue().getId());
                    break;
                case "Преподаватели":
                    averageMark = averageMarkService.calcPerfTeach(start.toString(), end.toString(),
                                                                filterTeacherCombo.getValue().getId());
                    break;
                case "Предметы":
                    averageMark = averageMarkService.calcPerfSubj(start.toString(), end.toString(),
                                                                filterSubjCombo.getValue().getName());
                    break;
                case "Группы":
                    averageMark = averageMarkService.calcPerfGroup(start.toString(), end.toString(),
                                                                filterGroupCombo.getValue().getName());
                    break;
                default:
                    averageMark = "";
                    break;
            }
            filterAverageTf.setText(averageMark);
        }
        disableComboBoxes();
    }

    private void disableComboBoxes() {
        filterStudCombo.setDisable(true);
        filterTeacherCombo.setDisable(true);
        filterSubjCombo.setDisable(true);
        filterGroupCombo.setDisable(true);
        filterCalcBut.setDisable(true);
    }
}
