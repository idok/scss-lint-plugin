# grunt-packages- v0.1.8 [![Build Status](https://travis-ci.org/gruntjs/grunt-contrib-uglify.png?branch=master)](https://travis-ci.org/gruntjs/grunt-contrib-uglify)

> Create a require-js packages project.



## Getting Started
This plugin requires Grunt `^0.4.0`

If you haven't used [Grunt](http://gruntjs.com/) before, be sure to check out the [Getting Started](http://gruntjs.com/getting-started) guide, as it explains how to create a [Gruntfile](http://gruntjs.com/sample-gruntfile) as well as install and use Grunt plugins. Once you're familiar with that process, you may install this plugin with this command:

```shell
npm install grunt-packages --save-dev
```

Once the plugin has been installed, it may be enabled inside your Gruntfile with this line of JavaScript:

```js
grunt.loadNpmTasks('grunt-packages');
```

## buildConfig task
_Run this task with the `grunt buildConfig` command._

This task builds an aggregated file for each package and minifies it using uglify js.
Task targets, files and options may be specified according to the grunt [Configuring tasks](http://gruntjs.com/configuring-tasks) guide.

## verify task
_Run this task with the `grunt verify` command._

This task checks the project structure and informs if it does not comply with the expected structure.
Task targets, files and options may be specified according to the grunt [Configuring tasks](http://gruntjs.com/configuring-tasks) guide.

## deps-tree task
_Run this task with the `grunt deps-tree` command._

This task prints the dependencies tree of the entire project.
Task targets, files and options may be specified according to the grunt [Configuring tasks](http://gruntjs.com/configuring-tasks) guide.

## usage task
_Run this task with the `grunt usage:[moduleId]` command._
This task takes a moduleId/packageId as a parameter and display all the files that import the moduleId/packageId.

### Options

This task primarily delegates to [UglifyJS2][], so please consider the [UglifyJS documentation][] as required reading for advanced configuration.

[UglifyJS2]: https://github.com/mishoo/UglifyJS2
[UglifyJS documentation]: http://lisperator.net/uglifyjs/

#### base
Type: `String`
Default: ``

Project base path.

#### sourceMain
Type: `String`  
Default: `src/main`

Path to source main files e.g. if the project structure is root/packages/package1/src/main/package1.js this should be src/main,
if the path is root/packages/package1/src/main/javascript/package1.js this should be src/main/javascript.

#### rjsMainTemplate
Type: `String`  
Default: `app/main-r.template.js`

Path to the requirejs data-main template file

#### rjsMain
Type: `String`
Default: `app/main-r.js`

Path to the requirejs data-main output file.

#### testMainTemplate
Type: `String`  
Default: `js/test/test-main.template.js`

Path to the test-main template file

#### testMain
Type: `String`
Default: `js/test/test-main.js`

Path to the test-main output file.

### Usage examples

#### Basic configuration

This configuration will build the project using the default options.

```js
// Project configuration.
grunt.initConfig({
    packages: {
        base: '',
        sourceMain: 'src/main',
        rjsMainTemplate: 'app/main-r.template.js',
        rjsMainMin: 'app/main-r.min.js',
        rjsMain: 'app/main-r.js'
    }
});
```

## Release History

 * 2014-06-11   v0.1.0   Work in progress, not yet officially released.
 * 2014-06-19   v0.1.1   Now rendering test-main.

#### Updating version
```bash
# Change version in package.json
git commit package.json -m'version 0.1.1'
git tag 0.1.1
git push
git push --tag
# Update grunt-packages version in the dependent app package.json
```


---

Task submitted by [Infra team](http://wix.com)

*This file was generated on Sat Mar 01 2014 20:36:24.*
