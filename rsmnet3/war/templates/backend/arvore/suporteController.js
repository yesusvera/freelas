function suporteController($scope,$http,$routeParams,$location)
{
	
  
$scope.suporte = function(){
	
	if($routeParams.id){
	 
	$scope.suporteEdit($routeParams.id); 
	 
	}else{
	
	
		$scope.showLoader();
 		
		$http.get($scope.server("/suporte/suporteJson")).success(function(data){
 				    if (!data.error) {
 				       $scope.registros  = data;
				   }else{
 					   }
     		});
		
		$http.get($scope.server("/suporte/suporteUltimo")).success(function(data){
			
			if (!data.error) {
 				       $scope.suporteEdit(data.cd_suporte); 
				   }else{
 					   }
			
 			 
   		});
	}
	}
	
$scope.suporteAdd = function(){
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
				$scope.errorTitulo       = '';
				$scope.errorMensagem     = '';
 				
  			   }

$scope.suporteEdit = function(index){
 				
 			    $scope.mensagem              = '';
				$scope.registros_respostas   = '';
				
 				$http.get($scope.server("/suporte/suporteJson")).success(function(data){
					if (!data.error) {
						$scope.registros  = data;
					}else{
					}
				});
				
				
	 
 				$http.get($scope.server("/suporte/suporteEditarJson/"+index)).success(function(data){
				  $scope.registro_suporte  = data;
   				});
  				
				$http.get($scope.server("/suporte/respostasJson/"+index)).success(function(data){
				   
				  
				    if (!data.error) {
 				      $scope.registros_respostas  = data;
				   }else{
 					   }
				
				});
				
				
			  }	
 

$scope.suporteSalvar = function() {
	 
	var dados = $.param({ 
 				titulo:     $("input:text[name='titulo']").val(),
				mensagem:   $("textarea#mensagem").val(),
    			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/suporte/suporteSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
				   
                  $scope.errorTitulo       = data.errors.titulo;
				  $scope.errorMensagem     = data.errors.mensagem;
 				  
   				  $scope.retorno_error     = data.retorno_error; 
                   
			 } else {
				  
 				  $scope.errorTitulo       = '';
				  $scope.errorMensagem     = '';
 				 
 				  $scope.retorno          = data.retorno;
				  $scope.retorno_error    = data.retorno_error;
				  
				  if(data.modal==true){
				    $("#editModal").modal("hide");
				    $scope.suporte(); 
				  }
				 }
        }); 
}


$scope.suporteResponder = function() {
	 
	var dados = $.param({ 
					codigo:      $("input:hidden[name='codigo']").val(),
					mensagem:    $("input:text[name='mensagem']").val()
    			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/suporte/suporteResponder"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
				   
                  $scope.errorCodigo       = data.errors.codigo;
				  $scope.errorMensagem     = data.errors.mensagem;
 				  
   				  $scope.retorno_error     = data.retorno_error; 
                   
			 } else {
				  
 				  $scope.errorCodigo       = '';
				  $scope.errorMensagem     = '';
 				 
 				  $scope.retorno          = data.retorno;
				  $scope.retorno_error    = data.retorno_error;
   				  $scope.suporteEdit(data.codigo); 
				 
				 }
        }); 
}


	
	 } 