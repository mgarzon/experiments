//$Id: SybaseDialect.java 15898 2009-02-05 03:48:51Z gbadner $
package org.hibernate.dialect;

/**
 * SybaseDialect is being deprecated.
 *
 * AbstractTransactSQLDialect should be used as a base
 * class for Sybase and MS SQL Server dialects.
 * 
 * @author Gail Badner
 * @deprecated SybaseASE15Dialect or SQLServerDialect should be 
 * used instead.
 */

public class SybaseDialect extends AbstractTransactSQLDialect {
}
