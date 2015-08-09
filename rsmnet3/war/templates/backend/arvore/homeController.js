function homeController($scope,$http,$routeParams,$location)
{
 	
	$scope.homePrincipal = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/home/financeiro")).success(function(data){
 			$scope.registros = data;
			
			$scope.saldo_disponivel             = $scope.registros.saldo_disp;	
			$scope.saldo_bloqueado              = $scope.registros.bloqueado;	
 	 		
		});
		
		
			$http.get($scope.server("/home/financeiroInter")).success(function(data){
 			$scope.registros2 = data;
			
			$scope.saldo_disponivel_inter             = $scope.registros2.saldo_disp;	
			$scope.saldo_bloqueado_inter              = $scope.registros2.bloqueado;	
 	 		
		});
		
		
		$http.get($scope.server("/financeiro/psl_info")).success(function(data){
 			$scope.psl_info = data;
  		});
		
		
		
		
		$http.get($scope.server("/home/feeds")).success(function(data){
			    if (data.vazio==true) {
                   $scope.feeds = '';
 			 } else {
 				  $scope.feeds = data;	
				 }
 		});
		
		$http.get($scope.server("/home/rede")).success(function(data){
 			 if (data.vazio==true) {
                  $scope.redes = '';
				  $scope.redesqtd =  0;
 			 } else {
				  $scope.redes = data;
				   $scope.redesqtd =  data.length;
				 }
 		});
 		
	}
	
	
	
	 }