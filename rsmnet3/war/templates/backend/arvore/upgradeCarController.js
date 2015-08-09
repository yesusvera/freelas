function upgradeCarController($scope,$http,$routeParams,$location)
{
	
$scope.upgrade = function(){
 		$scope.showLoader();
		$scope.consulta_retorno  = false;
		$scope.plano_selecionada = 1;
		
		$http.get($scope.server("/upgrade_car/Json")).success(function(data){
 			$scope.registros  = data;
   		});
		
		$http.get($scope.server("/home/financeiro")).success(function(data){
 			$scope.saldos = data;
 			$scope.saldo_disponivel             = $scope.saldos.saldo_disp;	
			$scope.saldo_bloqueado              = $scope.saldos.bloqueado;			
		});
		
  	}
	 	
$scope.newPlano = function(plano) {
  	 $http.get($scope.server("/upgrade_car/consulta/"+plano)).success(function(data){
 			$scope.consulta_retorno  = true;
			$scope.consulta  = data;
   		});
	 
}

$scope.upgradeSalvar = function() {
	 $scope.novo             = false;	
	$scope.enviando          = true;
	$scope.retorno_error     = '';
 	
	var dados = $.param({ 
			cd_plano:  $("input:hidden[name='cd_plano']").val() 
 		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/upgrade_car/salvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.sucesso) {
		        $scope.novo             = false;
 			   $scope.enviando          = false;
 			   $scope.retorno_error     = data.retorno_error; 
 	 } else {
		            $scope.enviando           = false;
 			  
 					$scope.retorno_error    = data.retorno_error;
					$scope.novo             = true;
					$scope.novo_boleto      = data.novo_boleto;
					$scope.novo_email       = data.novo_email;
					$scope.codigo_boleto     = data.codigo_boleto;
		   
 		 }
	}); 
	}	 	
} 