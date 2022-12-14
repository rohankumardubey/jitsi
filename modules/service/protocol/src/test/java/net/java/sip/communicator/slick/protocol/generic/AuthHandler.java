package net.java.sip.communicator.slick.protocol.generic;/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * Implementation for the authorization handler.
 */
public class AuthHandler
implements AuthorizationHandler
{

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthHandler.class);

    public AuthorizationResponse processAuthorisationRequest(
        AuthorizationRequest req, Contact sourceContact)
    {
        logger.trace("processAuthorisationRequest " + req + " " +
            sourceContact);

        return new AuthorizationResponse(AuthorizationResponse.ACCEPT, "");
    }

    public AuthorizationRequest createAuthorizationRequest(Contact contact)
    {
        logger.trace("createAuthorizationRequest " + contact);
        return new AuthorizationRequest();
    }

    public void processAuthorizationResponse(
        AuthorizationResponse response, Contact sourceContact)
    {
        logger.debug("auth response from: " +
            sourceContact.getAddress() + " " +
            response.getResponseCode().getCode());
    }
}
