/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.definition;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.util.FileUtils;
import org.ballerinalang.langserver.util.TestUtil;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.jsonrpc.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test goto definition language server feature.
 */
public class DefinitionTest {
    private Path configRoot;
    private Path sourceRoot;
    private Path projectPath = FileUtils.RES_DIR.resolve("referencesProject");
    protected Gson gson = new Gson();
    protected JsonParser parser = new JsonParser();
    protected Endpoint serviceEndpoint;
    private static final Logger log = LoggerFactory.getLogger(DefinitionTest.class);

    @BeforeClass
    public void init() throws Exception {
        configRoot = FileUtils.RES_DIR.resolve("definition").resolve("expected");
        sourceRoot = FileUtils.RES_DIR.resolve("definition").resolve("sources");
        this.serviceEndpoint = TestUtil.initializeLanguageSever();
    }

    @Test(description = "Test goto definitions", dataProvider = "testDataProvider")
    public void test(String configPath, String configDir) throws IOException {
        JsonObject configObject = FileUtils.fileContentAsObject(configRoot.resolve(configDir)
                .resolve(configPath).toString());
        JsonObject source = configObject.getAsJsonObject("source");
        Path sourcePath = sourceRoot.resolve(source.get("file").getAsString());
        Position position = gson.fromJson(configObject.get("position"), Position.class);
        this.compareResults(sourcePath, position, configObject, sourceRoot);
    }

    @Test(description = "Test Go to definition between two files in same module", enabled = false)
    public void testDifferentFiles() throws IOException {
        log.info("Test textDocument/definition for Two Files in same module");
        JsonObject configObject = FileUtils.fileContentAsObject(configRoot.resolve("multifile")
                .resolve("defMultiFile1.json").toString());
        JsonObject source = configObject.getAsJsonObject("source");
        String dirPath = source.get("dir").getAsString().replace("/", CommonUtil.FILE_SEPARATOR);
        Path sourcePath = projectPath.resolve(dirPath).resolve(source.get("file").getAsString());
        Position position = gson.fromJson(configObject.get("position"), Position.class);
        this.compareResults(sourcePath, position, configObject, projectPath);
    }

    @Test(description = "Test Go to definition between two modules", enabled = false)
    public void testDifferentModule() throws IOException {
        log.info("Test textDocument/definition for two modules");
        JsonObject configObject = FileUtils.fileContentAsObject(configRoot.resolve("multipkg")
                .resolve("defMultiPkg1.json").toString());
        JsonObject source = configObject.getAsJsonObject("source");
        String dirPath = source.get("dir").getAsString().replace("/", CommonUtil.FILE_SEPARATOR);
        Path sourcePath = projectPath.resolve(dirPath).resolve(source.get("file").getAsString());
        Position position = gson.fromJson(configObject.get("position"), Position.class);
        this.compareResults(sourcePath, position, configObject, projectPath);
    }

    private void compareResults(Path sourcePath, Position position, JsonObject configObject, Path root)
            throws IOException {
        TestUtil.openDocument(serviceEndpoint, sourcePath);
        String actualStr = TestUtil.getDefinitionResponse(sourcePath.toString(), position, serviceEndpoint);
        TestUtil.closeDocument(serviceEndpoint, sourcePath);

        JsonArray expected = configObject.getAsJsonArray("result");
        JsonArray actual = parser.parse(actualStr).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("left");
        this.alterExpectedUri(expected, root);
        this.alterActualUri(actual);
        Assert.assertEquals(actual, expected);
    }

    @DataProvider
    public Object[][] testDataProvider() throws IOException {
        log.info("Test textDocument/definition for Basic Cases");
        return new Object[][]{
                // Note: Variable Reference Expressions will be addressed in almost all the test cases
                // Covers the Service Definition Test cases
                {"defService1.json", "service"},
                {"defService2.json", "service"},
                {"defService3.json", "service"},
                {"defService4.json", "service"},
                {"defService5.json", "service"},
                {"defService7.json", "service"},
                // Covers the Function Definition Test cases
                {"defFunction1.json", "function"},
                {"defFunction3.json", "function"},
                {"defFunction4.json", "function"},
                {"defFunction5.json", "function"},
                {"defFunction6.json", "function"},
                {"defFunction7.json", "function"},
                {"defFunction8.json", "function"},
                {"defFunction9.json", "function"},
                {"defFunction10.json", "function"},
                {"defFunction11.json", "function"},
                {"defFunction12.json", "function"},
                // Following tests covers the type descriptor and Module Type Definitions
                // Covers Simple Type Descriptor
//                {"defTypeDesc1.json", "typedescriptor"},
//                {"defTypeDesc2.json", "typedescriptor"},
                // Covers List Type Descriptor
//                {"defTypeDesc3.json", "typedescriptor"},
//                {"defTypeDesc4.json", "typedescriptor"},
//                {"defTypeDesc5.json", "typedescriptor"},
//                {"defTypeDesc6.json", "typedescriptor"},
//                {"defTypeDesc7.json", "typedescriptor"},
//                {"defTypeDesc8.json", "typedescriptor"},
                // Covers Map Type Descriptor
//                {"defTypeDesc9.json", "typedescriptor"},
//                {"defTypeDesc10.json", "typedescriptor"},
//                {"defTypeDesc11.json", "typedescriptor"},
//                {"defTypeDesc12.json", "typedescriptor"},
//                {"defTypeDesc13.json", "typedescriptor"},
//                {"defTypeDesc14.json", "typedescriptor"},
//                {"defTypeDesc15.json", "typedescriptor"},
//                {"defTypeDesc16.json", "typedescriptor"},
                // Covers Function Type Descriptor
//                {"defTypeDesc17.json", "typedescriptor"},
//                {"defTypeDesc18.json", "typedescriptor"},
//                {"defTypeDesc19.json", "typedescriptor"},
//                {"defTypeDesc20.json", "typedescriptor"},
//                {"defTypeDesc21.json", "typedescriptor"},
                // Covers Object Type Descriptor
//                {"defTypeDesc22.json", "typedescriptor"},
//                {"defTypeDesc23.json", "typedescriptor"},
//                {"defTypeDesc24.json", "typedescriptor"},
//                {"defTypeDesc25.json", "typedescriptor"},
//                {"defTypeDesc26.json", "typedescriptor"},
//                {"defTypeDesc27.json", "typedescriptor"},
//                {"defTypeDesc28.json", "typedescriptor"},
//                {"defTypeDesc29.json", "typedescriptor"},
//                {"defTypeDesc30.json", "typedescriptor"},
//                {"defTypeDesc31.json", "typedescriptor"},
//                {"defTypeDesc32.json", "typedescriptor"},
//                {"defTypeDesc33.json", "typedescriptor"},
//                {"defTypeDesc34.json", "typedescriptor"},
//                {"defTypeDesc35.json", "typedescriptor"},
//                {"defTypeDesc36.json", "typedescriptor"},
//                {"defTypeDesc37.json", "typedescriptor"},
                // Covers Future type Descriptor
//                {"defTypeDesc38.json", "typedescriptor"},
//                {"defTypeDesc39.json", "typedescriptor"},
//                {"defTypeDesc40.json", "typedescriptor"},
                // Covers TypeDesc Type Descriptor
//                {"defTypeDesc41.json", "typedescriptor"},
//                {"defTypeDesc42.json", "typedescriptor"},
//                {"defTypeDesc43.json", "typedescriptor"},
                // Covers Handle Type Descriptor
//                {"defTypeDesc44.json", "typedescriptor"},
                // Covers Union Type Descriptor
//                {"defTypeDesc45.json", "typedescriptor"},
//                {"defTypeDesc46.json", "typedescriptor"},
//                {"defTypeDesc47.json", "typedescriptor"},
                // Optional Type Descriptor
//                {"defTypeDesc48.json", "typedescriptor"},
//                {"defTypeDesc49.json", "typedescriptor"},
//                {"defTypeDesc50.json", "typedescriptor"},
                // Covers the Module Level variable declarations
                {"defModuleVar1.json", "modulevardecl"},
                {"defModuleVar2.json", "modulevardecl"},
                {"defModuleVar3.json", "modulevardecl"},
                {"defModuleVar4.json", "modulevardecl"},
                // Covers the Module Level variable declarations
                {"defModuleConst1.json", "moduleconst"},
                {"defModuleConst2.json", "moduleconst"},
                // Covers the List Constructor Expression
                {"defListConstructorExpr1.json", "expression"},
                // Covers the Mapping Constructor Expression
                {"defMappingConstructorExpr1.json", "expression"},
                {"defMappingConstructorExpr2.json", "expression"},
                {"defServiceConstructorExpr1.json", "expression"},
                // Covers the String Template Expression
                {"defStringTemplateExpr1.json", "expression"},
                // Covers the New Expression
                {"defNewExpr1.json", "expression"},
                {"defNewExpr2.json", "expression"},
                {"defNewExpr3.json", "expression"},
                // Covers Function Call Expression
                {"defFunctionCallExpr1.json", "expression"},
                {"defFunctionCallExpr2.json", "expression"},
                {"defFunctionCallExpr3.json", "expression"},
                {"defFunctionCallExpr4.json", "expression"},
                {"defFunctionCallExpr5.json", "expression"},
                {"defFunctionCallExpr6.json", "expression"},
                // Covers Method Call Expression
                {"defMethodCallExpr1.json", "expression"},
                {"defMethodCallExpr2.json", "expression"},
                {"defMethodCallExpr3.json", "expression"},
                // Covers Anon Function Expression
                {"defAnonFunctionExpr1.json", "expression"},
                {"defAnonFunctionExpr2.json", "expression"},
                {"defAnonFunctionExpr3.json", "expression"},
                {"defAnonFunctionExpr4.json", "expression"},
                {"defAnonFunctionExpr5.json", "expression"},
                {"defAnonFunctionExpr6.json", "expression"},
                {"defAnonFunctionExpr7.json", "expression"},
                // Covers Arrow Function Expression
                {"defArrowFunctionExpr1.json", "expression"},
                // Covers Type Cast Expression
                {"defTypeCastExpr1.json", "expression"},
                {"defTypeCastExpr2.json", "expression"},
                // Covers Typeof Expression
                {"defTypeOfExpr1.json", "expression"},
                // Covers Unary Expression
                {"defUnaryExpr1.json", "expression"},
                // Covers Multiplicative Expression
                {"defMultiplicativeExpr1.json", "expression"},
                {"defMultiplicativeExpr2.json", "expression"},
                // Covers Additive Expression
                {"defAdditiveExpr1.json", "expression"},
                {"defAdditiveExpr2.json", "expression"},
                // Covers Shift Expression
                {"defShiftExpr1.json", "expression"},
                {"defShiftExpr2.json", "expression"},
                // Covers Range Expression
                {"defRangeExpr1.json", "expression"},
                {"defRangeExpr2.json", "expression"},
                // Covers Numeric Expression
                {"defNumericExpr1.json", "expression"},
                {"defNumericExpr2.json", "expression"},
                // Covers Equality Expression
                {"defEqualityExpr1.json", "expression"},
                {"defEqualityExpr2.json", "expression"},
                // Covers Logical Expression
                {"defLogicalExpr2.json", "expression"},
                {"defLogicalExpr1.json", "expression"},
                // Covers Conditional Expression
                {"defConditionalExpr1.json", "expression"},
                {"defConditionalExpr2.json", "expression"},
                {"defConditionalExpr3.json", "expression"},
                {"defConditionalExpr4.json", "expression"},
                {"defConditionalExpr5.json", "expression"},
                // Covers Check Expression
                {"defCheckExpr1.json", "expression"},
                // Covers Check Panic Expression
                {"defCheckPanicExpr1.json", "expression"},
                // Covers Trap Expression
                {"defTrapExpr1.json", "expression"},
                // Covers the Start Action
                {"defStartAction1.json", "action"},
                {"defStartAction2.json", "action"}, // Remote method call action is also similar
                // Covers the wait action
                {"defWaitAction1.json", "action"},
                {"defWaitAction2.json", "action"},
                {"defWaitAction3.json", "action"},
                // Covers Worker Send Action
//                {"defSyncSendAction1.json", "action"},
//                {"defSyncSendAction2.json", "action"},
//                {"defAsyncSendAction1.json", "action"},
//                {"defAsyncSendAction2.json", "action"},
                // Covers Worker Receive Action
//                {"defReceiveAction1.json", "action"},
                // Covers Flush Action
//                {"defFlushAction1.json", "action"},
                // Covers Remote Action
                {"defRemoteAction1.json", "action"},
                {"defRemoteAction2.json", "action"},
                // Covers Variable Definition Statement with list Binding pattern
                {"defVarDefStmt1.json", "vardefstatement"},
                {"defVarDefStmt2.json", "vardefstatement"},
                {"defVarDefStmt3.json", "vardefstatement"},
                {"defVarDefStmt4.json", "vardefstatement"},
                // Covers Variable Definition Statement with map Binding pattern
                {"defVarDefStmt5.json", "vardefstatement"},
                {"defVarDefStmt6.json", "vardefstatement"},
                {"defVarDefStmt7.json", "vardefstatement"},
                {"defVarDefStmt8.json", "vardefstatement"},
                // Covers Variable Definition Statement with final and var
                {"defVarDefStmt9.json", "vardefstatement"},
                {"defVarDefStmt10.json", "vardefstatement"},
                {"defVarDefStmt11.json", "vardefstatement"},
                // Covers Assignment Statement
                {"defAssignment1.json", "assignment"},
                {"defAssignment2.json", "assignment"},
                {"defAssignment3.json", "assignment"},
                {"defAssignment4.json", "assignment"},
                {"defAssignment5.json", "assignment"},
                {"defAssignment6.json", "assignment"},
                {"defAssignment7.json", "assignment"},
                {"defAssignment8.json", "assignment"},
                {"defAssignment9.json", "assignment"},
                {"defAssignment10.json", "assignment"},
                {"defAssignment11.json", "assignment"},
                {"defAssignment12.json", "assignment"},
                {"defAssignment13.json", "assignment"},
                // Action Statement is covered in the Action section
                // Covers the Call Statement
                {"defCallStmt1.json", "callstatement"},
                {"defCallStmt2.json", "callstatement"},
                {"defCallStmt3.json", "callstatement"},
                {"defCallStmt4.json", "callstatement"},
                {"defCallStmt5.json", "callstatement"},
                {"defCallStmt6.json", "callstatement"},
                {"defCallStmt7.json", "callstatement"},
                // Covers the Conditional Statement
                {"defConditionalStmt1.json", "conditional"},
                {"defConditionalStmt2.json", "conditional"},
                {"defConditionalStmt3.json", "conditional"},
                {"defConditionalStmt4.json", "conditional"},
                {"defConditionalStmt5.json", "conditional"},
                // Covers the Match statement - variable name Binding pattern
                {"defMatchStmt1.json", "matchstmt"},
                // Covers the Match statement - List Binding pattern
                {"defMatchStmt2.json", "matchstmt"},
                {"defMatchStmt3.json", "matchstmt"},
                {"defMatchStmt4.json", "matchstmt"},
                // Covers the Match statement - Mapping Binding pattern
                {"defMatchStmt5.json", "matchstmt"},
                {"defMatchStmt6.json", "matchstmt"},
                {"defMatchStmt7.json", "matchstmt"},
                // Covers the Match statement - Constant pattern
                {"defMatchStmt8.json", "matchstmt"},
                {"defMatchStmt9.json", "matchstmt"},
                // Covers the Match Statement - List Pattern
                {"defMatchStmt10.json", "matchstmt"},
                // Covers the Match Statement - Mapping Pattern
                {"defMatchStmt11.json", "matchstmt"},
                //Covers Match statement expression
                {"defMatchStmt12.json", "matchstmt"},
                // TODO: Need to cover the match statement error pattern
                // Covers Foreach Statement
                {"defForeach1.json", "foreach"},
                {"defForeach2.json", "foreach"},
                {"defForeach3.json", "foreach"},
                {"defForeach4.json", "foreach"},
                {"defForeach5.json", "foreach"},
                {"defForeach6.json", "foreach"},
                {"defForeach7.json", "foreach"},
                {"defForeach8.json", "foreach"},
                {"defForeach9.json", "foreach"},
                {"defForeach10.json", "foreach"},
                {"defForeach11.json", "foreach"},
                // Covers While Statement
                {"defWhileStmt1.json", "while"},
                {"defWhileStmt2.json", "while"},
                // Covers Panic Statement
                {"defPanicStmt1.json", "panic"},
                // Covers Return Statement
                {"defReturnStmt1.json", "return"},
        };
    }
    
    @AfterClass
    public void shutDownLanguageServer() throws IOException {
        TestUtil.shutdownLanguageServer(this.serviceEndpoint);
    }

    private void alterExpectedUri(JsonArray expected, Path root) throws IOException {
        for (JsonElement jsonElement : expected) {
            JsonObject item = jsonElement.getAsJsonObject();
            String[] uriComponents = item.get("uri").toString().replace("\"", "").split("/");
            Path expectedPath = Paths.get(root.toUri());
            for (String uriComponent : uriComponents) {
                expectedPath = expectedPath.resolve(uriComponent);
            }
            item.remove("uri");
            item.addProperty("uri", expectedPath.toFile().getCanonicalPath());
        }
    }

    private void alterActualUri(JsonArray actual) throws IOException {
        for (JsonElement jsonElement : actual) {
            JsonObject item = jsonElement.getAsJsonObject();
            String uri = item.get("uri").toString().replace("\"", "");
            String canonicalPath = new File(URI.create(uri)).getCanonicalPath();
            item.remove("uri");
            item.addProperty("uri", canonicalPath);
        }
    }
}