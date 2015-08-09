function contratoController($scope,$http,$routeParams,$location)
{
	$scope.contrato1 = function(){
		$scope.registros1 = '';
		 
 		$http.get($scope.server("/contrato/arquivos_lista/1")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros1 = '';
		 } else {
			   $scope.registros1 = data;	
			 }
		});
	}	
	
	$scope.contrato2 = function(){
		
		$scope.registros2 = '';
 		$http.get($scope.server("/contrato/arquivos_lista/2")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros2 = '';
		 } else {
			   $scope.registros2 = data;	
			 }
		});
		 
	}	
	
	$scope.contrato3 = function(){
		  $scope.registros3 = '';
 		$http.get($scope.server("/contrato/arquivos_lista/3")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros3 = '';
		 } else {
			   $scope.registros3 = data;	
			 }
		});
		 
	}	
	
	$scope.contrato4 = function(){
		 $scope.registros4 = '';
 		$http.get($scope.server("/contrato/arquivos_lista/4")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros4 = '';
		 } else {
			   $scope.registros4 = data;	
			 }
		});
		 
	}
	
	
	
	
		$scope.contrato5 = function(){
		 $scope.registros5 = '';
 		$http.get($scope.server("/contrato/arquivos_lista/5")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros5 = '';
		 } else {
			   $scope.registros5 = data;	   
			 }
		});
		 
	}
	
	
	
	
		
	
	$scope.excluir=function(tipo,id){
		//alert(tipo+"|"+id+"|"+arquivo);
 		$http.get($scope.server("/contrato/arquivos_excluir/"+id)).success(function(data){
 			//alert(data.erro+""+data.msg+""+data.variavel);
			 if (data.erro==true) {
			  // alert(data.msg);
			   if(tipo == 1){
				   $scope.contrato1();   
			   }else if(tipo==2){
				   $scope.contrato2();  
			   }else if(tipo==3){
				   $scope.contrato3();  
			   }else if(tipo==4){
				   $scope.contrato4();  
			   }
			   
			   
			 }else{
				 $scope.contrato3();
			 }
		 
		});
	}
	
	
	
	
	
	
	$scope.girar = function(tipo,id){
		//alert(tipo+"|"+id+"|"+arquivo);
 		
		 
		 $http.get($scope.server("/contrato/girar/"+id)).success(function(data){
 			 if (data.erro==true) {
			  // alert(data.msg);
			   if(tipo == 1){
				   $scope.contrato1();   
			   }else if(tipo==2){
				   $scope.contrato2();  
			   }else if(tipo==3){
				   $scope.contrato3();  
			   }else if(tipo==4){
				   $scope.contrato4();  
			   }
			   
			   
			 }else{
				 $scope.contrato3();
			 }
		 
		}); 
	}
	
	
	
	
	
	
	
}