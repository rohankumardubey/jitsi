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
package net.java.sip.communicator.plugin.desktoputil.transparent;

import java.awt.*;
import java.lang.reflect.*;

/**
 *
 * @author Yana Stamcheva
 */
public class AWTUtilitiesWrapper
{
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AWTUtilitiesWrapper.class);

    private static Class<?> awtUtilitiesClass;
    private static Class<?> translucencyClass;
    private static Method mIsTranslucencySupported, mIsTranslucencyCapable,
        mSetWindowShape, mSetWindowOpacity, mSetWindowOpaque;

    public static Object PERPIXEL_TRANSPARENT, TRANSLUCENT, PERPIXEL_TRANSLUCENT;

    static void init()
    {
        try
        {
            awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
            translucencyClass
                = Class.forName("com.sun.awt.AWTUtilities$Translucency");

            if (translucencyClass.isEnum())
            {
                Object[] kinds = translucencyClass.getEnumConstants();
                if (kinds != null)
                {
                    PERPIXEL_TRANSPARENT = kinds[0];
                    TRANSLUCENT = kinds[1];
                    PERPIXEL_TRANSLUCENT = kinds[2];
                }
            }
            mIsTranslucencySupported = awtUtilitiesClass.getMethod(
                    "isTranslucencySupported", translucencyClass);
            mIsTranslucencyCapable = awtUtilitiesClass.getMethod(
                "isTranslucencyCapable", GraphicsConfiguration.class);
            mSetWindowShape = awtUtilitiesClass.getMethod(
                "setWindowShape", Window.class, Shape.class);
            mSetWindowOpacity = awtUtilitiesClass.getMethod(
                "setWindowOpacity", Window.class, float.class);
            mSetWindowOpaque = awtUtilitiesClass.getMethod(
                "setWindowOpaque", Window.class, boolean.class);
        }
        catch (NoSuchMethodException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (SecurityException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (ClassNotFoundException ex)
        {
            logger.info("Not available transparent windows.");
        }
    }

    static
    {
        init();
    }

    private static boolean isSupported(Method method, Object kind)
    {
        if (awtUtilitiesClass == null || method == null)
        {
            return false;
        }
        try
        {
            Object ret = method.invoke(null, kind);
            if (ret instanceof Boolean)
            {
                return ((Boolean)ret).booleanValue();
            }
        }
        catch (IllegalAccessException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (IllegalArgumentException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (InvocationTargetException ex)
        {
            logger.info("Not available transparent windows.");
        }
        return false;
    }

    public static boolean isTranslucencySupported(Object kind)
    {
        if (translucencyClass == null)
            return false;

        return isSupported(mIsTranslucencySupported, kind);
    }

    public static boolean isTranslucencyCapable(GraphicsConfiguration gc)
    {
        return isSupported(mIsTranslucencyCapable, gc);
    }

    private static void set(Method method, Window window, Object value)
    {
        if (awtUtilitiesClass == null || method == null)
        {
            return;
        }
        try
        {
            method.invoke(null, window, value);
        }
        catch (IllegalAccessException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (IllegalArgumentException ex)
        {
            logger.info("Not available transparent windows.");
        }
        catch (InvocationTargetException ex)
        {
            logger.info("Not available transparent windows.");
        }
    }

    public static void setWindowShape(Window window, Shape shape)
    {
        set(mSetWindowShape, window, shape);
    }

    public static void setWindowOpacity(Window window, float opacity)
    {
        set(mSetWindowOpacity, window, Float.valueOf(opacity));
    }

    public static void setWindowOpaque(Window window, boolean opaque)
    {
        set(mSetWindowOpaque, window, Boolean.valueOf(opaque));
    }
}
