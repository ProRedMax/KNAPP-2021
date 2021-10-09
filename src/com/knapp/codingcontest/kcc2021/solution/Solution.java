/* -*- java -*- ************************************************************************** *
 *
 *                     Copyright (C) KNAPP AG
 *
 *   The copyright to the computer program(s) herein is the property
 *   of Knapp.  The program(s) may be used   and/or copied only with
 *   the  written permission of  Knapp  or in  accordance  with  the
 *   terms and conditions stipulated in the agreement/contract under
 *   which the program(s) have been supplied.
 *
 * *************************************************************************************** */

package com.knapp.codingcontest.kcc2021.solution;

import java.util.*;

import com.knapp.codingcontest.kcc2021.data.InputData;
import com.knapp.codingcontest.kcc2021.data.Institute;
import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.Pallet.PacketPos;
import com.knapp.codingcontest.kcc2021.data.PalletType;
import com.knapp.codingcontest.kcc2021.warehouse.*;

/**
 * This is the code YOU have to provide
 */
public class Solution {
    public String getParticipantName() {
        return "Maximilian Burger"; // TODO: return your name
    }

    public Institute getParticipantInstitution() {
        return Institute.HTL_Rennweg_Wien; // TODO: return the Id of your institute - please refer to the hand-out
    }

    // ----------------------------------------------------------------------------

    protected final InputData input;
    protected final Warehouse warehouse;

    Iterator<Packet> packetIterator;

    LinkedList<Pallet> currentPallets = new LinkedList<>();


    // ----------------------------------------------------------------------------

    public Solution(final Warehouse warehouse, final InputData input) {
        this.input = input;
        this.warehouse = warehouse;
        // TODO: prepare data structures
        packetIterator = input.getPackets().listIterator();


    }

    // ----------------------------------------------------------------------------

    /**
     * The main entry-point
     */
    public void run() throws Exception {
        // TODO: make calls to API (see below)

        boolean extracted;

        while (packetIterator.hasNext()) {
            extracted = false;
            Packet currentPacket = packetIterator.next();
            if (!currentPallets.isEmpty()) {
                for (Pallet pallet : currentPallets) {
                    PacketPos packetPos = hasSpaceOnPallet(pallet, currentPacket);
                    if (packetPos != null) {
                        try {
                            warehouse.putPacket(pallet, currentPacket, packetPos.getX(), packetPos.getY(), packetPos.isRotated());
                            extracted = true;
                            break;
                        } catch (PalletExtendsViolatedException ignored) {
                        }
                    } else {
                        if ((pallet.getType().getMaxHeight() - 1 > pallet.getCurrentStackedHeight() && currentPacket.getTruckId() == pallet.getTruckId()) && pallet.getType().getMaxWeight() > pallet.getCurrentWeight() + currentPacket.getWeight()) {
                            if (currentPacket.getWidth() <= pallet.getType().getWidth() && currentPacket.getLength() <= pallet.getType().getLength()) {
                                warehouse.putPacket(pallet, currentPacket, 0, 0, false);
                                extracted = true;
                                break;
                            } else if (currentPacket.getLength() <= pallet.getType().getWidth() && currentPacket.getWidth() <= pallet.getType().getLength()) {
                                warehouse.putPacket(pallet, currentPacket, 0, 0, true);
                                extracted = true;
                                break;
                            }
                        }
                    }
                }
                if (!extracted) {
                    newPallet(currentPacket);
                    extracted = false;
                }
            } else {
                newPallet(currentPacket);
                extracted = false;
            }
        }
    }

    private void newPallet(Packet currentPacket) throws PacketAlreadyUsedException, WrongTruckException, WeightExceededException, PalletExtendsViolatedException, HeightExceededException {
        PalletType currentPalletType = getIdealPalletType(currentPacket);
        Pallet pallet = warehouse.preparePallet(currentPacket.getTruckId(), currentPalletType);
        currentPallets.add(pallet);
        warehouse.putPacket(pallet, currentPacket, 0, 0, false);
    }


    // ----------------------------------------------------------------------------

    public PalletType getIdealPalletType(Packet packet) {
        return input.getPalletTypes().stream().filter(l -> l.getWidth() >= packet.getWidth() && l.getLength() >= packet.getLength() && l.getMaxWeight() >= packet.getWeight()).sorted(Comparator.comparing(l -> l.getLength() * l.getWidth())).findFirst().orElse(null);
    }

    public PacketPos hasSpaceOnPallet(Pallet pallet, Packet packet) {
        if (((pallet.getType().getMaxWeight() - pallet.getCurrentWeight() >= packet.getWeight()) && pallet.getTruckId() == packet.getTruckId()) && pallet.getType().getMaxHeight() -1 > pallet.getCurrentStackedHeight()) {
            int x = 0;
            int y = 0;
            boolean rotate = false;
            Map<PacketPos, Packet> packets = pallet.getLayer(pallet.getCurrentStackedHeight() - 1).getPackets();
            // Y - Length
            HashMap<Integer, Integer> packetLength = new HashMap<>();
            HashMap<Integer, Integer> packetWidth = new HashMap<>();
            for (Map.Entry<PacketPos, Packet> entry : packets.entrySet()) {
                packetLength.merge(entry.getKey().getY(), entry.getKey().getX() + (entry.getKey().isRotated() ? entry.getValue().getWidth() : entry.getValue().getLength()), Integer::sum);
            }
            for (Map.Entry<Integer, Integer> entry : packetLength.entrySet()) {
                if ((pallet.getType().getLength() - entry.getValue() >= packet.getLength() && packet.getWidth() <= pallet.getType().getWidth())) {
                    return new PacketPos(entry.getValue(), entry.getKey(), false);
                }
                if (entry.getKey() == packetLength.size() - 1 && packet.getWidth() + entry.getKey() + 1 <= pallet.getType().getWidth() - packetLength.size()  && packet.getLength() <= pallet.getType().getLength()) {
                    return new PacketPos(0, entry.getKey() + 3, false);
                }
            }
        }
        return null;
    }

    // ----------------------------------------------------------------------------

    /**
     * Just for documentation purposes.
     * <p>
     * Method may be removed without any side-effects
     * <p>
     * divided into 4 sections
     *
     * <li><em>input methods</em>
     *
     * <li><em>main interaction methods</em>
     * - these methods are the ones that make (explicit) changes to the warehouse
     *
     * <li><em>information</em>
     * - information you might need for your solution
     *
     * <li><em>additional information</em>
     * - various other infos: statistics, information about (current) costs, ...
     */
    @SuppressWarnings("unused")
    private void apis() throws Exception {
        // ----- input -----

        final PalletType palletType = input.getPalletTypes().iterator().next();
        final Packet packet = input.getPackets().iterator().next();

        // ----- main interaction methods -----

        final Pallet pallet = warehouse.preparePallet(packet.getTruckId(), palletType);

        final int x = 0;
        final int y = 0;
        final boolean rotated = false;
        warehouse.putPacket(pallet, packet, x, y, rotated);

        // ----- information -----
        final int csh = pallet.getCurrentStackedHeight();
        final int cw = pallet.getCurrentWeight();
        final Pallet.Layer layer = pallet.getLayer(0);
        final Map<PacketPos, Packet> lpackets = layer.getPackets();

        // ----- additional information -----
        final WarehouseInfo info = warehouse.getInfo();

        final long tc = info.getTotalCost();
        final long upc = info.getUnfinishedPacketsCost();
        final long pac = info.getPalletsAreaCost();
        final long pvuc = info.getPalletsVolumeUsedCost();

        final int up = info.getUnfinishedPacketCount();
        final long pc = info.getPalletCount();
        final long pa = info.getPalletsArea();
        final long pvu = info.getPalletsVolumeUsed();
    }

    // ----------------------------------------------------------------------------
}
