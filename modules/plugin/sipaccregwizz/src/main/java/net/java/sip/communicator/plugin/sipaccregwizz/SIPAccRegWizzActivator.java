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
package net.java.sip.communicator.plugin.sipaccregwizz;

import java.util.*;

import lombok.extern.slf4j.*;
import net.java.sip.communicator.service.browserlauncher.*;
import net.java.sip.communicator.service.certificate.*;
import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.protocol.*;

import net.java.sip.communicator.util.osgi.*;
import org.jitsi.service.configuration.*;
import org.osgi.framework.*;

/**
 * Registers the <tt>SIPAccountRegistrationWizard</tt> in the UI Service.
 *
 * @author Yana Stamcheva
 */
@Slf4j
public class SIPAccRegWizzActivator extends DependentActivator
{
    /**
     * OSGi bundle context.
     */
    public static BundleContext bundleContext;

    private static WizardContainer wizardContainer;

    /**
     * A reference to the configuration service.
     */
    private static ConfigurationService configService;

    private static BrowserLauncherService browserLauncherService;

    private static SIPAccountRegistrationWizard sipWizard;

    private static UIService uiService;

    private static CertificateService certService;

    public SIPAccRegWizzActivator()
    {
        super(
            UIService.class,
            ConfigurationService.class,
            BrowserLauncherService.class,
            CertificateService.class
        );
    }

    /**
     * Starts this bundle.
     */
    @Override
    public void startWithServices(BundleContext bundleContext)
    {
        SIPAccRegWizzActivator.bundleContext = bundleContext;
        uiService = getService(UIService.class);
        configService = getService(ConfigurationService.class);
        browserLauncherService = getService(BrowserLauncherService.class);
        certService = getService(CertificateService.class);

        wizardContainer = uiService.getAccountRegWizardContainer();

        sipWizard = new SIPAccountRegistrationWizard(wizardContainer);

        Hashtable<String, String> containerFilter = new Hashtable<>();

        containerFilter.put(
                ProtocolProviderFactory.PROTOCOL,
                ProtocolNames.SIP);

        bundleContext.registerService(
            AccountRegistrationWizard.class.getName(),
            sipWizard,
            containerFilter);
    }

    /**
     * Returns the <tt>ProtocolProviderFactory</tt> for the SIP protocol.
     *
     * @return the <tt>ProtocolProviderFactory</tt> for the SIP protocol
     */
    public static ProtocolProviderFactory getSIPProtocolProviderFactory()
    {
        ServiceReference[] serRefs = null;
        String osgiFilter
            = "("
                + ProtocolProviderFactory.PROTOCOL
                + "="
                + ProtocolNames.SIP
                + ")";

        try
        {
            serRefs
                = bundleContext.getServiceReferences(
                        ProtocolProviderFactory.class.getName(),
                        osgiFilter);
        }
        catch (InvalidSyntaxException ex)
        {
            logger.error("SIPAccRegWizzActivator : " + ex);
            return null;
        }

        return
            (serRefs == null)
                ? null
                : (ProtocolProviderFactory)
                    bundleContext.getService(serRefs[0]);
    }

    /**
     * Returns the <tt>UIService</tt>.
     *
     * @return the <tt>UIService</tt>
     */
    public static UIService getUIService()
    {
        return uiService;
    }

    /**
     * Returns the <tt>BrowserLauncherService</tt> obtained from the bundle
     * context.
     * @return the <tt>BrowserLauncherService</tt> obtained from the bundle
     * context
     */
    public static BrowserLauncherService getBrowserLauncher()
    {
        return browserLauncherService;
    }

    /**
     * Returns the <tt>ConfigurationService</tt> obtained from the bundle
     * context.
     * @return the <tt>ConfigurationService</tt> obtained from the bundle
     * context
     */
    public static ConfigurationService getConfigurationService()
    {
        return configService;
    }

    /**
     * Returns the <tt>CertificateService</tt> obtained from the bundle
     * context.
     * @return the <tt>CertificateService</tt> obtained from the bundle
     * context
     */
    public static CertificateService getCertificateService()
    {
        return certService;
    }

    /**
     * Indicates if the advanced account configuration is currently disabled.
     *
     * @return <tt>true</tt> if the advanced account configuration is disabled,
     * otherwise returns false
     */
    public static boolean isAdvancedAccountConfigDisabled()
    {
        // Load the "net.java.sip.communicator.impl.gui.main.account
        // .ADVANCED_CONFIG_DISABLED" property.
        String advancedConfigDisabledDefaultProp
            = Resources.getResources().getSettingsString(
                "impl.gui.main.account.ADVANCED_CONFIG_DISABLED");

        boolean isAdvancedConfigDisabled = false;

        if (advancedConfigDisabledDefaultProp != null)
            isAdvancedConfigDisabled
                = Boolean.parseBoolean(advancedConfigDisabledDefaultProp);

        return getConfigurationService().getBoolean(
                "net.java.sip.communicator.impl.gui.main.account." +
                "ADVANCED_CONFIG_DISABLED",
                isAdvancedConfigDisabled);
    }
}
