//$Id: Sybase11Dialect.java 15898 2009-02-05 03:48:51Z gbadner $
package org.hibernate.dialect;

import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.Sybase11JoinFragment;

/**
 * A SQL dialect suitable for use with Sybase 11.9.2 (specifically: avoids ANSI JOIN syntax)
 * @author Colm O' Flaherty
 */
public class Sybase11Dialect extends AbstractTransactSQLDialect  {
	public Sybase11Dialect() {
		super();
	}

	public JoinFragment createOuterJoinFragment() {
		return new Sybase11JoinFragment();
	}

}
