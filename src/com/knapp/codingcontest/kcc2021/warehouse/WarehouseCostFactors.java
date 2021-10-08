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

public interface WarehouseCostFactors {
  /**
   * @return costs if there are any unfinished packet(s)
   */
  double getUnfinishedPacketsPenalty();

  /**
   * @return costs per unfinished packet
   */
  double getUnfinishedPacketCosts();

  /**
   * @return costs per area-unit covered by prepared pallets
   */
  double getPalletAreaCosts();

  /**
   * @return costs per volume-unit (stacking height) used by prepared pallets
   */
  double getPalletVolumeUsedCosts();
}
