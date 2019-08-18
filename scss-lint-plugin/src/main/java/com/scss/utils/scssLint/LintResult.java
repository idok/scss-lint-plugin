package com.scss.utils.scssLint;

import java.util.List;
import java.util.Map;

public class LintResult {
    public Map<String, List<Lint.Issue>> lint;
    public String errorOutput;
}
