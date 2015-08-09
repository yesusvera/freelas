function agendamentoController($scope,$http,$routeParams,$location)
{
	
  
$scope.agendamento = function(){
	
	if($routeParams.id){
	 
	$scope.agendamentoEdit($routeParams.id); 
	 
	}else{
	
	
		$scope.showLoader();
 		
		$http.get($scope.server("/agendamento/agendamentoJson")).success(function(data){
 				    if (!data.error) {
 				       $scope.registros  = data;
				   }else{
 					   }
     		});
		
		$http.get($scope.server("/agendamento/agendamentoUltimo")).success(function(data){
			
			if (!data.error) {
 				       $scope.agendamentoEdit(data.cd_agendamento); 
				   }else{
 					   }
			
 			 
   		});
	}
	}
	
$scope.agendamentoAdd = function(){
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
				$scope.errorTitulo       = '';
				$scope.errorMensagem     = '';
 				
  			   }

$scope.agendamentoEdit = function(index){
 				
 			    $scope.mensagem              = '';
				$scope.registros_respostas   = '';
				
 				$http.get($scope.server("/agendamento/agendamentoJson")).success(function(data){
					if (!data.error) {
						$scope.registros  = data;
					}else{
					}
				});
				
				
	 
 				$http.get($scope.server("/agendamento/agendamentoEditarJson/"+index)).success(function(data){
				  $scope.registro_agendamento  = data;
   				});
  				
				$http.get($scope.server("/agendamento/respostasJson/"+index)).success(function(data){
				   
				  
				    if (!data.error) {
 				      $scope.registros_respostas  = data;
				   }else{
 					   }
				
				});
				
				
			  }	
 

$scope.agendamentoSalvar = function() {
	 
	var dados = $.param({ 
 				titulo:     $("input:text[name='titulo']").val(),
				mensagem:   $("textarea#mensagem").val(),
    			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/agendamento/agendamentoSalvar"),
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
				    $scope.agendamento(); 
				  }
				 }
        }); 
}


$scope.agendamentoResponder = function() {
	 
	var dados = $.param({ 
					codigo:      $("input:hidden[name='codigo']").val(),
					mensagem:    $("input:text[name='mensagem']").val()
    			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/agendamento/agendamentoResponder"),
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
   				  $scope.agendamentoEdit(data.codigo); 
				 
				 }
        }); 
}


	
	 } 