/*
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

package net.java.sip.communicator.impl.osdependent.jdic;

import net.java.sip.communicator.service.protocol.*;

/**
 * The <tt>ProviderUnRegistration</tt> is used by the systray plugin
 * to make the unregistration to a protocol provider. This operation
 * is implemented within a thread, so that sip-communicator can
 * continue its execution smoothly.
 *
 * @author Nicolas Chamouard
 */
public class ProviderUnRegistration
    extends Thread
{
    /**
     * The protocol provider to whom we want to unregister
     */
    ProtocolProviderService protocolProvider;
    /**
     * The logger for this class.
     */
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProviderUnRegistration.class.getName());

    /**
     * Creates an instance of <tt>ProviderUnRegistration</tt>.
     * @param protocolProvider the provider we want to unregister
     */
    ProviderUnRegistration(ProtocolProviderService protocolProvider)
    {
        this.protocolProvider = protocolProvider;
    }

    /**
     * Start the thread which will unregister to the provider
     */
    @Override
    public void run()
    {
        try
        {
            protocolProvider.unregister();
        }
        catch (OperationFailedException ex)
        {
            int errorCode = ex.getErrorCode();
            if (errorCode == OperationFailedException.GENERAL_ERROR)
            {
                logger.error("Provider could not be unregistered"
                    + " due to the following general error: ", ex);
            }
            else if (errorCode == OperationFailedException.INTERNAL_ERROR)
            {
                logger.error("Provider could not be unregistered"
                    + " due to the following internal error: ", ex);
            }
            else if (errorCode == OperationFailedException.NETWORK_FAILURE)
            {
                logger.error("Provider could not be unregistered"
                        + " due to a network failure: " + ex);
            }
            else if (errorCode == OperationFailedException
                    .INVALID_ACCOUNT_PROPERTIES)
            {
                logger.error("Provider could not be unregistered"
                    + " due to an invalid account property: ", ex);
            }
            else
            {
                logger.error("Provider could not be unregistered.", ex);
            }
        }
    }
 }
