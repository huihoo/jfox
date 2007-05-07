/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.component;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfox.framework.annotation.Service;
import org.jfox.util.FileUtils;
import org.apache.log4j.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ASMClassLoader extends URLClassLoader {

    protected Logger logger = Logger.getLogger(this.getClass());

    /**
     * AnnotationName=>[ClassInfo]
     */
    private final Map<String, List<ClassInfo>> annotated = new HashMap<String, List<ClassInfo>>();


    public ASMClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        initASM();
    }

    protected ASMClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    protected void initASM() {
        URL[] urls = getASMClasspathURLs();
        // 有效 URL
        List<URL> appURLs = new ArrayList<URL>();
        for (URL url : urls) {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, null);
            //根据是否含有 java.lang.Object 滤掉 rt.jar
            URL testURL = urlClassLoader.findResource(Object.class.getName().replace(".", "/") + ".class");
            URL charsetURL = urlClassLoader.findResource("sun/nio/cs/ext/GBK.class"); // charset.jar
            //为保险起见，只滤掉含有java.lang.Object，滤掉其它
            if (testURL == null && charsetURL == null) {
                appURLs.add(url);
            }
        }
        Map<String, byte[]> classBytesArray = new HashMap<String, byte[]>();
        // 读取所有的类名，以便查找 Component
        for (URL url : appURLs) {
            try {
//                classNames.addAll(Arrays.asList(FileUtils.getClassNames(url)));
                classBytesArray.putAll(FileUtils.getClassBytesMap(url));
            }
            catch (IOException e) {
                logger.warn("Failed to get Class names from url: " + url.toString());
            }
        }
        for (Map.Entry<String,byte[]> entry : classBytesArray.entrySet()) {
            // 会将所有有 Annotation 的泪保存在 annotated 中
            readClass(entry.getKey(), entry.getValue());
        }
//        System.out.println("");
    }

    /**
     * 返回ClasspathURLs，用来供 ASM 搜索
     */
    protected URL[] getASMClasspathURLs() {
        // 从 parent URLClassLoader 中 getURLs
        if (getParent() != null && (getParent() instanceof URLClassLoader)) {
            return ((URLClassLoader)getParent()).getURLs();
        }
        else {
            return new URL[]{this.getClass().getProtectionDomain().getCodeSource().getLocation()};
        }
    }

    /**
     * 查找被 Annotation 标注的Class列表
     *
     * @param annotation annotation
     */
    public Class[] findClassAnnotatedWith(Class<? extends Annotation> annotation) {
        List<Class> classes = new ArrayList<Class>();
        List<ClassInfo> infos = getAnnotationInfos(annotation.getName());
        for (ClassInfo classInfo : infos) {
            try {
                Class clazz = classInfo.get();
// double check via proper reflection
                if (clazz.isAnnotationPresent(annotation)) {
                    classes.add(clazz);
                }
            }
            catch (ClassNotFoundException e) {
                logger.warn("Exception occupied while findClassAnnotatedWith " + annotation, e);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

// ----------- ASM bytecode reader ------------

    private void readClass(String className, byte[] classBytes) {
        try {
            ClassReader classReader = new ClassReader(classBytes);
            classReader.accept(new ClassInfoBuildingVisitor(), ClassReader.SKIP_DEBUG);
        }
        catch(RuntimeException e){
            logger.error("Error to read class: " + className, e);
            throw e;
        }
    }

    private List<ClassInfo> getAnnotationInfos(String name) {
        List<ClassInfo> infos = annotated.get(name);
        if (infos == null) {
            infos = new ArrayList<ClassInfo>();
            annotated.put(name, infos);
        }
        return infos;
    }

    class ClassInfoBuildingVisitor implements ClassVisitor, AnnotationVisitor {
        private AnnotatableInfo info;

        public ClassInfoBuildingVisitor() {
        }

        public ClassInfoBuildingVisitor(AnnotatableInfo info) {
            this.info = info;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (!name.endsWith("package-info")) {
                ClassInfo classInfo = new ClassInfo(javaName(name), javaName(superName));

                for (String interfce : interfaces) {
                    classInfo.getInterfaces().add(javaName(interfce));
                }
                info = classInfo;
            }
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            AnnotationInfo annotationInfo = new AnnotationInfo(desc);
            info.getAnnotations().add(annotationInfo);
            if (info instanceof ClassInfo) {
                getAnnotationInfos(annotationInfo.getName()).add((ClassInfo)info);
            }
            return new ClassInfoBuildingVisitor(annotationInfo);
        }

        private String javaName(String name) {
            return (name == null) ? null : name.replace('/', '.');
        }

        //--------- not need to implement
        public void visitAttribute(Attribute attribute) {
        }

        public void visitEnd() {
        }

        public FieldVisitor visitField(int i, String string, String string1, String string2, Object object) {
            return null;
        }

        public void visitInnerClass(String string, String string1, String string2, int i) {
        }

        public MethodVisitor visitMethod(int i, String string, String string1, String string2, String[] strings) {
            return null;
        }

        public void visitOuterClass(String string, String string1, String string2) {
        }

        public void visitSource(String string, String string1) {
        }

        public void visit(String string, Object object) {
        }

        public AnnotationVisitor visitAnnotation(String string, String string1) {
            return null;
        }

        public AnnotationVisitor visitArray(String string) {
            return null;
        }

        public void visitEnum(String string, String string1, String string2) {
        }
    }

    abstract class AnnotatableInfo {
        private final List<AnnotationInfo> annotations = new ArrayList<AnnotationInfo>();

        public AnnotatableInfo() {
        }

        public AnnotatableInfo(AnnotatedElement element) {
            for (Annotation annotation : element.getAnnotations()) {
                annotations.add(new AnnotationInfo(annotation.annotationType().getName()));
            }
        }

        public List<AnnotationInfo> getAnnotations() {
            return annotations;
        }

        public abstract String getName();
    }

    class ClassInfo extends AnnotatableInfo {
        private final String name;
        private final String superType;
        private final List<String> interfaces = new ArrayList<String>();
        private Class<?> clazz;
        private ClassNotFoundException notFound;

        public ClassInfo(Class clazz) {
            super(clazz);
            this.clazz = clazz;
            this.name = clazz.getName();
            Class superclass = clazz.getSuperclass();
            this.superType = superclass != null ? superclass.getName() : null;
        }

        public ClassInfo(String name, String superType) {
            this.name = name;
            this.superType = superType;
        }

        public String getPackageName() {
            return name.substring(name.lastIndexOf(".") + 1, name.length());
        }

        public List<String> getInterfaces() {
            return interfaces;
        }

        public String getName() {
            return name;
        }

        public String getSuperType() {
            return superType;
        }

        public Class get() throws ClassNotFoundException {
            if (clazz != null) return clazz;
            if (notFound != null) throw notFound;
            try {
                this.clazz = loadClass(name);
            }
            catch (ClassNotFoundException e) {
                notFound = e;
                throw e;
            }
            return clazz;
        }

        public String toString() {
            return name;
        }
    }

    class AnnotationInfo extends AnnotatableInfo {
        private final String name;

        public AnnotationInfo(Annotation annotation) {
            this(annotation.getClass().getName());
        }

        public AnnotationInfo(Class<? extends Annotation> annotation) {
            this.name = annotation.getName().intern();
        }

        public AnnotationInfo(String name) {
            name = name.replaceAll("^L|;$", "");
            name = name.replace('/', '.');
            this.name = name.intern();
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return getName();
        }
    }


    public static void main(String[] args) {
        ASMClassLoader asmClassLoader = new ASMClassLoader(ASMClassLoader.class.getClassLoader());
        Class[] deploiesComponent = asmClassLoader.findClassAnnotatedWith(Service.class);
        System.out.println(Arrays.toString(deploiesComponent));
    }
}
