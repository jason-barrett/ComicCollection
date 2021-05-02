package com.example.comiccollection.data.modifiers;

import com.example.comiccollection.data.entities.Issue;

import java.util.ArrayList;
import java.util.List;

public class IssuesSorter implements IssuesModifier {
    @Override
    public List<Issue> modify(ArrayList<Issue> issues) {
        issues.sort((i1, i2) -> {return i1.getIssueNumber().compareTo(i2.getIssueNumber());});

        return issues;
    }
}
