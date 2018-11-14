package com.zevan.compiler;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zevan.annotation.FakeActivity;
import com.zevan.annotation.bindView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class LKProcessor extends AbstractProcessor {

    private Elements elementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(FakeActivity.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.print("LKProcessor");
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FakeActivity.class);
        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;
            List<? extends Element> members = elementUtils.getAllMembers(typeElement);
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(element.asType()),"activity");
            for (Element item:members){
                bindView bindView = item.getAnnotation(bindView.class);
                if (bindView == null)continue;
                methodBuilder.addStatement(String.format("activity.%s = (%s) activity.findViewById(%s)",item.getSimpleName(),ClassName.get(item.asType()).toString(),bindView.value()));
            }
            TypeSpec typeSpec = TypeSpec.classBuilder("Fake"+ element.getSimpleName())
                    .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement),typeSpec).build();
            try{
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

}
