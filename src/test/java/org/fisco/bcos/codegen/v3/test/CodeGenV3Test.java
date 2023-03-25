package org.fisco.bcos.codegen.v3.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import org.fisco.bcos.codegen.CodeGenMain;
import org.junit.Assert;
import org.junit.Test;

public class CodeGenV3Test {
    private static final String JAVA_OUTPUT_DIR = "sdk";
    private static final String DEFAULT_PACKAGE = "com";

    static class MyJavaFileObject extends SimpleJavaFileObject {

        private String source;
        private ByteArrayOutputStream outputStream;

        /**
         * Construct a SimpleJavaFileObject of the given kind and with the given URI.
         *
         * @param uri the URI for this file object
         * @param kind the kind of this file object
         */
        protected MyJavaFileObject(URI uri, Kind kind) {
            super(uri, kind);
        }

        public MyJavaFileObject(String name, String source) {
            this(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            outputStream = new ByteArrayOutputStream();
            return outputStream;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if (source == null) {
                throw new IOException("Empty source");
            }
            return source;
        }
    }

    @Test
    public void complexABICodeGen() throws IOException {
        final String COMPLEX_ABI_FILE = "ComplexCodecTest.abi";
        final String COMPLEX_NAME = "ComplexCodecTest";
        codeGenTest(COMPLEX_ABI_FILE, COMPLEX_NAME);
    }

    @Test
    public void tableABICodeGen() throws IOException {
        final String ABI_FILE = "Table.abi";
        final String CONTRACT_NAME = "Table";
        codeGenTest(ABI_FILE, CONTRACT_NAME);
    }

    @Test
    public void voteABICodeGen() throws IOException {
        final String ABI_FILE = "AnonymousVoting.abi";
        final String CONTRACT_NAME = "AnonymousVoting";
        codeGenTest(ABI_FILE, CONTRACT_NAME);
    }

    @Test
    public void codecTestABICodeGen() throws IOException {
        final String ABI_FILE = "CodecTest.abi";
        final String CONTRACT_NAME = "CodecTest";
        codeGenTest(ABI_FILE, CONTRACT_NAME);
    }

    private void codeGenTest(String abiFileName, String contractName) throws IOException {
        String abiFile = CodeGenV3Test.class.getClassLoader().getResource(abiFileName).getPath();
        String javaOutPut = new File(abiFile).getParent() + File.separator + JAVA_OUTPUT_DIR;
        CodeGenMain.main(
                Arrays.asList(
                                "-v",
                                "V3",
                                "-a",
                                abiFile,
                                "-b",
                                abiFile,
                                "-s",
                                abiFile,
                                "-p",
                                DEFAULT_PACKAGE,
                                "-o",
                                javaOutPut,
                                "-e")
                        .toArray(new String[0]));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaFileManager javaFileManager =
                compiler.getStandardFileManager(collector, null, StandardCharsets.UTF_8);
        String codeFileName =
                javaOutPut
                        + File.separator
                        + File.separator
                        + DEFAULT_PACKAGE
                        + File.separator
                        + contractName
                        + ".java";

        File codeFile = new File(codeFileName);
        Long fileLength = codeFile.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        FileInputStream in = new FileInputStream(codeFile);
        in.read(fileContent);
        in.close();
        String code = new String(fileContent, StandardCharsets.UTF_8);

        JavaFileObject myJavaFileObject = new MyJavaFileObject(contractName, code);
        Boolean call =
                compiler.getTask(
                                null,
                                javaFileManager,
                                collector,
                                null,
                                null,
                                Collections.singletonList(myJavaFileObject))
                        .call();
        collector.getDiagnostics().forEach(log -> System.out.println(log.toString()));
        Assert.assertTrue(call);
    }
}
