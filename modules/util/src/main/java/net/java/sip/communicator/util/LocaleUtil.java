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
package net.java.sip.communicator.util;

import java.util.*;

public class LocaleUtil
{
    /**
     * Constructs a new <tt>Locale</tt> instance from a specific locale
     * identifier which can either be a two-letter language code or contain a
     * two-letter language code and a two-letter country code in the form
     * <tt>&lt;language&gt;_&lt;country&gt;</tt>.
     *
     * @param localeId the locale identifier describing the new <tt>Locale</tt>
     * instance to be created
     * @return a new <tt>Locale</tt> instance with language and country (if
     * specified) matching the given locale identifier
     */
    public static Locale getLocale(String localeId)
    {
        int underscoreIndex = localeId.indexOf('_');
        String language;
        String country;

        if (underscoreIndex == -1)
        {
            language = localeId;
            country = "";
        }
        else
        {
            language = localeId.substring(0, underscoreIndex);
            country = localeId.substring(underscoreIndex + 1);
        }
        return new Locale(language, country);
    }
}
