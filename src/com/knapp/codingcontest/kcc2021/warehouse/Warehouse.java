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

package com.knapp.codingcontest.kcc2021.warehouse;

import java.util.Collection;

import com.knapp.codingcontest.kcc2021.data.Packet;
import com.knapp.codingcontest.kcc2021.data.Pallet;
import com.knapp.codingcontest.kcc2021.data.PalletType;

public interface Warehouse {
  // ----------------------------------------------------------------------------
  // operations

  /**
   * Prepare an empty pallet to be used for a specific truck.
   *
   * @param truckId     id of the truck
   * @param palletType  type of the pallet to be prepared
   *
   * @return            a new empty pallet
   */
  Pallet preparePallet(int truckId, PalletType palletType);

  /**
   * Put a packet to a pallet.
   *
   *<pre>
   *     Example:
   *       Palette: length*width=12*8
   *       Packet:  length*width=4*3
   *
   *       x/y=3/1,rotated=false       x/y=9/1,rotated=true
   *       0----------+                0----------+
   *       |  x--+    |                |        x-+
   *       |  |  |    |                |        | |
   *       |  +--+    |                |        | |
   *       |          |                |        +-+
   *       |          |                |          |
   *       |          |                |          |
   *       +----------X (12/8)         +----------X (12/8)
   *</pre>
   *
   * @param pallet      the pallet the packet should be added to
   * @param packet      the packet to add
   * @param x           corner of left/back x-coordinates (lengthwise)
   * @param y           corner of left/back y-coordinates (crosswise)
   * @param rotated     <code>true</code> if the packet should be rotated
   *
   * @return the layer where the packet was put
   *
   * @throws PacketAlreadyUsedException
   * @throws WrongTruckException
   * @throws WeightExceededException
   * @throws PalletExtendsViolatedException
   * @throws HeightExceededException
   */
  int putPacket(Pallet pallet, Packet packet, int x, int y, boolean rotated) throws PacketAlreadyUsedException //
      , WrongTruckException, WeightExceededException, PalletExtendsViolatedException //
      , HeightExceededException;

  // ----------------------------------------------------------------------------
  // info

  /**
   * Information about the current state of the warehouse.
   *
   * @return info
   */
  WarehouseInfo getInfo();

  /**
   * Cost-factors used for calculating the result.
   *
   * @return cost-factors
   */
  WarehouseCostFactors getCostFactors();

  // ............................................................................

  /**
   * @return a collection of already prepared pallets
   */
  Collection<Pallet> getPallets();

  // ----------------------------------------------------------------------------
}
