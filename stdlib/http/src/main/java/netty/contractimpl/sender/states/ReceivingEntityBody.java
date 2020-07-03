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

package netty.contractimpl.sender.states;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import netty.contract.Constants;
import netty.contract.HttpResponseFuture;
import netty.contractimpl.common.Util;
import netty.message.HttpCarbonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contractimpl.common.states.SenderReqRespStateManager;
import org.wso2.transport.http.netty.contractimpl.common.states.StateUtil;
import org.wso2.transport.http.netty.contractimpl.sender.TargetHandler;

import static org.wso2.transport.http.netty.contract.Constants.IDLE_STATE_HANDLER;
import static org.wso2.transport.http.netty.contract.Constants.IDLE_TIMEOUT_TRIGGERED_WHILE_READING_INBOUND_RESPONSE_BODY;
import static org.wso2.transport.http.netty.contract.Constants.REMOTE_SERVER_CLOSED_WHILE_READING_INBOUND_RESPONSE_BODY;
import static org.wso2.transport.http.netty.contractimpl.common.Util.isKeepAlive;
import static org.wso2.transport.http.netty.contractimpl.common.Util.safelyRemoveHandlers;
import static org.wso2.transport.http.netty.contractimpl.common.states.StateUtil.ILLEGAL_STATE_ERROR;
import static org.wso2.transport.http.netty.contractimpl.common.states.StateUtil.handleIncompleteInboundMessage;

/**
 * State between start and end of inbound response entity body read.
 */
public class ReceivingEntityBody implements SenderState {

    private static final Logger LOG = LoggerFactory.getLogger(ReceivingEntityBody.class);

    private final SenderReqRespStateManager senderReqRespStateManager;
    private final TargetHandler targetHandler;

    ReceivingEntityBody(SenderReqRespStateManager senderReqRespStateManager, TargetHandler targetHandler) {
        this.senderReqRespStateManager = senderReqRespStateManager;
        this.targetHandler = targetHandler;
    }

    @Override
    public void writeOutboundRequestHeaders(netty.message.HttpCarbonMessage httpOutboundRequest) {
        LOG.warn("writeOutboundRequestHeaders {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void writeOutboundRequestEntity(netty.message.HttpCarbonMessage httpOutboundRequest, HttpContent httpContent) {
        LOG.warn("writeOutboundRequestEntity {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void readInboundResponseHeaders(TargetHandler targetHandler, HttpResponse httpInboundResponse) {
        LOG.warn("readInboundResponseHeaders {}", ILLEGAL_STATE_ERROR);
    }

    @Override
    public void readInboundResponseEntityBody(ChannelHandlerContext ctx, HttpContent httpContent,
                                              HttpCarbonMessage inboundResponseMsg) throws Exception {

        if (httpContent instanceof LastHttpContent) {
            StateUtil.setInboundTrailersToNewMessage(((LastHttpContent) httpContent).trailingHeaders(),
                                                     inboundResponseMsg);
            inboundResponseMsg.addHttpContent(httpContent);
            inboundResponseMsg.setLastHttpContentArrived();
            targetHandler.resetInboundMsg();
            Util.safelyRemoveHandlers(targetHandler.getTargetChannel().getChannel().pipeline(), Constants.IDLE_STATE_HANDLER);
            senderReqRespStateManager.state = new EntityBodyReceived(senderReqRespStateManager);

            if (!Util.isKeepAlive(targetHandler.getKeepAliveConfig(), targetHandler.getOutboundRequestMsg())) {
                targetHandler.closeChannel(ctx);
            }
            targetHandler.getConnectionManager().returnChannel(targetHandler.getTargetChannel());
        } else {
            inboundResponseMsg.addHttpContent(httpContent);
        }
    }

    @Override
    public void handleAbruptChannelClosure(TargetHandler targetHandler, netty.contract.HttpResponseFuture httpResponseFuture) {
        handleIncompleteInboundMessage(targetHandler.getInboundResponseMsg(),
                                       Constants.REMOTE_SERVER_CLOSED_WHILE_READING_INBOUND_RESPONSE_BODY);
    }

    @Override
    public void handleIdleTimeoutConnectionClosure(TargetHandler targetHandler,
                                                   HttpResponseFuture httpResponseFuture, String channelID) {
        senderReqRespStateManager.nettyTargetChannel.pipeline().remove(Constants.IDLE_STATE_HANDLER);
        senderReqRespStateManager.nettyTargetChannel.close();
        handleIncompleteInboundMessage(targetHandler.getInboundResponseMsg(),
                                       Constants.IDLE_TIMEOUT_TRIGGERED_WHILE_READING_INBOUND_RESPONSE_BODY);
    }
}
