package br.com.fatec.icpmanager.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class NameCutterHelperTest {

    private String name = "Paulo Vinicius de Mello";
    private String REGEX = " ";

    @Test
    public void cutName() {
        String[] splittedName = name.split(REGEX);
        assertThat(splittedName[0], is("Paulo"));
        System.out.println("Saida --> " + splittedName[0]);
    }

    @Test
    public void cutName1() {
        int end = 2, start = 0;
        String[] splittedName = name.split(REGEX);
        StringBuilder resultName = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i != end - 1)
                resultName.append(splittedName[i] + " ");
            else
                resultName.append(splittedName[i]);
        }
        assertThat(resultName.toString(), is("Paulo Vinicius"));
        System.out.println("Saida --> " + resultName.toString());
    }

    @Test
    public void cutName2() {
    }
}