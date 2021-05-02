package com.example.comiccollection.data.modifiers;

import com.example.comiccollection.data.entities.Issue;

import java.util.ArrayList;
import java.util.List;

public interface IssuesModifier {

    List<Issue> modify(ArrayList<Issue> issues);
}
