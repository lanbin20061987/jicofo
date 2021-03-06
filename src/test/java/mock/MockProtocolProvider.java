/*
 * Jicofo, the Jitsi Conference Focus.
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
package mock;

import mock.muc.*;
import mock.xmpp.*;
import org.jetbrains.annotations.*;
import org.jitsi.impl.protocol.xmpp.*;
import org.jitsi.jicofo.xmpp.*;
import org.jitsi.protocol.xmpp.*;
import org.jivesoftware.smack.*;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.*;
import org.jxmpp.stringprep.*;

import java.util.concurrent.*;

/**
 *
 * @author Pawel Domas
 */
public class MockProtocolProvider
    extends AbstractXmppProvider
{
    private MockXmppConnection connection;

    private AbstractOperationSetJingle jingleOpSet;

    private MockMultiUserChatOpSet mucApi;

    @NotNull public XmppConnectionConfig config;

    public MockProtocolProvider(@NotNull XmppConnectionConfig config)
    {
        this.config = config;
        connection = new MockXmppConnection(getOurJID());
        mucApi = new MockMultiUserChatOpSet(this);
        this.jingleOpSet = new MockOperationSetJingle(this);
    }

    @Override
    public String toString()
    {
        return "MockProtocolProvider " + config;
    }

    @Override
    public void register(ScheduledExecutorService executorService)
    {
        if (jingleOpSet != null)
        {
            connection.registerIQRequestHandler(jingleOpSet);
        }

        setRegistered(true);
    }

    @Override
    public void unregister()
    {
        if (jingleOpSet != null)
        {
            connection.unregisterIQRequestHandler(jingleOpSet);
        }

        setRegistered(false);
    }

    @Override
    public XmppConnectionConfig getConfig()
    {
        return config;
    }


    @Override
    public XmppConnection getXmppConnection()
    {
        return connection;
    }

    @Override
    public XMPPConnection getXmppConnectionRaw()
    {
        return null;
    }

    @Override
    public OperationSetJingle getJingleApi()
    {
        return jingleOpSet;
    }

    public EntityFullJid getOurJID()
    {
        try
        {
            return JidCreate.entityFullFrom(
                    "mock-" + config.getUsername() + "@" + config.getDomain() + "/" + config.getUsername());
        }
        catch (XmppStringprepException e)
        {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public ChatRoom createRoom(@NotNull String name) throws RoomExistsException
    {
        return mucApi.createChatRoom(name);
    }

    @NotNull
    @Override
    public ChatRoom findOrCreateRoom(@NotNull String name) throws RoomExistsException
    {
        return mucApi.findRoom(name);
    }
}
