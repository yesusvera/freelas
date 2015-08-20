package br.com.ibracon.idr.form.bo;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.model.indice.Item;
import br.com.ibracon.idr.form.model.indice.Response;

public class IndiceBO {
	static Logger logger = Logger.getLogger(IndiceBO.class);

	public DefaultMutableTreeNode montarArvoreIndice(byte[] byteBuff) {
		
		logger.info("Montando a árvore do índice");
		
		DefaultMutableTreeNode indice = new DefaultMutableTreeNode("Índice");

		Response response = new Response().montaResposta(byteBuff);
		if(response!=null){
			adicionaItensRecursivo(response.getLivro().getListaItens(), indice);
		}
		return indice;
	}

//	public void gotoPage(TreeSelectionEvent arg0) {
//		try{
//			Item item = (Item)((DefaultMutableTreeNode)(arg0.getNewLeadSelectionPath().getLastPathComponent())).getUserObject();
////			JOptionPane.showMessageDialog(null, item.getPaginareal());
//			formPrincipal.gotoPage(new Integer(item.getPaginareal()));
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	private void adicionaItensRecursivo (ArrayList<Item> listaItens, DefaultMutableTreeNode treeNode) {
		
		if(listaItens==null || listaItens.size()==0){	
			return;
		}
		
		for(Item item : listaItens){
			DefaultMutableTreeNode subTreeNode = new DefaultMutableTreeNode(item);
			adicionaItensRecursivo(item.getItens(), subTreeNode);
			treeNode.add(subTreeNode);
		}
	}
}










