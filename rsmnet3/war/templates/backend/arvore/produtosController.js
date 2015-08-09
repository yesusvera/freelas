function produtosController($scope,$http,$routeParams,$location)
{
 	
	$scope.rede = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/produtos/redeJson")).success(function(data){
			    if (data.vazio==true) {
                   $scope.redecred = '';
 			 } else {
 				  $scope.redecred = data;	
				 }
 		});
		
		
 	}
	
	$scope.manuais = function(){
		$scope.showLoader();
 	}
	
}