angular.module('cpa', [ 'ngRoute' ])
	.config(function($routeProvider) {
		$routeProvider.when('/home', {templateUrl : 'partials/home.html'})
		$routeProvider.when('/teachings', {templateUrl : 'partials/teachings.html'})
		$routeProvider.when('/cursus', {templateUrl : 'partials/cursus.html'})
		.otherwise({redirectTo : '/home'});
	});