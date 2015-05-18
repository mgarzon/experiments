// $Id: CollectionFunction.java 15871 2009-02-03 20:48:39Z steve.ebersole@jboss.com $
package org.hibernate.hql.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;

import org.hibernate.hql.ast.util.DisplayableNode;

/**
 * Represents 'elements()' or 'indices()'.
 *
 * @author josh Dec 6, 2004 8:36:42 AM
 */
public class CollectionFunction extends MethodNode implements DisplayableNode {
	public void resolve(boolean inSelect) throws SemanticException {
		initializeMethodNode( this, inSelect );
		if ( !isCollectionPropertyMethod() ) {
			throw new SemanticException( this.getText() + " is not a collection property name!" );
		}
		AST expr = getFirstChild();
		if ( expr == null ) {
			throw new SemanticException( this.getText() + " requires a path!" );
		}
		resolveCollectionProperty( expr );
	}

	protected void prepareSelectColumns(String[] selectColumns) {
		// we need to strip off the embedded parens so that sql-gen does not double these up
		String subselect = selectColumns[0].trim();
		if ( subselect.startsWith( "(") && subselect.endsWith( ")" ) ) {
			subselect = subselect.substring( 1, subselect.length() -1 );
		}
		selectColumns[0] = subselect;
	}
}
