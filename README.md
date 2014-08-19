# SCSS Lint Plugin #

[scss-lint](https://github.com/causes/scss-lint) is a tool to help keep your SCSS files clean and readable. see more [here](https://github.com/causes/scss-lint).<br/>
SCSS Lint plugin for WebStorm, PHPStorm and other Idea family IDE with Javascript plugin, provides integration with SCSS Lint and shows errors and warnings inside the editor.
* Support displaying SCSS Lint warnings as intellij inspections

## Getting started ##
### Prerequisites ###
Install scss-lint on your machine see instructions [here](https://github.com/causes/scss-lint#installation)</a>:<br/>

### Settings ###
To get started, you need to set the SCSS Lint plugin settings:<br/>

* Go to preferences, SCSS Lint plugin page and check the Enable plugin.
* Select the path to the SCSS Lint executable.
* Set the .scss-lint.yml file, or scss-lint will use the default settings.
* By default, SCSS Lint plugin annotate the editor with warning or error based on the SCSS Lint configuration, you can check the 'Treat all SCSS Lint issues as warnings' checkbox to display all issues from SCSS Lint as warnings.

Configuration:<br/>
![ESLint config](https://raw.githubusercontent.com/idok/scss-lint-plugin/master/docs/Settings.png)


Inspection:<br/>
![ESLint inline](https://raw.githubusercontent.com/idok/scss-lint-plugin/master/docs/Rule.png)


Analyze Code:<br/>
![ESLint inline](https://raw.githubusercontent.com/idok/scss-lint-plugin/master/docs/Inspection.png)