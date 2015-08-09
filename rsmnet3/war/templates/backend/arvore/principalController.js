function principalController($scope,$http,$routeParams,$location)
{
	$scope.notificacoes_suporte = function(){
 		$http.get($scope.server("/suporte/suporteJsonNaoLidos")).success(function(data){
 				    if (!data.error) {
 				       $scope.notificacoes_suporte_registros  = data;
					   $scope.notificacoes_suporte_qtd        =  data.length;
					   
				   }else{
					   
 					   }
     		});
    
	 }
	
	
$scope.limpar = function(){
				//alert('oi'); 
				
				$scope.not_sup = true;
				 
 				
  			   }	 
	
	
	
	
	
	
	
	
	
  }