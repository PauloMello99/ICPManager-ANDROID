package br.com.fatec.icpmanager.dao;

import br.com.fatec.icpmanager.model.Professor;

public class ProfessorDAO extends AbstractDAO<Professor> {
    public ProfessorDAO() {
        super("professors");
    }
}