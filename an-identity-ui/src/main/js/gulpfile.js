var gulp = require('gulp');
var template_cache = require('gulp-angular-templatecache');
var clean = require('gulp-clean');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var watch = require('gulp-watch');

var target="../resources/public/dist/"

gulp.task('build-js', function() {
    return gulp.src(
    		[
    			'src/main.js',
    			'src/**/*.js'
    		]
    	)
        .pipe(concat('js/app.js'))
        .pipe(gulp.dest(target));
});

gulp.task('build-css', function(){
    return gulp.src(
	[
        'node_modules/bootstrap/dist/css/bootstrap.min.css',
        'node_modules/angular-date-picker/angular-date-picker.css',
        'app/css/*.css'
    	]
	)
        .pipe(concat('css/styles.css'))
        .pipe(gulp.dest(target));

});

gulp.task('build-fonts', function(){
    return gulp.src('node_modules/bootstrap/dist/fonts/*')
        .pipe(rename({
            dirname: 'fonts/'
        }))
        .pipe(gulp.dest(target));
});

gulp.task('build-deps', function() {
    var jsFiles = [
        'node_modules/jquery/dist/jquery.min.js',
        'node_modules/underscore/underscore-min.js',
        'node_modules/angular/angular.js',
        'node_modules/angular-route/angular-route.js',
        'node_modules/angular-upload/angular-upload.js',
        'node_modules/bootstrap/dist/js/bootstrap.js',
        'node_modules/restangular/dist/restangular.js',
        'node_modules/moment/min/moment-with-locales.js',
        'node_modules/angular-moment/angular-moment.js',
        'node_modules/angular-plugin/dist/js/angular-plugin.js',
        'node_modules/angular-date-picker/angular-date-picker.js',
        'node_modules/angular-date-picker/angular-date-picker.js',
        'node_modules/spin.js/spin.min.js',
        'node_modules/angular-spinner/dist/angular-spinner.min.js',
        'node_modules/bootbox/bootbox.min.js'
    ];
    return gulp.src(jsFiles)
        .pipe(concat('js/dependencies.js'))
        .pipe(gulp.dest(target));
});

gulp.task('build-templates', function() {
    return gulp.src("src/templates/**/*.html")
        .pipe(template_cache('**',{"standalone":false}))
        .pipe(concat("js/templates.js"))
        .pipe(gulp.dest(target));
});

gulp.task('clean', function () {
    return gulp.src(target, {read: false})
        .pipe(clean());
});

gulp.task('default', ['build']);

gulp.task('build', ['build-js', 'build-deps', 'build-fonts', 'build-templates', 'build-css']);

gulp.task('watch', ['build'], function () {
    gulp.watch("src/**/*.js", ['build']);
    gulp.watch("src/**/*.html", ['build']);
});
