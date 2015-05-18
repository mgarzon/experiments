package org.jbrt.client;


import java.util.List;

/**
 * <p>Scenario is name of JBrt component that handles business logic. In scratch
 * it is an class that implements JScenario interface.
 * Business logic is proceeding after overriding of appropriate methods.
 * Almost all business logic is handled with methods of scenario.
 * Scenarios in JBrt should be created according to folding philosophy:</p>
 * <p>Scenario is always child of JScenario. You can use already prepared 
 * scenarios in distribution, or you can create your own and register it in JBrt
 * before JBrt was started. In other cases JBrtInitializeException is thrown.
 *
 * Some parameters are “injected” form JBRT during initialization:</p>
 * <ul>
 * <li>JConfiguration configuration – configuration (atoms, a maps)</li>
 * <li>Integer bufferSize – throwable buffer size.</li>
 * <li>File workingDirectory – working directory where can scenario store its files. Scenario needs rights of reading and writing in this directory.</li>
 * <li>LinkedList&lt;JAppearanceListener&gt; appListeners – listeners list for throwable appearance</li>
 * <li>LinkedList&lt;JResponseListener&gt; resListeners – listeners list for response</li>
 * </ul>
 * <p>The only problem is only business logic for new scenario creator. 
 * YOU need to implement this methods:</p>
 * <ul>
 * <li>initialize – scenario initialization</li>
 * <li>end – method is responsible for storing of unsaved throwables and all scenario important parameters before program shutting down.</li>
 * <li>commit – method process throwables from runtime.</li>
 * <li>post – method sends exceptions to server and handles response.</li>
 * <li>delete - delete all scenarios files in workingDirectory</li>
 * <lt>getThrowablesInMemory - returns all throwables that are actualy in memory</li>
 * </ul>

 * @author Cipov Peter
 * @version 1.0
 */
public interface JScenario {

    /**
     * Method will be cast before connecting scenario.
     * This method should be override, if you need to
     * initialize some extra field in your scenario. 
     * After calling this method, scenario should be ready 
     * for new exceptions. 
     */
    public void initialize();

    /**
     * This method is called, just before program shouting down.
     * It should handle also exceptions that are not already stored,
     * or not send. 
     */
    public void end();

    /**
     * This is input gate for Throwable handling.
     * This method is responsible for Throwable handling.
     * You can do what you want with them :)
     * @param ex Throwable wrapped in DAO object.
     * @return true if oK, else false 
     */
    public boolean commit(JThrowable ex);

    /**
     * Method is responsible for posting exceptions to servers
     * according to configuration. It should not contains any threads,
     * this method should be called in one thread. But it is not required. 
     */
    public void post();

    /**
     * Method is responsible for deleting all files, that this scenario
     * creates in working directory. It is method for user to restore 
     * working directory after some critical mistake.
     */
    public void delete();

    /**
     * Throwables list, that are in memory just now.
     * @return throwables list, empty list if nothing.
     */
    public abstract List<JThrowable> getThrowablesInMemory();
}
