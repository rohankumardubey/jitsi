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
package net.java.sip.communicator.impl.protocol.irc;

import static org.junit.Assert.*;

import org.easymock.*;
import org.junit.*;

public class MessageIrcImplTest
{
    @Test
    public void testConstruction()
    {
        new MessageIrcImpl("Message", MessageIrcImpl.DEFAULT_MIME_TYPE,
            MessageIrcImpl.DEFAULT_MIME_ENCODING, null);
    }

    @Test
    public void testCorrectConstruction()
    {
        MessageIrcImpl message =
            new MessageIrcImpl("Message", MessageIrcImpl.DEFAULT_MIME_TYPE,
                MessageIrcImpl.DEFAULT_MIME_ENCODING, null);
        assertEquals("Message", message.getContent());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());
        assertFalse(message.isAction());
        assertFalse(message.isCommand());
    }

    @Test
    public void testActionRecognized()
    {
        MessageIrcImpl message =
            new MessageIrcImpl("/me is a genius!",
                MessageIrcImpl.DEFAULT_MIME_TYPE,
                MessageIrcImpl.DEFAULT_MIME_ENCODING, null);
        assertTrue(message.isAction());
        assertTrue(message.isCommand());
    }

    @Test
    public void testCommandNonActionRecognized()
    {
        MessageIrcImpl message =
            new MessageIrcImpl("/msg user Hi!",
                MessageIrcImpl.DEFAULT_MIME_TYPE,
                MessageIrcImpl.DEFAULT_MIME_ENCODING, null);
        assertFalse(message.isAction());
        assertTrue(message.isCommand());
    }

    @Test
    public void testNewMessageFromIRC()
    {
        MessageIrcImpl message =
            MessageIrcImpl.newMessageFromIRC("Hello world.");
        assertEquals("Hello world.", message.getContent());
        assertEquals(MessageIrcImpl.HTML_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());
    }

    @Test
    public void testNewActionFromIRC()
    {
        final String text = "Hello world.";

        // Test with chat room member.
        ChatRoomMemberIrcImpl chatRoomMember =
            EasyMock.createMock(ChatRoomMemberIrcImpl.class);
        EasyMock.replay(chatRoomMember);
        MessageIrcImpl message = MessageIrcImpl.newActionFromIRC(text);
        assertEquals("/me Hello world.", message.getContent());
        assertEquals(MessageIrcImpl.HTML_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());

        // Test with contact.
        ContactIrcImpl contact = EasyMock.createMock(ContactIrcImpl.class);
        EasyMock.replay(contact);
        message = MessageIrcImpl.newActionFromIRC(text);
        assertEquals("/me Hello world.", message.getContent());
        assertEquals(MessageIrcImpl.HTML_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());
    }

    @Test
    public void testNewNoticeFromIRC()
    {
        final String text = "Hello world.";

        // Test with chat room member.
        ChatRoomMemberIrcImpl chatRoomMember =
            EasyMock.createMock(ChatRoomMemberIrcImpl.class);
        EasyMock.expect(chatRoomMember.getContactAddress())
            .andReturn("IamUser");
        EasyMock.replay(chatRoomMember);
        MessageIrcImpl message =
            MessageIrcImpl.newNoticeFromIRC(chatRoomMember, text);
        assertEquals(Utils.styleAsNotice(text, "IamUser"),
            message.getContent());
        assertEquals(MessageIrcImpl.HTML_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());

        // Test with contact.
        ContactIrcImpl contact = EasyMock.createMock(ContactIrcImpl.class);
        EasyMock.expect(contact.getAddress()).andReturn("IamUser");
        EasyMock.replay(contact);
        message = MessageIrcImpl.newNoticeFromIRC(contact, text);
        assertEquals(Utils.styleAsNotice(text, "IamUser"),
            message.getContent());
        assertEquals(MessageIrcImpl.HTML_MIME_TYPE,
            message.getContentType());
        assertEquals(MessageIrcImpl.DEFAULT_MIME_ENCODING,
            message.getEncoding());
        assertNull(message.getSubject());
    }
}
