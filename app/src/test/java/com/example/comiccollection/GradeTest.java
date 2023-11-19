package com.example.comiccollection;

import com.example.comiccollection.data.entities.Grade;

import org.junit.Test;

public class GradeTest {
    @Test
    public void stringsMapToGrades() {
        Grade fairPoorGrade = Grade.fromString("Fr/Pr");
        assert(fairPoorGrade == null);

        Grade poorFairGrade = Grade.fromString("Pr/Fr");
        assert(poorFairGrade != null);
        assert(poorFairGrade.toString().equals("PR/FR"));

        Grade fineGrade = Grade.fromString("Fine");
        assert(fineGrade != null);
        assert(fineGrade.equals(Grade.F));
    }
}
