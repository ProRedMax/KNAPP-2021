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

/**
 *
 */
public interface WarehouseInfo {
  // ----------------------------------------------------------------------------

  /**
   * The total result used for ranking.
   *
   *   (Excludes time-based ranking factor)
   *
   * @return
   */
  long getTotalCost();

  // ............................................................................

  /**
   * @return costs of (currently) unfinished packets
   */
  long getUnfinishedPacketsCost();

  /**
   * @return costs of area covered by prepared pallets
   */
  long getPalletsAreaCost();

  /**
   * @return costs of volume (stacking height) used by prepared pallets
   */
  long getPalletsVolumeUsedCost();

  // ----------------------------------------------------------------------------

  /**
   * @return number of unfinished packets
   */
  int getUnfinishedPacketCount();

  /**
   * @return number of prepared pallets
   */
  long getPalletCount();

  /**
   * @return area covered by prepared pallets
   */
  long getPalletsArea();

  /**
   * @return volume (stacking height) used by prepared pallets
   */
  long getPalletsVolumeUsed();

  // ----------------------------------------------------------------------------
}
