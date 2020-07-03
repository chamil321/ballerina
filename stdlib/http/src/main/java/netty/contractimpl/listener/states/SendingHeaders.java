/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package netty.contractimpl.listener.states;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import netty.contract.Constants;
import netty.contract.HttpResponseFuture;
import netty.contract.ServerConnectorFuture;
import netty.contract.config.ChunkConfig;
import netty.contract.exceptions.ServerConnectorException;
import netty.contractimpl.HttpOutboundRespListener;
import netty.contractimpl.common.Util;
import netty.message.HttpCarbonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contractimpl.common.states.StateUtil;

import static org.wso2.transport.http.netty.contract.Constants.CHUNKING_CONFIG;
import static org.wso2.transport.http.netty.contract.Constants.IDLE_TIMEOUT_TRIGGERED_WHILE_WRITING_OUTBOUND_RESPONSE_HEADERS;
import static org.wso2.transport.http.netty.contract.Constants.REMOTE_CLIENT_CLOSED_BEFORE_INITIATING_OUTBOUND_RESPONSE;
import static org.wso2.transport.http.netty.contract.Constants.REMOTE_CLIENT_CLOSED_WHILE_WRITING_OUTBOUND_RESPONSE_HEADERS;
import static org.wso2.transport.http.netty.contractimpl.common.Util.createHttpResponse;
import static org.wso2.transport.http.netty.contractimpl.common.Util.isLastHttpContent;
import static org.wso2.transport.http.netty.contractimpl.common.Util.setupChunkedRequest;
import static org.wso2.transport.http.netty.contractimpl.common.states.StateUtil.ILLEGAL_STATE_ERROR;
import static org.wso2.transport.http.netty.contractimpl.common.states.StateUtil.checkChunkingCompatibility;
import static org.wso2.transport.http.netty.contractimpl.common.states.StateUtil.notifyIfHeaderWriteFailure;

/**
 * State between start and end of outbound response headers write.
 */
public class SendingHeaders implements ListenerState {

    private static final Logger LOG = LoggerFactory.getLogger(SendingHeaders.class);

    private final netty.contractimpl.HttpOutboundRespListener outboundResponseListener;
    boolean keepAlive;
    private final ListenerReqRespStateManager listenerReqRespStateManager;
    netty.contract.config.ChunkConfig chunkConfig;
    netty.contract.HttpResponseFuture outboundRespStatusFuture;

    public SendingHeaders(ListenerReqRespStateManager listenerReqRespStateManager,
                          netty.contractimpl.HttpOutboundRespListener outboundResponseListener) {
        this.listenerReqRespStateManager = listenerReqRespStateManager;
        this.outboundResponseListener = outboundResponseListener;
        this.chunkConfig = outboundResponseListener.getChunkConfig();
        this.keepAlive = outboundResponseListener.isKeepAlive();
    }

    @Override
    public void readInboundRequestHeaders(netty.message.HttpCarbonMessage inboundRequestMsg, HttpRequest inboundRequestHeaders) {
        LOG.warn("readInboundRequestHeaders {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void readInboundRequestBody(Object inboundRequestEntityBody) throws ServerConnectorException {
        LOG.warn("readInboundRequestBody {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void writeOutboundResponseBody(HttpOutboundRespListener outboundResponseListener,
                                          netty.message.HttpCarbonMessage outboundResponseMsg, HttpContent httpContent) {
        LOG.warn("writeOutboundResponseBody {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void handleAbruptChannelClosure(netty.contract.ServerConnectorFuture serverConnectorFuture) {
        // OutboundResponseStatusFuture will be notified asynchronously via OutboundResponseListener.
        LOG.error(Constants.REMOTE_CLIENT_CLOSED_WHILE_WRITING_OUTBOUND_RESPONSE_HEADERS);
    }

    @Override
    public ChannelFuture handleIdleTimeoutConnectionClosure(ServerConnectorFuture serverConnectorFuture,
                                                            ChannelHandlerContext ctx) {
        // OutboundResponseStatusFuture will be notified asynchronously via OutboundResponseListener.
        LOG.error(Constants.IDLE_TIMEOUT_TRIGGERED_WHILE_WRITING_OUTBOUND_RESPONSE_HEADERS);
        return null;
    }

    @Override
    public void writeOutboundResponseHeaders(netty.message.HttpCarbonMessage outboundResponseMsg, HttpContent httpContent) {
        netty.contract.config.ChunkConfig responseChunkConfig = outboundResponseMsg.getProperty(
                Constants.CHUNKING_CONFIG) != null ?
                (netty.contract.config.ChunkConfig) outboundResponseMsg.getProperty(Constants.CHUNKING_CONFIG) : null;
        if (responseChunkConfig != null) {
            this.setChunkConfig(responseChunkConfig);
        }
        outboundRespStatusFuture = outboundResponseListener.getInboundRequestMsg().getHttpOutboundRespStatusFuture();
        String httpVersion = outboundResponseListener.getRequestDataHolder().getHttpVersion();

        if (Util.isLastHttpContent(httpContent)) {
            if (chunkConfig == netty.contract.config.ChunkConfig.ALWAYS && checkChunkingCompatibility(httpVersion, chunkConfig)) {
                writeHeaders(outboundResponseMsg, keepAlive, outboundRespStatusFuture);
                writeResponse(outboundResponseMsg, httpContent, true);
                return;
            }
        } else {
            if ((chunkConfig == netty.contract.config.ChunkConfig.ALWAYS || chunkConfig == netty.contract.config.ChunkConfig.AUTO) && (
                    checkChunkingCompatibility(httpVersion, chunkConfig))) {
                writeHeaders(outboundResponseMsg, keepAlive, outboundRespStatusFuture);
                writeResponse(outboundResponseMsg, httpContent, true);
                return;
            }
        }
        writeResponse(outboundResponseMsg, httpContent, false);
    }

    private void writeResponse(netty.message.HttpCarbonMessage outboundResponseMsg, HttpContent httpContent, boolean headersWritten) {
        listenerReqRespStateManager.state
                = new SendingEntityBody(listenerReqRespStateManager, outboundRespStatusFuture, headersWritten);
        listenerReqRespStateManager.writeOutboundResponseBody(outboundResponseListener, outboundResponseMsg,
                                                                         httpContent);
    }

    private void writeHeaders(netty.message.HttpCarbonMessage outboundResponseMsg, boolean keepAlive,
                              HttpResponseFuture outboundRespStatusFuture) {
        Util.setupChunkedRequest(outboundResponseMsg);
        StateUtil.addTrailerHeaderIfPresent(outboundResponseMsg);
        ChannelFuture outboundHeaderFuture = writeResponseHeaders(outboundResponseMsg, keepAlive);
        notifyIfHeaderWriteFailure(outboundRespStatusFuture, outboundHeaderFuture,
                                   Constants.REMOTE_CLIENT_CLOSED_BEFORE_INITIATING_OUTBOUND_RESPONSE);
    }

    void setChunkConfig(ChunkConfig chunkConfig) {
        this.chunkConfig = chunkConfig;
    }

    ChannelFuture writeResponseHeaders(HttpCarbonMessage outboundResponseMsg, boolean keepAlive) {
        HttpResponse response = Util.createHttpResponse(outboundResponseMsg,
                                                        outboundResponseListener.getRequestDataHolder().getHttpVersion(),
                                                        outboundResponseListener.getServerName(), keepAlive);
        return outboundResponseListener.getSourceContext().write(response);
    }
}
