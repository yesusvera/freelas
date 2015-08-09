function carcadastroController($scope,$http,$routeParams,$location)
{
 	
  $scope.cadastrar = function(){
	            $scope.novo             = '';
				$scope.parabens         = '';
	            $scope.formcadastro     = '';
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
 				
  				 $http.get($scope.server("/carcadastro/estados")).success(function(data){
 			       $scope.registros_estado   = data;
  				  });
				  
				  $http.get($scope.server("/carcadastro/planos")).success(function(data){
 			       $scope.registros_plano   = data;
  				  }); 
				
  			   }	
 
	$scope.$watch("estado",function( cd_estado, nome ) {
	if(cd_estado === nome) {
	  return;
	}else{
			$http.get($scope.server("/carcadastro/cidades/"+cd_estado)).success(function(data){
			 $scope.registros_cidade  = data;
			});
		}
	 }); 
	 
		 
	$scope.$watch("capital",function( cd_estado, nome ) {
		if(cd_estado === nome) {
		  return;
		}else{
				$http.get($scope.server("/carcadastro/capitais/"+cd_estado)).success(function(data){
				 $scope.registros_capitais  = data;
				});
			}
	 }); 	 
	 
	 
	 
	  $scope.origem = function() {
		  
				$scope.cpf  = "";
				$scope.cnpj = "";
				$scope.formcadastro  = false;
				$scope.errorCpf                  = '';
				$scope.errorCnpj                 = '';
     	    
 		      if ($scope.origem_selecionada == '') {
			    $scope.origem_brasil     = false;
			    $scope.origem_exterior   = false;
				$scope.formcadastro  = false;
    		}else if ($scope.origem_selecionada == 1) {
				$scope.origem_brasil     = true;
				$scope.origem_exterior   = false;
				$scope.formcadastro      = true;
   		}else if ($scope.origem_selecionada == 2) {
			  $scope.origem_brasil       = false;
				$scope.origem_exterior   = true;
				$scope.formcadastro      = true;
   			 }
       }
	 
	 
 	 
   
  $scope.tipoConta = function() {
	  		   $scope.cpf  = "";
			   $scope.cnpj = "";
			  // $scope.formcadastro  = false;
			   $scope.errorCpf                  = '';
			   $scope.errorCnpj                 = '';
 	  
 		      if ($scope.tipo_selecionada == '') {
			   $scope.tipo_cpf   = false;
			   $scope.tipo_cnpj  = false;
    		}else if ($scope.tipo_selecionada == 1) {
			   $scope.tipo_cpf   = true;
			   $scope.tipo_cnpj  = false;
   		}else if ($scope.tipo_selecionada == 2) {
			   $scope.tipo_cpf   = false;
			   $scope.tipo_cnpj  = true;
   			 }
       }	
	   
  $scope.cpfConta = function() {
  		   if (!$scope.cpf) {
 		   }else{
		   $http.get($scope.server("/carcadastro/cpf/"+$scope.cpf)).success(function(data){
 					 $scope.errorCpf  = data.errors.cpf;
 					
					if (data.errors.formcadastro == true) {
					   $scope.formcadastro   = true;
					}else{
					   $scope.formcadastro  = false;
					}
  				});
		   }
        }
	   
  $scope.cnpjConta = function() {
  		   if (!$scope.cnpj) {
 		   }else{
		   $http.get($scope.server("/carcadastro/cnpj/"+$scope.cnpj)).success(function(data){
 					 $scope.errorCnpj  = data.errors.cnpj;
 					
					if (data.errors.formcadastro == true) {
					   $scope.formcadastro   = true;
					}else{
					   $scope.formcadastro  = false;
					}
   				});
		   }
        }	   		   
	    
 
  
$scope.salvar = function() {
	
	$scope.enviando              = true;
	 
	var dados = $('#form').serialize();		
			
	$http({
        method  : 'POST',
        url     : $scope.server("/carcadastro/salvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
			
             if (!data.sucesso) {
				    $scope.enviando                  = false;
					$scope.errorChave                = data.errors.chave;
					$scope.errorPlano                = data.errors.plano;
					$scope.errorTipo_selecionada     = data.errors.tipo_selecionada;
					$scope.errorCpf                  = data.errors.cpf;
					$scope.errorCnpj                 = data.errors.cnpj;
					$scope.errorNome                 = data.errors.nome;
					$scope.errorRazaosocial          = data.errors.razaosocial;
					$scope.errorTelefone             = data.errors.telefone;
					$scope.errorCelular              = data.errors.celular;
					$scope.errorData_nascimento      = data.errors.data_nascimento;
					$scope.errorSexo                 = data.errors.sexo;
					$scope.errorEmail                = data.errors.email;
					$scope.errorEmail1               = data.errors.email1;
					$scope.errorCep                  = data.errors.cep;
					$scope.errorLogradouro           = data.errors.logradouro;
					$scope.errorNumero               = data.errors.numero; 
					$scope.errorComplemento          = data.errors.complemento;
					$scope.errorBairro               = data.errors.bairro;
					$scope.errorEstado               = data.errors.estado;
					$scope.errorCidade               = data.errors.cidade;
					$scope.errorAbrangencia          = data.errors.abrangencia;
					$scope.errorUsuario              = data.errors.usuario;
					$scope.errorSenha                = data.errors.senha;
					
					
					//exterior
					$scope.errorRegID                = data.errors.inter_regid;
					$scope.errorNome2                = data.errors.nome2;
					$scope.errorTelefone             = data.errors.telefone2;
					$scope.errorCelular              = data.errors.celular2;
					$scope.errorInterCidade          = data.errors.inter_cidade;
					$scope.errorInterEstado          = data.errors.inter_estado;
					$scope.errorPais                 = data.errors.pais;
					
					 
				  
   				    $scope.retorno_error             = data.retorno_error; 
					 
                   
			 } else {
				    $scope.enviando                  = false;
					$scope.errorChave                = '';
					$scope.errorPlano                = '';
					$scope.errorTipo_selecionada     = '';
					$scope.errorCpf                  = '';
					$scope.errorCnpj                 = '';
					$scope.errorNome                 = '';
					$scope.errorRazaosocial          = '';
					$scope.errorTelefone             = '';
					$scope.errorCelular              = '';
					$scope.errorData_nascimento      = '';
					$scope.errorSexo                 = '';
					$scope.errorEmail                = '';
					$scope.errorEmail1               = '';
					$scope.errorCep                  = '';
					$scope.errorLogradouro           = '';
					$scope.errorNumero               = '';
					$scope.errorComplemento          = '';
					$scope.errorBairro               = '';
					$scope.errorEstado               = '';
					$scope.errorCidade               = '';
					$scope.errorAbrangencia          = '';
					$scope.errorUsuario              = '';
					$scope.errorSenha                = '';
					
					// exterior
					$scope.errorRegID                = '';
					$scope.errorNome2                = '';
					$scope.errorTelefone             = '';
					$scope.errorCelular              = '';
					$scope.errorInterCidade          = '';
					$scope.errorInterEstado          = '';
					$scope.errorPais                 = '';
					
					 
					$scope.retorno          = data.retorno;
					$scope.retorno_error    = data.retorno_error;
					$scope.novo             = data.novo;
					$scope.novo_codigo      = data.novo_codigo;
					$scope.novo_boleto      = data.novo_boleto;
					$scope.novo_email       = data.novo_email;
					$scope.novo_usuario     = data.novo_usuario;
					$scope.novo_senha       = data.novo_senha; 
					$scope.novo_valor       = data.novo_valor;  
					
					 
				  
				  
				 }
        }); 
}



}