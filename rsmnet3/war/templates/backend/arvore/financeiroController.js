function financeiroController($scope,$http,$routeParams,$location)
{
	 
	 
	 $scope.extrato_inter = function(){
		$scope.showLoader();
		$scope.comprovante = "";
		
		$http.get($scope.server("/financeiro/extratoJson_inter")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros = '';
		 } else {
			   $scope.registros = data;	
			 }
		});
		
		
		
		$http.get($scope.server("/financeiro/extratoJsonTrans_inter")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registrost = '';
		 } else {
			   $scope.registrost = data;	
			 }
		});
		
	  
 	}	
	
	 
	 
$scope.boletos = function(){
		$scope.showLoader();
 		
		$http.get($scope.server("/financeiro/boletosJson")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros = '';
		 } else {
			   $scope.registros = data;	
			 }
		});
		
  	}		
	

$scope.extratoAbrirT = function(index){
	              
	             $http.get($scope.server("/financeiro/comprovanteTrans/"+index)).success(function(data){
				   $scope.comprovanteTrans  = data;
				});
 				
				 }	


$scope.extratoAbrir = function(index){
	              
	             $http.get($scope.server("/financeiro/comprovante/"+index)).success(function(data){
				   $scope.comprovante  = data;
				});
 				
				 }		 
	 
	
 	$scope.extrato = function(){
		$scope.showLoader();
		$scope.comprovante = "";
		
		$http.get($scope.server("/financeiro/extratoJson")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registros = '';
		 } else {
			   $scope.registros = data;	
			 }
		});
		
		
		
		$http.get($scope.server("/financeiro/extratoJsonTrans")).success(function(data){
			 if (data.vazio==true) {
			   $scope.registrost = '';
		 } else {
			   $scope.registrost = data;	
			 }
		});
		
	  
 	}	
	
	 
	 
	 
	 
	  $scope.compra = function(){
  		$scope.showLoader();
   		$http.get($scope.server("/financeiro/tranferenciaJson")).success(function(data){
  					   $scope.valores = data;
      		});
 	}	
	
	 
	$scope.comprarSalvar = function() {
	
	
	$scope.codigo            = '';	
	$scope.enviando          = true;
	$scope.retorno_error     = '';
	$scope.errorValor        = '';
	$scope.errorValor1       = '';
	$scope.errorSenha        = '';
	$scope.errorSenha1       = '';
	
	var dados = $.param({ 
			valor:    $("input:text[name='valor']").val(),
			valor1:   $("input:text[name='valor1']").val(),
			senha:    $("input:password[name='senha']").val(),
			senha1:   $("input:password[name='senha1']").val() 
		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/comprarSalvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
		 
 			  $scope.enviando          = false;
			  $scope.errorValor        = data.errors.valor;
			  $scope.errorValor1       = data.errors.valor1;
 			  $scope.errorSenha        = data.errors.senha;
			  $scope.errorSenha1       = data.errors.senha1;
			  $scope.codigo            = '';
			  $scope.retorno_error     = data.retorno_error; 
 	 } else {
		      $scope.valor              = '';
			  $scope.valor1             = '';
			  
 			  $scope.errorUsuario       = '';
			  $scope.retorno_error      = '';
			  $scope.codigo             = data.codigo;
			  $scope.vcodigo            = data.vcodigo;
			  $scope.retorno            = data.retorno;
			  $scope.transferencia      = false;
			  $scope.enviando           = false;
			  
  			  
		 }
	}); 
	}	
 
	 
	 
    $scope.pendentes = function(){
  		$scope.showLoader();
   		$http.get($scope.server("/financeiro/pendentesJson")).success(function(data){
 				    if (!data.vazio) {
						 $scope.vazio = false;
 					   $scope.registros = data;
					   $scope.qtd =  data.length;
 
    				   }else{
						    $scope.vazio = true;
						    $scope.qtd   =  "0";
  					   }
     		});
 	}	
   
 
     $scope.debitoMudar = function() {
		 if ($scope.debito_selecionada === '') {
		  return;
		}else{
		 		$http.get($scope.server("/financeiro/debitoMudar/"+$scope.debito_selecionada)).success(function(data){
				 
 				 if (!data.sucesso) {
   				  $scope.retorno_error    = data.retorno_error;
             
			 } else {
				  $scope.retorno        = data.retorno;
				  $scope.retorno_error  = data.retorno_error;
				 }
		 });
		    }
		 
      }
	  
	  
	  
	  
	  
	  
	    
	  
	
	$scope.buscarBoleto = function() {
	
	var dados = $.param({ 
			boleto:    $("input:text[name='boleto']").val()
		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/buscarBoleto"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
		 
			  $scope.transferencia     = false;
			  $scope.errorBoleto       = data.errors.boleto;
 	 } else {
		  
 			  $scope.errorBoleto       = '';
			  $scope.transferencia      = true;
			  $scope.registros          = data.registros;
		 }
	}); 
	}	
	  
	  
	  
	 $scope.pagamentoSalvar = function() {
		
	$scope.enviando          = true;
	$scope.retorno_error     = '';
	$scope.errorUsuario      = '';
	$scope.errorValor        = '';
	$scope.errorSenha        = '';
	$scope.errorSenha1       = '';
	
	var dados = $.param({ 
			boleto_selecionado:  $("input:hidden[name='boleto_selecionado']").val(),
 			senha:    $("input:password[name='senha']").val(),
			senha1:   $("input:password[name='senha1']").val() 
		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/pagamentoSalvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
		 
			  $scope.transferencia     = true;
			  $scope.enviando          = false;
			  $scope.errorBoleto_selecionado       = data.errors.boleto_selecionado;
 			  $scope.errorSenha        = data.errors.senha;
			  $scope.errorSenha1       = data.errors.senha1;
			  
			   $scope.retorno_error    = data.retorno_error; 
 	 } else {
		       $scope.boleto              = '';
			  $scope.boleto_selecionado             = '';
 			  $scope.errorBoleto_selecionado        = '';
			  $scope.retorno_error      = '';
			  $scope.retorno            = data.retorno;
			  $scope.transferencia      = false;
			  $scope.enviando           = false;
			  
			  $http.get($scope.server("/financeiro/tranferenciaJson")).success(function(data){
  					   $scope.valores = data;
      		});
			  
			  
		 }
	}); 
	}	 
	  
	  
	  
	
	$scope.buscarUsuario = function() {
	
	var dados = $.param({ 
			usuario:    $("input:text[name='usuario']").val()
		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/buscarUsuario"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
		 
			  $scope.transferencia     = false;
			  $scope.errorUsuario      = data.errors.usuario;
 	 } else {
		  
 			  $scope.errorUsuario       = '';
			  $scope.transferencia      = true;
			  $scope.registros          = data.registros;
		 }
	}); 
	}	
	
	  
	  
	
	 
////////////// TRANSFERENCIA 
 
  $scope.transferencia = function(){
  		
		
		
		$http.get($scope.server("/home/financeiro")).success(function(data){
 			$scope.registros = data;
			
			$scope.saldo_disponivel             = $scope.registros.saldo_disp;	
			$scope.saldo_bloqueado              = $scope.registros.bloqueado;	
 	 		
		});
		
		
			$http.get($scope.server("/home/financeiroInter")).success(function(data){
 			$scope.registros2 = data;
			
			$scope.saldo_disponivel_inter             = $scope.registros2.saldo_disp;	
			$scope.saldo_bloqueado_inter              = $scope.registros2.bloqueado;	
 	 		
		});
		
		
		
		
 	}	
	
	 
	$scope.transferenciaSalvar = function() {
		
	$scope.enviando          = true;
	$scope.retorno_error     = '';
	$scope.errorUsuario      = '';
	$scope.errorValor        = '';
	$scope.errorSenha        = '';
	$scope.errorSenha1       = '';
	
	var dados = $.param({ 
			usuario:  $("input:hidden[name='usuario']").val(),
			valor:    $("input:text[name='valor']").val(),
			senha:    $("input:password[name='senha']").val(),
			senha1:   $("input:password[name='senha1']").val(),
			moeda:    $("input[type='radio'][name='moeda']:checked").val(),
 
		});	 
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/transferenciaSalvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
		 
			  $scope.transferencia     = true;
			  $scope.enviando          = false;
			  $scope.errorUsuario      = data.errors.usuario;
			  $scope.errorValor        = data.errors.valor;
			  $scope.errorSenha        = data.errors.senha;
			  $scope.errorSenha1       = data.errors.senha1;
			  $scope.errorMoeda       = data.errors.moeda;
			  
			   $scope.retorno_error    = data.retorno_error; 
 	 } else {
		      $scope.usuario            = '';
 			  $scope.errorUsuario       = '';
			  $scope.retorno_error      = '';
			  $scope.retorno            = data.retorno;
			  $scope.transferencia      = false;
			  $scope.enviando           = false;
			  
				$http.get($scope.server("/home/financeiro")).success(function(data){
 			$scope.registros = data;
			
			$scope.saldo_disponivel             = $scope.registros.saldo_disp;	
			$scope.saldo_bloqueado              = $scope.registros.bloqueado;	
 	 		
		});
		
		
			$http.get($scope.server("/home/financeiroInter")).success(function(data){
 			$scope.registros2 = data;
			
			$scope.saldo_disponivel_inter             = $scope.registros2.saldo_disp;	
			$scope.saldo_bloqueado_inter              = $scope.registros2.bloqueado;	
 	 		
		});
			  
			  
		 }
	}); 
	}	
 

	$scope.saldo = function(){
		$scope.showLoader();
  		
		$http.get($scope.server("/financeiro/saldoTotalJson/")).success(function(data){
 		  	$scope.saldo  = data;  
   		});
  
	}	
	
	
	
	
	
	
	

	  $scope.saque = function(){
  		
		$scope.showLoader();
    	$http.get($scope.server("/financeiro/saldoTotalJson")).success(function(data){
			   $scope.saldo  = data; 
			   
				if(data.credito_para_saque > 300){
				 $scope.abrir_saldo = "true";  
				   }else{
				$scope.abrir_saldo = "";  	   
					   } 
       		});
			
		$http.get($scope.server("/financeiro/diasJson")).success(function(data){
			   $scope.dias  = data; 
			   if(data.dias > 39){
				 $scope.abrir_data = "true";  
				   }else{
				$scope.abrir_data = "";  	   
					   } 
      		});	
			
			
		$http.get($scope.server("/perfil/bancosJson")).success(function(data){
 			 if (data.vazio==true) {
                   $scope.registros = '';
 			 } else {
 				  $scope.registros = data;	
				 }
   		});	
			
 
 
  $scope.saqueSalvar = function() {
	  	
	$scope.enviando          = true;
	$scope.retorno_error     = '';
 	$scope.errorValor        = '';
	$scope.errorValor1       = '';
	$scope.errorSenha        = '';
	$scope.errorSenha1       = '';
	$scope.errorConta        = '';
	
 	var dados = $('#form').serialize();	
 	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/saqueSalvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
		
	 if(!data.success){
 			  $scope.transferencia      = true;
			  $scope.enviando           = false;
 			  $scope.errorValor         = data.errors.valor;
 			  $scope.errorValor1        = data.errors.valor1;
 			  $scope.errorSenha         = data.errors.senha;
			  $scope.errorSenha1        = data.errors.senha1;
			  $scope.errorConta         = data.errors.cd_cliente_banco;
			  $scope.retorno_error      = data.retorno_error; 
 	 }else{
 			  $scope.retorno_error      = '';
			  $scope.retorno            = data.retorno;
			  $scope.transferencia      = false;
			  $scope.enviando           = false;
 			  $scope.valor              = '';
			  $scope.valor1             = '';
			  $scope.senha              = '';
			  $scope.senha1             = '';
			  $scope.cd_cliente_banco   = '';
 			  $scope.saque();
 		 }
		 
	}); 
	}	
		
			
			
 	}	
	
	
	
	 

////////////// CAMBIO 
 
  $scope.trocarmoeda = function(){
  		$scope.showLoader();
		$scope.form_moeda  = false;
		$scope.valor = 0;
 		
   		$http.get($scope.server("/financeiro/saldoTotalJson")).success(function(data){
  			$scope.saldo_disponivel             = data.saldo_disp;	
  		});
		
		
			$http.get($scope.server("/financeiro/saldoTotalInterJson")).success(function(data){
 		  	 $scope.saldo_disponivel_inter             = data.saldo_disp;	
 		});
		
		
		
 	}	
	
	
		  $scope.moeda = function() {
  				$scope.form_moeda  = false;
      	    
 		      if ($scope.moeda_selecionada == '') {
					$scope.moeda_1     = false;
					$scope.moeda_2     = false;
					$scope.form_moeda  = false;
    		}else if ($scope.moeda_selecionada == 1) {
					$scope.moeda_1     = true;
					$scope.moeda_2     = false;
					$scope.form_moeda  = true;

   		}else if ($scope.moeda_selecionada == 2) {
			 	    $scope.moeda_1     = false;
					$scope.moeda_2     = true;
					$scope.form_moeda  = true;
   			 }
       }
	
	
	
	  
	
	 
	$scope.trocarSalvar = function() {
		
	$scope.enviando          = true;
	$scope.retorno_error     = '';
 	$scope.errorValor        = '';
	$scope.errorSenha        = '';
	$scope.errorSenha1       = '';
	
	var dados = $('#form').serialize();	
		
	$http({
		method  : 'POST',
		url     : $scope.server("/financeiro/trocarSalvar"),
		data    : dados,  
		headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  
	})
	
	.success(function(data) {
	 if (!data.success) {
 			  $scope.enviando          = false;
 			  $scope.errorValor        = data.errors.valor;
			  $scope.errorSenha        = data.errors.senha;
			  $scope.errorSenha1       = data.errors.senha1;
			  $scope.retorno_error    = data.retorno_error; 
 	 } else {
 			  $scope.retorno_error      = '';
			  $scope.retorno            = data.retorno;
 			  $scope.enviando           = false;
 			  $scope.moeda_selecionada  = '';
			 
			  $scope.trocarmoeda();
 		 
		 }
	}); 
	}	
	
	
		
	
} 