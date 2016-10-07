/**
 * WebLogic Automation Book Source Code (JMX sources)
 * <p>
 * This file is part of the WLS-Automation book sourcecode software distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Martin Heinzl
 * Copyright (C) 2013 MH-EnterpriseConsulting, All rights reserved.
 */
package me.yekki.jmx.troubleshooting;

import me.yekki.jmx.utils.JMXWrapper;
import me.yekki.jmx.utils.WLSJMXException;

import javax.management.ObjectName;

public class ThreadDumpUtils {

    private JMXWrapper myJMXWrapper = null;


    public ThreadDumpUtils(JMXWrapper _wrapper) throws WLSJMXException {
        myJMXWrapper = _wrapper;
    }

    public String getThreadDump(String serverName) throws WLSJMXException {
        try {
            // get the server runtime(!)
            ObjectName serverRuntime = myJMXWrapper.getServerRuntime(serverName);

            // get JVMRuntime of that server
            ObjectName jvmRuntime = (ObjectName) myJMXWrapper.getAttribute(serverRuntime, "JVMRuntime");

            // finally return the threaddump
            return (String) myJMXWrapper.getAttribute(jvmRuntime, "ThreadStackDump");
        } catch (Exception ex) {
            throw new WLSJMXException("Error while getThreadDump of server " + serverName + " : " + ex.getMessage());
        }
    }

}    