var gulp = require('gulp');
var template_cache = require('gulp-angular-templatecache');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var watch = require('gulp-watch');

var target="../resources/public/otp"

gulp.task('build-js', function() {
    return gulp.src(
    		[
    			'module.js',
    			'src/**/*.js'
    		]
    	)
        .pipe(concat('js/module.js'))
        .pipe(gulp.dest(target));
});

gulp.task('build-templates', function() {
    return gulp.src("templates/**/*.html")
        .pipe(template_cache('**',{"standalone":false, root: "otp/templates"}))
        .pipe(concat("js/templates.js"))
        .pipe(gulp.dest(target));
});

gulp.task('default', ['build']);

gulp.task('build', ['build-js', 'build-templates' ]);

gulp.task('watch', ['build'], function () {
    gulp.watch("src/**/*.js", ['build']);
    gulp.watch("templates/**/*.html", ['build']);
});
