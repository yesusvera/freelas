function redeController($scope,$http,$routeParams,$location)
{
	
 	$scope.pontos_binario = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/rede/pontos_binarioJson")).success(function(data){
			 if (data.vazio==true) {
			   $scope.pontos = '';
		 } else {
			   $scope.pontos = data;	
			 }
		});
		
		$http.get($scope.server("/rede/binario_total")).success(function(data){
			if (data.vazio==true) {
			   $scope.binario_total_esquerda  = 0;	
			   $scope.binario_total_direita   = 0;
			   $scope.binario_total_total     = 0;	
		 } else {
			   $scope.binario_total_esquerda  = data.esquerda;	
			   $scope.binario_total_direita   = data.direita;
			   $scope.binario_total_total     = data.total;	
			 }
		});
		
		$http.get($scope.server("/rede/binario_total_hoje")).success(function(data){
			if (data.vazio==true) {
			   $scope.binario_total_hoje_esquerda  = 0;	
			   $scope.binario_total_hoje_direita   = 0;
			   $scope.binario_total_hoje_total     = 0;	
		 } else {
			   $scope.binario_total_hoje_esquerda  = data.esquerda;	
			   $scope.binario_total_hoje_direita   = data.direita;
			   $scope.binario_total_hoje_total     = data.total;	
			 }
		});	
		
		
		$http.get($scope.server("/rede/binario_total_ontem")).success(function(data){
			if (data.vazio==true) {
			   $scope.binario_total_ontem_esquerda  = 0;	
			   $scope.binario_total_ontem_direita   = 0;
			   $scope.binario_total_ontem_total     = 0;	
		 } else {
			   $scope.binario_total_ontem_esquerda  = data.esquerda;	
			   $scope.binario_total_ontem_direita   = data.direita;
			   $scope.binario_total_ontem_total     = data.total;	
			 }
		});	
				
	 
		
 	}	
	
	
	
$scope.binaria = function(){
	if($routeParams.id){
 	    $scope.showLoader();
   		$http.get($scope.server("/rede/binariaJson/"+$routeParams.id)).success(function(data){
 				    if (!data.error) {
 				       $scope.registros  = data;
					  // console.log(data);
  				   }else{
  					   }
     		});
		
	    
	}else{
		   $scope.showLoader();
   		$http.get($scope.server("/rede/binariaJson")).success(function(data){
 				    if (!data.error) {
 				       $scope.registros  = data;
					   //console.log(data);
  				   }else{
					   
  					   }
     		});
		
		 
		 }
	
	
	
	
	
	
	 
	
	
	
	}
	
	
$scope.chave = function(){
 	
 		$scope.showLoader();
   		$http.get($scope.server("/rede/chaveJson")).success(function(data){
 				    if (!data.error) {
 					   $scope.chave_selecionada = data.posicao;
   				   }else{
  					   }
     		});
 	}	
   
 
     $scope.chaveMudar = function() {
		 if ($scope.chave_selecionada === '') {
		  return;
		}else{
		 		$http.get($scope.server("/rede/chaveMudar/"+$scope.chave_selecionada)).success(function(data){
				 
 				 if (!data.sucesso) {
   				  $scope.retorno_error    = data.retorno_error;
             
			 } else {
				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
				 
				 
				 
				 
				 
				 
				 
				 
				 
				 
				});
		    }
		 
      };
 
 
 
 
	
	
	
} 