var OrganisationsController = function($scope, $http){
	$scope.filter = {};
	
	
	
	$scope.find = function(){
		$http.get("http://localhost:8080/api/organisations?name="+encodeURI($scope.filter.name)).success(function(responseBody){
			$scope.results = responseBody;
		});
	};
	
	
	
	$scope.delete = function(org){
		
		$http.delete("http://localhost:8080/api/organisations/"+org.id).success(function(){
			var indexOfDeletedItem = $scope.results.indexOf(org);
			$scope.results.splice(indexOfDeletedItem, 1);
		});
	};
	
	
	// mode développement : 
	$scope.filter = {name:"%%"};
	$scope.find();
};

var OrganisationController = function($scope, $http, $location, $routeParams){
	if($routeParams=="new"){
		// création
		$scope.organisation = {}
	}
	else{
		$http.get("http://localhost:8080/api/organisations/"+$routeParams.id).success(function(responseBody){
			$scope.organisation = responseBody;
		});
	}
	
	$scope.save = function(){
		if($scope.organisation.id==null){
			$http.post("http://localhost:8080/api/organisations", $scope.organisation).success(function(responseBody){
				$location.path("/organisations");
			})
		}
		else{
			$http.put("http://localhost:8080/api/organisations/"+$scope.organisation.id, $scope.organisation).success(function(responseBody){
				$location.path("/organisations");
			})
		}
	}
};

angular.module('cpa', [ 'ngRoute' ])
	.controller('OrganisationsController', OrganisationsController)
	.controller('OrganisationController', OrganisationController)
	.config(function($routeProvider) {
		$routeProvider.when('/organisations', {templateUrl : 'partials/organisations.html'})
		$routeProvider.when('/organisations/:id', {templateUrl : 'partials/organisation.html'});
	});

