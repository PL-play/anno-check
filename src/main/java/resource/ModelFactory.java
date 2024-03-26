package resource;

import org.w3c.dom.Document;
import spoon.Launcher;
import spoon.reflect.CtModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelFactory {
    private static CtModel model;
    private static String filePath;

    private static Set<String> mapper;

    public static CtModel init(String path) {
        if (model == null) {
            Launcher launcher = new Launcher();
            launcher.addInputResource(path);
            model = launcher.buildModel();
            filePath = path;
        }
        if (mapper == null) {
            mapper = new HashSet<>();
            Map<String, String> mappers = findMyBatisMappers(ModelFactory.getFilePath());
            mappers.forEach((p, namespace) -> mapper.add(namespace));
        }

        return model;
    }

    public static CtModel getModel() {
        return model;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static Set<String> getMybatisMapper(){
        return mapper;
    }

    private static Map<String, String> findMyBatisMappers(String directoryPath) {
        Map<String, String> mapperNamespaces = new HashMap<>();
        try {
            Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .forEach(path -> {
                        try {
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            Document doc = dBuilder.parse(path.toFile());
                            doc.getDocumentElement().normalize();

                            if ("mapper".equals(doc.getDocumentElement().getNodeName())) {
                                String namespace = doc.getDocumentElement().getAttribute("namespace");
                                mapperNamespaces.put(path.toString(), namespace);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapperNamespaces;
    }
}
