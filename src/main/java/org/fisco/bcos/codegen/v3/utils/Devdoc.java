package org.fisco.bcos.codegen.v3.utils;

import java.util.Map;

public class Devdoc {
    private String details;
    private Map<String, Method> methods;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, Method> methods) {
        this.methods = methods;
    }

    public static class Method {
        private String details;
        private Map<String, String> params;
        private Map<String, String> returns;

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public Map<String, String> getReturns() {
            return returns;
        }

        public void setReturns(Map<String, String> returns) {
            this.returns = returns;
        }
    }
}
