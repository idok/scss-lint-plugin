package com.scss.utils.scssLint;

import java.util.List;
import java.util.Map;

/**
 * Created by idok on 8/11/14.
 */
public class LintResult {
    public Map<String, List<Lint.Issue>> lint;
    public String errorOutput;
}
