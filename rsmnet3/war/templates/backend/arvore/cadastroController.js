function cadastroController($scope,$http,$routeParams,$location)
{
	
	
  $scope.cadastrar = function(){
	            $scope.novo             = '';
				$scope.parabens         = '';
	            $scope.formcadastro     = '';
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
			/*	$scope.errorBanco       = '';
				$scope.errorAgencia     = '';
				$scope.errorConta       = '';
				$scope.errorTipo        = '';	*/
 				 
 				 $http.get($scope.server("/cadastro/estados")).success(function(data){
 			       $scope.registros_estado   = data;
  				  });
				  
				  $http.get($scope.server("/cadastro/planos")).success(function(data){
 			       $scope.registros_plano   = data;
  				  }); 
				
  			   }	
 
	$scope.$watch("estado",function( cd_estado, nome ) {
	if(cd_estado === nome) {
	  return;
	}else{
			$http.get($scope.server("/cadastro/cidades/"+cd_estado)).success(function(data){
			 $scope.registros_cidade  = data;
			});
		}
	 }); 
	 
		 
	$scope.$watch("capital",function( cd_estado, nome ) {
		if(cd_estado === nome) {
		  return;
		}else{
				$http.get($scope.server("/cadastro/capitais/"+cd_estado)).success(function(data){
				 $scope.registros_capitais  = data;
				});
			}
	 }); 	 
 	 
   
  $scope.tipoConta = function() {
	  		   $scope.cpf  = "";
			   $scope.cnpj = "";
			   $scope.formcadastro  = false;
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
		   $http.get($scope.server("/cadastro/cpf/"+$scope.cpf)).success(function(data){
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
		   $http.get($scope.server("/cadastro/cnpj/"+$scope.cnpj)).success(function(data){
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
	
 	var dados = $.param({ 
	            chave:            $("#chave option:selected").val(),
				plano:            $("#plano option:selected").val(),
  				tipo_selecionada: $("input[type='radio'][name='tipo_selecionada']:checked").val(),
				cpf:              $("input:text[name='cpf']").val(),
				cnpj:             $("input:text[name='cnpj']").val(),
				nome:             $("input:text[name='nome']").val(),
				razaosocial:      $("input:text[name='razaosocial']").val(),
				telefone:         $("input:text[name='telefone']").val(),
				celular:          $("input:text[name='celular']").val(),
				data_nascimento:  $("input:text[name='data_nascimento']").val(),
				sexo:             $("#sexo option:selected").val(),
				email:            $("input:text[name='email']").val(),
				email1:           $("input:text[name='email1']").val(),
				cep:              $("input:text[name='cep']").val(),
				logradouro:       $("input:text[name='logradouro']").val(),
				numero:           $("input:text[name='numero']").val(),
				complemento:      $("input:text[name='complemento']").val(),
				bairro:           $("input:text[name='bairro']").val(),
				estado:           $("#estado option:selected").val(),
				cidade:           $("#cidade option:selected").val(),
				abrangencia:      $("#abrangencia option:selected").val(),
				usuario:          $("input:text[name='usuario']").val(),
				senha:            $("input:password[name='senha']").val(),
				senha1:           $("input:password[name='senha1']").val() 
			 
   			});	   
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/cadastro/salvar"),
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
					
					
					$scope.retorno          = data.retorno;
					$scope.retorno_error    = data.retorno_error;
					$scope.novo             = data.novo;
					$scope.novo_codigo      = data.novo_codigo;
					$scope.novo_boleto      = data.novo_boleto;
					$scope.novo_email       = data.novo_email;
					$scope.novo_usuario     = data.novo_usuario;
					$scope.novo_senha       = data.novo_senha;
				  
				  
				 }
        }); 
}



}