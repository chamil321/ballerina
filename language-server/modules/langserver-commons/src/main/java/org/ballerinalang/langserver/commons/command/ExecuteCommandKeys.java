/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.commons.command;

import org.ballerinalang.langserver.commons.LSContext;
import org.ballerinalang.langserver.commons.capability.LSClientCapabilities;
import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentManager;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.List;

/**
 * Keys associated to execute command operation.
 *
 * @since v0.964.0
 */
public class ExecuteCommandKeys {

    public static final LSContext.Key<WorkspaceDocumentManager> DOCUMENT_MANAGER_KEY = new LSContext.Key<>();

    public static final LSContext.Key<String> FILE_URI_KEY = new LSContext.Key<>();

    public static final LSContext.Key<Position> POSITION_START_KEY = new LSContext.Key<>();

    public static final LSContext.Key<List<Object>> COMMAND_ARGUMENTS_KEY = new LSContext.Key<>();

    public static final LSContext.Key<LanguageServer> LANGUAGE_SERVER_KEY = new LSContext.Key<>();

    public static final LSContext.Key<LanguageClient> LANGUAGE_CLIENT_KEY = new LSContext.Key<>();

    public static final LSContext.Key<String> PKG_NAME_KEY = new LSContext.Key<>();

    public static final LSContext.Key<LSClientCapabilities> LS_CLIENT_CAPABILITIES_KEY = new LSContext.Key<>();

    private ExecuteCommandKeys() {
    }
}