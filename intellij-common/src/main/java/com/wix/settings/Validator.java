package com.wix.settings;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Validator {

    private final List<ValidationInfo> errs = new ArrayList<>();

    public boolean hasErrors() {
        return !errs.isEmpty();
    }

    public void add(JTextField textEditor, String s, String fixIt) {
        errs.add(new ValidationInfo(textEditor, s, fixIt));
    }

    public List<ValidationInfo> getErrors() {
        return errs;
    }
}
