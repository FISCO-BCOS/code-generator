package org.fisco.bcos.codegen.v2.utils;

import com.squareup.javapoet.MethodSpec;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.fisco.bcos.codegen.v2.exceptions.CodeGenException;
import org.fisco.bcos.sdk.abi.wrapper.ABIDefinition;
import org.fisco.bcos.sdk.transaction.tools.JsonUtils;
import org.fisco.bcos.sdk.utils.StringUtils;

public class DocUtils {

    public static Devdoc.Method getMethod(Devdoc devdoc, ABIDefinition functionDefinition) {
        if (devdoc != null && devdoc.getMethods() != null) {
            return devdoc.getMethods().get(functionDefinition.getMethodSignatureAsString());
        }

        return null;
    }

    public static void addMethodComments(Devdoc.Method method, MethodSpec.Builder methodBuilder) {
        if (method == null) {
            return;
        }

        // add comments for method
        if (!StringUtils.isEmpty(method.getDetails())) {
            methodBuilder.addJavadoc("$L \n", method.getDetails());
        }
    }

    public static void addParamsComments(Devdoc.Method method, MethodSpec.Builder methodBuilder) {
        if (method == null) {
            return;
        }

        // add comments for params
        Map<String, String> params = method.getParams();
        if (params != null) {
            for (String p : params.keySet()) {
                if (!StringUtils.isEmpty(params.get(p))) {
                    methodBuilder.addJavadoc("@param $N $L \n", p, params.get(p));
                }
            }
        }
    }

    public static void addReturnsComments(
            String comments, Devdoc.Method method, MethodSpec.Builder methodBuilder) {
        if (method == null) {
            return;
        }

        // add comments for returns
        Map<String, String> returns = method.getReturns();
        if (returns != null) {
            for (String r : returns.keySet()) {
                if (!StringUtils.isEmpty(returns.get(r))) {
                    methodBuilder.addJavadoc("$L $N $L \n", comments, r, returns.get(r));
                }
            }
        }
    }

    public static Devdoc convertDevDoc(File devdocFile) throws CodeGenException, IOException {
        if (devdocFile != null && devdocFile.exists()) {
            byte[] devdocBytes = CodeGenUtils.readBytes(devdocFile);
            return JsonUtils.fromJson(new String(devdocBytes), Devdoc.class);
        } else {
            return null;
        }
    }
}
