package freemarker.ext.rhino;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.Wrapper;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Attila Szegedi
 * @version $Id: RhinoWrapper.java,v 1.2.2.1 2006/07/31 11:34:52 szegedia Exp $
 */
public class RhinoWrapper extends BeansWrapper {

    public TemplateModel wrap(Object obj) throws TemplateModelException {
        // So our existence builtins work as expected.
        if(obj == Undefined.instance || obj == UniqueTag.NOT_FOUND) {
            return null;
        }
        // UniqueTag.NULL_VALUE represents intentionally set null in Rhino, and
        // BeansWrapper#nullModel also represents intentionally returned null.
        // I [A.Sz.] am fairly certain that this value is never passed out of
        // any of the Rhino code back to clients, but is instead always being
        // converted back to null. However, since this object is available to 
        // any 3rd party Scriptable implementations as well, they might return
        // it, so we'll just be on the safe side, and handle it.
        if(obj == UniqueTag.NULL_VALUE) {
            return super.wrap(null);
        }
        // So, say, a JavaAdapter for FreeMarker interfaces works
        if(obj instanceof Wrapper) {
            obj = ((Wrapper)obj).unwrap();
        }
        return super.wrap(obj);
    }

    protected ModelFactory getModelFactory(Class clazz) {
        if(Scriptable.class.isAssignableFrom(clazz)) {
            return RhinoScriptableModel.FACTORY;
        }
        return super.getModelFactory(clazz);
    }
}
