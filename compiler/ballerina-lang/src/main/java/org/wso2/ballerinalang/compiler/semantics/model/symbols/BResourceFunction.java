/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ballerinalang.compiler.semantics.model.symbols;

import org.wso2.ballerinalang.compiler.semantics.model.types.BInvokableType;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.diagnotic.DiagnosticPos;

import java.util.List;

/**
 * {@code BResourceFunction} represents a resource function in Ballerina.
 *
 * @since 2.0
 */
public class BResourceFunction extends BAttachedFunction {

    public List<Name> resourcePath;
    public Name accessor;

    public BResourceFunction(Name funcName, BInvokableSymbol symbol, BInvokableType type,
                             List<Name> resourcePath, Name accessor, DiagnosticPos pos) {
        super(funcName, symbol, type, pos);
        this.resourcePath = resourcePath;
        this.accessor = accessor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("resource function ").append(accessor).append(" ");
        resourcePath.forEach(r -> sb.append(r));
        sb.append(" ").append(type.getTypeSignature());
        return sb.toString();
    }
}
