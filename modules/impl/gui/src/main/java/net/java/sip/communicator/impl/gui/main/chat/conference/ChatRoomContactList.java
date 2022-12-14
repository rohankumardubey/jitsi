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
package net.java.sip.communicator.impl.gui.main.chat.conference;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.main.chat.*;
import net.java.sip.communicator.plugin.desktoputil.*;
import net.java.sip.communicator.service.contactlist.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.skin.*;

/**
 * DeafultContactlist used to display <code>JList</code>s with contacts.
 *
 * @author Damian Minkov
 * @author Yana Stamcheva
 * @author Adam Netocny
 */
public class ChatRoomContactList
    extends JList<ChatContact<?>>
    implements Skinnable
{
    private static final long serialVersionUID = 0L;

    /**
     * List cell renderer.
     */
    ChatContactCellRenderer renderer = new ChatContactCellRenderer();

    /**
     * Creates an instance of <tt>DefaultContactList</tt>.
     */
    public ChatRoomContactList()
    {
        this.setOpaque(false);

        this.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        this.setDragEnabled(true);
        this.setTransferHandler(new ChatRoomContactListTransferHandler(this));
        this.setCellRenderer(renderer);
    }

    /**
     * Checks if the given contact is currently active.
     * Dummy method used and overridden from classes extending this
     * functionality such as ContactList.
     *
     * @param metaContact the <tt>MetaContact</tt> to verify
     * @return TRUE if the given <tt>MetaContact</tt> is active, FALSE -
     * otherwise
     */
    public boolean isMetaContactActive(MetaContact metaContact)
    {
        return false;
    }

    /**
     * Checks whether the group is closed.
     * Dummy method used and overridden from classes extending this
     * functionality such as ContactList.
     *
     * @param group The group to check.
     * @return True if the group is closed, false - otherwise.
     */
    public boolean isGroupClosed(MetaContactGroup group)
    {
        return false;
    }

    /**
     * Returns the general status of the given MetaContact. Detects the status
     * using the priority status table. The priority is defined on the
     * "availability" factor and here the most "available" status is returned.
     *
     * @param metaContact The metaContact for which the status is asked.
     * @return PresenceStatus The most "available" status from all subcontact
     *         statuses.
     */
    public PresenceStatus getMetaContactStatus(MetaContact metaContact)
    {
        PresenceStatus status = null;
        Iterator<Contact> i = metaContact.getContacts();
        while (i.hasNext()) {
            Contact protoContact = i.next();
            PresenceStatus contactStatus = protoContact.getPresenceStatus();

            if (status == null) {
                status = contactStatus;
            } else {
                status = (contactStatus.compareTo(status) > 0) ? contactStatus
                        : status;
            }
        }
        return status;
    }

    /**
     * Creates a customized tooltip for this contact list.
     *
     * @return The customized tooltip.
     */
    @Override
    public JToolTip createToolTip()
    {
        Point currentMouseLocation = MouseInfo.getPointerInfo().getLocation();

        SwingUtilities.convertPointFromScreen(currentMouseLocation, this);

        int index = this.locationToIndex(currentMouseLocation);

        Object element = getModel().getElementAt(index);

        SwingExtendedTooltip tip = new SwingExtendedTooltip(true);

        if (element instanceof MetaContact)
        {
            MetaContact metaContact = (MetaContact) element;

            byte[] avatarImage = metaContact.getAvatar();

            if (avatarImage != null && avatarImage.length > 0)
                tip.setImage(new ImageIcon(avatarImage));

            tip.setTitle(metaContact.getDisplayName());

            Iterator<Contact> i = metaContact.getContacts();

            String statusMessage = null;
            Contact protocolContact;
            while (i.hasNext())
            {
                protocolContact = i.next();

                ImageIcon protocolStatusIcon
                    = new ImageIcon(
                        protocolContact.getPresenceStatus().getStatusIcon());

                String contactAddress = protocolContact.getAddress();
                //String statusMessage = protocolContact.getStatusMessage();

                tip.addLine(protocolStatusIcon, contactAddress);

                // Set the first found status message.
                if (statusMessage == null
                    && protocolContact.getStatusMessage() != null
                    && protocolContact.getStatusMessage().length() > 0)
                    statusMessage = protocolContact.getStatusMessage();
            }

            if (statusMessage != null)
                tip.setBottomText(statusMessage);
        }
        else if (element instanceof MetaContactGroup)
        {
            MetaContactGroup metaGroup = (MetaContactGroup) element;

            tip.setTitle(metaGroup.getGroupName());
        }
        else if (element instanceof ChatContact<?>)
        {
            ChatContact<?> chatContact = (ChatContact<?>) element;

            ImageIcon avatarImage = chatContact.getAvatar();

            if (avatarImage != null)
                tip.setImage(avatarImage);

            tip.setTitle(chatContact.getName());

            Object descriptor = chatContact.getDescriptor();

            if(descriptor instanceof ChatRoomMember)
            {
                ChatRoomMember member = (ChatRoomMember)descriptor;
                String roleName =
                    GuiActivator.getResources().getI18NString(
                        member.getRole().getLocalizedRoleName());

                tip.addLine(ChatContactRoleIcon.getRoleIcon(member.getRole()),
                    roleName);
                PresenceStatus status = member.getPresenceStatus();
                tip.addLine(null, status.getStatusName());
            }
        }

        tip.setComponent(this);

        return tip;
    }

    /**
     * Returns the string to be used as the tooltip for <i>event</i>. We don't
     * really use this string, but we need to return a different string each
     * time in order to make the TooltipManager change the tooltip over the
     * different cells in the JList.
     *
     * @param event the <tt>MouseEvent</tt> that notified us
     * @return the string to be used as the tooltip for <i>event</i>.
     */
    @Override
    public String getToolTipText(MouseEvent event)
    {
        Point currentMouseLocation = event.getPoint();

        int index = this.locationToIndex(currentMouseLocation);

        // If the index is equals to -1, then we have nothing to do here, we
        // just return null.
        if (index == -1)
            return null;

        Object element = getModel().getElementAt(index);

        /*
         * As stated above, the returned tooltip isn't actually displayed and we
         * just have to be sure to return different string values for the
         * different list elements. But the displayName property value doesn't
         * cut it because it isn't unique across the elements.
         */
        if (element instanceof MetaContact)
        {
            MetaContact metaContact = (MetaContact) element;

            return metaContact.getMetaUID();
        }
        else if (element instanceof MetaContactGroup)
        {
            MetaContactGroup metaGroup = (MetaContactGroup) element;

            return metaGroup.getMetaUID();
        }
        else if (element instanceof ChatContact<?>)
        {
            ChatContact<?> chatContact = (ChatContact<?>) element;

            return chatContact.getUID();
        }
        return null;
    }

    /**
     * Returns the next list element that starts with a prefix.
     *
     * @param prefix the string to test for a match
     * @param startIndex the index for starting the search
     * @param bias the search direction, either Position.Bias.Forward or
     *            Position.Bias.Backward.
     * @return the index of the next list element that starts with the prefix;
     *         otherwise -1
     */
    @Override
    public int getNextMatch(String prefix, int startIndex, Position.Bias bias)
    {
        int max = getModel().getSize();

        if (prefix == null)
            throw new IllegalArgumentException("prefix");
        if (startIndex < 0 || startIndex >= max)
            throw new IllegalArgumentException("startIndex");

        prefix = prefix.toUpperCase();

        // start search from the next element after the selected element
        int increment = (bias == Position.Bias.Forward) ? 1 : -1;
        int index = startIndex;
        do
        {
            Object o = getModel().getElementAt(index);

            if (o != null)
            {
                String contactName = null;

                if (o instanceof MetaContact)
                {
                    contactName = ((MetaContact) o).getDisplayName()
                        .toUpperCase();
                }
                else if(o instanceof ConferenceChatContact)
                {
                    contactName = ((ConferenceChatContact) o).getName()
                        .toUpperCase();
                }

                if (contactName != null && contactName.startsWith(prefix))
                {
                    return index;
                }
            }
            index = (index + increment + max) % max;
        } while (index != startIndex);
        return -1;
    }

    /**
     * Reloads skin information stored in render class.
     */
    public void loadSkin()
    {
        renderer.loadSkin();
    }
}
