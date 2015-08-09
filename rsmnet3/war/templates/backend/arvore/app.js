//URL de acesso ao servidor RESTful
SERVER_URL = "http://www.forlifenetwork.com/backoffice";
SERVER_URL2 = "http://www.forlifenetwork.com/ge";

$app = angular.module('app',['ui.mask','angularFileUpload']);
$app.config(function($routeProvider,$httpProvider){
 
	$routeProvider.
	when('/',{ templateUrl: SERVER_URL+'/home/principal', controller:homeController}).
	
	// perfil
	when('/perfil/dados',{templateUrl: SERVER_URL+'/perfil/dados',controller:perfilController}).
	when('/perfil/endereco',{templateUrl: SERVER_URL+'/perfil/endereco',controller:perfilController}).
	when('/perfil/senha',{templateUrl: SERVER_URL+'/perfil/senha',controller:perfilController}).
	when('/perfil/bancos',{templateUrl: SERVER_URL+'/perfil/bancos',controller:perfilController}).
	when('/perfil/chave',{templateUrl: SERVER_URL+'/perfil/chave',controller:perfilController}).
	when('/perfil/dependentes',{templateUrl: SERVER_URL+'/perfil/dependentes',controller:perfilController}).
	when('/perfil/download',{templateUrl: SERVER_URL+'/perfil/download',controller:perfilController}).

     // venda
	 when('/venda/cadastro',{templateUrl: SERVER_URL+'/venda/home',controller:vendaController}).
	 when('/venda/tickets',{templateUrl: SERVER_URL+'/venda/tickets',controller:vendaController}). 
	 when('/venda/relatorio',{templateUrl: SERVER_URL+'/venda/relatorio',controller:vendaController}). 
	 when('/venda/gerenciar/:id',{templateUrl: SERVER_URL+'/venda/gerenciar', controller:vendaController}).

	
	// upgrade
	when('/upgrade',{templateUrl: SERVER_URL+'/upgrade/home',controller:upgradeController}).
	when('/upgradeGlobal',{templateUrl: SERVER_URL+'/upgrade_global/home',controller:upgradeGlobalController}).
	when('/upgradeCar',{templateUrl: SERVER_URL+'/upgrade_car/home',controller:upgradeCarController}).
	
 	// suporte
	when('/suporte/home',{templateUrl: SERVER_URL+'/suporte/home', controller:suporteController}).
	when('/suporte/abrir/:id',{templateUrl: SERVER_URL+'/suporte/home', controller:suporteController}).
    
	// rede  
	when('/rede/binaria',{templateUrl: SERVER_URL+'/rede/binaria',controller:redeController}).
	when('/rede/binaria/:id',{templateUrl: SERVER_URL+'/rede/binaria', controller:redeController}).
	when('/rede/chave',{templateUrl: SERVER_URL+'/rede/chave',controller:redeController}).
	when('/rede/pontos_binario',{templateUrl: SERVER_URL+'/rede/pontos_binario',controller:redeController}).

 	
	// financeiro
     when('/financeiro/debito',{templateUrl: SERVER_URL+'/financeiro/debito',controller:financeiroController}).
	 when('/financeiro/transferencia',{templateUrl: SERVER_URL+'/financeiro/transferencia',controller:financeiroController}).
	 when('/financeiro/pagamento',{templateUrl: SERVER_URL+'/financeiro/pagamento',controller:financeiroController}).
	 when('/financeiro/pendentes',{templateUrl: SERVER_URL+'/financeiro/pendentes',controller:financeiroController}).
	 when('/financeiro/compra',{templateUrl: SERVER_URL+'/financeiro/compra',controller:financeiroController}).
 	 	 when('/financeiro/extrato',{templateUrl: SERVER_URL+'/financeiro/extrato',controller:financeiroController}).
 	 	 when('/financeiro/boletos',{templateUrl: SERVER_URL+'/financeiro/boletos',controller:financeiroController}).
 	 	 when('/financeiro/saldo',{templateUrl: SERVER_URL+'/financeiro/saldo',controller:financeiroController}).
 	 	 when('/financeiro/saque',{templateUrl: SERVER_URL+'/financeiro/saque',controller:financeiroController}).
 when('/financeiro/trocarmoeda',{templateUrl: SERVER_URL+'/financeiro/trocarmoeda',controller:financeiroController}).	
 
  	 	 when('/financeiro/extrato_inter',{templateUrl: SERVER_URL+'/financeiro/extrato_inter',controller:financeiroController}).
	
	 
	// cadastro
	 when('/cadastro',{templateUrl: SERVER_URL+'/cadastro/home',controller:cadastroController}).
	 
	 
  // contratos
	 when('/contrato/home',{templateUrl: SERVER_URL+'/contrato/home',controller:contratoController}).
 	 
	 // enviar
	 when('/contrato/enviarServico',{templateUrl: SERVER_URL+'/contrato/enviarServico',controller:enviarServicoController}).
	 when('/contrato/enviarDeclaracao',{templateUrl: SERVER_URL+'/contrato/enviarDeclaracao',controller:enviarDeclaracaoController}).
	 when('/contrato/enviarInstrumento',{templateUrl: SERVER_URL+'/contrato/enviarInstrumento',controller:enviarInstrumentoController}).
	 when('/contrato/enviarDocumentos',{templateUrl: SERVER_URL+'/contrato/enviarDocumentos',controller:enviarDocumentosController}).
	 when('/contrato/enviarComprovantes',{templateUrl: SERVER_URL+'/contrato/enviarComprovantes',controller:enviarComprovantesController}).
 
     
	
	
		 when('/produtos/rede',{templateUrl: SERVER_URL+'/produtos/rede',controller:produtosController}).
	 when('/produtos/manuais',{templateUrl: SERVER_URL+'/produtos/manuais',controller:produtosController}).
 	 
	 when('/produtos/global12',{templateUrl: SERVER_URL+'/produtos/global12',controller:produtosController}).
	 when('/produtos/global20',{templateUrl: SERVER_URL+'/produtos/global20',controller:produtosController}).
	 when('/produtos/global30',{templateUrl: SERVER_URL+'/produtos/global30',controller:produtosController}).
	 
	 when('/produtos/protecaoCarro',{templateUrl: SERVER_URL+'/produtos/protecaoCarro',controller:produtosController}).
	 when('/produtos/protecaoCaminhonete',{templateUrl: SERVER_URL+'/produtos/protecaoCarro',controller:produtosController}).
	 when('/produtos/protecaoCaminhoes',{templateUrl: SERVER_URL+'/produtos/protecaoCarro',controller:produtosController}).
	
	
	
	
	// agendamento
	when('/agendamento/home',{templateUrl: SERVER_URL+'/agendamento/home', controller:agendamentoController}).
	when('/agendamento/abrir/:id',{templateUrl: SERVER_URL+'/agendamento/home', controller:agendamentoController}).
	
	
     //////////global
	 when('/ge/cadastro',{templateUrl: SERVER_URL+'/gecadastro/home',controller:gecadastroController}).
 	  	 when('/ge/visto',{templateUrl: SERVER_URL+'/gecadastro/visto',controller:gecadastroController}).

	//// car assist
		 when('/car/cadastro',{templateUrl: SERVER_URL+'/carcadastro/home',controller:carcadastroController}).

 	 
	 otherwise({redirectTo:'/'});

 
	$httpProvider.responseInterceptors.push(function($q,$rootScope) {
		return function(promise) {
			//Always disable loader
			$rootScope.hideLoader();
			return promise.then(function(response) {
			      // do something on success
			      return(response);
			  }, function(response) {
			      // do something on error
			      $data = response.data;
			      $error = $data.error;
			      console.error($data);
			      if ($error && $error.text)
			      	alert("ERROR: " + $error.text);
			      else{
			      	if (response.status=404)
			      		alert("Erro ao acessar servidor.");
			      	else
			      		alert("ERROR!");
			      }
			      return $q.reject(response);
			  });
		}
	});
});	



$app.run(function($rootScope){

	//Uma flag que define se o ícone de acesso ao servidor deve estar ativado
	$rootScope.showLoaderFlag     = false;

	//Força que o ícone de acesso ao servidor seja ativado
	$rootScope.showLoader=function(){
		$rootScope.showLoaderFlag = true;
	}
	//Força que o ícone de acesso ao servidor seja desativado
	$rootScope.hideLoader=function(){
		$rootScope.showLoaderFlag = false;
	}

	// Método que retorna a URL completa de acesso ao servidor. 
	// Evita usar concatenação
	$rootScope.server=function(url){
		return SERVER_URL + url;
	}
	
	
		$rootScope.server2=function(url){
		return SERVER_URL2 + url;
	}
	
	 

});
 

$app.filter('startFrom', function() {
	return function(input, start) {
		if (input==null)
			return null;
        start = +start; //parse to int
        return input.slice(start);
    }
});
