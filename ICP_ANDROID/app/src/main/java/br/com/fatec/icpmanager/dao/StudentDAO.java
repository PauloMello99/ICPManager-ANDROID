package br.com.fatec.icpmanager.dao;

import br.com.fatec.icpmanager.model.Student;

public class StudentDAO extends AbstractDAO<Student> {

    public StudentDAO() {
        super("students");
    }
}
