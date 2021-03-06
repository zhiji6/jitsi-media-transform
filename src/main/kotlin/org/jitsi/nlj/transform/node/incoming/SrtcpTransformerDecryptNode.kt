/*
 * Copyright @ 2018 Atlassian Pty Ltd
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
package org.jitsi.nlj.transform.node.incoming

import org.jitsi.nlj.PacketInfo
import org.jitsi.nlj.stats.NodeStatsBlock
import org.jitsi.nlj.transform.node.AbstractSrtpTransformerNode
import org.jitsi_modified.impl.neomedia.transform.SinglePacketTransformer

class SrtcpTransformerDecryptNode : AbstractSrtpTransformerNode("SRTCP decrypt") {
    private var numDecryptFailures = 0
    override fun doTransform(pkts: List<PacketInfo>, transformer: SinglePacketTransformer): List<PacketInfo> {
        val outPackets = mutableListOf<PacketInfo>()
        pkts.forEach {
            transformer.reverseTransform(it.packet)?.let { decryptedPacket ->
                it.packet = decryptedPacket
                outPackets.add(it)
            } ?: run {
                logger.error("Error decrypting RTCP")
                numDecryptFailures++
            }
        }
        return outPackets
    }

    override fun getNodeStats(): NodeStatsBlock {
        val parentStats = super.getNodeStats()
        return NodeStatsBlock(name).apply {
            addAll(parentStats)
            addStat("num decrypt failures: $numDecryptFailures")
        }
    }
}
