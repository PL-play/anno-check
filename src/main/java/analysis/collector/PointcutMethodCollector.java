package analysis.collector;

import resource.CachedElementFinder;
import resource.ResourceRole;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

/**
 * Created by: zhang ran
 * 2024-03-05
 */
public class PointcutMethodCollector<E extends CtElement> extends AbstractElementCollector<E> {
    private final static String ANNOTATION = "org.aspectj.lang.annotation.Pointcut";

    @Override
    public Predicate<E> defaultPredictor() {
        // TODO fix bug. when source code with import wildcard like "import org.aspectj.lang.annotation.*", the actual
        // type of the annotations is missing.
        return (e) -> {
            var annos = e.getAnnotations();
            for (CtAnnotation<? extends Annotation> a : annos) {
                var name = a.getAnnotationType().getPackage() + "." + a.getAnnotationType().getSimpleName();
                if (name.equals(ANNOTATION)) {
                    return true;
                }
            }
            return false;
        };
    }

    @Override
    public ResourceRole role() {
        return ResourceRole.METHOD;
    }

    @Override
    public void collect(Object e) {
        if (getPredictor().test((E) e)) {
            elements().add((E) e);
            // cache pointcut methods.
            CachedElementFinder.getInstance().addPointcutMethod((CtMethod<?>) e);
        }
    }
}
