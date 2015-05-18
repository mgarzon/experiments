//$Id: FirebirdDialect.java 15897 2009-02-04 23:14:05Z steve.ebersole@jboss.com $
package org.hibernate.dialect;

/**
 * An SQL dialect for Firebird.
 *
 * @author Reha CENANI
 */
public class FirebirdDialect extends InterbaseDialect {

	public String getDropSequenceString(String sequenceName) {
		return "drop generator " + sequenceName;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length() + 20 )
				.append( sql )
				.insert( 6, hasOffset ? " first ? skip ?" : " first ?" )
				.toString();
	}

	public boolean bindLimitParametersFirst() {
		return true;
	}

	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

}