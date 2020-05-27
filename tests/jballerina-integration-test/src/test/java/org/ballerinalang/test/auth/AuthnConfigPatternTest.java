/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.ballerinalang.test.auth;

import org.ballerinalang.test.util.HttpResponse;
import org.ballerinalang.test.util.HttpsClientRequest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for authentication config pattern test scenarios.
 * The followings are the config patterns used for authentication handlers:
 * Pattern 1 - authHandlers: [basicAuthHandler19_1]
 * Pattern 2 - authHandlers: [basicAuthHandler19_1, basicAuthHandler19_2]
 * Pattern 3 - authHandlers: [[basicAuthHandler19_1]]
 * Pattern 4 - authHandlers: [[basicAuthHandler19_1], [basicAuthHandler19_3]]
 * Pattern 5 - authHandlers: [[basicAuthHandler19_1, basicAuthHandler19_2], [basicAuthHandler19_3, basicAuthHandler19_4]]
 */
@Test(groups = "auth-test")
public class AuthnConfigPatternTest extends AuthBaseTest {

    private final int servicePort = 20025;

    @Test(description = "Test pattern 1 with user group1")
    public void testUserGroup1ForPattern1() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Y2hhbmFrYToxMjM=");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test1"),
                headers, serverInstance.getServerHome());
        assertOK(response);
    }

    @Test(description = "Test pattern 2 with user group1")
    public void testUserGroup1ForPattern2() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Y2hhbmFrYToxMjM=");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test2"),
                headers, serverInstance.getServerHome());
        assertOK(response);
    }

    @Test(description = "Test pattern 3 with user group1")
    public void testUserGroup1ForPattern3() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Y2hhbmFrYToxMjM=");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test3"),
                headers, serverInstance.getServerHome());
        assertOK(response);
    }

    @Test(description = "Test pattern 4 with user group1")
    public void testUserGroup1ForPattern4() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Y2hhbmFrYToxMjM=");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test4"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 5 with user group1")
    public void testUserGroup1ForPattern5() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Y2hhbmFrYToxMjM=");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test5"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 1 with user group2")
    public void testUserGroup2ForPattern1() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic bGFrbWFsOjQ1Ng==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test1"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 2 with user group2")
    public void testUserGroup2ForPattern2() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic bGFrbWFsOjQ1Ng==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test2"),
                headers, serverInstance.getServerHome());
        assertOK(response);
    }

    @Test(description = "Test pattern 3 with user group2")
    public void testUserGroup2ForPattern3() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic bGFrbWFsOjQ1Ng==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test3"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 4 with user group2")
    public void testUserGroup2ForPattern4() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic bGFrbWFsOjQ1Ng==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test4"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 5 with user group2")
    public void testUserGroup2ForPattern5() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic bGFrbWFsOjQ1Ng==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test5"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 1 with user group3")
    public void testUserGroup3ForPattern1() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWxpY2U6Nzg5");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test1"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 2 with user group3")
    public void testUserGroup3ForPattern2() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWxpY2U6Nzg5");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test2"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 3 with user group3")
    public void testUserGroup3ForPattern3() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWxpY2U6Nzg5");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test3"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 4 with user group3")
    public void testUserGroup3ForPattern4() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWxpY2U6Nzg5");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test4"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 5 with user group3")
    public void testUserGroup3ForPattern5() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YWxpY2U6Nzg5");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test5"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 1 with user group4")
    public void testUserGroup4ForPattern1() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Ym9iOjE1MA==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test1"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 2 with user group4")
    public void testUserGroup4ForPattern2() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Ym9iOjE1MA==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test2"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 3 with user group4")
    public void testUserGroup4ForPattern3() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Ym9iOjE1MA==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test3"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 4 with user group4")
    public void testUserGroup4ForPattern4() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Ym9iOjE1MA==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test4"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }

    @Test(description = "Test pattern 5 with user group4")
    public void testUserGroup4ForPattern5() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic Ym9iOjE1MA==");
        HttpResponse response = HttpsClientRequest.doGet(serverInstance.getServiceURLHttps(servicePort, "echo/test5"),
                headers, serverInstance.getServerHome());
        assertUnauthorized(response);
    }
}
