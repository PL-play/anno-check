package analysis.processor.aop.resolver.builder;

import analysis.processor.aop.parser.Expr;
import analysis.processor.aop.parser.Parser;
import analysis.processor.aop.parser.Scanner;
import analysis.processor.aop.parser.Token;
import analysis.processor.aop.resolver.PredictorResolver;
import resource.PointcutPredictorCache;
import spoon.reflect.declaration.CtMethod;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static resource.ElementUtil.getValueOfAnnotationAsString;

/**
 * Created by: zhang ran
 * 2024-04-05
 */
public class CachedPredictPointcutResolverBuilder<E> implements PointcutResolverBuilder<CtMethod<?>, Predicate<E>> {

    private final PointcutPredictorCache pointcutResolverCache = PointcutPredictorCache.getInstance();

    @Override
    @SuppressWarnings("unchecked")
    public Predicate<E> build(Set<CtMethod<?>> contextResource, CtMethod<?> currentResource) {
        if (pointcutResolverCache.contains(currentResource)) {
            return (Predicate<E>) pointcutResolverCache.getPredictor(currentResource);
        }
        String value = getValueOfAnnotationAsString(currentResource, "org.aspectj.lang.annotation.Pointcut");

        Scanner scanner = new Scanner(value);
        List<Token> tokens = scanner.scanTokens();
        if (scanner.isHasError()) {
            return null;
        }
        Parser parser = new Parser(tokens);
        Expr expr = parser.parse();
        if (parser.isHasError()) {
            return null;
        }
        PredictorResolver<CtMethod<?>> resolver = new PredictorResolver<>(expr,
                currentResource.getDeclaringType().getPackage().toString(),
                currentResource.getDeclaringType().getSimpleName());
        resolver.setSource(value);
        var predictor = resolver.resolvePredictor();
        if (predictor == null) return null;
        pointcutResolverCache.addPredictor(currentResource, predictor);
        pointcutResolverCache.addPredictor(value, predictor);
        return (Predicate<E>) predictor;
    }
}
