package com.example.comiccollection.data.modifiers;

import com.example.comiccollection.data.entities.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssuesSorter implements IssuesModifier {
    @Override
    public List<Issue> modify(ArrayList<Issue> issues) {
        issues.sort((i1, i2) -> {
            /*
            The issue numbers will usually, but maybe not necessarily, be numeric.  Sort
            numeric numbers first, as integers, then sort any remaining as strings.
             */
            Pattern pattern = Pattern.compile("^\\d+$");
            Matcher matcher1 = pattern.matcher(i1.getIssueNumber());
            Matcher matcher2 = pattern.matcher(i2.getIssueNumber());

            /*
            Both numeric.
             */
            if( matcher1.find() && matcher2.find() ) {
                return Integer.valueOf(i1.getIssueNumber()).compareTo(Integer.valueOf(i2.getIssueNumber()));
            }

            /*
            Neither numeric.
             */
            if( !matcher1.find() && !matcher2.find() ) {
                return i1.getIssueNumber().compareTo(i2.getIssueNumber());
            }

            /*
            Issue 1 is numeric, sort it first.
             */
            if( matcher1.find() ) {
                return -1;
            }

            /*
            Issue 2 is numeric, sort it first.
             */
            return 1;
        });

        return issues;
    }
}
