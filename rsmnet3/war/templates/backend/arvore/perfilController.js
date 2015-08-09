function perfilController($scope,$http,$routeParams,$location)
{
 
	$scope.dados = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/perfil/dadosJson")).success(function(data){
 			$scope.registro  = data;
			
		      if(data['sexo']==1){
				 $scope.registros_sexo = [
				  {nome: "Masculino", id: "1"},
				  {nome: "Feminino", id: "2"},
				];
 			 
			}else if(data['sexo']==2){
 				$scope.registros_sexo = [
				  {nome: "Feminino", id: "2"},
				  {nome: "Masculino", id: "1"} 
				];
			
 			 }else{
				$scope.registros_sexo = [
					{nome: "Selecione", id: ""},
					{nome: "Masculino", id: "1"}, 
					{nome: "Feminino", id: "2"}
 				]; 
 			 }
 			 
			 
  		});
 		
		  
		
	}
	 
 
	 
$scope.dadosSalvar = function() {
	
	var dados = $.param({ 
				lasamerica:     $("input:text[name='lasamerica']").val(),
				nome:     $("input:text[name='nome']").val(),
				cpf:      $("input:text[name='cpf']").val(),
				telefone: $("input:text[name='telefone']").val(),
				celular:  $("input:text[name='celular']").val(),
				data_nascimento: $("input:text[name='data_nascimento']").val(),
				sexo:     $("#sexo option:selected").val(),
				email:    $("input:text[name='email']").val(),
				email1:   $("input:text[name='email1']").val(),
				senha:    $("input:password[name='senha']").val(),
				senha1:   $("input:password[name='senha1']").val(),
 				});	 
 	 
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/dadosSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
 
            if (!data.sucesso) {
 					$scope.errorLas       = data.errors.lasamerica;
					$scope.errorNome       = data.errors.nome;
					$scope.errorCpf        = data.errors.cpf;
					$scope.errorTelefone   = data.errors.telefone; 
					$scope.errorCelular    = data.errors.celular;
					$scope.errorData_nascimento = data.errors.data_nascimento;
					$scope.errorSexo       = data.errors.sexo;
					$scope.errorEmail      = data.errors.email;
					$scope.errorEmail1     = data.errors.email1;
					$scope.errorSenha      = data.errors.senha;
					$scope.errorSenha1     = data.errors.senha1;
				  
  				    $scope.retorno_error    = data.retorno_error;
             
			 } else {
				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
        });
}
	 
	
	
//// endereco
	   $scope.endereco = function(){
	  	
 		$scope.showLoader();
 		$http.get($scope.server("/perfil/enderecoJson")).success(function(data){
 			
			$scope.registro  = data;
			
 			if(data['cd_estado'] ==''){
   				  
				   $http.get($scope.server("/perfil/enderecoEstadoJson")).success(function(data2){
 			       $scope.registros_estado   = data2;
  				  });
				   
			}else{
			
				   $http.get($scope.server("/perfil/enderecoEstadoJson")).success(function(data2){
 			       $scope.registros_estado   = data2;
  				  });
			 
				  
 			
			}			
   		});
 		
	}
	
$scope.enderecoSalvar = function() {
	
	var dados = $.param({ 
				cep:         $("input:text[name='cep']").val(),
				logradouro:  $("input:text[name='logradouro']").val(),
				numero:      $("input:text[name='numero']").val(),
				complemento: $("input:text[name='complemento']").val(),
 				bairro:      $("input:text[name='bairro']").val(),
				estado:      $("#estado option:selected").val(),
				cidade:      $("#cidade option:selected").val(),
				estadomudar: $('#check').is(':checked')
  			});	 
 	 
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/enderecoSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
 
            if (!data.sucesso) {
                  $scope.errorCep  = data.errors.cep;
				  $scope.errorLogradouro = data.errors.logradouro;
				  $scope.errorNumero = data.errors.numero;
				  $scope.errorBairro = data.errors.bairro;
 				  $scope.errorEstado  = data.errors.estado;
				  $scope.errorCidade  = data.errors.cidade;  
   				  $scope.retorno_error    = data.retorno_error;
             
			 } else {
				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
        });
}


// senha

$scope.senhaSalvar = function() {
	
	var dados = $.param({ 
				back_senha:       $("input:password[name='back_senha']").val(),
				back_novasenha:   $("input:password[name='back_novasenha']").val(),
				back_novasenha1:  $("input:password[name='back_novasenha1']").val(),
   			});	 
 	 
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/senhaSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
                  $scope.errorSenha  = data.errors.back_senha;
				  $scope.errorNovasenha = data.errors.back_novasenha;
				  $scope.errorNovasenha1 = data.errors.back_novasenha1;
   				  $scope.retorno_error    = data.retorno_error;
             
			 } else {
				 
				  $scope.errorSenha      = '';
				  $scope.errorNovasenha  = '';
				  $scope.errorNovasenha1 = '';
				 
 				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
        });
}


// chave

$scope.chaveSalvar = function() {
	
	var dados = $.param({ 
				back_senha:       $("input:password[name='back_senha']").val(),
				back_novasenha:   $("input:password[name='back_novasenha']").val(),
				back_novasenha1:  $("input:password[name='back_novasenha1']").val(),
   			});	 
 	 
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/chaveSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
                  $scope.errorSenha       = data.errors.back_senha;
				  $scope.errorNovasenha   = data.errors.back_novasenha;
				  $scope.errorNovasenha1  = data.errors.back_novasenha1;
   				  $scope.retorno_error    = data.retorno_error;
             
			 } else {
				 
				  $scope.errorSenha      = '';
				  $scope.errorNovasenha  = '';
				  $scope.errorNovasenha1 = '';
				 
 				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
        });
}


/// bancos
$scope.bancos = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/perfil/bancosJson")).success(function(data){
 			 if (data.vazio==true) {
                   $scope.registros = '';
 			 } else {
 				  $scope.registros = data;	
				 }
   		});
  
	}
	
 
$scope.bancoAdd = function(){
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
				$scope.errorBanco       = '';
				$scope.errorAgencia     = '';
				$scope.errorConta       = '';
				$scope.errorTipo        = '';	
 				 
				$scope.registro_banco = null;
				$scope.registros_tipo = [
					{nome: "Selecione", id: ""},
					{nome: "Poupança", id: "2"},
					{nome: "Corrente", id: "1"},
				];
				
				$http.get($scope.server("/perfil/bancosTodosJson/")).success(function(data){
				  $scope.registros_TodosBanco  = data;
				});
				
  			   }

$scope.bancoEdit = function(index){
	
	            $scope.retorno          = '';
				$scope.retorno_error    = ''; 
	
				$scope.errorBanco       = '';
				$scope.errorAgencia     = '';
				$scope.errorConta       = '';
				$scope.errorTipo        = '';
 	            
	 		    $scope.registro_banco = null;
 				$scope.registros_tipo = null;
				
				$http.get($scope.server("/perfil/bancosEditarJson/"+index)).success(function(data){
				$scope.registro_banco  = data;
				
				if(data['tipo']==1){
				
				$scope.registros_tipo = [
					{nome: "Corrente", id: "1"},
					{nome: "Poupança", id: "2"},
				];
				
				}else if(data['tipo']==2){
				$scope.registros_tipo = [
				    {nome: "Poupança", id: "2"},
					{nome: "Corrente", id: "1"},
					 
				];
				
				}else{
					
				$scope.registros_tipo = [
					{nome: "Selecione", id: ""},
					{nome: "Poupança", id: "2"},
					{nome: "Corrente", id: "1"},
				]; 
				}
				 
  				});
				
 				
				$http.get($scope.server("/perfil/bancosTodosJson/")).success(function(data){
				  $scope.registros_TodosBanco  = data;
				});
				
				
			  }	
 

$scope.bancoSalvar = function() {
	 
	var dados = $.param({ 
				codigo:      $("input:hidden[name='codigo']").val(),
 				banco:       $("#banco option:selected").val(),
				agencia:     $("input:text[name='agencia']").val(),
				agencia_dg:  $("input:text[name='agencia_dg']").val(),
				conta:       $("input:text[name='conta']").val(),
				conta_dg:    $("input:text[name='conta_dg']").val(),
				tipo:        $("#tipo option:selected").val()
   			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/bancoSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
				   
                  $scope.errorBanco       = data.errors.banco;
				  $scope.errorAgencia     = data.errors.agencia;
				  $scope.errorConta       = data.errors.conta;
				  $scope.errorTipo        = data.errors.tipo;
				  
   				  $scope.retorno_error    = data.retorno_error; 
                   
			 } else {
				  
 				  $scope.errorBanco       = '';
				  $scope.errorAgencia     = '';
				  $scope.errorConta       = '';
				  $scope.errorTipo        = '';
				 
 				  $scope.retorno          = data.retorno;
				  $scope.retorno_error    = data.retorno_error;
				  
				  if(data.modal==true){
				    $("#editModal").modal("hide");
				    $scope.bancos(); 
				  }
				 }
        }); 
}



/////////////  dependentes

/// bancos
$scope.dependentes = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/perfil/dependentesJson")).success(function(data){
 			 if (data.vazio==true) {
                   $scope.registros = '';
 			 } else {
 				  $scope.registros = data;	
				 }
   		});
  
	}
	
 
$scope.dependentesAdd = function(){
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
				$scope.errorNome       = '';
				$scope.errorCpf     = '';
				$scope.errorData       = '';
				$scope.errorParentesco        = '';	
 				 
				$scope.registro_dependente = null;
				
				$scope.registros_tipo = [
					{nome: "Selecione", id: ""},
					{nome: "Esposo(a)", id: "1"},
 					{nome: "Filho(a)", id: "2"},
 				];
 				
   			   }

$scope.dependenteEdit = function(index){
	
	            $scope.retorno          = '';
				$scope.retorno_error    = ''; 
	
				$scope.errorNome       = '';
				$scope.errorCpf     = '';
				$scope.errorData       = '';
				$scope.errorParentesco        = '';	
 	            
	 		    $scope.registro_dependente = null;
 				$scope.registros_tipo = null;
				
				$http.get($scope.server("/perfil/dependentesEditarJson/"+index)).success(function(data){
				$scope.registro_dependente  = data;
				
				if(data['parente']==1){
				
				$scope.registros_tipo = [
					{nome: "Esposo(a)", id: "1"},
 					{nome: "Filho(a)", id: "2"},
				];
				
				}else if(data['parente']==2){
				$scope.registros_tipo = [
					{nome: "Filho(a)", id: "2"},
				    {nome: "Esposo(a)", id: "1"},
 				 
					 
				];
				
				}else{
					
				$scope.registros_tipo = [
					{nome: "Selecione", id: ""},
					{nome: "Esposo(a)", id: "1"},
 					{nome: "Filho(a)", id: "2"},
				]; 
				}
				 
  				});
				
 			 
				
				
			  }	
 

$scope.dependenteSalvar = function() {
	 
	var dados = $.param({ 
				codigo:      $("input:hidden[name='codigo']").val(),
 				nome:        $("input:text[name='nome']").val(),
				cpf:         $("input:text[name='cpf']").val(),
				data_nascimento:  $("input:text[name='data_nascimento']").val(),
 				parente:        $("#parente option:selected").val()
   			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/perfil/dependenteSalvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
             if (!data.sucesso) {
				   
                  $scope.errorNome         = data.errors.nome;
				  $scope.errorCpf          = data.errors.cpf;
				  $scope.errorData         = data.errors.data_nascimento;
				  $scope.errorParentesco   = data.errors.parente;
				  
   				  $scope.retorno_error    = data.retorno_error; 
                   
			 } else {
				  
			    $scope.errorNome       = '';
				$scope.errorCpf     = '';
				$scope.errorData       = '';
				$scope.errorParentesco        = '';	
				 
 				  $scope.retorno          = data.retorno;
				  $scope.retorno_error    = data.retorno_error;
				  
				  if(data.modal==true){
				    $("#editModal").modal("hide");
				    $scope.dependentes(); 
				  }
				 }
        }); 
}


//////////////////


			  
			  
 
 
		$scope.$watch("estado",function( cd_estado, nome ) {
		if (cd_estado === nome) {
		  return;
		}else{
		 		$http.get($scope.server("/perfil/enderecoCidadeJson/"+cd_estado)).success(function(data){
				 $scope.registros_cidade  = data;
				});
		    }
		 });
		 
		 
		 
		 
		 
		 
	
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 