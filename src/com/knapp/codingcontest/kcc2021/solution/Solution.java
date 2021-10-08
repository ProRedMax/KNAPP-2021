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

import java.util.Map;

import com.knapp.codingcontest.kcc2021.data.InputData;
import com.knapp.codingcontest.kcc2021.data.Institute;
import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.Pallet.PacketPos;
import com.knapp.codingcontest.kcc2021.data.PalletType;
import com.knapp.codingcontest.kcc2021.warehouse.Warehouse;
import com.knapp.codingcontest.kcc2021.warehouse.WarehouseInfo;

/**
 * This is the code YOU have to provide
 *
 */
public class Solution {
  public String getParticipantName() {
    return "Maximilian Burger"; // TODO: return your name
  }

  public Institute getParticipantInstitution() {
    return Institute.HTL_Rennweg_Wien ; // TODO: return the Id of your institute - please refer to the hand-out
  }

  // ----------------------------------------------------------------------------

  protected final InputData input;
  protected final Warehouse warehouse;

  // ----------------------------------------------------------------------------

  public Solution(final Warehouse warehouse, final InputData input) {
    this.input = input;
    this.warehouse = warehouse;
    // TODO: prepare data structures

  }

  // ----------------------------------------------------------------------------

  /**
   * The main entry-point
   */
  public void run() throws Exception {
    // TODO: make calls to API (see below)

  }

  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  /**
   * Just for documentation purposes.
   *
   * Method may be removed without any side-effects
   *
   *   divided into 4 sections
   *
   *     <li><em>input methods</em>
   *
   *     <li><em>main interaction methods</em>
   *         - these methods are the ones that make (explicit) changes to the warehouse
   *
   *     <li><em>information</em>
   *         - information you might need for your solution
   *
   *     <li><em>additional information</em>
   *         - various other infos: statistics, information about (current) costs, ...
   *
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
