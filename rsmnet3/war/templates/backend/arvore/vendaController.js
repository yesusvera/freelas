function vendaController($scope,$http,$routeParams,$location)
{

$scope.tickets = function(){
 		$http.get($scope.server("/venda/ticketsJson")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros = '';
		 } else {
			   $scope.registros = data;	
			 }
		});
		 
	}	

	
	
$scope.relatorio = function(){
 		$http.get($scope.server("/venda/relatorioJson")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros = '';
		 } else {
			   $scope.registros = data;	
			 }
		});
		 
	}	
	
$scope.gerenciar = function(){
  	    $scope.showLoader();
  	      
		$http.get($scope.server("/venda/gerenciarJson/"+$routeParams.id)).success(function(data){
		  $scope.registro  = data;
		}); 
		
	$http.get($scope.server("/venda/dependentesJson/"+$routeParams.id)).success(function(data){
 			 if (data.vazio==true) {
                   $scope.registros_dependentes = '';
 			 } else {
 				   $scope.registros_dependentes = data;	
				 }
   		});
		
	 
 
	  }		
	 
	
  $scope.cadastrar = function(){
	            $scope.novo             = '';
				$scope.parabens         = '';
	            $scope.formcadastro     = '';
				$scope.retorno          = '';
				$scope.retorno_error    = ''; 
				
  				 $http.get($scope.server("/venda/estados")).success(function(data){
 			       $scope.registros_estado   = data;
  				  });
				  
   			   }	
 
	$scope.$watch("estado",function( cd_estado, nome ) {
	if(cd_estado === nome) {
	  return;
	}else{
			$http.get($scope.server("/venda/cidades/"+cd_estado)).success(function(data){
			 $scope.registros_cidade  = data;
			});
		}
	 }); 
	 
		 
	$scope.$watch("capital",function( cd_estado, nome ) {
		if(cd_estado === nome) {
		  return;
		}else{
				$http.get($scope.server("/venda/capitais/"+cd_estado)).success(function(data){
				 $scope.registros_capitais  = data;
				});
			}
	 }); 	 
 	 
 
	   
  $scope.cpfConta = function() {
  		   if (!$scope.cpf) {
 		   }else{
		   $http.get($scope.server("/venda/cpf/"+$scope.cpf)).success(function(data){
 					 $scope.errorCpf  = data.errors.cpf;
 					
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
 				cpf:              $("input:text[name='cpf']").val(),
 				nome:             $("input:text[name='nome']").val(),
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
				ticket:           $("input:text[name='ticket']").val(),
				senha:            $("input:text[name='senha']").val(),	
				adesao:            $("input:text[name='adesao']").val(),	
				venc_adesao:            $("input:text[name='venc_adesao']").val()			 
   			});	   
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/venda/salvar"),
        data    : dados,  
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
    })
        .success(function(data) {
			
             if (!data.sucesso) {
				    $scope.enviando                  = false;
 					$scope.errorCpf                  = data.errors.cpf;
 					$scope.errorNome                 = data.errors.nome;
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
					$scope.errorTicket               = data.errors.ticket;
					$scope.errorSenha                = data.errors.senha;
    			    $scope.retorno_error             = data.retorno_error; 
					 
                   
			 } else {    
				    $scope.enviando                  = false;
 					//location.href = $scope.server("/#/venda/gerenciar/"+data.secreto);
  					 
					 
					 
					$scope.retorno          = data.retorno;
					$scope.retorno_error    = data.retorno_error;
					$scope.novo             = true;
					$scope.secreto_adesao      = data.secreto_adesao;
 					 
					 
					 
					 
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
						{nome: "Pai", id: "3"},
					{nome: "Mãe", id: "4"},
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
				
				$http.get($scope.server("/venda/dependentesEditarJson/"+index)).success(function(data){
				$scope.registro_dependente  = data;
				
				if(data['parente']==1){
				
				$scope.registros_tipo = [
					{nome: "Esposo(a)", id: "1"},
 					{nome: "Filho(a)", id: "2"},
					{nome: "Pai", id: "3"},
					{nome: "Mãe", id: "4"},
				];
				
				}else if(data['parente']==2){
				$scope.registros_tipo = [
					{nome: "Filho(a)", id: "2"},
				    {nome: "Esposo(a)", id: "1"},
						{nome: "Pai", id: "3"},
					{nome: "Mãe", id: "4"},
 				 
					 
				];
				
				
				
				}else if(data['parente']==3){
				$scope.registros_tipo = [
				
					{nome: "Pai", id: "3"},
					{nome: "Filho(a)", id: "2"},
				    {nome: "Esposo(a)", id: "1"},
					 
					{nome: "Mãe", id: "4"},
 				 
					 
				];
				
				 
				
				}else if(data['parente']==4){
				$scope.registros_tipo = [
				{nome: "Mãe", id: "4"},
					{nome: "Filho(a)", id: "2"},
				    {nome: "Esposo(a)", id: "1"},
						{nome: "Pai", id: "3"},
					 
 				 
					 
				];
				
				}
				else{
					
				$scope.registros_tipo = [
					{nome: "Selecione", id: ""},
					{nome: "Esposo(a)", id: "1"},
 					{nome: "Filho(a)", id: "2"},
						{nome: "Pai", id: "3"},
					{nome: "Mãe", id: "4"},
					
				]; 
				}
				 
  				});
				
 			 
				
				
			  }	
 

$scope.dependenteSalvar = function() {
	 
	var dados = $.param({ 
				secreto:          $("input:hidden[name='secreto']").val(),
				codigo:           $("input:hidden[name='codigo']").val(),
 				nome:             $("input:text[name='nome']").val(),
				cpf:              $("input:text[name='cpf']").val(),
				data_nascimento:  $("input:text[name='data_nascimento']").val(),
 				parente:          $("#parente option:selected").val()
   			});	 
			
			
	$http({
        method  : 'POST',
        url     : $scope.server("/venda/dependenteSalvar"),
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

 
$scope.dependentes = function(){
		$scope.showLoader();
		
		$http.get($scope.server("/venda/dependentesJson/"+$routeParams.id)).success(function(data){
 			 if (data.vazio==true) {
                   $scope.registros_dependentes = '';
 			 } else {
 				   $scope.registros_dependentes = data;	
				 }
   		});
  
	}





}