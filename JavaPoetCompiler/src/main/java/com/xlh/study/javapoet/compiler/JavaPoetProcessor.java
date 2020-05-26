package com.xlh.study.javapoet.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JavaPoetProcessor extends AbstractProcessor {

    // Elements中包含用于操作Element的工作方法
    private Elements elementUtils;
    // Filer用来创建新的源文件，class文件以及辅助文件
    private Filer filer;
    // 打印使用
    private Messager messager;
    // Types包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Override.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        generateActivitySample();

        generateJavaPoetSample();
        
        return true;
    }

    private void generateJavaPoetSample() {
        // 包名
        String packageName = "com.xlh.study.javapoetsample";
        // import包
        // ClassName.get(“包名”，“类名”) 返回ClassName对象
        ClassName log = ClassName.get("android.util", "Log");

        // 类相关
        TypeSpec.Builder javaPoetSample = TypeSpec
                // 类名
                .classBuilder("JavaPoetSample")
                // 类修饰符
                .addModifiers(Modifier.PUBLIC);

        // 成员变量
        FieldSpec str = FieldSpec.builder(String.class, "str", Modifier.PRIVATE)
                .initializer(CodeBlock.of("\"Hello JavaPoet\""))
                .build();


        // say方法相关
        MethodSpec say = MethodSpec
                // 方法名
                .methodBuilder("say")
                // 方法修饰符
                .addModifiers(Modifier.PUBLIC)
                // 方法内容，addStatement负责分号和换行
                .addStatement("$T.e(\"TAG\", \"say:\" + str)", log)
                .build();

        // testFor方法相关
        CodeBlock.Builder fori = CodeBlock.builder();
        // 注意没有{  }
        fori.beginControlFlow("for (int i = 0; i < 10; i++) ");
        fori.add(CodeBlock.of("Log.e(\"TAG\", \"i--->\" + i);\n"));
        fori.endControlFlow();

        MethodSpec testFor = MethodSpec
                // 方法名
                .methodBuilder("testFor")
                // 方法修饰符
                .addModifiers(Modifier.PUBLIC)
                // 方法内容
                .addCode(fori.build())
                .build();

        // 类相关
        TypeSpec sample = javaPoetSample
                // 类添加成员变量
                .addField(str)
                // 类添加方法
                .addMethod(say)
                .addMethod(testFor)
                .build();

        JavaFile file = JavaFile
                // 包名，类
                .builder(packageName, sample).build();

        try {
            // 写入，创建class文件
            file.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generateActivitySample() {

        // 包名
        String packageName = "com.xlh.study.javapoetsample";
        // import包
        // ClassName.get(“包名”，“类名”) 返回ClassName对象
        ClassName activity = ClassName.get("android.app", "Activity");
        ClassName override = ClassName.get("java.lang", "Override");
        ClassName bundle = ClassName.get("android.os", "Bundle");
        ClassName nullable = ClassName.get("android.support.annotation", "Nullable");

        // 类相关
        TypeSpec.Builder mainActivityBuilder = TypeSpec
                // 类名
                .classBuilder("JavaPoetActivity")
                // 类修饰符
                .addModifiers(Modifier.PUBLIC)
                // 相关继承
                .superclass(activity);

        // 参数相关
        ParameterSpec savedInstanceState = ParameterSpec
                // 参数类型，参数名
                .builder(bundle, "savedInstanceState")
                // 参数注解
                .addAnnotation(nullable)
                .build();

        // 方法相关
        MethodSpec onCreate = MethodSpec
                // 方法名
                .methodBuilder("onCreate")
                // 方法注解
                .addAnnotation(override)
                // 方法修饰符
                .addModifiers(Modifier.PROTECTED)
                // 方法参数
                .addParameter(savedInstanceState)
                // 方法内容，addStatement负责分号和换行
                .addStatement("super.onCreate(savedInstanceState)")
                .addStatement("setContentView(R.layout.activity_main)")
                .build();

        // 类相关
        TypeSpec mainActivity = mainActivityBuilder
                // 类添加方法
                .addMethod(onCreate)
                .build();

        JavaFile file = JavaFile
                // 包名，类
                .builder(packageName, mainActivity).build();

        try {
            // 写入，创建class文件
            file.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
