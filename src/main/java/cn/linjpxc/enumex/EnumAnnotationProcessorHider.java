package cn.linjpxc.enumex;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class EnumAnnotationProcessorHider {

    @SupportedSourceVersion(SourceVersion.RELEASE_8)
    @SupportedAnnotationTypes(value = {"cn.linjpxc.enumex.Enum"})
    public static class EnumProcessor extends AbstractProcessor {

        private static final String ENUM_ANNOTATE_CLASS_NAME = Enum.class.getName();
        private static final String ENUM_VALUE_DEFAULT_CLASS_NAME = Integer.class.getName();
        private static final String ENUM_VALUE_FIELD_DEFAULT_NAME = "value";
        private static final String ENUM_VALUE_TYPE_FIELD_NAME = "valueType";
        private static final String ENUM_VALUE_FIELD_NAME_FIELD_NAME = "valueFieldName";

        private static final String ENUM_VALUE_CLASS_NAME = EnumValue.class.getName();
        private static final String ENUM_VALUE_METHOD_NAME = "value";
        private static final String VALUE_OF_METHOD_NAME = "valueOf";

        private static final String JAVA_STRING_CLASS_NAME = "java.lang.String";

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (!(this.processingEnv instanceof JavacProcessingEnvironment)) {
                this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "ProcessingEnvironment not is JavacProcessingEnvironment");
                return true;
            }

            final Context context = ((JavacProcessingEnvironment) this.processingEnv).getContext();
            final Elements elementUtils = this.processingEnv.getElementUtils();
            final TreeMaker treeMaker = TreeMaker.instance(context);
            final JavacTrees javacTrees = JavacTrees.instance(this.processingEnv);
            final Names names = Names.instance(context);

            final TypeElement enumValueTypeElement = elementUtils.getTypeElement(ENUM_VALUE_CLASS_NAME);

            roundEnv.getElementsAnnotatedWith(Enum.class).forEach(element -> {
                final JCTree jcTree = javacTrees.getTree(element);
                if (jcTree.getKind() == Tree.Kind.ENUM) {
                    final EnumAnnotationInfo enumAnnotationInfo = getEnumAnnotationInfo(element, elementUtils, names);
                    if (enumAnnotationInfo == null) {
                        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not found Enum Annotation.");
                        return;
                    }

                    jcTree.accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            treeMaker.pos = jcClassDecl.pos;
                            // 判断有没有实现 EnumValue 接口，没有实现就自动实现
                            if (isNonImplement(jcClassDecl.implementing, enumValueTypeElement)) {
                                // 自动导入包
                                autoImportPackage(javacTrees.getPath(element), treeMaker, elementUtils, enumValueTypeElement);
                                autoImplementEnumValue(jcClassDecl, treeMaker, enumValueTypeElement, enumAnnotationInfo);

                                addValueFieldAndConstructor(jcClassDecl, enumAnnotationInfo, treeMaker, names);
                            }
                            autoImportPackage(javacTrees.getPath(element), treeMaker, elementUtils, enumAnnotationInfo.valueType);
                            addValueOfMethod(jcClassDecl, enumValueTypeElement, enumAnnotationInfo.valueType, treeMaker, names);
                            super.visitClassDef(jcClassDecl);
                        }

                        @Override
                        public void visitTopLevel(JCTree.JCCompilationUnit jcCompilationUnit) {
                            super.visitTopLevel(jcCompilationUnit);
                        }
                    });
                } else {
                    this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, jcTree + " not is enum.");
                }
            });
            return true;
        }

        private static void addValueOfMethod(JCTree.JCClassDecl jcClassDecl, TypeElement enumValueTypeElement, TypeElement valueTypeElement, TreeMaker treeMaker, Names names) {
            if (hasValueOfMethod(jcClassDecl, treeMaker.Modifiers(Flags.PUBLIC), valueTypeElement, names)) {
                return;
            }

            final JCTree.JCFieldAccess enumValueOfMethod = treeMaker.Select(treeMaker.Ident((Symbol) enumValueTypeElement), names.fromString(VALUE_OF_METHOD_NAME));
            final JCTree.JCFieldAccess enumClass = treeMaker.Select(treeMaker.Ident(jcClassDecl.sym), names._class);
            JCTree.JCVariableDecl paramValue = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("value"), treeMaker.Ident((Symbol) valueTypeElement), null);
            final JCTree.JCMethodInvocation methodInvocation = treeMaker.Apply(List.nil(), enumValueOfMethod, List.of(
                    enumClass, treeMaker.Ident(names.fromString("value"))
            ));

            final JCTree.JCReturn aReturn = treeMaker.Return(methodInvocation);
            final JCTree.JCBlock block = treeMaker.Block(0L, List.of(aReturn));
            final JCTree.JCVariableDecl valueDecl = treeMaker.VarDef(
                    new Symbol.VarSymbol(Flags.PRIVATE | Flags.FINAL, names.fromString("value"), jcClassDecl.sym.type, jcClassDecl.sym),
                    null
            );
            JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                    names.fromString(VALUE_OF_METHOD_NAME),
                    (JCTree.JCExpression) valueDecl.getType(),
                    List.nil(),
                    List.of(paramValue),
                    List.nil(),
                    block,
                    null);

            jcClassDecl.defs = jcClassDecl.defs.append(methodDecl);
        }

        private static boolean hasValueOfMethod(JCTree.JCClassDecl jcClassDecl, JCTree.JCModifiers modifiers, TypeElement valueTypeElement, Names names) {
            final List<JCTree> defs = jcClassDecl.defs;
            for (JCTree jcTree : defs) {
                if (jcTree instanceof JCTree.JCMethodDecl) {
                    final JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) jcTree;
                    if (methodDecl.sym != null && methodDecl.sym.isStatic() && methodDecl.sym.getSimpleName().equals(names.fromString(VALUE_OF_METHOD_NAME))) {
                        final List<JCTree.JCVariableDecl> parameters = methodDecl.getParameters();
                        if (parameters != null && parameters.size() == 1) {
                            final JCTree.JCVariableDecl jcVariableDecl = parameters.get(0);
                            if (jcVariableDecl.sym.type.asElement().getQualifiedName().equals(valueTypeElement.getQualifiedName())) {
                                return true;
                            }
                        }
                    }
                }
            }
            if (jcClassDecl.sym != null) {
                java.util.List<Symbol> enclosedElements = jcClassDecl.sym.getEnclosedElements();
                if (enclosedElements != null && enclosedElements.size() > 0) {
                    return hasStaticMethod(enclosedElements, modifiers, names.fromString(VALUE_OF_METHOD_NAME), valueTypeElement);
                }
            }

            return false;
        }

        private static boolean hasStaticMethod(Iterable<Symbol> symbols, JCTree.JCModifiers modifier, Name methodName, TypeElement... typeElements) {
            for (Symbol symbol : symbols) {
                if (symbol instanceof Symbol.MethodSymbol) {
                    final Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) symbol;
                    if (methodSymbol.isStatic() && hasModifier(methodSymbol, (Modifier) modifier.getFlags().toArray()[0])) {
                        if (methodSymbol.getSimpleName().equals(methodName) && equals(methodSymbol.getParameters(), typeElements)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private static boolean equals(List<Symbol.VarSymbol> varSymbols, TypeElement... typeElements) {
            if (varSymbols == null || varSymbols.size() < 1) {
                if (typeElements == null || typeElements.length < 1) {
                    return true;
                }
                return false;
            }
            if (typeElements == null || typeElements.length < 1) {
                return false;
            }

            for (int i = 0; i < typeElements.length; i++) {
                if (!varSymbols.get(i).type.asElement().getQualifiedName().equals(typeElements[i].getQualifiedName())) {
                    return false;
                }
            }

            return true;
        }

        private static boolean hasModifier(Symbol.MethodSymbol methodSymbol, Modifier modifier) {
            final Set<Modifier> modifiers = methodSymbol.getModifiers();
            if (modifiers != null && modifiers.size() > 0) {
                for (Modifier item : modifiers) {
                    if (item.equals(modifier)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static JCTree overrideValueMethod(JCTree.JCVariableDecl field, TreeMaker treeMaker, Names names) {
            return treeMaker.MethodDef(
                    treeMaker.Modifiers(Flags.PUBLIC),
                    names.fromString(ENUM_VALUE_METHOD_NAME),
                    (JCTree.JCExpression) field.getType(),
                    List.nil(),
                    List.nil(),
                    List.nil(),
                    treeMaker.Block(0L, List.of(
                            treeMaker.Return(
                                    treeMaker.Select(
                                            treeMaker.Ident(names._this),
                                            names.fromString(field.getName().toString())
                                    )
                            )
                    )), null
            );
        }

        private static void addValueFieldAndConstructor(JCTree.JCClassDecl jcClassDecl, EnumAnnotationInfo enumAnnotationInfo, TreeMaker treeMaker, Names names) {
            final ListBuffer<JCTree> listBuffer = new ListBuffer<>();
            listBuffer.appendList(removeConstructor(jcClassDecl.defs));

            final String fieldName = enumAnnotationInfo.valueFieldName;

            final JCTree.JCVariableDecl valueDecl = treeMaker.VarDef(
                    new Symbol.VarSymbol(Flags.PRIVATE | Flags.FINAL, names.fromString(fieldName), ((Symbol) enumAnnotationInfo.valueType).type, jcClassDecl.sym),
                    null
            );
            listBuffer.append(valueDecl);
//            listBuffer.append(addConstructor(valueDecl, treeMaker, names));
            listBuffer.appendList(addConstructor(valueDecl, treeMaker, names));
            listBuffer.append(overrideValueMethod(valueDecl, treeMaker, names));

            jcClassDecl.defs = listBuffer.toList();
        }

        private static List<JCTree> addConstructor(JCTree.JCVariableDecl field, TreeMaker treeMaker, Names names) {
            final ListBuffer<JCTree> list = new ListBuffer<>();


            final Symbol.VarSymbol paramSymbol = new Symbol.VarSymbol(Flags.PARAMETER, field.name, field.type, field.sym);

            final JCTree.JCFieldAccess _this = treeMaker.Select(treeMaker.Ident(names._this), field.name);
            final JCTree.JCAssign assign = treeMaker.Assign(_this, treeMaker.Ident(field));
            final JCTree.JCBlock block = treeMaker.Block(0L, List.of(treeMaker.Exec(assign)));

            final Type.MethodType methodType = new Type.MethodType(List.of(field.type), new Type.JCVoidType(), List.nil(), null);
            final Symbol.MethodSymbol methodSymbol = new Symbol.MethodSymbol(0L, names.init, methodType, treeMaker.VarDef(paramSymbol, null).sym);
            methodSymbol.params = List.of(paramSymbol);

            // String 类型新增无参构造函数
            if (field.getType().toString().equals(JAVA_STRING_CLASS_NAME)) {
                final Type.MethodType noParamMethodType = new Type.MethodType(List.nil(), new Type.JCVoidType(), List.nil(), null);

                JCTree.JCAssign name = treeMaker.Assign(_this, treeMaker.Apply(List.nil(), treeMaker.Select(treeMaker.Ident(names._this), names.fromString("name")), List.nil()));

                list.add(treeMaker.MethodDef(new Symbol.MethodSymbol(0L, names.init, noParamMethodType, treeMaker.VarDef(paramSymbol, null).sym), treeMaker.Block(0L, List.of(treeMaker.Exec(name)))));
            }

            list.add(treeMaker.MethodDef(methodSymbol, block));

            return list.toList();
        }

        private static List<JCTree> removeConstructor(List<JCTree> defs) {
            final ListBuffer<JCTree> list = new ListBuffer<>();
            for (JCTree jcTree : defs) {
                if (!(jcTree instanceof JCTree.JCMethodDecl)) {
                    list.append(jcTree);
                    continue;
                }
                final JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) jcTree;
                if (!methodDecl.sym.isConstructor()) {
                    list.append(jcTree);
                }
            }
            return list.toList();
        }

//        private static TypeElement getEnumValueType(Element element, Elements elementUtils, Names names) {
//            final java.util.List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
//            if (annotationMirrors == null || annotationMirrors.size() < 1) {
//                return null;
//            }
//
//            for (AnnotationMirror annotationMirror : annotationMirrors) {
//                if (!annotationMirror.getAnnotationType().asElement().getSimpleName().equals(elementUtils.getTypeElement(Enum.class.getName()).getSimpleName())) {
//                    continue;
//                }
//                final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
//                if (elementValues == null) {
//                    return null;
//                }
//                final ExecutableElement valueType = elementValues.keySet().stream().filter(key -> key.getSimpleName().equals(names.fromString(ENUM_VALUE_TYPE_FIELD_NAME))).findFirst().orElse(null);
//                if (valueType == null) {
//                    return elementUtils.getTypeElement(Integer.class.getName());
//                }
//                final AnnotationValue annotationValue = elementValues.get(valueType);
//                return elementUtils.getTypeElement(annotationValue.getValue().toString());
//            }
//            return null;
//        }

        private static EnumAnnotationInfo getEnumAnnotationInfo(Element element, Elements elements, Names names) {
            final java.util.List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
            if (annotationMirrors == null) {
                return null;
            }
            final AnnotationMirror annotationMirror = annotationMirrors
                    .stream()
                    .filter(item -> item.getAnnotationType().asElement().getSimpleName().equals(elements.getTypeElement(ENUM_ANNOTATE_CLASS_NAME).getSimpleName()))
                    .findFirst()
                    .orElse(null);
            if (annotationMirror == null) {
                return null;
            }

            final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            if (elementValues == null) {
                return new EnumAnnotationInfo(elements.getTypeElement(ENUM_VALUE_DEFAULT_CLASS_NAME), ENUM_VALUE_FIELD_DEFAULT_NAME);
            }

            final Set<? extends ExecutableElement> executableElements = elementValues.keySet();
            TypeElement valueTypeElement = null;
            String valueFieldName = ENUM_VALUE_FIELD_DEFAULT_NAME;
            for (ExecutableElement item : executableElements) {
                final Name simpleName = item.getSimpleName();
                if (simpleName.equals(names.fromString(ENUM_VALUE_TYPE_FIELD_NAME))) {
                    valueTypeElement = elements.getTypeElement(elementValues.get(item).getValue().toString());
                }
                if (simpleName.equals(names.fromString(ENUM_VALUE_FIELD_NAME_FIELD_NAME))) {
                    valueFieldName = elementValues.get(item).getValue().toString();
                }
            }

            return new EnumAnnotationInfo(valueTypeElement == null ? elements.getTypeElement(ENUM_VALUE_DEFAULT_CLASS_NAME) : valueTypeElement, valueFieldName);
        }

        private static void autoImplementEnumValue(JCTree.JCClassDecl jcClassDecl, TreeMaker treeMaker, Element enumValueElement, EnumAnnotationInfo enumAnnotationInfo) {
            final ListBuffer<JCTree.JCExpression> listBuffer = new ListBuffer<>();
            listBuffer.appendList(jcClassDecl.implementing);

            if (isNonImplement(jcClassDecl.implementing, enumValueElement)) {
                listBuffer.append(
                        treeMaker.TypeApply(treeMaker.Ident(
                                (Symbol) enumValueElement
                        ), List.of(
                                treeMaker.Ident(jcClassDecl.sym),
                                treeMaker.Ident((Symbol) enumAnnotationInfo.valueType)
                        ))
                );
            }

            jcClassDecl.implementing = listBuffer.toList();
        }

        private static boolean isNonImplement(List<JCTree.JCExpression> implementing, Element element) {
            for (JCTree.JCExpression item : implementing) {
                final JCTree tree = item.getTree();
                if (tree != null) {
                    final Type type = tree.type;
                    if (type != null) {
                        final Symbol.TypeSymbol symbol = type.asElement();
                        if (symbol != null) {
                            if (symbol.getSimpleName().equals(element.getSimpleName())) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        private static void autoImportPackage(TreePath treePath, TreeMaker treeMaker, Elements elementUtils, Element... elements) {
            final CompilationUnitTree compilationUnit = treePath.getCompilationUnit();
            if (compilationUnit instanceof JCTree.JCCompilationUnit) {
                final JCTree.JCCompilationUnit jcCompilationUnit = (JCTree.JCCompilationUnit) compilationUnit;
                final List<JCTree.JCImport> imports = jcCompilationUnit.getImports();

                for (Element element : elements) {
                    if (!isImport(imports, element.getSimpleName())) {
                        JCTree.JCImport anImport = treeMaker.Import(treeMaker.Select(
                                treeMaker.Ident((com.sun.tools.javac.util.Name) elementUtils.getPackageOf(element).getQualifiedName()),
                                (com.sun.tools.javac.util.Name) element.getSimpleName()
                        ), false);

                        jcCompilationUnit.defs = jcCompilationUnit.defs.append(anImport);
                    }
                }
            }
        }

        private static boolean isImport(List<JCTree.JCImport> imports, Name name) {
            for (JCTree.JCImport jcImport : imports) {
                final JCTree.JCFieldAccess qualid = (JCTree.JCFieldAccess) jcImport.qualid;
                if (qualid.sym.name.equals(name)) {
                    return true;
                }
            }
            return false;
        }

        private static final class EnumAnnotationInfo {
            final TypeElement valueType;
            final String valueFieldName;

            private EnumAnnotationInfo(TypeElement valueType, String valueFieldName) {
                this.valueType = Objects.requireNonNull(valueType);
                this.valueFieldName = valueFieldName == null || valueFieldName.trim().length() < 1 ? ENUM_VALUE_FIELD_DEFAULT_NAME : valueFieldName;
            }
        }
    }
}
