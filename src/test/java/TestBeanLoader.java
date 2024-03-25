import analysis.collector.SpringBeanAnnoMethodCollector;
import analysis.collector.SpringComponentAnnoClassCollector;
import analysis.collector.SpringControllerClassCollector;
import analysis.collector.SpringServiceAnnoClassCollector;
import analysis.processor.beanloader.SpringBeanAnnoBeanLoader;
import analysis.processor.beanloader.SpringComponentAnnoBeanLoader;
import analysis.processor.beanloader.SpringControllerAnnoBeanLoader;
import analysis.processor.beanloader.SpringServiceAnnoBeanLoader;
import analysis.processor.resource.ResourceScanner;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.support.reflect.declaration.CtAnnotationImpl;

import java.util.HashSet;
import java.util.Set;

public class TestBeanLoader {

    @Test
    public void test1() {
        System.out.println("Test BeanLoader");
    }

    @Test
    public void test2() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/demo/src/main/java/");
        launcher.buildModel();
        launcher.process();
        ResourceScanner processor = new ResourceScanner();

        var c1 = new SpringComponentAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c1);

        var c2 = new SpringServiceAnnoClassCollector<CtClass<?>>();
        processor.addCollector(c2);

        var c3 = new SpringControllerClassCollector<CtClass<?>>();
        processor.addCollector(c3);

        var c4 = new SpringBeanAnnoMethodCollector<CtMethod<?>>();
        processor.addCollector(c4);

        processor.scan(launcher.getModel().getRootPackage());

        var com = new SpringComponentAnnoBeanLoader();
        System.out.println("@Component");
        c1.elements().forEach(e -> {
            var bd = com.load(null, e);
            System.out.println(bd);
        });
        System.out.println();

        System.out.println("@Service");
        var ser = new SpringServiceAnnoBeanLoader();
        c2.elements().forEach(e -> {
            var bd = ser.load(null, e);
            System.out.println(bd);
        });
        System.out.println();
        System.out.println("@Controller");
        var con = new SpringControllerAnnoBeanLoader();
        c3.elements().forEach(e -> {
            var bd = con.load(null, e);
            System.out.println(bd);
        });

        System.out.println();
        System.out.println("@Bean");
        var be = new SpringBeanAnnoBeanLoader();
        c4.elements().forEach(e -> {
            var bd = be.load(null, e);
            System.out.println(bd);
        });

    }
}
