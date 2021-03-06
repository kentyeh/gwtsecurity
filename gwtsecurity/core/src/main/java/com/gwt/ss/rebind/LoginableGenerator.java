/**
 * $Id$
 *
 * Copyright (c) 2014 Steven Jardine, All Rights Reserved.
 * Copyright (c) 2014 MJN Services, Inc., All Rights Reserved.
 */
package com.gwt.ss.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.gwt.ss.client.loginable.LoginableAsync;
import com.gwt.ss.client.loginable.LoginableService;

/**
 * Proxy generator for {@link com.gwt.ss.client.loginable.LoginableAsync LoginableAsync}
 *
 * Thanks to Steven Jardine steven.j...@gmail.com http://code.google.com/u/@UhVVQ1JZAxVEXgF4/ for providing
 * debuging and patch.
 *
 * @author Kent Yeh
 */
public class LoginableGenerator extends Generator {

    /** The class name. */
    private String className;

    /** The context. */
    private GeneratorContext context;

    /** The package name. */
    private String packageName;

    /** The service name. */
    private String serviceName;

    /** The service type. */
    private JClassType serviceType;

    /** The source type. */
    private JClassType sourceType;

    /** The type name. */
    private String typeName;

    /**
     * Format.
     *
     * @param s the s
     * @param args the args
     * @return the string
     */
    private String format(final String s, final Object... args) {
        return String.format(s, args);
    }

    /** {@inheritDoc} */
    @Override
    public String generate(final TreeLogger logger, final GeneratorContext context, final String typeName)
            throws UnableToCompleteException {
        this.context = context;
        validate(logger, typeName);
        this.className = format("%s_Proxy", this.sourceType.getSimpleSourceName());
        generateClass(logger);
        return this.packageName + "." + this.className;
    }

    /**
     * Generate class.
     *
     * @param logger the logger
     */
    private void generateClass(final TreeLogger logger) {
        PrintWriter printWriter = context.tryCreate(logger, this.packageName, this.className);
        if (printWriter == null) {
            return;
        }
        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(this.packageName, this.className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.addImport("com.google.gwt.core.client.Scheduler");
        composer.addImport("com.google.web.bindery.event.shared.HandlerRegistration");
        composer.addImport("com.google.gwt.user.client.rpc.AsyncCallback");
        composer.addImport("com.gwt.ss.client.exceptions.GwtAccessDeniedException");
        composer.addImport("com.gwt.ss.client.exceptions.GwtSecurityException");
        composer.addImport("com.gwt.ss.client.loginable.HasLoginHandler");
        composer.addImport("com.gwt.ss.client.loginable.LoginEvent");
        composer.addImport("com.gwt.ss.client.loginable.LoginHandler");
        composer.addImport("com.gwt.ss.client.loginable.LoginableService");
        composer.addImport("com.gwt.ss.client.loginable.LoginCancelException");
        composer.addImport("com.gwt.ss.client.loginable.AbstractLoginHandler");
        composer.addImplementedInterface(this.typeName);
        composer.addImplementedInterface(format("%s<%s>", LoginableService.class.getName(), this.typeName));
        SourceWriter writer = composer.createSourceWriter(context, printWriter);
        generateFields(writer);
        generateMethods(writer);
        writer.outdent();
        writer.println("}");
        context.commit(logger, printWriter);
    }

    /**
     * Generate fields.
     *
     * @param writer the writer
     */
    private void generateFields(final SourceWriter writer) {
        writer.println("private HasLoginHandler hasLoginHandler = null;");
        writer.println("private %s service = null;", this.typeName);
        writer.println();
        writer.println("@Override");
        writer.println("public HasLoginHandler getHasLoginHandler(){");
        writer.indentln("return hasLoginHandler;");
        writer.println("}");
        writer.println();
        writer.println("@Override");
        writer.println("public void setHasLoginHandler(HasLoginHandler hasLoginHandler){");
        writer.indentln("this.hasLoginHandler = hasLoginHandler;");
        writer.println("}");
        writer.println();
        writer.println("@Override");
        writer.println("public %s getRemoteService(){", this.typeName);
        writer.indent();
        writer.println("if(this.service ==null){");
        writer.indentln("this.service = (%s) GWT.create(%s.class);", this.typeName, this.serviceName);
        writer.println("}");
        writer.println("return this.service;");
        writer.outdent();
        writer.println("}");
        writer.println();
        writer.println("@Override");
        writer.println("public void setRemoteService(%s service){", this.typeName);
        writer.indentln("this.service = service;");
        writer.println("}");
        writer.println();
    }

    /**
     * Generate method.
     *
     * @param writer the writer
     * @param method the method
     */
    private void generateMethod(final SourceWriter writer, final JMethod method) {
        String returnType = method.getReturnType().getParameterizedQualifiedSourceName();
        if (returnType.equals("void")) {
            returnType = "Void";
        }
        writer.println("@Override");
        writer.print("public void %s(", method.getName());
        for (JParameter param : method.getParameters()) {
            writer.print("final %s %s, ", param.getType().getParameterizedQualifiedSourceName(), param.getName());
        }
        writer.println("final AsyncCallback<%s> callback) {", returnType);
        writer.indent();
        writer.print("getRemoteService().%s(", method.getName());
        for (JParameter param : method.getParameters()) {
            writer.print("%s, ", param.getName());
        }
        writer.println("new AsyncCallback<%s>() {", returnType);
        writer.println();
        writer.indent();
        writer.println("@Override");
        writer.println("public void onSuccess(%s result) {", returnType);
        writer.indentln("callback.onSuccess(result);");
        writer.println("}");
        writer.println();
        writer.println("@Override");
        writer.println("public void onFailure(Throwable caught) {");
        writer.indent();
        writer.println("boolean handled = false;");
        writer.println("if (getHasLoginHandler() != null && caught instanceof GwtSecurityException) {");
        writer.indent();
        writer.println("GwtSecurityException e = (GwtSecurityException) caught;");
        writer.println("if (!(e.isAuthenticated() && e instanceof GwtAccessDeniedException)) {");
        writer.indent();
        writer.println("handled = true;");
        writer.println("LoginHandler lh = new AbstractLoginHandler() {");
        writer.indent();
        writer.println("@Override");
        writer.println("public void onCancelled() {");
        writer.indentln("callback.onFailure(new LoginCancelException(CANCELLED_MSG));");
        writer.println("}");
        writer.println();
        writer.println("@Override");
        writer.println("public void resendPayload() {");
        writer.indent();
        writer.print("%s(", method.getName());
        for (JParameter param : method.getParameters()) {
            writer.print("%s, ", param.getName());
        }
        writer.println("callback);");
        writer.outdent();
        writer.println("}");
        writer.outdent();
        writer.println("};");
        writer.println("lh.setLoginHandlerRegistration(getHasLoginHandler().addLoginHandler(lh));");
        writer.println("getHasLoginHandler().startLogin(e);");
        writer.outdent();
        writer.println("}");
        writer.outdent();
        writer.println("}");
        writer.println("if (!handled) {");
        writer.indentln("callback.onFailure(caught);");
        writer.println("}");

        writer.outdent();
        writer.println("}");
        writer.outdent();
        writer.println("});");
        writer.outdent();
        writer.println("}");
        writer.println();
    }

    /**
     * Generate methods.
     *
     * @param writer the writer
     */
    private void generateMethods(final SourceWriter writer) {
        for (JMethod method : this.serviceType.getMethods()) {
            generateMethod(writer, method);
        }

        for (JClassType parentType : this.serviceType.getImplementedInterfaces()) {
            for (JMethod method : parentType.getMethods()) {
                generateMethod(writer, method);
            }
        }
    }

    /**
     * Validate.
     *
     * @param logger the logger
     * @param typeName the type name
     * @throws UnableToCompleteException the unable to complete exception
     */
    private void validate(final TreeLogger logger, final String typeName) throws UnableToCompleteException {
        if (!typeName.endsWith("Async")) {
            logger.log(TreeLogger.ERROR,
                format("Asynchronous service's name must ends with \"Async\",Obviously \"%s\" doesn't", typeName));
            throw new UnableToCompleteException();
        }
        TypeOracle typeOracle = this.context.getTypeOracle();
        this.typeName = typeName;
        try {
            this.sourceType = typeOracle.getType(typeName);
            if (this.sourceType.isInterface() == null) {
                logger.log(TreeLogger.ERROR,
                    format("%s is not an interface.", sourceType.getParameterizedQualifiedSourceName()), null);
                throw new UnableToCompleteException();
            }
            JClassType loginableAsyncType = typeOracle.getType(LoginableAsync.class.getName());
            if (!sourceType.getFlattenedSupertypeHierarchy().contains(loginableAsyncType)) {
                logger.log(TreeLogger.ERROR, format("Type: %s not extends LoginableAsync", typeName));
                throw new UnableToCompleteException();
            }
            this.packageName = this.sourceType.getPackage().getName();
        } catch (NotFoundException ex) {
            logger.log(TreeLogger.ERROR, format("validate %s fail", typeName), ex);
            throw new UnableToCompleteException();
        }
        this.serviceName = typeName.substring(0, typeName.lastIndexOf("Async"));
        this.serviceType = typeOracle.findType(serviceName);
        if (serviceType == null) {
            logger.log(TreeLogger.ERROR, format("Could not find remote service type : %s", serviceName));
            throw new UnableToCompleteException();
        }
        if (this.serviceType.isInterface() == null) {
            logger.log(TreeLogger.ERROR,
                format("%s is not an interface.", serviceType.getParameterizedQualifiedSourceName()), null);
            throw new UnableToCompleteException();
        }
        // Find Remote Service
        JClassType remoteServiceType = typeOracle.findType(RemoteService.class.getName());
        if (!serviceType.getFlattenedSupertypeHierarchy().contains(remoteServiceType)) {
            logger.log(TreeLogger.ERROR, format("Type: %s not extends RemoteService", serviceName));
            throw new UnableToCompleteException();
        }
    }
}
